package com.xd.customviewpagerindicator.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.xd.customviewpagerindicator.R;
import com.xd.customviewpagerindicator.fragment.SimpleFragment;
import com.xd.customviewpagerindicator.view.CustomVI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CustomVI mCustomVI;
    private List<String> mDatas = Arrays.asList("短信", "收藏", "推荐");

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<SimpleFragment> mContents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDatas();
    }

    private void initViews() {
        mCustomVI = (CustomVI) findViewById(R.id.custom_vi);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mContents = new ArrayList<>();
    }

    private void initDatas() {
        for (String title : mDatas) {
            mContents.add(SimpleFragment.getInstance(title));
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };

        mViewPager.setAdapter(mAdapter);
    }
}
