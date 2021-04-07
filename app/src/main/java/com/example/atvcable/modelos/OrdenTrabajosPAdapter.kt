package com.example.atvcable.modelos

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.atvcable.OrdenTrabajosPActivity
import com.example.atvcable.R
import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.get
import kotlinx.android.synthetic.main.item_ordentrabajop.view.*



class OrdenTrabajosPAdapter
    : RecyclerView.Adapter<OrdenTrabajosPAdapter.ViewHolder>() {
    var ordenestrabajo = ArrayList<OrdenTrabajo>()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        private val preferences by lazy {
            PreferenceHelper.defaultPrefs(this.itemView.context)
        }

        fun bind(ordentrabajo: OrdenTrabajo) = with (itemView) {

            if (ordentrabajo.Tipo == "instalacion"){
                tvIdOTCotextop.text ="Instalación"
                tvIdOTP.text = "${ordentrabajo.Id}"
                tvClienteOTP.text = "${ordentrabajo.NombreCliente}"
                tvDireccionOTP.text = "${ordentrabajo.Direccion}"
                tvTelefonoOTP.text = "${ordentrabajo.Telefono}"
                tvDañoOTP.text = "Instalación en ${ordentrabajo.Referencia}"
            }else{
                tvIdOTCotextop.text ="Orden de Trabajo"
                tvIdOTP.text = "${ordentrabajo.Id}"
                tvClienteOTP.text = "${ordentrabajo.fichaordentrabajo.Nombres} ${ordentrabajo.fichaordentrabajo.Apellidos}"
                tvDireccionOTP.text = "${ordentrabajo.fichaordentrabajo.DireccionDomicilio}"
                tvTelefonoOTP.text = "${ordentrabajo.fichaordentrabajo.TelefonoDomicilio}"
                tvDañoOTP.text = "Reparación de ${ordentrabajo.Dano}"
            }

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
                        // Obtienes el texto
                        val texto = ordentrabajo.Id.toString()
                        val fechahoraarrivo = ordentrabajo.FechaHoraArrivo.toString()
                        // Creamos un nuevo Bundle
                        val args = Bundle()
                        // Colocamos el String
                        args.putString("idot", texto)
                        args.putString("fha", fechahoraarrivo)

                        // Supongamos que tu Fragment se llama TestFragment. Colocamos este nuevo Bundle como argumento en el fragmento.
                        ordenTrabajosPActivity.botonFragmentFinalizar.arguments = args

                        ordenTrabajosPActivity.botonFragmentFinalizar.show(ordenTrabajosPActivity.supportFragmentManager, "Boton fragment finalizar")


                    }
                }
            }
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
