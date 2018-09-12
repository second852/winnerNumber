package com.whc.winnernumber.Control;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whc.winnernumber.R;

import java.util.HashMap;


public class PriceActivity extends Fragment {
    private ViewPager priceViewPager;
    private Activity context;
    private AdView adView;
    private ImageView menu;
    private RewardedVideoAd mRewardedVideoAd;






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.context=(Activity) context;
        }else{
            this.context=getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.price_main, container, false);
        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        priceViewPager = (ViewPager) view.findViewById(R.id.priceViewPager);

        //set Page
        FragmentPagerItems pages = new FragmentPagerItems(context);
        pages.add(FragmentPagerItem.of("發票兌獎", PriceHand.class));
        pages.add(FragmentPagerItem.of("兌獎號碼", PriceNumber.class));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getFragmentManager(),pages);
        priceViewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(priceViewPager);
        priceViewPager.setPageTransformer(true, new CubeOutTransformer());

        //set 廣告
        adView = (AdView) view.findViewById(R.id.adView);
        Common.setAdView(adView, context);
        //獎勵式廣告
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener(new showAds());
        loadRewardedVideoAd();
        //menu
        menu= (ImageView) view.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.menu_main);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.showAd:
                                mRewardedVideoAd.show();
                                break;
                            case R.id.closed:
                                context.finish();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        return view;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",new AdRequest.Builder().build());
    }

    private class showAds implements RewardedVideoAdListener {
        @Override
        public void onRewardedVideoAdLoaded() {

        }

        @Override
        public void onRewardedVideoAdOpened() {

        }

        @Override
        public void onRewardedVideoStarted() {

        }

        @Override
        public void onRewardedVideoAdClosed() {
            loadRewardedVideoAd();
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {

        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            loadRewardedVideoAd();
        }
    }
}
