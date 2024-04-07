const { utils } = require('../utils');

async function initRoutes(router, context) {
	const repositories = {};
	context.repositories = repositories;

	(await utils.requireAll('app/repository/')).forEach((name, value) => {
		repositories[name] = value.create(context);
	});

	const middlewares = {};
	(await utils.requireAll('app/api/')).forEach((name, value) => {
		middlewares[name] = value.create(context);
	});

	const sessionMiddleware = middlewares.sessions;
	const sessionRepository = repositories.sessions;

	router.use(async function (req, res, next) {
		const authHeader = req.header('Authorization');

		// White-list эндпоинтов без авторизации:
		const isSessionsPost = req.path === '/sessions' && req.method === 'POST';
		const isSessionByTokenGet = req.path.startsWith("/sessions/") && req.path !== "/sessions/" && req.method === 'GET';
		const isSessionByTokenDelete = req.path.startsWith("/sessions/") && req.path !== "/sessions/" && req.method === 'DELETE';
		const isUserCreate = req.path.startsWith('/users') && req.method === 'POST';
		if (isSessionsPost || isSessionByTokenGet || isSessionByTokenDelete || isUserCreate) {
			return next();
		}

		if (authHeader) {
			const token = authHeader.replace('Bearer ', '');
            const session = await sessionRepository.fetchSessionByToken(token);
            if (session) {
            	return next();
            }
		}

		res.status(401).send(utils.wrapError(new Error('Not authorized')));
	});

	router.post('/sessions', sessionMiddleware.createSession());
	router.get('/sessions', sessionMiddleware.fetchSessions());
	router.get('/sessions/:token', sessionMiddleware.fetchSessionByToken());
	router.delete('/sessions/:token', sessionMiddleware.deleteSessionByToken());

	router.post('/users', middlewares.users.createUser());

	router.use((err, req, res, next) => {
	  if (res.headersSent) {
    	return next(err)
      }
	  console.log(err);
	  res.status(err.status || 500).send(utils.wrapError(err));
	});
}

exports.initRoutes = initRoutes;