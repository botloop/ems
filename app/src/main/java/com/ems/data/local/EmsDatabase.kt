package com.ems.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ems.data.local.dao.PcrDao
import com.ems.data.local.dao.VitalSignsDao
import com.ems.data.local.entity.PcrEntity
import com.ems.data.local.entity.VitalSignsEntity

@Database(
    entities = [PcrEntity::class, VitalSignsEntity::class],
    version = 1,
    exportSchema = true
)
abstract class EmsDatabase : RoomDatabase() {
    abstract fun pcrDao(): PcrDao
    abstract fun vitalSignsDao(): VitalSignsDao

    companion object {
        const val DATABASE_NAME = "ems_database"
    }
}
