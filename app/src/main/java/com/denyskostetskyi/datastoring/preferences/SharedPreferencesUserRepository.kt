package com.denyskostetskyi.datastoring.preferences

import android.content.SharedPreferences
import com.denyskostetskyi.datastoring.domain.model.User

class SharedPreferencesUserRepository(private val preferences: SharedPreferences) {
    fun saveUser(user: User) {
        preferences.edit()
            .putInt(KEY_ID, user.id)
            .putString(KEY_FIRST_NAME, user.firstName)
            .putString(KEY_LAST_NAME, user.lastName)
            .apply()
    }

    fun getUser() = with(preferences) {
        val id = getInt(KEY_ID, User.DEFAULT.id)
        if (id == User.DEFAULT.id) return User.DEFAULT
        val firstName = getString(KEY_FIRST_NAME, null) ?: return User.DEFAULT
        val lastName = getString(KEY_LAST_NAME, null) ?: return User.DEFAULT
        User(id, firstName, lastName)
    }

    fun updateUser(user: User) = saveUser(user)

    fun deleteUser() = preferences.edit().clear().apply()

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
    }
}
