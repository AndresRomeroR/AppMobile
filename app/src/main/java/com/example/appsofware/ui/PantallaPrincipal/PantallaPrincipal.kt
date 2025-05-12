// app/src/main/java/com/example/appsofware/ui/PantallaPrincipal.kt
package com.example.appsofware.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.appsofware.data.Habito
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    habitos: SnapshotStateList<Habito>,
    onAgregarClick: () -> Unit,
    onToggleCompletado: (Habito) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Hábitos") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClick) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            items(habitos, key = { it.id }) { habito ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = habito.nombre,
                            textDecoration =
                                if (habito.completado) TextDecoration.LineThrough else null
                        )
                        Checkbox(
                            checked = habito.completado,
                            onCheckedChange = {
                                scope.launch {
                                    val idx = habitos.indexOfFirst { it.id == habito.id }
                                    if (idx != -1) {
                                        val actualizado =
                                            habitos[idx].copy(completado = !habito.completado)
                                        habitos[idx] = actualizado
                                        onToggleCompletado(actualizado)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
