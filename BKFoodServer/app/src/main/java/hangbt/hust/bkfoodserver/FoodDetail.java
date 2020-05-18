package hangbt.hust.bkfoodserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import hangbt.hust.bkfoodserver.Model.Common;
import hangbt.hust.bkfoodserver.Model.Food;


public class FoodDetail extends AppCompatActivity {

    TextView food_name, food_price, food_description, txtDiscount;
    ImageView food_image;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods = FirebaseDatabase.getInstance().getReference();

    FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    String userId = mAuth.getUid();

    Food food = new Food();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        anhXa();

        //get foodid
        if(getIntent() != null){
            foodId = getIntent().getStringExtra("FoodId");
        }

        if(!foodId.isEmpty()){
            if(Common.isConnectedToInternet(getBaseContext())) {
                getDetailFood(foodId);
            }else{
                Toast.makeText(FoodDetail.this,"Please check your connection!",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void getDetailFood(final String foodId) {
        foods.child("Foods").child(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                food = dataSnapshot.getValue(Food.class);
                food.setFoodId(dataSnapshot.getKey());
                Picasso.with(getBaseContext()).load(food.getImage()).into(food_image);

                food_price.setText(food.getPrice());
                food_name.setText(food.getName());
                food_description.setText(food.getDescription());
                txtDiscount.setText("-" + food.getDiscount()+"$");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void anhXa(){

        food_description = findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_image = findViewById(R.id.img_food);
        food_price = findViewById(R.id.food_price);
        txtDiscount = findViewById(R.id.discount);
    }
}

