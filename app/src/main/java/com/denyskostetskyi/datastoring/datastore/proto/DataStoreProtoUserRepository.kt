package com.denyskostetskyi.datastoring.datastore.proto

import android.content.Context
import androidx.datastore.dataStore
import com.denyskostetskyi.datastoring.model.User
import com.denyskostetskyi.datastoring.proto.UserProto
import kotlinx.coroutines.flow.first

class DataStoreProtoUserRepository(private val applicationContext: Context) {
    private val Context.dataStore by dataStore(fileName = FILE_NAME, serializer = UserSerializer)

    suspend fun saveUser(user: User) {
        applicationContext.dataStore.updateData { currentUser ->
            currentUser.toBuilder()
                .setId(user.id)
                .setFirstName(user.firstName)
                .setLastName(user.lastName)
                .build()
        }
    }

    suspend fun getUser(): User {
        val userProto = applicationContext.dataStore.data.first()
        return if (userProto == UserProto.getDefaultInstance()) {
            User.DEFAULT
        } else {
            User(
                id = userProto.id,
                firstName = userProto.firstName,
                lastName = userProto.lastName
            )
        }
    }

    suspend fun updateUser(user: User) = saveUser(user)

    suspend fun deleteUser() {
        applicationContext.dataStore.updateData {
            it.toBuilder().clear().build()
        }
    }

    companion object {
        private const val FILE_NAME = "user_proto"
    }
}