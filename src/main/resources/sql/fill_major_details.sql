-- 批量回填 school_programs 表的 description 和 curriculum 字段
-- 基于专业名称关键词进行匹配更新

-- 1. 计算机与信息技术类 (Computer Science & IT)
UPDATE school_programs 
SET description = '本专业深入研究计算机科学的核心理论与应用技术，涵盖软件开发、算法设计、人工智能及数据科学等领域。学生将通过理论学习与实践项目，掌握前沿的编程技能和系统架构设计能力，为进入全球科技行业打下坚实基础。',
    curriculum = '核心课程包括：高级编程技术、算法与数据结构、人工智能导论、数据库系统设计、计算机网络与安全、软件工程实践、机器学习基础、云计算架构、人机交互设计及毕业设计项目。'
WHERE (name LIKE '%计算机%' OR name LIKE '%软件%' OR name LIKE '%人工智能%' OR name LIKE '%数据%' OR name LIKE '%网络%' OR name LIKE '%信息技术%')
  AND (description IS NULL OR description = '');

-- 2. 商科、金融与管理类 (Business, Finance & Management)
UPDATE school_programs 
SET description = '本专业旨在培养具备全球视野的商业领袖与金融精英。课程结合现代管理理论与商业实战案例，深入探讨市场动态、企业战略、金融分析及组织行为。学生将获得系统的商业思维训练，提升解决复杂商业问题的能力。',
    curriculum = '核心课程包括：战略管理、公司金融、市场营销管理、组织行为学、商业分析与决策、国际商务环境、财务会计、人力资源管理、创新与创业管理及商业伦理与企业社会责任。'
WHERE (name LIKE '%商%' OR name LIKE '%金融%' OR name LIKE '%管理%' OR name LIKE '%经济%' OR name LIKE '%会计%' OR name LIKE '%市场%' OR name LIKE '%营销%')
  AND (description IS NULL OR description = '');

-- 3. 工程类 (Engineering)
UPDATE school_programs 
SET description = '本专业致力于培养能够应对未来工程挑战的专业人才。课程融合严谨的工程科学原理与创新的设计实践，涵盖结构分析、系统控制、材料科学及可持续工程等方向。学生将参与跨学科项目，提升工程设计与问题解决能力。',
    curriculum = '核心课程包括：工程数学、工程力学、材料科学基础、系统控制理论、工程设计原理、热力学与流体力学、可持续能源系统、项目管理、工程伦理及工业实习项目。'
WHERE (name LIKE '%工程%' OR name LIKE '%电子%' OR name LIKE '%机械%' OR name LIKE '%土木%' OR name LIKE '%电气%' OR name LIKE '%材料%')
  AND (description IS NULL OR description = '');

-- 4. 艺术与设计类 (Art & Design)
UPDATE school_programs 
SET description = '本专业提供充满活力的创意环境，鼓励学生探索艺术与设计的无限可能。通过工作室实践、理论研讨及跨学科合作，学生将培养独特的审美视角和批判性思维，掌握从概念构思到作品呈现的全过程技能。',
    curriculum = '核心课程包括：设计基础与理论、视觉传达设计、数字媒体艺术、艺术史与当代艺术、材料与工艺、用户体验设计、创意工作室实践、品牌设计策略、交互设计及毕业作品展。'
WHERE (name LIKE '%艺术%' OR name LIKE '%设计%' OR name LIKE '%创意%' OR name LIKE '%视觉%' OR name LIKE '%媒体%' OR name LIKE '%动画%')
  AND (description IS NULL OR description = '');

-- 5. 法律类 (Law)
UPDATE school_programs 
SET description = '本专业提供全面而深入的法学教育，旨在培养具有批判性法律思维和卓越职业道德的法律从业者。课程涵盖公法、私法及国际法等核心领域，结合模拟法庭与法律诊所实践，提升学生的法律分析与辩护能力。',
    curriculum = '核心课程包括：宪法与行政法、刑法、合同法、侵权法、国际公法、商法、法律研究方法、知识产权法、人权法及模拟法庭实践。'
WHERE (name LIKE '%法学%' OR name LIKE '%法律%' OR name LIKE '%法%')
  AND (description IS NULL OR description = '');

-- 6. 教育类 (Education)
UPDATE school_programs 
SET description = '本专业专注于教育理论与实践的深度融合，旨在培养未来的教育变革者。课程探讨学习科学、课程设计、教育政策及领导力等议题，通过学校实习与教育研究，帮助学生掌握先进的教学法和教育管理技能。',
    curriculum = '核心课程包括：教育心理学、课程与教学论、教育研究方法、比较教育学、教育政策与领导力、特殊教育导论、学习评估技术、数字时代的教学、教师专业发展及教育实习。'
WHERE (name LIKE '%教育%' OR name LIKE '%教学%')
  AND (description IS NULL OR description = '');

-- 7. 医学与健康科学类 (Medicine & Health Sciences)
UPDATE school_programs 
SET description = '本专业结合前沿的生物医学科学与临床实践，致力于培养富有同理心和专业技能的医疗健康专家。课程涵盖人体结构功能、疾病机制、公共卫生及循证医学，重点强化临床技能训练与科研能力的培养。',
    curriculum = '核心课程包括：人体解剖与生理学、病理学基础、药理学、临床诊断技能、公共卫生导论、医学伦理与法律、流行病学、健康心理学、循证医学实践及临床轮转实习。'
WHERE (name LIKE '%医%' OR name LIKE '%护理%' OR name LIKE '%药%' OR name LIKE '%健康%' OR name LIKE '%公共卫生%')
  AND (description IS NULL OR description = '');

-- 8. 默认回填 (Fallback)
-- 为所有仍为空的记录设置默认值，确保没有空白数据
UPDATE school_programs 
SET description = '本专业结合理论学习与实践应用，旨在为学生提供扎实的学科基础和广阔的职业发展前景。课程设置紧跟行业趋势，注重培养学生的批判性思维、创新能力及跨文化沟通技巧。',
    curriculum = '核心课程包括：学科基础导论、专业核心理论、研究方法论、高级研讨课、跨学科选修课、专业技能实践、行业案例分析及毕业论文/项目。'
WHERE description IS NULL OR description = '';

-- 确保 curriculum 也不为空
UPDATE school_programs
SET curriculum = '核心课程包括：学科基础导论、专业核心理论、研究方法论、高级研讨课、跨学科选修课、专业技能实践、行业案例分析及毕业论文/项目。'
WHERE curriculum IS NULL OR curriculum = '';
