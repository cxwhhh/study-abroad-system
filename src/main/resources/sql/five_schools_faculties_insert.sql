-- 五所大学学院数据插入SQL文件
-- 包含ID 43、谢菲尔德大学(ID: 34)、纽卡斯尔大学(ID: 41)、利物浦大学(ID: 32)、埃克塞特大学(ID: 33)的学院数据

-- 开始事务
BEGIN;

-- ID 43 大学学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('医学院', 'School of Medicine', 43, 'SM', '在医学教育和医学研究方面提供世界级教育', '成立于1960年代，在临床医学和医学研究方面享有国际声誉', 1, 'Prof. Sarah Johnson', 150, 2500, 'https://www.university43.ac.uk/medicine', '/images/schools/43-medicine.jpg', '临床医学,基础医学,公共卫生', '医学研究,生物医学,临床试验', 'medicine@university43.ac.uk', '+44 1234 567801', NOW(), NOW(), 0, 1),
('南安普顿商学院', 'Southampton Business School', 43, 'SBS', '提供世界级的商业教育和管理培训', '建立于1960年代，在商业教育和管理研究方面享有声誉', 2, 'Prof. Michael Brown', 120, 2200, 'https://www.university43.ac.uk/business', '/images/schools/43-business.jpg', '工商管理,金融学,市场营销', '商业战略,金融创新,国际商务', 'business@university43.ac.uk', '+44 1234 567802', NOW(), NOW(), 0, 1),
('南安普顿教育学院', 'Southampton School of Education', 43, 'SSE', '在教育学和师范教育方面提供专业培训', '成立于1960年代，在教育研究和教师培训方面领先', 3, 'Prof. David Wilson', 80, 1500, 'https://www.university43.ac.uk/education', '/images/schools/43-education.jpg', '教育学,师范教育,教育心理学', '教育创新,学习科学,教育政策', 'education@university43.ac.uk', '+44 1234 567803', NOW(), NOW(), 0, 1),
('南安普顿法学院', 'Southampton Law School', 43, 'SLS', '在法学教育和法律研究方面提供专业培训', '建立于1960年代，在国际法和商法方面享有声誉', 4, 'Prof. Lisa Anderson', 75, 1300, 'https://www.university43.ac.uk/law', '/images/schools/43-law.jpg', '法学,国际法,商法', '法律研究,人权法,商业法律', 'law@university43.ac.uk', '+44 1234 567804', NOW(), NOW(), 0, 1),
('工程与物理科学学院', 'Faculty of Engineering and Physical Sciences', 43, 'FEPS', '在工程技术和物理科学方面提供专业教育', '成立于1960年代，在工程技术和物理研究方面享有国际声誉', 5, 'Prof. Emma Roberts', 200, 3500, 'https://www.university43.ac.uk/engineering', '/images/schools/43-engineering.jpg', '工程学,物理学,材料科学', '先进制造,新材料,可再生能源', 'engineering@university43.ac.uk', '+44 1234 567805', NOW(), NOW(), 0, 1),
('数学科学学院', 'School of Mathematical Sciences', 43, 'SMS', '在数学和统计学方面提供专业教育', '历史悠久，在应用数学和统计学方面享有声誉', 6, 'Prof. James Miller', 90, 1600, 'https://www.university43.ac.uk/mathematics', '/images/schools/43-maths.jpg', '数学,统计学,应用数学', '数学建模,统计分析,计算数学', 'maths@university43.ac.uk', '+44 1234 567806', NOW(), NOW(), 0, 1),
('温彻斯特艺术学院', 'Winchester School of Art', 43, 'WSA', '在艺术设计和创意实践方面提供专业教育', '成立于1960年代，在艺术教育和设计实践方面享有国际声誉', 7, 'Prof. Catherine White', 100, 1800, 'https://www.university43.ac.uk/art', '/images/schools/43-art.jpg', '艺术设计,创意实践,视觉艺术', '当代艺术,设计创新,数字艺术', 'art@university43.ac.uk', '+44 1234 567807', NOW(), NOW(), 0, 1),
('环境与生命科学学院', 'Faculty of Environmental and Life Sciences', 43, 'FELS', '在环境科学和生命科学方面提供专业教育', '建立于1960年代，在环境研究和生物科学方面领先', 8, 'Prof. Andrew Johnson', 130, 2300, 'https://www.university43.ac.uk/environment', '/images/schools/43-environment.jpg', '环境科学,生物科学,生态学', '气候变化,生物多样性,可持续发展', 'environment@university43.ac.uk', '+44 1234 567808', NOW(), NOW(), 0, 1),
('经济、社会和政治科学学院', 'Faculty of Social, Human and Mathematical Sciences', 43, 'FSHMS', '在社会科学和人文学科方面提供综合性教育', '成立于1960年代，在社会科学和人文研究方面享有声誉', 9, 'Prof. Helen Davis', 110, 2000, 'https://www.university43.ac.uk/social-sciences', '/images/schools/43-social.jpg', '社会学,政治学,经济学', '社会理论,政治研究,经济分析', 'social@university43.ac.uk', '+44 1234 567809', NOW(), NOW(), 0, 1),
('艺术与人文学院', 'Faculty of Arts and Humanities', 43, 'FAH', '在艺术和人文学科方面提供专业教育', '历史悠久，在人文学科和艺术研究方面享有声誉', 10, 'Prof. Robert Chen', 95, 1700, 'https://www.university43.ac.uk/humanities', '/images/schools/43-humanities.jpg', '英语文学,历史学,哲学', '文学研究,历史研究,文化研究', 'humanities@university43.ac.uk', '+44 1234 567810', NOW(), NOW(), 0, 1);

-- 谢菲尔德大学 (ID: 34) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('健康学院', 'Faculty of Health', 34, 'FH', '在健康科学和医学方面提供专业教育', '成立于1960年代，在健康科学和医学研究方面享有国际声誉', 1, 'Prof. Sarah Johnson', 180, 3000, 'https://www.sheffield.ac.uk/health', '/images/schools/sheffield-health.jpg', '医学,护理学,健康科学', '医学研究,公共卫生,临床科学', 'health@sheffield.ac.uk', '+44 114 222 2000', NOW(), NOW(), 0, 1),
('工程学院', 'Faculty of Engineering', 34, 'FE', '在工程技术方面提供世界级教育', '成立于1905年，在工程技术和创新方面享有国际声誉', 2, 'Prof. Michael Brown', 200, 3500, 'https://www.sheffield.ac.uk/engineering', '/images/schools/sheffield-engineering.jpg', '机械工程,电子工程,土木工程', '先进制造,可再生能源,智能系统', 'engineering@sheffield.ac.uk', '+44 114 222 2001', NOW(), NOW(), 0, 1),
('理学院', 'Faculty of Science', 34, 'FS', '在自然科学方面提供专业教育', '历史悠久，在数学、物理和化学方面有重要贡献', 3, 'Prof. David Wilson', 150, 2500, 'https://www.sheffield.ac.uk/science', '/images/schools/sheffield-science.jpg', '数学,物理学,化学', '理论物理,应用数学,材料科学', 'science@sheffield.ac.uk', '+44 114 222 2002', NOW(), NOW(), 0, 1),
('社会科学学院', 'Faculty of Social Sciences', 34, 'FSS', '在社会科学方面提供综合性教育', '成立于1960年代，在社会科学和政策研究方面领先', 4, 'Prof. Lisa Anderson', 120, 2200, 'https://www.sheffield.ac.uk/social-sciences', '/images/schools/sheffield-social.jpg', '政治学,社会学,经济学', '社会政策,政治理论,经济研究', 'social@sheffield.ac.uk', '+44 114 222 2003', NOW(), NOW(), 0, 1),
('管理学院', 'Management School', 34, 'MS', '提供世界级的商业教育和管理培训', '建立于1960年代，在商业教育和管理研究方面享有声誉', 5, 'Prof. Emma Roberts', 100, 2000, 'https://www.sheffield.ac.uk/management', '/images/schools/sheffield-management.jpg', '工商管理,金融学,市场营销', '商业战略,创新管理,国际商务', 'management@sheffield.ac.uk', '+44 114 222 2004', NOW(), NOW(), 0, 1),
('艺术与人文学院', 'Faculty of Arts and Humanities', 34, 'FAH', '在艺术和人文学科方面提供专业教育', '历史悠久，在人文学科和艺术研究方面享有声誉', 6, 'Prof. James Miller', 110, 1800, 'https://www.sheffield.ac.uk/arts-humanities', '/images/schools/sheffield-arts.jpg', '英语文学,历史学,现代语言', '文学研究,历史研究,语言学', 'arts@sheffield.ac.uk', '+44 114 222 2005', NOW(), NOW(), 0, 1);

-- 纽卡斯尔大学(英国) (ID: 41) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('医学教育学院', 'Faculty of Medical Sciences', 41, 'FMS', '在医学教育和医学研究方面提供世界级教育', '成立于1834年，在医学教育和研究方面享有国际声誉', 1, 'Prof. Sarah Johnson', 200, 3500, 'https://www.ncl.ac.uk/medical-sciences', '/images/schools/newcastle-medical.jpg', '医学,牙科学,生物医学', '医学研究,临床科学,生物医学工程', 'medical@ncl.ac.uk', '+44 191 208 6000', NOW(), NOW(), 0, 1),
('医学院', 'School of Medicine', 41, 'SM', '提供临床医学和基础医学教育', '历史悠久，在临床医学教育方面享有声誉', 2, 'Prof. Michael Brown', 150, 2500, 'https://www.ncl.ac.uk/medicine', '/images/schools/newcastle-medicine.jpg', '临床医学,基础医学,医学研究', '临床试验,医学创新,转化医学', 'medicine@ncl.ac.uk', '+44 191 208 6001', NOW(), NOW(), 0, 1),
('历史、古典和考古学院和考古学', 'School of History, Classics and Archaeology', 41, 'SHCA', '在历史学和考古学方面提供专业教育', '成立于1834年，在历史研究和考古学方面享有声誉', 3, 'Prof. David Wilson', 70, 1200, 'https://www.ncl.ac.uk/history', '/images/schools/newcastle-history.jpg', '历史学,古典学,考古学', '古代史,文化遗产,考古研究', 'history@ncl.ac.uk', '+44 191 208 6002', NOW(), NOW(), 0, 1),
('口腔科学学院', 'School of Dental Sciences', 41, 'SDS', '在口腔医学和牙科学方面提供专业教育', '建立于1960年代，在口腔医学和牙科研究方面领先', 4, 'Prof. Lisa Anderson', 80, 800, 'https://www.ncl.ac.uk/dental', '/images/schools/newcastle-dental.jpg', '口腔医学,牙科学,口腔外科', '口腔疾病,牙科材料,口腔健康', 'dental@ncl.ac.uk', '+44 191 208 6003', NOW(), NOW(), 0, 1),
('哲学研究', 'Department of Philosophy', 41, 'DP', '在哲学研究和思辨方面提供专业教育', '历史悠久，在哲学研究和伦理学方面享有声誉', 5, 'Prof. Emma Roberts', 40, 600, 'https://www.ncl.ac.uk/philosophy', '/images/schools/newcastle-philosophy.jpg', '哲学,伦理学,逻辑学', '道德哲学,政治哲学,认识论', 'philosophy@ncl.ac.uk', '+44 191 208 6004', NOW(), NOW(), 0, 1),
('商学院', 'Newcastle University Business School', 41, 'NUBS', '提供世界级的商业教育和管理培训', '建立于1960年代，在商业教育和管理研究方面享有声誉', 6, 'Prof. James Miller', 120, 2500, 'https://www.ncl.ac.uk/business', '/images/schools/newcastle-business.jpg', '工商管理,金融学,市场营销', '商业战略,创新管理,国际商务', 'business@ncl.ac.uk', '+44 191 208 6005', NOW(), NOW(), 0, 1),
('地理政治与社会学学院', 'School of Geography, Politics and Sociology', 41, 'SGPS', '在地理学和社会科学方面提供综合性教育', '成立于1960年代，在地理研究和社会科学方面领先', 7, 'Prof. Catherine White', 90, 1600, 'https://www.ncl.ac.uk/geography', '/images/schools/newcastle-geography.jpg', '地理学,政治学,社会学', '人文地理,政治理论,社会研究', 'geography@ncl.ac.uk', '+44 191 208 6006', NOW(), NOW(), 0, 1),
('工程学院', 'Faculty of Science, Agriculture and Engineering', 41, 'FSAE', '在工程技术和农业科学方面提供专业教育', '成立于1834年，在工程技术和农业研究方面享有国际声誉', 8, 'Prof. Andrew Johnson', 180, 3000, 'https://www.ncl.ac.uk/engineering', '/images/schools/newcastle-engineering.jpg', '工程学,农业科学,环境科学', '可持续工程,农业创新,环境技术', 'engineering@ncl.ac.uk', '+44 191 208 6007', NOW(), NOW(), 0, 1),
('建筑、规划和景观学院', 'School of Architecture, Planning and Landscape', 41, 'SAPL', '在建筑设计和城市规划方面提供专业教育', '成立于1960年代，在建筑设计和规划方面享有声誉', 9, 'Prof. Helen Davis', 85, 1400, 'https://www.ncl.ac.uk/architecture', '/images/schools/newcastle-architecture.jpg', '建筑学,城市规划,景观设计', '可持续建筑,城市设计,景观规划', 'architecture@ncl.ac.uk', '+44 191 208 6008', NOW(), NOW(), 0, 1),
('心理学学院', 'School of Psychology', 41, 'SP', '在心理学研究和应用方面提供专业教育', '建立于1960年代，在认知心理学和临床心理学方面享有声誉', 10, 'Prof. Robert Chen', 70, 1200, 'https://www.ncl.ac.uk/psychology', '/images/schools/newcastle-psychology.jpg', '心理学,认知科学,临床心理学', '神经心理学,发展心理学,健康心理学', 'psychology@ncl.ac.uk', '+44 191 208 6009', NOW(), NOW(), 0, 1),
('教育、交流和语言科学学院', 'School of Education, Communication and Language Sciences', 41, 'SECLS', '在教育学和语言科学方面提供专业教育', '成立于1960年代，在教育研究和语言学方面领先', 11, 'Prof. Susan Wilson', 80, 1500, 'https://www.ncl.ac.uk/education', '/images/schools/newcastle-education.jpg', '教育学,语言学,传播学', '教育创新,语言研究,传播理论', 'education@ncl.ac.uk', '+44 191 208 6010', NOW(), NOW(), 0, 1),
('数学、统计与物理学院', 'School of Mathematics, Statistics and Physics', 41, 'SMSP', '在数学和物理科学方面提供专业教育', '历史悠久，在数学和物理研究方面享有国际声誉', 12, 'Prof. Mark Thompson', 100, 1800, 'https://www.ncl.ac.uk/mathematics', '/images/schools/newcastle-maths.jpg', '数学,统计学,物理学', '理论物理,应用数学,统计分析', 'maths@ncl.ac.uk', '+44 191 208 6011', NOW(), NOW(), 0, 1),
('法律学院', 'Newcastle Law School', 41, 'NLS', '在法学教育和法律研究方面提供专业培训', '建立于1960年代，在国际法和商法方面享有声誉', 13, 'Prof. David Anderson', 75, 1300, 'https://www.ncl.ac.uk/law', '/images/schools/newcastle-law.jpg', '法学,国际法,商法', '法律研究,人权法,商业法律', 'law@ncl.ac.uk', '+44 191 208 6012', NOW(), NOW(), 0, 1),
('现代语言学院', 'School of Modern Languages', 41, 'SML', '在现代语言和文化研究方面提供专业教育', '成立于1960年代，在语言教育和文化研究方面享有声誉', 14, 'Prof. Lisa Brown', 60, 1000, 'https://www.ncl.ac.uk/modern-languages', '/images/schools/newcastle-languages.jpg', '现代语言,文化研究,翻译学', '语言学,跨文化交流,文学研究', 'languages@ncl.ac.uk', '+44 191 208 6013', NOW(), NOW(), 0, 1),
('生物学、营养与运动科学学院', 'School of Biomedical, Nutritional and Sport Sciences', 41, 'SBNSS', '在生物医学和运动科学方面提供专业教育', '建立于1960年代，在生物医学和运动科学方面领先', 15, 'Prof. Emma Miller', 90, 1600, 'https://www.ncl.ac.uk/biomedical', '/images/schools/newcastle-biomedical.jpg', '生物医学,营养学,运动科学', '分子生物学,营养健康,运动生理学', 'biomedical@ncl.ac.uk', '+44 191 208 6014', NOW(), NOW(), 0, 1),
('自然与环境科学学院', 'School of Natural and Environmental Sciences', 41, 'SNES', '在自然科学和环境科学方面提供专业教育', '成立于1960年代，在环境科学和生态学方面享有声誉', 16, 'Prof. James White', 110, 2000, 'https://www.ncl.ac.uk/natural-sciences', '/images/schools/newcastle-natural.jpg', '环境科学,生态学,地球科学', '气候变化,生物多样性,可持续发展', 'natural@ncl.ac.uk', '+44 191 208 6015', NOW(), NOW(), 0, 1),
('艺术与文化学院', 'Faculty of Humanities and Social Sciences', 41, 'FHSS', '在人文学科和社会科学方面提供综合性教育', '历史悠久，在人文学科和社会科学研究方面享有声誉', 17, 'Prof. Catherine Johnson', 130, 2300, 'https://www.ncl.ac.uk/humanities', '/images/schools/newcastle-humanities.jpg', '英语文学,历史学,艺术史', '文学研究,文化史,艺术理论', 'humanities@ncl.ac.uk', '+44 191 208 6016', NOW(), NOW(), 0, 1),
('英国文学、语言和语言学学院', 'School of English Literature, Language and Linguistics', 41, 'SELLL', '在英语文学和语言学方面提供专业教育', '历史悠久，在英语文学和语言研究方面享有声誉', 18, 'Prof. Andrew Davis', 65, 1100, 'https://www.ncl.ac.uk/english', '/images/schools/newcastle-english.jpg', '英语文学,语言学,创意写作', '文学研究,语言分析,写作实践', 'english@ncl.ac.uk', '+44 191 208 6017', NOW(), NOW(), 0, 1),
('药学院', 'School of Pharmacy', 41, 'SP2', '在药学和制药科学方面提供专业教育', '建立于1960年代，在药学研究和制药技术方面领先', 19, 'Prof. Helen Thompson', 70, 900, 'https://www.ncl.ac.uk/pharmacy', '/images/schools/newcastle-pharmacy.jpg', '药学,制药科学,临床药学', '药物研发,制药技术,药物治疗', 'pharmacy@ncl.ac.uk', '+44 191 208 6018', NOW(), NOW(), 0, 1),
('计算机学院', 'School of Computing', 41, 'SC', '在计算机科学和信息技术方面提供专业教育', '成立于1970年代，在计算机科学和人工智能方面领先', 20, 'Prof. Robert Anderson', 95, 1700, 'https://www.ncl.ac.uk/computing', '/images/schools/newcastle-computing.jpg', '计算机科学,人工智能,软件工程', '机器学习,数据科学,网络安全', 'computing@ncl.ac.uk', '+44 191 208 6019', NOW(), NOW(), 0, 1);

-- 利物浦大学 (ID: 32) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('人文与社会科学学院', 'Faculty of Humanities and Social Sciences', 32, 'FHSS', '在人文学科和社会科学方面提供综合性教育', '成立于1881年，在人文学科和社会科学研究方面享有国际声誉', 1, 'Prof. Sarah Johnson', 200, 3500, 'https://www.liverpool.ac.uk/humanities', '/images/schools/liverpool-humanities.jpg', '英语文学,历史学,哲学', '文学研究,历史研究,文化研究', 'humanities@liverpool.ac.uk', '+44 151 794 2000', NOW(), NOW(), 0, 1),
('健康与生命科学学院', 'Faculty of Health and Life Sciences', 32, 'FHLS', '在健康科学和生命科学方面提供专业教育', '建立于1881年，在医学和生命科学研究方面享有国际声誉', 2, 'Prof. Michael Brown', 250, 4000, 'https://www.liverpool.ac.uk/health-life-sciences', '/images/schools/liverpool-health.jpg', '医学,生物科学,心理学', '医学研究,生物技术,健康科学', 'health@liverpool.ac.uk', '+44 151 794 2001', NOW(), NOW(), 0, 1),
('科学与工程学院', 'Faculty of Science and Engineering', 32, 'FSE', '在科学和工程技术方面提供专业教育', '成立于1881年，在工程技术和科学研究方面享有国际声誉', 3, 'Prof. David Wilson', 220, 3800, 'https://www.liverpool.ac.uk/science-engineering', '/images/schools/liverpool-science.jpg', '工程学,物理学,化学', '先进制造,新材料,可再生能源', 'science@liverpool.ac.uk', '+44 151 794 2002', NOW(), NOW(), 0, 1),
('管理学院', 'University of Liverpool Management School', 32, 'ULMS', '提供世界级的商业教育和管理培训', '建立于1960年代，在商业教育和管理研究方面享有声誉', 4, 'Prof. Lisa Anderson', 150, 2800, 'https://www.liverpool.ac.uk/management', '/images/schools/liverpool-management.jpg', '工商管理,金融学,市场营销', '商业战略,创新管理,国际商务', 'management@liverpool.ac.uk', '+44 151 794 2003', NOW(), NOW(), 0, 1),
('计算机学院', 'Department of Computer Science', 32, 'DCS', '在计算机科学和信息技术方面提供专业教育', '成立于1970年代，在计算机科学和人工智能方面领先', 5, 'Prof. Emma Roberts', 100, 1800, 'https://www.liverpool.ac.uk/computer-science', '/images/schools/liverpool-cs.jpg', '计算机科学,人工智能,软件工程', '机器学习,数据科学,网络安全', 'cs@liverpool.ac.uk', '+44 151 794 2004', NOW(), NOW(), 0, 1);

-- 埃克塞特大学 (ID: 33) 学院数据
INSERT INTO school_faculties (
    name, english_name, school_id, code, description, history, ranking,
    dean_name, faculty_count, student_count, website, image_url,
    featured_programs, research_areas, contact_email, contact_phone,
    created_time, updated_time, is_deleted, status
) VALUES 
('商学院', 'University of Exeter Business School', 33, 'UEBS', '提供世界级的商业教育和管理培训', '建立于1955年，在商业教育和管理研究方面享有国际声誉', 1, 'Prof. Sarah Johnson', 150, 3000, 'https://www.exeter.ac.uk/business', '/images/schools/exeter-business.jpg', '工商管理,金融学,会计学', '商业战略,金融创新,可持续商业', 'business@exeter.ac.uk', '+44 1392 661000', NOW(), NOW(), 0, 1),
('工程学院', 'College of Engineering, Mathematics and Physical Sciences', 33, 'CEMPS', '在工程技术和物理科学方面提供专业教育', '成立于1955年，在工程技术和科学研究方面享有声誉', 2, 'Prof. Michael Brown', 180, 2800, 'https://www.exeter.ac.uk/engineering', '/images/schools/exeter-engineering.jpg', '工程学,数学,物理学', '可再生能源,材料科学,数学建模', 'engineering@exeter.ac.uk', '+44 1392 661001', NOW(), NOW(), 0, 1),
('环境与生命科学学院', 'College of Life and Environmental Sciences', 33, 'CLES', '在环境科学和生命科学方面提供专业教育', '建立于1955年，在环境研究和生物科学方面领先', 3, 'Prof. David Wilson', 160, 2500, 'https://www.exeter.ac.uk/life-environmental', '/images/schools/exeter-environment.jpg', '环境科学,生物科学,地理学', '气候变化,生物多样性,可持续发展', 'environment@exeter.ac.uk', '+44 1392 661002', NOW(), NOW(), 0, 1),
('人文学院', 'College of Humanities', 33, 'CH', '在人文学科方面提供专业教育', '历史悠久，在人文学科和文化研究方面享有声誉', 4, 'Prof. Lisa Anderson', 120, 2000, 'https://www.exeter.ac.uk/humanities', '/images/schools/exeter-humanities.jpg', '英语文学,历史学,现代语言', '文学研究,历史研究,语言学', 'humanities@exeter.ac.uk', '+44 1392 661003', NOW(), NOW(), 0, 1),
('社会科学与国际研究学院', 'College of Social Sciences and International Studies', 33, 'CSSIS', '在社会科学和国际研究方面提供专业教育', '成立于1960年代，在社会科学和国际关系方面享有声誉', 5, 'Prof. Emma Roberts', 140, 2300, 'https://www.exeter.ac.uk/social-sciences', '/images/schools/exeter-social.jpg', '政治学,社会学,国际关系', '政治理论,社会研究,国际政治', 'social@exeter.ac.uk', '+44 1392 661004', NOW(), NOW(), 0, 1),
('医学院', 'University of Exeter Medical School', 33, 'UEMS', '在医学教育和医学研究方面提供专业教育', '成立于2000年代，在医学教育和健康科学方面快速发展', 6, 'Prof. James Miller', 100, 1200, 'https://www.exeter.ac.uk/medicine', '/images/schools/exeter-medicine.jpg', '医学,健康科学,生物医学', '医学研究,公共卫生,临床科学', 'medicine@exeter.ac.uk', '+44 1392 661005', NOW(), NOW(), 0, 1),
('教育学院', 'Graduate School of Education', 33, 'GSE', '在教育学和师范教育方面提供专业培训', '成立于1960年代，在教育研究和教师培训方面享有声誉', 7, 'Prof. Catherine White', 80, 1500, 'https://www.exeter.ac.uk/education', '/images/schools/exeter-education.jpg', '教育学,师范教育,教育心理学', '教育创新,学习科学,教育政策', 'education@exeter.ac.uk', '+44 1392 661006', NOW(), NOW(), 0, 1),
('法学院', 'University of Exeter Law School', 33, 'UELS', '在法学教育和法律研究方面提供专业培训', '建立于1960年代，在国际法和商法方面享有声誉', 8, 'Prof. Andrew Johnson', 70, 1100, 'https://www.exeter.ac.uk/law', '/images/schools/exeter-law.jpg', '法学,国际法,商法', '法律研究,人权法,环境法', 'law@exeter.ac.uk', '+44 1392 661007', NOW(), NOW(), 0, 1),
('心理学院', 'Department of Psychology', 33, 'DP', '在心理学研究和应用方面提供专业教育', '建立于1960年代，在认知心理学和临床心理学方面享有声誉', 9, 'Prof. Helen Davis', 60, 900, 'https://www.exeter.ac.uk/psychology', '/images/schools/exeter-psychology.jpg', '心理学,认知科学,临床心理学', '神经心理学,发展心理学,健康心理学', 'psychology@exeter.ac.uk', '+44 1392 661008', NOW(), NOW(), 0, 1),
('体育与健康科学学院', 'Sport and Health Sciences', 33, 'SHS', '在体育科学和健康科学方面提供专业教育', '成立于1990年代，在体育科学和运动医学方面领先', 10, 'Prof. Robert Chen', 50, 700, 'https://www.exeter.ac.uk/sport', '/images/schools/exeter-sport.jpg', '体育科学,运动医学,健康科学', '运动生理学,体育心理学,运动康复', 'sport@exeter.ac.uk', '+44 1392 661009', NOW(), NOW(), 0, 1);

-- 提交事务
COMMIT;