package com.bconline.homesapp.sharedSNS

import android.content.Context
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.lang.Exception
import java.net.URLEncoder


object KakaoStoryProvider {
    fun share(context:Context, url:String, text:String){
        try {
            val shareBody = URLEncoder.encode("$text\n$url","UTF-8")
            val appId = context.packageName
            val appName = "홈즈"
            val url = "storylink://posting?post=$shareBody&appid=$appId&appver=1.0&apiver=1.0&appname=$appName"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }catch (ex:Exception){
            Toast.makeText(context,"카카오스토리를 설치해주세요",Toast.LENGTH_LONG).show()
        }
    }
}