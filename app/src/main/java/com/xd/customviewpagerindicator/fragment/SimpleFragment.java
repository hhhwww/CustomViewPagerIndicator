package com.xd.customviewpagerindicator.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hhhhwei on 16/3/15.
 */
public class SimpleFragment extends android.support.v4.app.Fragment {

    public static final String BUNDLE_KEL = "title_key";
    private TextView textView;

    //用这种方法给Fragment传递参数
    public static SimpleFragment getInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEL, title);
        SimpleFragment simpleFragment = new SimpleFragment();
        simpleFragment.setArguments(bundle);
        return simpleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String title = bundle.getString(BUNDLE_KEL);
        textView = new TextView(getActivity());
        textView.setText(TextUtils.isEmpty(title) ? "无内容" : title);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
