-- 学院数据插入SQL文件
-- 为学校ID 36, 39, 40, 35, 38 插入学院信息

-- 萨塞克斯大学 (ID: 36) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('全球研究学院', 'School of Global Studies', 36, 'SGS', '致力于全球化研究和国际关系，培养具有国际视野的人才', '成立于1961年，是英国最早开设全球研究的学院之一', 1, 'Prof. Sarah Johnson', 85, 1200, 'https://www.sussex.ac.uk/global-studies', '/images/schools/sussex-global.jpg', '国际关系,全球政治,发展研究', '全球化研究,国际政治经济,可持续发展', 'global@sussex.ac.uk', '+44 1273 606755', NOW(), NOW(), 0, 1),
('商学院', 'Business School', 36, 'SBS', '提供世界一流的商业教育，培养未来商业领袖', '建立于1965年，获得AACSB和EQUIS双重认证', 2, 'Prof. Michael Brown', 120, 2500, 'https://www.sussex.ac.uk/business-school', '/images/schools/sussex-business.jpg', '工商管理,金融学,市场营销', '创新管理,数字化转型,可持续商业', 'business@sussex.ac.uk', '+44 1273 606756', NOW(), NOW(), 0, 1),
('媒体、艺术与人文学院', 'School of Media, Arts and Humanities', 36, 'SMAH', '融合媒体、艺术和人文学科，培养创意人才', '历史悠久，在媒体研究和艺术教育方面享有盛誉', 3, 'Prof. Emma Wilson', 95, 1800, 'https://www.sussex.ac.uk/media-arts', '/images/schools/sussex-media.jpg', '媒体研究,艺术史,创意写作', '数字媒体,文化研究,视觉艺术', 'media@sussex.ac.uk', '+44 1273 606757', NOW(), NOW(), 0, 1),
('工程与信息学学院', 'School of Engineering and Informatics', 36, 'SEI', '在工程技术和计算机科学领域提供前沿教育', '成立于1970年，在人工智能和机器人技术方面领先', 4, 'Prof. David Chen', 110, 2200, 'https://www.sussex.ac.uk/engineering', '/images/schools/sussex-engineering.jpg', '计算机科学,人工智能,机器人技术', '机器学习,物联网,可持续工程', 'engineering@sussex.ac.uk', '+44 1273 606758', NOW(), NOW(), 0, 1),
('布莱顿和苏塞克斯医学院', 'Brighton and Sussex Medical School', 36, 'BSMS', '与布莱顿大学合作办学的医学院', '2002年成立，是英国最新的医学院之一', 5, 'Prof. Helen Roberts', 75, 800, 'https://www.bsms.ac.uk', '/images/schools/sussex-medical.jpg', '临床医学,生物医学,公共卫生', '医学研究,健康科学,临床实践', 'medical@bsms.ac.uk', '+44 1273 606759', NOW(), NOW(), 0, 1),
('心理学院', 'School of Psychology', 36, 'SP', '在心理学研究和教育方面享有国际声誉', '成立于1965年，在认知科学和发展心理学方面领先', 6, 'Prof. James Miller', 60, 1000, 'https://www.sussex.ac.uk/psychology', '/images/schools/sussex-psychology.jpg', '认知心理学,发展心理学,临床心理学', '神经科学,行为研究,心理健康', 'psychology@sussex.ac.uk', '+44 1273 606760', NOW(), NOW(), 0, 1),
('教育与社会工作学院', 'School of Education and Social Work', 36, 'SESW', '培养教育和社会工作专业人才', '历史悠久，在教育研究和社会政策方面有重要贡献', 7, 'Prof. Lisa Anderson', 55, 900, 'https://www.sussex.ac.uk/education', '/images/schools/sussex-education.jpg', '教育学,社会工作,社会政策', '教育创新,社会公正,儿童发展', 'education@sussex.ac.uk', '+44 1273 606761', NOW(), NOW(), 0, 1),
('数学与物理科学学院', 'School of Mathematical and Physical Sciences', 36, 'SMPS', '在数学、物理和天文学领域提供优质教育', '建立于1961年，在理论物理和应用数学方面享有声誉', 8, 'Prof. Robert Taylor', 80, 1100, 'https://www.sussex.ac.uk/mps', '/images/schools/sussex-maths.jpg', '数学,物理学,天文学', '理论物理,应用数学,天体物理', 'mps@sussex.ac.uk', '+44 1273 606762', NOW(), NOW(), 0, 1),
('法律、政治与社会学学院', 'School of Law, Politics and Sociology', 36, 'SLPS', '在法学、政治学和社会学领域提供综合教育', '成立于1960年代，在社会科学研究方面有重要影响', 9, 'Prof. Catherine White', 70, 1300, 'https://www.sussex.ac.uk/law-politics-sociology', '/images/schools/sussex-law.jpg', '法学,政治学,社会学', '社会政策,政治理论,法律研究', 'law@sussex.ac.uk', '+44 1273 606763', NOW(), NOW(), 0, 1),
('生命科学学院', 'School of Life Sciences', 36, 'SLS', '在生物科学和生命科学领域提供前沿研究和教育', '历史悠久，在分子生物学和生态学方面领先', 10, 'Prof. Mark Davis', 90, 1400, 'https://www.sussex.ac.uk/lifesci', '/images/schools/sussex-lifesci.jpg', '生物学,生态学,分子生物学', '基因研究,环境科学,生物技术', 'lifesci@sussex.ac.uk', '+44 1273 606764', NOW(), NOW(), 0, 1);

-- 东安格利亚大学 (ID: 39) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('医学与健康科学学院', 'Norwich Medical School', 39, 'NMS', '提供医学和健康科学教育，培养医疗专业人才', '2000年成立，是英国较新的医学院之一，以创新教学著称', 1, 'Prof. Sarah Thompson', 120, 1500, 'https://www.uea.ac.uk/medicine', '/images/schools/uea-medical.jpg', '临床医学,护理学,健康科学', '医学研究,公共卫生,临床实践', 'medical@uea.ac.uk', '+44 1603 591515', NOW(), NOW(), 0, 1),
('商学院', 'Norwich Business School', 39, 'NBS', '提供商业和管理教育，培养商业领袖', '成立于1963年，获得AACSB认证，在商业教育方面享有声誉', 2, 'Prof. Michael Johnson', 95, 2000, 'https://www.uea.ac.uk/business', '/images/schools/uea-business.jpg', '工商管理,会计学,金融学', '创业研究,可持续商业,数字营销', 'business@uea.ac.uk', '+44 1603 591516', NOW(), NOW(), 0, 1),
('理学院', 'Faculty of Science', 39, 'FS', '在自然科学领域提供优质教育和研究', '建立于1963年，在环境科学和生物科学方面领先', 3, 'Prof. David Wilson', 110, 1800, 'https://www.uea.ac.uk/science', '/images/schools/uea-science.jpg', '生物科学,化学,环境科学', '气候研究,生态学,可持续发展', 'science@uea.ac.uk', '+44 1603 591517', NOW(), NOW(), 0, 1),
('社会科学学院', 'Faculty of Social Sciences', 39, 'FSS', '在社会科学领域提供综合教育', '成立于1960年代，在发展研究和国际关系方面享有国际声誉', 4, 'Prof. Emma Brown', 85, 1600, 'https://www.uea.ac.uk/social-sciences', '/images/schools/uea-social.jpg', '国际关系,发展研究,社会学', '全球发展,社会政策,国际合作', 'social@uea.ac.uk', '+44 1603 591518', NOW(), NOW(), 0, 1),
('艺术和人文学院', 'Faculty of Arts and Humanities', 39, 'FAH', '在艺术和人文学科方面提供优质教育', '历史悠久，在文学研究和艺术教育方面有重要贡献', 5, 'Prof. James Miller', 75, 1200, 'https://www.uea.ac.uk/arts-humanities', '/images/schools/uea-arts.jpg', '英语文学,历史学,艺术史', '文化研究,创意写作,艺术理论', 'arts@uea.ac.uk', '+44 1603 591519', NOW(), NOW(), 0, 1);

-- 考文垂大学 (ID: 40) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('健康和生命科学学院', 'Faculty of Health and Life Sciences', 40, 'FHLS', '在健康科学和生命科学领域提供专业教育', '成立于1970年，在护理和健康科学教育方面享有声誉', 1, 'Prof. Helen Roberts', 100, 2200, 'https://www.coventry.ac.uk/health', '/images/schools/coventry-health.jpg', '护理学,生物医学,公共卫生', '健康研究,医疗技术,康复科学', 'health@coventry.ac.uk', '+44 24 7765 8000', NOW(), NOW(), 0, 1),
('商业和法律学院', 'Faculty of Business and Law', 40, 'FBL', '提供商业和法律教育，培养专业人才', '建立于1960年代，在商业教育和法律研究方面有重要地位', 2, 'Prof. Robert Taylor', 120, 2800, 'https://www.coventry.ac.uk/business', '/images/schools/coventry-business.jpg', '工商管理,法学,会计学', '商业创新,法律研究,企业管理', 'business@coventry.ac.uk', '+44 24 7765 8001', NOW(), NOW(), 0, 1),
('工程、环境和计算机学院', 'Faculty of Engineering, Environment and Computing', 40, 'FEEC', '在工程技术和计算机科学领域提供前沿教育', '成立于1970年，在汽车工程和计算机科学方面领先', 3, 'Prof. Mark Davis', 130, 3000, 'https://www.coventry.ac.uk/engineering', '/images/schools/coventry-engineering.jpg', '汽车工程,计算机科学,环境工程', '可持续技术,人工智能,机械工程', 'engineering@coventry.ac.uk', '+44 24 7765 8002', NOW(), NOW(), 0, 1),
('艺术和人文学院', 'Faculty of Arts and Humanities', 40, 'FAH', '在艺术和人文学科方面提供创新教育', '历史悠久，在媒体研究和艺术教育方面有重要贡献', 4, 'Prof. Lisa Anderson', 80, 1800, 'https://www.coventry.ac.uk/arts', '/images/schools/coventry-arts.jpg', '媒体研究,艺术设计,英语文学', '创意产业,数字媒体,文化研究', 'arts@coventry.ac.uk', '+44 24 7765 8003', NOW(), NOW(), 0, 1);

-- 诺丁汉大学 (ID: 35) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('医学与健康科学学院', 'Faculty of Medicine and Health Sciences', 35, 'FMHS', '在医学和健康科学领域提供世界一流的教育', '成立于1970年，在医学研究和临床教育方面享有国际声誉', 1, 'Prof. Catherine White', 150, 2500, 'https://www.nottingham.ac.uk/medicine', '/images/schools/nottingham-medical.jpg', '临床医学,护理学,药学', '医学研究,生物医学,公共卫生', 'medical@nottingham.ac.uk', '+44 115 951 5151', NOW(), NOW(), 0, 1),
('商学院', 'Nottingham University Business School', 35, 'NUBS', '提供世界级的商业教育，培养全球商业领袖', '建立于1960年，获得三重认证(AACSB, EQUIS, AMBA)', 2, 'Prof. Andrew Johnson', 140, 3500, 'https://www.nottingham.ac.uk/business', '/images/schools/nottingham-business.jpg', '工商管理,金融学,市场营销', '创新管理,国际商务,可持续发展', 'business@nottingham.ac.uk', '+44 115 951 5152', NOW(), NOW(), 0, 1),
('工程学院', 'Faculty of Engineering', 35, 'FE', '在工程技术领域提供前沿教育和研究', '成立于1948年，在机械工程和电子工程方面领先', 3, 'Prof. David Chen', 160, 3200, 'https://www.nottingham.ac.uk/engineering', '/images/schools/nottingham-engineering.jpg', '机械工程,电子工程,土木工程', '可持续工程,智能制造,新能源技术', 'engineering@nottingham.ac.uk', '+44 115 951 5153', NOW(), NOW(), 0, 1),
('文学院', 'Faculty of Arts', 35, 'FA', '在人文学科和艺术领域提供优质教育', '历史悠久，在英语文学和历史研究方面享有盛誉', 4, 'Prof. Emma Wilson', 100, 2200, 'https://www.nottingham.ac.uk/arts', '/images/schools/nottingham-arts.jpg', '英语文学,历史学,现代语言', '文化研究,语言学,文学理论', 'arts@nottingham.ac.uk', '+44 115 951 5154', NOW(), NOW(), 0, 1),
('理学院', 'Faculty of Science', 35, 'FS', '在自然科学领域提供卓越的教育和研究', '建立于1881年，在数学、物理和化学方面有重要贡献', 5, 'Prof. Michael Brown', 120, 2800, 'https://www.nottingham.ac.uk/science', '/images/schools/nottingham-science.jpg', '数学,物理学,化学', '理论物理,应用数学,材料科学', 'science@nottingham.ac.uk', '+44 115 951 5155', NOW(), NOW(), 0, 1),
('社会科学学院', 'Faculty of Social Sciences', 35, 'FSS', '在社会科学领域提供综合性教育', '成立于1960年代，在政治学和社会学研究方面领先', 6, 'Prof. Sarah Thompson', 90, 2000, 'https://www.nottingham.ac.uk/social-sciences', '/images/schools/nottingham-social.jpg', '政治学,社会学,经济学', '社会政策,政治理论,经济研究', 'social@nottingham.ac.uk', '+44 115 951 5156', NOW(), NOW(), 0, 1);

-- 雷丁大学 (ID: 38) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('亨利商学院', 'Henley Business School', 38, 'HBS', '世界顶级商学院，在商业教育方面享有国际声誉', '成立于1945年，是欧洲最古老的商学院之一，获得三重认证', 1, 'Prof. John Williams', 180, 4000, 'https://www.henley.ac.uk', '/images/schools/reading-henley.jpg', '工商管理,金融学,房地产', '金融创新,房地产投资,可持续商业', 'henley@reading.ac.uk', '+44 118 378 8741', NOW(), NOW(), 0, 1),
('农业、政策与发展学院', 'School of Agriculture, Policy and Development', 38, 'SAPD', '在农业科学和发展政策方面提供专业教育', '建立于1893年，在农业研究和食品科学方面享有国际声誉', 2, 'Prof. Mary Johnson', 95, 1500, 'https://www.reading.ac.uk/agriculture', '/images/schools/reading-agriculture.jpg', '农业科学,食品科学,发展研究', '可持续农业,食品安全,农村发展', 'agriculture@reading.ac.uk', '+44 118 378 8742', NOW(), NOW(), 0, 1),
('建筑与工程学院', 'School of Built Environment', 38, 'SBE', '在建筑和工程领域提供专业教育', '成立于1960年代，在建筑设计和工程管理方面领先', 3, 'Prof. Robert Taylor', 85, 1200, 'https://www.reading.ac.uk/built-environment', '/images/schools/reading-built.jpg', '建筑学,工程管理,城市规划', '可持续建筑,智慧城市,建筑技术', 'built@reading.ac.uk', '+44 118 378 8743', NOW(), NOW(), 0, 1),
('建筑系', 'Department of Architecture', 38, 'DA', '在建筑设计和理论方面提供专业教育', '历史悠久，在建筑教育和设计实践方面有重要贡献', 4, 'Prof. Lisa Anderson', 45, 600, 'https://www.reading.ac.uk/architecture', '/images/schools/reading-architecture.jpg', '建筑设计,建筑理论,城市设计', '可持续设计,建筑历史,数字建筑', 'architecture@reading.ac.uk', '+44 118 378 8744', NOW(), NOW(), 0, 1),
('影视系', 'Department of Film, Theatre & Television', 38, 'DFTT', '在影视制作和戏剧艺术方面提供专业教育', '成立于1970年代，在影视教育和创意产业方面享有声誉', 5, 'Prof. David Chen', 55, 800, 'https://www.reading.ac.uk/film-theatre-tv', '/images/schools/reading-film.jpg', '影视制作,戏剧艺术,媒体研究', '数字媒体,创意产业,视觉文化', 'film@reading.ac.uk', '+44 118 378 8745', NOW(), NOW(), 0, 1),
('心理与临床语言科学学院', 'School of Psychology and Clinical Language Sciences', 38, 'SPCLS', '在心理学和语言科学方面提供专业教育', '建立于1960年代，在认知心理学和语言治疗方面领先', 6, 'Prof. Helen Roberts', 70, 1000, 'https://www.reading.ac.uk/psychology', '/images/schools/reading-psychology.jpg', '心理学,语言治疗,认知科学', '神经科学,语言研究,心理健康', 'psychology@reading.ac.uk', '+44 118 378 8746', NOW(), NOW(), 0, 1),
('政治和国际关系系', 'Department of Politics and International Relations', 38, 'DPIR', '在政治学和国际关系方面提供专业教育', '成立于1960年代，在国际关系和政治理论方面享有声誉', 7, 'Prof. Mark Davis', 60, 900, 'https://www.reading.ac.uk/politics', '/images/schools/reading-politics.jpg', '政治学,国际关系,外交学', '国际政治,政治理论,外交研究', 'politics@reading.ac.uk', '+44 118 378 8747', NOW(), NOW(), 0, 1),
('教育学院', 'Institute of Education', 38, 'IOE', '在教育学和师范教育方面提供专业培训', '建立于1960年代，在教育研究和教师培训方面有重要贡献', 8, 'Prof. Emma Wilson', 65, 1100, 'https://www.reading.ac.uk/education', '/images/schools/reading-education.jpg', '教育学,师范教育,教育心理学', '教育创新,学习科学,教育政策', 'education@reading.ac.uk', '+44 118 378 8748', NOW(), NOW(), 0, 1),
('气象学系', 'Department of Meteorology', 38, 'DM', '在气象学和大气科学方面提供专业教育', '成立于1960年代，在气象研究和气候科学方面享有国际声誉', 9, 'Prof. James Miller', 50, 400, 'https://www.reading.ac.uk/meteorology', '/images/schools/reading-meteorology.jpg', '气象学,大气科学,气候学', '气候变化,天气预报,大气物理', 'meteorology@reading.ac.uk', '+44 118 378 8749', NOW(), NOW(), 0, 1),
('法学院', 'School of Law', 38, 'SL', '在法学教育和法律研究方面提供专业培训', '建立于1960年代，在商法和国际法方面享有声誉', 10, 'Prof. Catherine White', 75, 1300, 'https://www.reading.ac.uk/law', '/images/schools/reading-law.jpg', '法学,商法,国际法', '法律研究,商业法律,人权法', 'law@reading.ac.uk', '+44 118 378 8750', NOW(), NOW(), 0, 1),
('生物科学学院', 'School of Biological Sciences', 38, 'SBS', '在生物科学和生命科学方面提供专业教育', '历史悠久，在生物学研究和生物技术方面领先', 11, 'Prof. Andrew Johnson', 90, 1600, 'https://www.reading.ac.uk/biological-sciences', '/images/schools/reading-biology.jpg', '生物学,生物技术,生态学', '分子生物学,环境生物学,生物技术', 'biology@reading.ac.uk', '+44 118 378 8751', NOW(), NOW(), 0, 1),
('经济系', 'Department of Economics', 38, 'DE', '在经济学教育和研究方面提供专业培训', '成立于1960年代，在经济理论和应用经济学方面享有声誉', 12, 'Prof. Michael Brown', 55, 800, 'https://www.reading.ac.uk/economics', '/images/schools/reading-economics.jpg', '经济学,计量经济学,发展经济学', '经济理论,金融经济学,国际贸易', 'economics@reading.ac.uk', '+44 118 378 8752', NOW(), NOW(), 0, 1),
('考古系', 'Department of Archaeology', 38, 'DA2', '在考古学和文物保护方面提供专业教育', '历史悠久，在考古研究和文化遗产保护方面有重要贡献', 13, 'Prof. Sarah Thompson', 40, 500, 'https://www.reading.ac.uk/archaeology', '/images/schools/reading-archaeology.jpg', '考古学,文物保护,古代史', '史前考古,文化遗产,考古科学', 'archaeology@reading.ac.uk', '+44 118 378 8753', NOW(), NOW(), 0, 1),
('艺术与传播设计学院', 'School of Arts and Communication Design', 38, 'SACD', '在艺术设计和传播方面提供创新教育', '成立于1970年代，在视觉艺术和设计教育方面享有声誉', 14, 'Prof. David Chen', 60, 1000, 'https://www.reading.ac.uk/arts-design', '/images/schools/reading-arts.jpg', '艺术设计,传播设计,视觉艺术', '数字艺术,设计创新,视觉传达', 'arts@reading.ac.uk', '+44 118 378 8754', NOW(), NOW(), 0, 1),
('计算机科学系', 'Department of Computer Science', 38, 'DCS', '在计算机科学和信息技术方面提供专业教育', '成立于1970年代，在人工智能和软件工程方面领先', 15, 'Prof. Lisa Anderson', 80, 1400, 'https://www.reading.ac.uk/computer-science', '/images/schools/reading-cs.jpg', '计算机科学,软件工程,人工智能', '机器学习,数据科学,网络安全', 'cs@reading.ac.uk', '+44 118 378 8755', NOW(), NOW(), 0, 1),
('雷丁艺术学院', 'Reading School of Art', 38, 'RSA', '在纯艺术和应用艺术方面提供专业教育', '历史悠久，在艺术教育和创作实践方面享有声誉', 16, 'Prof. Helen Roberts', 45, 700, 'https://www.reading.ac.uk/art', '/images/schools/reading-art.jpg', '纯艺术,应用艺术,艺术史', '当代艺术,艺术理论,创作实践', 'art@reading.ac.uk', '+44 118 378 8756', NOW(), NOW(), 0, 1),
('食品和营养科学系', 'Department of Food and Nutritional Sciences', 38, 'DFNS', '在食品科学和营养学方面提供专业教育', '建立于1960年代，在食品技术和营养研究方面领先', 17, 'Prof. Mark Davis', 65, 900, 'https://www.reading.ac.uk/food-nutrition', '/images/schools/reading-food.jpg', '食品科学,营养学,食品技术', '食品安全,营养健康,食品创新', 'food@reading.ac.uk', '+44 118 378 8757', NOW(), NOW(), 0, 1);

-- 提交事务
COMMIT;