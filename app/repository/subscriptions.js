const { utils } = require('../utils');

class SubscriptionRepository {

	constructor(context) {
		this.context = context;
        this.subscribedUserIds = [];
	}

    enableSubscription(user, isEnabled) {
        if (isEnabled) {
            console.log(`Подписываем юзера: ${user._id}`);
        } else {
            console.log(`Отписываем юзера: ${user._id}`);
        }
        console.log('Подписки ДО:');
        console.log(this.subscribedUserIds);

        if (isEnabled) {
           this.subscribedUserIds.append(user._id);
        } else {
            this.subscribedUserIds = this.subscribedUserIds.filter((item) => item != user._id);
        }

        console.log('Подписки ПОСЛЕ:');
        console.log(this.subscribedUserIds);
    }

}

exports.create = (context) => new SubscriptionRepository(context); 