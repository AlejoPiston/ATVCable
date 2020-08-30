package com.example.atvcable

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_main.*
import com.example.atvcable.util.PreferenceHelper.get
import com.example.atvcable.util.PreferenceHelper.set
import com.example.atvcable.io.ApiService
import com.example.atvcable.io.response.LoginResponse
import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val apiService: ApiService by lazy{
        ApiService.create()
    }
    private val snackBar by lazy{
        Snackbar.make(mainLayout,"Presione una vez más para salir", Snackbar.LENGTH_SHORT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = PreferenceHelper.defaultPrefs(this)
        if (preferences["jwt", ""].contains("."))
            goToMenuActivity()

        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin(){

        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            toast("Por favor ingrese un correo y una contraseña")
            return
        }

        val call = apiService.postLogin(email, password)
        call.enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)

            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse == null) {
                        toast("Se obtuvo una respuesta inesperada del servidor")
                        return
                    }
                    if (loginResponse.success) {
                        createSessionPreference(loginResponse.jwt)
                        toast("Bienvenido ${loginResponse.user.name} ${loginResponse.user.Apellidos}")
                        goToMenuActivity(true)
                    } else{
                        toast("Las credenciales son incorrectas")
                    }
                }else{
                    toast("Se obtuvo una respuesta inesperada del servidor")
                }
            }
        })
    }

    private fun createSessionPreference(jwt: String){

        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["jwt"] = jwt
    }
    private fun goToMenuActivity(isUserInput: Boolean = false){
        val intent = Intent(this, MenuActivity::class.java)
        if (isUserInput) {
            intent.putExtra("store_token", true)
        }

        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

        if(snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()


    }
}