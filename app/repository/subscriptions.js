const { utils } = require('../utils');

class SubscriptionRepository {

	constructor(context) {
		this.context = context;
        this.subscribedUserIds = [];
	}

    enableSubscription(user, isEnabled) {
        console.log('Подписки ДО:');
        console.log(this.subscribedUserIds);

        if (isEnabled) {
            if (this.subscribedUserIds.includes(user._id.toString())) {
                // Do nothing
                return;
            }
            this.subscribedUserIds.push(user._id.toString());
            console.log(`Подписываем юзера: ${user._id}`);
        } else {
            this.subscribedUserIds = this.subscribedUserIds.filter((item) => item != user._id);
            console.log(`Отписываем юзера: ${user._id}`);
        }

        console.log('Подписки ПОСЛЕ:');
        console.log(this.subscribedUserIds);
    }

    getSubscriptions() {
        return this.subscribedUserIds;
    }

}

exports.create = (context) => new SubscriptionRepository(context); 