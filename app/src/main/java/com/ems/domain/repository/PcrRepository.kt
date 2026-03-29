package com.ems.domain.repository

import com.ems.domain.model.Pcr
import kotlinx.coroutines.flow.Flow

interface PcrRepository {
    fun getAllPcrs(): Flow<List<Pcr>>
    suspend fun getPcrById(id: String): Pcr?
    suspend fun savePcr(pcr: Pcr)
    suspend fun deletePcr(id: String)
    fun getPcrCount(): Flow<Int>
    suspend fun syncToFirestore()
}
