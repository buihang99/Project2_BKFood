package hangbt.hust.hustlib.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hangbt.hust.hustlib.Interface.ItemClickListener;
import hangbt.hust.hustlib.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView orderId, orderPhone, orderAddress, orderTime, orderStatus;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderId = itemView.findViewById(R.id.orderId);
        orderAddress = itemView.findViewById(R.id.orderAddress);
        orderPhone = itemView.findViewById(R.id.orderPhone);
        orderTime = itemView.findViewById(R.id.orderTime);
        orderStatus = itemView.findViewById(R.id.orderStatus);

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
