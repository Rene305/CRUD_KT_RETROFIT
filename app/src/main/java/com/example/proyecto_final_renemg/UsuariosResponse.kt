package com.example.proyecto_final_renemg

import com.google.gson.annotations.SerializedName

data class UsuariosResponse(
    @SerializedName("listaUsuarios") var listaUsuarios:ArrayList<Usuario>
)


