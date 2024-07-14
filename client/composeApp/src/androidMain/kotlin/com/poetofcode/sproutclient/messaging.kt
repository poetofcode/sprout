package com.poetofcode.sproutclient

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

/*
    Инструкция:
    https://firebase.google.com/docs/cloud-messaging/android/first-message?hl=ru
    https://medium.com/@ravisharma23523/sending-notifications-to-mobile-devices-with-firebase-cloud-messaging-fcm-in-node-js-8fe3faead58b
 */

// TODO Remove:
//      esFT4_iUTxmToOG5v3hgND:APA91bGIOAToVV1nA9_sq6VZXEpOBZQBUrAEeC1P_HwIZIXXzLBlXlZJ7EY0y6pzgS_VDiYWPesXRWp2XRw-JYflVW-8oJ5xyvogtX6B4c8z9QPJoui1Abws0xYIbLw0bQs34MqNYuU6
//      POCO: dMFX3x94RmOngId8Twbk2J:APA91bHIjMYfo0M1DmPru-Eh-geUwS1PsBsznEpHYIFVPYT_6uu5oa_PP9jN-u6pEuR8Eur4SAZgzudaJvyoV5amVDO2IIzOykTrySZYuttn2jplUKZ-egFr2fnjiHvtt1frDH55gLO1

fun Context.retrieveFirebasePushToken(onSuccess: (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("mylog", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result
        onSuccess(token)
    })
}
