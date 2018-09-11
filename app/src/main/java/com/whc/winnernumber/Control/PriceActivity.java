package com.whc.winnernumber.Control;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.android.gms.ads.AdView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whc.winnernumber.R;



public class PriceActivity extends Fragment {
    private ViewPager priceViewPager;
    private Activity context;
    private AdView adView;



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
        FragmentPagerItems pages = new FragmentPagerItems(context);
        pages.add(FragmentPagerItem.of("發票兌獎", PriceHand.class));
        pages.add(FragmentPagerItem.of("兌獎號碼", PriceNumber.class));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getFragmentManager(),pages);
        priceViewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(priceViewPager);
        adView = (AdView) view.findViewById(R.id.adView);
        Common.setAdView(adView, context);
        priceViewPager.setPageTransformer(true, new CubeOutTransformer());
        return view;
    }
}
