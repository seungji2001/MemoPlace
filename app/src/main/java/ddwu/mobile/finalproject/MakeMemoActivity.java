package ddwu.mobile.finalproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ScrollView;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MakeMemoActivity extends AppCompatActivity {

    final int REQ_PERMISSION_CODE = 100;
    final int MAKE_MEMO = 101;
    LocationDBHelper dbHelper = new LocationDBHelper(MakeMemoActivity.this);

    public TextView today;
    public TextView select_location;
    public EditText start_location;
    public Button find_location_btn;
    public Button button;
    public ListView list_view;
    public ListView list_view_search;
    public String today_str;
    public List<String> locationList;
    public List<String> searchPlaces;
    public ScrollView scrollView2;
    public ScrollView scrollView3;
    public EditText title;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter_search;
    FusedLocationProviderClient flpClient;
    GoogleMap gm;
    boolean first;
    Marker centerMarker;
    Marker selectMarker;
    Marker nMarker;
    Location mLastLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_memo);

        Log.d("TAG", "??? ??????");
        today = findViewById(R.id.show_today_date);
        start_location = findViewById(R.id.start_location);
        list_view = findViewById(R.id.list_view);
        list_view_search = findViewById(R.id.list_view_search);
        select_location = findViewById(R.id.select_location);
        scrollView2 = findViewById(R.id.scrollView2);
        scrollView3 = findViewById(R.id.scrollView3);
        title = findViewById(R.id.title);
        checkPermission();

        Intent intent = getIntent();
        today_str = intent.getExtras().getString("today");
        today.setText(today_str);

        SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        smf.getMapAsync(mapReadyCallback);
        flpClient = LocationServices.getFusedLocationProviderClient(this);

        locationList = new ArrayList<String>();
        //?????? locationnList ??????
        SQLiteDatabase dbHelperReadableDatabase = dbHelper.getReadableDatabase();
        String[] projection = {
                "address",
                "filename"
        };

        String selection = "create_date=?";
        String[] selectionArgs = {today_str};

        Cursor cursor = dbHelperReadableDatabase.query(
                LocationDBHelper.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filterby row groups
                null               // The sort order
        );
            String address = null;
            String filename = null;
            if(cursor.moveToNext()){
                address = cursor.getString(0);
                filename = cursor.getString(1);
                locationList.add(address + " " + filename);
                cursor.close();
            }
//        String filename = today_str.replaceAll(" / ", "") + ".txt";
//        String path = getFilesDir().getPath() + "/" + filename;
//        File readFile = new File(path);
//        if (readFile.exists()) {
//            //??????????????? ???????????? ?????? ????????? ?????????
//            Log.d("TAG", "file exist");
//            FileReader fileReader = null;
//            try {
//                Log.d("TAG", "file exist");
//                fileReader = new FileReader(readFile);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            BufferedReader br = new BufferedReader(fileReader);
//
//            String line = "";
//            while (true) {
//                try {
//                    if (!((line = br.readLine()) != null)) break;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                //?????? ?????? ??? ???????????? ??????
//                Log.d("TAG", "line" + line);
//                locationList.add(line);
//            }
//            try {
//                br.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        searchPlaces = new ArrayList<>();
        adapter_search = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchPlaces);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationList);

        list_view.setAdapter(adapter);
        scrollView3.setVisibility(View.GONE);
        list_view_search.setVisibility(View.GONE);
        //??????
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", String.valueOf(view));
                Log.d("TAG", String.valueOf(position));
                Log.d("TAG", String.valueOf(id));

                //r
                String cFile = (String) parent.getAdapter().getItem(position);
                Log.d("TAG",cFile);
                SQLiteDatabase dbHelperReadableDatabase = dbHelper.getReadableDatabase();
                String[] projection = {
                        "address",
                        "latitude",
                        "longitude"
                };

                String selection = "filename=?";
                String[] selectionArgs = {cFile};

                Cursor cursor = dbHelperReadableDatabase.query(
                        LocationDBHelper.TABLE_NAME,   // The table to query
                        projection,             // The array of columns to return (pass null to get all)
                        selection,              // The columns for the WHERE clause
                        selectionArgs,          // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filterby row groups
                        null               // The sort order
                );
                String address = null;
                String latitude = null;
                String longitude = null;
                if(cursor.moveToNext()){
                    address = cursor.getString(0);
                    latitude = cursor.getString(1);
                    longitude = cursor.getString(2);
                    cursor.close();
                }

                Intent intent = new Intent(getApplicationContext(), DetailMemoActivity.class);
                intent.putExtra("today", today_str);
                intent.putExtra("str_location",address);
                intent.putExtra("filename",cFile);
                intent.putExtra("select_location", latitude + " " + longitude);
                startActivityForResult(intent, MAKE_MEMO);
            }
        });

        //?????????
        list_view_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG","select");

                String address = (String) parent.getAdapter().getItem(position);

                String cFile = today_str.replaceAll("/","") + "_" + title.getText().toString() + ".txt";
                SQLiteDatabase dbHelperWritableDatabase = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put("address",address);
                values.put("filename", cFile);
// Insert the new row, returning the primary key value of the new row
                long newRowId = dbHelperWritableDatabase.insert(LocationDBHelper.TABLE_NAME, null, values);

//                GeoApi geoApi = new GeoApi();
//                geoApi.execute(address);

                String latlng = select_location.getText().toString();
                Log.d("TAG",latlng);
                Intent intent = new Intent(getApplicationContext(),DetailMemoActivity.class);
                intent.putExtra("today",today_str);
                intent.putExtra("select_location",latlng);
                intent.putExtra("str_location",address);
                intent.putExtra("filename",cFile);
                startActivityForResult(intent,MAKE_MEMO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAKE_MEMO && resultCode == 103) {
            //memoLocation??? ????????? ??????
            String memoLocation = data.getStringExtra("memoLocation");
            locationList.add(memoLocation);

            //???????????? ????????? ?????? ????????????
            String filename = today_str.replaceAll(" / ", "") + ".txt";
            String path = getFilesDir().getPath() + "/" + filename;
            File readFile = new File(path);
            if (readFile.exists()) {
                //?????? ????????? ???????????? ?????? ???????????? ??? ????????? ?????? ????????? ????????? ??????
                Log.d("TAG", "exist");
                try {
                    RandomAccessFile raf = new RandomAccessFile(readFile, "rw");
                    raf.seek(raf.length());
                    raf.writeBytes("\r\n" + memoLocation);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                File file = new File(getFilesDir(), filename);
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
        } else if (requestCode == MAKE_MEMO && resultCode == 102) {
            //memoLocation??? ??????????????? ??????
            Log.d("TAG", "resultCode == 102");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "paused");
    }

    //??? ??????
    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            gm = googleMap;
            checkPermission();
            gm.setMyLocationEnabled(true);

            LatLng currentLoc = new LatLng(37.606320, 127.041808);
            gm.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

            MarkerOptions options = new MarkerOptions();
            options.position(currentLoc);
            options.icon(BitmapDescriptorFactory.defaultMarker());
            options.title("?????? ??????");
            options.snippet("?????????");

            centerMarker = gm.addMarker(options);

            gm.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NonNull Marker marker) {
                    Toast.makeText(MakeMemoActivity.this, "??????" + marker.getId(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, String.valueOf(marker.getPosition()));
                    String str = select_location.getText().toString();
                    String filename=today_str.replaceAll("/","") + "_" + title.getText().toString() + ".txt";
                    //?????? ????????? ????????????????????? ??????
                    //?????? ????????????????????? ??????
                    SQLiteDatabase dbHelperWritableDatabase = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put("create_date", today_str);
                    values.put("address",str);
                    values.put("latitude",marker.getPosition().latitude);
                    values.put("longitude",marker.getPosition().longitude);
                    values.put("filename",filename);

// Insert the new row, returning the primary key value of the new row
                    long newRowId = dbHelperWritableDatabase.insert(LocationDBHelper.TABLE_NAME, null, values);
                    Toast myToast = Toast.makeText(getApplicationContext(),"location table ??????", Toast.LENGTH_SHORT);
                    myToast.show();
                    Intent intent = new Intent(getApplicationContext(),DetailMemoActivity.class);
                    intent.putExtra("today",today_str);
                    intent.putExtra("select_location",String.valueOf(marker.getPosition()));
                    intent.putExtra("str_location",str);
                    intent.putExtra("filename",filename);
                    startActivityForResult(intent, MAKE_MEMO);
                }
            });

            gm.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    String loc = String.format("%f,%f", latLng.longitude,  latLng.latitude);
                    ReverseGeoApi reverseGeoApi = new ReverseGeoApi();
                    reverseGeoApi.execute(loc);

                    Log.d("TAG",String.valueOf(latLng));
                    MarkerOptions options_select = new MarkerOptions();
                    options_select.position(latLng);
                    options_select.icon(BitmapDescriptorFactory.defaultMarker());
                    options_select.title("????????????");
                    options_select.snippet("????????????");

                    if(selectMarker == null)
                        selectMarker = gm.addMarker(options_select);
                    else
                        selectMarker.setPosition(latLng);
                    Toast.makeText(MakeMemoActivity.this, loc, Toast.LENGTH_SHORT).show();
                }
            });

        }
    };

    //??????
    class GeoApi extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            ApiGeocoding apiGeocoding = new ApiGeocoding();
            return apiGeocoding.start(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            select_location.setText(s);
            JsonParser parser = new JsonParser();
            JsonObject jsonObject1;
            JsonObject jsonObject2;
            JsonArray jsonArray;
            String x="";
            String y="";
            Double x_double;
            Double y_double;
            jsonObject1 = (JsonObject) parser.parse(s);
            jsonArray = (JsonArray)jsonObject1.get("addresses");
            for(int i=0; i<jsonArray.size(); i++) {
                jsonObject2 = (JsonObject) jsonArray.get(i);
                if (null != jsonObject2.get("x")) {
                    x = jsonObject2.get("x").toString();
                }
                if (null != jsonObject2.get("y")) {
                    y = (String) jsonObject2.get("y").toString();
                }
                Log.d("TAG", x + ", " + y);
            }
            Log.d("TAG",x);
            String ansx =  x.substring(1,(x.length()-1));
            String ansy =  y.substring(1,(y.length()-1));
            Log.d("TAG",ansx);
            x_double = Double.valueOf(ansx);
            y_double = Double.valueOf(ansy);
            Log.d("TAG",String.valueOf(x_double));
            LatLng nLatLng = new LatLng(y_double,x_double);

            SQLiteDatabase dbHelperWritableDatabase = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put("longitude", ansx);
            values.put("latitude", ansy);

            String whereClause="filename=?";
            String[] whereArgs = new String[]{title.getText().toString()};

// Insert the new row, returning the primary key value of the new row
            dbHelperWritableDatabase.update(LocationDBHelper.TABLE_NAME, values, whereClause, whereArgs);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    class ReverseGeoApi extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            ApiReverseGeocoding apiReverseGeocoding = new ApiReverseGeocoding();
            return apiReverseGeocoding.start(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GeocoderXmlParser geocoderXmlParser = new GeocoderXmlParser();
            ArrayList<String> geoList = geocoderXmlParser.parse(s);
            select_location.setText(geoList.get(0));
            Log.d("TAG", String.valueOf(geoList));
        }
    }

    private void executeGeocoding(LatLng latLng) {
        if (Geocoder.isPresent()) {
            Log.d("TAG",String.valueOf(latLng.latitude));
            Toast.makeText(this, "Run Geocoder", Toast.LENGTH_SHORT).show();
            if (latLng != null)  new GeoTask().execute(latLng);
        } else {
            Toast.makeText(this, "No Geocoder", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            //??????
            case R.id.find_location_btn:
                searchPlaces.clear();
                adapter_search.notifyDataSetChanged();
                String loc = start_location.getText().toString();
                myAsyncTask task = new myAsyncTask();
                task.execute(loc);

//                if(nLatLng!=null){
//                    Log.d("TAG",nLatLng.toString());
//                }
                list_view.setVisibility(View.GONE);
                scrollView2.setVisibility(View.GONE);
                list_view_search.setVisibility(View.VISIBLE);
                scrollView3.setVisibility(View.VISIBLE);
                list_view_search.setAdapter(adapter_search);

                break;
            case R.id.current_location_btn:
                //???????????? ??????
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
                //????????? ????????? ????????????
                if(scrollView2.getVisibility() == View.VISIBLE){
                    list_view.setVisibility(View.GONE);
                    scrollView2.setVisibility(View.GONE);
                    list_view_search.setVisibility(View.VISIBLE);
                    scrollView3.setVisibility(View.VISIBLE);
                    list_view_search.setAdapter(adapter_search);
                }
                else if(scrollView3.getVisibility() == View.VISIBLE){
                    list_view_search.setVisibility(View.GONE);
                    scrollView3.setVisibility(View.GONE);
                    list_view.setVisibility(View.VISIBLE);
                    scrollView2.setVisibility(View.VISIBLE);
                    list_view.setAdapter(adapter);
                }
                break;
        }
    }

    class myAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            ApiExamSearchBlog apiExamSearchBlog = new ApiExamSearchBlog();
            return apiExamSearchBlog.start(strings[0]);
        }

        //?????? ??????
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LocationXmlParser locationXmlParser = new LocationXmlParser();
            List<SearchPlace> searchPlaceArrayList = new ArrayList<>();
            searchPlaceArrayList = locationXmlParser.parse(s);

            for(SearchPlace searchPlace : searchPlaceArrayList){
                searchPlaces.add(searchPlace.getAddress());
            }
            adapter_search.notifyDataSetChanged();
            Log.d("TAG", String.valueOf(searchPlaceArrayList));
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

//                ?????? ?????? ????????? GoogleMap ?????? ??????
                LatLng latLng = new LatLng(lat,lng);

                gm.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                mLastLocation = loc;
                centerMarker.setPosition(latLng);

                gm.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        Toast.makeText(MakeMemoActivity.this, "??????" + marker.getId(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),DetailMemoActivity.class);
                        intent.putExtra("today",today_str);
                        intent.putExtra("select_location",String.valueOf(marker.getPosition()));
                        startActivityForResult(intent, MAKE_MEMO);
                    }
                });

                gm.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        String loc = String.format("?????? : %f, ?????? : %f", latLng.latitude, latLng.longitude);
                        select_location.setText(loc);

                        MarkerOptions options_select = new MarkerOptions();
                        options_select.position(latLng);
                        options_select.icon(BitmapDescriptorFactory.defaultMarker());
                        options_select.title("????????????");
                        options_select.snippet("????????????");

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
                    Toast.makeText(this, "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "???????????? ?????????", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // ????????? ?????? ?????? ????????? ??????
            Toast.makeText(this,"Permissions Granted", Toast.LENGTH_SHORT).show();
        } else {
            // ?????? ??????
            requestPermissions(new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
        }
    }
    class GeoTask extends AsyncTask<LatLng, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(MakeMemoActivity.this, Locale.getDefault());

        @Override
        protected List<Address> doInBackground(LatLng... latLngs) {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latLngs[0].latitude,
                        latLngs[0].longitude, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            Address address = addresses.get(0);
            Log.d("TAG",String.valueOf(addresses.get(0)));
            Toast.makeText(MakeMemoActivity.this, address.getAddressLine(0), Toast.LENGTH_SHORT ).show();
        }
    }
}