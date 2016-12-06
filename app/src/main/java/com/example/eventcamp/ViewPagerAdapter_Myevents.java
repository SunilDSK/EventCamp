package com.example.eventcamp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Sunilkumar on 07-11-2016.
 */

public class ViewPagerAdapter_Myevents extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabHeaders = new ArrayList<>();

    public void addFragments(Fragment fragment,String tabHeader){
        fragments.add(fragment);
        this.tabHeaders.add(tabHeader);
    }
    //Constructor
    public ViewPagerAdapter_Myevents(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabHeaders.get(position);
    }
}
