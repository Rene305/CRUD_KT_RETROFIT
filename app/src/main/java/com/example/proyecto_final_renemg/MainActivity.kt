package com.example.proyecto_final_renemg


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_renemg.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Response

class MainActivity : AppCompatActivity(), UsuarioAdapter.OnItemClicked {
    lateinit var binding: ActivityMainBinding
    lateinit var adatador: UsuarioAdapter

    var listaUsuarios = arrayListOf<Usuario>()


    var usuario = Usuario(-1, "","","")
    var isEditando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()

        obtenerUsuarios()

        binding.btnGuardar.setOnClickListener {
            var isValido = validarCampos()
            if (isValido) {
                if (!isEditando) {
                    agregarUsuario()
                } else {
                    actualizarUsuario()
                }
            } else {
                Toast.makeText(this, "Se deben llenar los campos", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun setupRecyclerView() {
        adatador = UsuarioAdapter(this, listaUsuarios)
        adatador.setOnClick(this@MainActivity)
        binding.rvUsuarios.adapter = adatador

    }

    fun validarCampos(): Boolean {
        return !(binding.etNombre.text.isNullOrEmpty() || binding.etCorreo.text.isNullOrEmpty() || binding.etContrasenia.text.isNullOrEmpty())
    }

    fun obtenerUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerUsuarios()
            runOnUiThread {
                if (call.isSuccessful) {
                    listaUsuarios = call.body()!!.listaUsuarios
                    setupRecyclerView()
                } else {
                    Toast.makeText(this@MainActivity, "ERROR CONSULTAR TODOS", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun agregarUsuario() {

        this.usuario.idUsuario = -1
        this.usuario.nombre = binding.etNombre.text.toString()
        this.usuario.correo = binding.etCorreo.text.toString()
        this.usuario.contrasenia = binding.etContrasenia.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.agregarUsuario(usuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()

                } else {
                    Toast.makeText(this@MainActivity, "ERROR ADD", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun actualizarUsuario() {

        this.usuario.nombre = binding.etNombre.text.toString()
        this.usuario.correo = binding.etCorreo.text.toString()
        this.usuario.contrasenia = binding.etContrasenia.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.actualizarUsuario(usuario.idUsuario, usuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()

                    binding.btnGuardar.setText("Agregar Usuario")
                    binding.btnGuardar.backgroundTintList = resources.getColorStateList(R.color.celeste)
                    isEditando = false
                }
            }
        }
    }

    fun limpiarCampos() {
        binding.etNombre.setText("")
        binding.etCorreo.setText("")
        binding.etContrasenia.setText("")
    }

    fun limpiarObjeto() {
        this.usuario.idUsuario = -1
        this.usuario.nombre = ""
        this.usuario.correo = ""
        this.usuario.contrasenia = ""
    }

    override fun editarUsuario(usuario: Usuario) {
        binding.etNombre.setText(usuario.nombre)
        binding.etCorreo.setText(usuario.correo)
        binding.etContrasenia.setText(usuario.contrasenia)
        binding.btnGuardar.setText("Actualizar Usuario")
        binding.btnGuardar.backgroundTintList = resources.getColorStateList(R.color.verde)
        this.usuario = usuario
        isEditando = true
    }

    override fun borrarUsuario(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.borrarUsuario(idUsuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                }
            }
        }
    }
}