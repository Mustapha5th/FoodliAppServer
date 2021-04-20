package com.example.foodliappserver.Screens.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodliapp.Interface.ItemClickListener;
import com.example.foodliapp.Model.Category;
import com.example.foodliapp.R;
import com.example.foodliapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MenuFragment extends Fragment {
    // initialize variables

    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    //Slider
    HashMap<String, String> image_list;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager manager;

    SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);


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
                loadMenu();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadMenu();
            }
        });
        // init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class).build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int position, @NonNull Category category) {

                menuViewHolder.txtMenuName.setText(category.getName());
                Picasso.get().load(category.getImage()).into(menuViewHolder.menuImage);
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //   Get Category and send to new activity
                        Intent foodList = new Intent(getActivity(), FoodList.class);
                        // Get Category Key of item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };


        //Load menu
        recycler_menu = root.findViewById(R.id.recycler_menu);
        recycler_menu.setLayoutManager(new GridLayoutManager(getContext(), 2));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
                R.anim.layout_fall_down);
        recycler_menu.setLayoutAnimation(controller);







    return root;


}




    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void loadMenu() {

        adapter.startListening();
        adapter.notifyDataSetChanged(); // Refresh data if there is any change
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
        //Animation
        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }


}
