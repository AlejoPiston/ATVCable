package com.example.atvcable


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import androidx.recyclerview.widget.LinearLayoutManager

import com.example.atvcable.io.ApiService
import com.example.atvcable.modelos.OrdenTrabajo
import com.example.atvcable.modelos.OrdenTrabajosPAdapter


import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.get
import com.example.atvcable.util.toast
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_orden_trabajos_p.*


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrdenTrabajosPActivity : AppCompatActivity() {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    //lateinit var btnStopUpdates: Button
    lateinit var txtLatp: TextView
    lateinit var txtLongp: TextView
    lateinit var txtTimep: TextView
    lateinit var txtDireccionp: TextView


    private val apiService: ApiService by lazy {
        ApiService.create()
    }
    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }
    private val ordenestrabajoAdapter = OrdenTrabajosPAdapter()
    val botonFragmentFinalizar = FragmentFinalizar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orden_trabajos_p)

        mLocationRequest = LocationRequest()

        //btnStartupdate = findViewById(R.id.btn_start_upds)

        txtLatp = findViewById(R.id.txtLatp);
        txtLongp = findViewById(R.id.txtLongp);
        txtTimep = findViewById(R.id.txtTimep);
        txtDireccionp = findViewById(R.id.txtDireccionp);


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
        startLocationUpdates()

        /*btnStartupdate.setOnClickListener {
            if (checkPermissionForLocation(this)) {
                startLocationUpdates()
                btnStartupdate.isEnabled = false
                btnStopUpdates.isEnabled = true
            }
        }

        btnStopUpdates.setOnClickListener {
            stoplocationUpdates()
            txtTime.text = "Updates Stoped"
            btnStartupdate.isEnabled = true
            btnStopUpdates.isEnabled = false
        }*/


        loadOrdenesTrabajo()

        recyclerviewOrdenTrabajosP.layoutManager = LinearLayoutManager(this)
        recyclerviewOrdenTrabajosP.adapter = ordenestrabajoAdapter

    }

    private fun loadOrdenesTrabajo(){
        val jwt = preferences["jwt", ""]
        val call = apiService.getOrdenesP("Bearer $jwt")
        call.enqueue(object: Callback<ArrayList<OrdenTrabajo>> {
            override fun onFailure(call: Call<ArrayList<OrdenTrabajo>>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<ArrayList<OrdenTrabajo>>,
                response: Response<ArrayList<OrdenTrabajo>>
            ) {
                if (response.isSuccessful) {
                    response.body() ?.let {
                        ordenestrabajoAdapter.ordenestrabajo = it
                        ordenestrabajoAdapter.notifyDataSetChanged()

                    }
                }
            }

        })
    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Tu GPS parece estar desactivado, ¿quieres activarlo?")
            .setCancelable(false)
            .setPositiveButton("Si") { dialog, id ->
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    fun startLocationUpdates() {

        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }
    private fun getDireccion(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list[0].getAddressLine(0)
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined

        mLastLocation = location
        val date: Date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh:mm:ss a")
        txtTimep.text = "Hora: " + sdf.format(date)
        txtLatp.text = "Lat: " + mLastLocation.latitude
        txtLongp.text = "Long: " + mLastLocation.longitude
        // You can now create a LatLng Object for use with maps
        val address = getDireccion(mLastLocation.latitude, mLastLocation.longitude)
        txtDireccionp.text = address

    }
    fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
        txtTimep.text ="Ubicación a enviar"

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
                //btnStartupdate.isEnabled = false
                //btnStopUpdates.isEnabled = true
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }
}