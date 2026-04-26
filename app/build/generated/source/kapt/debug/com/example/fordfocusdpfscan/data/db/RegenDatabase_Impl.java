package com.example.fordfocusdpfscan.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class RegenDatabase_Impl extends RegenDatabase {
  private volatile RegenDao _regenDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `regen_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `startTimestamp` INTEGER NOT NULL, `endTimestamp` INTEGER, `preOdometerKm` INTEGER NOT NULL, `preSootPct` REAL NOT NULL, `preLoadPct` REAL NOT NULL, `preDeltaPKpa` REAL NOT NULL, `preEgtC` REAL NOT NULL, `preCoolantC` REAL NOT NULL, `peakEgtC` REAL NOT NULL, `peakDeltaPKpa` REAL NOT NULL, `postSootPct` REAL, `postLoadPct` REAL, `postOdometerKm` INTEGER, `postEgtC` REAL, `postCoolantC` REAL, `durationMinutes` INTEGER, `regenType` TEXT NOT NULL, `result` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `regen_data_points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `elapsedSeconds` INTEGER NOT NULL, `sootPct` REAL NOT NULL, `loadPct` REAL NOT NULL, `deltaPKpa` REAL NOT NULL, `egtC` REAL NOT NULL, `coolantC` REAL NOT NULL, FOREIGN KEY(`sessionId`) REFERENCES `regen_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_regen_data_points_sessionId` ON `regen_data_points` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7440ba12494f5e8f1a90ff0de10bc2cc')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `regen_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `regen_data_points`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRegenSessions = new HashMap<String, TableInfo.Column>(19);
        _columnsRegenSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("startTimestamp", new TableInfo.Column("startTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("endTimestamp", new TableInfo.Column("endTimestamp", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("preOdometerKm", new TableInfo.Column("preOdometerKm", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("preSootPct", new TableInfo.Column("preSootPct", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("preLoadPct", new TableInfo.Column("preLoadPct", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("preDeltaPKpa", new TableInfo.Column("preDeltaPKpa", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("preEgtC", new TableInfo.Column("preEgtC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("preCoolantC", new TableInfo.Column("preCoolantC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("peakEgtC", new TableInfo.Column("peakEgtC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("peakDeltaPKpa", new TableInfo.Column("peakDeltaPKpa", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("postSootPct", new TableInfo.Column("postSootPct", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("postLoadPct", new TableInfo.Column("postLoadPct", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("postOdometerKm", new TableInfo.Column("postOdometerKm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("postEgtC", new TableInfo.Column("postEgtC", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("postCoolantC", new TableInfo.Column("postCoolantC", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("regenType", new TableInfo.Column("regenType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenSessions.put("result", new TableInfo.Column("result", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRegenSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRegenSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRegenSessions = new TableInfo("regen_sessions", _columnsRegenSessions, _foreignKeysRegenSessions, _indicesRegenSessions);
        final TableInfo _existingRegenSessions = TableInfo.read(db, "regen_sessions");
        if (!_infoRegenSessions.equals(_existingRegenSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "regen_sessions(com.example.fordfocusdpfscan.data.db.RegenSession).\n"
                  + " Expected:\n" + _infoRegenSessions + "\n"
                  + " Found:\n" + _existingRegenSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsRegenDataPoints = new HashMap<String, TableInfo.Column>(9);
        _columnsRegenDataPoints.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenDataPoints.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenDataPoints.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenDataPoints.put("elapsedSeconds", new TableInfo.Column("elapsedSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenDataPoints.put("sootPct", new TableInfo.Column("sootPct", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenDataPoints.put("loadPct", new TableInfo.Column("loadPct", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenDataPoints.put("deltaPKpa", new TableInfo.Column("deltaPKpa", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenDataPoints.put("egtC", new TableInfo.Column("egtC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegenDataPoints.put("coolantC", new TableInfo.Column("coolantC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRegenDataPoints = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRegenDataPoints.add(new TableInfo.ForeignKey("regen_sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRegenDataPoints = new HashSet<TableInfo.Index>(1);
        _indicesRegenDataPoints.add(new TableInfo.Index("index_regen_data_points_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoRegenDataPoints = new TableInfo("regen_data_points", _columnsRegenDataPoints, _foreignKeysRegenDataPoints, _indicesRegenDataPoints);
        final TableInfo _existingRegenDataPoints = TableInfo.read(db, "regen_data_points");
        if (!_infoRegenDataPoints.equals(_existingRegenDataPoints)) {
          return new RoomOpenHelper.ValidationResult(false, "regen_data_points(com.example.fordfocusdpfscan.data.db.RegenDataPoint).\n"
                  + " Expected:\n" + _infoRegenDataPoints + "\n"
                  + " Found:\n" + _existingRegenDataPoints);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "7440ba12494f5e8f1a90ff0de10bc2cc", "114ad3f8fed128269c18b5076241cb4f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "regen_sessions","regen_data_points");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `regen_sessions`");
      _db.execSQL("DELETE FROM `regen_data_points`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RegenDao.class, RegenDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RegenDao regenDao() {
    if (_regenDao != null) {
      return _regenDao;
    } else {
      synchronized(this) {
        if(_regenDao == null) {
          _regenDao = new RegenDao_Impl(this);
        }
        return _regenDao;
      }
    }
  }
}
