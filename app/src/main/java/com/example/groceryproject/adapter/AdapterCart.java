package com.example.groceryproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryproject.Model.Modelcart;
import com.example.groceryproject.R;
import com.example.groceryproject.activites.ShopDetailActivity;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.HolderCartItem>{

    private Context context;
    private ArrayList<Modelcart> cartItems;

    public AdapterCart(Context context, ArrayList<Modelcart> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.row_cart,parent,false);
        return new HolderCartItem(view);




    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, @SuppressLint("RecyclerView") int position) {

        Modelcart modelcart = cartItems.get(position);
        String id = modelcart.getId();
        String pid = modelcart.getPid();
        String title = modelcart.getName();
        String cost = modelcart.getCost();
        String price = modelcart.getPrice();
        String quantity = modelcart.getQuantity();

        holder.itemTitle.setText(title);
        holder.itemprice.setText(""+price);
        holder.itemQuantity.setText("["+quantity+"]");
        holder.itempriceeach.setText(""+cost);

        holder.itemRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                        .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))

                        .doneTableColumn();

                easyDB.deleteRow(1,id);
                Toast.makeText(context, "Remove from cart", Toast.LENGTH_SHORT).show();
                cartItems.remove(position);
               notifyItemChanged(position);
                notifyDataSetChanged();

                double tx = Double.parseDouble((((ShopDetailActivity)context).total.getText().toString().trim().replace("RS","")));
                double totalprice = tx - Double.parseDouble(cost.replace("RS",""));
                double deliveryfee  = Double.parseDouble((((ShopDetailActivity)context).deliveryfee.replace("RS","")));
                double stotalprice = Double.parseDouble(String.format("%.2f",totalprice)) - Double.parseDouble(String.format("%.2f",deliveryfee));
                ((ShopDetailActivity)context).allTotalprice = 0.00;
                ((ShopDetailActivity)context).stotal.setText("Rs" +String.format("%.2f",stotalprice));
                ((ShopDetailActivity)context).total.setText("RS"+String.format("%.2f",Double.parseDouble(String.format("%.2f",totalprice))));

                ((ShopDetailActivity)context).cartcount();


            }
        });



    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{
              TextView itemTitle,itemprice,itempriceeach,itemQuantity
                     ,itemRemove;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemprice = itemView.findViewById(R.id.itemprice);
            itempriceeach = itemView.findViewById(R.id.itempriceeach);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemRemove = itemView.findViewById(R.id.itemRemove);


        }
    }

}
