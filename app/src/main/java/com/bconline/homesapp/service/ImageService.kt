package com.bconline.homesapp.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore

object ImageService {

    fun pickImageFromGallery(activity: Activity, reqeustCode:Int){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activity.startActivityForResult(intent,reqeustCode)
    }

    fun pickMultiImageFromGallery(activity: Activity, reqeustCode:Int){
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(intent,"이미지선택"),reqeustCode)
    }
}