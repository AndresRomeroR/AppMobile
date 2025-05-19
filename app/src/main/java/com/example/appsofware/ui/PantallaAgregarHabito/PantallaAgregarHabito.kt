// app/src/main/java/com/example/appsofware/ui/PantallaAgregarHabito.kt
package com.example.appsofware.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun PantallaAgregarHabito(onLoginExitoso: () -> Unit) {
    var usuario by remember { mutableStateOf(TextFieldValue("")) }
    var error   by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = usuario,
            onValueChange = {
                usuario = it
                error = false
            },
            label = { Text("Usuario") },
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (usuario.text == "admin") onLoginExitoso() else error = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ingresar") }
        if (error) {
            Spacer(Modifier.height(8.dp))
            Text("Usuario incorrecto", color = MaterialTheme.colorScheme.error)
        }
    }
}
