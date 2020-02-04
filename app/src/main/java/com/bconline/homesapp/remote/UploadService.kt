package com.bconline.homesapp.remote

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class UploadService(val webview:WebView, val context: Context){

    fun uploadMemberPhoto(contentURI: Uri?, memberId:String){
        val retrofit: Retrofit? = RetrofitClient.get(context)
        val uploadApi:UploadApi = retrofit!!.create(UploadApi::class.java)
        val filepath:String? = getRealPath(contentURI)
        val file = File(filepath)
        Log.d("MainActivity","uploadMemberPhoto parameter filepath="+filepath+",filename="+file.name)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"),file)
        val part: MultipartBody.Part = MultipartBody.Part.createFormData("photo",file.name,requestBody)
        val memberParam = RequestBody.create(MediaType.parse("text/plain"), memberId)
        val call = uploadApi.memberUpload(part, memberParam)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("UploadService","memberUpload  error image" + t)
                Toast.makeText(context,"사진업로드에 실패했습니다.", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()!!.string()
                Log.i(
                    "MainActivity",
                    "memberUpload response code=" + response.code()
                            + ", errorBody=" + response.errorBody()
                            + ", message=" + response.message()
                            + ", raw=" + response.raw()
                            + ", body=" + body
                )
                val url:String = "javascript:memberUploadComplete('$body')"
                webview.loadUrl(url)
                Log.d("UploadService","call : " + url)
            }

        })
    }

    fun uploadInqueryPhoto(contentURI: Uri?){
        val retrofit: Retrofit? = RetrofitClient.get(context)
        val uploadApi:UploadApi = retrofit!!.create(UploadApi::class.java)

        val filepath = getRealPath(contentURI)
        if(filepath == null){
            Toast.makeText(context,"선택된 파일을 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
            return
        }
        val file = File(filepath)
        Log.d("UploadService","uploadInqueryPhoto params filepath="+filepath+",filename="+file.name)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"),file)
        val part: MultipartBody.Part = MultipartBody.Part.createFormData("photo[]",file.name, requestBody)
        val call = uploadApi.inqueryUpload(part)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("UploadService","uploadInqueryPhoto error image" + t)
                Toast.makeText(context,"사진 등록에 실패했습니다.", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body:String = response.body()!!.string()
                Log.i("UploadService","response raw=" + response.raw()+",body=" + body)
                val url:String = "javascript:inquiryUploadComplete('"+body+"')"
                webview.loadUrl(url)
                Log.d("UploadService","call : " + url)
            }
        })
    }


    private fun getRealPath(contentURI: Uri?):String?{
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        if(contentURI != null) {
            val cursor: Cursor? = context.contentResolver.query(contentURI, projection, null, null, null)
            try {
                val columeIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columeIndex)
                }
            } finally {
                cursor!!.close()
            }
        }
        return null
    }
}