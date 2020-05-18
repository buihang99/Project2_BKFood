package hangbt.hust.hustlib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hangbt.hust.hustlib.Model.Common;
import hangbt.hust.hustlib.Model.Food_Cart;
import hangbt.hust.hustlib.Model.Order;
import hangbt.hust.hustlib.ViewHolder.CartAdapter;
import retrofit2.Call;
import retrofit2.Callback;

public class CartView extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference carts = FirebaseDatabase.getInstance().getReference();

    //FireDatabase fireDatabase = new FireDatabase();

    public TextView txttotal;
    Button btnorder;
    EditText edtName,edtPhone,edtAddress;

    List<Food_Cart> list = new ArrayList<>();

    CartAdapter adapter;

    FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    String userId = mAuth.getUid();

    Order order = new Order();

    //Database data = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);

        anhXa();

        loadListFood();


        btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txttotal.getText().equals("0")){
                    Toast.makeText(CartView.this,"Chưa có sản phẩm nào",Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartView.this,android.R.style.Theme_DeviceDefault_Light_Dialog);
                    builder.setTitle("One more step!");
                    builder.setIcon(R.drawable.ic_shopping_cart_black_24dp);
                    builder.setMessage("Information order");

                    LayoutInflater inflater = getLayoutInflater();
                    View v = inflater.inflate(R.layout.alert_order,null);

                    edtName = v.findViewById(R.id.edtName);
                    edtPhone = v.findViewById(R.id.edtPhoneOrder);
                    edtAddress = v.findViewById(R.id.edtAddress);

                    builder.setView(v);
                    builder.setCancelable(false);

                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name = edtName.getText().toString();
                            String phone = edtPhone.getText().toString();
                            String address = edtAddress.getText().toString();
                            if(name.equals("")||phone.equals("")||address.equals("")){
                                Toast.makeText(CartView.this,"Nhập thiếu thông tin",Toast.LENGTH_SHORT).show();
                            }else{
                                order.setUserId(userId);
                                order.setName(name);
                                order.setPhone(phone);
                                order.setAddress(address);
                                order.setStatus("Updating");
                                order.setOrderId(String.valueOf(System.currentTimeMillis()));
                                order.setTotal(txttotal.getText().toString());

                                carts.child("Order").child(order.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        carts.child("Cart").child(userId).child("Foods").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                                    Food_Cart f = data.getValue(Food_Cart.class);
                                                    carts.child("Order").child(order.getOrderId()).
                                                            child("Food").child(f.getFoodId()).setValue(f);
                                                    carts.child("Cart").child(userId).child("Foods").child(f.getFoodId()).removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        carts.child("Order").child(order.getOrderId()).setValue(order);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                Toast.makeText(CartView.this,"Thank you " + name,Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                    builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void loadListFood() {
        carts.child("Cart").child(userId).child("Foods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot data : dataSnapshot.getChildren()){
                   Food_Cart f = data.getValue(Food_Cart.class);
                   list.add(f);
               }
                adapter = new CartAdapter(list,CartView.this);
               // adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                int total = 0;
                for(Food_Cart f : list){
                    int p = Integer.parseInt(f.getFoodPrice());
                    int d = Integer.parseInt(f.getFoodDiscount());
                    int a = Integer.parseInt(f.getFoodAmount());
                    total += (p-d)*a;
                }
                txttotal.setText(String.valueOf(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void anhXa(){
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txttotal = findViewById(R.id.total);
        btnorder = findViewById(R.id.btnOrder);

    }
}
