package com.example.appsofware.data

data class Usuario(
    var nombre: String = "Laura",
    var email: String  = "",
    var telefono: String = "",
    var edad: String = "",
    var anioVinculacion: String = "",
    val imageUrl: String = "https://randomuser.me/api/portraits/women/2.jpg"
)
