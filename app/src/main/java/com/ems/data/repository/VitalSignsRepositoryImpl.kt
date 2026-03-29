package com.ems.data.repository

import com.ems.data.local.dao.VitalSignsDao
import com.ems.data.local.entity.toDomain
import com.ems.data.local.entity.toEntity
import com.ems.domain.model.VitalSigns
import com.ems.domain.repository.VitalSignsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitalSignsRepositoryImpl @Inject constructor(
    private val vitalSignsDao: VitalSignsDao
) : VitalSignsRepository {

    override fun getVitalSignsForPcr(pcrId: String): Flow<List<VitalSigns>> =
        vitalSignsDao.getVitalSignsForPcr(pcrId).map { it.map { e -> e.toDomain() } }

    override suspend fun saveVitalSigns(vitals: VitalSigns) {
        vitalSignsDao.insertVitalSigns(vitals.toEntity())
    }

    override suspend fun deleteVitalSigns(vitalsId: String) {
        vitalSignsDao.getVitalSignsById(vitalsId)?.let { vitalSignsDao.deleteVitalSigns(it) }
    }

    override suspend fun deleteAllVitalsForPcr(pcrId: String) {
        vitalSignsDao.deleteAllVitalsForPcr(pcrId)
    }
}
