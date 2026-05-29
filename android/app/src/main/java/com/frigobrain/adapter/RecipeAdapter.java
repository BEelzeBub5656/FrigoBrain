package com.frigobrain.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frigobrain.R;
import com.frigobrain.data.db.entity.Recipe;
import com.frigobrain.ui.recipe.RecipeDetailActivity;

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
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);
            intent.putExtra("recipeId", recipe.getRecipeId());
            intent.putExtra("recipeName", recipe.getName());
            intent.putExtra("instructions", recipe.getInstructions());
            intent.putExtra("calories", recipe.getCalories());
            intent.putExtra("protein", recipe.getProtein());
            intent.putExtra("fat", recipe.getFat());
            intent.putExtra("carbs", recipe.getCarbs());
            intent.putExtra("tags", recipe.getTags());
            v.getContext().startActivity(intent);
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
