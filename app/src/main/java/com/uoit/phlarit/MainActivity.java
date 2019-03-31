package com.uoit.phlarit;

/**
 * This is an application that makes use of Fragments
 * and the navigation drawer to create a simple UI
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Any fragment used inside the activity must have its
 * onFragment InteractionListener implemented
 */
public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, PhotoFragment.OnFragmentInteractionListener {

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
    PhotoFragment photoFragment;
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instatiate Volley queue
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

                fragment = photoFragment;
                break;

            case R.id.nav_send:

                sendData();
                fragment = photoFragment;
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


    public void sendData() {

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

        try {

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

    }


    public void checkPermissions(Activity activity) {
        PackageManager packMan = activity.getPackageManager();
        int hasWritePermission = packMan.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity.getPackageName());
        int hasRecordPermission = packMan.checkPermission(Manifest.permission.RECORD_AUDIO, activity.getPackageName());
        int hasFineLocationPermission = packMan.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, activity.getPackageName());
        int hasCoarseLocationPermission = packMan.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, activity.getPackageName());
        int hasInternetPermission = packMan.checkPermission(Manifest.permission.INTERNET, activity.getPackageName());
        int hasCameraPermission = packMan.checkPermission(Manifest.permission.CAMERA, activity.getPackageName());

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


        } /* If the API is lower than 23, we cannot use runtime permission statements, so we must check to see if permission has been granted. */


    }

}













