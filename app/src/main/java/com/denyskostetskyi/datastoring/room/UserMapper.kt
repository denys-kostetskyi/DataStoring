package com.denyskostetskyi.datastoring.room

import com.denyskostetskyi.datastoring.model.User

class UserMapper {
    fun mapEntityToDbModel(user: User) = UserDbModel(
        id = user.id,
        firstName = user.firstName,
        lastName = user.lastName,
    )

    fun mapDbModelToEntity(userDbModel: UserDbModel) = User(
        id = userDbModel.id,
        firstName = userDbModel.firstName,
        lastName = userDbModel.lastName,
    )

    fun mapEntityListToDbModelList(userList: List<User>) = userList.map(::mapEntityToDbModel)

    fun mapDbModelListToEntityList(userDbModelList: List<UserDbModel>) =
        userDbModelList.map(::mapDbModelToEntity)
}
