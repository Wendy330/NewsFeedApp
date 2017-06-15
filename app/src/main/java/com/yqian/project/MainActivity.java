package com.yqian.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import layout.OneFragment;
import layout.ThreeFragment;
import layout.TwoFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ViewPagerAdapter adapter;

    private FirebaseAuth firebaseAuth;

    private final int STANDARD_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adapter.getItem(position).onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();

            startActivity(new Intent(this, Login.class));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ThreeFragment(), "HOME");
        adapter.addFragment(new OneFragment(), "MUSIC");
        adapter.addFragment(new TwoFragment(), "MOVIES");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()){
            case R.id.nav_item_login:
                Intent i = new Intent(this, Login.class);
                startActivityForResult(i, STANDARD_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String passMusicUrl = "";
    private String passMovieUrl = "";

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, OneFragment.class);

        switch (item.getItemId()){
            case R.id.nav_item_login:
                if (firebaseAuth.getCurrentUser() != null){
                    Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show();
                }
                Intent i = new Intent(this, Login.class);
                startActivityForResult(i, STANDARD_REQUEST_CODE);
                break;
            case R.id.nav_item_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.nav_item_favourites:
                startActivity(new Intent(this, Favourite.class));
                break;
            case R.id.itunes:
                final String MOVIES_URL_ITUNES = "https://itunes.apple.com/us/rss/topmovies/limit=10/genre=4401/xml";
                final String MUSIC_URL_ITUNES = "https://itunes.apple.com/us/rss/topalbums/limit=10/xml";
                passMusicUrl = MUSIC_URL_ITUNES;
                passMovieUrl = MOVIES_URL_ITUNES;
                break;
            case R.id.etOnline:
                final String MOVIES_URL_ET = "http://feeds.etonline.com/ETMovies";
                final String MUSIC_URL_ET = "http://feeds.etonline.com/ETMusic";
                passMusicUrl = MUSIC_URL_ET;
                passMovieUrl = MOVIES_URL_ET;
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    public String getPassMusicUrl(){
        return passMusicUrl;
    }
    public String getPassMovieUrl() { return passMovieUrl; }

}
