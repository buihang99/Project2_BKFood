package hangbt.hust.bkfoodserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import hangbt.hust.bkfoodserver.Interface.ItemClickListener;
import hangbt.hust.bkfoodserver.Model.Common;
import hangbt.hust.bkfoodserver.Model.Order;
import hangbt.hust.bkfoodserver.ViewHolder.OrderViewHolder;
import retrofit2.Call;
import retrofit2.Callback;

public class OrderList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference order;

    FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    MaterialSpinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        database = FirebaseDatabase.getInstance();

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
                order
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, final Order order, int i) {
                orderViewHolder.orderId.setText(adapter.getRef(i).getKey());
                orderViewHolder.orderStatus.setText(order.getStatus());
                orderViewHolder.orderPhone.setText("Số điện thoại: "+order.getPhone());
                orderViewHolder.orderAddress.setText("Địa chỉ: "+order.getAddress());
                orderViewHolder.orderTime.setText(order.getTime());

                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent orderdetail = new Intent(OrderList.this, OrderDetail.class);
                        orderdetail.putExtra("orderId",adapter.getRef(position).getKey());
                        startActivity(orderdetail);
//                        Intent tracking = new Intent(OrderList.this, TrackingOrder.class);
//                        Common.currenOrder = order;
//                        startActivity(tracking);

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            updateOrder(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }else if(item.getTitle().equals(Common.DELETE)){
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(final String key) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderList.this);
        alertDialog.setTitle("Bạn chắc chắn muốn xóa chứ?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                order.child(key).removeValue();
                dialogInterface.dismiss();
                Toast.makeText(OrderList.this,"Deleted!!!",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void updateOrder(String key, final Order item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderList.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Choose status");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_order,null);

        spinner = view.findViewById(R.id.status);
        spinner.setItems("Đã xác nhận","Đang giao","Đã giao");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(Common.convertStatus(spinner.getSelectedIndex()));

                order.child(localKey).child("status").setValue(item.getStatus());

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
}
