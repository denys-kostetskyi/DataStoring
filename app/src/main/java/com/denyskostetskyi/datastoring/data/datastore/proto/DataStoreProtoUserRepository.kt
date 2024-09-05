package com.denyskostetskyi.datastoring.data.datastore.proto

import androidx.datastore.core.DataStore
import com.denyskostetskyi.datastoring.domain.model.User
import com.denyskostetskyi.datastoring.domain.repository.UserRepository
import com.denyskostetskyi.datastoring.proto.UserProto
import kotlinx.coroutines.flow.first

class DataStoreProtoUserRepository(private val dataStore: DataStore<UserProto>) : UserRepository {
    override suspend fun saveUser(user: User) {
        dataStore.updateData { currentUser ->
            currentUser.toBuilder()
                .setId(user.id)
                .setFirstName(user.firstName)
                .setLastName(user.lastName)
                .build()
        }
    }

    override suspend fun getUser(id: Int): User {
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

    override suspend fun updateUser(user: User): Boolean {
        saveUser(user)
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        dataStore.updateData {
            it.toBuilder().clear().build()
        }
        return true
    }
}
