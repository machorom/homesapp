package com.bconline.homesapp.sharedSNS

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback


object KakaolinkProvider {

    private const val KAKAO_BASE_LINK = "https://developers.kakao.com"

    // 공유하기 눌렀을 때 처리
    fun sendKakaoLink(context: Context, imageUrl: String, title:String, content: String, url: String){
        Log.d("SNS","sendKakaoLink imageUrl=$imageUrl, title=$title, content=$content, url=$url" )
        val params : FeedTemplate = FeedTemplate.newBuilder(ContentObject.newBuilder(title,imageUrl,
            LinkObject.newBuilder()
                .setWebUrl(url)
                .setMobileWebUrl(url)
                .build())
                .setDescrption(content).build()).build()
        KakaoLinkService.getInstance().sendDefault(context, params, object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult) {
                var msg:String = String.format("code=%s, message=%s", errorResult.errorCode, errorResult.errorMessage)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                Log.d("SNS","kakao send Failure " + msg)
            }
            override fun onSuccess(result: KakaoLinkResponse) {
            }
        })
    }

}