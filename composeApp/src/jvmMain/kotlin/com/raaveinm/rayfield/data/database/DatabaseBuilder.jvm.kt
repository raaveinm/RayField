package com.raaveinm.rayfield.data.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val userHome = System.getProperty("user.home")
    val dbDir = File(userHome, ".rayfield")
    if (!dbDir.exists()) {
        dbDir.mkdirs()
    }
    val dbFile = File(dbDir, "rayfield.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
}
