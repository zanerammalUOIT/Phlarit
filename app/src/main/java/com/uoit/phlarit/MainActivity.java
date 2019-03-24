package com.uoit.phlarit;

/**
 * This is an application that makes use of Fragments
 * and the navigation drawer to create a simple UI
 */

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Any fragment used inside the activity must have its
 * onFragment InteractionListener implemented
 */
public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, PhotoFragment.OnFragmentInteractionListener {


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

                Context context = getApplicationContext();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                Toast toast = Toast.makeText(context, format, duration);
                toast.show();
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

    // -------------------------------

}
