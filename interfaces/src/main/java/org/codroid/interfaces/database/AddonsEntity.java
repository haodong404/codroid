package org.codroid.interfaces.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AddonsEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public String pkg;

    @ColumnInfo(name = "addon_path")
    public String addonPath;

    // 0 if imported but not active, 1 if active.
    public int status;

    @ColumnInfo(name = "imported_date")
    public long importedDateStamp;

    public AddonsEntity(String name, String pkg, String addonPath, int status) {
        this.name = name;
        this.pkg = pkg;
        this.addonPath = addonPath;
        this.status = status;
        this.importedDateStamp = System.currentTimeMillis();
    }
}
