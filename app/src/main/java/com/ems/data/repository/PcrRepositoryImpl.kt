package com.ems.data.repository

import com.ems.data.local.dao.PcrDao
import com.ems.data.local.entity.toDomain
import com.ems.data.local.entity.toEntity
import com.ems.domain.model.Pcr
import com.ems.domain.repository.PcrRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PcrRepositoryImpl @Inject constructor(
    private val pcrDao: PcrDao,
    private val firestore: FirebaseFirestore
) : PcrRepository {

    override fun getAllPcrs(): Flow<List<Pcr>> =
        pcrDao.getAllPcrs().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getPcrById(id: String): Pcr? =
        pcrDao.getPcrById(id)?.toDomain()

    override suspend fun savePcr(pcr: Pcr) {
        pcrDao.insertPcr(pcr.toEntity())
    }

    override suspend fun deletePcr(id: String) {
        pcrDao.deletePcrById(id)
    }

    override fun getPcrCount(): Flow<Int> = pcrDao.getPcrCount()

    override suspend fun syncToFirestore() {
        val unsynced = pcrDao.getUnsyncedPcrs()
        unsynced.forEach { entity ->
            try {
                firestore.collection("pcrs")
                    .document(entity.id)
                    .set(entity)
                    .await()
                pcrDao.insertPcr(entity.copy(syncedAt = Instant.now().toEpochMilli()))
            } catch (_: Exception) {
                // Will retry on next sync attempt
            }
        }
    }
}
