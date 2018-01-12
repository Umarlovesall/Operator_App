package com.moadd.operatorApp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.moaddi.operatorApp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Policy extends Fragment {
WebView wv;

    public Policy() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_policy, container, false);
        wv= (WebView) v.findViewById(R.id.wv);
        wv.loadUrl("https://sites.google.com/view/moaddiapp/home");
        return v;
    }

}
