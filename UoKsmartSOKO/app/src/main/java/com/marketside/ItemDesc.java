package com.marketside;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.uoksmartsoko.MainActivity;
import com.example.uoksmartsoko.R;

public class ItemDesc extends AppCompatActivity {
    private ImageView mPosted;
    private TextView mPrice,mTitle,mDesc,mPhone;
    private String title,price,phone,desc,imageUrl;
    private FloatingActionButton mCall,mWhatsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_desc);
        mPhone = (TextView)findViewById(R.id.tv_contact_desc);
        mTitle = (TextView)findViewById(R.id.tv_title_desc);
        mDesc = (TextView)findViewById(R.id.tv_desc_desc);
        mPrice = (TextView)findViewById(R.id.tv_price_desc);
        mPosted = (ImageView)findViewById(R.id.emage_imageposted);
        mCall = (FloatingActionButton)findViewById(R.id.floatingCall);
        mWhatsApp = (FloatingActionButton)findViewById(R.id.floatingWhatsApp);

        imageUrl = getIntent().getExtras().getString("imageUrl");
        title = getIntent().getExtras().getString("title");
        price = getIntent().getExtras().getString("price");
        phone = getIntent().getExtras().getString("phone");
        desc = getIntent().getExtras().getString("desc");

        //setting the values
        //the image
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        Glide.with(ItemDesc.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mPosted);

        //other values
        mPrice.setText("KSH "+price);
        mTitle.setText(title);
        mDesc.setText("Item description:\n"+desc);
        mPhone.setText("+254"+phone);
        mPhone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        setTitle(title);

        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phone;
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+number));
                startActivity(callIntent);
            }
        });
        mWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp(v);
            }
        });
    }

    public void openWhatsApp(View view){
        PackageManager pm=getPackageManager();
        try {


            String toNumber = phone; // Replace with mobile phone number without +Sign or leading zeros.
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(ItemDesc.this,"Please you need WhatsApp Messenger to use this option",Toast.LENGTH_LONG).show();

        }
    }
}
