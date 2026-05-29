<template>
  <div class="inventory">
    <div class="page-header">
      <div>
        <h2 class="page-title">库存管理</h2>
        <p class="page-desc">管理冰箱食材</p>
      </div>
      <el-button type="primary" @click="showAddDialog = true" :icon="Plus">
        添加食材
      </el-button>
    </div>

    <!-- Filters -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索食材..."
          :prefix-icon="Search"
          clearable
          class="search-input"
          @input="handleSearch"
        />
        <div class="category-filters">
          <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="Vegetable">蔬菜</el-radio-button>
            <el-radio-button value="Fruit">水果</el-radio-button>
            <el-radio-button value="Meat">肉类</el-radio-button>
            <el-radio-button value="Dairy">乳制品</el-radio-button>
            <el-radio-button value="Other">其他</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </el-card>

    <!-- Food Table -->
    <el-card shadow="never" class="table-card">
      <el-table
        :data="filteredFoods"
        stripe
        style="width: 100%"
        v-loading="loading"
        empty-text="未找到食材"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="name" label="名称" min-width="160" sortable="custom">
          <template #default="{ row }">
            <div class="food-name-cell">
              <el-icon :size="18" :color="categoryColor(row.category)">
                <component :is="categoryIcon(row.category)" />
              </el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="110" align="center">
          <template #default="{ row }">
            <el-tag :color="categoryColor(row.category)" effect="dark" size="small" style="border: none;">
              {{ row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" align="center">
          <template #default="{ row }">
            {{ row.quantity }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="expiryDate" label="过期日期" width="130" align="center" sortable="custom">
          <template #default="{ row }">
            {{ row.expiryDate }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="140" align="center">
          <template #default="{ row }">
            <FoodStatusTag :expiry-date="row.expiryDate" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-popconfirm
              title="确认删除？"
              confirm-button-text="删除"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" size="small" :icon="Delete" circle plain />
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add Food Dialog -->
    <el-dialog
      v-model="showAddDialog"
      title="添加食材"
      width="520px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="110px"
        label-position="left"
        status-icon
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="例如：有机胡萝卜" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择" style="width: 100%">
                <el-option label="蔬菜" value="Vegetable" />
                <el-option label="水果" value="Fruit" />
                <el-option label="肉类" value="Meat" />
                <el-option label="乳制品" value="Dairy" />
                <el-option label="其他" value="Other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="0.1" :step="0.5" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="form.unit" placeholder="请选择" style="width: 100%">
                <el-option label="kg" value="kg" />
                <el-option label="g" value="g" />
                <el-option label="L" value="L" />
                <el-option label="pcs" value="pcs" />
                <el-option label="box" value="box" />
                <el-option label="bag" value="bag" />
                <el-option label="heads" value="heads" />
                <el-option label="blocks" value="blocks" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="过期日期" prop="expiryDate">
              <el-date-picker
                v-model="form.expiryDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="价格（$）" prop="price">
          <el-input-number v-model="form.price" :min="0" :step="0.5" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleAdd">
          {{ submitting ? '添加中...' : '添加食材' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus, Search, Delete,
  ColdDrink, Cherry, Goblet, ShoppingTrolley, Coin
} from '@element-plus/icons-vue'
import FoodStatusTag from '@/components/FoodStatusTag.vue'
import { getFoods, addFood, deleteFood } from '@/api/foods'

const loading = ref(false)
const submitting = ref(false)
const searchQuery = ref('')
const selectedCategory = ref('')
const sortField = ref('')
const sortOrder = ref('')
const showAddDialog = ref(false)
const formRef = ref(null)
const allFoods = ref([])

const form = reactive({
  name: '',
  category: 'Vegetable',
  quantity: 1,
  unit: 'kg',
  expiryDate: '',
  price: 0
})

const formRules = {
  name: [{ required: true, message: '请输入食材名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  expiryDate: [{ required: true, message: '请选择过期日期', trigger: 'change' }]
}

const filteredFoods = computed(() => {
  let result = [...allFoods.value]

  if (selectedCategory.value) {
    result = result.filter(f => f.category === selectedCategory.value)
  }

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(f => f.name.toLowerCase().includes(q))
  }

  if (sortField.value) {
    result.sort((a, b) => {
      let valA = a[sortField.value]
      let valB = b[sortField.value]
      if (sortField.value === 'expiryDate' || sortField.value === 'name') {
        if (sortField.value === 'expiryDate') {
          valA = new Date(valA).getTime()
          valB = new Date(valB).getTime()
        } else {
          valA = String(valA).toLowerCase()
          valB = String(valB).toLowerCase()
        }
      }
      if (valA < valB) return sortOrder.value === 'ascending' ? -1 : 1
      if (valA > valB) return sortOrder.value === 'ascending' ? 1 : -1
      return 0
    })
  }

  return result
})

function categoryColor(cat) {
  const colors = {
    Vegetable: '#4CAF50',
    Fruit: '#FF9800',
    Meat: '#F56C6C',
    Dairy: '#409EFF',
    Other: '#9C27B0'
  }
  return colors[cat] || '#909399'
}

function categoryIcon(cat) {
  const icons = {
    Vegetable: ColdDrink,
    Fruit: Cherry,
    Meat: Goblet,
    Dairy: ShoppingTrolley,
    Other: Coin
  }
  return icons[cat] || Coin
}

function handleSearch() {
  // computed will handle it
}

function handleCategoryChange(val) {
  selectedCategory.value = val
}

function handleSortChange({ prop, order }) {
  sortField.value = prop || ''
  sortOrder.value = order || ''
}

async function fetchFoods() {
  loading.value = true
  try {
    allFoods.value = await getFoods()
  } finally {
    loading.value = false
  }
}

async function handleAdd() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await addFood({ ...form })
      ElMessage.success('食材添加成功！')
      showAddDialog.value = false
      resetForm()
      await fetchFoods()
    } catch {
      ElMessage.error('添加食材失败')
    } finally {
      submitting.value = false
    }
  })
}

async function handleDelete(row) {
  try {
    await deleteFood(row.id)
    ElMessage.success(`"${row.name}" 已删除`)
    await fetchFoods()
  } catch {
    ElMessage.error('删除失败')
  }
}

function resetForm() {
  form.name = ''
  form.category = 'Vegetable'
  form.quantity = 1
  form.unit = 'kg'
  form.expiryDate = ''
  form.price = 0
  formRef.value?.resetFields()
}

onMounted(() => {
  fetchFoods()
})
</script>

<style scoped>
.inventory {
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
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-input {
  max-width: 400px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.category-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-filters :deep(.el-radio-button__inner) {
  border: none;
  border-radius: 8px !important;
  padding: 8px 18px;
  font-size: 13px;
  background: #f0f2f5;
  color: #606266;
  transition: all 0.2s;
}

.category-filters :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: var(--fb-primary);
  color: #fff;
  box-shadow: none;
}

.category-filters :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-left: none;
}

.table-card {
  border-radius: var(--fb-radius);
}

.table-card :deep(.el-table th.el-table__cell) {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

.table-card :deep(.el-table__row) {
  cursor: default;
}

.food-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
