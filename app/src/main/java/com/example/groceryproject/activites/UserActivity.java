package com.example.groceryproject.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryproject.Model.ModelShops;
import com.example.groceryproject.Model.ModelUserProducts;
import com.example.groceryproject.R;
import com.example.groceryproject.adapter.AdapterShop;
import com.example.groceryproject.adapter.Adapterorderdetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserActivity extends AppCompatActivity {
    private  TextView name,EmailTV,PhoneTV,tabshopTV,taborderTV;
    private RelativeLayout shopRL,orderRL;
    private ImageView profileIV;
    private ImageButton power;
    private FirebaseAuth firebaseAuth;
    private  ImageButton edit;
    private RecyclerView shopRV , ordersRV;


private ArrayList<ModelShops> shopList;
private AdapterShop adapterShop;

  ArrayList<ModelUserProducts> orderlist;
 Adapterorderdetail adapterUserProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        name = findViewById(R.id.nameTV);
        EmailTV = findViewById(R.id.EmailTV);
        PhoneTV =findViewById(R.id.PhoneTV);
        tabshopTV=findViewById(R.id.tabShopTV);
        taborderTV=findViewById(R.id.tabOrderTV);
        profileIV = findViewById(R.id.profileIV);
        shopRL = findViewById(R.id.shopRL);
        orderRL = findViewById(R.id.orderRL);
        power = findViewById(R.id.power);
        edit = findViewById(R.id.edit);
        shopRV = findViewById(R.id.shopRV);
        ordersRV = findViewById(R.id.ordersRV);
        shopRV.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserActivity.this ,LinearLayoutManager.VERTICAL,false);
        ordersRV.setLayoutManager(linearLayoutManager);


        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        showshopui();
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this , Edit_user_profile.class));
            }
        });
        tabshopTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showshopui();
            }
        });
        taborderTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showorderui();
            }
        });
    }
       private void checkUser() {
        FirebaseUser seller = firebaseAuth.getCurrentUser();
        if(seller == null){
            startActivity(new Intent(UserActivity.this , LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String names = ""+ds.child("name").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String email = ""+ds.child("email").getValue();
                            String Profileimage = "" + ds.child("ProfileImage").getValue();
                            String city = "" + ds.child("city").getValue();

                            String accountType = ""+ds.child("accountType").getValue();
                            name.setText(names);
                            PhoneTV.setText(phone);
                            EmailTV.setText(email);
                            try {
                                Picasso.get().load(Profileimage).placeholder(R.drawable.ic_store_gray).into(profileIV);
                            } catch (Exception e) {
                                profileIV.setImageResource(R.drawable.ic_store_gray);

                            }

                            loadshops(city);
                          loaders();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loaders() {
        orderlist = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderlist.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("OrderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {

                                            ModelUserProducts modelUserProducts = ds.getValue(ModelUserProducts.class);
                                            orderlist.add(modelUserProducts);

                                        }
                                        adapterUserProduct = new Adapterorderdetail(UserActivity.this, orderlist);
                                        ordersRV.setAdapter(adapterUserProduct);


                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadshops(String city) {
        shopList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType").equalTo("seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            ModelShops modelShops = ds.getValue(ModelShops.class);
                            String shopcity = ""+ds.child("city").getValue();
                            if(shopcity.equals(shopcity)){
                                shopList.add(modelShops);
                            }
                           //if dispaly all shop skip the if statment
                           // shopList.add(modelShops);
                            adapterShop = new AdapterShop(UserActivity.this,shopList);
                            shopRV.setAdapter(adapterShop);;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showshopui() {
        //show products and hide
        shopRL.setVisibility(View.VISIBLE);
        orderRL.setVisibility(View.GONE);

        tabshopTV.setTextColor(getResources().getColor(R.color.black));
        tabshopTV.setBackgroundResource(R.drawable.shape_rectangle_04);

        taborderTV.setTextColor(getResources().getColor(R.color.white));
        taborderTV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showorderui() {
        //show order and hide
        orderRL.setVisibility(View.VISIBLE);
        shopRL.setVisibility(View.GONE);


        tabshopTV.setTextColor(getResources().getColor(R.color.white));
        tabshopTV.setBackgroundColor(getResources().getColor(android.R.color.transparent));


        taborderTV.setTextColor(getResources().getColor(R.color.black));
        taborderTV.setBackgroundResource(R.drawable.shape_rectangle_04);


    }
}