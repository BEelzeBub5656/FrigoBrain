import request from './index'

const mockFoods = [
  { id: 1, name: 'Organic Carrots', category: 'Vegetable', quantity: 1.5, unit: 'kg', expiryDate: '2026-06-10', price: 3.99, addedDate: '2026-05-20' },
  { id: 2, name: 'Whole Milk', category: 'Dairy', quantity: 2, unit: 'L', expiryDate: '2026-06-05', price: 4.49, addedDate: '2026-05-22' },
  { id: 3, name: 'Chicken Breast', category: 'Meat', quantity: 0.8, unit: 'kg', expiryDate: '2026-06-01', price: 8.99, addedDate: '2026-05-25' },
  { id: 4, name: 'Baby Spinach', category: 'Vegetable', quantity: 0.25, unit: 'kg', expiryDate: '2026-05-31', price: 3.49, addedDate: '2026-05-26' },
  { id: 5, name: 'Greek Yogurt', category: 'Dairy', quantity: 4, unit: 'pcs', expiryDate: '2026-06-15', price: 5.99, addedDate: '2026-05-28' },
  { id: 6, name: 'Strawberries', category: 'Fruit', quantity: 1, unit: 'box', expiryDate: '2026-05-30', price: 4.99, addedDate: '2026-05-27' },
  { id: 7, name: 'Salmon Fillet', category: 'Meat', quantity: 0.6, unit: 'kg', expiryDate: '2026-06-02', price: 12.99, addedDate: '2026-05-28' },
  { id: 8, name: 'Free-Range Eggs', category: 'Dairy', quantity: 12, unit: 'pcs', expiryDate: '2026-06-20', price: 6.49, addedDate: '2026-05-29' },
  { id: 9, name: 'Avocados', category: 'Fruit', quantity: 3, unit: 'pcs', expiryDate: '2026-06-03', price: 4.50, addedDate: '2026-05-28' },
  { id: 10, name: 'Tofu', category: 'Other', quantity: 2, unit: 'blocks', expiryDate: '2026-06-08', price: 2.99, addedDate: '2026-05-26' },
  { id: 11, name: 'Romaine Lettuce', category: 'Vegetable', quantity: 2, unit: 'heads', expiryDate: '2026-06-01', price: 2.99, addedDate: '2026-05-27' },
  { id: 12, name: 'Ground Beef', category: 'Meat', quantity: 0.5, unit: 'kg', expiryDate: '2026-05-31', price: 7.99, addedDate: '2026-05-29' },
  { id: 13, name: 'Cheddar Cheese', category: 'Dairy', quantity: 0.3, unit: 'kg', expiryDate: '2026-07-01', price: 5.49, addedDate: '2026-05-25' },
  { id: 14, name: 'Blueberries', category: 'Fruit', quantity: 2, unit: 'boxes', expiryDate: '2026-06-04', price: 5.99, addedDate: '2026-05-29' },
  { id: 15, name: 'Mixed Nuts', category: 'Other', quantity: 1, unit: 'bag', expiryDate: '2026-09-01', price: 8.99, addedDate: '2026-05-20' },
  { id: 16, name: 'Tomatoes', category: 'Vegetable', quantity: 6, unit: 'pcs', expiryDate: '2026-06-06', price: 3.99, addedDate: '2026-05-30' },
  { id: 17, name: 'Pork Chops', category: 'Meat', quantity: 4, unit: 'pcs', expiryDate: '2026-06-03', price: 9.99, addedDate: '2026-05-30' },
  { id: 18, name: 'Orange Juice', category: 'Other', quantity: 1, unit: 'L', expiryDate: '2026-06-12', price: 3.99, addedDate: '2026-05-28' },
  { id: 19, name: 'Broccoli', category: 'Vegetable', quantity: 3, unit: 'heads', expiryDate: '2026-06-05', price: 2.49, addedDate: '2026-05-30' },
  { id: 20, name: 'Butter', category: 'Dairy', quantity: 2, unit: 'blocks', expiryDate: '2026-07-15', price: 4.99, addedDate: '2026-05-29' }
]

export async function getFoods(params = {}) {
  try {
    const res = await request.get('/foods', { params })
    return res.data || res
  } catch {
    let result = [...mockFoods]
    if (params.category && params.category !== 'All') {
      result = result.filter(f => f.category === params.category)
    }
    if (params.search) {
      const s = params.search.toLowerCase()
      result = result.filter(f => f.name.toLowerCase().includes(s))
    }
    if (params.sortBy) {
      result.sort((a, b) => {
        if (params.sortBy === 'expiryDate') return new Date(a.expiryDate) - new Date(b.expiryDate)
        if (params.sortBy === 'name') return a.name.localeCompare(b.name)
        return 0
      })
    }
    return result
  }
}

export async function getExpiring(days = 3) {
  try {
    const res = await request.get('/foods/expiring', { params: { days } })
    return res.data || res
  } catch {
    const now = new Date()
    const limit = new Date(now.getTime() + days * 86400000)
    return mockFoods.filter(f => {
      const d = new Date(f.expiryDate)
      return d >= now && d <= limit
    }).sort((a, b) => new Date(a.expiryDate) - new Date(b.expiryDate))
  }
}

export async function getCategories() {
  try {
    const res = await request.get('/foods/categories')
    return res.data || res
  } catch {
    return ['Vegetable', 'Fruit', 'Meat', 'Dairy', 'Other']
  }
}

export async function addFood(food) {
  try {
    const res = await request.post('/foods', food)
    return res.data || res
  } catch {
    const newFood = {
      ...food,
      id: Date.now(),
      addedDate: new Date().toISOString().split('T')[0]
    }
    mockFoods.unshift(newFood)
    return newFood
  }
}

export async function deleteFood(id) {
  try {
    const res = await request.delete(`/foods/${id}`)
    return res.data || res
  } catch {
    const idx = mockFoods.findIndex(f => f.id === id)
    if (idx !== -1) mockFoods.splice(idx, 1)
    return { success: true }
  }
}
