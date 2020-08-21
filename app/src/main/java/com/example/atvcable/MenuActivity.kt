package com.example.atvcable

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.atvcable.io.ApiService
import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.set
import com.example.atvcable.util.PreferenceHelper.get
import com.example.atvcable.util.toast
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {
    private val apiService by lazy{
        ApiService.create()
    }
    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }
    private val authHeader by lazy {
        val jwt = preferences["jwt", ""]
        "Bearer $jwt"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val storeToken = intent.getBooleanExtra("store_token", false)
        if (storeToken)
            storeToken()

        setOnClickListeners()
    }
    private fun setOnClickListeners() {

        btnOT.setOnClickListener {
            val intent = Intent(this, OrdenTrabajosActivity::class.java)
            startActivity(intent)
        }
        btnOTC.setOnClickListener {
            val intent = Intent(this, OrdenTrabajosCActivity::class.java)
            startActivity(intent)
        }
        btnOTP.setOnClickListener {
            val intent = Intent(this, OrdenTrabajosPActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout(){
        val call = apiService.postLogout(authHeader)
         call.enqueue(object: Callback<Void> {
             override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
             }

             override fun onResponse(call: Call<Void>, response: Response<Void>) {
                clearSessionPreference()
                 val intent = Intent(this@MenuActivity, MainActivity::class.java)
                 startActivity(intent)
                 finish()
             }

         })
    }
    private fun clearSessionPreference() {
        preferences["jwt"] = ""
    }

    companion object {
        private const val TAG = "MenuActivity"
    }

    private fun storeToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val deviceToken = instanceIdResult.token

            val call = apiService.postToken(authHeader, deviceToken)
            call.enqueue(object: Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    toast(t.localizedMessage)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Token registrado correctamente")
                    } else {
                        Log.d(TAG, "Hubo un problema al registrar el token")
                    }
                }
            })
        }
    }
}