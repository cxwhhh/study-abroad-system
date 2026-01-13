/**
 * 常量定义文件
 */

// 国家列表
export const COUNTRIES = {
  'us': '美国',
  'uk': '英国',
  'ca': '加拿大',
  'au': '澳大利亚',
  'nz': '新西兰',
  'sg': '新加坡',
  'jp': '日本',
  'kr': '韩国',
  'cn': '中国',
  'hk': '中国香港',
  'de': '德国',
  'fr': '法国',
  'it': '意大利',
  'es': '西班牙',
  'nl': '荷兰',
  'ch': '瑞士',
  'se': '瑞典',
  'fi': '芬兰',
  'no': '挪威',
  'dk': '丹麦',
  'ie': '爱尔兰',
  'ru': '俄罗斯',
  'br': '巴西',
  'za': '南非',
  'in': '印度'
};

// 学校类型
export const SCHOOL_TYPES = {
  'university': '综合大学',
  'college': '学院',
  'institute': '研究所',
  'graduate_school': '研究生院',
  'community_college': '社区学院',
  'technical_institute': '技术学院',
  'vocational_college': '职业学院',
  'liberal_arts': '文理学院'
};

// 学校所有权类型
export const OWNERSHIP_TYPES = {
  'public': '公立',
  'private': '私立',
  'private_nonprofit': '私立非营利',
  'private_for_profit': '私立营利'
};

// 申请材料
export const APPLICATION_MATERIALS = {
  'personal_statement': '个人陈述',
  'resume': '简历',
  'recommendation_letters': '推荐信',
  'transcripts': '成绩单',
  'standardized_test_scores': '标准化考试成绩',
  'language_test_scores': '语言考试成绩',
  'portfolio': '作品集',
  'research_proposal': '研究计划',
  'writing_sample': '写作样本',
  'interview': '面试',
  'financial_statement': '资金证明',
  'passport_copy': '护照复印件',
  'application_fee': '申请费'
};

// 学位类型
export const DEGREE_TYPES = [
  { code: 'ASSOCIATE', name: '副学士' },
  { code: 'BACHELOR', name: '学士' },
  { code: 'MASTER', name: '硕士' },
  { code: 'DOCTORAL', name: '博士' },
  { code: 'CERTIFICATE', name: '证书' },
  { code: 'DIPLOMA', name: '文凭' },
  { code: 'FOUNDATION', name: '预科' }
]

// 申请状态
export const APPLICATION_STATUS = {
  'pending': '待处理',
  'reviewing': '审核中',
  'approved': '已通过',
  'rejected': '已拒绝',
  'waitlisted': '候补名单',
  'deferred': '延期',
  'withdrawn': '已撤回'
};

// 研究领域
export const RESEARCH_AREAS = {
  'computer_science': '计算机科学',
  'business': '商业管理',
  'engineering': '工程',
  'medicine': '医学',
  'law': '法律',
  'arts': '艺术',
  'humanities': '人文',
  'social_sciences': '社会科学',
  'natural_sciences': '自然科学',
  'education': '教育'
};

export const STUDY_MODES = [
  { code: 'FULL_TIME', name: '全日制' },
  { code: 'PART_TIME', name: '非全日制' },
  { code: 'ONLINE', name: '在线' },
  { code: 'BLENDED', name: '混合' }
]

export const CAMPUS_SETTINGS = [
  { code: 'URBAN', name: '城市' },
  { code: 'SUBURBAN', name: '郊区' },
  { code: 'RURAL', name: '乡村' }
]

export const SEMESTER_SYSTEMS = [
  { code: 'SEMESTER', name: '学期制' },
  { code: 'QUARTER', name: '学季制' },
  { code: 'TRIMESTER', name: '三学期制' }
]

export const LANGUAGE_TESTS = [
  { code: 'TOEFL', name: '托福' },
  { code: 'IELTS', name: '雅思' },
  { code: 'DUOLINGO', name: '多邻国英语测试' },
  { code: 'PTE', name: 'PTE学术英语考试' }
]

export const STANDARDIZED_TESTS = [
  { code: 'SAT', name: 'SAT' },
  { code: 'ACT', name: 'ACT' },
  { code: 'GRE', name: 'GRE' },
  { code: 'GMAT', name: 'GMAT' },
  { code: 'LSAT', name: '法学院入学考试' },
  { code: 'MCAT', name: '医学院入学考试' }
]

export const SCHOLARSHIP_TYPES = [
  { code: 'MERIT', name: '学术优秀奖学金' },
  { code: 'NEED', name: '需求基础奖学金' },
  { code: 'ATHLETIC', name: '体育奖学金' },
  { code: 'INTERNATIONAL', name: '国际生奖学金' },
  { code: 'DEPARTMENTAL', name: '院系奖学金' }
]

export const HOUSING_TYPES = [
  { code: 'DORMITORY', name: '学生宿舍' },
  { code: 'APARTMENT', name: '公寓' },
  { code: 'HOMESTAY', name: '寄宿家庭' },
  { code: 'OFF_CAMPUS', name: '校外住宿' }
]

export const SERVICES = [
  { code: 'CAREER', name: '就业咨询' },
  { code: 'HEALTH', name: '健康服务' },
  { code: 'COUNSELING', name: '心理咨询' },
  { code: 'DISABILITY', name: '残障学生服务' },
  { code: 'INTERNATIONAL', name: '国际学生服务' },
  { code: 'TUTORING', name: '学业辅导' },
  { code: 'LIBRARY', name: '图书馆服务' },
  { code: 'TECHNOLOGY', name: '技术支持' },
  { code: 'RECREATIONAL', name: '娱乐设施' }
]

export default {
  COUNTRIES,
  SCHOOL_TYPES,
  OWNERSHIP_TYPES,
  APPLICATION_MATERIALS,
  DEGREE_TYPES,
  APPLICATION_STATUS,
  RESEARCH_AREAS,
  STUDY_MODES,
  CAMPUS_SETTINGS,
  SEMESTER_SYSTEMS,
  LANGUAGE_TESTS,
  STANDARDIZED_TESTS,
  SCHOLARSHIP_TYPES,
  HOUSING_TYPES,
  SERVICES
}; 