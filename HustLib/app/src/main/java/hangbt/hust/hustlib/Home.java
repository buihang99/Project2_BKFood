package hangbt.hust.hustlib;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hangbt.hust.hustlib.Interface.ItemClickListener;
import hangbt.hust.hustlib.Model.Category;
import hangbt.hust.hustlib.Model.Common;
import hangbt.hust.hustlib.Model.Food;
import hangbt.hust.hustlib.Service.ListenOrder;
import hangbt.hust.hustlib.ViewHolder.FoodViewHolder;
import hangbt.hust.hustlib.ViewHolder.MenuViewHolder;
import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullname, txtEmail;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    String userId = mAuth.getUid();

    //search
    FirebaseRecyclerAdapter<Food, FoodViewHolder > searchadapter;
    DatabaseReference foodList;

    MaterialSearchBar searchBar;
    List<String> suggestions = new ArrayList<>();
    List<String> suggest = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //init paper
        Paper.init(this);

        anhXa();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Toast.makeText(Home.this,FirebaseAuth.getInstance().getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();
        }

        //init firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        foodList = database.getReference("Foods");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart = new Intent(Home.this, CartView.class);
                startActivity(cart);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //new code
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open, R.string.close);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        //Set name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullname = headerView.findViewById(R.id.txtFullname);
        txtEmail = headerView.findViewById(R.id.txtEmail);
        txtFullname.setText(Common.currenUser.getFullname());
        txtEmail.setText(Common.currenUser.getEmail());

        //load menu
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        if (Common.isConnectedToInternet(getBaseContext())) {
            loadMenu();
            Intent service = new Intent(Home.this, ListenOrder.class);
            startService(service);
        } else {
            Toast.makeText(Home.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
        }

        //SEARCH
        searchBar = findViewById(R.id.searchBarHome);
        loadSuggest();
        searchBar.setCardViewElevation(10);
        searchBar.setLastSuggestions(suggestions);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                for(String search : suggestions){
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase())){
                        suggest.add(search);
                    }
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    recycler_menu.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchadapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.txtFoodName.setText(food.getName());
                Picasso.with(getBaseContext()).load(food.getImage()).into(foodViewHolder.imgFood);

                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(Home.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchadapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recycler_menu.setAdapter(searchadapter);
    }

    private void loadSuggest() {
        foodList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Food item = d.getValue(Food.class);
                    suggestions.add(item.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        foodList.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot d : dataSnapshot.getChildren()){
//                    Food item = d.getValue(Food.class);
//                    suggestions.add(item.getName());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {
                menuViewHolder.txtMenuName.setText(category.getName());
                Picasso.with(getBaseContext()).load(category.getPicture()).into(menuViewHolder.imageView);

                final Category clickItem = category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get categoryId
                        Intent foodList = new Intent(Home.this,FoodList.class);
                        foodList.putExtra("categoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }
        };
        recycler_menu.setAdapter(adapter);
    }

    private void anhXa(){
        txtFullname = findViewById(R.id.txtFullname);
        txtEmail = findViewById(R.id.txtEmail);
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.refresh){
            loadMenu();
        }else if(id == R.id.search){
            Intent searchFood = new Intent(Home.this, SearchFood.class);
            startActivity(searchFood);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.recycler_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_menu){

        }else if(id == R.id.nav_cart){
            Intent cart = new Intent(Home.this, CartView.class);
            startActivity(cart);

        }else if(id == R.id.nav_orders){
            Intent orders = new Intent(Home.this, OrderList.class);
            startActivity(orders);
        }else if(id == R.id.nav_log_out){
            Paper.book().destroy();

            FirebaseAuth.getInstance().signOut();

            Intent signout = new Intent(Home.this, MainActivity.class);
            signout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            signout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            signout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signout);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
