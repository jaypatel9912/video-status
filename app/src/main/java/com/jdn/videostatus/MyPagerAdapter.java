package com.jdn.videostatus;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = CategoryFragment.newInstance();
                break;
            case 1:
                fragment =  VideosFragment.newInstance(true);
                break;
            case 2:
                fragment =  VideosFragment.newInstance(false);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Category";
            case 1:
                return "Latest";
            case 2:
                return "Popular";
            default:
                return null;
        }
    }

}
