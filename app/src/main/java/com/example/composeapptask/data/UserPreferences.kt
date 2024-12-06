package com.example.composeapptask.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences>
            by preferencesDataStore(name = DATA_STORE_NAME)

    val userEmail: Flow<String?>
        get() = getData(USER_EMAIL_KEY)


    suspend fun getUserEmailSafe() = userEmail.first() ?: ""

    suspend fun saveUserEmail(userType: String) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[USER_EMAIL_KEY] = userType
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun <T> getData(key: Key<T>): Flow<T?> =
        context.dataStore.data
            .map { preferences -> preferences[key] }

    companion object {
        private const val DATA_STORE_NAME = "compose_task"
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")

    }
}