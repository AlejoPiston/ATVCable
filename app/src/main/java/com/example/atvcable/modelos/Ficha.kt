package com.example.atvcable.modelos

data class Ficha(
    val Id: Int,
    val Nombres: String,
    val Apellidos: String
) {
    override fun toString(): String {
        return Nombres
    }
}