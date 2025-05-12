// app/src/main/java/com/example/appsofware/ui/PantallaPerfil.kt
package com.example.appsofware.ui

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appsofware.R
import com.example.appsofware.data.UserDataStore
import com.example.appsofware.data.Usuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    usuarioState: MutableState<Usuario>,
    onOpenDrawer: () -> Unit
) {
    val ctx       = LocalContext.current
    val dataStore = remember { UserDataStore(ctx) }
    val scope     = rememberCoroutineScope()

    var uiUser by remember { mutableStateOf(usuarioState.value) }

    /* ---------- Snackbar ---------- */
    val snackbarHost = remember { SnackbarHostState() }

    /* ---------- Inputs ---------- */
    var nombre   by remember { mutableStateOf(uiUser.nombre) }
    var email    by remember { mutableStateOf(uiUser.email) }
    var telefono by remember { mutableStateOf(uiUser.telefono) }
    var edad     by remember { mutableStateOf(uiUser.edad) }
    var anio     by remember { mutableStateOf(uiUser.anioVinculacion) }
    var pwd      by remember { mutableStateOf("") }
    var showPwd  by remember { mutableStateOf(false) }

    /* ---------- Errores ---------- */
    var emailErr by remember { mutableStateOf(false) }
    var phoneErr by remember { mutableStateOf(false) }
    var ageErr   by remember { mutableStateOf(false) }
    var yearErr  by remember { mutableStateOf(false) }

    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }          // ← muestra el pop-up
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = uiUser.imageUrl,
                placeholder = painterResource(R.drawable.ic_person),
                error       = painterResource(R.drawable.ic_person),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RectangleShape)
            )
            Spacer(Modifier.height(24.dp))

            /* ---------- Campos ---------- */
            OutlinedTextField(
                nombre, { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                email, { email = it; emailErr = false },
                label = { Text("Email") },
                isError = emailErr,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (emailErr) Text("Email inválido", color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                telefono, { telefono = it; phoneErr = false },
                label = { Text("Teléfono (10 dígitos)") },
                isError = phoneErr,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneErr) Text("Teléfono inválido", color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                edad, { edad = it; ageErr = false },
                label = { Text("Edad") },
                isError = ageErr,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (ageErr) Text("Edad inválida (1-120)", color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                anio, { anio = it; yearErr = false },
                label = { Text("Año de vinculación") },
                isError = yearErr,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (yearErr) Text("Año inválido (1900-$currentYear)", color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = pwd,
                onValueChange = { pwd = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPwd = !showPwd }) {
                        Icon(
                            imageVector = if (showPwd) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                /* ------------ Validaciones ------------ */
                emailErr = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                phoneErr = telefono.length != 10 || telefono.any { !it.isDigit() }
                ageErr   = edad.toIntOrNull()?.let { it !in 1..120 } ?: true
                yearErr  = anio.toIntOrNull()?.let { it !in 1900..currentYear } ?: true
                if (emailErr || phoneErr || ageErr || yearErr) return@Button

                /* ------------ Guardar & persistir ------------ */
                uiUser = uiUser.copy(
                    nombre          = nombre,
                    email           = email,
                    telefono        = telefono,
                    edad            = edad,
                    anioVinculacion = anio
                )
                usuarioState.value = uiUser
                scope.launch {
                    dataStore.save(uiUser)
                    snackbarHost.showSnackbar("Datos actualizados con éxito")   // ← pop-up
                }
            }) { Text("Guardar") }
        }
    }
}
