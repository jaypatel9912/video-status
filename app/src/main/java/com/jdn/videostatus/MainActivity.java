package com.jdn.videostatus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    ViewPager viewPager;
    AppBarLayout appBarLayout;
    MyPagerAdapter myPagerAdapter;
    TabLayout tabLayout;
    private InterstitialAd mInterstitialAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        if (Utils.getPreference(MainActivity.this, Constants.language).isEmpty()) {
            Utils.setPreference(MainActivity.this, Constants.language, Constants.ENGLISH);
        }

        appBarLayout = findViewById(R.id.appBarLayout);
        viewPager = findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        changeTabsFont();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setLatestTab();
    }

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/JosefinSans-Regular.ttf"));
                }
            }
        }
    }

    public void setLatestTab() {
        viewPager.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Globals.releasePlayer();

        if (mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }

        if (Utils.getIntPreference(MainActivity.this, Constants.AD_SHOW_COUNT) == 5)
            initInterstitialAd();

    }

    private void initInterstitialAd() {
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_Interstitial_ad_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("491ADC364AC32187B25F8B6C8FCCDD85").build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                mInterstitialAd = null;
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                mInterstitialAd = null;
                Utils.setIntPreference(MainActivity.this, Constants.AD_SHOW_COUNT, 0);
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                mInterstitialAd = null;
                Utils.setIntPreference(MainActivity.this, Constants.AD_SHOW_COUNT, 0);
            }
        });

    }
}
