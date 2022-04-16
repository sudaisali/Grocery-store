package com.example.groceryproject.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryproject.Filter.FilterProducts;
import com.example.groceryproject.Model.ModelProducts;
import com.example.groceryproject.R;
import com.example.groceryproject.activites.EditproductActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {
    private Context context;
    public ArrayList<ModelProducts> productsList , filterLists;
    private FilterProducts filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProducts> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterLists = productsList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
     ModelProducts modelProducts = productsList.get(position);
     String id = modelProducts.getProductId();
     String uid = modelProducts.getUid();
     String discountAvaliable = modelProducts.getDiscountAvaliable();
      String discountNote = modelProducts.getDiscountnote();
      String discountPrice = modelProducts.getDiscountPrice();
      String productCategory = modelProducts.getProductCategory();
      String productprice = modelProducts.getOriginalPrice();
      String productDescription = modelProducts.getProductDescription();
      String icon = modelProducts.getProductIcon();
      String quantity = modelProducts.getProductQuantity();
      String title = modelProducts.getProductTitle();
      String timestamp = modelProducts.getTimestamp();
      //setdata
        holder.titleTV.setText(title);
        holder.quantityTV.setText(quantity);
        holder.discountnoteTv.setText(discountNote);
        holder.discountpriceTV.setText("RS"+discountPrice);
        holder.originalpriceTV.setText("RS"+productprice);
        if(discountAvaliable.equals("true")){
            holder.discountpriceTV.setVisibility(View.VISIBLE);
            holder.discountnoteTv.setVisibility(View.VISIBLE);
            holder.originalpriceTV.setPaintFlags(holder.originalpriceTV
            .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else{
            holder.discountpriceTV.setVisibility(View.GONE );
            holder.discountnoteTv.setVisibility(View.GONE );
        }
        try{
            Picasso.get().load(icon).placeholder(
                    R.drawable.ic_shopping_primary).into(holder.productIconIV);

        }catch(Exception e){
            holder.productIconIV.setImageResource(R.drawable.ic_shopping_primary);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item click to show item details

                detailsBottomSheet(modelProducts);


            }
        });

    }

    private void detailsBottomSheet(ModelProducts modelProducts) {



        //bottom sheet

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view of bottom sheet

        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller ,null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        //init views of bottom sheet
        ImageButton backIB = view.findViewById(R.id.backIB);
        ImageButton deleteIB = view.findViewById(R.id.deleteIB);
        ImageButton editIB = view.findViewById(R.id.editIB);
        ImageView productIconIV = view.findViewById(R.id.productIconIV);
        TextView discountnoteTv = view.findViewById(R.id.discountnoteTv);
        TextView titleTV = view.findViewById(R.id.titleTV);
        TextView descriptionTV = view.findViewById(R.id.descriptionTV);
        TextView categoryTV = view.findViewById(R.id.categoryTV);
        TextView quantityTV = view.findViewById(R.id.quantityTV);
        TextView discountedpriceTV = view.findViewById(R.id.discountedpriceTV);
        TextView originalpriceTV = view.findViewById(R.id.originalpriceTV);
        //getdata
        String id = modelProducts.getProductId();
        String uid = modelProducts.getUid();
        String discountAvaliable = modelProducts.getDiscountAvaliable();
        String discountNote = modelProducts.getDiscountnote();
        String discountPrice = modelProducts.getDiscountPrice();
        String productCategory = modelProducts.getProductCategory();
        String productprice = modelProducts.getOriginalPrice();
        String productDescription = modelProducts.getProductDescription();
        String icon = modelProducts.getProductIcon();
        String quantity = modelProducts.getProductQuantity();
        String title = modelProducts.getProductTitle();
        String timestamp = modelProducts.getTimestamp();
        //setText
        titleTV.setText(title);
        descriptionTV.setText(productDescription);
        categoryTV.setText(productCategory);
        quantityTV.setText(quantity);
        discountedpriceTV.setText(discountPrice);
        originalpriceTV.setText("RS "+productprice);
        discountnoteTv.setText("RS "+discountNote);
        if(discountAvaliable.equals("true")){
            discountedpriceTV.setVisibility(View.VISIBLE);
            discountnoteTv.setVisibility(View.VISIBLE);
            originalpriceTV.setPaintFlags(originalpriceTV
                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else{
            discountedpriceTV.setVisibility(View.GONE );
            discountnoteTv.setVisibility(View.GONE );
        }
        try{
            Picasso.get().load(icon).placeholder(
                    R.drawable.ic_shopping_primary).into(productIconIV);

        }catch(Exception e){
            productIconIV.setImageResource(R.drawable.ic_shopping_primary);
        }
        //editclick
        editIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //open edit product activity
                Intent intent = new Intent(context, EditproductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);


            }
        });
        //delete
        deleteIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //show delete confirm dialouge
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product"+ title+"?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProduct(id);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });
        //back
        backIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


    }

    private void deleteProduct(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //product deleted
                        Toast.makeText(context, "product deleted.....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterProducts(this,filterLists);

        }

        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder {

          /*Hold view of recycler view*/

        private ImageView productIconIV;
        private TextView titleTV,quantityTV,discountpriceTV,
                originalpriceTV,nextIV,discountnoteTv;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);
            productIconIV = itemView.findViewById(R.id.productIconIV);
            titleTV = itemView.findViewById(R.id.titleTV);
            quantityTV = itemView.findViewById(R.id.quantityTV);
            discountpriceTV = itemView.findViewById(R.id.discountpriceTV);
            originalpriceTV = itemView.findViewById(R.id.originalpriceTV);
            discountnoteTv = itemView.findViewById(R.id.discountnoteTv);






        }
    }

}
