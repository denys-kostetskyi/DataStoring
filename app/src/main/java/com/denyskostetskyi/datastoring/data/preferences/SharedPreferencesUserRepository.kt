package com.denyskostetskyi.datastoring.data.preferences

import android.content.SharedPreferences
import com.denyskostetskyi.datastoring.domain.model.User
import com.denyskostetskyi.datastoring.domain.repository.UserRepository

class SharedPreferencesUserRepository(private val preferences: SharedPreferences) : UserRepository {
    override suspend fun saveUser(user: User) {
        preferences.edit()
            .putInt(KEY_ID, user.id)
            .putString(KEY_FIRST_NAME, user.firstName)
            .putString(KEY_LAST_NAME, user.lastName)
            .apply()
    }

    override suspend fun getUser(id: Int) = with(preferences) {
        val persistedId = getInt(KEY_ID, User.DEFAULT.id)
        if (persistedId == User.DEFAULT.id) return User.DEFAULT
        val firstName = getString(KEY_FIRST_NAME, null) ?: return User.DEFAULT
        val lastName = getString(KEY_LAST_NAME, null) ?: return User.DEFAULT
        User(persistedId, firstName, lastName)
    }

    override suspend fun updateUser(user: User): Boolean {
        saveUser(user)
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        preferences.edit().clear().apply()
        return true
    }

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
    }
}
