package com.example.tsaichangyang.motohero5;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.face.Face;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        SensorEventListener
{

    GoogleMap map;
    SupportMapFragment sm;
    JSONArray data;
    MarkerOptions mo;
    EditText ed;
    TextView txv2;
    List<Address> list;
    Geocoder gc;
    MarkerOptions markeroptions;
    MarkerOptions markeroptions1;
    MarkerOptions markerOptions;
    MarkerOptions markerOptions1;
    double disLat;
    double disLng;
    double malat;
    double malng;
    double mylat;
    double mylng;
    double Lat;
    double Lng;
    double radLat1;
    double radLat2;
    double a;
    double b;
    double s;
    double speed;
    int sec;
    int Speed;
    float distance=0;
    int p=0;
    int q=0;
    int r=0;
    int u;
    int j;
    int open;
    int open1;
    int six;
    int three;
    int one;
    Marker mymarker;
    TextView txv;
    LatLng myLatLng;
    LatLng disLatLng;
    LatLng CameraLatLng;
    LatLng CameraLatLag1;
    Vibrator vb;
    Uri imauri;
    LocationManager lm;
    SensorManager srm;
    Sensor sr;
    String url;
    NotificationManager nm;
    ArrayList<String> lat=new ArrayList();
    ArrayList<String> lng=new ArrayList();
    ArrayList<String> address=new ArrayList();
    ArrayList<String> direct=new ArrayList<>();
    ArrayList<String> limit=new ArrayList<>();
    ArrayList<LatLng> listPoints;
    ArrayList<Float> getAngle;
    ArrayList<Float> getAngle1;
    ArrayList<Float> getAngleMed;
    ArrayList<String> getAddress;
    ArrayList<String> getAddress1;
    ArrayList<String> getAddressMed;
    ArrayList<String> getLimit1;
    ArrayList<String> getLimitMed;
    private static final String TAG="MainActivity";
    private static final int LOCATION_REQUEST = 500;

    private void parserJson(JSONObject response) {
        try {
            data=response.getJSONObject("result").getJSONArray("records");
            for(int i=1;i<data.length();i++) {
                JSONObject o = data.getJSONObject(i);
                if(o.getDouble("Latitude")>=22.5756&&o.getDouble("Latitude")<=23.00
                        &&o.getDouble("Longitude")>=120.143&&o.getDouble("Longitude")<=120.3446){
                    lat.add(o.getString("Latitude"));
                    lng.add(o.getString("Longitude"));
                    address.add(o.getString("Address"));
                    direct.add(o.getString("direct"));
                    limit.add(o.getString("limit"));
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listPoints=new ArrayList<>();
        ed=(EditText)findViewById(R.id.editText);
        open=0;
        open1=0;
        six=0;
        three=0;
        one=0;
        getAngle1=new ArrayList<>();
        getAngle=new ArrayList<>();
        getLimit1=new ArrayList<>();
        gc=new Geocoder(this, Locale.getDefault());
        vb=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        sm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        sm.getMapAsync(this);
        srm=(SensorManager)getSystemService(SENSOR_SERVICE);
        sr=srm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1,this);
        String urlParkingArea =
                "https://od.moi.gov.tw/api/v1/rest/datastore/A01010000C-000674-011";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                urlParkingArea,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response = " + response.toString());
                        parserJson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "error : " + error.toString());
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    /*nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    Intent call=new Intent(this,MainActivity.class);
    NotificationCompat.Builder bulider=new NotificationCompat.Builder(this);
    bulider.setContentText("測速照相");
    nm.notify(5,bulider.build());*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.it1:
                item.setChecked(!item.isChecked());
                if(item.isChecked()){
                    for (int i=0;i<lat.size();i++){
                        map.addMarker(new MarkerOptions().
                            position(new LatLng(Double.parseDouble(lat.get(i)),Double.parseDouble(lng.get(i))))).
                            setIcon(BitmapDescriptorFactory.fromResource(R.drawable.waring));
                    }
                }
                else{
                    map.clear();
                    map.addMarker(new MarkerOptions().position(new LatLng(malat,malng)));
                }

                break;
            case R.id.it2:
                item.setChecked(!item.isChecked());
                map.setTrafficEnabled(item.isChecked());;
                break;
            case R.id.it3:
                r=1;
                mo=new MarkerOptions();
                malat=mylat;
                malng=mylng;
                MarkerOptions mop=new MarkerOptions();
                mop.position(new LatLng(malat,malng));
                mop.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                map.addMarker(mop);
                break;
            case R.id.it4:
                map.clear();
                u=0;
                j=0;
                break;
            case R.id.it5:
                if(q==1){
                    item.setChecked(true);
                }
                else {
                    item.setChecked(!item.isChecked());
                    if (item.isChecked()) {
                        p = 1;
                    } else {
                        p = 0;
                    }
                    break;
                }
            case R.id.it6:
                if(p==1){
                    item.setChecked(true);
                }
                else {
                    item.setChecked(!item.isChecked());
                    if (item.isChecked()) {
                        q = 1;
                    } else {
                        q = 0;
                    }
                    break;
                }
            case R.id.it7:
                if(r==1){
                    u=1;
                    j=0;
                    map.clear();
                    listPoints=new ArrayList<>();
                    LatLng maLatLng=new LatLng(malat,malng);
                    myLatLng=new LatLng(mylat,mylng);
                    listPoints.add(myLatLng);
                    listPoints.add(maLatLng);
                    markerOptions=new MarkerOptions();
                    markerOptions1=new MarkerOptions();
                    markerOptions.position(maLatLng);
                    markerOptions1.position(myLatLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    map.addMarker(markerOptions);
                    map.addMarker(markerOptions1);
                    String url=getRequestUri1(listPoints.get(0),listPoints.get(1));
                    TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                else {
                    Toast.makeText(this,"喂!!你根本沒停車啊",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        LatLng My = new LatLng(22.999631,120.219760);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(My,14));
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mymarker!=null){
            mymarker.remove();
        }
        DecimalFormat df = new DecimalFormat("##.00");
        mylat = location.getLatitude();
        mylng = location.getLongitude();
        LatLng my = new LatLng(mylat,mylng);
        map.moveCamera(CameraUpdateFactory.newLatLng(my));
        myLatLng=new LatLng(mylat,mylng);
        speed=location.getSpeed();
        Speed=((int)((speed*3600)/1000));
        txv=(TextView)findViewById(R.id.textView7);
        txv.setText("時速："+String.valueOf(Speed)+"km/hr");
        MarkerOptions usermarker=new MarkerOptions().position(myLatLng);
        mymarker=map.addMarker(usermarker);
        if(u==1){
            map.clear();
            listPoints=new ArrayList<>();
            LatLng maLatLng=new LatLng(malat,malng);
            myLatLng=new LatLng(mylat,mylng);
            listPoints.add(myLatLng);
            listPoints.add(maLatLng);
            markerOptions=new MarkerOptions();
            markerOptions1=new MarkerOptions();
            markerOptions.position(maLatLng);
            markerOptions1.position(myLatLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            map.addMarker(markerOptions);
            map.addMarker(markerOptions1);
            String url=getRequestUri(listPoints.get(0),listPoints.get(1));
            TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
            taskRequestDirections.execute(url);
            if(mylng==malat&&mylng==malng){
                map.clear();
                u=0;
            }
        }
        if(j==1){
            map.clear();
                listPoints=new ArrayList<>();
                myLatLng=new LatLng(mylat,mylng);
                disLatLng=new LatLng(disLat,disLng);
                listPoints.add(disLatLng);
            listPoints.add(myLatLng);
                markeroptions = new MarkerOptions();
                markeroptions1 = new MarkerOptions();
                markeroptions.position(disLatLng);
                markeroptions1.position(myLatLng);
                markeroptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markeroptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                map.addMarker(markeroptions);
                map.addMarker(markeroptions1);
                String url = getRequestUri(listPoints.get(0), listPoints.get(1));
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
                if(mylat==disLat&&mylng==disLng){
                    map.clear();
                    j=0;
                }
        }
            for (int i = 0; i < lat.size(); i++) {
                distance=0;
                one=0;
                txv=(TextView)findViewById(R.id.textView7);
                Lat = Double.parseDouble(lat.get(i));
                Lng = Double.parseDouble(lng.get(i));
                radLat1 = (mylat * Math.PI / 180.0);
                radLat2 = (Lat * Math.PI / 180.0);
                a = radLat1 - radLat2;
                b = (mylng - Lng) * Math.PI / 180.0;
                s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                        + Math.cos(radLat1) * Math.cos(radLat2)
                        * Math.pow(Math.sin(b / 2), 2)));
                s = s * 6378000;
                s = Math.round(s * 10000) / 10000;
                distance = Float.parseFloat(df.format(s));
                if (distance <= 600&&distance>300&&open==0) {
                    txv2=(TextView)findViewById(R.id.textView10);
                    txv2.setText("");
                    open=0;
                    open1=0;
                    six=0;
                    six=six+1;
                    Toast.makeText(this, address.get(i) + "有測速照相\n" + "方向為" + direct.get(i) + "\n"
                            + "限速" + limit.get(i), Toast.LENGTH_SHORT).show();
                    txv.setTextColor(Color.GREEN);
                }
                if(distance<=300&&distance>150&&open1==0){
                    txv2=(TextView)findViewById(R.id.textView10);
                    txv2.setText("");
                    open1=0;
                    open=1;
                    three=0;
                    three=three+1;
                    vb.vibrate(2000);
                    Toast.makeText(this, address.get(i) + "真的有測速照相\n" + "方向為" + direct.get(i) + "\n"
                            + "限速" + limit.get(i), Toast.LENGTH_SHORT).show();
                    txv.setTextColor(Color.BLUE);
                    CameraLatLng=new LatLng(Double.parseDouble(lat.get(i)),Double.parseDouble(lng.get(i)));
                    AzAngle(myLatLng,CameraLatLng);
                    getAngle.add(AzAngle(myLatLng,CameraLatLng));
                }
                if(distance<=150){
                    open=1;
                    open1=1;
                    one=one+1;
                    vb.vibrate(5000);
                    //getAngle1=new ArrayList<>();
                    Toast.makeText(this, "即將通過測速照相\n"+"位置："+address.get(i) + "\n" + "方向為" + direct.get(i) + "\n"
                            + "限速" + limit.get(i), Toast.LENGTH_SHORT).show();
                    txv.setTextColor(Color.RED);
                    CameraLatLag1=new LatLng(Double.parseDouble(lat.get(i)),Double.parseDouble(lng.get(i)));
                    AzAngle(myLatLng,CameraLatLag1);
                    getAngle1.add(AzAngle(myLatLng,CameraLatLag1));
                    //getAddress.add(address.get(i));
                    getLimit1.add(limit.get(i));
                    if(getAngle.size() >=1) {
                        for (int k = 0; k < getAngle.size(); k++) {
                            for (int p = 0; p < getAngle1.size(); p++) {
                                if (getAngle.get(k) - getAngle1.get(p) <= 5 || getAngle.get(k) - getAngle1.get(p) >= -5) {
                                    Toast.makeText(this, "有測速照相喔", Toast.LENGTH_SHORT).show();
                                    getLimitMed = new ArrayList<>();
                                    getLimitMed.add(getLimit1.get(p));
                                    txv2 = (TextView) findViewById(R.id.textView10);
                                    if (Integer.parseInt(getLimitMed.get(p)) <= Speed) {
                                        txv2.setText("你他媽的超速了!!測速照相就在眼前");
                                    }
                                }
                            }
                        }
                        getAngle=new ArrayList<>();
                        getAngle1=new ArrayList<>();
                    }
                }

            }
            if(six>=1){
                open=1;
            }
            if(three>=1){
                open1=1;
            }
            if(one<1){
                open=0;
                open1=0;
            }
    }

    private float AzAngle(LatLng myLatLng, LatLng CameraLatLng) {
        float angle = 0;
        double MLat=CameraLatLng.latitude;
        double MLng=CameraLatLng.longitude;
        mylat=myLatLng.latitude;
        mylng=myLatLng.longitude;
        if(mylat<=MLat) {
            double d = 0;
            double lat_a = mylat * Math.PI / 180;
            double lng_a = mylng * Math.PI / 180;
            double lat_b = MLat * Math.PI / 180;
            double lng_b = MLng * Math.PI / 180;
            d = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
            d = Math.sqrt(1 - d * d);
            d = Math.cos(lat_b) * Math.sin(lng_b - lng_a) / d;
            angle= (float) (Math.asin(d) * 180 / Math.PI);
            //getAngle.add(angle);
        }
        if(mylat>MLat) {
            double d = 0;
            double lat_a = mylat * Math.PI / 180;
            double lng_a = mylng * Math.PI / 180;
            double lat_b = MLat * Math.PI / 180;
            double lng_b = MLng * Math.PI / 180;
            d = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a) * Math.cos(lat_b) * Math.cos(lng_a - lng_b);
            d = Math.sqrt(1 - d * d);
            d = Math.cos(lat_b) * Math.sin(lng_a - lng_b) / d;
            angle = (float) (Math.asin(d) * 180 / Math.PI) + 180;
            //getAngle.add(angle);
        }
        return angle;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //txv=(TextView)findViewById(R.id.textView);
        //txv.setText(String.valueOf(sensorEvent.values[1]));
        if(sensorEvent.values[1]<0&&p==1){
            Intent it=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(it,101);
        }
        if(sensorEvent.values[1]>8&&q==1){
            Intent it=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(it,101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&&resultCode==Activity.RESULT_OK){
            Bitmap bm=(Bitmap)data.getExtras().get("data");
            p=0;
            q=0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        srm.registerListener(this,sr,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        srm.unregisterListener(this);
    }

    public void serch(View v){
        try {
            j=1;
            u=0;
            list=null;
            //txv=(TextView)findViewById(R.id.textView7);
            //txv.setText(String.valueOf(malat));
            list=gc.getFromLocationName(ed.getText().toString(),1);
            listPoints=new ArrayList<>();
            disLat=list.get(0).getLatitude();
            disLng=list.get(0).getLongitude();
            myLatLng=new LatLng(mylat,mylng);
            listPoints.add(myLatLng);
            disLatLng=new LatLng(disLat,disLng);
            listPoints.add(disLatLng);
            map.clear();
            markeroptions=new MarkerOptions();
            markeroptions1=new MarkerOptions();
            markeroptions.position(disLatLng);
            markeroptions1.position(myLatLng);
            markeroptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markeroptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            map.addMarker(markeroptions);
            map.addMarker(markeroptions1);
            String url=getRequestUri(listPoints.get(0),listPoints.get(1));
            TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
            taskRequestDirections.execute(url);
            /*if(mylat<=disLat) {
                double d = 0;
                double lat_a = mylat * Math.PI / 180;
                double lng_a = mylng * Math.PI / 180;
                double lat_b = disLat * Math.PI / 180;
                double lng_b = disLng * Math.PI / 180;
                d = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
                d = Math.sqrt(1 - d * d);
                d = Math.cos(lat_b) * Math.sin(lng_b - lng_a) / d;
                float angle = (float) (Math.asin(d) * 180 / Math.PI);
                txv = (TextView) findViewById(R.id.textView7);
                txv.setText(String.valueOf(angle));
            }
            if(mylat>disLat){
                double d = 0;
                double lat_a = mylat * Math.PI / 180;
                double lng_a = mylng * Math.PI / 180;
                double lat_b = disLat * Math.PI / 180;
                double lng_b = disLng * Math.PI / 180;
                d = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a) * Math.cos(lat_b) * Math.cos(lng_a - lng_b);
                d = Math.sqrt(1 - d * d);
                d = Math.cos(lat_b) * Math.sin(lng_a - lng_b) / d;
                float angle = (float) (Math.asin(d) * 180 / Math.PI)+180;
                txv = (TextView) findViewById(R.id.textView7);
                txv.setText(String.valueOf(angle));
            }*/
        } catch (IOException e) {
            Toast.makeText(this,"幹你媽的機掰小去吃屎比較快操你的幹死你的腦袋",Toast.LENGTH_SHORT);
        }
    }

    private String getRequestUri(LatLng origin, LatLng dest) {
        String str_org="origin="+origin.latitude+","+origin.longitude;
        String str_dest="destination="+dest.latitude+","+dest.longitude;
        String sensor="sensor=false";
        String mode = "mode=driving";
        String avoid="avoid=highway";
        String param=str_org+"&"+str_dest+"&"+avoid+"&"+sensor+"&"+mode;
        String output="json";
        String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;
        return url;
    }
    private String getRequestUri1(LatLng origin, LatLng dest) {
        String str_org="origin="+origin.latitude+","+origin.longitude;
        String str_dest="destination="+dest.latitude+","+dest.longitude;
        String sensor="sensor=false";
        String mode = "mode=walking";
        String param=str_org+"&"+str_dest+"&"+"&"+sensor+"&"+mode;
        String output="json";
        String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;
        return url;
    }
    private String requestDirection(String reqUrl) throws IOException {
        String responseString="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;
        try{
            URL url=new URL(reqUrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.connect();

            inputStream=httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferReader=new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while ((line=bufferReader.readLine())!=null){
                stringBuffer.append(line);

            }

            responseString=stringBuffer.toString();
            bufferReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    map.setMyLocationEnabled(true);
                }
                break;
        }
    }
    public class TaskRequestDirections extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String responseString="";
            try {
                responseString=requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser=new TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends AsyncTask<String,Void,List<List<HashMap<String, String>>>>{


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject=null;
            List<List<HashMap<String, String>>>routes=null;
            try {
                jsonObject=new JSONObject(strings[0]);
                DirectionsParser directionsParser=new DirectionsParser();
                routes=directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points=null;
            PolylineOptions polylineOptions=null;
            for(List<HashMap<String, String>> path:lists){
                points=new ArrayList();
                polylineOptions=new PolylineOptions();
                for(HashMap<String, String> point:path){
                    double lat=Double.parseDouble(point.get("lat"));
                    double lon=Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if(polylineOptions!=null){
                map.addPolyline(polylineOptions);
            }
            else {
                Toast.makeText(getApplicationContext(),"sleep",Toast.LENGTH_SHORT).show();
            }
        }

    }

}
