package com.example.eventcamp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class Myevents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myevents);

        //Tabs
        TabLayout tabs = (TabLayout) findViewById(R.id.myevents_tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.myevents_pagger);
        ViewPagerAdapter_Myevents viewPagerAdapterMyevents = new ViewPagerAdapter_Myevents(getSupportFragmentManager());
        viewPagerAdapterMyevents.addFragments(new Registered_Fragments(),"REGISTERED");
        viewPagerAdapterMyevents.addFragments(new Organized_Fragments(),"ORGANIZED");
        pager.setPageMargin(10);
        pager.setAdapter(viewPagerAdapterMyevents);
        tabs.setTabTextColors(Color.parseColor("#9FA8DA"),Color.parseColor("#ffffff"));
        tabs.setBackgroundColor(Color.parseColor("#3F51B5"));
        tabs.setupWithViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homescreen_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nav_profile:
                return true;
            case R.id.nav_addevent:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
