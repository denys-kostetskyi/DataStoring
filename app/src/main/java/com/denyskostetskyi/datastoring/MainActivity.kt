package com.denyskostetskyi.datastoring

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import com.denyskostetskyi.datastoring.data.datastore.preferences.DataStorePreferencesUserRepository
import com.denyskostetskyi.datastoring.data.datastore.proto.DataStoreProtoUserRepository
import com.denyskostetskyi.datastoring.data.datastore.proto.UserSerializer
import com.denyskostetskyi.datastoring.data.keystore.KeyStoreRepository
import com.denyskostetskyi.datastoring.data.preferences.SharedPreferencesUserRepository
import com.denyskostetskyi.datastoring.data.room.RoomUserRepository
import com.denyskostetskyi.datastoring.data.room.UserDatabase
import com.denyskostetskyi.datastoring.data.room.UserMapper
import com.denyskostetskyi.datastoring.data.sqlite.SQLiteUserRepository
import com.denyskostetskyi.datastoring.data.storage.InternalStorageUserRepository
import com.denyskostetskyi.datastoring.databinding.ActivityMainBinding
import com.denyskostetskyi.datastoring.domain.model.User
import com.denyskostetskyi.datastoring.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("ActivityMainBinding is null")

    private val Context.dataStorePreferences by preferencesDataStore(name = DATASTORE_PREFS_NAME)
    private val Context.dataStore by dataStore(
        fileName = DATASTORE_PROTO_NAME,
        serializer = UserSerializer
    )

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
        binding.buttonTest.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                testRepositories()
            }
        }
    }

    private suspend fun testRepositories() {
        val initialUser = User(id = 1, firstName = "Denys", lastName = "Kostetskyi")
        val updatedUser = initialUser.copy(firstName = "Updated", lastName = "User")
        testSharedPreferencesRepository(initialUser, updatedUser)
        testInternalStorageRepository(initialUser, updatedUser)
        testSQLiteRepository(initialUser, updatedUser)
        testRoomRepository(initialUser, updatedUser)
        testDataStorePreferencesRepository(initialUser, updatedUser)
        testDataStoreProtoRepository(initialUser, updatedUser)
        testKeyStoreRepository()
    }

    private suspend fun testUserRepository(
        repository: UserRepository,
        initialUser: User,
        updatedUser: User,
        logTag: String
    ) {
        repository.saveUser(initialUser)
        Log.d(logTag, "Saved user: ${repository.getUser(initialUser.id)}")
        repository.updateUser(updatedUser)
        Log.d(logTag, "Updated user: ${repository.getUser(updatedUser.id)}")
        Log.d(logTag, "User deleted: ${repository.deleteUser(updatedUser.id)}")
    }

    private suspend fun testSharedPreferencesRepository(initialUser: User, updatedUser: User) {
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val repository = SharedPreferencesUserRepository(preferences)
        testUserRepository(repository, initialUser, updatedUser, TAG_SHARED_PREFERENCES)
    }

    private suspend fun testInternalStorageRepository(initialUser: User, updatedUser: User) {
        val repository = InternalStorageUserRepository(applicationContext)
        testUserRepository(repository, initialUser, updatedUser, TAG_INTERNAL_STORAGE)
    }

    private suspend fun testSQLiteRepository(initialUser: User, updatedUser: User) {
        val repository = SQLiteUserRepository(applicationContext)
        testUserRepository(repository, initialUser, updatedUser, TAG_SQLITE_DATABASE)
        repository.closeConnection()
    }

    private suspend fun testRoomRepository(initialUser: User, updatedUser: User) {
        val userDao = UserDatabase.getInstance(this).userDao()
        val repository = RoomUserRepository(userDao, UserMapper())
        testUserRepository(repository, initialUser, updatedUser, TAG_ROOM)
    }

    private suspend fun testDataStorePreferencesRepository(initialUser: User, updatedUser: User) {
        val repository = DataStorePreferencesUserRepository(dataStorePreferences)
        testUserRepository(repository, initialUser, updatedUser, TAG_DATASTORE_PREFERENCES)
    }

    private suspend fun testDataStoreProtoRepository(initialUser: User, updatedUser: User) {
        val repository = DataStoreProtoUserRepository(dataStore)
        testUserRepository(repository, initialUser, updatedUser, TAG_DATASTORE_PROTO)
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
        private const val TAG_ROOM = "Room"
        private const val TAG_DATASTORE_PREFERENCES = "DataStorePreferences"
        private const val TAG_DATASTORE_PROTO = "DataStoreProto"
        private const val TAG_KEYSTORE = "KeyStore"

        private const val DATASTORE_PREFS_NAME = "user_preferences"
        private const val DATASTORE_PROTO_NAME = "user_proto"
    }
}
