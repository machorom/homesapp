package com.bconline.homesapp.sharedSNS

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity



object TwitterProvider {
    fun share(context:Context, url:String, text:String){
        val tweetUrl =
            "https://twitter.com/intent/tweet?url=$url&text=$text"
        val uri = Uri.parse(tweetUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}