package com.denyskostetskyi.datastoring

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.denyskostetskyi.datastoring.model.User
import com.denyskostetskyi.datastoring.preferences.SharedPreferencesUserRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        testRepositories()
    }

    private fun testRepositories() {
        val initialUser = User(id = 1, firstName = "Denys", lastName = "Kostetskyi")
        val updatedUser = User(id = 1, firstName = "Updated", lastName = "User")
        testSharedPreferencesRepository(initialUser, updatedUser)
    }

    private fun testSharedPreferencesRepository(initialUser: User, updatedUser: User) {
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val repository = SharedPreferencesUserRepository(preferences)
        repository.saveUser(initialUser)
        Log.d(TAG_SHARED_PREFERENCES, "Saved user: ${repository.getUser()}")
        repository.updateUser(updatedUser)
        Log.d(TAG_SHARED_PREFERENCES, "Updated user: ${repository.getUser()}")
        repository.deleteUser()
        Log.d(TAG_SHARED_PREFERENCES, "User deleted: ${repository.getUser() == User.DEFAULT}")
    }

    companion object {
        private const val TAG_SHARED_PREFERENCES = "SharedPreferences"
    }
}