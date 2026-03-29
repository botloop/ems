package com.ems.domain.repository

import com.ems.domain.model.VitalSigns
import kotlinx.coroutines.flow.Flow

interface VitalSignsRepository {
    fun getVitalSignsForPcr(pcrId: String): Flow<List<VitalSigns>>
    suspend fun saveVitalSigns(vitals: VitalSigns)
    suspend fun deleteVitalSigns(vitalsId: String)
    suspend fun deleteAllVitalsForPcr(pcrId: String)
}
