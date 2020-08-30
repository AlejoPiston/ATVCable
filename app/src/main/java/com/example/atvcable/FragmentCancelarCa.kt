package com.example.atvcable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import com.example.atvcable.io.ApiService

import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.get
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_ot_cancelar.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCancelarCa: BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ot_cancelar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var ordenTrabajosCaActi: OrdenTrabajosCaActivity = context  as OrdenTrabajosCaActivity

        val text = ordenTrabajosCaActi.botonFragmentCancelarOTCa.arguments?.getString("idot")
        tvIdOTFragmentCancelar.text = text

        btnCancelarOT2.setOnClickListener {
            val apiService: ApiService by lazy {
                ApiService.create()
            }

            val preferences by lazy {
                PreferenceHelper.defaultPrefs(this.requireContext())
            }
            val jwt = preferences["jwt", ""]
            val authHeader = "Bearer $jwt"

            val Id = tvIdOTFragmentCancelar.text.toString()
            val Activa = "cancelada"
            val Justificacion = etOTJustificacion.text.toString()

            var Latitud = ordenTrabajosCaActi.mLastLocation.latitude.toString()
            val Longitud = ordenTrabajosCaActi.mLastLocation.longitude.toString()
            val call = apiService.postOrdenTrabajoCan(authHeader, Id, Activa, Latitud, Longitud, Justificacion) //(authHeader, Activa, phone, address)
            call.enqueue(object: Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(ordenTrabajosCaActi, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(ordenTrabajosCaActi, "La orden de trabajo fue cancelada con éxito", Toast.LENGTH_SHORT).show()
                        regresarMenu()
                    }
                }
                private fun regresarMenu(){
                    val context: Context
                    context = ordenTrabajosCaActi
                    val intent = Intent(context, MenuActivity::class.java)
                    context.startActivity(intent)
                }
            })
        }

    }

}
