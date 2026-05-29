<template>
  <div ref="cardRef" class="stat-card" :style="{ borderLeftColor: color }"
       @mouseenter="onMouseEnter" @mouseleave="onMouseLeave">
    <div class="stat-card-icon" :style="{ backgroundColor: color + '18', color: color }">
      <el-icon :size="28">
        <component :is="iconComponent" />
      </el-icon>
    </div>
    <div class="stat-card-content">
      <div class="stat-card-label">{{ label }}</div>
      <div class="stat-card-value" :style="{ color: color }">{{ value }}</div>
      <div v-if="subtext" class="stat-card-subtext">{{ subtext }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import gsap from 'gsap'
import * as Icons from '@element-plus/icons-vue'

const props = defineProps({
  icon: { type: String, default: 'InfoFilled' },
  value: { type: [String, Number], default: '' },
  label: { type: String, default: '' },
  subtext: { type: String, default: '' },
  color: { type: String, default: '#4CAF50' }
})

const cardRef = ref(null)
const iconComponent = computed(() => Icons[props.icon] || Icons.InfoFilled)

// Hover animation
function onMouseEnter() {
  if (cardRef.value) {
    gsap.to(cardRef.value, { scale: 1.03, duration: 0.25, ease: 'power2.out' })
    gsap.to(cardRef.value, { boxShadow: '0 8px 30px rgba(0,0,0,0.15)', duration: 0.25 })
  }
}
function onMouseLeave() {
  if (cardRef.value) {
    gsap.to(cardRef.value, { scale: 1, duration: 0.25, ease: 'power2.out' })
    gsap.to(cardRef.value, { boxShadow: '0 1px 6px rgba(0,0,0,0.06)', duration: 0.25 })
  }
}

// Entrance animation
onMounted(() => {
  if (cardRef.value) {
    gsap.from(cardRef.value, { y: 30, opacity: 0, duration: 0.5, ease: 'power3.out' })
  }
})
</script>
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  border-radius: var(--fb-radius);
  padding: 20px 24px;
  box-shadow: var(--fb-card-shadow);
  border-left: 4px solid var(--fb-primary);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.stat-card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-card-content {
  flex: 1;
  min-width: 0;
}

.stat-card-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-card-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
}

.stat-card-subtext {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 2px;
}
</style>
