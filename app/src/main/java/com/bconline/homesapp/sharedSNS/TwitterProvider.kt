package com.bconline.homesapp.sharedSNS

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity



object TwitterProvider {
    fun share(context:Context, message:String, url:String){
        val tweetUrl =
            "https://twitter.com/intent/tweet?text=$message&url=$url"
        val uri = Uri.parse(tweetUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}