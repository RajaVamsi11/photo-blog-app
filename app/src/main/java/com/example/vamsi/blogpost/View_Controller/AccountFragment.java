package com.example.vamsi.blogpost.View_Controller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vamsi.blogpost.R;


/**
 * A simple {@link Fragment} subclass.
 */

public class  AccountFragment extends Fragment {

    //Account Fragment will show the details of the user who was loggedin ,the photo of
    //the user along the mane
    //This feature was not implemented as it was not a part of this assignment
    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

}