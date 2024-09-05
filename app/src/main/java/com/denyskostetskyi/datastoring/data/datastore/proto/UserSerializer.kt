package com.denyskostetskyi.datastoring.data.datastore.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.denyskostetskyi.datastoring.proto.UserProto
import java.io.InputStream
import java.io.OutputStream

object UserSerializer : Serializer<UserProto> {
    override val defaultValue: UserProto = UserProto.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): UserProto {
        try {
            return UserProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserProto, output: OutputStream) = t.writeTo(output)
}