/**
 * 统一 Toast：偏移量避开 sticky 顶栏，避免被遮挡
 */
import { ElMessage } from 'element-plus'

/** 顶栏 sticky 高度 60px，消息下移避免被遮挡 */
const TOAST_OFFSET = 72
const TOAST_Z_INDEX = 10000

const baseOptions = {
  offset: TOAST_OFFSET,
  zIndex: TOAST_Z_INDEX
}

/** 统一成功/警告/错误提示入口 */
export const toast = {
  success: (message) => ElMessage.success({ message, ...baseOptions }),
  warning: (message) => ElMessage.warning({ message, ...baseOptions }),
  error: (message) => ElMessage.error({ message, ...baseOptions })
}
