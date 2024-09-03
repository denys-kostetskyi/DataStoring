package com.denyskostetskyi.datastoring

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.denyskostetskyi.datastoring.databinding.ActivityMainBinding
import com.denyskostetskyi.datastoring.model.User
import com.denyskostetskyi.datastoring.preferences.SharedPreferencesUserRepository
import com.denyskostetskyi.datastoring.sqlite.SQLiteUserRepository
import com.denyskostetskyi.datastoring.storage.InternalStorageUserRepository

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("ActivityMainBinding is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        binding.buttonTest.setOnClickListener { testRepositories() }
    }

    private fun testRepositories() {
        val initialUser = User(id = 1, firstName = "Denys", lastName = "Kostetskyi")
        val updatedUser = User(id = 1, firstName = "Updated", lastName = "User")
        testSharedPreferencesRepository(initialUser, updatedUser)
        testInternalStorageRepository(initialUser, updatedUser)
        testSQLiteRepository(initialUser, updatedUser)
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

    private fun testInternalStorageRepository(initialUser: User, updatedUser: User) {
        val handlerThread = HandlerThread(INTERNAL_STORAGE_THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            val repository = InternalStorageUserRepository(applicationContext)
            repository.saveUser(initialUser)
            Log.d(TAG_INTERNAL_STORAGE, "Saved user: ${repository.getUser()}")
            repository.updateUser(updatedUser)
            Log.d(TAG_INTERNAL_STORAGE, "Updated user: ${repository.getUser()}")
            repository.deleteUser()
            Log.d(TAG_INTERNAL_STORAGE, "User deleted: ${repository.getUser() == User.DEFAULT}")
            handlerThread.quitSafely()
        }
    }

    private fun testSQLiteRepository(initialUser: User, updatedUser: User) {
        val handlerThread = HandlerThread(INTERNAL_STORAGE_THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            val repository = SQLiteUserRepository(applicationContext)
            repository.saveUser(initialUser)
            Log.d(SQLITE_DATABASE_THREAD_NAME, "Saved user: ${repository.getUser(initialUser.id)}")
            repository.updateUser(updatedUser)
            Log.d(SQLITE_DATABASE_THREAD_NAME, "Updated user: ${repository.getUser(updatedUser.id)}")
            val deleteResult = repository.deleteUser(updatedUser.id) > 0
            Log.d(SQLITE_DATABASE_THREAD_NAME, "User deleted: $deleteResult")
            repository.closeConnection()
            handlerThread.quitSafely()
        }
    }

    companion object {
        private const val TAG_SHARED_PREFERENCES = "SharedPreferences"
        private const val TAG_INTERNAL_STORAGE = "InternalStorage"
        private const val INTERNAL_STORAGE_THREAD_NAME = "InternalStorageTestThread"
        private const val SQLITE_DATABASE_THREAD_NAME = "SQLiteDatabaseTestThread"
    }
}
