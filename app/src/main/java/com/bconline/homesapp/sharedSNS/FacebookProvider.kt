package com.bconline.homesapp.sharedSNS

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object FacebookProvider {
    fun share(context: Context, url:String){
        Log.d("FacebookProvider","share $url")
        var intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT, url)
        var isFacebookForground = false
        val matches = context.packageManager.queryIntentActivities(intent, 0)
        for (m in matches){
            if(m.activityInfo.packageName.toLowerCase().startsWith("com.facebook")){
                intent.setPackage(m.activityInfo.packageName)
                isFacebookForground = true
                break
            }
        }
        if(!isFacebookForground){
            val sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=$url"
            Log.d("FacebookProvider","isFacebookForground $sharerUrl")
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
        }
        Log.d("FacebookProvider","start $intent")
        context.startActivity(intent)
    }
}
