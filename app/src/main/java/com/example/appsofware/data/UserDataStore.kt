// app/src/main/java/com/example/appsofware/data/UserDataStore.kt
package com.example.appsofware.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore by preferencesDataStore("user_prefs")

object PrefKeys {
    val NAME  = stringPreferencesKey("name")
    val EMAIL = stringPreferencesKey("email")
    val PHONE = stringPreferencesKey("phone")
    val AGE   = stringPreferencesKey("age")
    val YEAR  = stringPreferencesKey("year")
}

class UserDataStore(private val context: Context) {

    val userFlow: Flow<Usuario> = context.userDataStore.data.map { prefs ->
        Usuario(
            nombre          = prefs[PrefKeys.NAME]  ?: "Laura",
            email           = prefs[PrefKeys.EMAIL] ?: "",
            telefono        = prefs[PrefKeys.PHONE] ?: "",
            edad            = prefs[PrefKeys.AGE]   ?: "",
            anioVinculacion = prefs[PrefKeys.YEAR]  ?: "",
            imageUrl        = "https://randomuser.me/api/portraits/women/2.jpg"
        )
    }

    suspend fun save(usuario: Usuario) {
        context.userDataStore.edit { prefs ->
            prefs[PrefKeys.NAME]  = usuario.nombre
            prefs[PrefKeys.EMAIL] = usuario.email
            prefs[PrefKeys.PHONE] = usuario.telefono
            prefs[PrefKeys.AGE]   = usuario.edad
            prefs[PrefKeys.YEAR]  = usuario.anioVinculacion
        }
    }
}
