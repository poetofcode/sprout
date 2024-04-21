const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');

class SessionRepository {

    constructor(context) {
        this.context = context;
        this.db = context.getDb();
        this.sessionCollection = this.db.collection('sessions');
    }

    async createSession(userId, clientIP) {
        const newSession = {
            token: crypto.randomUUID(),
            createdAt: new Date(),
            userId: new ObjectId(userId),
            ip: clientIP
        }
        const sessions = await this.sessionCollection.insertOne(newSession);
        const user = await this.context.repositories.users.findUserById(newSession.userId.toString());
        delete newSession.userId;
        newSession.user = user;
        return newSession;
    }

    async fetchSessionsAll() {
        const sessions = await this.sessionCollection.find({}).toArray();
        return sessions;
    }

    async fetchSessionByToken(token) {
        const session = await this.sessionCollection.findOne({ token : token });
        const user = await this.context.repositories.users.findUserById(session.userId.toString());
        delete session.userId;
        session.user = user;
        return session;
    }

    async deleteSessionByToken(token) {
        const result = await this.sessionCollection.deleteOne({ token : token });
        return result;
    }

}

exports.create = (context) => new SessionRepository(context);