package com.datoban.rich_uncle.visual.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel : ViewModel() {

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onUsernameChange(value: String) {
        _username.value = value
    }
    fun clearLoginFields() {
        _email.value = ""
        _password.value = ""
    }
    // LOGIN
    fun login(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val email = _email.value ?: ""
        val password = _password.value ?: ""


        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val uid = result.user?.uid ?: return@addOnSuccessListener

                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .get()
                    .addOnSuccessListener { snapshot ->

                        val name = snapshot.child("name").getValue(String::class.java) ?: "Anon"

                        _username.value = name // 🔥 GUARDAS EL NOMBRE
                        println("USERNAME ACTUAL: ${_username.value}")

                        clearLoginFields()

                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError("Error obteniendo usuario")
                    }
            }

            .addOnFailureListener {
                _error.value = "Contraseña o correo incorrectos"
                onError(it.message ?: "Error al iniciar sesión")
            }
    }

    // SIGN UP
    fun signUp(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val email = _email.value ?: ""
        val password = _password.value ?: ""
        val username = _username.value ?: ""

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            onError("Completa todos los campos")
            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                clearLoginFields()
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val userMap = mapOf(
                    "id" to uid,
                    "name" to username,
                    "email" to email
                )

                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .setValue(userMap)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError("Error guardando usuario")
                    }
            }
            .addOnFailureListener {
                onError(it.message ?: "Error en registro")
            }
    }
}