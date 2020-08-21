package com.example.atvcable.modelos

data class Ficha(
    val Id: Int,
    val Nombres: String,
    val Apellidos: String,
    val DireccionDomicilio: String,
    val TelefonoDomicilio: String
) {
    override fun toString(): String {
        return Nombres
    }
}