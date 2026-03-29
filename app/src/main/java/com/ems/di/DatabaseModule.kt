package com.ems.di

import android.content.Context
import androidx.room.Room
import com.ems.data.local.EmsDatabase
import com.ems.data.local.dao.PcrDao
import com.ems.data.local.dao.VitalSignsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEmsDatabase(@ApplicationContext context: Context): EmsDatabase =
        Room.databaseBuilder(context, EmsDatabase::class.java, EmsDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePcrDao(db: EmsDatabase): PcrDao = db.pcrDao()

    @Provides
    fun provideVitalSignsDao(db: EmsDatabase): VitalSignsDao = db.vitalSignsDao()
}
