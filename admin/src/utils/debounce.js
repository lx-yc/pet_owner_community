import { ElMessage } from 'element-plus'

/**
 * 按钮防抖函数（适用于点赞、收藏等按钮）
 * 第一次点击立即执行，在delay时间内忽略后续点击
 * @param {Function} fn - 需要防抖的函数
 * @param {number} delay - 防抖延迟时间（毫秒），默认500ms
 * @returns {Function} 防抖后的函数
 */
export function debounce(fn, delay = 500) {
  let lastCallTime = 0
  let timer = null

  return function (...args) {
    const now = Date.now()
    const timeSinceLastCall = now - lastCallTime

    // 如果在延迟时间内，忽略此次点击
    if (timeSinceLastCall < delay && timeSinceLastCall >= 0) {
      // 关闭之前的消息，只显示最新的
      ElMessage.closeAll()
      ElMessage.warning('操作过于频繁，请稍后再试')
      return
    }

    // 记录本次点击时间
    lastCallTime = now

    // 清除之前的定时器（如果有）
    if (timer) {
      clearTimeout(timer)
      timer = null
    }

    // 执行函数
    return fn.apply(this, args)
  }
}

/**
 * 创建带状态的防抖函数（适用于点赞、收藏等按钮）
 * 在请求完成前不允许重复点击
 * @param {Function} fn - 异步函数
 * @returns {Function} 防抖后的函数
 */
export function createAsyncDebounce(fn) {
  let isExecuting = false

  return async function (...args) {
    if (isExecuting) {
      console.warn('操作正在执行中，请勿重复点击')
      return
    }

    isExecuting = true
    try {
      return await fn.apply(this, args)
    } finally {
      isExecuting = false
    }
  }
}

/**
 * 创建按钮防抖函数（带加载状态）
 * 返回一个包含防抖函数和加载状态的对象
 * @param {Function} fn - 异步函数
 * @param {number} delay - 防抖延迟时间，默认300ms
 * @returns {Object} { debouncedFn, isLoading }
 */
export function createButtonDebounce(fn, delay = 300) {
  const executingSet = new Set()
  let lastCallTime = 0

  const debouncedFn = async function (...args) {
    const now = Date.now()
    const key = args[0]?.id || JSON.stringify(args)

    // 防抖：检查是否在延迟时间内
    if (now - lastCallTime < delay) {
      console.warn('操作过于频繁，请稍后再试')
      return
    }

    // 防重：检查是否正在执行
    if (executingSet.has(key)) {
      console.warn('该操作正在执行中，请勿重复点击')
      return
    }

    lastCallTime = now
    executingSet.add(key)

    try {
      return await fn.apply(this, args)
    } finally {
      executingSet.delete(key)
    }
  }

  return {
    debouncedFn,
    isLoading: (key) => executingSet.has(key)
  }
}
