package com.example.foodliappserver.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodliappserver.Model.Order;
import com.example.foodliappserver.R;

import java.util.List;

class  MyViewHolder extends RecyclerView.ViewHolder{
    TextView name,quantity,price,discount;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
        discount = itemView.findViewById(R.id.product_discount);
    }
}
public class OrderDetailAdapter extends  RecyclerView.Adapter<MyViewHolder>{
    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    List<Order> myOrders;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Order order = myOrders.get(position);
        holder.name.setText(String.format("Name : %s", order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s", order.getQuantity()));
        holder.price.setText(String.format("Price : ₦%s", order.getPrice()));
        holder.discount.setText(String.format("Discount : %s", order.getDiscount()));

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
