package com.marketside;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uoksmartsoko.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainSoko extends Fragment {
    private View view;
    private CardView mHouse,mComps,mElec,mOthers,mPhones;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_main_soko, container, false);
        mHouse = (CardView)view.findViewById(R.id.cardhouseholds);
        mComps = (CardView)view.findViewById(R.id.cardComputers);
        mElec = (CardView)view.findViewById(R.id.cardElectronics);
        mOthers = (CardView)view.findViewById(R.id.cardOthers);
        mPhones = (CardView)view.findViewById(R.id.cardphones);

        mHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ListTest.class);
                intent.putExtra("value","Households");
                startActivity(intent);
            }
        });
        mComps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ListTest.class);
                intent.putExtra("value","Computers");
                startActivity(intent);
            }
        });
        mElec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ListTest.class);
                intent.putExtra("value","Electronics");
                startActivity(intent);
            }
        });
        mOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ListTest.class);
                intent.putExtra("value","Others");
                startActivity(intent);
            }
        });
        mPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ListTest.class);
                intent.putExtra("value","Phones and Tablets");
                startActivity(intent);
            }
        });



        return view;
    }

}
