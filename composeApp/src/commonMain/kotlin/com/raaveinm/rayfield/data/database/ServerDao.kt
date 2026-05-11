package com.raaveinm.rayfield.data.database

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.data.xray.types.ServerState
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    ///////////////////////////////////////////////
    // ServerUnit operations
    ///////////////////////////////////////////////
    @Query("SELECT * FROM server_units")
    fun getAllServerUnits(): Flow<List<ServerUnit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerUnit(server: ServerUnit)

    @Delete
    suspend fun deleteServerUnit(server: ServerUnit)

    @Query("SELECT * FROM server_units WHERE serverId = :id")
    suspend fun getServerUnitById(id: String): ServerUnit?

    ///////////////////////////////////////////////
    // ServerState operations
    ///////////////////////////////////////////////
    @Query("SELECT * FROM server_states")
    fun getAllServerStates(): Flow<List<ServerState>>

    @Query("SELECT * FROM server_states WHERE serverId = :serverId")
    fun getServerStatesForServer(serverId: String): Flow<List<ServerState>>

    @Query("SELECT * FROM server_states WHERE configId = :id")
    suspend fun getConfigById(id: String): ServerState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerState(state: ServerState)

    @Delete
    suspend fun deleteServerState(state: ServerState)

    @Transaction
    @Query("SELECT * FROM server_units WHERE serverId = :serverId")
    fun getServerWithStates(serverId: String): Flow<ServerWithState?>
}
