package com.example.groceryproject.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryproject.Model.ModelUserProducts;
import com.example.groceryproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Adapterorderdetail extends RecyclerView.Adapter<Adapterorderdetail.HolderUserProduct>{

    private Context context;
    private  ArrayList<ModelUserProducts> orderlist;

    public Adapterorderdetail(Context context, ArrayList<ModelUserProducts> orderlist) {
        this.context = context;
        this.orderlist = orderlist;
    }

    @NonNull
    @Override
    public HolderUserProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_user,parent,false);
        return new HolderUserProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUserProduct holder, int position) {


        //get Data
        ModelUserProducts modelUserProducts = orderlist.get(position);
        loadShopInfo(modelUserProducts,holder);
        String orderid = modelUserProducts.getOrderId();
        String orderby = modelUserProducts.getOrderBy();
        String ordercost = modelUserProducts.getOrderCost();
        String orderstatus = modelUserProducts.getOrderStatus();
        String ordertime = modelUserProducts.getOrderTime();
        String orderto = modelUserProducts.getOrderTo();

        //setData
        holder.AmountTv.setText("Amount Rs: " + ordercost);
        holder.status.setText(orderstatus);
        holder.OrderIdTv.setText("Order Id"+orderid);
        if(orderstatus.equals("In Progress")){
            holder.status.setTextColor(context.getResources().getColor(R.color.colorprimary));
        }
        else if(orderstatus.equals("Completed")){
            holder.status.setTextColor(context.getResources().getColor(R.color.colorgreen));
        }
        else if(orderstatus.equals("Cancelled")){
            holder.status.setTextColor(context.getResources().getColor(R.color.colorred));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(ordertime));
        String Formateddate = DateFormat.format("dd/MM/yyyy",calendar).toString();
        holder.DateTv.setText(Formateddate);

    }

    private void loadShopInfo(ModelUserProducts modelUserProducts, HolderUserProduct holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelUserProducts.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                          String Shopname = ""+snapshot.child("shopname").getValue();
                          holder.shopNameTV.setText(Shopname);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        }

    @Override
    public int getItemCount() {
        return orderlist.size();
    }


    class HolderUserProduct extends RecyclerView.ViewHolder{

       private TextView OrderIdTv,DateTv,shopNameTV,AmountTv,status;


        public HolderUserProduct(@NonNull View itemView) {
            super(itemView);
            OrderIdTv = itemView.findViewById(R.id.OrderIdTv);
            DateTv = itemView.findViewById(R.id.DateTv);
            shopNameTV = itemView.findViewById(R.id.shopNameTV);
            AmountTv = itemView.findViewById(R.id.AmountTv);
            status = itemView.findViewById(R.id.status);



        }
    }
}
