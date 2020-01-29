package com.bconline.homesapp

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import java.util.*

class IntroActivity : AppCompatActivity() {

    private var locationManager : LocationManager? = null
    private val REQUEST_CODE:Int = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        checkLocationProvider()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode ==REQUEST_CODE) {
            goMain()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun goMain(){
        Timer().schedule(object:TimerTask(){
            override fun run() {
                val intent = Intent(this@IntroActivity, MainActivity::class.java)
                startActivity(intent)
                this@IntroActivity.finish()
            }
        },2000)
    }

    private fun checkLocationProvider(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if(!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            val builder = AlertDialog.Builder(ContextThemeWrapper(this@IntroActivity, R.style.Theme_AppCompat_Light_Dialog))
            builder.setMessage("GPS 모듈을 켜야 주위 매물정보를 편하게 보실수 있습니다. GPS 기능을 켜주세요.")
            builder.setPositiveButton("켜기") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent,REQUEST_CODE)
            }
            builder.setNegativeButton("취소") { _, _ ->
                goMain()
            }
            builder.show()
        } else {
            goMain()
        }
    }
}
