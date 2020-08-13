package com.example.atvcable.modelos


import com.google.gson.annotations.SerializedName

data class OrdenTrabajo(
    val Id: Int,
    val Fecha: String,
    val Dano: String,
    val Resultado: String,
    val FechaHoraArrivo: String,
    val FechaHoraSalida: String,
    @SerializedName("created_at") val createdAt: String,
    val Activa: String,

    val fichaordentrabajo: Ficha
)