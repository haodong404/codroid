package org.codroid.interfaces.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AddonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAddon(AddonsEntity addonsEntity);

    @Query("SELECT * FROM addonsentity ORDER BY imported_date DESC")
    List<AddonsEntity> findAll();

    @Query("SELECT COUNT(*) FROM addonsentity")
    int countAll();

    @Query("SELECT COUNT(*) FROM AddonsEntity WHERE status=1")
    int countLoaded();
}
