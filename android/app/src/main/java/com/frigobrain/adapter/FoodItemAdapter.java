package com.frigobrain.adapter;

import android.graphics.Color;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frigobrain.R;
import com.frigobrain.data.db.entity.FoodCategory;
import com.frigobrain.data.db.entity.FoodItem;
import com.frigobrain.data.model.FoodWithCategory;
import com.frigobrain.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.ViewHolder> {

    private List<FoodWithCategory> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodWithCategory item = items.get(position);
        FoodItem food = item.food;
        FoodCategory category = item.category;

        holder.tvName.setText(food.getName());
        holder.tvCategory.setText(category != null ? category.getName() : "");
        holder.tvQuantity.setText(food.getQuantity() + " " + food.getUnit());

        int daysLeft = DateUtils.daysUntil(food.getExpiryDate());
        String statusText;
        int statusColor;

        if (daysLeft < 0) {
            statusText = "已过期 " + Math.abs(daysLeft) + " 天";
            statusColor = Color.parseColor("#F44336");
        } else if (daysLeft <= 3) {
            statusText = "还剩 " + daysLeft + " 天";
            statusColor = Color.parseColor("#FFC107");
        } else {
            statusText = "还剩 " + daysLeft + " 天";
            statusColor = Color.parseColor("#4CAF50");
        }
        holder.tvExpiry.setText(statusText);
        holder.tvExpiry.setTextColor(statusColor);
        holder.vStatus.setBackgroundColor(statusColor);

        holder.itemView.setOnClickListener(v -> {
            String msg = food.getName() + " · " + statusText;
            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<FoodWithCategory> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View vStatus;
        TextView tvName, tvCategory, tvExpiry, tvQuantity;

        ViewHolder(View itemView) {
            super(itemView);
            vStatus = itemView.findViewById(R.id.v_status);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvExpiry = itemView.findViewById(R.id.tv_expiry);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}
