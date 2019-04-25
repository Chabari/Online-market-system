package com.marketside;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.uoksmartsoko.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {

    private List<UploadItemList> uploadItemLists;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth auth;

    public MarketAdapter(List<UploadItemList> uploadItemLists) {
        this.uploadItemLists = uploadItemLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_display,viewGroup,false);
       context = viewGroup.getContext();
       auth = FirebaseAuth.getInstance();
       firebaseFirestore = FirebaseFirestore.getInstance();
       user_id = auth.getCurrentUser().getUid();
       return new MarketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {


        //setting other values to their respective item views
        final String title = uploadItemLists.get(i).getTitle();
        final String price = uploadItemLists.get(i).getPrice();
        String pricebefore = uploadItemLists.get(i).getPricebefore();
        final String image_url = uploadItemLists.get(i).getImageUrl();
        final String desc = uploadItemLists.get(i).getDesc();
        final String phone = uploadItemLists.get(i).getPhone();

        viewHolder.setImagePosted(image_url);
        viewHolder.setTitle(title);
        viewHolder.setPrice(price);
        viewHolder.setPriceBefore(pricebefore);

        viewHolder.ImagePosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(v.getContext(),ItemDesc.class);
                intent.putExtra("imageUrl",image_url);
                intent.putExtra("title",title);
                intent.putExtra("price",price);
                intent.putExtra("desc",desc);
                intent.putExtra("phone",phone);
                v.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return uploadItemLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView Title,Price,PriceBefore;
        private ImageView ImagePosted;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ImagePosted = (ImageView) view.findViewById(R.id.image_posted_owner);
        }

        public void setTitle(String title){
            Title = (TextView)view.findViewById(R.id.tv_title_owner);
            Title.setText(title);
        }

        public void setPrice(String price){
            Price = (TextView)view.findViewById(R.id.tv_price_owner);
            Price.setText("KSH "+price);
        }

        public void setPriceBefore(String price){
            PriceBefore = (TextView)view.findViewById(R.id.tv_pricebefore_owner);
            PriceBefore.setText("KSH "+price);
            PriceBefore.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        public void setImagePosted(String imageUrl){
            ImagePosted = (ImageView) view.findViewById(R.id.image_posted_owner);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(ImagePosted);
        }
    }
}
