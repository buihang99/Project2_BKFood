package hangbt.hust.hustlib.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.squareup.picasso.Picasso;

import hangbt.hust.hustlib.Interface.ItemClickListener;
import hangbt.hust.hustlib.Model.Food;
import hangbt.hust.hustlib.R;

class FoodSuggestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView foodname, price;
    private ItemClickListener itemClickListener;

    public FoodSuggestHolder(@NonNull View itemView) {
            super(itemView);

            foodname = itemView.findViewById(R.id.foodName);
            price = itemView.findViewById(R.id.foodPrice);

            itemView.setOnClickListener(this);
            }

    public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
            }

    @Override
    public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
            }
}

public class FoodSuggestAdapter extends SuggestionsAdapter<Food, FoodSuggestHolder>  {

    public FoodSuggestAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public void onBindSuggestionHolder(Food suggestion, FoodSuggestHolder holder, int position) {
            holder.foodname.setText(suggestion.getName());
            holder.price.setText(suggestion.getPrice());
    }

    @Override
    public int getSingleViewHeight() {
        return 60;
    }

    @NonNull
    @Override
    public FoodSuggestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.food_search_item, parent, false);
        return new FoodSuggestHolder(view);
    }
}
