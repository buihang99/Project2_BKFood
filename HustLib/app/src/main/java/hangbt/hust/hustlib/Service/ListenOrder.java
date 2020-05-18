package hangbt.hust.hustlib.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hangbt.hust.hustlib.Model.Food_Cart;
import hangbt.hust.hustlib.Model.Order;
import hangbt.hust.hustlib.OrderList;
import hangbt.hust.hustlib.R;

import static hangbt.hust.hustlib.App.CHANNEL_1_ID;

public class ListenOrder extends Service implements ChildEventListener {

    FirebaseDatabase database;
    DatabaseReference order;

    FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

    List<Food_Cart> list;
    private NotificationManagerCompat notificationManager;

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        order = database.getReference("Order");
        notificationManager = NotificationManagerCompat.from(this);
        list = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        order.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        for(DataSnapshot d : dataSnapshot.child("Food").getChildren()){
            list.add(d.getValue(Food_Cart.class));
        }
        Order o = new Order();
        o.setListFood(list);
        o.setUserId(dataSnapshot.child("userId").getValue().toString());
        o.setTotal(dataSnapshot.child("total").getValue().toString());
        o.setStatus(dataSnapshot.child("status").getValue().toString());
        o.setPhone(dataSnapshot.child("phone").getValue().toString());
        o.setAddress(dataSnapshot.child("address").getValue().toString());

        if(o.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            showNoti(dataSnapshot.getKey(), o);
        }
    }

    private void showNoti(String key, Order o) {
        Intent intent = new Intent(getBaseContext(), OrderList.class);
        PendingIntent contenIntent = PendingIntent.getActivity(getBaseContext(),0,intent,0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("BKFood")
                .setContentInfo("Trạng thái đơn hàng")
                .setContentText("Đơn hàng #" + key +" của bạn được cập nhật thành "+o.getStatus())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contenIntent)
                .build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
