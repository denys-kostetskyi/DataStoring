package com.denyskostetskyi.datastoring.sqlite

import android.provider.BaseColumns

object UserContract {
    const val DATABASE_NAME = "user.db"
    const val DATABASE_VERSION = 1

    object UserEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"

        const val SQL_CREATE_ENTRIES = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_FIRST_NAME TEXT,
                $COLUMN_LAST_NAME TEXT
            )
        """
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}