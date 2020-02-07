package com.bconline.homesapp.service

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log

class LocationService(val context:Context) {

    private var locationManager : LocationManager? = null
    private var lat:String? = null
    private var lng:String? = null

    fun initLocation(){
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            val location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location != null) {
                lat = location.latitude.toString()
                lat = location.longitude.toString()
            }
        }catch(ex: SecurityException){
            Log.d("MainActivity","security exception" + ex)
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            lat = location.latitude.toString()
            lng = location.longitude.toString()
            Log.d("MainActivity","gps=" + location.longitude + ":" + location.latitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    fun getLocationString():String{
        return String.format("{\"lat\":%s,\"lng\":%s}",lat, lng)
    }
}