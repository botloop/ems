package com.ems.data.local.dao

import androidx.room.*
import com.ems.data.local.entity.VitalSignsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalSignsDao {
    @Query("SELECT * FROM vital_signs WHERE pcrId = :pcrId ORDER BY timestamp ASC")
    fun getVitalSignsForPcr(pcrId: String): Flow<List<VitalSignsEntity>>

    @Query("SELECT * FROM vital_signs WHERE id = :id")
    suspend fun getVitalSignsById(id: String): VitalSignsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitalSigns(vitals: VitalSignsEntity)

    @Update
    suspend fun updateVitalSigns(vitals: VitalSignsEntity)

    @Delete
    suspend fun deleteVitalSigns(vitals: VitalSignsEntity)

    @Query("DELETE FROM vital_signs WHERE pcrId = :pcrId")
    suspend fun deleteAllVitalsForPcr(pcrId: String)
}
