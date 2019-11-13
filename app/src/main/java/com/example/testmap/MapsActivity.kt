package com.example.testmap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    /**
     * 緯度フィールド。
     */
    var _latitude = 0.0
    /**
     * 経度フィールド
     */
   var _longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //LocationManagerオブジェクトを取得。
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //位置情報が更新された際のリスナオブジェクトを生成。
        val locationListener = GPSLocationListener()
        //ACCESS_FINE_LOCATIONの許可が下りていないなら…
        if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示。その際、リクエストコードを1000に設定。
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this@MapsActivity, permissions, 1000)
            //onCreate()メソッドを終了。
            return
        }
        /**
         * パラメータの説明をします。
         * @param provider 上記で説明したプロバイダー名を指定します。
         * @param minTime 位置情報の更新間隔をミリ秒で指定します。
         * @param minDistance 位置情報の更新距離をメートルで指定します。
         * @param listener LocationListenerを指定します。
         */
        //位置情報の追跡を開始。
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //ACCESS_FINE_LOCATIONに対するパーミションダイアログでかつ許可を選択したなら…
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //LocationManagerオブジェクトを取得。
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            //位置情報が更新された際のリスナオブジェクトを生成。
            val locationListener = GPSLocationListener()
            onMapReady(mMap)
            //再度ACCESS_FINE_LOCATIONの許可が下りていないかどうかのチェックをし、降りていないなら処理を中止。
            if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            //位置情報の追跡を開始。
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    /**
     * ロケーションリスナクラス。
     */
    private inner class GPSLocationListener : LocationListener {
        //位置情報が更新されたら
        override fun onLocationChanged(location: Location) {
            //引数のLocationオブジェクトから緯度を取得。
            _latitude = location.latitude
            //引数のLocationオブジェクトから経度を取得。
            _longitude = location.longitude
            val myLocation = LatLng(_latitude, _longitude)
            //地図へのマーカーの設定方法(ピンの位置)
            mMap.addMarker(MarkerOptions().position(myLocation).title("now").snippet("tigatiga"))
            val latLng = LatLng(_latitude, _longitude) // 中心点
            val radius = 1000 * 10.0 // 10km
            mMap.addCircle(
                CircleOptions()
                    .center(latLng)          // 円の中心位置
                    .radius(radius)          // 半径 (メートル単位)
                    .strokeColor(Color.BLUE) // 線の色
                    .strokeWidth(2f)         // 線の太さ
                    .fillColor(0x40ff1493)   // 円の塗りつぶし色
            )
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //羽田の座標を設定
        val tokyo = LatLng(35.681298, 139.766247)
        //地図へのマーカーの設定方法(ピンの位置)
        mMap.addMarker(MarkerOptions().position(tokyo).title("東京").snippet("tigatiga"))
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //地図の移動　画面上に表示される場所を指定する(起動時の場所)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tokyo))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //マップのズーム絶対値指定　1: 世界 5: 大陸 10:都市 15:街路 20:建物 ぐらいのサイズ
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        mMap.setMyLocationEnabled(true)

        //地図を移動して特定段階のズーム
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanedaAirport, 15F))

        //UI設定　
        googleMap.uiSettings.run {
            //スクロール操作
            //isScrollGesturesEnabled = false

        }

        //スクロール操作
        //googleMap.uiSettings.isScrollGesturesEnabled = false



    }


}
