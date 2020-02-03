package com.bconline.homesapp.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UploadApi {
    @Multipart
    @POST("member/app_upload")
    fun memberUpload(@Part file: MultipartBody.Part): Call<ResponseBody>

    @Multipart
    @POST("inquiry/app_upload")
    fun inqueryUpload(@Part file: MultipartBody.Part): Call<ResponseBody>
}