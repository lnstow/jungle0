package com.lnstow.jungle0.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lnstow.jungle0.database.entity.Read;

import java.util.List;

@Dao
public interface ReadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertReads(Read... reads);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertReads(List<Read> reads);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateReads(Read... reads);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateReads(List<Read> reads);

    @Delete
    void deleteReads(Read... reads);

    @Delete
    void deleteReads(List<Read> reads);

    @Query("select rowid,* from read order by time desc")
    List<Read> loadAllReads();

    @Query("select rowid,* from read where status = :status order by rowid desc limit 300")
    List<Read> loadAllReadsByStatus(byte status);
}
