package application.test.testapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    LocationListener locationListener;


    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private EditText seachWord = null;
    private String locationAsText = null;
    private Location location = null;

    private final static String CLIENT_ID = "PE2P5S2CNYYRHSNSLSQLVXLKKMY4SLZ3F4R1P0QKLR3EOCK2";
    private final static String SECRED = "WBOYILZ2CDNJWR2GON0Z0IULVTAVI1MMVKZLZME3JX44FJ1A";
    private final static String VERSION = "20140806";

    private ArrayList <Venues> venuesArray = new ArrayList<Venues>();

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
        } else {
            // display error
        }

        // LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);

        dialog.dismiss();

        if (checkPlayServices()) {

            // Building the GoogleApi client

        }

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

        getLocation();

        seachWord = (EditText) findViewById(R.id.the_word);



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        getLocation();

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location2) {
                // Called when a new location is found by the network location provider.
                //location.distanceTo(location);

                if (location != null) {

                    location = location2;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        Long minute = 60000L;
        Float distance = 300f;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minute, distance, locationListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //result.length();

            try {

                JSONObject jsonObject = new JSONObject(result);

                String response = jsonObject.getString("response");

                jsonObject = new JSONObject(response);

                String venues = jsonObject.getString("venues");

                //jsonObject = new JSONObject(venues);

                JSONArray jsonArray = new JSONArray(venues);
                for (int i=0; i < jsonArray.length(); i++) {
                    Venues venue = new Venues();  // create a new object here
                    JSONObject jpersonObj = jsonArray.getJSONObject(i);

                    venue.setId((String) jpersonObj.getString("id"));
                    venue.setName((String) jpersonObj.getString("name"));
                    venue.setName((String) jpersonObj.getString("name"));

                    String locationText = jpersonObj.getString("location");
                    if(locationText!=null){

                        JSONObject locationJson = new JSONObject(locationText);

                        venue.setDistance(Double.parseDouble(locationJson.getString("distance")));

                        venue.setAddress(locationJson.getString("formattedAddress"));

                        String longitude = locationJson.getString("lng");
                        String latitude = locationJson.getString("lat");

                        Location location = new Location("GPS");
                        location.setLongitude(Double.parseDouble(longitude));
                        location.setLatitude(Double.parseDouble(latitude));

                        venue.setLocation(location);
                    }
                    venuesArray.add(venue);
                }

                venuesArray.size();
                //JSONObject json = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Get ListView object from xml
            ListView listView = (ListView) findViewById(R.id.list);

            // Defined Array values to show in ListView
            String[] values = new String[venuesArray.size()] ;

            for(int i = 0; i< venuesArray.size();i++){
                values[i] = "Nimi:"+venuesArray.get(i).getName()+" Osoite:"+venuesArray.get(i).getAddress()+" Etäisyys:"+Double.toString(venuesArray.get(i).getDistance());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);
                    //textView.setText(result);

            listView.setAdapter(adapter);

            dialog.dismiss();
        }
    }

    String DEBUG_TAG = "tag_test_application";

    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String seachWord) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 1200;

        String urlText = GenerateUrl.clientId(CLIENT_ID, SECRED, VERSION, locationAsText, seachWord);

        try {
            URL url = new URL(urlText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = getASCIIContent(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    protected String getASCIIContent(InputStream is) throws IllegalStateException, IOException {
        InputStream in = is;
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * Creating google api client object
     * */


    /**
     * Method to display the location on UI
     */
    private void getLocation() {

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

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) != null) {
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }


        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            locationAsText = Double.toString(latitude).substring(0, 4) + "," + Double.toString(longitude).substring(0,4);

            //lblLocation.setText(latitude + ", " + longitude);

        } else {

            //lblLocation
            //      .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }


    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void onClick(View v) {

        String searchWord = seachWord.getText().toString();

        if(searchWord.length()<2)
            return;

        getLocation();

        //if(dialog!=null)
          //  dialog.dismiss();


        dialog.show();

        //dialog = ProgressDialog.show(getBaseContext(), "Loading", "Please wait...", true);

        new DownloadWebpageTask().execute(searchWord);
    }
}
