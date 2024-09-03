package com.denyskostetskyi.datastoring.datastore.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.denyskostetskyi.datastoring.model.User
import kotlinx.coroutines.flow.first

class DataStorePreferencesUserRepository(
    private val applicationContext: Context
) {
    private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)

    suspend fun saveUser(user: User) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_ID] = user.id
            preferences[KEY_FIRST_NAME] = user.firstName
            preferences[KEY_LAST_NAME] = user.lastName
        }
    }

    suspend fun getUser(): User {
        val preferences = applicationContext.dataStore.data.first()
        val id = preferences[KEY_ID] ?: return User.DEFAULT
        val firstName = preferences[KEY_FIRST_NAME] ?: return User.DEFAULT
        val lastName = preferences[KEY_LAST_NAME] ?: return User.DEFAULT
        return User(id, firstName, lastName)
    }

    suspend fun updateUser(user: User) = saveUser(user)

    suspend fun deleteUser() = applicationContext.dataStore.edit { it.clear() }

    companion object {
        private const val PREFERENCES_NAME = "user_datastore"
        private val KEY_ID = intPreferencesKey("id")
        private val KEY_FIRST_NAME = stringPreferencesKey("first_name")
        private val KEY_LAST_NAME = stringPreferencesKey("last_name")
    }
}
