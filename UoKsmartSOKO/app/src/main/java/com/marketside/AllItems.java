package com.marketside;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.uoksmartsoko.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllItems extends Fragment {
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private List<UploadItemList> uploadItemLists;
    private MarketAdapter marketAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;


    public AllItems() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_all_items, container, false);uploadItemLists = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        marketAdapter = new MarketAdapter(uploadItemLists);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_list);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(marketAdapter);


        //setting the list

        Query query = firebaseFirestore.collection("TopDeals").orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String postID = doc.getDocument().getId();
                        UploadItemList uploadItemList  = doc.getDocument().toObject(UploadItemList.class).withID(postID);

                        uploadItemLists.add(uploadItemList);
                        marketAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getContext(), "No posts", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(marketAdapter);
                    }
                },300);
            }
        });

        return view;
    }

}
