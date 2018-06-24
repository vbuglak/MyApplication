package sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by crazycrosshair on 21.06.2018.
 */

public class DataBase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "MainDB";
    public static final String TABLE_ANIM = "Animals";
    public static final String TABLE_PRO = "Property";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_AGE = "age";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_TYPE = "type";
    public static final String KEY_ANIM = "animal";
    public static final String KEY_DATE = "date_pro";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ANIM + "(" + KEY_ID
                + " integer primary key AUTOINCREMENT," + KEY_NAME + " text," + KEY_AGE + " text," + KEY_GENDER + " text," + KEY_TYPE + " text" + ")");
        db.execSQL("create table " + TABLE_PRO + "(" + KEY_ID
                + " integer primary key AUTOINCREMENT," + KEY_DATE + " text," + KEY_ANIM  + " integer, foreign key (" + KEY_ANIM + ") references " + TABLE_ANIM + "(" + KEY_ID + ")"+" )");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ANIM);
        db.execSQL("drop table if exists " + TABLE_PRO);

        onCreate(db);

    }


}
