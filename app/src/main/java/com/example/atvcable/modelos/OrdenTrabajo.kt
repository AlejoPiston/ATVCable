package com.example.atvcable.modelos


import com.google.gson.annotations.SerializedName

data class OrdenTrabajo(
    val Id: Int,
    val Fecha: String,
    val Dano: String,

    val Tipo: String,
    val NombreCliente: String,
    val Referencia: String,
    val Direccion: String,
    val Telefono: String,

    val Resultado: String,
    val FechaHoraArrivo: String,
    val FechaHoraSalida: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val Activa: String,

    val fichaordentrabajo: Ficha
)