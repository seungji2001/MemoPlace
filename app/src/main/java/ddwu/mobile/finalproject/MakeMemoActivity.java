package ddwu.mobile.finalproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MakeMemoActivity extends AppCompatActivity {

    final int REQ_PERMISSION_CODE = 100;
    final int MAKE_MEMO = 101;

    public TextView today;
    public TextView select_location;
    public EditText start_location;
    public Button find_location_btn;
    public Button button;
    public ListView list_view;
    public  String today_str;
    public List<String> locationList;
    ArrayAdapter<String> adapter;

    FusedLocationProviderClient flpClient;
    GoogleMap gm;
    Marker centerMarker;
    Marker selectMarker;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_memo);

        Log.d("TAG","첫 오픈");
        today = findViewById(R.id.show_today_date);
        start_location = findViewById(R.id.start_location);
        list_view = findViewById(R.id.list_view);
        select_location = findViewById(R.id.select_location);

        checkPermission();

        Intent intent = getIntent();
        today_str = intent.getExtras().getString("today");
        today.setText(today_str);

        SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        smf.getMapAsync(mapReadyCallback);
        flpClient = LocationServices.getFusedLocationProviderClient(this);

        locationList = new ArrayList<String>();
        //초기 locationnList 지정
        String filename = today_str.replaceAll(" / ","") + ".txt";
        String path = getFilesDir().getPath() + "/" + filename;
        File readFile  = new File(path);
        if(readFile.exists()) {
            //해당파일이 존재하는 경우 파일을 읽어서
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
                //읽어 들인 후 리스트에 넣기
                Log.d("TAG", "line" + line);
                locationList.add(line);
            }
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        for(String location : locationList){
//
//        }
//        ArrayList<String> phoneData = new ArrayList<>();
//        for(int i = 0; i<100; i++){
//            phoneData.add("010-1111"+i);
//        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,locationList);

        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG",String.valueOf(view));
                Log.d("TAG",String.valueOf(position));
                Log.d("TAG",String.valueOf(id));

                String loc = (String)parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(),DetailMemoActivity.class);
                intent.putExtra("today",today_str);
                intent.putExtra("select_location",loc);
                startActivityForResult(intent, MAKE_MEMO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MAKE_MEMO && resultCode == 103){
            //memoLocation을 받았을 경우
            String memoLocation = data.getStringExtra("memoLocation");
            Log.d("TAG","clicked" + memoLocation);
            locationList.add(memoLocation);

            //파일에도 추가된 내용 추가하기
            String filename = today_str.replaceAll(" / ","") + ".txt";
            String path = getFilesDir().getPath() + "/" + filename;
            File readFile  = new File(path);
            if(readFile.exists()){
                //이미 파일이 존재하는 경우 읽어들인 후 추가로 작성 만들지 않아도 된다
                Log.d("TAG","exist");
                try {
                    RandomAccessFile raf = new RandomAccessFile(readFile, "rw");
                    raf.seek(raf.length());
                    raf.writeBytes("\r\n" + memoLocation);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                File file = new File(getFilesDir(),filename);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(memoLocation.getBytes());
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            adapter.notifyDataSetChanged();
        }
        else if(requestCode == MAKE_MEMO && resultCode == 102){
            //memoLocation을 받지않았을 경우
            Log.d("TAG","resultCode == 102");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG","paused");
    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            gm = googleMap;

            LatLng currentLoc = new LatLng(37.606320, 127.041808);
            gm.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

            MarkerOptions options = new MarkerOptions();
            options.position(currentLoc);
            options.icon(BitmapDescriptorFactory.defaultMarker());
            options.title("현재 위치");
            options.snippet("이동중");

            centerMarker = gm.addMarker(options);

            gm.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NonNull Marker marker) {
                    Toast.makeText(MakeMemoActivity.this, "마카" + marker.getId(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, String.valueOf(marker.getPosition()));
                    Intent intent = new Intent(getApplicationContext(),DetailMemoActivity.class);
                    intent.putExtra("today",today_str);
                    intent.putExtra("select_location",String.valueOf(marker.getPosition()));
                    startActivityForResult(intent, MAKE_MEMO);
                }
            });

            gm.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    String loc = String.format("위도 : %f, 경도 : %f", latLng.latitude, latLng.longitude);
                    select_location.setText(loc);
                    MarkerOptions options_select = new MarkerOptions();
                    options_select.position(latLng);
                    options_select.icon(BitmapDescriptorFactory.defaultMarker());
                    options_select.title("선택위치");
                    options_select.snippet("메모하기");

                    if(selectMarker == null)
                        selectMarker = gm.addMarker(options_select);
                    else
                        selectMarker.setPosition(latLng);
                    Toast.makeText(MakeMemoActivity.this, loc, Toast.LENGTH_SHORT).show();
                }
            });

        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_location_btn:
                String loc = start_location.toString();
                executeGeocoding(loc);
                break;
            case R.id.current_location_btn:
                //현재위치 표시
                Log.d(TAG,"clicked");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                flpClient.requestLocationUpdates(
                        getLocationRequest(),
                        mLocCallback,
                        Looper.getMainLooper()
                );
                break;
            case R.id.save_changes:
                //선택한 위치들 저장하기

                break;
        }
    }
    private void executeGeocoding(String address) {
        if (Geocoder.isPresent()) {
            Toast.makeText(this, "Run Geocoder", Toast.LENGTH_SHORT).show();
            if (address != null)  new GeoTask().execute(address);
        } else {
            Toast.makeText(this, "No Geocoder", Toast.LENGTH_SHORT).show();
        }
    }
    class GeoTask extends AsyncTask<String, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(MakeMemoActivity.this, Locale.getDefault());
        @Override
        protected List<Address> doInBackground(String... addresses) {
            Log.d(TAG,addresses[0].toString());
            List<Address> locations = null;
            try {
                locations = geocoder.getFromLocationName(addresses[0],5);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return locations;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            Address address = addresses.get(0);
            Toast.makeText(MakeMemoActivity.this, address.getAddressLine(0), Toast.LENGTH_SHORT ).show();
        }
    }
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    LocationCallback mLocCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location loc : locationResult.getLocations()) {
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();

//                현재 수신 위치로 GoogleMap 위치 설정
                LatLng latLng = new LatLng(lat,lng);

                gm.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                mLastLocation = loc;
                centerMarker.setPosition(latLng);

                gm.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        Toast.makeText(MakeMemoActivity.this, "마카" + marker.getId(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),DetailMemoActivity.class);
                        intent.putExtra("today",today_str);
                        intent.putExtra("select_location",String.valueOf(marker.getPosition()));
                        startActivityForResult(intent, MAKE_MEMO);
                    }
                });

                gm.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        String loc = String.format("위도 : %f, 경도 : %f", latLng.latitude, latLng.longitude);
                        select_location.setText(loc);

                        MarkerOptions options_select = new MarkerOptions();
                        options_select.position(latLng);
                        options_select.icon(BitmapDescriptorFactory.defaultMarker());
                        options_select.title("선택위치");
                        options_select.snippet("메모하기");

                        selectMarker.setPosition(latLng);
                        Toast.makeText(MakeMemoActivity.this, loc, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "위치권한 획득 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "위치권한 미획득", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // 권한이 있을 경우 수행할 동작
            Toast.makeText(this,"Permissions Granted", Toast.LENGTH_SHORT).show();
        } else {
            // 권한 요청
            requestPermissions(new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
        }
    }
}