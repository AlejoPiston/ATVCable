package com.example.atvcable.modelos


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.atvcable.MenuActivity
import com.example.atvcable.OrdenTrabajosCaActivity
import com.example.atvcable.R
import com.example.atvcable.io.ApiService
import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.get
import kotlinx.android.synthetic.main.item_ordentrabajoc.view.*

import kotlinx.android.synthetic.main.item_ordentrabajoca.view.*
import kotlinx.android.synthetic.main.item_ordentrabajoca.view.linearLayoutDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdenTrabajosCaAdapter
    : RecyclerView.Adapter<OrdenTrabajosCaAdapter.ViewHolder>() {
    var ordenestrabajo = ArrayList<OrdenTrabajo>()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

         private val apiService: ApiService by lazy {
             ApiService.create()
         }
         private val preferences by lazy {
             PreferenceHelper.defaultPrefs(this.itemView.context)
         }

        fun bind(ordentrabajo: OrdenTrabajo) = with (itemView) {

            tvIdOTCotextoca.text ="Orden de Trabajo"
            tvIdOTCa.text = "${ordentrabajo.Id}"
            tvClienteOTCa.text = "${ordentrabajo.fichaordentrabajo.Nombres} ${ordentrabajo.fichaordentrabajo.Apellidos}"
            tvDireccionOTCa.text = "${ordentrabajo.fichaordentrabajo.DireccionDomicilio}"
            tvTelefonoOTCa.text = "${ordentrabajo.fichaordentrabajo.TelefonoDomicilio}"

            tvDañoOTCa.text = "Reparación de ${ordentrabajo.Dano}"
            val Id = tvIdOTCa.text.toString()
            val Activa = "en progreso"
            val jwt = preferences["jwt", ""]
            val authHeader = "Bearer $jwt"

            ibExpandca.setOnClickListener {
                var ordenTrabajosCaActivity: OrdenTrabajosCaActivity = context  as OrdenTrabajosCaActivity

                TransitionManager.beginDelayedTransition(parent as ViewGroup, AutoTransition())

                if (linearLayoutDetails.visibility == View.VISIBLE) {
                    ordenTrabajosCaActivity.startLocationUpdates()

                    linearLayoutDetails.visibility = View.GONE
                    ibExpandca.setImageResource(R.drawable.ic_expandir)


                } else {
                    ordenTrabajosCaActivity.stoplocationUpdates()
                    linearLayoutDetails.visibility = View.VISIBLE
                    ibExpandca.setImageResource(R.drawable.ic_minimizar)
                    btnSolucionarOTCa.setOnClickListener {

                        var Latitud = ordenTrabajosCaActivity.mLastLocation.latitude.toString()
                        val Longitud = ordenTrabajosCaActivity.mLastLocation.longitude.toString()
                        val call = apiService.postOrdenTrabajo(authHeader, Id, Activa, Latitud, Longitud, Resultado = "") //(authHeader, Activa, phone, address)
                        call.enqueue(object: Callback<Void> {
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(itemView.context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(itemView.context, "La orden de trabajo está en progreso", Toast.LENGTH_SHORT).show()
                                    regresarMenu()
                                }
                            }
                        })


                    }
                    btnCancelarOTCa.setOnClickListener {
                        // Obtienes el texto
                        val texto = ordentrabajo.Id.toString()
                        // Creamos un nuevo Bundle
                        val args = Bundle()
                        // Colocamos el String
                        args.putString("idot", texto)
                        // Supongamos que tu Fragment se llama TestFragment. Colocamos este nuevo Bundle como argumento en el fragmento.
                        ordenTrabajosCaActivity.botonFragmentCancelarOTCa.arguments = args

                        ordenTrabajosCaActivity.botonFragmentCancelarOTCa.show(ordenTrabajosCaActivity.supportFragmentManager, "Boton fragment cancelarOTca")


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
                R.layout.item_ordentrabajoca, parent, false)
        )
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ordentrabajo = ordenestrabajo[position]
        holder.bind(ordentrabajo)

    }

    override fun getItemCount() = ordenestrabajo.size
}
