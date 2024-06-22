package com.poetofcode.sproutclient

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

/*
    Инструкция:
    https://firebase.google.com/docs/cloud-messaging/android/first-message?hl=ru
 */

// TODO Remove:
//      esFT4_iUTxmToOG5v3hgND:APA91bGIOAToVV1nA9_sq6VZXEpOBZQBUrAEeC1P_HwIZIXXzLBlXlZJ7EY0y6pzgS_VDiYWPesXRWp2XRw-JYflVW-8oJ5xyvogtX6B4c8z9QPJoui1Abws0xYIbLw0bQs34MqNYuU6

fun Context.retrieveToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("mylog", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result

        // Log and toast
        // val msg = getString(R.string.msg_token_fmt, token)
        val msg = "FCM Token: $token"
        Log.d("mylog", msg)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    })
}
