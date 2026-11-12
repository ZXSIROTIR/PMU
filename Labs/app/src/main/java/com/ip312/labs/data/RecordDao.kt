package com.ip312.labs.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecordDao {

    @Query("SELECT * FROM records WHERE userId = :userId LIMIT 1")
    abstract suspend fun getByUser(userId: Long): GameRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrReplace(record: GameRecord): Long

    @Transaction
    open suspend fun saveIfBetter(record: GameRecord): Boolean {
        val old = getByUser(record.userId)

        if (old != null && old.score >= record.score) {
            return false
        }

        insertOrReplace(record)
        return true
    }

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
    abstract fun observeTop(): Flow<List<RecordRow>>
}