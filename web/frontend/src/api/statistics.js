import request from './index'

const mockWasteMonthly = {
  categories: ['Vegetable', 'Fruit', 'Meat', 'Dairy', 'Other'],
  months: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
  data: [
    [2.5, 3.0, 2.8, 3.5, 4.0, 3.2],
    [1.8, 2.2, 2.0, 2.5, 3.0, 2.4],
    [1.2, 1.5, 1.3, 1.8, 2.0, 1.6],
    [2.0, 2.5, 2.2, 2.8, 3.2, 2.6],
    [0.8, 1.0, 0.9, 1.2, 1.5, 1.1]
  ],
  reasons: [
    { name: 'Expired', value: 45 },
    { name: 'Over-purchased', value: 25 },
    { name: 'Spoiled', value: 20 },
    { name: 'Other', value: 10 }
  ],
  summary: [
    { category: 'Vegetable', waste: 19.0, cost: 28.50 },
    { category: 'Fruit', waste: 13.9, cost: 22.40 },
    { category: 'Meat', waste: 9.4, cost: 35.60 },
    { category: 'Dairy', waste: 15.3, cost: 18.75 },
    { category: 'Other', waste: 6.5, cost: 12.30 }
  ]
}

const mockNutritionDaily = {
  dates: ['Mon 24', 'Tue 25', 'Wed 26', 'Thu 27', 'Fri 28', 'Sat 29', 'Sun 30'],
  calories: [2100, 1950, 2200, 1800, 2050, 2300, 1900],
  protein: [85, 72, 90, 68, 80, 95, 75],
  fat: [65, 55, 70, 50, 60, 75, 58],
  carbs: [250, 230, 260, 200, 240, 280, 220]
}

const mockRecommended = {
  calories: 2200,
  protein: 80,
  fat: 65,
  carbs: 260,
  fiber: 30,
  sugar: 36
}

const mockInventorySummary = {
  totalItems: 20,
  expiringSoon: 5,
  totalValue: 115.30,
  categoryDistribution: [
    { name: 'Vegetable', count: 6 },
    { name: 'Fruit', count: 3 },
    { name: 'Meat', count: 4 },
    { name: 'Dairy', count: 5 },
    { name: 'Other', count: 2 }
  ]
}

export async function getWasteMonthly() {
  try {
    const res = await request.get('/statistics/waste-monthly')
    return res.data || res
  } catch {
    return mockWasteMonthly
  }
}

export async function getNutritionDaily() {
  try {
    const res = await request.get('/statistics/nutrition-daily')
    return res.data || res
  } catch {
    return mockNutritionDaily
  }
}

export async function getTrend(days = 7) {
  try {
    const res = await request.get('/statistics/trend', { params: { days } })
    return res.data || res
  } catch {
    return {
      days: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      calories: mockNutritionDaily.calories
    }
  }
}

export async function getInventorySummary() {
  try {
    const res = await request.get('/statistics/inventory-summary')
    return res.data || res
  } catch {
    return mockInventorySummary
  }
}

export function getRecommendedIntake() {
  return mockRecommended
}
