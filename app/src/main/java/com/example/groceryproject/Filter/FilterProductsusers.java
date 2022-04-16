package com.example.groceryproject.Filter;

import android.widget.Filter;

import com.example.groceryproject.Model.ModelProducts;
import com.example.groceryproject.adapter.AdapterProductSeller;
import com.example.groceryproject.adapter.Adapter_product_user;

import java.util.ArrayList;

public class FilterProductsusers extends Filter {
    private Adapter_product_user adapter;
    private ArrayList<ModelProducts> filterList;

    public FilterProductsusers(Adapter_product_user adapter, ArrayList<ModelProducts> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if(constraint != null && constraint.length()>0){
            // change to upper case to case sensitive
            constraint = constraint.toString().toUpperCase();
            //store our filter list
            ArrayList<ModelProducts> filterModels = new ArrayList<>();
              for(int i=0 ; i<filterList.size() ; i++){
                  // check search by title and category
                  if(filterList.get(i).getProductTitle().toUpperCase()
                  .contains(constraint)||filterList.get(i).getProductCategory().toUpperCase()
                  .contains(constraint)){
                      //add filter data to list
                      filterModels.add(filterList.get(i));
                  }
              }

              results.count = filterList.size();
              results.values = filterModels;
        }
        else{
            //search filed empty
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
     adapter.productsList = (ArrayList<ModelProducts>)  results.values;
     //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
