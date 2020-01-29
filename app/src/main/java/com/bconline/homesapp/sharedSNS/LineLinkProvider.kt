package com.bconline.homesapp.sharedSNS

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object LineLinkProvider {
    fun sendLink(context:Context, url:String){
        if (isInstalledPackage(context)) {
            val content: String = String.format("line://msg/text/%s", url)
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(content)
            context.startActivity(intent)
        } else {
            Toast.makeText(context,"라인앱이 설치되지 않았네요~", Toast.LENGTH_SHORT).show()
//            val intent = Intent()
//            intent.data = Uri.parse("market://details?id=jp.naver.line.android")
//            intent.action = Intent.ACTION_VIEW
//            context.startActivity(intent)
        }
    }

    private fun isInstalledPackage(context:Context):Boolean{
        val pm = context.packageManager
        try {
            pm.getPackageInfo("jp.naver.line.android", PackageManager.GET_ACTIVITIES)
            return true
        }catch (ex:PackageManager.NameNotFoundException){
        }
        return false
    }

}