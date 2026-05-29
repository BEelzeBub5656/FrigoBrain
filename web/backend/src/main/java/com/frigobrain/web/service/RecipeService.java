package com.frigobrain.web.service;

import com.frigobrain.web.entity.FoodItem;
import com.frigobrain.web.entity.Recipe;
import com.frigobrain.web.repository.FoodRepository;
import com.frigobrain.web.repository.RecipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;

    public RecipeService(RecipeRepository recipeRepository, FoodRepository foodRepository) {
        this.recipeRepository = recipeRepository;
        this.foodRepository = foodRepository;
    }

    @Transactional(readOnly = true)
    public Page<Recipe> listRecipes(String query, String cuisine, String mealType,
                                    String tag, Pageable pageable) {
        return recipeRepository.searchRecipes(query, cuisine, mealType, tag, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    public Recipe createRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Transactional(readOnly = true)
    public List<Recipe> recommendRecipes() {
        List<FoodItem> availableFoods = foodRepository.findByIsConsumedFalse(Pageable.unpaged())
                .getContent();

        if (availableFoods.isEmpty()) {
            return recipeRepository.findAll(Pageable.ofSize(5)).getContent();
        }

        Set<String> ingredientKeywords = availableFoods.stream()
                .map(FoodItem::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        availableFoods.stream()
                .map(FoodItem::getCategoryId)
                .filter(Objects::nonNull)
                .forEach(cat -> ingredientKeywords.add(cat.toLowerCase()));

        List<Recipe> allRecipes = recipeRepository.findAll();

        Map<Recipe, Integer> scoreMap = new HashMap<>();
        for (Recipe recipe : allRecipes) {
            int score = 0;
            String recipeText = (recipe.getName() + " " + recipe.getTags()).toLowerCase();

            for (String keyword : ingredientKeywords) {
                if (recipeText.contains(keyword)) {
                    score++;
                }
            }
            if (score > 0) {
                scoreMap.put(recipe, score);
            }
        }

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Recipe, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
