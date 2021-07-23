package com.example.foodliappserver.Screens.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Interface.ItemClickListener;
import com.example.foodliappserver.Model.Category;
import com.example.foodliappserver.R;
import com.example.foodliappserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

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
        manager = new LinearLayoutManager(getContext());
        recycler_menu.setLayoutManager(manager);
        recycler_menu.setHasFixedSize(true);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null ){
            saveUri = data.getData();
            btnSelect.setText("Selected");
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.DELETE)){
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
            Toast.makeText(getContext(), "Menu item deleted", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
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
    private void deleteCategory(String key) {
        category.child(key).removeValue();
    }
    private void showUpdateDialog(String key, Category item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_item = inflater.inflate(R.layout.add_new_menu_item, null);

        edtName = add_menu_item.findViewById(R.id.edtName);
        btnSelect = add_menu_item.findViewById(R.id.btnSelect);
        btnUpload = add_menu_item.findViewById(R.id.btnUpload);

        // set default values
        edtName.setText(item.getName());
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage(); // allows user to select image from gallery
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changeImage(item);

            }
        });
        alertDialog.setView(add_menu_item);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart);
        // set button
        alertDialog.setPositiveButton(Html.fromHtml("<font color='#DE8405'>YES</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               item.setName(edtName.getText().toString());
               category.child(key).setValue(item);

                Snackbar.make(getActivity().findViewById(android.R.id.content), "category "+item.getName()+" was edited", Snackbar.LENGTH_SHORT).show();

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
                uploadImage();

            }
        });
        alertDialog.setView(add_menu_item);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart);
        // set button
        alertDialog.setPositiveButton(Html.fromHtml("<font color='#DE8405'>YES</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // create new category
                if(newCategory != null){
                    category.push().setValue(newCategory);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "New Category"+newCategory.getName()+" was added", Snackbar.LENGTH_SHORT).show();
                    loadMenu();
                }

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
    private void uploadImage() {
        if (saveUri != null){
            ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Uplaoding...");
            mDialog.show();
            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(requireContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // set value for nee category
                            newCategory = new Category(edtName.getText().toString(), uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(requireContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded " +progress+"%");

                }
            });
        }
    }
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }
    private void changeImage(Category item) {
        if (saveUri != null){
            ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Uplaoding...");
            mDialog.show();
            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(requireContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // set value for nee category
                          item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(requireContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded " +progress+"%");

                }
            });
        }
    }


}
