package com.marketside;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ListTest extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private List<UploadItemList> uploadItemLists;
    private MarketAdapter marketAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);

        firebaseFirestore = FirebaseFirestore.getInstance();
        uploadItemLists = new ArrayList<>();
        marketAdapter = new MarketAdapter(uploadItemLists);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_list1);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe1);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ListTest.this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(marketAdapter);


        //setting the list

        Query query = firebaseFirestore.collection("TopDeals").orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(ListTest.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String postID = doc.getDocument().getId();
                        UploadItemList uploadItemList  = doc.getDocument().toObject(UploadItemList.class).withID(postID);

                        uploadItemLists.add(uploadItemList);
                        marketAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getApplicationContext(), "No posts", Toast.LENGTH_SHORT).show();
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

    }
}
