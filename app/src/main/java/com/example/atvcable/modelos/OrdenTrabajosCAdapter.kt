package com.example.atvcable.modelos

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.atvcable.R
import kotlinx.android.synthetic.main.item_ordentrabajoc.view.*


class OrdenTrabajosCAdapter
    : RecyclerView.Adapter<OrdenTrabajosCAdapter.ViewHolder>() {
    var ordenestrabajo = ArrayList<OrdenTrabajo>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){



        fun bind(ordentrabajo: OrdenTrabajo) = with (itemView) {

            tvIdOTCo.text = "Orden de Trabajo ${ordentrabajo.Id}"
            tvClienteOTCo.text = "${ordentrabajo.fichaordentrabajo.Nombres} ${ordentrabajo.fichaordentrabajo.Apellidos}"
            tvDireccionOTCo.text = "${ordentrabajo.fichaordentrabajo.DireccionDomicilio}"
            tvTelefonoOTCo.text = "${ordentrabajo.fichaordentrabajo.TelefonoDomicilio}"

            tvDañoOTCo.text = "Reparación de ${ordentrabajo.Dano}"

            ibExpand.setOnClickListener {
                TransitionManager.beginDelayedTransition(parent as ViewGroup, AutoTransition())

                if (linearLayoutDetails.visibility == View.VISIBLE) {
                    linearLayoutDetails.visibility = View.GONE
                    ibExpand.setImageResource(R.drawable.ic_expandir)
                } else {
                    linearLayoutDetails.visibility = View.VISIBLE
                    ibExpand.setImageResource(R.drawable.ic_minimizar)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ordentrabajoc, parent, false)
        )
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ordentrabajo = ordenestrabajo[position]
        holder.bind(ordentrabajo)

    }

    override fun getItemCount() = ordenestrabajo.size

}