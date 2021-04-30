package com.example.foodliappserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Interface.ItemClickListener;
import com.example.foodliappserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{


    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    public Button btnEdit, btnRemove, btnDetail;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);

        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnDetail = itemView.findViewById(R.id.btnDetail);


    }

  }
