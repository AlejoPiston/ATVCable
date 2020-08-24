package com.example.atvcable.modelos


import android.content.Context
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.atvcable.MenuActivity
import com.example.atvcable.R
import com.example.atvcable.io.ApiService
import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.get
import kotlinx.android.synthetic.main.item_ordentrabajoc.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdenTrabajosCAdapter
    : RecyclerView.Adapter<OrdenTrabajosCAdapter.ViewHolder>() {
    var ordenestrabajo = ArrayList<OrdenTrabajo>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

         private val apiService: ApiService by lazy {
             ApiService.create()
         }
         private val preferences by lazy {
             PreferenceHelper.defaultPrefs(this.itemView.context)
         }

        fun bind(ordentrabajo: OrdenTrabajo) = with (itemView) {
            tvIdOTCotexto.text ="Orden de Trabajo"
            tvIdOTCo.text = "${ordentrabajo.Id}"
            tvClienteOTCo.text = "${ordentrabajo.fichaordentrabajo.Nombres} ${ordentrabajo.fichaordentrabajo.Apellidos}"
            tvDireccionOTCo.text = "${ordentrabajo.fichaordentrabajo.DireccionDomicilio}"
            tvTelefonoOTCo.text = "${ordentrabajo.fichaordentrabajo.TelefonoDomicilio}"

            tvDañoOTCo.text = "Reparación de ${ordentrabajo.Dano}"
            val Id = tvIdOTCo.text.toString()
            val Activa = "en progreso"
            val jwt = preferences["jwt", ""]
            val authHeader = "Bearer $jwt"

            ibExpand.setOnClickListener {
                TransitionManager.beginDelayedTransition(parent as ViewGroup, AutoTransition())

                if (linearLayoutDetails.visibility == View.VISIBLE) {
                    linearLayoutDetails.visibility = View.GONE
                    ibExpand.setImageResource(R.drawable.ic_expandir)


                } else {
                    linearLayoutDetails.visibility = View.VISIBLE
                    ibExpand.setImageResource(R.drawable.ic_minimizar)
                    btnAtenderOTCo.setOnClickListener {
                        val call = apiService.postOrdenTrabajo(authHeader, Id, Activa) //(authHeader, Activa, phone, address)
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
                R.layout.item_ordentrabajoc, parent, false)
        )
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ordentrabajo = ordenestrabajo[position]
        holder.bind(ordentrabajo)

    }

    override fun getItemCount() = ordenestrabajo.size
}
