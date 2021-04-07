package com.example.atvcable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.atvcable.io.ApiService
import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.get
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_ot_finalizar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class FragmentFinalizar: BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ot_finalizar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var ordenTrabajosPActi: OrdenTrabajosPActivity = context  as OrdenTrabajosPActivity

        val text = ordenTrabajosPActi.botonFragmentFinalizar.arguments?.getString("idot")
        tvIdOTPFragment.text = text

        val fechahoraa = ordenTrabajosPActi.botonFragmentFinalizar.arguments?.getString("fha")
        tvfechaarriboOTPFragment.text = fechahoraa


        btnFinalizarOTP2.setOnClickListener {
            val apiService: ApiService by lazy {
                ApiService.create()
            }

            val preferences by lazy {
                PreferenceHelper.defaultPrefs(this.requireContext())
            }
            val jwt = preferences["jwt", ""]
            val authHeader = "Bearer $jwt"

            val Id = tvIdOTPFragment.text.toString()
            val Activa = "atendida"

            val FechaHoraArrivo = tvfechaarriboOTPFragment.text.toString()

            val Resultado = etOTResultado.text.toString()

            val c: Calendar = Calendar.getInstance()
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formattedDate: String = df.format(c.getTime())
            val FechaHoraSalida = formattedDate


            var Latitud = ordenTrabajosPActi.mLastLocation.latitude.toString()
            val Longitud = ordenTrabajosPActi.mLastLocation.longitude.toString()
            val call = apiService.postOrdenTrabajo(authHeader, Id, Activa, Latitud, Longitud, Resultado, FechaHoraArrivo, FechaHoraSalida) //(authHeader, Activa, phone, address)
            call.enqueue(object: Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(ordenTrabajosPActi, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(ordenTrabajosPActi, "La orden de trabajo fue atendida con éxito", Toast.LENGTH_SHORT).show()
                        regresarMenu()
                    }
                }
                private fun regresarMenu(){
                    val context: Context
                    context = ordenTrabajosPActi
                    val intent = Intent(context, MenuActivity::class.java)
                    context.startActivity(intent)
                }
            })
        }

    }

}
