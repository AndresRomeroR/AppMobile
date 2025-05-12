// app/src/main/java/com/example/appsofware/MainActivity.kt
package com.example.appsofware

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appsofware.data.*
import com.example.appsofware.ui.*
import com.example.appsofware.ui.theme.AppSofwareTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private enum class Destino { HABITOS, HISTORIAL, PERFIL }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppSofwareTheme {

                var estaLogueado by remember { mutableStateOf(false) }

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope       = rememberCoroutineScope()

                var destino by remember { mutableStateOf(Destino.HABITOS) }

                val usuarioState: MutableState<Usuario> = remember { mutableStateOf(Usuario()) }

                val habitos: SnapshotStateList<Habito> = remember {
                    mutableStateListOf(
                        Habito(1, "Leer 10 minutos"),
                        Habito(2, "Ejercicio diario"),
                        Habito(3, "Tomar agua")
                    )
                }
                val historial = remember { mutableStateListOf<HistorialEntry>() }

                var mostrarDialogo by remember { mutableStateOf(false) }
                var nuevoNombre   by remember { mutableStateOf("") }

                fun agregarAHistorial(h: Habito) {
                    if (h.completado) {
                        historial.add(
                            HistorialEntry(
                                id   = historial.size + 1,
                                nombre = h.nombre
                            )
                        )
                    }
                }

                if (estaLogueado) {

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp)
                                ) {
                                    AsyncImage(
                                        model = usuarioState.value.imageUrl,
                                        placeholder = painterResource(R.drawable.ic_person),
                                        error       = painterResource(R.drawable.ic_person),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(96.dp)
                                            .clip(RectangleShape)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(usuarioState.value.nombre, style = MaterialTheme.typography.titleMedium)
                                }
                                NavigationDrawerItem(
                                    label = { Text("Hábitos") },
                                    selected = destino == Destino.HABITOS,
                                    onClick = {
                                        destino = Destino.HABITOS
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = { Icon(Icons.Filled.List, null) }
                                )
                                NavigationDrawerItem(
                                    label = { Text("Historial") },
                                    selected = destino == Destino.HISTORIAL,
                                    onClick = {
                                        destino = Destino.HISTORIAL
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = { Icon(Icons.Filled.History, null) }
                                )
                                NavigationDrawerItem(
                                    label = { Text("Perfil") },
                                    selected = destino == Destino.PERFIL,
                                    onClick = {
                                        destino = Destino.PERFIL
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = { Icon(Icons.Filled.Person, null) }
                                )
                            }
                        }
                    ) {
                        when (destino) {
                            Destino.HABITOS -> {
                                if (mostrarDialogo) {
                                    AlertDialog(
                                        onDismissRequest = { mostrarDialogo = false },
                                        title = { Text("Nuevo Hábito") },
                                        text = {
                                            TextField(
                                                value = nuevoNombre,
                                                onValueChange = { nuevoNombre = it },
                                                label = { Text("Nombre del hábito") }
                                            )
                                        },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                if (nuevoNombre.isNotBlank()) {
                                                    habitos.add(
                                                        Habito(
                                                            id     = habitos.size + 1,
                                                            nombre = nuevoNombre.trim()
                                                        )
                                                    )
                                                    nuevoNombre   = ""
                                                    mostrarDialogo = false
                                                }
                                            }) { Text("Agregar") }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = {
                                                nuevoNombre   = ""
                                                mostrarDialogo = false
                                            }) { Text("Cancelar") }
                                        }
                                    )
                                }

                                PantallaPrincipal(
                                    habitos           = habitos,
                                    onAgregarClick    = { mostrarDialogo = true },
                                    onToggleCompletado = { agregarAHistorial(it) },
                                    onOpenDrawer      = { scope.launch { drawerState.open() } }
                                )
                            }

                            Destino.HISTORIAL -> PantallaHistorial(
                                historial    = historial,
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )

                            Destino.PERFIL -> PantallaPerfil(
                                usuarioState = usuarioState,
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                    }

                } else {
                    var usuario by remember { mutableStateOf("") }  // <--- estado para el input

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .sizeIn(maxWidth = 200.dp, maxHeight = 200.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Iniciar sesión",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(24.dp))
                            TextField(
                                value = usuario,
                                onValueChange = { usuario = it },  // <--- actualiza el estado
                                placeholder = { Text("Usuario") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = { if (usuario.isNotBlank()) estaLogueado = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ingresar")
                            }
                        }
                    }
                }
            }
        }
    }
}
