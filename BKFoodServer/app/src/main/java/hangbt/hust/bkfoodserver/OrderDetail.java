package hangbt.hust.bkfoodserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hangbt.hust.bkfoodserver.Model.Food_Cart;
import hangbt.hust.bkfoodserver.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView orderId, orderPhone, orderAddress, orderTotal, orderTime;
    String order_Id = "";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    OrderDetailAdapter adapter;
    List<Food_Cart> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = findViewById(R.id.orderId);
        orderPhone = findViewById(R.id.orderPhone);
        orderAddress = findViewById(R.id.orderAddress);
        orderTime = findViewById(R.id.orderTime);
        orderTotal = findViewById(R.id.orderTotal);

        recyclerView = findViewById(R.id.foodOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent() != null){
            order_Id = getIntent().getStringExtra("orderId");
            orderId.setText(order_Id);
            loadListFood();
        }
    }

    private void loadListFood() {
        database.child("Order").child(order_Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderPhone.setText( "Số điện thoại"+ dataSnapshot.child("phone").getValue().toString());
                orderAddress.setText("Địa chỉ" +dataSnapshot.child("address").getValue().toString());
                orderTotal.setText("Tổng thanh toán: "+ dataSnapshot.child("total").getValue().toString());
                orderTime.setText( dataSnapshot.child("time").getValue().toString());

                for(DataSnapshot d : dataSnapshot.child("Food").getChildren()){
                    list.add(d.getValue(Food_Cart.class));
                }

                adapter = new OrderDetailAdapter(list,OrderDetail.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
