package com.example.uoksmartsoko;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.marketside.AllItems;
import com.marketside.ListTest;
import com.marketside.MainSoko;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private View view;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        view = new View(this);

        checkinUserAccount();

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(Home.this,Upload.class));
            }
        });

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setting user details
        try{
            settingUserDetails(navigationView);
        }catch (Exception e){

            Toast.makeText(this, "Error\n"+e.getMessage(), Toast.LENGTH_SHORT).show();

        }



        toolbar.setTitle("Top Deals");

        toolbar.setTitle("Top Deals");
        AllItems allItems = new AllItems();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main,allItems).commitNow();
        //startActivity(new Intent(Home.this,ListTest.class));
    }

    private void settingUserDetails(NavigationView naviagationView){
        View view =  naviagationView.getHeaderView(0);

        final CircleImageView profile = (CircleImageView)view.findViewById(R.id.image_profile_home);
        final TextView username = (TextView)view.findViewById(R.id.user_fullname);
        final TextView phone = (TextView)view.findViewById(R.id.user_phone);

        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String fname = task.getResult().getString("fname").toLowerCase();
                                String lname = task.getResult().getString("lname").toLowerCase();
                                String Phone = task.getResult().getString("phone");
                                String imageUrl  = task.getResult().getString("imageUrl");

                                //setting values
                                username.setText(fname+" "+lname);
                                phone.setText(Phone);

                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();
                                Glide.with(Home.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(profile);
                            }
                        }else {
                            Toast.makeText(Home.this, "No details...\n"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkinUserAccount();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuLogout) {
            final AlertDialog.Builder builder  = new AlertDialog.Builder(this);
            builder.setTitle("Exiting");
            builder.setMessage("Are sure you want to exit?");
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    FirebaseAuth.getInstance().signOut();
                    finish();
                    startActivity(new Intent(Home.this, Login.class));
                }
            });
            builder.show();
        }

        if (id == R.id.action_addpost){
            startActivity(new Intent(Home.this,Upload.class));
        }
        if (id == R.id.action_myaccount){
            startActivity(new Intent(Home.this,MyUserDetails.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_market) {

            toolbar.setTitle("Top Deals");
            AllItems allItems = new AllItems();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main,allItems).commitNow();
            //startActivity(new Intent(Home.this,ListTest.class));

        } else if (id == R.id.nav_account) {
            startActivity(new Intent(Home.this,MyUserDetails.class));

        }else if (id == R.id.nav_share){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out on UoK SmartSoko app on: https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkinUserAccount(){
        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                    }else {

                        startActivity(new Intent(Home.this,MyUserDetails.class));

                    }
                }else {
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkinUserAccount();
    }
}
