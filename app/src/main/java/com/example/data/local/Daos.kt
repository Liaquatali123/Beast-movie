package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getAll(): Flow<List<WatchlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :id)")
    fun isInWatchlist(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM watchlist")
    suspend fun clearAll()
}

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC")
    fun getAll(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearAll()
}

@Dao
interface ContinueWatchingDao {
    @Query("SELECT * FROM continue_watching ORDER BY lastWatchedAt DESC")
    fun getAll(): Flow<List<ContinueWatchingEntity>>

    @Query("SELECT * FROM continue_watching WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getByMediaId(mediaId: String): ContinueWatchingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ContinueWatchingEntity)

    @Query("DELETE FROM continue_watching WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM continue_watching")
    suspend fun clearAll()
}

@Dao
interface RecentSearchDao {
    @Query("SELECT query FROM recent_searches ORDER BY searchedAt DESC LIMIT 10")
    fun getRecent(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun delete(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}
