import request from './index'

const mockRecipes = [
  {
    id: 1,
    name: 'Grilled Chicken Caesar Salad',
    image: '',
    calories: 380,
    difficulty: 'Easy',
    cuisine: 'Western',
    tags: ['Healthy', 'High-Protein', 'Low-Carb'],
    cookTime: '20 min',
    ingredients: [
      '200g chicken breast',
      '1 head romaine lettuce',
      '30g parmesan cheese',
      'Caesar dressing',
      'Croutons',
      'Lemon juice'
    ],
    steps: [
      'Season chicken breast with salt, pepper, and lemon juice',
      'Grill chicken for 6-7 minutes each side until fully cooked',
      'Chop romaine lettuce and place in a large bowl',
      'Slice grilled chicken and place on top of lettuce',
      'Add croutons, shaved parmesan, and Caesar dressing',
      'Toss gently and serve immediately'
    ],
    nutrition: { calories: 380, protein: 35, fat: 18, carbs: 15, fiber: 4, sugar: 3 }
  },
  {
    id: 2,
    name: 'Vegetable Stir-Fry',
    image: '',
    calories: 280,
    difficulty: 'Easy',
    cuisine: 'Asian',
    tags: ['Vegetarian', 'Quick', 'Low-Calorie'],
    cookTime: '15 min',
    ingredients: [
      '1 broccoli head',
      '2 carrots',
      '1 bell pepper',
      '200g mushrooms',
      '3 cloves garlic',
      'Soy sauce',
      'Sesame oil',
      'Ginger'
    ],
    steps: [
      'Cut all vegetables into bite-sized pieces',
      'Heat sesame oil in a wok over high heat',
      'Add minced garlic and ginger, stir for 30 seconds',
      'Add carrots and broccoli first, stir-fry for 2 minutes',
      'Add bell pepper and mushrooms, stir-fry for 3 more minutes',
      'Add soy sauce to taste, toss well, and serve'
    ],
    nutrition: { calories: 280, protein: 12, fat: 10, carbs: 35, fiber: 10, sugar: 8 }
  },
  {
    id: 3,
    name: 'Pan-Seared Salmon with Asparagus',
    image: '',
    calories: 450,
    difficulty: 'Medium',
    cuisine: 'Western',
    tags: ['High-Protein', 'Omega-3', 'Low-Carb'],
    cookTime: '25 min',
    ingredients: [
      '200g salmon fillet',
      '200g asparagus',
      '2 tbsp olive oil',
      'Lemon',
      'Garlic',
      'Salt and pepper',
      'Fresh dill'
    ],
    steps: [
      'Pat salmon dry and season with salt, pepper, and dill',
      'Heat olive oil in a non-stick pan over medium-high heat',
      'Place salmon skin-side down, cook 4 minutes',
      'Flip and cook another 3-4 minutes',
      'In another pan, sauté asparagus with garlic for 5 minutes',
      'Serve salmon with asparagus and lemon wedges'
    ],
    nutrition: { calories: 450, protein: 40, fat: 22, carbs: 10, fiber: 3, sugar: 2 }
  },
  {
    id: 4,
    name: 'Tomato Basil Pasta',
    image: '',
    calories: 420,
    difficulty: 'Easy',
    cuisine: 'Italian',
    tags: ['Vegetarian', 'Quick', 'Comfort Food'],
    cookTime: '20 min',
    ingredients: [
      '200g pasta',
      '4 ripe tomatoes',
      'Fresh basil leaves',
      '3 cloves garlic',
      'Olive oil',
      'Parmesan cheese',
      'Salt and pepper'
    ],
    steps: [
      'Boil pasta in salted water according to package directions',
      'Dice tomatoes and mince garlic',
      'Heat olive oil, sauté garlic until fragrant',
      'Add tomatoes, cook down for 8-10 minutes',
      'Toss cooked pasta with the tomato sauce',
      'Top with fresh basil and grated parmesan'
    ],
    nutrition: { calories: 420, protein: 14, fat: 12, carbs: 65, fiber: 5, sugar: 8 }
  },
  {
    id: 5,
    name: 'Beef and Broccoli',
    image: '',
    calories: 480,
    difficulty: 'Medium',
    cuisine: 'Asian',
    tags: ['High-Protein', 'Popular'],
    cookTime: '25 min',
    ingredients: [
      '250g beef sirloin, sliced thin',
      '2 cups broccoli florets',
      'Soy sauce',
      'Oyster sauce',
      'Cornstarch',
      'Garlic',
      'Ginger',
      'Sesame oil'
    ],
    steps: [
      'Marinate sliced beef with soy sauce and cornstarch for 10 minutes',
      'Blanch broccoli in boiling water for 1 minute, set aside',
      'Heat oil in wok, stir-fry beef until browned, remove',
      'Sauté garlic and ginger, add broccoli',
      'Return beef to wok, add oyster sauce mixture',
      'Stir until sauce thickens, serve over rice'
    ],
    nutrition: { calories: 480, protein: 38, fat: 20, carbs: 30, fiber: 4, sugar: 6 }
  },
  {
    id: 6,
    name: 'Fruit Smoothie Bowl',
    image: '',
    calories: 320,
    difficulty: 'Easy',
    cuisine: 'International',
    tags: ['Breakfast', 'Healthy', 'Vegan'],
    cookTime: '10 min',
    ingredients: [
      '1 banana',
      '1 cup frozen berries',
      '1/2 cup Greek yogurt',
      '1 tbsp honey',
      'Granola',
      'Chia seeds',
      'Sliced fruits for topping'
    ],
    steps: [
      'Blend banana, frozen berries, yogurt, and honey until smooth',
      'Pour into a bowl',
      'Top with granola, chia seeds, and fresh fruit slices',
      'Serve immediately'
    ],
    nutrition: { calories: 320, protein: 12, fat: 5, carbs: 60, fiber: 8, sugar: 35 }
  },
  {
    id: 7,
    name: 'Mushroom Risotto',
    image: '',
    calories: 400,
    difficulty: 'Hard',
    cuisine: 'Italian',
    tags: ['Vegetarian', 'Comfort Food', 'Creamy'],
    cookTime: '40 min',
    ingredients: [
      '1 cup arborio rice',
      '200g mixed mushrooms',
      '1 onion',
      '2 cloves garlic',
      '1/2 cup white wine',
      '4 cups vegetable broth',
      'Parmesan cheese',
      'Butter',
      'Fresh thyme'
    ],
    steps: [
      'Sauté chopped onion and garlic in butter until translucent',
      'Add sliced mushrooms, cook until golden',
      'Add rice, stir for 1 minute until edges become translucent',
      'Pour in wine, stir until absorbed',
      'Add warm broth one ladle at a time, stirring continuously',
      'Continue for 18-20 minutes until rice is creamy and al dente',
      'Fold in parmesan and butter, season, and serve'
    ],
    nutrition: { calories: 400, protein: 12, fat: 14, carbs: 58, fiber: 3, sugar: 4 }
  },
  {
    id: 8,
    name: 'Chicken Tacos',
    image: '',
    calories: 350,
    difficulty: 'Easy',
    cuisine: 'Mexican',
    tags: ['Quick', 'Fun', 'Family-Friendly'],
    cookTime: '20 min',
    ingredients: [
      '300g chicken thighs',
      '8 small corn tortillas',
      '1 avocado',
      'Salsa',
      'Sour cream',
      'Lime',
      'Cilantro',
      'Taco seasoning'
    ],
    steps: [
      'Season chicken thighs with taco seasoning',
      'Cook chicken in a skillet until done, shred with forks',
      'Warm tortillas in a dry pan',
      'Slice avocado',
      'Assemble tacos: tortilla, chicken, avocado, salsa',
      'Top with sour cream, cilantro, and lime juice'
    ],
    nutrition: { calories: 350, protein: 28, fat: 16, carbs: 28, fiber: 5, sugar: 3 }
  }
]

export async function getRecipes(params = {}) {
  try {
    const res = await request.get('/recipes', { params })
    return res.data || res
  } catch {
    let result = [...mockRecipes]
    if (params.search) {
      const s = params.search.toLowerCase()
      result = result.filter(r =>
        r.name.toLowerCase().includes(s) ||
        r.tags.some(t => t.toLowerCase().includes(s)) ||
        r.cuisine.toLowerCase().includes(s)
      )
    }
    if (params.cuisine && params.cuisine !== 'All') {
      result = result.filter(r => r.cuisine === params.cuisine)
    }
    if (params.difficulty && params.difficulty !== 'All') {
      result = result.filter(r => r.difficulty === params.difficulty)
    }
    return result
  }
}

export async function getRecommend() {
  try {
    const res = await request.get('/recipes/recommend')
    return res.data || res
  } catch {
    return mockRecipes.slice(0, 3)
  }
}

export async function getRecipeDetail(id) {
  try {
    const res = await request.get(`/recipes/${id}`)
    return res.data || res
  } catch {
    return mockRecipes.find(r => r.id === id) || null
  }
}
