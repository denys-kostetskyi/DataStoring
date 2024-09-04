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
import com.denyskostetskyi.datastoring.datastore.keystore.KeyStoreRepository
import com.denyskostetskyi.datastoring.datastore.preferences.DataStorePreferencesUserRepository
import com.denyskostetskyi.datastoring.datastore.proto.DataStoreProtoUserRepository
import com.denyskostetskyi.datastoring.model.User
import com.denyskostetskyi.datastoring.preferences.SharedPreferencesUserRepository
import com.denyskostetskyi.datastoring.sqlite.SQLiteUserRepository
import com.denyskostetskyi.datastoring.storage.InternalStorageUserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        testDataStorePreferencesRepository(initialUser, updatedUser)
        testDataStoreProtoRepository(initialUser, updatedUser)
        testKeyStoreRepository()
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
        val handlerThread = HandlerThread(SQLITE_DATABASE_THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            val repository = SQLiteUserRepository(applicationContext)
            repository.saveUser(initialUser)
            Log.d(TAG_SQLITE_DATABASE, "Saved user: ${repository.getUser(initialUser.id)}")
            repository.updateUser(updatedUser)
            Log.d(TAG_SQLITE_DATABASE, "Updated user: ${repository.getUser(updatedUser.id)}")
            val deleteResult = repository.deleteUser(updatedUser.id) > 0
            Log.d(TAG_SQLITE_DATABASE, "User deleted: $deleteResult")
            repository.closeConnection()
            handlerThread.quitSafely()
        }
    }

    private fun testDataStorePreferencesRepository(initialUser: User, updatedUser: User) {
        val repository = DataStorePreferencesUserRepository(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveUser(initialUser)
            val savedUser = repository.getUser()
            Log.d(TAG_DATASTORE_PREFERENCES, "Saved user: $savedUser")
            repository.updateUser(updatedUser)
            val updatedUserResult = repository.getUser()
            Log.d(TAG_DATASTORE_PREFERENCES, "Updated user: $updatedUserResult")
            repository.deleteUser()
            val deletedUser = repository.getUser()
            Log.d(TAG_DATASTORE_PREFERENCES, "User deleted: ${deletedUser == User.DEFAULT}")
        }
    }

    private fun testDataStoreProtoRepository(initialUser: User, updatedUser: User) {
        val repository = DataStoreProtoUserRepository(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveUser(initialUser)
            val savedUser = repository.getUser()
            Log.d(TAG_DATASTORE_PROTO, "Saved user: $savedUser")
            repository.updateUser(updatedUser)
            val updatedUserResult = repository.getUser()
            Log.d(TAG_DATASTORE_PROTO, "Updated user: $updatedUserResult")
            repository.deleteUser()
            val deletedUser = repository.getUser()
            Log.d(TAG_DATASTORE_PROTO, "User deleted: ${deletedUser == User.DEFAULT}")
        }
    }

    private fun testKeyStoreRepository() {
        val alias = "testKey"
        val repository = KeyStoreRepository()
        val key = repository.createKey(alias)
        Log.d(TAG_KEYSTORE, "Key: $key")
        val updatedKey = repository.updateKey(alias)
        Log.d(TAG_KEYSTORE, "Updated key: $updatedKey")
        repository.deleteKey(alias)
        Log.d(TAG_KEYSTORE, "Key deleted: ${repository.getKey(alias) == null}")
    }

    companion object {
        private const val TAG_SHARED_PREFERENCES = "SharedPreferences"
        private const val TAG_INTERNAL_STORAGE = "InternalStorage"
        private const val TAG_SQLITE_DATABASE = "SQLiteDatabase"
        private const val TAG_DATASTORE_PREFERENCES = "DataStorePreferences"
        private const val TAG_DATASTORE_PROTO = "DataStoreProto"
        private const val TAG_KEYSTORE = "KeyStore"

        private const val INTERNAL_STORAGE_THREAD_NAME = "InternalStorageTestThread"
        private const val SQLITE_DATABASE_THREAD_NAME = "SQLiteDatabaseTestThread"
    }
}
