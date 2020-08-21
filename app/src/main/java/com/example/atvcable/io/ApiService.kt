package com.example.atvcable.io

import com.example.atvcable.io.response.LoginResponse
import com.example.atvcable.modelos.Ficha
import com.example.atvcable.modelos.OrdenTrabajo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {

    @POST("auth/fcm/token")
    fun postToken(
        @Header("Authorization") authHeader: String,
        @Query("device_token") token: String
    ): Call<Void>

    @POST( value = "auth/login")
    fun postLogin(@Query(value = "email") email: String, @Query(value = "password") password: String):
            Call<LoginResponse>

    @POST( value = "auth/logout")
    fun postLogout(@Header(value = "Authorization") authHeader: String):
            Call<Void>

    @GET( value = "auth/ordenes_t")
    fun getOrdenes(@Header(value = "Authorization") authHeader: String): Call<ArrayList<OrdenTrabajo>>

    @GET( value = "auth/ordenes_tc")
    fun getOrdenesC(@Header(value = "Authorization") authHeader: String): Call<ArrayList<OrdenTrabajo>>

    @GET( value = "auth/ordenes_tp")
    fun getOrdenesP(@Header(value = "Authorization") authHeader: String): Call<ArrayList<OrdenTrabajo>>

    companion object Factory {
        private const val BASE_URL = "http://104.131.36.170/api/"

        fun create() : ApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}