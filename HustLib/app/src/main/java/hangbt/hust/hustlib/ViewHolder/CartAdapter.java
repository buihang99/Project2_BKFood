package hangbt.hust.hustlib.ViewHolder;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hangbt.hust.hustlib.CartView;
import hangbt.hust.hustlib.Interface.ItemClickListener;
import hangbt.hust.hustlib.Model.Food_Cart;
import hangbt.hust.hustlib.R;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName, txtPrice;
    public ImageView itemImage;
    public ElegantNumberButton number;
    public ImageButton imgdelete;

    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.itemName);
        txtPrice = itemView.findViewById(R.id.itemPrice);
        itemImage = itemView.findViewById(R.id.itemImage);
        number = itemView.findViewById(R.id.number_button);
        imgdelete = itemView.findViewById(R.id.imgDelete);
    }

    public void setTxtName(TextView txtName) {
        this.txtName = txtName;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Food_Cart> list = new ArrayList<>();
    private CartView cart;

    DatabaseReference data = FirebaseDatabase.getInstance().getReference();
    FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    String userId = mAuth.getUid();

    public CartAdapter(List<Food_Cart> list, CartView cart) {
        this.list = list;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {
        TextDrawable drawable = TextDrawable.builder().buildRound(""+list.get(position).getFoodImage(), Color.RED);

        Picasso.with(cart).load(list.get(position).getFoodImage()).into(holder.itemImage);
        int curPosition = position;
        final Food_Cart foodCart = list.get(position);

        holder.txtPrice.setText(list.get(position).getFoodPrice());
        holder.number.setNumber(list.get(position).getFoodAmount());
        holder.txtName.setText(list.get(position).getFoodName());

        holder.number.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Food_Cart f = list.get(position);
                f.setFoodAmount(String.valueOf(newValue));
                list.get(position).setFoodAmount(String.valueOf(newValue));

                //data.child("Cart").child(userId).child("Foods").child(f.getFoodId()).child("foodAmount").setValue(f.getFoodAmount());
                data.child("Cart").child(userId).child("Foods").child(list.get(position).getFoodId()).setValue(list.get(position));
                int total = 0;
                for(Food_Cart t : list){
                    int p = Integer.parseInt(t.getFoodPrice());
                    int d = Integer.parseInt(t.getFoodDiscount());
                    int a = Integer.parseInt(t.getFoodAmount());
                    total += (p-d)*a;
                }
                //data.child("Cart").child(userId).child("total").setValue(total);
                cart.txttotal.setText(String.valueOf(total));
                notifyDataSetChanged();
            }
        });

        holder.imgdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(holder);
            }
        });
    }

    private void removeItem(CartViewHolder holder) {
        int currposition = holder.getAdapterPosition();
        Food_Cart foodCart = list.get(currposition);
        list.remove(currposition);
        notifyItemRemoved(currposition);
        data.child("Cart").child(userId).child("Foods").child(foodCart.getFoodId()).removeValue();
        int total = 0;
        for(Food_Cart t : list){
            int p = Integer.parseInt(t.getFoodPrice());
            int d = Integer.parseInt(t.getFoodDiscount());
            int a = Integer.parseInt(t.getFoodAmount());
            total += (p-d)*a;
        }
        //data.child("Cart").child(userId).child("total").setValue(total);
        cart.txttotal.setText(String.valueOf(total));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
