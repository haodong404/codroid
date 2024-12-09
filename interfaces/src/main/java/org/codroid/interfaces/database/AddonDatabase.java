package org.codroid.interfaces.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AddonsEntity.class}, version = 1, exportSchema = false)
public abstract class AddonDatabase extends RoomDatabase {
    public abstract AddonDao addonDao();

}
