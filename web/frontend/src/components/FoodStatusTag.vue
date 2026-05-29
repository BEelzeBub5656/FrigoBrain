<template>
  <el-tag :type="tagType" :color="tagColor" effect="dark" size="small" round>
    <el-icon style="margin-right: 4px;" :size="12">
      <component :is="statusIcon" />
    </el-icon>
    {{ statusText }}
  </el-tag>
</template>

<script setup>
import { computed } from 'vue'
import { CircleCheck, Warning, CircleClose } from '@element-plus/icons-vue'

const props = defineProps({
  expiryDate: { type: [String, Date, Number], required: true }
})

const daysUntilExpiry = computed(() => {
  const now = new Date()
  const expiry = new Date(props.expiryDate)
  const diff = expiry.getTime() - now.getTime()
  return Math.ceil(diff / 86400000)
})

const statusText = computed(() => {
  const days = daysUntilExpiry.value
  if (days < 0) return 'Expired'
  if (days === 0) return 'Expires Today'
  if (days <= 3) return `Expires in ${days}d`
  if (days <= 7) return `${days} days left`
  return 'Fresh'
})

const tagType = computed(() => {
  const days = daysUntilExpiry.value
  if (days < 0) return 'danger'
  if (days <= 3) return 'warning'
  if (days <= 7) return ''
  return 'success'
})

const tagColor = computed(() => {
  const days = daysUntilExpiry.value
  if (days < 0) return '#F56C6C'
  if (days <= 3) return '#E6A23C'
  if (days <= 7) return '#4CAF50'
  return '#67C23A'
})

const statusIcon = computed(() => {
  const days = daysUntilExpiry.value
  if (days < 0) return CircleClose
  if (days <= 3) return Warning
  return CircleCheck
})
</script>
