package com.example.foodliappserver.Screens.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Interface.ItemClickListener;
import com.example.foodliappserver.Model.Category;
import com.example.foodliappserver.Model.Order;
import com.example.foodliappserver.Model.Request;
import com.example.foodliappserver.R;
import com.example.foodliappserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatusFragment extends Fragment {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager manager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseDatabase database;
    DatabaseReference requests;

    MaterialSpinner spinner;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         View root = inflater.inflate(R.layout.fragment_order_status, container, false);
        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        recyclerView = root.findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        // swipe to refresh
        swipeRefreshLayout = root.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.red,
                R.color.colorPrimaryDarkNight,
                R.color.green,
                R.color.blue
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrders();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadOrders();
            }
        });

        return root;
    }

    private void loadOrders() {
        Query orderQuery =  requests.orderByKey();
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(orderQuery,Request.class).build();
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i, @NonNull Request request) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(i).getKey());
                orderViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                orderViewHolder.txtOrderAddress.setText(request.getAddress());
                orderViewHolder.txtOrderPhone.setText(request.getPhone());
                orderViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(i).getKey(), adapter.getItem(i));

                    }
                });
                orderViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent(getContext(), OrderDetail.class);
                        Common.currentRequest = request;
                        orderDetail.putExtra("OrderId",adapter.getRef(i).getKey());
                        startActivity(orderDetail);
                    }
                });
                orderViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Remove Order");
                        builder.setMessage("This order is going to be deleted, are you sure you want to delete this order?");
                        builder.setPositiveButton(Html.fromHtml("<font color= '#DE8405'>Yes</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteOrder(adapter.getRef(i).getKey());
                                Toast.makeText(getContext(), "Order item deleted", Toast.LENGTH_SHORT).show();

                            }
                        });
                        builder.setNegativeButton(Html.fromHtml("<font color= '#DE8405'>No</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        // show dialog
                        builder.show();

                    }
                });
              }


            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView  = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent,false);
                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }
    private void showUpdateDialog(String key, Request item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose order status");
        LayoutInflater inflater = this.getLayoutInflater();
        View update_order_layout = inflater.inflate(R.layout.update_order_layout, null);

        spinner = update_order_layout.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","Processing","Processed","On it's way", "Delivered");

        alertDialog.setView(update_order_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_access_time);
        final String localKeys = key;
        // set button
        alertDialog.setPositiveButton(Html.fromHtml("<font color='#DE8405'>YES</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localKeys).setValue(item);
                adapter.notifyDataSetChanged();
//                Snackbar.make(getActivity().findViewById(android.R.id.content), "category "+item.getName()+" was edited", Snackbar.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#DE8405'>No</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();


    }

}