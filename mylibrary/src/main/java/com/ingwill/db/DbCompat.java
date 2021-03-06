package com.ingwill.db;

import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteCursor;

/**
 * Created by netcorner on 15/9/26.
 */
public class DbCompat {
    protected static final int FIELD_TYPE_BLOB = 4;
    protected static final int FIELD_TYPE_FLOAT = 2;
    protected static final int FIELD_TYPE_INTEGER = 1;
    protected static final int FIELD_TYPE_NULL = 0;
    protected static final int FIELD_TYPE_STRING = 3;

    /**
     * 得到数据字段类型
     * @param cursor
     * @param i
     * @return
     */
    public static int getType(Cursor cursor, int i) {
        SQLiteCursor sqLiteCursor = (SQLiteCursor) cursor;
        CursorWindow cursorWindow = sqLiteCursor.getWindow();
        int pos = cursor.getPosition();
        int type = -1;
        if (cursorWindow.isNull(pos, i)) {
            type = FIELD_TYPE_NULL;
        } else if (cursorWindow.isLong(pos, i)) {
            type = FIELD_TYPE_INTEGER;
        } else if (cursorWindow.isFloat(pos, i)) {
            type = FIELD_TYPE_FLOAT;
        } else if (cursorWindow.isString(pos, i)) {
            type = FIELD_TYPE_STRING;
        } else if (cursorWindow.isBlob(pos, i)) {
            type = FIELD_TYPE_BLOB;
        }
        return type;
    }
}
