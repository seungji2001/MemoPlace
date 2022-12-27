package ddwu.mobile.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    public String readDay = null;
    public String str = null;
    public CalendarView calendarView;
    public TextView diaryTextView;
    public TextView logIn_success_id_tv;
    public Button make_memo;
    public String today;
    public Button login;
    public EditText logId;
    public  EditText pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserDBHelper dbHelper = new UserDBHelper(MainActivity.this);

        calendarView = findViewById(R.id.calendarView);
        diaryTextView = findViewById(R.id.diaryTextView);
        make_memo = findViewById(R.id.make_memo);
        logId = findViewById(R.id.logId);
        pwd = findViewById(R.id.password);
        login = findViewById(R.id.login);
        logIn_success_id_tv = findViewById(R.id.logIn_success_id);

        calendarView.setVisibility(View.INVISIBLE);
        make_memo.setVisibility(View.INVISIBLE);
        diaryTextView.setVisibility(View.INVISIBLE);
        logIn_success_id_tv.setVisibility(View.INVISIBLE);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                diaryTextView.setVisibility(View.VISIBLE);
                today = String.format("%d / %d / %d", year, month + 1, dayOfMonth);
                diaryTextView.setText(today);
                checkDay(year, month, dayOfMonth);
            }
        });

        make_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MakeMemoActivity.class);
                intent.putExtra("today",today);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = logId.getText().toString();
                String password = pwd.getText().toString();
//                String password = pwd.getText().toString();
                //아이디 존재시 검사, 아이디 존재 하지 않을 경우 생성
                SQLiteDatabase dbHelperReadableDatabase = dbHelper.getReadableDatabase();
                String[] projection = {
                        "password"
                };

                String selection = "logId=?";
                String[] selectionArgs = {id};

                Cursor cursor = dbHelperReadableDatabase.query(
                        UserDBHelper.TABLE_NAME,   // The table to query
                        projection,             // The array of columns to return (pass null to get all)
                        selection,              // The columns for the WHERE clause
                        selectionArgs,          // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filterby row groups
                        null               // The sort order
                );
                String exist_password = null;
                if(cursor.moveToNext()){
                    exist_password = cursor.getString(0);
                    cursor.close();
                    if(exist_password.equals(password)){
                        Toast.makeText(getApplicationContext(),"로그인 되었습니다",Toast.LENGTH_SHORT);
                        Toast myToast = Toast.makeText(getApplicationContext(),"로그인 성공", Toast.LENGTH_SHORT);
                        myToast.show();
                        logId.setVisibility(View.GONE);
                        login.setVisibility(View.GONE);
                        pwd.setVisibility(View.GONE);
                        calendarView.setVisibility(View.VISIBLE);
                        make_memo.setVisibility(View.VISIBLE);
                        diaryTextView.setVisibility(View.VISIBLE);
                        logIn_success_id_tv.setVisibility(View.VISIBLE);
                        logIn_success_id_tv.setText(id);
                    }
                    else if(!exist_password.equals(password)){
                        Log.d("TAG","이미존재하는 아이디");
                        Toast myToast = Toast.makeText(getApplicationContext(),"이미 존재하는 아이디입니다", Toast.LENGTH_SHORT);
                        myToast.show();
                    }
                }
                else{
                    //password 존재하지 않는 경우 - 생성

                    SQLiteDatabase dbHelperWritableDatabase = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put("logId", id);
                    values.put("password", password);

// Insert the new row, returning the primary key value of the new row
                    long newRowId = dbHelperWritableDatabase.insert(UserDBHelper.TABLE_NAME, null, values);
                    Toast myToast = Toast.makeText(getApplicationContext(),"계정을 생성하였습니다", Toast.LENGTH_SHORT);
                    myToast.show();
                }

            }
        });
    }

    public void checkDay(int cYear, int cMonth, int cDay)
    {
        readDay = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt";
        FileInputStream fis;

        try
        {
            fis = openFileInput(readDay);

            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            str = new String(fileData);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}