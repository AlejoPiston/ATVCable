package com.example.atvcable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atvcable.io.ApiService
import com.example.atvcable.modelos.OrdenTrabajo

import com.example.atvcable.modelos.OrdenTrabajosCAdapter
import com.example.atvcable.util.PreferenceHelper
import com.example.atvcable.util.PreferenceHelper.get
import com.example.atvcable.util.toast
import kotlinx.android.synthetic.main.activity_orden_trabajos.*
import kotlinx.android.synthetic.main.activity_orden_trabajos_c.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdenTrabajosCActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }
    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    private val ordenestrabajoAdapter = OrdenTrabajosCAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orden_trabajos_c)

        loadOrdenesTrabajo()

        recyclerviewOrdenTrabajosC.layoutManager = LinearLayoutManager(this)
        recyclerviewOrdenTrabajosC.adapter = ordenestrabajoAdapter

    }

    private fun loadOrdenesTrabajo(){
        val jwt = preferences["jwt", ""]
        val call = apiService.getOrdenesC("Bearer $jwt")
        call.enqueue(object: Callback<ArrayList<OrdenTrabajo>> {
            override fun onFailure(call: Call<ArrayList<OrdenTrabajo>>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<ArrayList<OrdenTrabajo>>,
                response: Response<ArrayList<OrdenTrabajo>>
            ) {
                if (response.isSuccessful) {
                    response.body() ?.let {
                        ordenestrabajoAdapter.ordenestrabajo = it
                        ordenestrabajoAdapter.notifyDataSetChanged()
                    }

                }
            }

        })
    }
}