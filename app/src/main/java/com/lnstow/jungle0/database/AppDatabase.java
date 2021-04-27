package com.lnstow.jungle0.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lnstow.jungle0.database.dao.ReadDao;
import com.lnstow.jungle0.database.entity.Read;

@Database(version = 1, entities = {Read.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReadDao readDao();
}
