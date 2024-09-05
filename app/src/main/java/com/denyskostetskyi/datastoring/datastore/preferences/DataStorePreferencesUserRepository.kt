package com.denyskostetskyi.datastoring.datastore.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.denyskostetskyi.datastoring.domain.model.User
import kotlinx.coroutines.flow.first

class DataStorePreferencesUserRepository(private val dataStore: DataStore<Preferences>) {
    suspend fun saveUser(user: User) = dataStore.edit { preferences ->
        preferences[KEY_ID] = user.id
        preferences[KEY_FIRST_NAME] = user.firstName
        preferences[KEY_LAST_NAME] = user.lastName
    }

    suspend fun getUser(): User {
        val preferences = dataStore.data.first()
        val id = preferences[KEY_ID] ?: return User.DEFAULT
        val firstName = preferences[KEY_FIRST_NAME] ?: return User.DEFAULT
        val lastName = preferences[KEY_LAST_NAME] ?: return User.DEFAULT
        return User(id, firstName, lastName)
    }

    suspend fun updateUser(user: User) = saveUser(user)

    suspend fun deleteUser() = dataStore.edit { it.clear() }

    companion object {
        private val KEY_ID = intPreferencesKey("id")
        private val KEY_FIRST_NAME = stringPreferencesKey("first_name")
        private val KEY_LAST_NAME = stringPreferencesKey("last_name")
    }
}
