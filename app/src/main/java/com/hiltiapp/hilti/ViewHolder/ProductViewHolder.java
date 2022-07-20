package com.hiltiapp.hilti.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hiltiapp.hilti.Interface.ItemClickListener;
import com.hiltiapp.hilti.R;


public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ItemClickListener listener;
    public TextView productName;
    public ImageView productImage;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.product_layout_name);
        productImage = itemView.findViewById(R.id.product_layout_image);
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
