package ddwu.mobile.finalproject;

import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DetailMemoActivity extends AppCompatActivity {

    final int DETAIL_MEMO_CODE = 102;
    final int REQUEST_TAKE_PHOTO = 200;

    public TextView selected_location;
    public TextView show_today;
    public EditText write_memo;
    public ImageView camera_image;

    public String mCurrentPhotoPath;
    public boolean first_start = false;
    String location;
    String today;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_memo);
        Log.d("TAG","start");

        selected_location = findViewById(R.id.selected_location);
        show_today = findViewById(R.id.today);
        write_memo = findViewById(R.id.write_memo);
        camera_image = findViewById(R.id.camera_image);

        Intent intent = getIntent();
        today = intent.getExtras().getString("today");
        location = intent.getExtras().getString("select_location");

        selected_location.setText(location);
        show_today.setText(today);

    }

    //저장된 글 불러와서 보여주기
    public void showDetail(){
        filename = today.replaceAll(" / ","") + location.replaceAll("[^0-9]","") + ".txt";;
        String path = getFilesDir().getPath() + "/" + filename;
        Log.d("TAG","resume path" + path);
        File readFile  = new File(path);
        if(readFile.exists()) {
            Log.d("TAG","file exist");
            FileReader fileReader = null;
            try {
                Log.d("TAG","file exist");
                fileReader = new FileReader(readFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader br = new BufferedReader(fileReader);

            String line = "";
            while (true) {
                try {
                    if (!((line = br.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                write_memo.setText(line);
                Log.i(TAG, line);
            }
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //저장된 글 불러오기
        showDetail();

        //저장된 이미지 파일 존재시 그것으로 화면에 보여주기
        filename = today.replaceAll(" / ","") + location.replaceAll("[^0-9]","");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String filepath = storageDir + "/" + filename + ".jpg";
        File file = new File(filepath);
        if(file.exists()){
            Log.d("TAG", "image file exist");
            mCurrentPhotoPath = filepath;
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            camera_image.setImageBitmap(bitmap);
        }
        Log.d("TAG",filepath);
    }

    public void onClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.save_memo:
                makeFile();
                Intent intent = new Intent(DetailMemoActivity.this,MakeMemoActivity.class);
                if(first_start) {
                    intent.putExtra("memoLocation", location);
                    setResult(103,intent);
                }
                else {
                    setResult(102, intent);
                }
                finish();
                break;
            case R.id.capture_image:
                makeFile();
                savePic();
                break;
        }
    }

    private void savePic() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!= null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //파일 권한과, 파일명 지정해준 uri을 file provider에서 가져오기
            Uri uri = FileProvider.getUriForFile(this,"ddwu.mobile.finalproject",photoFile);
            //uri를 intent에 실어서 보내기
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            startActivityForResult(intent,REQUEST_TAKE_PHOTO);

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        filename = today.replaceAll(" / ","") + location.replaceAll("[^0-9]","");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String filepath = storageDir + "/" + filename + ".jpg";
        File image = new File(filepath);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("TAG",mCurrentPhotoPath);
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
            showDetail();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = camera_image.getWidth();
        int targetH = camera_image.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        camera_image.setImageBitmap(bitmap);
    }

    public void makeFile() throws IOException {

        filename = today.replaceAll(" / ","") + location.replaceAll("[^0-9]","") + ".txt";

        File file = new File(getFilesDir(),filename);
        if(!file.exists()){
            first_start = true;
        }
        FileOutputStream fos = new FileOutputStream(file);

        String data =write_memo.getText().toString();
        Log.d("TAG","data " + data);
        fos.write(data.getBytes());

        fos.flush();
        fos.close();
    }
}