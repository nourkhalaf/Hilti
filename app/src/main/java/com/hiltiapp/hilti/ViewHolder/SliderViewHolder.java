package com.hiltiapp.hilti.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hiltiapp.hilti.Interface.ItemClickListener;
import com.hiltiapp.hilti.R;

public class SliderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ItemClickListener listener;
     public ImageView sliderImage;

    public SliderViewHolder(@NonNull View itemView) {
        super(itemView);
         sliderImage = itemView.findViewById(R.id.slider_image);
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
