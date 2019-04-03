package com.uoit.phlarit;

/**
 * This is an application that makes use of Fragments
 * and the navigation drawer to create a simple UI
 */

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Any fragment used inside the activity must have its
 * onFragment InteractionListener implemented
 */
public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener {

    File fileSend;
    Uri path = Uri.parse("android.resource://com.uoit.phlarit/" + R.drawable.orangutanupload3);
    String fileName = path.toString();
    public Bitmap globalBitmap;
    public String encodedString;

    int serverResponseCode = 0;
    // Volley Requestqueue is used later to send data to the postgres db
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

    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("fileName as a string", fileName);

        // Instatiate Volley queue
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
        checkPermissions(this);

        /* Create instances of the fragments */
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

    /**
     * Must be implemented or the code wont build
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("FragmentInteraction", "In FragmentInteraction");
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

                Intent photoIntent = new Intent(MainActivity.this, CameraActivity.class);
                MainActivity.this.startActivity(photoIntent);

                //fragment = photoFragment;
                break;

            case R.id.nav_send:


                Math.random();
                Random rand = new Random();
                int n = rand.nextInt(100);


//                try {
//                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.orangutanupload3);
//                    globalBitmap = bm;
//                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//                    fileSend = new File(extStorageDirectory, "orangutanupload3.png");
//                    FileOutputStream outStream = new FileOutputStream(fileSend);
//                    bm.compress(Bitmap.CompressFormat.PNG, 50, outStream);
//                    outStream.flush();
//                    outStream.close();
//                    File f_path = new File(extStorageDirectory + "/orangutanupload3.png");
//                    InputStream fis = null;
//                    fis = new BufferedInputStream(new FileInputStream(f_path));
//                    File file2 = new File(Environment.getExternalStorageDirectory().toString() + "orangutanupload3.png");
//
//                    Log.d("After uploadfile", "after uploadfile");
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//
//                uploadImageMethod();
                new UploadFileAsync().execute("");

                break;

            case R.id.nav_login:
                Log.d("nav_login", "Login Pressed");
                break;

            case R.id.nav_signup:
                Log.d("nav_signup", "Sign Up Pressed");
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




    public void uploadImageMethod() {

        String uploadString = getStringImage(globalBitmap);
        encodedString = uploadString;
        sendImage(uploadString);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    String theurl = "http://ec2-54-160-8-114.compute-1.amazonaws.com/receiveImage.php";

    private void sendImage(final String image) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, theurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("upload", response);
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("image", image);
                return params;
            }
        };

        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }


    private class UploadFileAsync extends AsyncTask<String, Void, String> {

        public String imageString;

        public String getStringImage(Bitmap bmp) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }

        public void sendData() {

            try {
                Log.d("In catch block", "123");
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.orangutanupload3);
                globalBitmap = bm;
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                fileSend = new File(extStorageDirectory, "orangutanupload3.png");
                FileOutputStream outStream = new FileOutputStream(fileSend);
                bm.compress(Bitmap.CompressFormat.PNG, 50, outStream);
                outStream.flush();
                outStream.close();
                File f_path = new File(extStorageDirectory + "/orangutanupload3.png");
                InputStream fis = null;
                fis = new BufferedInputStream(new FileInputStream(f_path));
                File file2 = new File(Environment.getExternalStorageDirectory().toString() + "orangutanupload3.png");

                this.imageString = getStringImage(bm);

            } catch (Exception e) {

                e.printStackTrace();
            }

            Log.d("After uploadfile", "after uploadfile");

            final Context context = getApplicationContext();

            // Get id
            final String id = UUID.randomUUID().toString();

            // Get time
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy-hh-mm-ss");
            String format = simpleDateFormat.format(new Date());
            final String time = format;
            final String imageUpload = this.imageString;

            // Get imageName
            final String imageName = "orangutanupload3.png";

            // Get locale
            final String locale = context.getResources().getConfiguration().locale.getCountry();

            try {
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
                        params.put("id", id);
                        params.put("image", imageUpload);
                        params.put("time", time);
                        params.put("locale", locale);

                        return params;

                    }
                };
                requestQueue.add(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final Context context = getApplicationContext();

        protected String doInBackground(String... params) {
            //uploadFile(new File(Environment.getExternalStorageDirectory().toString() + "orangutanupload3.png"));
            sendData();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    public void checkPermissions(Activity activity) {
        PackageManager packMan = activity.getPackageManager();
        int hasWritePermission = packMan.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity.getPackageName());
        int hasRecordPermission = packMan.checkPermission(Manifest.permission.RECORD_AUDIO, activity.getPackageName());
        int hasFineLocationPermission = packMan.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, activity.getPackageName());
        int hasCoarseLocationPermission = packMan.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, activity.getPackageName());
        int hasInternetPermission = packMan.checkPermission(Manifest.permission.INTERNET, activity.getPackageName());
        int hasCameraPermission = packMan.checkPermission(Manifest.permission.CAMERA, activity.getPackageName());
        int hasReadPermission = packMan.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, activity.getPackageName());

        /* If the API is  greater than 22, we can use runtime permission statements. */
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
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
            switch (hasCameraPermission) {

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
            switch (hasReadPermission) {

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
}













