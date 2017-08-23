package com.wang.module.test.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wang.module.test.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LelfFragment extends Fragment {


    public LelfFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lelf, container, false);
    }

}
