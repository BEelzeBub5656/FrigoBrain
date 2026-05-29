package com.frigobrain.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frigobrain.MainActivity;
import com.frigobrain.R;
import com.frigobrain.data.db.entity.Recipe;
import com.frigobrain.util.RecipeHolder;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.tvName.setText(recipe.getName());

        // Tags and difficulty
        String info = "";
        if (recipe.getDifficulty() != null) info += "难度:" + recipe.getDifficulty() + "  ";
        if (recipe.getCookTime() > 0) info += recipe.getCookTime() + "分钟";
        holder.tvTags.setText(info.isEmpty() ? recipe.getTags() : info);

        // Source indicator
        int isSystem = recipe.getIsSystem();
        if (isSystem == 2) {
            holder.tvSource.setText("来自网络");
            holder.tvSource.setVisibility(View.VISIBLE);
        } else {
            holder.tvSource.setVisibility(View.GONE);
        }

        // Match rate in tags (set by RecipeListActivity)
        if (recipe.getTags() != null && recipe.getTags().contains("匹配")) {
            String match = recipe.getTags().replaceAll(".*匹配(\\d+%).*", "$1");
            holder.tvMatch.setText(match);
            holder.tvMatch.setVisibility(View.VISIBLE);
        } else {
            holder.tvMatch.setVisibility(View.GONE);
        }

        // Nutrition summary
        holder.tvMissing.setText(recipe.getCalories() + "kcal | 蛋白" +
                (int)recipe.getProtein() + "g | 碳水" + (int)recipe.getCarbs() + "g");

        holder.itemView.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                .setTitle(recipe.getName())
                .setMessage(recipe.getCalories() + "kcal | 蛋白" + (int)recipe.getProtein()
                    + "g | 碳水" + (int)recipe.getCarbs() + "g\n\n已挂载到主页")
                .setPositiveButton("查看主页", (d, w) -> {
                    RecipeHolder.selected = recipe;
                    Intent i = new Intent(v.getContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    v.getContext().startActivity(i);
                })
                .setNegativeButton("取消", null)
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes != null ? newRecipes : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTags, tvMatch, tvMissing, tvSource;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTags = itemView.findViewById(R.id.tv_tags);
            tvMatch = itemView.findViewById(R.id.tv_match);
            tvMissing = itemView.findViewById(R.id.tv_missing);
            tvSource = itemView.findViewById(R.id.tv_source);
        }
    }
}
