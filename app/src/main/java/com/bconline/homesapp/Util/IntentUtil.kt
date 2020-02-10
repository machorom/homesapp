package com.bconline.homesapp.Util

import android.content.Context
import android.content.Intent
import android.net.Uri

object IntentUtil {
    fun intentToSMS(context: Context, address:String){
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(address))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}