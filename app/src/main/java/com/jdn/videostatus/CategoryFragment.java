package com.jdn.videostatus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.util.ArrayList;


public class CategoryFragment extends Fragment {

    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    View view;
    BoomMenuButton bmb;
    TextView tvLanguage;
    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, container, false);
        tvLanguage = view.findViewById(R.id.tvLanguage);
        bmb = view.findViewById(R.id.bmb);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("491ADC364AC32187B25F8B6C8FCCDD85").build();
        mAdView.loadAd(adRequest);

        final ArrayList<String> categories = new ArrayList<>();
        categories.add(Constants.ENGLISH);
        categories.add(Constants.HINDI);
        categories.add(Constants.GUJARATI);
        categories.add(Constants.PUNJABI);
        categories.add(Constants.KANNADA);
        categories.add(Constants.TAMIL);
        categories.add(Constants.MARATHI);

        for (int i = 0; i < 7; i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalText(categories.get(i))
                    .textGravity(Gravity.CENTER)
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            Constants.IS_CATEGORY_CHANGED_LATEST = true;
                            Constants.IS_CATEGORY_CHANGED_POPULAR = true;
                            Utils.setPreference(getActivity(), Constants.language, categories.get(index));
                            setUserVisibleHint(true);
                            try {
                                ((MainActivity) getActivity()).setLatestTab();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
            bmb.addBuilder(builder);
        }

        return view;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isAdded() && isVisibleToUser) {
            try {
                tvLanguage.setText(Utils.getPreference(getActivity(), Constants.language));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
