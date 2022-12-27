package ddwu.mobile.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class UserDBHelper extends SQLiteOpenHelper {

    final static String TAG = "UserDBHelper";
    final  static String DB_NAME = "users.db";
    public final static String TABLE_NAME="user_table";
    public final static String COL_ID = "_id";
    public final static String COL_LOGID = "logId";
    public final static String COL_PASSWORD = "password";

    public UserDBHelper(@Nullable Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_LOGID + " TEXT, " + COL_PASSWORD + " TEXT) ";
        Log.d(TAG,sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
