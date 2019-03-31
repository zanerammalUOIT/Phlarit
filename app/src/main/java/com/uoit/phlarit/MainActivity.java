package com.uoit.phlarit;

/**
 * This is an application that makes use of Fragments
 * and the navigation drawer to create a simple UI
 */


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import org.json.JSONArray;
import org.json.simple.parser.JSONParser;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Any fragment used inside the activity must have its
 * onFragment InteractionListener implemented
 */
public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, PhotoFragment.OnFragmentInteractionListener {

    // Link to php file

    String phpurl = "http://ec2-54-160-8-114.compute-1.amazonaws.com/addRecord.php";
    RequestQueue requestQueue;

    /* The 'hamburger' icon that opens the navigation drawer*/
    private ActionBarDrawerToggle drawerToggle;
    /* The bar in the top right*/
    private Toolbar toolbar;
    /*  Related to the design of navigation drawer*/
    public DrawerLayout drawerLayout;
    /* Related to the design of navigation drawer*/
    private NavigationView navigationView;

    /* Make all Toasts short*/
    int duration = Toast.LENGTH_SHORT;

    /* Declare fragments here so they can be initialized when needed*/
    PhotoFragment photoFragment;
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
        checkPermissions(this);

        /* Create instances of the fragments */
        photoFragment = PhotoFragment.newInstance("0", ("0"));
        homeFragment = HomeFragment.newInstance("0", "0");

        /* Instatiate variables that are needed*/
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        /* This is launching the HomeFragment when the activity is created. */
        /* Removes any fragment that may be loaded already, then loads the StartFragment*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, homeFragment).commit();

        /* Configure the Navigation Drawer*/

        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        setupDrawerContent(navigationView);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll("JSONObject");
        }
    }


    // -------------------------------

    /**
     * Must be implemented or the code wont build
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {


    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    /* Because the fragments were instatiated in onCreate(), they can
     * be opened during runtime. Here I map each button on the Navigation drawer to
     * a fragment
     * */
    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.nav_main:
                fragment = homeFragment;
                break;
            case R.id.nav_photo:
                fragment = photoFragment;
                break;
            case R.id.nav_send:


                // Get timestamp
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                final String time = format;

                /* Use SecureRandom to generate an ID */
                SecureRandom random = new SecureRandom();
                byte bytes[] = new byte[100];
                random.nextBytes(bytes);
                Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
                String token = encoder.encodeToString(bytes);

                final String id = token;


                // For testing purposes just turn a string into a byte array
                // it will appear as noise if turned into an image
                final String byteStr = "Placeholder";
                //byte[] byteArr = byteStr.getBytes();
                //final String image = byteArr.toString();


                try {

                    // Attempt to get whale iamge

                    // Get whale image as byte array
//                    String whaleUrl = "https://cdn.pixabay.com/photo/2013/02/10/00/20/humpback-79855_960_720.jpg";
//                    URL imgUrl = new URL(whaleUrl);
//                    URLConnection connection = imgUrl.openConnection();
//                    connection.setConnectTimeout(5000);
//                    connection.setReadTimeout(5000);
//                    connection.connect();
//                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    IOUtils.copy(connection.getInputStream(), byteArrayOutputStream);
                    // final String image = byteArrayOutputStream.toString();
                    final Context context = getApplicationContext();
                    final String locale = context.getResources().getConfiguration().locale.getCountry();

                    String url = "http://ec2-54-160-8-114.compute-1.amazonaws.com/addRecord.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("onResponse", response.toString());
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.e("ErrorResponse", error.toString());
                                }

                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("id", id.trim());
                            params.put("image", "Test".trim());
                            params.put("time", time.trim());
                            params.put("locale", locale.trim());
                            return params;

                        }

                    };


                    stringRequest.setTag("JSONOBject");
                    requestQueue.add(stringRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragment = photoFragment;
                break;

            default:
                fragment = homeFragment;
                break;
        }

        /* Removes any fragment that may be loaded already, then loads appropriate fragment */

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();


        } else {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, homeFragment).commit();

        }

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();

    }

    /* Methods*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void checkPermissions(Activity activity) {
        PackageManager packMan = activity.getPackageManager();
        int hasWritePermission = packMan.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity.getPackageName());
        int hasRecordPermission = packMan.checkPermission(Manifest.permission.RECORD_AUDIO, activity.getPackageName());
        int hasFineLocationPermission = packMan.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, activity.getPackageName());
        int hasCoarseLocationPermission = packMan.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, activity.getPackageName());
        int hasInternetPermission = packMan.checkPermission(Manifest.permission.INTERNET, activity.getPackageName());

        /* If the API is  greater than 22, we can use runtime permission statements. */
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1000);

        } /* If the API is  greater than 22, we can use runtime permission statements. */

        /* If the API is lower than 23, we cannot use runtime permission statements, so we must check to see if permission has been granted. */
        if (Build.VERSION.SDK_INT < 23) {

            switch (hasWritePermission) {

                case ((PackageManager.PERMISSION_GRANTED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Write permission is granted.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }

                case ((PackageManager.PERMISSION_DENIED)): {

                    Context context = getApplicationContext();
                    CharSequence text = "Write permission is denied. Application will not function correctly.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }

                default:

                    Context context = getApplicationContext();
                    CharSequence text = "Default statement reached. Application may not function correctly.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
            }

            switch (hasRecordPermission) {

                case ((PackageManager.PERMISSION_GRANTED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Record permission is granted.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    break;
                }
                case ((PackageManager.PERMISSION_DENIED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Record permission is denied. Application will not function correctly.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                default:
                    // warn that default statement reached
                    break;
            }

            switch (hasFineLocationPermission) {

                case ((PackageManager.PERMISSION_GRANTED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Fine Location permission is granted.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    break;
                }
                case ((PackageManager.PERMISSION_DENIED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Fine Location permission is denied. Application will not function correctly.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                default:
                    // warn that default statement reached
                    break;
            }

            switch (hasCoarseLocationPermission) {

                case ((PackageManager.PERMISSION_GRANTED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Coarse Location permission is granted.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    break;
                }
                case ((PackageManager.PERMISSION_DENIED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Coarse Location permission is denied. Application will not function correctly.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                default:
                    // warn that default statement reached
                    break;
            }

            switch (hasInternetPermission) {

                case ((PackageManager.PERMISSION_GRANTED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Coarse Location permission is granted.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    break;
                }
                case ((PackageManager.PERMISSION_DENIED)): {
                    Context context = getApplicationContext();
                    CharSequence text = "Coarse Location permission is denied. Application will not function correctly.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                default:
                    // warn that default statement reached
                    break;
            }


        } /* If the API is lower than 23, we cannot use runtime permission statements, so we must check to see if permission has been granted. */


    }

//    public class PostDataAsyncTask extends AsyncTask<String, String> {
//
//
//        @Override
//        protected String doInBackground(Void... params) {
//            try {
//
//
//
//                /* For testing purposes, convert a Pixabay image into a byte
//                 * array and put it in the databse
//                 */
//
//                // Get timestamp
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy-hh-mm-ss");
//                String format = simpleDateFormat.format(new Date());
//                final String time = format;
//
//                /* Use SecureRandom to generate an ID */
//                SecureRandom random = new SecureRandom();
//                byte bytes[] = new byte[100];
//                random.nextBytes(bytes);
//                Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
//                String token = encoder.encodeToString(bytes);
//
//                final String id = token;
//
//
//                String whaleUrl = "https://cdn.pixabay.com/photo/2013/02/10/00/20/humpback-79855_960_720.jpg";
//                URL imgUrl = new URL(whaleUrl);
//                URLConnection connection2 = imgUrl.openConnection();
//                connection2.setConnectTimeout(5000);
//                connection2.setReadTimeout(5000);
//                connection2.connect();
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                IOUtils.copy(connection2.getInputStream(), byteArrayOutputStream);
//                final String image = byteArrayOutputStream.toString();
//                Log.d("WhaleByteArray", image);
//
//                String postReceiverUrl = "http://ec2-54-160-8-114.compute-1" +
//                        ".amazonaws.com/addRecord.php";
//                URL url = new URL(postReceiverUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setReadTimeout(10000);
//                connection.setConnectTimeout(15000);
//                connection.setRequestMethod("POST");
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//
//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("id", id)
//                        .appendQueryParameter("image", image)
//                        .appendQueryParameter("time", time);
//
//                String query = builder.build().getEncodedQuery();
//
//                OutputStream outputStream = connection.getOutputStream();
//                BufferedWriter bufferedWriter =
//                        new BufferedWriter(new OutputStreamWriter(outputStream,
//                                "UTF-8"));
//                bufferedWriter.write(query);
//                bufferedWriter.flush();
//                bufferedWriter.close();
//                outputStream.close();
//
//
//                connection.connect();
//
//
//            } catch (Exception e) {
//
//
//            }
//
//
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String o) {
//            super.onPostExecute(o);
//        }
    //}


}













