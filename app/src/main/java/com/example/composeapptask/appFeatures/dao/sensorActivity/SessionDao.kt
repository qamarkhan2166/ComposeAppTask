package com.example.composeapptask.appFeatures.dao.sensorActivity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSession(session: Session): Long

    @Update
    suspend fun updateSession(session: Session)

    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId")
    fun getSessionById(sessionId: Long): Flow<Session?>

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Transaction
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getSessionsWithDetails(): Flow<List<SessionWithDetails>>
}

@Dao
interface ActivityTransitionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertActivityTransition(transition: ActivityTransition)

    @Query("SELECT * FROM activity_transitions WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getActivityTransitionsForSession(sessionId: Long): Flow<List<ActivityTransition>>
}

@Dao
interface SensorReadingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSensorReading(reading: SensorReading)

    @Query("SELECT * FROM sensor_readings WHERE sessionId = :sessionId AND sensorType = :sensorType ORDER BY timestamp ASC")
    fun getSensorReadingsForSession(sessionId: Long, sensorType: String): Flow<List<SensorReading>>
}

@Dao
interface LocationPointDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocationPoint(point: LocationPoint)

    @Query("SELECT * FROM location_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getLocationPointsForSession(sessionId: Long): Flow<List<LocationPoint>>

    // todo qamar.k check later query to get location points for distance calculation
    // @Query("SELECT latitude, longitude FROM location_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    // suspend fun getLocationCoordinatesForSession(sessionId: Long): List<Pair<Double, Double>>
}