package com.hiltiapp.hilti.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hiltiapp.hilti.Interface.ItemClickListener;
import com.hiltiapp.hilti.R;

public class CartViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ItemClickListener listener;
    public TextView productName, productTotalPrice , productBrand, productModel, productNotes, productQuantity, productCategory, productCode;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.cart_item_layout_name);
        productTotalPrice = itemView.findViewById(R.id.cart_item_layout_price);
        productBrand = itemView.findViewById(R.id.cart_item_layout_brand);
        productModel = itemView.findViewById(R.id.cart_item_layout_model);
        productNotes = itemView.findViewById(R.id.cart_item_layout_note);
        productQuantity = itemView.findViewById(R.id.cart_item_layout_quantity);
        productCategory = itemView.findViewById(R.id.cart_item_layout_category);
        productCode = itemView.findViewById(R.id.cart_item_layout_code);
     }


    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }
}

