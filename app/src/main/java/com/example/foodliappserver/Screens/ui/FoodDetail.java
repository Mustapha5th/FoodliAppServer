package com.example.foodliappserver.Screens.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.foodliappserver.Common.Common;
import com.example.foodliappserver.Database.Database;
import com.example.foodliappserver.Model.Food;
import com.example.foodliappserver.Model.Order;
import com.example.foodliappserver.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity  {
    TextView food_name, food_price, food_description;
    ImageView img_food;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fbtnCart;
    ElegantNumberButton numberButton;

    RatingBar ratingBar;

    String foodId="";
    FirebaseDatabase database;
    DatabaseReference foods, ratingTbl;
    Food currentFood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        // Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");

        ratingTbl = database.getReference("Rating");

        //Init view
        numberButton = findViewById(R.id.number_button);


        ratingBar = findViewById(R.id.ratingBar);


        fbtnCart = findViewById(R.id.fbtnCart);
        fbtnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                                foodId,
                                currentFood.getName(),
                                numberButton.getNumber(),
                                currentFood.getPrice(),
                                currentFood.getDiscount(),
                                currentFood.getImage()

                        )
                );

                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        food_description = findViewById(R.id.food_description);
        food_price = findViewById(R.id.food_price);
        food_name = findViewById(R.id.food_name);
        img_food = findViewById(R.id.img_food);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        // Get Food id from intent
        if (getFragmentManager() != null)
            foodId = getIntent().getStringExtra("CategoryId");
        if (!foodId.isEmpty()){
            if (Common.isConnectedToInternet(getBaseContext())){
            getDetailFood(foodId);

            } else {

                // TO DO: convert to snack bar ...
                Toast.makeText(FoodDetail.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }



    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentFood = snapshot.getValue(Food.class);

                //Set Image
                Picasso.get().load(currentFood.getImage()).into(img_food);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }


}