package com.ems.data.local.dao

import androidx.room.*
import com.ems.data.local.entity.PcrEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PcrDao {
    @Query("SELECT * FROM pcrs ORDER BY createdAt DESC")
    fun getAllPcrs(): Flow<List<PcrEntity>>

    @Query("SELECT * FROM pcrs WHERE id = :id")
    suspend fun getPcrById(id: String): PcrEntity?

    @Query("SELECT * FROM pcrs WHERE status = :status ORDER BY createdAt DESC")
    fun getPcrsByStatus(status: String): Flow<List<PcrEntity>>

    @Query("SELECT * FROM pcrs WHERE syncedAt IS NULL")
    suspend fun getUnsyncedPcrs(): List<PcrEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPcr(pcr: PcrEntity)

    @Update
    suspend fun updatePcr(pcr: PcrEntity)

    @Delete
    suspend fun deletePcr(pcr: PcrEntity)

    @Query("DELETE FROM pcrs WHERE id = :id")
    suspend fun deletePcrById(id: String)

    @Query("SELECT COUNT(*) FROM pcrs")
    fun getPcrCount(): Flow<Int>
}
