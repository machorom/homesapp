package com.bconline.homesapp.Util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ContextThemeWrapper
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import com.bconline.homesapp.MainActivity
import com.bconline.homesapp.R
import kotlinx.android.synthetic.main.activity_main.*


object WebViewUtil {
    const val BASE_URL = "https://homesapp.co.kr"
    fun userAgent(context:Context):String{
        return "APP_ANDROID&"+ context.applicationContext.packageManager.getPackageInfo(context.packageName,0).versionName
    }

    fun isMatchPattern(url:String, pattern:String):Boolean{
        val regex = pattern.toRegex()
        val matchResult : MatchResult? = regex.find(url)
        if( matchResult == null )
            return false
        return true
    }
    fun isLastedPage(webview:WebView):Boolean{
        if( webview.getUrl().endsWith("/login")
            || webview.getUrl().endsWith("/join")
            || webview.getUrl().endsWith("/map")
            || isMatchPattern(webview.getUrl().replace("$BASE_URL/contents/",""),"(^[0-9]*\$)")
            || webview.getUrl().endsWith("/mlike")
            || webview.getUrl().endsWith("/recent")
            || webview.getUrl().endsWith("/clike")
            || webview.getUrl().endsWith("/mypage")
            || webview.getUrl().endsWith("/notice")
            || webview.getUrl().endsWith("/event")
            || webview.getUrl().endsWith("/faq")
            || webview.getUrl().endsWith("/inquiry")
            || webview.getUrl().endsWith("/member/edit")
            || webview.getUrl().endsWith("/member/setting")
            || webview.getUrl().endsWith("/")
            || !webview.canGoBack() ){
            return true
        }
        return false
    }
}