package ddwu.mobile.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    public String readDay = null;
    public String str = null;
    public CalendarView calendarView;
    public TextView diaryTextView, textView3;
    public Button make_memo;
    public String today;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = findViewById(R.id.calendarView);
        diaryTextView = findViewById(R.id.diaryTextView);
        textView3 = findViewById(R.id.textView3);
        make_memo = findViewById(R.id.make_memo);

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