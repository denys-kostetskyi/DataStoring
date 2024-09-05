package com.denyskostetskyi.datastoring.data.datastore.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.denyskostetskyi.datastoring.domain.model.User
import com.denyskostetskyi.datastoring.domain.repository.UserRepository
import kotlinx.coroutines.flow.first

class DataStorePreferencesUserRepository(private val dataStore: DataStore<Preferences>) :
    UserRepository {
    override suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[KEY_ID] = user.id
            preferences[KEY_FIRST_NAME] = user.firstName
            preferences[KEY_LAST_NAME] = user.lastName
        }
    }

    override suspend fun getUser(id: Int): User {
        val preferences = dataStore.data.first()
        val persistedId = preferences[KEY_ID] ?: return User.DEFAULT
        val firstName = preferences[KEY_FIRST_NAME] ?: return User.DEFAULT
        val lastName = preferences[KEY_LAST_NAME] ?: return User.DEFAULT
        return User(persistedId, firstName, lastName)
    }

    override suspend fun updateUser(user: User): Boolean {
        saveUser(user)
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        dataStore.edit { it.clear() }
        return true
    }

    companion object {
        private val KEY_ID = intPreferencesKey("id")
        private val KEY_FIRST_NAME = stringPreferencesKey("first_name")
        private val KEY_LAST_NAME = stringPreferencesKey("last_name")
    }
}
