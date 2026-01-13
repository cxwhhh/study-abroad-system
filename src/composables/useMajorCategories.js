import { reactive } from 'vue';

export function useMajorCategories() {
  const majorCategories = reactive({
    humanitiesSocial: {
      label: "人文社科",
      majors: [
        { label: "历史学", value: "历史学" },
        { label: "哲学", value: "哲学" },
        { label: "社会学", value: "社会学" },
        { label: "人类学", value: "人类学" },
        { label: "政治学", value: "政治学" },
        { label: "国际关系", value: "国际关系" },
        { label: "公共政策", value: "公共政策" },
        { label: "公共管理", value: "公共管理" },
      ],
    },
    media: {
      label: "传媒大类",
      majors: [
        { label: "新闻学", value: "新闻学" },
        { label: "传播学", value: "传播学" },
        { label: "新媒体", value: "新媒体" },
        { label: "数字媒体", value: "数字媒体" },
        { label: "影视制作", value: "影视制作" },
        { label: "广告学", value: "广告学" },
        { label: "公关与品牌传播", value: "公关与品牌传播" },
      ],
    },
    business: {
      label: "商科",
      majors: [
        { label: "会计", value: "会计" },
        { label: "金融", value: "金融" },
        { label: "经济学", value: "经济学" },
        { label: "商业分析", value: "商业分析" },
        { label: "管理学", value: "管理学" },
        { label: "市场营销", value: "市场营销" },
        { label: "人力资源管理", value: "人力资源管理" },
        { label: "供应链管理", value: "供应链管理" },
      ],
    },
    engineering: {
      label: "工科",
      majors: [
        { label: "机械工程", value: "机械工程" },
        { label: "电子工程", value: "电子工程" },
        { label: "电气工程", value: "电气工程" },
        { label: "土木工程", value: "土木工程" },
        { label: "材料工程", value: "材料工程" },
        { label: "化学工程", value: "化学工程" },
        { label: "工业工程", value: "工业工程" },
        { label: "环境工程", value: "环境工程" },
      ],
    },
    architectureUrban: {
      label: "建筑与城规",
      majors: [
        { label: "建筑学", value: "建筑学" },
        { label: "城市规划", value: "城市规划" },
        { label: "景观设计", value: "景观设计" },
        { label: "城乡规划", value: "城乡规划" },
        { label: "城市设计", value: "城市设计" },
        { label: "可持续建筑", value: "可持续建筑" },
      ],
    },
    mathData: {
      label: "数学与数据",
      majors: [
        { label: "数学", value: "数学" },
        { label: "统计学", value: "统计学" },
        { label: "数据科学", value: "数据科学" },
        { label: "数据分析", value: "数据分析" },
        { label: "精算", value: "精算" },
        { label: "运筹学", value: "运筹学" },
        { label: "量化金融", value: "量化金融" },
      ],
    },
    science: {
      label: "理科",
      majors: [
        { label: "物理", value: "物理" },
        { label: "化学", value: "化学" },
        { label: "地球科学", value: "地球科学" },
        { label: "天文学", value: "天文学" },
        { label: "环境科学", value: "环境科学" },
        { label: "地理信息科学", value: "地理信息科学" },
      ],
    },
    lifeScienceMedicine: {
      label: "生命科学/医学",
      majors: [
        { label: "生物学", value: "生物学" },
        { label: "生物医学", value: "生物医学" },
        { label: "生物信息学", value: "生物信息学" },
        { label: "公共卫生", value: "公共卫生" },
        { label: "药学", value: "药学" },
        { label: "营养与食品科学", value: "营养与食品科学" },
      ],
    },
    socialScience: {
      label: "社会科学",
      majors: [
        { label: "心理学", value: "心理学" },
        { label: "教育学", value: "教育学" },
        { label: "社会政策", value: "社会政策" },
        { label: "法律", value: "法律" },
        { label: "犯罪学", value: "犯罪学" },
        { label: "社会工作", value: "社会工作" },
      ],
    },
    computer: {
      label: "计算机大类",
      majors: [
        { label: "计算机科学", value: "计算机科学" },
        { label: "软件工程", value: "软件工程" },
        { label: "人工智能", value: "人工智能" },
        { label: "网络安全", value: "网络安全" },
        { label: "人机交互", value: "人机交互" },
        { label: "信息系统", value: "信息系统" },
        { label: "机器学习", value: "机器学习" },
      ],
    },
    artDesign: {
      label: "设计、表演和艺术",
      majors: [
        { label: "平面设计", value: "平面设计" },
        { label: "交互设计", value: "交互设计" },
        { label: "工业设计", value: "工业设计" },
        { label: "产品设计", value: "产品设计" },
        { label: "时尚设计", value: "时尚设计" },
        { label: "表演", value: "表演" },
        { label: "音乐", value: "音乐" },
        { label: "艺术管理", value: "艺术管理" },
      ],
    },
    languageEducation: {
      label: "语言/教育",
      majors: [
        { label: "英语语言文学", value: "英语语言文学" },
        { label: "语言学", value: "语言学" },
        { label: "翻译与口译", value: "翻译与口译" },
        { label: "对外汉语", value: "对外汉语" },
        { label: "TESOL", value: "TESOL" },
        { label: "教育技术", value: "教育技术" },
        { label: "教育管理", value: "教育管理" },
      ],
    },
  });

  const expandedCategories = reactive({
    humanitiesSocial: true,
    media: false,
    business: false,
    engineering: false,
    architectureUrban: false,
    mathData: false,
    science: false,
    lifeScienceMedicine: false,
    socialScience: false,
    computer: false,
    artDesign: false,
    languageEducation: false,
  });

  const toggleCategory = (category) => {
    expandedCategories[category] = !expandedCategories[category];
  };

  return {
    majorCategories,
    expandedCategories,
    toggleCategory,
  };
}
