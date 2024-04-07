const { utils } = require('../utils');

class SessionMiddleware {

    constructor(context, repositories) {
        this.context = context;
        this.repositories = repositories;
    }

    createSession() {
        return async(req, res, next) => {
            if(!req.body) {
                return next(utils.buildError(400, 'Body is empty'))
            }
            const login = req.body.login;
            const password = req.body.password;

            if (!login || login == 'undefined') {
                return next(utils.buildError(400, '"login" is empty'))
            }
            if (!password || password == 'undefined') {
                return next(utils.buildError(400, '"password" is empty'))
            }

            const foundUser = await this.repositories.users.findUserByLogin(login);
            if (!foundUser) {
                console.log(`foundUser: ${foundUser}`);
                return next(utils.buildError(400, `User is not found, wrong login or password`));
            }

            if (!foundUser.activated) {
                return next(utils.buildError(400, `User is not activated yet`));   
            }

            if (utils.sha1(password) !== foundUser.password) {
                console.log(`pass1: ${utils.sha1(password)}, pass2: ${foundUser.password}`);
                return next(utils.buildError(400, `User is not found, wrong login or password`));
            }

            try {
                const clientIP = parseIp(req);
                const session = await this.sessionRepository.createSession(foundUser._id, clientIP);
                res.send(utils.wrapResult(session));
            }
            catch(err) {
                next(err);
            }              
        }
    }

    fetchSessions() {
        return async(req, res, next) => {
            try {
                const sessions = await this.repositories.sessions.fetchSessionsAll();
                res.send(utils.wrapResult(sessions));
            }
            catch(err) {
                next(err);
            }  
        }
    }

    fetchSessionByToken() {
        return async(req, res, next) => {
            try {
                const token = req.params.token;
                const session = await this.repositories.sessions.fetchSessionByToken(token);
                if (!session) {
                    const err = new Error('Not found');
                    err.status = 400;
                    return next(err)
                }
                res.send(utils.wrapResult(session));
            }
            catch(err) {
                next(err);
            }  
        }
    }

    deleteSessionByToken() {
        return async(req, res, next) => {
            try {
                const token = req.params.token;
                await this.repositories.sessions.deleteSessionByToken(token);
                res.send(utils.wrapResult('ok'));
            }
            catch(err) {
                next(err);
            }  
        }
    }

}

const parseIp = (req) =>
    req.headers['x-forwarded-for']?.split(',').shift()
    || req.socket?.remoteAddress


exports.create = (context, repositories) => new SessionMiddleware(context, repositories);