package com.example.foodliappserver.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.foodliappserver.Interface.ItemClickListener;
import com.example.foodliappserver.R;
public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView food_name, food_price;
    public ImageView food_image, favIcon, addToCartIcon;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        food_name = itemView.findViewById(R.id.food_name);
        food_price = itemView.findViewById(R.id.food_price);
        food_image = itemView.findViewById(R.id.food_image);
        favIcon = itemView.findViewById(R.id.favIcon);
        addToCartIcon = itemView.findViewById(R.id.addToCartIcon);


        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
