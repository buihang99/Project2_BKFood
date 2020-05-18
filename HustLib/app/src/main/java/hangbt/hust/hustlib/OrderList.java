package hangbt.hust.hustlib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hangbt.hust.hustlib.Interface.ItemClickListener;
import hangbt.hust.hustlib.Model.Order;
import hangbt.hust.hustlib.ViewHolder.OrderViewHolder;

public class OrderList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference order;

    FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        //recycler
        recyclerView = findViewById(R.id.recycler_order_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //firebase
        order = FirebaseDatabase.getInstance().getReference("Order");

        //load Order
        loadOrderList();
    }

    private void loadOrderList() {
        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(
                Order.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                order.orderByChild("userId").equalTo(userId)
                ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Order order, int i) {
                orderViewHolder.orderId.setText(adapter.getRef(i).getKey());
                orderViewHolder.orderStatus.setText(order.getStatus());
                orderViewHolder.orderPhone.setText("Số điện thoại: "+order.getPhone());
                orderViewHolder.orderAddress.setText("Địa chỉ: "+order.getAddress());
                orderViewHolder.orderTime.setText(order.getTime());

                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(!isLongClick){
                            Intent orderdetail = new Intent(OrderList.this, OrderDetail.class);
                            orderdetail.putExtra("orderId",adapter.getRef(position).getKey());
                            startActivity(orderdetail);
                        }else{
                            Toast.makeText(OrderList.this,"Chưa có gì!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
