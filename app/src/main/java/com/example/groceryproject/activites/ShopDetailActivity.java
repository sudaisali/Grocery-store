package com.example.groceryproject.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryproject.Model.ModelProducts;
import com.example.groceryproject.Model.ModelShops;
import com.example.groceryproject.Model.Modelcart;
import com.example.groceryproject.R;
import com.example.groceryproject.adapter.AdapterCart;
import com.example.groceryproject.adapter.AdapterProductSeller;
import com.example.groceryproject.adapter.Adapter_product_user;
import com.example.groceryproject.constants.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailActivity extends AppCompatActivity {

    RelativeLayout shopRL,toolbarRl;
    TextView shopNameTV,phoneTV,emailTv,shopopenTv,deliveryfeeTv, addressTv,shopdetails,filterdproducttv,cartcount;
    ImageView callbtn,mapbtn,shopIV;
    ImageButton shoppingcart,back,filterBtn;
    EditText searchproductEt;
    RecyclerView productrv;
    private String shopuid;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String mylatitude,mylongitude , myphone;

    private String shoplatitude,shoplongitude ,shopphone, shopname , shopemail , shopaddress;
   public String deliveryfee;
    private ArrayList<ModelProducts> productsList;
   private Adapter_product_user adapter_product_user;

   private ArrayList<Modelcart> cartitemlist;
   private AdapterCart adapterCart;
    EasyDB easyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        shopRL = findViewById(R.id.shopRL);
        shopNameTV = findViewById(R.id.shopNameTV);
        phoneTV = findViewById(R.id.phoneTV);
        shopIV = findViewById(R.id.shopIV);
        emailTv = findViewById(R.id.emailTv);
        shopopenTv = findViewById(R.id.shopopenTv);
        deliveryfeeTv = findViewById(R.id.deliveryfeeTv);
        addressTv = findViewById(R.id.addressTv);
        shopdetails = findViewById(R.id.shopdetails);
        callbtn = findViewById(R.id.callbtn);
        mapbtn = findViewById(R.id.mapbtn);
        shoppingcart = findViewById(R.id.shoppingcart);
        back = findViewById(R.id.back);
        filterBtn = findViewById(R.id.filterBtn);
        searchproductEt = findViewById(R.id.searchproductEt);
        productrv = findViewById(R.id.RecyclerView);
        filterdproducttv = findViewById(R.id.filterdproducttv);
        cartcount = findViewById(R.id.cartcount);
        shopuid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        loadmyinfo();
        loadshopdetails();
        loadshopproducts();

        deletecartdata();
        cartcount();
        searchproductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapter_product_user.getFilter().filter(s);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailActivity.this);
                builder.setTitle("Choose category")
                        .setItems(constants.productCategory2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = constants.productCategory2[which];
                                filterdproducttv.setText(selected);
                                if (selected.equals("ALL")) {
                                    loadshopproducts();
                                } else {
                                   adapter_product_user.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialphone();

            }
        });
        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmap();
                
            }
        });
        shoppingcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcartDialouge();
            }
        });


    }

    private void deletecartdata() {

        easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();
    }

    public void cartcount(){
        int count = easyDB.getAllData().getCount();
        if(count<=0){
            cartcount.setVisibility(View.GONE);
        }
        else{
            cartcount.setVisibility(View.VISIBLE);
            cartcount.setText(""+count);
        }

    }





    public double allTotalprice = 0.0;
    public TextView  stotal,dfee,total;
    private void showcartDialouge() {

        cartitemlist = new ArrayList<>();
         View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
         TextView Shopname  = view.findViewById(R.id.Shopname);
         RecyclerView cartitem  = view.findViewById(R.id.cartitem);
         stotal  = view.findViewById(R.id.stotal);
         dfee  = view.findViewById(R.id.dfee);
         total  = view.findViewById(R.id.total);
         Button checkout  = view.findViewById(R.id.checkout);
         checkout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (mylatitude.equals("") || mylatitude.equals("null") || mylongitude.equals("") || mylongitude.equals("null")) {
                     Toast.makeText(ShopDetailActivity.this, "Please enter address in profile", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if (myphone.equals("") || myphone.equals("null")) {
                     Toast.makeText(ShopDetailActivity.this, "Please enter contact in profile", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if(cartitemlist.size() == 0){
                     Toast.makeText(ShopDetailActivity.this, "No item in cart", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 submitorder();
             }
         });
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setView(view);
          Shopname.setText(shopname);

        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pid = res.getString(2);
            String name= res.getString(3);
            String price = res.getString(4);
            String cost= res.getString(5);
            String quantity= res.getString(6);

            allTotalprice = allTotalprice+Double.parseDouble(quantity)*Double.parseDouble(price);

            Modelcart modelcart = new Modelcart(
                   ""+id,
                    ""+pid,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );
            cartitemlist.add(modelcart);

        }
        adapterCart = new AdapterCart(this , cartitemlist);
        cartitem.setAdapter(adapterCart);
        dfee.setText("RS" + deliveryfee);

        stotal.setText("RS "+String.format("%.2f",allTotalprice));

        total.setText("RS"+(allTotalprice+Double.parseDouble(deliveryfee.replace("RS",""))));
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalprice =0.00;

            }
        });

    }

    private void submitorder() {

        progressDialog.setMessage("Order placing");
        progressDialog.show();

        // for order id and time stamp
        String time = ""+   System.currentTimeMillis();
        String cost = total.getText().toString().trim().replace("RS","");
        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put("OrderId", ""+time);
        hashMap.put("OrderTime", ""+time);
        hashMap.put("OrderStatus","In Progress");
        hashMap.put("OrderCost",""+cost);
        hashMap.put("OrderBy",""+firebaseAuth.getUid());
        hashMap.put("OrderTo",""+shopuid);

        //addtodb


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopuid).child("Orders");
        ref.child(time).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        for(int i = 0 ; i < cartitemlist.size() ; i++){
                            String Pid = cartitemlist.get(i).getPid();
                            String id = cartitemlist.get(i).getId();
                            String cost = cartitemlist.get(i).getCost();
                            String name = cartitemlist.get(i).getName();
                            String price = cartitemlist.get(i).getPrice();
                            String quantity = cartitemlist.get(i).getQuantity();
                            HashMap<String , String> hashMap1 = new HashMap<>();
                            hashMap1.put("Pid" , ""+Pid);
                            hashMap1.put("Name" , ""+name);
                            hashMap1.put("Cost" , ""+cost);
                            hashMap1.put("Price" , ""+price);
                            hashMap1.put("Quantity" , ""+quantity);
                            ref.child(time).child("Items").child(Pid).setValue(hashMap1);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailActivity.this, "Order Not placed", Toast.LENGTH_SHORT).show();

                    }
                });



















    }

    private void openmap() {
        String address="https://maps.google.com/maps?saddr="+ mylatitude + "," + mylongitude + "&daddr=" + shoplatitude +","+shoplongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);}

    private void dialphone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopphone))));
        Toast.makeText(this, ""+shopphone, Toast.LENGTH_SHORT).show();
    }


    private void loadshopdetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopuid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopname = ""+snapshot.child("shopname").getValue();
                        shopemail = ""+snapshot.child("email").getValue();
                        shopphone = ""+snapshot.child("phone").getValue();
                        shopaddress= ""+snapshot.child("address").getValue();
                        shoplatitude  = ""+snapshot.child("latitude").getValue();
                        shoplongitude = ""+snapshot.child("longitude").getValue();
                        deliveryfee  = ""+snapshot.child("delivery fee").getValue();
                        String profileImage =  ""+snapshot.child("ProfileImage").getValue();
                        String shopOpen = ""+snapshot.child("shopOpen").getValue();
                        //setdata
                        shopNameTV.setText(shopname);
                        phoneTV.setText(shopphone);
                        emailTv.setText(shopemail);
                        deliveryfeeTv.setText("Delivery Fee = "+deliveryfee);
                        addressTv.setText(shopaddress);
                        if(shopOpen.equals("true")){
                            shopopenTv.setText("Open");
                        }else{
                            shopopenTv.setText("closed");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadshopproducts() {
        productsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopuid).child("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                            productsList.add(modelProducts);
                        }
                        adapter_product_user = new Adapter_product_user(ShopDetailActivity.this, productsList);
                        productrv.setAdapter(adapter_product_user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });

    }



    private void loadmyinfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String names = ""+ds.child("shopname").getValue();
                             myphone = ""+ds.child("phone").getValue();
                            String email = ""+ds.child("email").getValue();
                            String Profileimage = "" + ds.child("ProfileImage").getValue();
                            String city = "" + ds.child("city").getValue();
                            mylatitude = "" + ds.child("latitude").getValue();
                            mylongitude = "" + ds.child("longitude").getValue();

                            String accountType = ""+ds.child("accountType").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}