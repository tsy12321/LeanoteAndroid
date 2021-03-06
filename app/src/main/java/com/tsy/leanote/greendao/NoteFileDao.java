package com.tsy.leanote.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.tsy.leanote.feature.note.bean.NoteFile;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTE_FILE".
*/
public class NoteFileDao extends AbstractDao<NoteFile, Long> {

    public static final String TABLENAME = "NOTE_FILE";

    /**
     * Properties of entity NoteFile.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Noteid = new Property(1, String.class, "noteid", false, "NOTEID");
        public final static Property FileId = new Property(2, String.class, "fileId", false, "FILE_ID");
        public final static Property LocalFileId = new Property(3, String.class, "localFileId", false, "LOCAL_FILE_ID");
        public final static Property Type = new Property(4, String.class, "type", false, "TYPE");
        public final static Property Title = new Property(5, String.class, "title", false, "TITLE");
        public final static Property HasBody = new Property(6, boolean.class, "hasBody", false, "HAS_BODY");
        public final static Property IsAttach = new Property(7, boolean.class, "isAttach", false, "IS_ATTACH");
    }


    public NoteFileDao(DaoConfig config) {
        super(config);
    }
    
    public NoteFileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTE_FILE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NOTEID\" TEXT," + // 1: noteid
                "\"FILE_ID\" TEXT," + // 2: fileId
                "\"LOCAL_FILE_ID\" TEXT," + // 3: localFileId
                "\"TYPE\" TEXT," + // 4: type
                "\"TITLE\" TEXT," + // 5: title
                "\"HAS_BODY\" INTEGER NOT NULL ," + // 6: hasBody
                "\"IS_ATTACH\" INTEGER NOT NULL );"); // 7: isAttach
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTE_FILE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NoteFile entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String noteid = entity.getNoteid();
        if (noteid != null) {
            stmt.bindString(2, noteid);
        }
 
        String fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindString(3, fileId);
        }
 
        String localFileId = entity.getLocalFileId();
        if (localFileId != null) {
            stmt.bindString(4, localFileId);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
        stmt.bindLong(7, entity.getHasBody() ? 1L: 0L);
        stmt.bindLong(8, entity.getIsAttach() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NoteFile entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String noteid = entity.getNoteid();
        if (noteid != null) {
            stmt.bindString(2, noteid);
        }
 
        String fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindString(3, fileId);
        }
 
        String localFileId = entity.getLocalFileId();
        if (localFileId != null) {
            stmt.bindString(4, localFileId);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
        stmt.bindLong(7, entity.getHasBody() ? 1L: 0L);
        stmt.bindLong(8, entity.getIsAttach() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NoteFile readEntity(Cursor cursor, int offset) {
        NoteFile entity = new NoteFile( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // noteid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // fileId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // localFileId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // title
            cursor.getShort(offset + 6) != 0, // hasBody
            cursor.getShort(offset + 7) != 0 // isAttach
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NoteFile entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNoteid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFileId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLocalFileId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTitle(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setHasBody(cursor.getShort(offset + 6) != 0);
        entity.setIsAttach(cursor.getShort(offset + 7) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NoteFile entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NoteFile entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NoteFile entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
