// Restrukturert: ok

package no.slomic.body.measurements.storage;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

import java.io.File;

public class DbBackupHelper extends BackupAgentHelper {

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "dbs";

    @Override
    public void onCreate() {
        FileBackupHelper dbs = new FileBackupHelper(this, SQLiteHelper.DATABASE_NAME);
        addHelper(PREFS_BACKUP_KEY, dbs);
    }

    @Override
    public File getFilesDir() {
        File path = getDatabasePath(SQLiteHelper.DATABASE_NAME);
        return path.getParentFile();
    }
}
