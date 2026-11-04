package com.ip312.labs.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert
    suspend fun insert(record: GameRecord): Long

    @Query(
        """
        SELECT r.id AS id,
               u.name AS name,
               r.score AS score,
               r.difficulty AS difficulty,
               r.hits AS hits,
               r.misses AS misses,
               r.roundMinutes AS roundMinutes,
               r.playedAt AS playedAt
        FROM records AS r
        INNER JOIN users AS u ON u.id = r.userId
        ORDER BY r.score DESC, r.playedAt ASC
        LIMIT 100
        """
    )
    fun observeTop(): Flow<List<RecordRow>>
}
