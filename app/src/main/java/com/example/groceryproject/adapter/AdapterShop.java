package com.example.groceryproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryproject.Model.ModelShops;
import com.example.groceryproject.R;
import com.example.groceryproject.activites.ShopDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop>{
    private Context context;
    public ArrayList<ModelShops>shopsList;

    public AdapterShop(Context context, ArrayList<ModelShops> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout
       .row_shop,parent,false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
      //getdata
        ModelShops modelShops = shopsList.get(position);
        String accountType = modelShops.getAccountType();
        String address = modelShops.getAddress();
        String city = modelShops.getCity();
        String country = modelShops.getCountry();
        String deliveryfee = modelShops.getDeliveryfee();
        String email = modelShops.getEmail();
        String latitude = modelShops.getLatitude();
        String longitude = modelShops.getLongitude();
        String online = modelShops.getOnline();
        String name = modelShops.getName();
        String phone = modelShops.getPhone();
        String uid = modelShops.getUid();
        String timestamp = modelShops.getTimestamp();
        String shopopen = modelShops.getShopOpen();
        String state = modelShops.getState();
        String profileImage = modelShops.getProfileImage();
        String shopName  = modelShops.getShopname();
        //setText
        holder.shopNameTV.setText(shopName);
        holder.phoneTV.setText(phone);
        holder.addressTV.setText(address);
        if(online.equals("true")){
            holder.onlineIV.setVisibility(View.VISIBLE);
        }
        else{
            holder.onlineIV.setVisibility(View.GONE);
        }
        //CHECK IF SHOP IS OPEN
        if(shopopen.equals("true")){
            holder.shopClosedTV.setVisibility(View.GONE);
        }
        else{
            holder.shopClosedTV.setVisibility(View.VISIBLE);
        }
        try{
            Picasso.get().load(profileImage).placeholder(
                    R.drawable.ic_store_gray
            ).into(holder.shopIV);

        }catch (Exception e){
            holder.shopIV.setImageResource(R.drawable.ic_store_gray);
        }

     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(context, ShopDetailActivity.class);
             intent.putExtra("shopUid",uid);
             context.startActivity(intent);
         }
     });



    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }


    //view Holder
    class HolderShop extends RecyclerView.ViewHolder{

        private ImageView shopIV,onlineIV,nextIV;
        private TextView shopClosedTV,shopNameTV,phoneTV,addressTV;
        private RatingBar ratingBar;



        public HolderShop(@NonNull View itemView) {

            super(itemView);
            shopIV = itemView.findViewById(R.id.shopIV);
            onlineIV = itemView.findViewById(R.id.onlineIV);
            shopClosedTV = itemView.findViewById(R.id.shopClosedTV);
            shopNameTV = itemView.findViewById(R.id.shopNameTV);
            phoneTV = itemView.findViewById(R.id.phoneTV);
            addressTV = itemView.findViewById(R.id.addressTV);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            nextIV = itemView.findViewById(R.id.nextIV);

        }
    }
}
