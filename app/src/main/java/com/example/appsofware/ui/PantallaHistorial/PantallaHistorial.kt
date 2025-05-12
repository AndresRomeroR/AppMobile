// app/src/main/java/com/example/appsofware/ui/PantallaHistorial.kt
package com.example.appsofware.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appsofware.data.HistorialEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    historial: SnapshotStateList<HistorialEntry>,
    onOpenDrawer: () -> Unit
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(historial, key = { it.id }) { entrada ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(entrada.nombre)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatter.format(Date(entrada.timestamp)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
