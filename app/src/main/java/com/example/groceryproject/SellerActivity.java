package com.example.groceryproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SellerActivity extends AppCompatActivity {
 private TextView name , tabproductstv,tabordertv;
 private ImageButton power;
 private FirebaseAuth firebaseAuth;
 private ImageButton edit , addproduct;
 private ImageView profile;
 TextView shopname,email;
 private ProgressDialog progressDialog;
 RelativeLayout productRL , orderRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        name = findViewById(R.id.name);
        power = findViewById(R.id.power);
        edit = findViewById(R.id.edit);
        shopname = findViewById(R.id.shopname);
        addproduct = findViewById(R.id.addproduct);
        email = findViewById(R.id.Email);
        profile = findViewById(R.id.profile);
        tabordertv=findViewById(R.id.tabOrderTv);
        tabproductstv=findViewById(R.id.tabProductsTv);
        productRL=findViewById(R.id.productRL);
        orderRL=findViewById(R.id.orderRL);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        showproductui();

        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makemeoffline();

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerActivity.this , Edit_seller_profile.class));
            }
        });
        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerActivity.this , Addproduct.class));
            }
        });
        tabproductstv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showproductui();
            }
        });
        tabordertv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             showorderui();
            }
        });
    }

    private void showproductui() {
        //show products and hide
        productRL.setVisibility(View.VISIBLE);
        orderRL.setVisibility(View.GONE);

        tabproductstv.setTextColor(getResources().getColor(R.color.black));
        tabproductstv.setBackgroundResource(R.drawable.shape_rectangle_04);

        tabordertv.setTextColor(getResources().getColor(R.color.white));
        tabordertv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showorderui() {
        //show order and hide
        orderRL.setVisibility(View.VISIBLE);
        productRL.setVisibility(View.GONE);


        tabproductstv.setTextColor(getResources().getColor(R.color.white));
        tabproductstv.setBackgroundColor(getResources().getColor(android.R.color.transparent));


        tabordertv.setTextColor(getResources().getColor(R.color.black));
        tabordertv.setBackgroundResource(R.drawable.shape_rectangle_04);



    }

    private void makemeoffline() {
        progressDialog.setMessage("Logging out.....");
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("online" , "false");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(SellerActivity.this , LoginActivity.class));
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
                           String accountType = ""+ds.child("accountType").getValue();
                            String shopnme = ""+ds.child("shopname").getValue();
                            String emai = ""+ds.child("email").getValue();
                            String Profileimage = ""+ds.child("ProfileImage").getValue();
                            name.setText(names);
                            shopname.setText(shopnme);
                             email.setText(emai);
                          try{
                              Picasso.get().load(Profileimage).placeholder(R.drawable.ic_store_gray).into(profile);
                          }
                          catch(Exception e){
                              profile.setImageResource(R.drawable.ic_store_gray);

                          }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}