package com.example.groceryproject;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterProductSeller {
    class HolderProductSeller extends RecyclerView.ViewHolder {
          /*Hold view of recycler view*/
        private ImageView product
        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);


        }
    }

}
