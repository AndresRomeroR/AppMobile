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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private enum class Destino { HABITOS, HISTORIAL, PERFIL }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppSofwareTheme {

                /* ───── Colecciones por usuario ───── */
                val allowedUsers  = remember { mutableStateListOf("admin") }
                val userHabitos   = remember {
                    mutableStateMapOf(
                        "admin" to mutableStateListOf(
                            Habito(1, "Leer 10 minutos"),
                            Habito(2, "Ejercicio diario"),
                            Habito(3, "Tomar agua")
                        )
                    )
                }
                val userHistorial = remember { mutableStateMapOf<String, SnapshotStateList<HistorialEntry>>() }

                /* ─────  PERFIL (nombre, foto, etc.)  ───── */
                val userProfiles  = remember {
                    mutableStateMapOf(
                        "admin" to Usuario(
                            nombre   = "admin",
                            imageUrl = "https://randomuser.me/api/portraits/women/2.jpg"
                        )
                    )
                }

                /* ───── Estado global de sesión ───── */
                var estaLogueado by rememberSaveable { mutableStateOf(false) }
                var currentUser  by rememberSaveable { mutableStateOf("") }
                var destino      by rememberSaveable { mutableStateOf(Destino.HABITOS) }

                /* ───── Auxiliares UI ───── */
                val drawerState  = rememberDrawerState(DrawerValue.Closed)
                val scope        = rememberCoroutineScope()
                val usuarioState = remember { mutableStateOf(Usuario()) }

                /* Referencias a listas del usuario activo */
                var habitos   by remember { mutableStateOf(mutableStateListOf<Habito>()) }
                var historial by remember { mutableStateOf(mutableStateListOf<HistorialEntry>()) }

                /* Dialogo hábito */
                var showAddHabitDialog by remember { mutableStateOf(false) }
                var newHabitName       by remember { mutableStateOf("") }

                fun agregarAHistorial(h: Habito) {
                    if (h.completado) {
                        historial.add(
                            HistorialEntry(id = historial.size + 1, nombre = h.nombre)
                        )
                    }
                }

                /* ════════════════════════════════════ */
                if (estaLogueado) {

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 24.dp)
                                ) {
                                    /* ─── Parte superior ─── */
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
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
                                        Text(
                                            usuarioState.value.nombre.ifBlank { "Invitado" },
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(Modifier.height(24.dp))

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

                                    Spacer(modifier = Modifier.weight(1f))

                                    Divider()
                                    NavigationDrawerItem(
                                        label = { Text("Cerrar sesión") },
                                        selected = false,
                                        onClick = {
                                            scope.launch { drawerState.close() }
                                            estaLogueado = false
                                            currentUser  = ""
                                            destino      = Destino.HABITOS
                                        },
                                        icon = { Icon(Icons.Filled.Logout, null) }
                                    )
                                }
                            }
                        }
                    ) {
                        when (destino) {
                            Destino.HABITOS -> {
                                if (showAddHabitDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showAddHabitDialog = false },
                                        title = { Text("Nuevo Hábito") },
                                        text  = {
                                            TextField(
                                                value = newHabitName,
                                                onValueChange = { newHabitName = it },
                                                label = { Text("Nombre del hábito") }
                                            )
                                        },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                if (newHabitName.isNotBlank()) {
                                                    habitos.add(
                                                        Habito(
                                                            id     = habitos.size + 1,
                                                            nombre = newHabitName.trim()
                                                        )
                                                    )
                                                    newHabitName      = ""
                                                    showAddHabitDialog = false
                                                }
                                            }) { Text("Agregar") }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = {
                                                newHabitName = ""
                                                showAddHabitDialog = false
                                            }) { Text("Cancelar") }
                                        }
                                    )
                                }

                                PantallaPrincipal(
                                    habitos            = habitos,
                                    onAgregarClick     = { showAddHabitDialog = true },
                                    onToggleCompletado = { agregarAHistorial(it) },
                                    onOpenDrawer       = { scope.launch { drawerState.open() } }
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
                    /* ═════════════════ Login ═════════════════ */
                    var usuario by remember { mutableStateOf("") }
                    var error   by remember { mutableStateOf(false) }

                    var showRegisterDialog by remember { mutableStateOf(false) }
                    var newUserName       by remember { mutableStateOf("") }
                    var userExists        by remember { mutableStateOf(false) }
                    var genderSelected    by remember { mutableStateOf("women") } // women / men

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
                                modifier = Modifier.sizeIn(maxWidth = 200.dp, maxHeight = 200.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(24.dp))

                            OutlinedTextField(
                                value = usuario,
                                onValueChange = { usuario = it; error = false },
                                placeholder  = { Text("Usuario") },
                                singleLine   = true,
                                isError      = error,
                                modifier     = Modifier.fillMaxWidth()
                            )
                            if (error) {
                                Spacer(Modifier.height(8.dp))
                                Text("Usuario no registrado", color = MaterialTheme.colorScheme.error)
                            }

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick  = {
                                    if (allowedUsers.contains(usuario)) {
                                        /* ── Carga listas ── */
                                        currentUser = usuario
                                        if (!userHabitos.containsKey(usuario))
                                            userHabitos[usuario] = mutableStateListOf()
                                        if (!userHistorial.containsKey(usuario))
                                            userHistorial[usuario] = mutableStateListOf()

                                        habitos   = userHabitos[usuario]!!
                                        historial = userHistorial[usuario]!!

                                        /* ── Carga perfil almacenado ── */
                                        usuarioState.value =
                                            userProfiles[usuario] ?: Usuario(nombre = usuario)

                                        estaLogueado = true
                                        destino      = Destino.HABITOS
                                    } else error = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) { Text("Ingresar") }

                            Spacer(Modifier.height(12.dp))

                            OutlinedButton(
                                onClick  = {
                                    genderSelected   = "women"
                                    showRegisterDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Crear usuario") }
                        }

                        /* ═════════════ Registro ═════════════ */
                        if (showRegisterDialog) {
                            AlertDialog(
                                onDismissRequest = {
                                    showRegisterDialog = false
                                    newUserName = ""
                                    userExists  = false
                                },
                                title = { Text("Nuevo usuario") },
                                text = {
                                    Column {
                                        OutlinedTextField(
                                            value        = newUserName,
                                            onValueChange = { newUserName = it; userExists = false },
                                            label        = { Text("Nombre de usuario") },
                                            singleLine   = true,
                                            isError      = userExists
                                        )
                                        Spacer(Modifier.height(12.dp))

                                        /* ─── Género ─── */
                                        Text("Género")
                                        Spacer(Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = genderSelected == "women",
                                                onClick  = { genderSelected = "women" }
                                            )
                                            Text("Mujer")
                                            Spacer(Modifier.width(16.dp))
                                            RadioButton(
                                                selected = genderSelected == "men",
                                                onClick  = { genderSelected = "men" }
                                            )
                                            Text("Hombre")
                                        }

                                        if (userExists) {
                                            Spacer(Modifier.height(4.dp))
                                            Text("El usuario ya existe", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val name = newUserName.trim()
                                        if (name.isNotBlank()) {
                                            if (allowedUsers.contains(name)) {
                                                userExists = true
                                            } else {
                                                /* ── Crea foto aleatoria ── */
                                                val randomId = Random.nextInt(100)
                                                val imageUrl = "https://randomuser.me/api/portraits/$genderSelected/$randomId.jpg"

                                                /* ── Actualiza colecciones ── */
                                                allowedUsers.add(name)
                                                userHabitos[name]   = mutableStateListOf()
                                                userHistorial[name] = mutableStateListOf()
                                                userProfiles[name]  = Usuario(nombre = name, imageUrl = imageUrl)

                                                /* ── Prepara sesión ── */
                                                currentUser        = name
                                                habitos            = userHabitos[name]!!
                                                historial          = userHistorial[name]!!
                                                usuarioState.value = userProfiles[name]!!

                                                estaLogueado       = true
                                                destino            = Destino.PERFIL
                                                showRegisterDialog = false
                                            }
                                        }
                                    }) { Text("Guardar") }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        showRegisterDialog = false
                                        newUserName = ""
                                        userExists  = false
                                    }) { Text("Cancelar") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
