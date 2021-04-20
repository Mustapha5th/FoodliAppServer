package com.example.foodliappserver.Screens.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Interface.ItemClickListener;
import com.example.foodliappserver.Model.Category;
import com.example.foodliappserver.R;
import com.example.foodliappserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MenuFragment extends Fragment {
    // initialize variables

    FloatingActionButton fbtnAddMenu;
    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;

    Category newCategory;
// Menu Layout variables

    MaterialEditText edtName;
    Button btnSelect, btnUpload;
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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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


        fbtnAddMenu = root.findViewById(R.id.fbtnAddMenu);
        fbtnAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });





        return root;


}

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Please fill full information");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_item = inflater.inflate(R.layout.add_new_menu_item, null);

        edtName = add_menu_item.findViewById(R.id.edtName);
        btnSelect = add_menu_item.findViewById(R.id.btnSelect);
        btnUpload = add_menu_item.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage(); // allows user to select image from gallery
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        alertDialog.setView(add_menu_item);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart);
        // set button
        alertDialog.setPositiveButton(Html.fromHtml("<font color='#DE8405'>YES</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

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

    private void chooseImage() {
       Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
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
