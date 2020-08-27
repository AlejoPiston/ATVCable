package com.example.atvcable.modelos

import android.content.Context
import android.content.Intent
import android.location.Location
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.atvcable.MenuActivity
import com.example.atvcable.OrdenTrabajosPActivity

import com.example.atvcable.R
import com.example.atvcable.io.ApiService
import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.get
import kotlinx.android.synthetic.main.item_ordentrabajop.view.*


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdenTrabajosPAdapter
    : RecyclerView.Adapter<OrdenTrabajosPAdapter.ViewHolder>() {
    var ordenestrabajo = ArrayList<OrdenTrabajo>()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val apiService: ApiService by lazy {
            ApiService.create()
        }
        private val preferences by lazy {
            PreferenceHelper.defaultPrefs(this.itemView.context)
        }

        fun bind(ordentrabajo: OrdenTrabajo) = with (itemView) {

            tvIdOTCotextop.text ="Orden de Trabajo"
            tvIdOTP.text = "${ordentrabajo.Id}"
            tvClienteOTP.text = "${ordentrabajo.fichaordentrabajo.Nombres} ${ordentrabajo.fichaordentrabajo.Apellidos}"
            tvDireccionOTP.text = "${ordentrabajo.fichaordentrabajo.DireccionDomicilio}"
            tvTelefonoOTP.text = "${ordentrabajo.fichaordentrabajo.TelefonoDomicilio}"

            tvDañoOTP.text = "Reparación de ${ordentrabajo.Dano}"
            val Id = tvIdOTP.text.toString()
            val Activa = "atendida"
            val jwt = preferences["jwt", ""]
            val authHeader = "Bearer $jwt"

            ibExpandP.setOnClickListener {
                var ordenTrabajosPActivity: OrdenTrabajosPActivity = context  as OrdenTrabajosPActivity

                TransitionManager.beginDelayedTransition(parent as ViewGroup, AutoTransition())

                if (linearLayoutDetails.visibility == View.VISIBLE) {
                    ordenTrabajosPActivity.startLocationUpdates()

                    linearLayoutDetails.visibility = View.GONE
                    ibExpandP.setImageResource(R.drawable.ic_expandir)


                } else {
                    ordenTrabajosPActivity.stoplocationUpdates()
                    linearLayoutDetails.visibility = View.VISIBLE
                    ibExpandP.setImageResource(R.drawable.ic_minimizar)
                    btnFinalizarOTP.setOnClickListener {

                        var Latitud = ordenTrabajosPActivity.mLastLocation.latitude.toString()
                        val Longitud = ordenTrabajosPActivity.mLastLocation.longitude.toString()
                        val call = apiService.postOrdenTrabajo(authHeader, Id, Activa, Latitud, Longitud) //(authHeader, Activa, phone, address)
                        call.enqueue(object: Callback<Void> {
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(itemView.context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(itemView.context, "La orden de trabajo fue atendida con éxito", Toast.LENGTH_SHORT).show()
                                    regresarMenu()
                                }
                            }
                        })


                    }
                }
            }
        }
        private fun regresarMenu(){
            val context: Context
            context = itemView.context
            val intent = Intent(context, MenuActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ordentrabajop, parent, false)
        )
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ordentrabajo = ordenestrabajo[position]
        holder.bind(ordentrabajo)

    }

    override fun getItemCount() = ordenestrabajo.size
}
