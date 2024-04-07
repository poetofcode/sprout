const ObjectId = require("mongodb").ObjectId;
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
            const userName = req.body.name;
            const password = req.body.password;

            if (!userName || userName == 'undefined') {
                return next(utils.buildError(400, '"name" is empty'))
            }
            if (!password || password == 'undefined') {
                return next(utils.buildError(400, '"password" is empty'))
            }




            if (userName !== refName || password !== refPassword) {
                return next(utils.buildError(400, 'Invalid login or password'));
            }

            try {
                const clientIP = parseIp(req);
                const session = await this.sessionRepository.createSession(userName, clientIP);
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