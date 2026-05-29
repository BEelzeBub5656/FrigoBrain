package com.frigobrain.web.repository;

import com.frigobrain.web.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe r WHERE " +
           "(:query IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.tags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:cuisine IS NULL OR LOWER(r.cuisineType) = LOWER(:cuisine)) " +
           "AND (:mealType IS NULL OR LOWER(r.mealType) = LOWER(:mealType)) " +
           "AND (:tag IS NULL OR LOWER(r.tags) LIKE LOWER(CONCAT('%', :tag, '%')))")
    Page<Recipe> searchRecipes(@Param("query") String query,
                               @Param("cuisine") String cuisine,
                               @Param("mealType") String mealType,
                               @Param("tag") String tag,
                               Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE " +
           "LOWER(r.tags) LIKE LOWER(CONCAT('%', :ingredient, '%')) " +
           "OR LOWER(r.name) LIKE LOWER(CONCAT('%', :ingredient, '%'))")
    List<Recipe> findByIngredient(@Param("ingredient") String ingredient);

    List<Recipe> findByMealType(String mealType);
}
