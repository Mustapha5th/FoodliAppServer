package com.example.foodliappserver.ViewHolder;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Database.Database;
import com.example.foodliappserver.Interface.ItemClickListener;
import com.example.foodliappserver.Model.Order;
import com.example.foodliappserver.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView txt_cart_name, txt_price;
    public ImageView img_cart_count;
    public ElegantNumberButton btn_quantity;
    public ImageView cart_image;
    private ItemClickListener itemClickListener;

    public CartViewHolder( View itemView) {
        super(itemView);
        txt_cart_name =  itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_price);
        btn_quantity = itemView.findViewById(R.id.btn_quantity);
        cart_image = itemView.findViewById(R.id.cart_image);

        itemView.setOnCreateContextMenuListener(this);


    }

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private final CartFragment cartFragment;
    private List<Order> listData = new ArrayList<>();

    public CartAdapter(List<Order> listData, CartFragment cartFragment) {
        this.listData = listData;
        this.cartFragment = cartFragment;
    }


    @NotNull
    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cartFragment.requireContext());
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return  new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(""+listData.get(position).getQuantity(), Color.RED);
//        holder.img_cart_count.setImageDrawable(drawable);

        Picasso.get().load(listData.get(position).getImage())
                .centerCrop()
                .resize(70,70)
                .into(holder.cart_image);

        holder.btn_quantity.setNumber(listData.get(position).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cartFragment.requireContext()).updateCart(order);

                // update total
                int total = 0;
                // Calculate total price
                List<Order> orders = new Database(cartFragment.getContext()).getCart();
                for (Order item:orders)
                    total +=(Integer.parseInt(order.getPrice())) * (Integer.parseInt(item.getQuantity()));
                Locale locale = new Locale("en","NG");
                NumberFormat format = NumberFormat.getCurrencyInstance(locale);

                cartFragment.txtTotal.setText(format.format(total));

            }
        });
        Locale locale = new Locale("en","NG");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity())) ;
        holder.txt_price.setText(format.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
