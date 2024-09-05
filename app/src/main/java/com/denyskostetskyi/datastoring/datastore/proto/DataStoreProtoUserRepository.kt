package com.denyskostetskyi.datastoring.datastore.proto

import androidx.datastore.core.DataStore
import com.denyskostetskyi.datastoring.model.User
import com.denyskostetskyi.datastoring.proto.UserProto
import kotlinx.coroutines.flow.first

class DataStoreProtoUserRepository(private val dataStore: DataStore<UserProto>) {
    suspend fun saveUser(user: User) = dataStore.updateData { currentUser ->
        currentUser.toBuilder()
            .setId(user.id)
            .setFirstName(user.firstName)
            .setLastName(user.lastName)
            .build()
    }

    suspend fun getUser(): User {
        val userProto = dataStore.data.first()
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

    suspend fun deleteUser() = dataStore.updateData {
        it.toBuilder().clear().build()
    }
}
