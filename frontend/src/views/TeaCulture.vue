<template>
  <div class="tea-culture-page">
    <div class="banner">
      <div class="banner-overlay">
        <div class="container banner-inner">
          <h1 class="banner-title">
            <el-icon><Reading /></el-icon>
            茶文化介绍
          </h1>
          <p class="banner-subtitle">从六大茶类、功夫茶冲泡到养生价值，一页读懂中国茶文化的核心脉络。</p>
          <div class="banner-actions">
            <el-button type="primary" @click="goToProducts">去选购好茶</el-button>
            <el-button @click="goHome">返回首页</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="tea-culture-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Reading /></el-icon>
            本页内容
          </h2>
          <el-link type="primary" :underline="false" @click="scrollTo('teaTypes')">跳到第一节 →</el-link>
        </div>

        <div class="quick-nav">
          <el-button round class="quick-nav-btn" @click="scrollTo('teaTypes')">中国六大茶类概览</el-button>
          <el-button round class="quick-nav-btn" @click="scrollTo('gongfu')">功夫茶冲泡流程</el-button>
          <el-button round class="quick-nav-btn" @click="scrollTo('health')">茶的养生价值</el-button>
          <el-button round class="quick-nav-btn" @click="scrollTo('famous')">知名传统名茶介绍</el-button>
          <el-button round class="quick-nav-btn" @click="scrollTo('wares')">传统茶器文化</el-button>
          <el-button round class="quick-nav-btn" @click="scrollTo('seasons')">四季饮茶指南</el-button>
        </div>

        <div
          class="culture-layout"
          :class="{ 'culture-layout-reverse': index % 2 === 1 }"
          v-for="(section, index) in cultureSections"
          :key="section.title"
          :ref="(el) => registerSectionEl(section.key, el)"
        >
          <div class="culture-left">
            <h3>{{ section.title }}</h3>
            <p class="culture-desc">{{ section.desc }}</p>

            <div v-if="section.key === 'teaTypes'" class="chips">
              <el-tag effect="light">绿茶（不发酵）</el-tag>
              <el-tag effect="light" type="warning">黄茶（轻发酵）</el-tag>
              <el-tag effect="light" type="info">白茶（微发酵）</el-tag>
              <el-tag effect="light" type="success">青茶/乌龙茶（半发酵）</el-tag>
              <el-tag effect="light" type="danger">红茶（全发酵）</el-tag>
              <el-tag effect="light">黑茶（后发酵）</el-tag>
            </div>

            <ol v-else-if="section.key === 'gongfu'" class="steps">
              <li>温杯洁具：保证茶器洁净无异味</li>
              <li>投茶：把控茶叶用量</li>
              <li>悬壶高冲：激发茶香</li>
              <li>刮沫淋盖：提升茶温</li>
              <li>关公巡城、韩信点兵：分茶均匀一致</li>
            </ol>

            <div v-else-if="section.key === 'health'" class="highlight">
              <div class="highlight-title">常见成分与作用</div>
              <div class="highlight-desc">
                茶多酚抗氧化；咖啡碱提神醒脑；茶多糖辅助调节代谢。长期适量饮茶，有助于舒缓身心、调理身体。
              </div>
            </div>

            <div v-else-if="section.key === 'famous'" class="famous-grid">
              <div class="famous-card" v-for="tea in famousTeas" :key="tea.name">
                <div class="famous-name">{{ tea.name }}</div>
                <div class="famous-note">{{ tea.note }}</div>
                <div class="famous-tags">
                  <el-tag size="small" effect="light">{{ tea.region }}</el-tag>
                  <el-tag size="small" effect="light" type="info">{{ tea.flavor }}</el-tag>
                </div>
              </div>
            </div>

            <ul v-else-if="section.key === 'wares'" class="bullets">
              <li><b>紫砂壶</b>：透气保香，适合冲泡乌龙茶</li>
              <li><b>盖碗</b>：可察色闻香，适配各类茶品</li>
              <li><b>公道杯</b>：均分茶汤，保证每杯茶风味一致</li>
              <li><b>品茗杯</b>：小巧精致，方便细品茶味</li>
            </ul>

            <div v-else class="season-grid">
              <div class="season-card" v-for="item in seasonGuide" :key="item.season">
                <div class="season-name">{{ item.season }}</div>
                <div class="season-tea">{{ item.tea }}</div>
                <div class="season-desc">{{ item.desc }}</div>
              </div>
            </div>
          </div>

          <div class="culture-right">
            <el-image :src="section.image" fit="cover" class="culture-hero-image" :preview-src-list="[section.image]" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { isValidCultureSection } from '../utils/teaCultureKeywords'
import { Reading } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const cultureSections = ref([
  {
    key: 'teaTypes',
    title: '中国六大茶类概览',
    desc: '中国茶叶根据制作工艺与发酵程度可分为六大茶类：绿茶（不发酵）、黄茶（轻发酵）、白茶（微发酵）、青茶（乌龙茶，半发酵）、红茶（全发酵）、黑茶（后发酵）。不同茶类在香气、口感与风味上各具特色，构成了丰富的中国茶文化体系。',
    image: '/IntroductionDiagram/culture-section-1.png'
  },
  {
    key: 'gongfu',
    title: '功夫茶冲泡流程',
    desc: '功夫茶是中国传统茶艺的代表，核心在于精细的冲泡步骤：温杯洁具保证茶器洁净无异味，投茶把控茶叶用量，悬壶高冲激发茶香，刮沫淋盖提升茶温，最后通过关公巡城、韩信点兵的分茶手法，让每一杯茶的浓度与风味都均匀一致，尽显茶道的细致与讲究。',
    image: '/IntroductionDiagram/culture-section-2.png'
  },
  {
    key: 'health',
    title: '茶的养生价值',
    desc: '茶叶不仅是饮品，更蕴含丰富的养生价值：茶多酚具有强大的抗氧化作用，咖啡碱可提神醒脑，茶多糖能辅助调节代谢。不同茶类的养生侧重各有不同，长期适量饮茶，有助于舒缓身心、调理身体，是中式养生文化的重要组成部分。',
    image: '/IntroductionDiagram/culture-section-3.png'
  },
  {
    key: 'famous',
    title: '知名传统名茶介绍',
    desc: '中国名茶众多，其中西湖龙井、安溪铁观音、云南普洱、武夷岩茶是最具代表性的传统名茶：龙井的鲜爽甘醇、铁观音的兰香悠长、普洱的醇厚陈香、岩茶的岩骨花香，各自承载着不同地域的风土与制茶工艺，是中国茶文化的璀璨名片。',
    image: '/IntroductionDiagram/culture-section-4.png'
  },
  {
    key: 'wares',
    title: '传统茶器文化',
    desc: '中式茶器是茶文化的重要载体，紫砂壶透气保香，适合冲泡乌龙茶；盖碗可察色闻香，适配各类茶品；公道杯均分茶汤，保证每杯茶风味一致；品茗杯小巧精致，方便细品茶味，一套完整的茶器，是品茶仪式感的来源。',
    image: '/IntroductionDiagram/culture-section-5.png'
  },
  {
    key: 'seasons',
    title: '四季饮茶指南',
    desc: '顺应时节饮茶更贴合养生之道：春季饮茉莉花茶，可疏肝理气、驱散春困；夏季饮绿茶，清热解暑、生津止渴；秋季饮乌龙茶，润燥降火、调和脾胃；冬季饮红茶，温中暖胃、抵御寒邪，应季而饮，才能更好地发挥茶的价值。',
    image: '/IntroductionDiagram/culture-section-6.png'
  }
])

const sectionEls = ref({})

const registerSectionEl = (key, el) => {
  if (!key || !el) return
  sectionEls.value[key] = el
}

const scrollTo = (key) => {
  const el = sectionEls.value[key]
  if (!el) return
  el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const famousTeas = ref([
  { name: '西湖龙井', note: '鲜爽甘醇，清香悠长', region: '浙江·杭州', flavor: '清鲜回甘' },
  { name: '安溪铁观音', note: '兰香悠长，韵味十足', region: '福建·安溪', flavor: '兰香馥郁' },
  { name: '云南普洱', note: '醇厚陈香，越陈越佳', region: '云南·普洱', flavor: '醇厚陈香' },
  { name: '武夷岩茶', note: '岩骨花香，层次丰富', region: '福建·武夷山', flavor: '岩韵花香' }
])

const seasonGuide = ref([
  { season: '春', tea: '茉莉花茶', desc: '疏肝理气、驱散春困' },
  { season: '夏', tea: '绿茶', desc: '清热解暑、生津止渴' },
  { season: '秋', tea: '乌龙茶', desc: '润燥降火、调和脾胃' },
  { season: '冬', tea: '红茶', desc: '温中暖胃、抵御寒邪' }
])

const scrollToSectionFromRoute = async () => {
  const section = route.query.section
  if (!section || typeof section !== 'string' || !isValidCultureSection(section)) return
  await nextTick()
  requestAnimationFrame(() => scrollTo(section))
}

onMounted(() => {
  scrollToSectionFromRoute()
})

watch(
  () => route.query.section,
  () => {
    scrollToSectionFromRoute()
  }
)

const goToProducts = () => router.push('/products')
const goHome = () => router.push('/')
</script>

<style scoped>
.tea-culture-page {
  width: 100%;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.banner {
  height: 360px;
  background-image: url('/banner/banner1.png');
  background-size: cover;
  background-position: center;
  position: relative;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.55) 0%, rgba(0, 0, 0, 0.25) 60%, rgba(0, 0, 0, 0.1) 100%);
  display: flex;
  align-items: center;
}

.banner-inner {
  padding: 0 20px;
}

.banner-title {
  margin: 0 0 10px 0;
  font-size: 40px;
  font-weight: 800;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.banner-subtitle {
  margin: 0 0 18px 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.92);
  max-width: 720px;
  line-height: 1.7;
}

.banner-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.tea-culture-section {
  background: transparent;
  padding: 2px 0 0;
}

.tea-culture-section .container {
  padding-top: 16px;
  padding-bottom: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.section-title .el-icon {
  color: var(--primary-color);
  font-size: 28px;
}

.culture-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  align-items: stretch;
  margin-bottom: 10px;
}

.culture-layout:last-child {
  margin-bottom: 0;
}

.culture-layout-reverse .culture-left {
  order: 2;
  border-radius: 0 12px 12px 0;
}

.culture-layout-reverse .culture-right {
  order: 1;
  border-radius: 12px 0 0 12px;
}

.culture-left {
  background: #fff;
  border-radius: 12px 0 0 12px;
  padding: 16px 18px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 10px;
  min-height: 260px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f3f5;
}

.culture-left h3 {
  font-size: 18px;
  color: var(--text-primary);
  line-height: 1.35;
  margin: 0;
}

.culture-left p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 13px;
}

.culture-desc {
  white-space: pre-line;
}

.quick-nav {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin: 8px 0 18px;
}

.quick-nav-btn {
  border: 1px solid #eef0f2;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.steps {
  margin: 10px 0 0;
  padding-left: 18px;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.85;
}

.highlight {
  margin-top: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.08) 0%, rgba(103, 194, 58, 0.06) 100%);
  border: 1px solid rgba(64, 158, 255, 0.18);
}

.highlight-title {
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.highlight-desc {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.8;
}

.bullets {
  margin: 10px 0 0;
  padding-left: 18px;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.9;
}

.bullets b {
  color: var(--text-primary);
}

.famous-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.famous-card {
  border: 1px solid #f1f3f5;
  border-radius: 12px;
  padding: 12px 12px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.famous-name {
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.famous-note {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.7;
  margin-bottom: 10px;
}

.famous-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.season-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.season-card {
  border: 1px solid #f1f3f5;
  border-radius: 12px;
  padding: 12px 12px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.season-name {
  font-weight: 900;
  font-size: 18px;
  color: var(--primary-color);
  margin-bottom: 4px;
}

.season-tea {
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.season-desc {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.culture-right {
  background: #f5f6f7;
  border-radius: 0 12px 12px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f3f5;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-self: stretch;
  min-height: 260px;
  min-width: 0;
}

.culture-hero-image {
  flex: 1 1 auto;
  width: 100%;
  min-height: 0;
  height: 100%;
  display: block;
  position: relative;
}

.culture-hero-image :deep(.el-image__wrapper) {
  width: 100% !important;
  height: 100% !important;
}

.culture-hero-image :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.tips-grid {
  margin-top: 18px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.tip-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px 16px;
  border: 1px solid #f1f3f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.tip-title {
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.tip-desc {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

@media (max-width: 768px) {
  .banner {
    height: 300px;
  }

  .banner-title {
    font-size: 32px;
  }

  .culture-layout {
    grid-template-columns: 1fr;
    gap: 8px;
    min-height: 0;
  }

  .culture-left,
  .culture-right,
  .culture-layout-reverse .culture-left,
  .culture-layout-reverse .culture-right {
    order: initial;
    border-radius: 12px;
  }

  .culture-left {
    min-height: 0;
  }

  .culture-right {
    min-height: 220px;
    height: 240px;
    flex: none;
  }

  .culture-hero-image {
    flex: none;
    height: 100%;
    min-height: 220px;
  }

  .famous-grid {
    grid-template-columns: 1fr;
  }

  .season-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .banner-title {
    font-size: 28px;
  }

  .season-grid {
    grid-template-columns: 1fr;
  }
}
</style>

