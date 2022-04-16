package com.example.groceryproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.LabeledIntent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryproject.Filter.FilterProducts;
import com.example.groceryproject.Filter.FilterProductsusers;
import com.example.groceryproject.Model.ModelProducts;
import com.example.groceryproject.R;
import com.example.groceryproject.activites.ShopDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class Adapter_product_user extends RecyclerView.Adapter<Adapter_product_user.HolderProductuser>  implements Filterable {
       private Context context;
       public ArrayList<ModelProducts> productsList , filterlist;
       private  FilterProductsusers filter;

    public Adapter_product_user(Context context, ArrayList<ModelProducts> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterlist = productsList;

    }

    @NonNull
    @Override
    public HolderProductuser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_shop,parent,false);
        return new HolderProductuser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductuser holder, int position) {
        ModelProducts modelProducts = productsList.get(position);
        String discountAvaliable = modelProducts.getDiscountAvaliable();
        String descripton = modelProducts.getProductDescription();
        String discountNote = modelProducts.getDiscountnote();
        String discountPrice = modelProducts.getDiscountPrice();
        String productprice = modelProducts.getOriginalPrice();
        String icon = modelProducts.getProductIcon();
        String title = modelProducts.getProductTitle();
        //setdata
        holder.titleTV.setText(title);
        holder.descriptionTV.setText(descripton);
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
        holder.addtocartTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product to cart
                showquantitydialouge(modelProducts);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product detail

            }
        });

    }
    private  double cost = 0;
    private double finalcost = 0;
    private int quantity =0;

    private void showquantitydialouge(ModelProducts modelProducts) {

     View view = LayoutInflater.from(context).inflate(R.layout.dialouge_quantity,null);

        ImageView productIv = view.findViewById(R.id.productIv);
        TextView pquantity  = view.findViewById(R.id.pquantity);
        TextView ptitle = view.findViewById(R.id.title);
        TextView discountnote  = view.findViewById(R.id.discountnote);
        TextView description  = view.findViewById(R.id.description);
        TextView originalprice  = view.findViewById(R.id.originalprice);
        TextView discountprice  = view.findViewById(R.id.discountprice);
        TextView finalprice  = view.findViewById(R.id.finalprice);
        ImageButton decrementbtn  = view.findViewById(R.id.decrementbtn);
        ImageButton incerementbtn  = view.findViewById(R.id.incerementbtn);
        TextView quantittv  = view.findViewById(R.id.quantity);
        Button continuebtn = view.findViewById(R.id.continuebtn);

        String productid = modelProducts.getProductId();
        String title = modelProducts.getProductTitle();
        String productQuantity = modelProducts.getProductQuantity();
        String descrp = modelProducts.getProductDescription();
        String discountnot = modelProducts.getDiscountnote();
        String image = modelProducts.getProductIcon();
        String price;
        if(modelProducts.getDiscountAvaliable().equals("true")){
            price  = modelProducts.getDiscountPrice();
            discountnote.setVisibility(View.VISIBLE);
            originalprice.setPaintFlags(originalprice.getPaintFlags()
            |Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            discountnote.setVisibility(View.GONE);
            discountprice.setVisibility(View.GONE);
            price = modelProducts.getOriginalPrice();
        }

        cost = Double.parseDouble(price.replaceAll("RS",""));
        finalcost = Double.parseDouble(price.replaceAll("RS",""));
        quantity =1;


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart).into(productIv);

        }
        catch(Exception e){
            productIv.setImageResource(R.drawable.ic_cart);
        }
        ptitle.setText(""+title);
        pquantity.setText(""+productQuantity);
        description.setText(""+descrp);
        discountnote.setText(""+discountnot);
        quantittv.setText(""+quantity);
        originalprice.setText("RS"+modelProducts.getOriginalPrice());
        discountprice.setText("RS"+modelProducts.getDiscountPrice());
        finalprice.setText("RS"+finalcost);
        AlertDialog dialog = builder.create();
        dialog.show();

        incerementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finalcost = finalcost+cost;
                quantity++;
                finalprice.setText("RS"+finalcost);
                quantittv.setText(""+quantity);

            }
        });
        decrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1){
                    finalcost = finalcost-cost;
                    quantity--;
                    finalprice.setText("RS"+finalcost);
                    quantittv.setText(""+quantity);

                }
            }
        });
        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ptitle.getText().toString().trim();
                String priceEach = price;
                String totalprice = finalprice.getText().toString().trim().replace("","");
                String quantity = quantittv.getText().toString().trim();
                addToCart(productid , title , priceEach , totalprice , quantity);
                dialog.dismiss();
            }
        });




    }
    private  int itemid  = 1;
    private void addToCart(String productid, String title, String priceEach, String price, String quantity) {

      itemid++;
        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();

        boolean b = easyDB
                .addData("Item_Id", itemid)
                .addData("Item_PID", productid)
                .addData("Item_Name", title)
                .addData("Item_Price_Each", priceEach)
                .addData("Item_Price", price)
                .addData("Item_Quantity", quantity)
                .doneDataAdding();
        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();

        ((ShopDetailActivity)context).cartcount();
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProductsusers(this ,filterlist);

        }
        return filter;
    }

    class HolderProductuser extends RecyclerView.ViewHolder{
       private ImageView productIconIV;
       private TextView titleTV,discountpriceTV,descriptionTV,addtocartTV,
               originalpriceTV,nextIV,discountnoteTv;


       public HolderProductuser(@NonNull View itemView) {
           super(itemView);
           productIconIV = itemView.findViewById(R.id.productIconIV);
           titleTV = itemView.findViewById(R.id.titleTV);
           descriptionTV = itemView.findViewById(R.id.descriptionTV);
           addtocartTV = itemView.findViewById(R.id.addtocartTV);
           discountpriceTV = itemView.findViewById(R.id.discountpriceTV);
           originalpriceTV = itemView.findViewById(R.id.originalpriceTV);
           discountnoteTv = itemView.findViewById(R.id.discountnoteTv);


       }
   }
}
