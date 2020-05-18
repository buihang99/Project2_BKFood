package hangbt.hust.bkfoodserver.ViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import hangbt.hust.bkfoodserver.Model.Food_Cart;
import hangbt.hust.bkfoodserver.R;

class FoodHolder extends RecyclerView.ViewHolder{

    public TextView foodname, price, amount, discount;
    public ImageView image;

    public FoodHolder(@NonNull View itemView) {
        super(itemView);
        foodname = itemView.findViewById(R.id.itemName);
        price = itemView.findViewById(R.id.itemPrice);
        amount = itemView.findViewById(R.id.itemAmound);
        image = itemView.findViewById(R.id.itemImage);
        discount = itemView.findViewById(R.id.itemDiscount);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<FoodHolder> {

    List<Food_Cart> list;
    Context context;

    public OrderDetailAdapter(List<Food_Cart> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.order_food_item,parent,false);
        return new FoodHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodHolder holder, int position) {
        Food_Cart food = list.get(position);
        holder.foodname.setText(food.getFoodName());
        holder.price.setText("Price:  "+food.getFoodPrice());
        holder.amount.setText("Quantity:  "+food.getFoodAmount());
        holder.discount.setText("Discount:  "+food.getFoodDiscount()+"$");
        Picasso.with(context).load(list.get(position).getFoodImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

