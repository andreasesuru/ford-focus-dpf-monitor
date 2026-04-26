package com.example.fordfocusdpfscan.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class RegenDao_Impl implements RegenDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RegenSession> __insertionAdapterOfRegenSession;

  private final EntityInsertionAdapter<RegenDataPoint> __insertionAdapterOfRegenDataPoint;

  private final EntityDeletionOrUpdateAdapter<RegenSession> __updateAdapterOfRegenSession;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public RegenDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRegenSession = new EntityInsertionAdapter<RegenSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `regen_sessions` (`id`,`startTimestamp`,`endTimestamp`,`preOdometerKm`,`preSootPct`,`preLoadPct`,`preDeltaPKpa`,`preEgtC`,`preCoolantC`,`peakEgtC`,`peakDeltaPKpa`,`postSootPct`,`postLoadPct`,`postOdometerKm`,`postEgtC`,`postCoolantC`,`durationMinutes`,`regenType`,`result`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegenSession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStartTimestamp());
        if (entity.getEndTimestamp() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getEndTimestamp());
        }
        statement.bindLong(4, entity.getPreOdometerKm());
        statement.bindDouble(5, entity.getPreSootPct());
        statement.bindDouble(6, entity.getPreLoadPct());
        statement.bindDouble(7, entity.getPreDeltaPKpa());
        statement.bindDouble(8, entity.getPreEgtC());
        statement.bindDouble(9, entity.getPreCoolantC());
        statement.bindDouble(10, entity.getPeakEgtC());
        statement.bindDouble(11, entity.getPeakDeltaPKpa());
        if (entity.getPostSootPct() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getPostSootPct());
        }
        if (entity.getPostLoadPct() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getPostLoadPct());
        }
        if (entity.getPostOdometerKm() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getPostOdometerKm());
        }
        if (entity.getPostEgtC() == null) {
          statement.bindNull(15);
        } else {
          statement.bindDouble(15, entity.getPostEgtC());
        }
        if (entity.getPostCoolantC() == null) {
          statement.bindNull(16);
        } else {
          statement.bindDouble(16, entity.getPostCoolantC());
        }
        if (entity.getDurationMinutes() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getDurationMinutes());
        }
        if (entity.getRegenType() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getRegenType());
        }
        if (entity.getResult() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getResult());
        }
      }
    };
    this.__insertionAdapterOfRegenDataPoint = new EntityInsertionAdapter<RegenDataPoint>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `regen_data_points` (`id`,`sessionId`,`timestamp`,`elapsedSeconds`,`sootPct`,`loadPct`,`deltaPKpa`,`egtC`,`coolantC`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegenDataPoint entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindLong(4, entity.getElapsedSeconds());
        statement.bindDouble(5, entity.getSootPct());
        statement.bindDouble(6, entity.getLoadPct());
        statement.bindDouble(7, entity.getDeltaPKpa());
        statement.bindDouble(8, entity.getEgtC());
        statement.bindDouble(9, entity.getCoolantC());
      }
    };
    this.__updateAdapterOfRegenSession = new EntityDeletionOrUpdateAdapter<RegenSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `regen_sessions` SET `id` = ?,`startTimestamp` = ?,`endTimestamp` = ?,`preOdometerKm` = ?,`preSootPct` = ?,`preLoadPct` = ?,`preDeltaPKpa` = ?,`preEgtC` = ?,`preCoolantC` = ?,`peakEgtC` = ?,`peakDeltaPKpa` = ?,`postSootPct` = ?,`postLoadPct` = ?,`postOdometerKm` = ?,`postEgtC` = ?,`postCoolantC` = ?,`durationMinutes` = ?,`regenType` = ?,`result` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegenSession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStartTimestamp());
        if (entity.getEndTimestamp() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getEndTimestamp());
        }
        statement.bindLong(4, entity.getPreOdometerKm());
        statement.bindDouble(5, entity.getPreSootPct());
        statement.bindDouble(6, entity.getPreLoadPct());
        statement.bindDouble(7, entity.getPreDeltaPKpa());
        statement.bindDouble(8, entity.getPreEgtC());
        statement.bindDouble(9, entity.getPreCoolantC());
        statement.bindDouble(10, entity.getPeakEgtC());
        statement.bindDouble(11, entity.getPeakDeltaPKpa());
        if (entity.getPostSootPct() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getPostSootPct());
        }
        if (entity.getPostLoadPct() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getPostLoadPct());
        }
        if (entity.getPostOdometerKm() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getPostOdometerKm());
        }
        if (entity.getPostEgtC() == null) {
          statement.bindNull(15);
        } else {
          statement.bindDouble(15, entity.getPostEgtC());
        }
        if (entity.getPostCoolantC() == null) {
          statement.bindNull(16);
        } else {
          statement.bindDouble(16, entity.getPostCoolantC());
        }
        if (entity.getDurationMinutes() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getDurationMinutes());
        }
        if (entity.getRegenType() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getRegenType());
        }
        if (entity.getResult() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getResult());
        }
        statement.bindLong(20, entity.getId());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM regen_sessions";
        return _query;
      }
    };
  }

  @Override
  public Object insertSession(final RegenSession session,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRegenSession.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDataPoint(final RegenDataPoint point,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRegenDataPoint.insert(point);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSession(final RegenSession session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRegenSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RegenSession>> getAllSessions() {
    final String _sql = "SELECT * FROM regen_sessions ORDER BY startTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"regen_sessions"}, new Callable<List<RegenSession>>() {
      @Override
      @NonNull
      public List<RegenSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimestamp");
          final int _cursorIndexOfEndTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimestamp");
          final int _cursorIndexOfPreOdometerKm = CursorUtil.getColumnIndexOrThrow(_cursor, "preOdometerKm");
          final int _cursorIndexOfPreSootPct = CursorUtil.getColumnIndexOrThrow(_cursor, "preSootPct");
          final int _cursorIndexOfPreLoadPct = CursorUtil.getColumnIndexOrThrow(_cursor, "preLoadPct");
          final int _cursorIndexOfPreDeltaPKpa = CursorUtil.getColumnIndexOrThrow(_cursor, "preDeltaPKpa");
          final int _cursorIndexOfPreEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "preEgtC");
          final int _cursorIndexOfPreCoolantC = CursorUtil.getColumnIndexOrThrow(_cursor, "preCoolantC");
          final int _cursorIndexOfPeakEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "peakEgtC");
          final int _cursorIndexOfPeakDeltaPKpa = CursorUtil.getColumnIndexOrThrow(_cursor, "peakDeltaPKpa");
          final int _cursorIndexOfPostSootPct = CursorUtil.getColumnIndexOrThrow(_cursor, "postSootPct");
          final int _cursorIndexOfPostLoadPct = CursorUtil.getColumnIndexOrThrow(_cursor, "postLoadPct");
          final int _cursorIndexOfPostOdometerKm = CursorUtil.getColumnIndexOrThrow(_cursor, "postOdometerKm");
          final int _cursorIndexOfPostEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "postEgtC");
          final int _cursorIndexOfPostCoolantC = CursorUtil.getColumnIndexOrThrow(_cursor, "postCoolantC");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfRegenType = CursorUtil.getColumnIndexOrThrow(_cursor, "regenType");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final List<RegenSession> _result = new ArrayList<RegenSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegenSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStartTimestamp;
            _tmpStartTimestamp = _cursor.getLong(_cursorIndexOfStartTimestamp);
            final Long _tmpEndTimestamp;
            if (_cursor.isNull(_cursorIndexOfEndTimestamp)) {
              _tmpEndTimestamp = null;
            } else {
              _tmpEndTimestamp = _cursor.getLong(_cursorIndexOfEndTimestamp);
            }
            final long _tmpPreOdometerKm;
            _tmpPreOdometerKm = _cursor.getLong(_cursorIndexOfPreOdometerKm);
            final float _tmpPreSootPct;
            _tmpPreSootPct = _cursor.getFloat(_cursorIndexOfPreSootPct);
            final float _tmpPreLoadPct;
            _tmpPreLoadPct = _cursor.getFloat(_cursorIndexOfPreLoadPct);
            final float _tmpPreDeltaPKpa;
            _tmpPreDeltaPKpa = _cursor.getFloat(_cursorIndexOfPreDeltaPKpa);
            final float _tmpPreEgtC;
            _tmpPreEgtC = _cursor.getFloat(_cursorIndexOfPreEgtC);
            final float _tmpPreCoolantC;
            _tmpPreCoolantC = _cursor.getFloat(_cursorIndexOfPreCoolantC);
            final float _tmpPeakEgtC;
            _tmpPeakEgtC = _cursor.getFloat(_cursorIndexOfPeakEgtC);
            final float _tmpPeakDeltaPKpa;
            _tmpPeakDeltaPKpa = _cursor.getFloat(_cursorIndexOfPeakDeltaPKpa);
            final Float _tmpPostSootPct;
            if (_cursor.isNull(_cursorIndexOfPostSootPct)) {
              _tmpPostSootPct = null;
            } else {
              _tmpPostSootPct = _cursor.getFloat(_cursorIndexOfPostSootPct);
            }
            final Float _tmpPostLoadPct;
            if (_cursor.isNull(_cursorIndexOfPostLoadPct)) {
              _tmpPostLoadPct = null;
            } else {
              _tmpPostLoadPct = _cursor.getFloat(_cursorIndexOfPostLoadPct);
            }
            final Long _tmpPostOdometerKm;
            if (_cursor.isNull(_cursorIndexOfPostOdometerKm)) {
              _tmpPostOdometerKm = null;
            } else {
              _tmpPostOdometerKm = _cursor.getLong(_cursorIndexOfPostOdometerKm);
            }
            final Float _tmpPostEgtC;
            if (_cursor.isNull(_cursorIndexOfPostEgtC)) {
              _tmpPostEgtC = null;
            } else {
              _tmpPostEgtC = _cursor.getFloat(_cursorIndexOfPostEgtC);
            }
            final Float _tmpPostCoolantC;
            if (_cursor.isNull(_cursorIndexOfPostCoolantC)) {
              _tmpPostCoolantC = null;
            } else {
              _tmpPostCoolantC = _cursor.getFloat(_cursorIndexOfPostCoolantC);
            }
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final String _tmpRegenType;
            if (_cursor.isNull(_cursorIndexOfRegenType)) {
              _tmpRegenType = null;
            } else {
              _tmpRegenType = _cursor.getString(_cursorIndexOfRegenType);
            }
            final String _tmpResult;
            if (_cursor.isNull(_cursorIndexOfResult)) {
              _tmpResult = null;
            } else {
              _tmpResult = _cursor.getString(_cursorIndexOfResult);
            }
            _item = new RegenSession(_tmpId,_tmpStartTimestamp,_tmpEndTimestamp,_tmpPreOdometerKm,_tmpPreSootPct,_tmpPreLoadPct,_tmpPreDeltaPKpa,_tmpPreEgtC,_tmpPreCoolantC,_tmpPeakEgtC,_tmpPeakDeltaPKpa,_tmpPostSootPct,_tmpPostLoadPct,_tmpPostOdometerKm,_tmpPostEgtC,_tmpPostCoolantC,_tmpDurationMinutes,_tmpRegenType,_tmpResult);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllSessionsOnce(final Continuation<? super List<RegenSession>> $completion) {
    final String _sql = "SELECT * FROM regen_sessions ORDER BY startTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RegenSession>>() {
      @Override
      @NonNull
      public List<RegenSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimestamp");
          final int _cursorIndexOfEndTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimestamp");
          final int _cursorIndexOfPreOdometerKm = CursorUtil.getColumnIndexOrThrow(_cursor, "preOdometerKm");
          final int _cursorIndexOfPreSootPct = CursorUtil.getColumnIndexOrThrow(_cursor, "preSootPct");
          final int _cursorIndexOfPreLoadPct = CursorUtil.getColumnIndexOrThrow(_cursor, "preLoadPct");
          final int _cursorIndexOfPreDeltaPKpa = CursorUtil.getColumnIndexOrThrow(_cursor, "preDeltaPKpa");
          final int _cursorIndexOfPreEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "preEgtC");
          final int _cursorIndexOfPreCoolantC = CursorUtil.getColumnIndexOrThrow(_cursor, "preCoolantC");
          final int _cursorIndexOfPeakEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "peakEgtC");
          final int _cursorIndexOfPeakDeltaPKpa = CursorUtil.getColumnIndexOrThrow(_cursor, "peakDeltaPKpa");
          final int _cursorIndexOfPostSootPct = CursorUtil.getColumnIndexOrThrow(_cursor, "postSootPct");
          final int _cursorIndexOfPostLoadPct = CursorUtil.getColumnIndexOrThrow(_cursor, "postLoadPct");
          final int _cursorIndexOfPostOdometerKm = CursorUtil.getColumnIndexOrThrow(_cursor, "postOdometerKm");
          final int _cursorIndexOfPostEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "postEgtC");
          final int _cursorIndexOfPostCoolantC = CursorUtil.getColumnIndexOrThrow(_cursor, "postCoolantC");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfRegenType = CursorUtil.getColumnIndexOrThrow(_cursor, "regenType");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final List<RegenSession> _result = new ArrayList<RegenSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegenSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStartTimestamp;
            _tmpStartTimestamp = _cursor.getLong(_cursorIndexOfStartTimestamp);
            final Long _tmpEndTimestamp;
            if (_cursor.isNull(_cursorIndexOfEndTimestamp)) {
              _tmpEndTimestamp = null;
            } else {
              _tmpEndTimestamp = _cursor.getLong(_cursorIndexOfEndTimestamp);
            }
            final long _tmpPreOdometerKm;
            _tmpPreOdometerKm = _cursor.getLong(_cursorIndexOfPreOdometerKm);
            final float _tmpPreSootPct;
            _tmpPreSootPct = _cursor.getFloat(_cursorIndexOfPreSootPct);
            final float _tmpPreLoadPct;
            _tmpPreLoadPct = _cursor.getFloat(_cursorIndexOfPreLoadPct);
            final float _tmpPreDeltaPKpa;
            _tmpPreDeltaPKpa = _cursor.getFloat(_cursorIndexOfPreDeltaPKpa);
            final float _tmpPreEgtC;
            _tmpPreEgtC = _cursor.getFloat(_cursorIndexOfPreEgtC);
            final float _tmpPreCoolantC;
            _tmpPreCoolantC = _cursor.getFloat(_cursorIndexOfPreCoolantC);
            final float _tmpPeakEgtC;
            _tmpPeakEgtC = _cursor.getFloat(_cursorIndexOfPeakEgtC);
            final float _tmpPeakDeltaPKpa;
            _tmpPeakDeltaPKpa = _cursor.getFloat(_cursorIndexOfPeakDeltaPKpa);
            final Float _tmpPostSootPct;
            if (_cursor.isNull(_cursorIndexOfPostSootPct)) {
              _tmpPostSootPct = null;
            } else {
              _tmpPostSootPct = _cursor.getFloat(_cursorIndexOfPostSootPct);
            }
            final Float _tmpPostLoadPct;
            if (_cursor.isNull(_cursorIndexOfPostLoadPct)) {
              _tmpPostLoadPct = null;
            } else {
              _tmpPostLoadPct = _cursor.getFloat(_cursorIndexOfPostLoadPct);
            }
            final Long _tmpPostOdometerKm;
            if (_cursor.isNull(_cursorIndexOfPostOdometerKm)) {
              _tmpPostOdometerKm = null;
            } else {
              _tmpPostOdometerKm = _cursor.getLong(_cursorIndexOfPostOdometerKm);
            }
            final Float _tmpPostEgtC;
            if (_cursor.isNull(_cursorIndexOfPostEgtC)) {
              _tmpPostEgtC = null;
            } else {
              _tmpPostEgtC = _cursor.getFloat(_cursorIndexOfPostEgtC);
            }
            final Float _tmpPostCoolantC;
            if (_cursor.isNull(_cursorIndexOfPostCoolantC)) {
              _tmpPostCoolantC = null;
            } else {
              _tmpPostCoolantC = _cursor.getFloat(_cursorIndexOfPostCoolantC);
            }
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final String _tmpRegenType;
            if (_cursor.isNull(_cursorIndexOfRegenType)) {
              _tmpRegenType = null;
            } else {
              _tmpRegenType = _cursor.getString(_cursorIndexOfRegenType);
            }
            final String _tmpResult;
            if (_cursor.isNull(_cursorIndexOfResult)) {
              _tmpResult = null;
            } else {
              _tmpResult = _cursor.getString(_cursorIndexOfResult);
            }
            _item = new RegenSession(_tmpId,_tmpStartTimestamp,_tmpEndTimestamp,_tmpPreOdometerKm,_tmpPreSootPct,_tmpPreLoadPct,_tmpPreDeltaPKpa,_tmpPreEgtC,_tmpPreCoolantC,_tmpPeakEgtC,_tmpPeakDeltaPKpa,_tmpPostSootPct,_tmpPostLoadPct,_tmpPostOdometerKm,_tmpPostEgtC,_tmpPostCoolantC,_tmpDurationMinutes,_tmpRegenType,_tmpResult);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveSession(final Continuation<? super RegenSession> $completion) {
    final String _sql = "SELECT * FROM regen_sessions WHERE result = 'IN_PROGRESS' ORDER BY startTimestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RegenSession>() {
      @Override
      @Nullable
      public RegenSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimestamp");
          final int _cursorIndexOfEndTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimestamp");
          final int _cursorIndexOfPreOdometerKm = CursorUtil.getColumnIndexOrThrow(_cursor, "preOdometerKm");
          final int _cursorIndexOfPreSootPct = CursorUtil.getColumnIndexOrThrow(_cursor, "preSootPct");
          final int _cursorIndexOfPreLoadPct = CursorUtil.getColumnIndexOrThrow(_cursor, "preLoadPct");
          final int _cursorIndexOfPreDeltaPKpa = CursorUtil.getColumnIndexOrThrow(_cursor, "preDeltaPKpa");
          final int _cursorIndexOfPreEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "preEgtC");
          final int _cursorIndexOfPreCoolantC = CursorUtil.getColumnIndexOrThrow(_cursor, "preCoolantC");
          final int _cursorIndexOfPeakEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "peakEgtC");
          final int _cursorIndexOfPeakDeltaPKpa = CursorUtil.getColumnIndexOrThrow(_cursor, "peakDeltaPKpa");
          final int _cursorIndexOfPostSootPct = CursorUtil.getColumnIndexOrThrow(_cursor, "postSootPct");
          final int _cursorIndexOfPostLoadPct = CursorUtil.getColumnIndexOrThrow(_cursor, "postLoadPct");
          final int _cursorIndexOfPostOdometerKm = CursorUtil.getColumnIndexOrThrow(_cursor, "postOdometerKm");
          final int _cursorIndexOfPostEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "postEgtC");
          final int _cursorIndexOfPostCoolantC = CursorUtil.getColumnIndexOrThrow(_cursor, "postCoolantC");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfRegenType = CursorUtil.getColumnIndexOrThrow(_cursor, "regenType");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final RegenSession _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStartTimestamp;
            _tmpStartTimestamp = _cursor.getLong(_cursorIndexOfStartTimestamp);
            final Long _tmpEndTimestamp;
            if (_cursor.isNull(_cursorIndexOfEndTimestamp)) {
              _tmpEndTimestamp = null;
            } else {
              _tmpEndTimestamp = _cursor.getLong(_cursorIndexOfEndTimestamp);
            }
            final long _tmpPreOdometerKm;
            _tmpPreOdometerKm = _cursor.getLong(_cursorIndexOfPreOdometerKm);
            final float _tmpPreSootPct;
            _tmpPreSootPct = _cursor.getFloat(_cursorIndexOfPreSootPct);
            final float _tmpPreLoadPct;
            _tmpPreLoadPct = _cursor.getFloat(_cursorIndexOfPreLoadPct);
            final float _tmpPreDeltaPKpa;
            _tmpPreDeltaPKpa = _cursor.getFloat(_cursorIndexOfPreDeltaPKpa);
            final float _tmpPreEgtC;
            _tmpPreEgtC = _cursor.getFloat(_cursorIndexOfPreEgtC);
            final float _tmpPreCoolantC;
            _tmpPreCoolantC = _cursor.getFloat(_cursorIndexOfPreCoolantC);
            final float _tmpPeakEgtC;
            _tmpPeakEgtC = _cursor.getFloat(_cursorIndexOfPeakEgtC);
            final float _tmpPeakDeltaPKpa;
            _tmpPeakDeltaPKpa = _cursor.getFloat(_cursorIndexOfPeakDeltaPKpa);
            final Float _tmpPostSootPct;
            if (_cursor.isNull(_cursorIndexOfPostSootPct)) {
              _tmpPostSootPct = null;
            } else {
              _tmpPostSootPct = _cursor.getFloat(_cursorIndexOfPostSootPct);
            }
            final Float _tmpPostLoadPct;
            if (_cursor.isNull(_cursorIndexOfPostLoadPct)) {
              _tmpPostLoadPct = null;
            } else {
              _tmpPostLoadPct = _cursor.getFloat(_cursorIndexOfPostLoadPct);
            }
            final Long _tmpPostOdometerKm;
            if (_cursor.isNull(_cursorIndexOfPostOdometerKm)) {
              _tmpPostOdometerKm = null;
            } else {
              _tmpPostOdometerKm = _cursor.getLong(_cursorIndexOfPostOdometerKm);
            }
            final Float _tmpPostEgtC;
            if (_cursor.isNull(_cursorIndexOfPostEgtC)) {
              _tmpPostEgtC = null;
            } else {
              _tmpPostEgtC = _cursor.getFloat(_cursorIndexOfPostEgtC);
            }
            final Float _tmpPostCoolantC;
            if (_cursor.isNull(_cursorIndexOfPostCoolantC)) {
              _tmpPostCoolantC = null;
            } else {
              _tmpPostCoolantC = _cursor.getFloat(_cursorIndexOfPostCoolantC);
            }
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final String _tmpRegenType;
            if (_cursor.isNull(_cursorIndexOfRegenType)) {
              _tmpRegenType = null;
            } else {
              _tmpRegenType = _cursor.getString(_cursorIndexOfRegenType);
            }
            final String _tmpResult;
            if (_cursor.isNull(_cursorIndexOfResult)) {
              _tmpResult = null;
            } else {
              _tmpResult = _cursor.getString(_cursorIndexOfResult);
            }
            _result = new RegenSession(_tmpId,_tmpStartTimestamp,_tmpEndTimestamp,_tmpPreOdometerKm,_tmpPreSootPct,_tmpPreLoadPct,_tmpPreDeltaPKpa,_tmpPreEgtC,_tmpPreCoolantC,_tmpPeakEgtC,_tmpPeakDeltaPKpa,_tmpPostSootPct,_tmpPostLoadPct,_tmpPostOdometerKm,_tmpPostEgtC,_tmpPostCoolantC,_tmpDurationMinutes,_tmpRegenType,_tmpResult);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM regen_sessions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDataPointsForSession(final long sessionId,
      final Continuation<? super List<RegenDataPoint>> $completion) {
    final String _sql = "SELECT * FROM regen_data_points WHERE sessionId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RegenDataPoint>>() {
      @Override
      @NonNull
      public List<RegenDataPoint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfElapsedSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "elapsedSeconds");
          final int _cursorIndexOfSootPct = CursorUtil.getColumnIndexOrThrow(_cursor, "sootPct");
          final int _cursorIndexOfLoadPct = CursorUtil.getColumnIndexOrThrow(_cursor, "loadPct");
          final int _cursorIndexOfDeltaPKpa = CursorUtil.getColumnIndexOrThrow(_cursor, "deltaPKpa");
          final int _cursorIndexOfEgtC = CursorUtil.getColumnIndexOrThrow(_cursor, "egtC");
          final int _cursorIndexOfCoolantC = CursorUtil.getColumnIndexOrThrow(_cursor, "coolantC");
          final List<RegenDataPoint> _result = new ArrayList<RegenDataPoint>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegenDataPoint _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpElapsedSeconds;
            _tmpElapsedSeconds = _cursor.getInt(_cursorIndexOfElapsedSeconds);
            final float _tmpSootPct;
            _tmpSootPct = _cursor.getFloat(_cursorIndexOfSootPct);
            final float _tmpLoadPct;
            _tmpLoadPct = _cursor.getFloat(_cursorIndexOfLoadPct);
            final float _tmpDeltaPKpa;
            _tmpDeltaPKpa = _cursor.getFloat(_cursorIndexOfDeltaPKpa);
            final float _tmpEgtC;
            _tmpEgtC = _cursor.getFloat(_cursorIndexOfEgtC);
            final float _tmpCoolantC;
            _tmpCoolantC = _cursor.getFloat(_cursorIndexOfCoolantC);
            _item = new RegenDataPoint(_tmpId,_tmpSessionId,_tmpTimestamp,_tmpElapsedSeconds,_tmpSootPct,_tmpLoadPct,_tmpDeltaPKpa,_tmpEgtC,_tmpCoolantC);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
