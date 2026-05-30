/**
 * 商品详情中的茶文化关键词 → 茶文化介绍页锚点
 * section 与 TeaCulture.vue 中 cultureSections[].key 一致
 */
export const TEA_CULTURE_KEYWORDS = [
  // famous — 长词优先
  { keyword: '安溪铁观音', section: 'famous' },
  { keyword: '西湖龙井', section: 'famous' },
  { keyword: '云南普洱', section: 'famous' },
  { keyword: '武夷岩茶', section: 'famous' },
  { keyword: '铁观音', section: 'famous' },
  { keyword: '龙井', section: 'famous' },
  { keyword: '普洱', section: 'famous' },
  { keyword: '岩茶', section: 'famous' },
  // gongfu
  { keyword: '功夫茶', section: 'gongfu' },
  { keyword: '温杯洁具', section: 'gongfu' },
  { keyword: '关公巡城', section: 'gongfu' },
  { keyword: '韩信点兵', section: 'gongfu' },
  // teaTypes
  { keyword: '中国六大茶类', section: 'teaTypes' },
  { keyword: '六大茶类', section: 'teaTypes' },
  { keyword: '乌龙茶', section: 'teaTypes' },
  { keyword: '不发酵', section: 'teaTypes' },
  { keyword: '全发酵', section: 'teaTypes' },
  { keyword: '半发酵', section: 'teaTypes' },
  { keyword: '后发酵', section: 'teaTypes' },
  { keyword: '轻发酵', section: 'teaTypes' },
  { keyword: '微发酵', section: 'teaTypes' },
  { keyword: '绿茶', section: 'teaTypes' },
  { keyword: '黄茶', section: 'teaTypes' },
  { keyword: '白茶', section: 'teaTypes' },
  { keyword: '青茶', section: 'teaTypes' },
  { keyword: '红茶', section: 'teaTypes' },
  { keyword: '黑茶', section: 'teaTypes' },
  // health
  { keyword: '茶多酚', section: 'health' },
  { keyword: '咖啡碱', section: 'health' },
  { keyword: '茶多糖', section: 'health' },
  { keyword: '养生价值', section: 'health' },
  { keyword: '养生', section: 'health' },
  // wares
  { keyword: '紫砂壶', section: 'wares' },
  { keyword: '公道杯', section: 'wares' },
  { keyword: '品茗杯', section: 'wares' },
  { keyword: '盖碗', section: 'wares' },
  { keyword: '茶器', section: 'wares' },
  // seasons
  { keyword: '四季饮茶', section: 'seasons' },
  { keyword: '茉莉花茶', section: 'seasons' },
  { keyword: '应季而饮', section: 'seasons' },
  // 通用
  { keyword: '茶文化', section: 'teaTypes' }
]

const SECTION_KEYS = new Set(TEA_CULTURE_KEYWORDS.map((item) => item.section))

/** 校验 URL section 是否为已配置的文化区块 key */
export function isValidCultureSection(section) {
  return SECTION_KEYS.has(section)
}

function escapeRegExp(str) {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

function escapeHtml(text) {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function buildKeywordPattern() {
  const sorted = [...TEA_CULTURE_KEYWORDS].sort((a, b) => b.keyword.length - a.keyword.length)
  const alternation = sorted.map((item) => escapeRegExp(item.keyword)).join('|')
  return { pattern: new RegExp(alternation, 'g'), sorted }
}

const { pattern: keywordPattern, sorted: sortedKeywords } = buildKeywordPattern()

function sectionForKeyword(keyword) {
  const item = sortedKeywords.find((entry) => entry.keyword === keyword)
  return item ? item.section : 'teaTypes'
}

function linkPlainTextSegment(text) {
  return text.replace(keywordPattern, (match) => {
    const section = sectionForKeyword(match)
    return `<a href="/tea-culture?section=${section}" class="culture-link">${match}</a>`
  })
}

/**
 * 将商品详情文案中的茶文化关键词替换为站内链接（仅处理非 HTML 标签的文本段）
 */
export function linkTeaCultureKeywords(content) {
  if (!content) return ''

  const parts = String(content).split(/(<[^>]+>)/g)
  return parts
    .map((part) => {
      if (part.startsWith('<')) {
        return part
      }
      const looksLikeHtml = /<[a-z][\s\S]*>/i.test(content)
      const text = looksLikeHtml ? part : escapeHtml(part)
      return linkPlainTextSegment(text)
    })
    .join('')
}
