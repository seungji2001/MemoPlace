package ddwu.mobile.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class LocationDBHelper extends SQLiteOpenHelper {
    final static String TAG = "LocationDBHelper";
    final  static String DB_NAME = "locations.db";
    public final static String TABLE_NAME="location_table";
    public final static String COL_ID = "_id";
    public final static String COL_DATE = "create_date";
    public final static String COL_ADDRESS = "address";
    public final static String COL_LAT = "latitude";
    public final static String COL_LONG = "longitude";
    public final static String COL_FILENAME = "filename";



    public LocationDBHelper(@Nullable Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_DATE + " TEXT, " + COL_ADDRESS + " TEXT, "+ COL_LAT +
                " TEXT, " + COL_LONG + " TEXT, " + COL_FILENAME + " TEXT) ";
        Log.d(TAG,sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
