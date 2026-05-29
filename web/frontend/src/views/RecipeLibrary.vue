<template>
  <div class="recipes">
    <div class="page-header">
      <div>
        <h2 class="page-title">Recipe Library</h2>
        <p class="page-desc">Discover recipes based on your fridge contents</p>
      </div>
    </div>

    <!-- Search & Filters -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="Search recipes by name, ingredient, or tag..."
          :prefix-icon="Search"
          clearable
          class="search-input"
          @input="fetchRecipes"
        />
        <div class="filter-row">
          <div class="filter-group">
            <span class="filter-label">Cuisine:</span>
            <el-radio-group v-model="cuisineFilter" @change="fetchRecipes" size="small">
              <el-radio-button value="">All</el-radio-button>
              <el-radio-button value="Western">Western</el-radio-button>
              <el-radio-button value="Asian">Asian</el-radio-button>
              <el-radio-button value="Italian">Italian</el-radio-button>
              <el-radio-button value="Mexican">Mexican</el-radio-button>
              <el-radio-button value="International">International</el-radio-button>
            </el-radio-group>
          </div>
          <div class="filter-group">
            <span class="filter-label">Difficulty:</span>
            <el-radio-group v-model="difficultyFilter" @change="fetchRecipes" size="small">
              <el-radio-button value="">All</el-radio-button>
              <el-radio-button value="Easy">Easy</el-radio-button>
              <el-radio-button value="Medium">Medium</el-radio-button>
              <el-radio-button value="Hard">Hard</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Recipe Grid -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>
    <div v-else-if="recipes.length === 0" class="empty-state">
      <el-empty description="No recipes found" :image-size="120" />
    </div>
    <el-row v-else :gutter="20" class="recipe-grid">
      <el-col
        v-for="recipe in recipes"
        :key="recipe.id"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
        class="recipe-col"
      >
        <el-card
          shadow="hover"
          class="recipe-card"
          @click="showDetail(recipe)"
        >
          <div class="recipe-image">
            <div class="recipe-image-placeholder">
              <el-icon :size="48" color="#C8E6C9"><Notebook /></el-icon>
            </div>
            <div class="recipe-badge">
              <el-tag size="small" effect="dark" color="#4CAF50" style="border: none;">
                {{ recipe.calories }} kcal
              </el-tag>
            </div>
          </div>
          <div class="recipe-info">
            <h3 class="recipe-name">{{ recipe.name }}</h3>
            <div class="recipe-meta">
              <el-tag
                :type="difficultyType(recipe.difficulty)"
                size="small"
                effect="plain"
              >
                {{ recipe.difficulty }}
              </el-tag>
              <span class="recipe-cuisine">{{ recipe.cuisine }}</span>
              <span class="recipe-time">{{ recipe.cookTime || '~20 min' }}</span>
            </div>
            <div class="recipe-tags">
              <el-tag
                v-for="tag in recipe.tags.slice(0, 3)"
                :key="tag"
                size="small"
                class="recipe-tag"
                type="info"
                effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Recipe Detail Drawer -->
    <el-drawer
      v-model="showDrawer"
      :title="selectedRecipe?.name || 'Recipe Detail'"
      size="500px"
      destroy-on-close
    >
      <template v-if="selectedRecipe">
        <div class="drawer-content">
          <!-- Nutrition Summary -->
          <el-row :gutter="12" class="drawer-stats">
            <el-col :span="6">
              <div class="drawer-stat">
                <span class="drawer-stat-value">{{ selectedRecipe.calories }}</span>
                <span class="drawer-stat-label">Calories</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="drawer-stat">
                <span class="drawer-stat-value">{{ selectedRecipe.nutrition?.protein || '-' }}</span>
                <span class="drawer-stat-label">Protein (g)</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="drawer-stat">
                <span class="drawer-stat-value">{{ selectedRecipe.nutrition?.fat || '-' }}</span>
                <span class="drawer-stat-label">Fat (g)</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="drawer-stat">
                <span class="drawer-stat-value">{{ selectedRecipe.nutrition?.carbs || '-' }}</span>
                <span class="drawer-stat-label">Carbs (g)</span>
              </div>
            </el-col>
          </el-row>

          <!-- Meta -->
          <div class="drawer-meta">
            <el-tag :type="difficultyType(selectedRecipe.difficulty)" size="small">
              {{ selectedRecipe.difficulty }}
            </el-tag>
            <span class="drawer-meta-item">
              <el-icon><Clock /></el-icon> {{ selectedRecipe.cookTime || '~20 min' }}
            </span>
            <span class="drawer-meta-item">
              <el-icon><Food /></el-icon> {{ selectedRecipe.cuisine }}
            </span>
          </div>

          <!-- Tags -->
          <div class="drawer-section">
            <div class="drawer-tags">
              <el-tag
                v-for="tag in selectedRecipe.tags"
                :key="tag"
                size="small"
                type="success"
                effect="plain"
                class="drawer-tag"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>

          <!-- Ingredients -->
          <div class="drawer-section">
            <h4 class="drawer-section-title">
              <el-icon :size="18"><ShoppingTrolley /></el-icon>
              Ingredients
            </h4>
            <ul class="ingredient-list">
              <li v-for="(ing, idx) in selectedRecipe.ingredients" :key="idx" class="ingredient-item">
                {{ ing }}
              </li>
            </ul>
          </div>

          <!-- Steps -->
          <div class="drawer-section">
            <h4 class="drawer-section-title">
              <el-icon :size="18"><List /></el-icon>
              Instructions
            </h4>
            <ol class="step-list">
              <li v-for="(step, idx) in selectedRecipe.steps" :key="idx" class="step-item">
                <span class="step-number">{{ idx + 1 }}</span>
                <span>{{ step }}</span>
              </li>
            </ol>
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  Search, Notebook, Clock, Food, ShoppingTrolley, List
} from '@element-plus/icons-vue'
import { getRecipes } from '@/api/recipes'

const searchQuery = ref('')
const cuisineFilter = ref('')
const difficultyFilter = ref('')
const recipes = ref([])
const loading = ref(false)
const showDrawer = ref(false)
const selectedRecipe = ref(null)

function difficultyType(d) {
  const map = { Easy: 'success', Medium: 'warning', Hard: 'danger' }
  return map[d] || 'info'
}

async function fetchRecipes() {
  loading.value = true
  try {
    recipes.value = await getRecipes({
      search: searchQuery.value,
      cuisine: cuisineFilter.value,
      difficulty: difficultyFilter.value
    })
  } finally {
    loading.value = false
  }
}

function showDetail(recipe) {
  selectedRecipe.value = recipe
  showDrawer.value = true
}

onMounted(() => {
  fetchRecipes()
})
</script>

<style scoped>
.recipes {
  max-width: 1400px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.page-desc {
  font-size: 13px;
  color: #909399;
  margin: 4px 0 0;
}

.filter-card {
  border-radius: var(--fb-radius);
  margin-bottom: 24px;
}

.filter-bar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-input {
  max-width: 500px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
  white-space: nowrap;
}

.filter-group :deep(.el-radio-button__inner) {
  border: none;
  border-radius: 8px !important;
  padding: 6px 16px;
  font-size: 12px;
  background: #f0f2f5;
  color: #606266;
}

.filter-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: var(--fb-primary);
  color: #fff;
  box-shadow: none;
}

.filter-group :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-left: none;
}

.recipe-grid {
  margin-bottom: 20px;
}

.recipe-col {
  margin-bottom: 20px;
}

.recipe-card {
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  overflow: hidden;
  height: 100%;
}

.recipe-card:hover {
  transform: translateY(-4px);
}

.recipe-card :deep(.el-card__body) {
  padding: 0;
}

.recipe-image {
  height: 160px;
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.recipe-image-placeholder {
  opacity: 0.6;
}

.recipe-badge {
  position: absolute;
  top: 12px;
  right: 12px;
}

.recipe-info {
  padding: 16px;
}

.recipe-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recipe-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
  font-size: 12px;
  color: #909399;
}

.recipe-cuisine {
  color: #606266;
}

.recipe-time {
  color: #909399;
}

.recipe-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.recipe-tag {
  font-size: 11px;
}

.loading-state, .empty-state {
  padding: 40px 0;
}

/* Drawer Styles */
.drawer-content {
  padding: 0 4px;
}

.drawer-stats {
  margin-bottom: 20px;
}

.drawer-stat {
  text-align: center;
  padding: 12px 8px;
  background: #f8f9fa;
  border-radius: 8px;
}

.drawer-stat-value {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: var(--fb-primary);
}

.drawer-stat-label {
  display: block;
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
}

.drawer-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #606266;
}

.drawer-meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.drawer-section {
  margin-bottom: 20px;
}

.drawer-section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.drawer-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.drawer-tag {
  font-size: 12px;
}

.ingredient-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
}

.ingredient-item {
  padding: 6px 10px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 13px;
  color: #606266;
}

.step-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.step-item {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}

.step-item:last-child {
  border-bottom: none;
}

.step-number {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--fb-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}
</style>
