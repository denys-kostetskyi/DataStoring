package com.denyskostetskyi.datastoring.model

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
) {
    companion object {
        val DEFAULT = User(
            id = -1,
            firstName = "John",
            lastName = "Doe"
        )
    }
}
