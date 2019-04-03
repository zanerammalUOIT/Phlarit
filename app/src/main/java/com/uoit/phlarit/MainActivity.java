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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

import java.util.Date;
import java.util.HashMap;
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


                try {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.orangutanupload3);
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
                    uploadFile(file2);

                } catch (Exception e) {
                    e.printStackTrace();
                }




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


    public int uploadFile(File uploadImage) {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = uploadImage;

        String sourceFileUri = Environment.getExternalStorageDirectory().toString() + "orangutanupload3.png";

        if (sourceFile.isFile()) {

            try {
                String upLoadServerUri = "http://ec2-54-160-8-114.compute-1.amazonaws.com/images";

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE",
                        "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("bill", sourceFileUri);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                        + sourceFileUri + "\"" + lineEnd);

                dos.writeBytes(lineEnd);
                Log.d("After write bytes", "Ok");

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math
                            .min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0,
                            bufferSize);
                }

                // send multipart form data necesssary after file
                // data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens
                        + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn
                        .getResponseMessage();

                if (serverResponseCode == 200) {

                    // messageText.setText(msg);
                    Toast.makeText(getApplicationContext(), "File Upload Complete.", Toast.LENGTH_SHORT).show();

                    // recursiveDelete(mDirectory1);

                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (Exception e) {

                // dialog.dismiss();
                e.printStackTrace();

            }
            // dialog.dismiss();


            return 0;
        }

        return 0;
    }


    private class UploadFileAsync extends AsyncTask<String, Void, String> {


        public void sendData() {

            final Context context = getApplicationContext();

            // Get id
            final String id = UUID.randomUUID().toString();

            // Get time
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy-hh-mm-ss");
            String format = simpleDateFormat.format(new Date());
            final String time = format;

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
                        params.put("time", time);
                        params.put("locale", locale);
                        params.put("imageName", imageName);
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
            uploadFile(new File(Environment.getExternalStorageDirectory().toString() + "orangutanupload3.png"));
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













