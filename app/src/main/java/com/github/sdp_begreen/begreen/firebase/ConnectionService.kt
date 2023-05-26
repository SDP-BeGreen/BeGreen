package com.github.sdp_begreen.begreen.firebase

import kotlinx.coroutines.flow.Flow

interface ConnectionService {

    /**
     * Function to get a snapshot of the current connection status
     *
     * @return true if connected false otherwise
     */
    suspend fun getConnectionStatus(): Boolean

    /**
     * Function to get a flow of the current connection status
     *
     * @return flow of boolean, true for connected, false otherwise
     */
    suspend fun getFlowConnectionStatus(): Flow<Boolean>
}