package com.study.service.matching.algorithm;

import com.study.service.matching.MatchingDataService;
import com.study.service.matching.MatchingResult;
import com.study.service.matching.dto.MatchingRequest;
import com.study.service.schools.School;
import com.study.service.major.Major;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 莱斯特大学 (University of Leicester) 匹配算法
 * 
 * 规则来源：官方硕士录取要求图表
 * 
 * 1. 院校分类：
 * - 莱大重点院校 (Key List): 包含 985/211/双一流及绝大多数公办本科 (名单扩充至1000+)。
 * - 非重点院校 (Non-Key): 独立学院、民办院校、自考。
 * 
 * 2. 均分要求：
 * - 2:1 专业: 重点 73% / 非重点 75%
 * - 2:2 专业: 重点 65% / 非重点 70%
 * 
 * 3. 学院/专业区分：
 * - 2:2 (低门槛): 商学院、传媒、教育、工程、法学、博物馆学、心理学、科学类。
 * - 2:1 (高门槛): 计算机、政治国关、社会学、英语、数学物理。
 */
@Service
@SuppressWarnings("unused")
public class UKLeicesterMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKLeicesterMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    // 复用之前整理的 Tier 1 和 Tier 2 名单作为 "莱大重点院校" (Key Institutions)
    // 因为莱大名单有1000+，涵盖了几乎所有公办本科，所以我们将 Tier 3 (独立/民办) 视为非重点，其余视为重点。
    // 莱斯特大学 Key Institutions List (包含您提供的所有院校)
    // =================================================================================
    private static final Set<String> KEY_INSTITUTIONS = new HashSet<>();
    static {
        // --- Top Tier / 985 / 211 ---
        addKey("Tsinghua University", "清华大学");
        addKey("Peking University", "北京大学");
        addKey("Zhejiang University", "浙江大学");
        addKey("Shanghai Jiao Tong University", "上海交通大学");
        addKey("Fudan University", "复旦大学");
        addKey("Nanjing University", "南京大学");
        addKey("University of Science and Technology of China", "中国科学技术大学");
        addKey("Huazhong University of Science and Technology", "华中科技大学");
        addKey("Wuhan University", "武汉大学");
        addKey("Xi'an Jiaotong University", "西安交通大学");
        addKey("Sun Yat-sen University", "中山大学");
        addKey("Sichuan University", "四川大学");
        addKey("Harbin Institute of Technology", "哈尔滨工业大学");
        addKey("Beihang University", "北京航空航天大学");
        addKey("Southeast University", "东南大学");
        addKey("Beijing Institute of Technology", "北京理工大学");
        addKey("Tongji University", "同济大学");
        addKey("Renmin University of China", "中国人民大学");
        addKey("Beijing Normal University", "北京师范大学");
        addKey("Nankai University", "南开大学");
        addKey("Tianjin University", "天津大学");
        addKey("Peking Union Medical College", "北京协和医学院");
        addKey("Shandong University", "山东大学");
        addKey("Central South University", "中南大学");
        addKey("Xiamen University", "厦门大学");
        addKey("Northwestern Polytechnical University", "西北工业大学");
        addKey("South China University of Technology", "华南理工大学");
        addKey("Jilin University", "吉林大学");
        addKey("University of Electronic Science and Technology of China", "电子科技大学");
        addKey("Hunan University", "湖南大学");
        addKey("China Agricultural University", "中国农业大学");
        addKey("East China Normal University", "华东师范大学");
        addKey("Dalian University of Technology", "大连理工大学");
        addKey("Southern University of Science and Technology", "南方科技大学");
        addKey("Chongqing University", "重庆大学");
        addKey("Shanghai University of Finance and Economics", "上海财经大学");
        addKey("University of Science and Technology Beijing", "北京科技大学");
        addKey("Nanjing University of Science and Technology", "南京理工大学");
        addKey("Nanjing University of Aeronautics and Astronautics", "南京航空航天大学");
        addKey("Northeastern University", "东北大学");
        addKey("Xidian University", "西安电子科技大学");
        addKey("Lanzhou University", "兰州大学");
        addKey("Soochow University", "苏州大学");
        addKey("Huazhong Agricultural University", "华中农业大学");
        addKey("Beijing Jiaotong University", "北京交通大学");
        addKey("East China University of Science and Technology", "华东理工大学");
        addKey("ShanghaiTech University", "上海科技大学");
        addKey("Capital Medical University", "首都医科大学");
        addKey("Zhengzhou University", "郑州大学");
        addKey("Central University of Finance and Economics", "中央财经大学");
        addKey("Central China Normal University", "华中师范大学");
        addKey("Shanghai University", "上海大学");
        addKey("Nanjing Medical University", "南京医科大学");
        addKey("Harbin Engineering University", "哈尔滨工程大学");
        addKey("Southern Medical University", "南方医科大学");
        addKey("Jinan University", "暨南大学");
        addKey("China University of Political Science and Law", "中国政法大学");
        addKey("Nanjing Agricultural University", "南京农业大学");
        addKey("University of International Business and Economics", "对外经济贸易大学");
        addKey("China University of Petroleum (Beijing)", "中国石油大学（北京）");
        addKey("Northeast Normal University", "东北师范大学");
        addKey("Wuhan University of Technology", "武汉理工大学");
        addKey("Southwest Jiaotong University", "西南交通大学");
        addKey("China University of Mining and Technology", "中国矿业大学");
        addKey("Beijing University of Posts and Telecommunications", "北京邮电大学");
        addKey("The Chinese University of Hong Kong (Shenzhen)", "香港中文大学（深圳）");
        addKey("Nanjing Normal University", "南京师范大学");
        addKey("China University of Geosciences (Wuhan)", "中国地质大学（武汉）");
        addKey("Jiangnan University", "江南大学");
        addKey("Hohai University", "河海大学");
        addKey("Ocean University of China", "中国海洋大学");
        addKey("Beijing University of Technology", "北京工业大学");
        addKey("Harbin Medical University", "哈尔滨医科大学");
        addKey("Beijing University of Chemical Technology", "北京化工大学");
        addKey("Northwest University", "西北大学");
        addKey("Southwestern University of Finance and Economics", "西南财经大学");
        addKey("China University of Petroleum (East China)", "中国石油大学（华东）");
        addKey("Shaanxi Normal University", "陕西师范大学");
        addKey("Tianjin Medical University", "天津医科大学");
        addKey("Zhongnan University of Economics and Law", "中南财经政法大学");
        addKey("Shenzhen University", "深圳大学");
        addKey("Nanchang University", "南昌大学");
        addKey("Zhejiang University of Technology", "浙江工业大学");
        addKey("China Medical University", "中国医科大学");
        addKey("China University of Geosciences (Beijing)", "中国地质大学（北京）");
        addKey("Southwest University", "西南大学");
        addKey("Fuzhou University", "福州大学");
        addKey("Northwest A&F University", "西北农林科技大学");
        addKey("North China Electric Power University", "华北电力大学");
        addKey("China University of Mining and Technology (Beijing)", "中国矿业大学（北京）");
        addKey("Donghua University", "东华大学");
        addKey("Jiangsu University", "江苏大学");
        addKey("South China Normal University", "华南师范大学");
        addKey("Yunnan University", "云南大学");
        addKey("Beijing Foreign Studies University", "北京外国语大学");
        addKey("Yangzhou University", "扬州大学");
        addKey("Beijing Forestry University", "北京林业大学");
        addKey("Communication University of China", "中国传媒大学");
        addKey("Hefei University of Technology", "合肥工业大学");
        addKey("Beijing University of Chinese Medicine", "北京中医药大学");

        // --- Tier 1/2 Boundary & Strong Double Non (101-200) ---
        addKey("Ningbo University", "宁波大学");
        addKey("Nanjing Tech University", "南京工业大学");
        addKey("Nanjing University of Posts and Telecommunications", "南京邮电大学");
        addKey("Hunan Normal University", "湖南师范大学");
        addKey("Chang'an University", "长安大学");
        addKey("Capital Normal University", "首都师范大学");
        addKey("Nanjing University of Information Science and Technology", "南京信息工程大学");
        addKey("Hangzhou Dianzi University", "杭州电子科技大学");
        addKey("Xiangtan University", "湘潭大学");
        addKey("South China Agricultural University", "华南农业大学");
        addKey("Wenzhou Medical University", "温州医科大学");
        addKey("Yanshan University", "燕山大学");
        addKey("Fujian Normal University", "福建师范大学");
        addKey("Guangxi University", "广西大学");
        addKey("China Pharmaceutical University", "中国药科大学");
        addKey("Guangzhou University", "广州大学");
        addKey("Shanghai International Studies University", "上海外国语大学");
        addKey("Shanghai University of Traditional Chinese Medicine", "上海中医药大学");
        addKey("Henan University", "河南大学");
        addKey("Dalian Maritime University", "大连海事大学");
        addKey("Zhejiang Normal University", "浙江师范大学");
        addKey("Chongqing Medical University", "重庆医科大学");
        addKey("Guangdong University of Technology", "广东工业大学");
        addKey("Guangzhou Medical University", "广州医科大学");
        addKey("University of Shanghai for Science and Technology", "上海理工大学");
        addKey("Dongbei University of Finance and Economics", "东北财经大学");
        addKey("Shanghai New York University", "上海纽约大学");
        addKey("Shanxi University", "山西大学");
        addKey("China Foreign Affairs University", "外交学院");
        addKey("Anhui University", "安徽大学");
        addKey("Minzu University of China", "中央民族大学");
        addKey("Shandong Normal University", "山东师范大学");
        addKey("Hebei University of Technology", "河北工业大学");
        addKey("Qingdao University", "青岛大学");
        addKey("Taiyuan University of Technology", "太原理工大学");
        addKey("Guizhou University", "贵州大学");
        addKey("Nanjing Forestry University", "南京林业大学");
        addKey("Shanghai Normal University", "上海师范大学");
        addKey("Wuhan University of Science and Technology", "武汉科技大学");
        addKey("Inner Mongolia University", "内蒙古大学");
        addKey("Zhejiang Sci-Tech University", "浙江理工大学");
        addKey("Northeast Forestry University", "东北林业大学");
        addKey("Fujian Agriculture and Forestry University", "福建农林大学");
        addKey("Jiangxi University of Finance and Economics", "江西财经大学");
        addKey("University of Nottingham Ningbo China", "宁波诺丁汉大学");
        addKey("Xi'an University of Technology", "西安理工大学");
        addKey("Hainan University", "海南大学");
        addKey("Jiangxi Normal University", "江西师范大学");
        addKey("Northeast Agricultural University", "东北农业大学");
        addKey("Hebei Medical University", "河北医科大学");
        addKey("Changsha University of Science and Technology", "长沙理工大学");
        addKey("Xi'an University of Architecture and Technology", "西安建筑科技大学");
        addKey("Hubei University", "湖北大学");
        addKey("Nanjing University of Chinese Medicine", "南京中医药大学");
        addKey("Kunming University of Science and Technology", "昆明理工大学");
        addKey("Jiangsu Normal University", "江苏师范大学");
        addKey("Dalian Medical University", "大连医科大学");
        addKey("Capital University of Economics and Business", "首都经济贸易大学");
        addKey("Shandong University of Science and Technology", "山东科技大学");
        addKey("Liaoning University", "辽宁大学");
        addKey("Southwest Petroleum University", "西南石油大学");
        addKey("Hangzhou Normal University", "杭州师范大学");
        addKey("Hebei University", "河北大学");
        addKey("Shaanxi University of Science & Technology", "陕西科技大学");
        addKey("Anhui Normal University", "安徽师范大学");
        addKey("Beijing University of Civil Engineering and Architecture", "北京建筑大学");
        addKey("Huaqiao University", "华侨大学");
        addKey("Shantou University", "汕头大学");
        addKey("East China University of Political Science and Law", "华东政法大学");
        addKey("Xinjiang University", "新疆大学");
        addKey("Jiangsu University of Science and Technology", "江苏科技大学");
        addKey("Shihezi University", "石河子大学");
        addKey("China Jiliang University", "中国计量大学");
        addKey("Beijing Language and Culture University", "北京语言大学");
        addKey("Henan Agricultural University", "河南农业大学");
        addKey("Anhui Medical University", "安徽医科大学");
        addKey("Chongqing University of Posts and Telecommunications", "重庆邮电大学");
        addKey("Hubei University of Technology", "湖北工业大学");
        addKey("Shenyang Agricultural University", "沈阳农业大学");
        addKey("Sichuan Agricultural University", "四川农业大学");
        addKey("Fujian Medical University", "福建医科大学");
        addKey("Zhejiang Gongshang University", "浙江工商大学");
        addKey("Wuhan Institute of Technology", "武汉工程大学");
        addKey("Shandong Agricultural University", "山东农业大学");
        addKey("Changchun University of Science and Technology", "长春理工大学");
        addKey("Tianjin Normal University", "天津师范大学");
        addKey("Southwest University of Political Science & Law", "西南政法大学");
        addKey("Ningxia University", "宁夏大学");
        addKey("Heilongjiang University", "黑龙江大学");
        addKey("Hunan Agricultural University", "湖南农业大学");
        addKey("Changzhou University", "常州大学");
        addKey("Guangzhou University of Chinese Medicine", "广州中医药大学");
        addKey("Shanghai University of Sport", "上海体育大学");
        addKey("Zhejiang A&F University", "浙江农林大学");
        addKey("Hebei Normal University", "河北师范大学");
        addKey("Henan Normal University", "河南师范大学");
        addKey("Chengdu University of Technology", "成都理工大学");
        addKey("Tiangong University", "天津工业大学");
        addKey("Wenzhou University", "温州大学");
        addKey("Qilu University of Technology", "齐鲁工业大学");

        // --- 201 - 300 (Provincial Key / Public) ---
        addKey("Guangdong University of Foreign Studies", "广东外语外贸大学");
        addKey("Guangxi Normal University", "广西师范大学");
        addKey("Beijing Technology and Business University", "北京工商大学");
        addKey("Nantong University", "南通大学");
        addKey("Guangxi Medical University", "广西医科大学");
        addKey("Anhui Agricultural University", "安徽农业大学");
        addKey("University of Jinan", "济南大学");
        addKey("Jilin Agricultural University", "吉林农业大学");
        addKey("Zhejiang Chinese Medical University", "浙江中医药大学");
        addKey("Wuhan Textile University", "武汉纺织大学");
        addKey("Nanchang Hangkong University", "南昌航空大学");
        addKey("Henan University of Science and Technology", "河南科技大学");
        addKey("Shanghai Ocean University", "上海海洋大学");
        addKey("Tianjin University of Traditional Chinese Medicine", "天津中医药大学");
        addKey("Xi'an Jiaotong-Liverpool University", "西交利物浦大学"); // Joint Venture (in Key list)
        addKey("Jiangxi University of Science and Technology", "江西理工大学");
        addKey("Qufu Normal University", "曲阜师范大学");
        addKey("Shenzhen MSU-BIT University", "深圳北理莫斯科大学");
        addKey("Xi'an University of Science and Technology", "西安科技大学");
        addKey("Jimei University", "集美大学");
        addKey("Nanjing University of Finance and Economics", "南京财经大学");
        addKey("China Three Gorges University", "三峡大学");
        addKey("North China University of Technology", "北方工业大学");
        addKey("Anhui University of Technology", "安徽工业大学");
        addKey("Yanbian University", "延边大学");
        addKey("Zhejiang University of Finance & Economics", "浙江财经大学");
        addKey("Northwest Normal University", "西北师范大学");
        addKey("East China Jiaotong University", "华东交通大学");
        addKey("Henan Polytechnic University", "河南理工大学");
        addKey("Shanxi Medical University", "山西医科大学");
        addKey("Shanghai Maritime University", "上海海事大学");
        addKey("Liaoning Normal University", "辽宁师范大学");
        addKey("Shanghai University of International Business and Economics", "上海对外经贸大学");
        addKey("Qingdao University of Science and Technology", "青岛科技大学");
        addKey("Hebei Agricultural University", "河北农业大学");
        addKey("North University of China", "中北大学");
        addKey("Tianjin University of Science and Technology", "天津科技大学");
        addKey("Yangtze University", "长江大学");
        addKey("Hunan University of Science and Technology", "湖南科技大学");
        addKey("Yunnan Normal University", "云南师范大学");
        addKey("East China University of Technology", "东华理工大学");
        addKey("Nanjing Audit University", "南京审计大学");
        addKey("Ningxia Medical University", "宁夏医科大学");
        addKey("University of South China", "南华大学");
        addKey("Chengdu University of Traditional Chinese Medicine", "成都中医药大学");
        addKey("Xi'an University of Posts & Telecommunications", "西安邮电大学");
        addKey("Zhejiang Ocean University", "浙江海洋大学");
        addKey("Beijing Normal University-Hong Kong Baptist University United International College",
                "北京师范大学-香港浸会大学联合国际学院");
        addKey("Chongqing Jiaotong University", "重庆交通大学");
        addKey("Shenyang University of Technology", "沈阳工业大学");
        addKey("South-Central Minzu University", "中南民族大学");
        addKey("Sichuan Normal University", "四川师范大学");
        addKey("Inner Mongolia Agricultural University", "内蒙古农业大学");
        addKey("Tianjin University of Technology", "天津理工大学");
        addKey("Lanzhou University of Technology", "兰州理工大学");
        addKey("Shanghai University of Electric Power", "上海电力大学");
        addKey("Anhui University of Science and Technology", "安徽理工大学");
        addKey("University of International Relations", "国际关系学院");
        addKey("Shenyang Aerospace University", "沈阳航空航天大学");
        addKey("Central South University of Forestry and Technology", "中南林业科技大学");
        addKey("Qinghai University", "青海大学");
        addKey("Southwest University of Science and Technology", "西南科技大学");
        addKey("Beijing Information Science and Technology University", "北京信息科技大学");
        addKey("Shandong University of Technology", "山东理工大学");
        addKey("People's Public Security University of China", "中国人民公安大学");
        addKey("Henan University of Technology", "河南工业大学");
        addKey("Beijing Electronic Science and Technology Institute", "北京电子科技学院");
        addKey("Beijing International Studies University", "北京第二外国语学院");
        addKey("Suzhou University of Science and Technology", "苏州科技大学");
        addKey("Northeast Petroleum University", "东北石油大学");
        addKey("Shandong University of Finance and Economics", "山东财经大学");
        addKey("Tianjin University of Finance and Economics", "天津财经大学");
        addKey("Shanghai University of Engineering Science", "上海工程技术大学");
        addKey("Harbin Normal University", "哈尔滨师范大学");
        addKey("Qingdao University of Technology", "青岛理工大学");
        addKey("Yantai University", "烟台大学");
        addKey("Foshan University", "佛山大学");
        addKey("North China University of Water Resources and Electric Power", "华北水利水电大学");
        addKey("Beijing Sport University", "北京体育大学");
        addKey("Qingdao Agricultural University", "青岛农业大学");
        addKey("Shanxi Agricultural University", "山西农业大学");
        addKey("Anhui University of Finance and Economics", "安徽财经大学");
        addKey("Shijiazhuang Tiedao University", "石家庄铁道大学");
        addKey("Harbin University of Science and Technology", "哈尔滨理工大学");
        addKey("Gansu Agricultural University", "甘肃农业大学");
        addKey("Jiaxing University", "嘉兴大学");
        addKey("Hainan Normal University", "海南师范大学");
        addKey("Hunan University of Chinese Medicine", "湖南中医药大学");
        addKey("Huzhou University", "湖州师范学院");
        addKey("Wuhan Polytechnic University", "武汉轻工大学");
        addKey("Chongqing University of Technology", "重庆理工大学");
        addKey("Xinjiang Medical University", "新疆医科大学");
        addKey("Lanzhou Jiaotong University", "兰州交通大学");
        addKey("Ludong University", "鲁东大学");
        addKey("Southwest Medical University", "西南医科大学");
        addKey("Guilin University of Electronic Technology", "桂林电子科技大学");
        addKey("Xuzhou Medical University", "徐州医科大学");
        addKey("China West Normal University", "西华师范大学");
        addKey("Northwest University of Political Science and Law", "西北政法大学");

        // --- 301 - 472 (More Public Universities) ---
        addKey("Beijing University Of Agriculture", "北京农学院");
        addKey("Tibet University", "西藏大学");
        addKey("Dalian Polytechnic University", "大连工业大学");
        addKey("Chongqing Normal University", "重庆师范大学");
        addKey("Anhui Jianzhu University", "安徽建筑大学");
        addKey("Chengdu University", "成都大学");
        addKey("Liaocheng University", "聊城大学");
        addKey("Shandong University of Traditional Chinese Medicine", "山东中医药大学");
        addKey("Chengdu University of Information Technology", "成都信息工程大学");
        addKey("Hebei University of Science and Technology", "河北科技大学");
        addKey("Shandong Jianzhu University", "山东建筑大学");
        addKey("Henan University of Chinese Medicine", "河南中医药大学");
        addKey("Anhui Polytechnic University", "安徽工程大学");
        addKey("Dongguan University of Technology", "东莞理工学院");
        addKey("Xi'an Shiyou University", "西安石油大学");
        addKey("Nanjing Institute of Technology", "南京工程学院");
        addKey("Shenyang Jianzhu University", "沈阳建筑大学");
        addKey("Gannan Normal University", "赣南师范大学");
        addKey("Xi'an Technological University", "西安工业大学");
        addKey("Zhengzhou University of Light Industry", "郑州轻工业大学");
        addKey("Shanxi Normal University", "山西师范大学");
        addKey("Shenyang Pharmaceutical University", "沈阳药科大学");
        addKey("Beijing Institute of Graphic Communication", "北京印刷学院");
        addKey("Inner Mongolia Normal University", "内蒙古师范大学");
        addKey("Shandong First Medical University", "山东第一医科大学");
        addKey("Qinghai Normal University", "青海师范大学");
        addKey("Shenyang Normal University", "沈阳师范大学");
        addKey("Shenyang University of Chemical Technology", "沈阳化工大学");
        addKey("Xinxiang Medical University", "新乡医学院");
        addKey("Dalian University", "大连大学");
        addKey("NingboTech University", "浙大宁波理工学院");
        addKey("Shanghai Institute of Technology", "上海应用技术大学");
        addKey("Hefei University", "合肥大学");
        addKey("Xiamen University of Technology", "厦门理工大学");
        addKey("Liaoning Petrochemical University", "辽宁石油化工大学");
        addKey("Jiangxi University of Chinese Medicine", "江西中医药大学");
        addKey("Guilin University of Technology", "桂林理工大学");
        addKey("Heilongjiang University of Chinese Medicine", "黑龙江中医药大学");
        addKey("Hunan University of Technology", "湖南工业大学");
        addKey("Northeast Electric Power University", "东北电力大学");
        addKey("Guizhou Normal University", "贵州师范大学");
        addKey("Liaoning University of Technology", "辽宁工业大学");
        addKey("Civil Aviation University of China", "中国民航大学");
        addKey("Xinyang Normal University", "信阳师范大学");
        addKey("Jilin Normal University", "吉林师范大学");
        addKey("Jianghan University", "江汉大学");
        addKey("Zhongyuan University of Technology", "中原工学院");
        addKey("Linyi University", "临沂大学");
        addKey("Shaoxing University", "绍兴文理学院");
        addKey("Shanghai University of Political Science and Law", "上海政法学院");
        addKey("Yan'an University", "延安大学");
        addKey("Beijing Union University", "北京联合大学");
        addKey("Yancheng Institute of Technology", "盐城工学院");
        addKey("Guangdong University of Finance & Economics", "广东财经大学");
        addKey("Beijing Institute of Petrochemical Technology", "北京石油化工学院");
        addKey("Southwest Minzu University", "西南民族大学");
        addKey("Liaoning Technical University", "辽宁工程技术大学");
        addKey("Hangzhou City University", "浙大城市学院");
        addKey("North China University of Science and Technology", "华北理工大学");
        addKey("Henan University of Economics and Law", "河南财经政法大学");
        addKey("Xi'an Polytechnic University", "西安工程大学");
        addKey("Liaoning University of Traditional Chinese Medicine", "辽宁中医药大学");
        addKey("Changchun University of Technology", "长春工业大学");
        addKey("Jinggangshan University", "井冈山大学");
        addKey("Xihua University", "西华大学");
        addKey("Shanghai Customs College", "上海海关学院");
        addKey("Kunming Medical University", "昆明医科大学");
        addKey("Hebei Engineering University", "河北工程大学");
        addKey("Heilongjiang Bayi Agricultural University", "黑龙江八一农垦大学");
        addKey("Shanxi University of Finance and Economics", "山西财经大学");
        addKey("Zunyi Medical University", "遵义医科大学");
        addKey("Bohai University", "渤海大学");
        addKey("Dalian Jiaotong University", "大连交通大学");
        addKey("Anqing Normal University", "安庆师范大学");
        addKey("Jingdezhen Ceramic Institute", "景德镇陶瓷大学");
        addKey("Inner Mongolia Medical University", "内蒙古医科大学");
        addKey("Huaiyin Institute of Technology", "淮阴工学院");
        addKey("Jishou University", "吉首大学");
        addKey("Hunan University of Science and Technology", "湖南理工学院");
        addKey("Zhejiang University of Science and Technology", "浙江科技学院");
        addKey("Beijing Wuzi University", "北京物资学院");
        addKey("Taiyuan University of Science and Technology", "太原科技大学");
        addKey("Chongqing University of Science and Technology", "重庆科技学院");
        addKey("Shenyang Ligong University", "沈阳理工大学");
        addKey("Nanning Normal University", "南宁师范大学");
        addKey("Hubei University of Chinese Medicine", "湖北中医药大学");
        addKey("Huaibei Normal University", "淮北师范大学");
        addKey("Minnan Normal University", "闽南师范大学");
        addKey("Chongqing Technology and Business University", "重庆工商大学");
        addKey("Hubei University of Medicine", "湖北医药学院");
        addKey("Ningbo University of Technology", "宁波工程学院");
        addKey("Inner Mongolia University of Technology", "内蒙古工业大学");
        addKey("Xinjiang Normal University", "新疆师范大学");
        addKey("Changchun University of Chinese Medicine", "长春中医药大学");
        addKey("Dalian University of Foreign Languages", "大连外国语大学");
        addKey("Xinjiang Agricultural University", "新疆农业大学");
        addKey("Jinling Institute of Technology", "金陵科技学院");
        addKey("Nanchang Institute of Technology", "南昌工程学院");
        addKey("Changshu Institute of Technology", "常熟理工学院");
        addKey("Huaiyin Normal University", "淮阴师范学院");
        addKey("Yangtze Normal University", "长江师范学院");
        addKey("Hunan University of Technology and Business", "湖南工商大学");
        addKey("Weifang Medical University", "潍坊医学院");
        addKey("Beihua University", "北华大学");
        addKey("Guangdong Ocean University", "广东海洋大学");
        addKey("Guangdong Polytechnic Normal University", "广东技术师范大学");
        addKey("Jiangxi Science and Technology Normal University", "江西科技师范大学");
        addKey("Jinzhou Medical University", "锦州医科大学");
        addKey("Taizhou University", "台州学院");
        addKey("Anhui University of Chinese Medicine", "安徽中医药大学");
        addKey("Guangdong Medical University", "广东医科大学");
        addKey("Dalian Ocean University", "大连海洋大学");
        addKey("University of Science and Technology Liaoning", "辽宁科技大学");
        addKey("Xi'an International Studies University", "西安外国语大学");
        addKey("Luoyang Normal University", "洛阳师范学院");
        addKey("Hubei Normal University", "湖北师范大学");
        addKey("Shanghai Dianji University", "上海电机学院");
        addKey("Changzhou Institute of Technology", "常州工学院");
        addKey("Yancheng Teachers University", "盐城师范学院");
        addKey("Hubei University of Education", "湖北第二师范学院");
        addKey("Changchun Normal University", "长春师范大学");
        addKey("Bengbu Medical College", "蚌埠医学院");
        addKey("Jiangsu Ocean University", "江苏海洋大学");
        addKey("Yunnan Agricultural University", "云南农业大学");
        addKey("Changsha University", "长沙学院");
        addKey("Dali University", "大理大学");
        addKey("Sichuan University of Science and Engineering", "四川轻化工大学");
        addKey("Guangxi University of Chinese Medicine", "广西中医药大学");
        addKey("Minjiang University", "闽江学院");
        addKey("Jiangsu University of Technology", "江苏理工学院");
        addKey("Jilin Jianzhu University", "吉林建筑大学");
        addKey("Tianjin Foreign Studies University", "天津外国语大学");
        addKey("Fujian University of Technology", "福建理工大学");
        addKey("Shenyang Medical College", "沈阳医学院");
        addKey("Inner Mongolia University of Science and Technology", "内蒙古科技大学");
        addKey("Guizhou Medical University", "贵州医科大学");
        addKey("Guizhou Education University", "贵州师范学院");
        addKey("Tarim University", "塔里木大学");
        addKey("Huanggang Normal University", "黄冈师范学院");
        addKey("Chongqing University of Arts and Sciences", "重庆文理学院");
        addKey("Shenyang University", "沈阳大学");
        addKey("Guilin Medical University", "桂林医学院");
        addKey("Tongren University", "铜仁学院");
        addKey("Henan Institute of Science and Technology", "河南科技学院");
        addKey("Hainan Medical University", "海南医科大学");
        addKey("Wuyi University", "五邑大学");
        addKey("Hunan First Normal University", "湖南第一师范学院");
        addKey("Mianyang Teachers' College", "绵阳师范学院");
        addKey("Shanghai Lixin University of Accounting and Finance", "上海立信会计金融学院");
        addKey("Shanghai Polytechnic University", "上海第二工业大学");
        addKey("Binzhou University", "滨州学院");
        addKey("Hubei University of Arts and Science", "湖北文理学院");
        addKey("Shaanxi University of Chinese Medicine", "陕西中医药大学");
        addKey("Anhui Science And Technology University", "安徽科技学院");
        addKey("Zhejiang Wanli University", "浙江万里学院");
        addKey("Hubei University of Economics", "湖北经济学院");
        addKey("Jilin University of Finance and Economics", "吉林财经大学");
        addKey("Southwest Forestry University", "西南林业大学");
        addKey("Wannan Medical College", "皖南医学院");
        addKey("Tianjin Agricultural University", "天津农学院");
        addKey("Guangxi Minzu University", "广西民族大学");
        addKey("Quzhou University", "衢州学院");
        addKey("Tianjin Chengjian University", "天津城建大学");
        addKey("Sichuan International Studies University", "四川外国语大学");
        addKey("Changchun University", "长春大学");
        addKey("Hefei Normal University", "合肥师范学院");
        addKey("Quanzhou Normal University", "泉州师范学院");
        addKey("Fujian University of Traditional Chinese Medicine", "福建中医药大学");
        addKey("Shandong Jiaotong University", "山东交通大学");
        addKey("Wuxi University", "无锡学院");
        addKey("Hengyang Normal University", "衡阳师范学院");
        addKey("Tianjin University of Technology and Education", "天津职业技术师范大学");
        addKey("Hebei University of Economics and Business", "河北经贸大学");
        addKey("Hubei University of Science and Technology", "湖北科技学院");
        addKey("Panzhihua University", "攀枝花学院");
        addKey("Yichun University", "宜春学院");
        addKey("Communication University of Zhejiang", "浙江传媒学院");
        addKey("Zhongkai University of Agriculture and Engineering", "仲恺农业工程学院");
        addKey("Fuyang Normal University", "阜阳师范大学");
        addKey("Nanjing Xiaozhuang University", "南京晓庄学院");
        addKey("North Sichuan Medical College", "川北医学院");
        addKey("Guangdong Pharmaceutical University", "广东药科大学");
        addKey("Jiangsu Second Normal University", "江苏第二师范学院");
        addKey("Lingnan Normal University", "岭南师范学院");
        addKey("Wenzhou-Kean University", "温州肯恩大学");
        addKey("Tibet Agricultural and Animal Husbandry University", "西藏农牧学院");
        addKey("Huizhou University", "惠州学院");
        addKey("Tianjin University of Commerce", "天津商业大学");
        addKey("Hunan University of Arts and Science", "湖南文理学院");
        addKey("Neijiang Normal University", "内江师范学院");
        addKey("Heilongjiang Institute of Technology", "黑龙江工程学院");
        addKey("Nanyang Institute of Technology", "南阳理工学院");
        addKey("Lishui University", "丽水学院");
        addKey("Qiqihar University", "齐齐哈尔大学");
        addKey("Zhejiang University of Water Resources and Electric Power", "浙江水利水电学院");
        addKey("Binzhou Medical University", "滨州医学院");
        addKey("Northwest Minzu University", "西北民族大学");
        addKey("Anyang Normal University", "安阳师范学院");
        addKey("Huanghuai University", "黄淮学院");
        addKey("Guizhou Minzu University", "贵州民族大学");
        addKey("Nanyang Normal University", "南阳师范学院");
        addKey("Shangrao Normal University", "上饶师范学院");
        addKey("Dezhou University", "德州学院");
        addKey("Yulin Normal University", "玉林师范学院");
        addKey("Shaanxi University of Technology", "陕西理工大学");
        addKey("Xi'an University", "西安文理学院");
        addKey("Xuzhou University of Technology", "徐州工程学院");
        addKey("Zhoukou Normal University", "周口师范学院");
        addKey("China Investigation Police University of China", "中国刑事警察学院");
        addKey("Taishan University", "泰山学院");
        addKey("Xinyang Agriculture and Forestry University", "信阳农林学院");
        addKey("Dalian Minzu University", "大连民族大学");
        addKey("Sanming University", "三明学院");
        addKey("Zaozhuang University", "枣庄学院");
        addKey("Xi'an University of Finance and Economics", "西安财经大学");
        addKey("Chongqing Three Gorges University", "重庆三峡学院");
        addKey("Hubei University of Automotive Technology", "湖北汽车工业学院");
        addKey("Chengdu Normal University", "成都师范学院");
        addKey("Guangxi University of Science and Technology", "广西科技大学");
        addKey("Gannan Medical University", "赣南医科大学");
        addKey("Guangdong University of Petrochemical Technology", "广东石油化工学院");
        addKey("Chuzhou University", "滁州学院");
        addKey("Capital University of Physical Education and Sports", "首都体育学院");
        addKey("Guangdong University of Education", "广东第二师范学院");
        addKey("Harbin University of Commerce", "哈尔滨商业大学");
        addKey("China University of Labor Relations", "中国劳动关系学院");
        addKey("Weifang University", "潍坊学院");
        addKey("Institute of Disaster Prevention", "防灾科技学院");
        addKey("Jining Medical University", "济宁医学院");
        addKey("Hubei Engineering University", "湖北工程学院");
        addKey("Hubei Polytechnic University", "湖北理工学院");
        addKey("Hunan University of Science and Technology", "湖南科技学院");
        addKey("China People's Police University", "中国人民警察大学");
        addKey("Huaihua University", "怀化学院");
        addKey("Hunan City University", "湖南城市学院");
        addKey("Suqian College", "宿迁学院");
        addKey("Guizhou University of Finance and Economics", "贵州财经大学");
        addKey("Xiangnan University", "湘南学院");
        addKey("Chengdu Medical College", "成都医学院");
        addKey("Zhejiang International Studies University", "浙江外国语学院");
        addKey("Guizhou University of Technology", "贵州理工学院");
        addKey("Nanchang Normal University", "南昌师范学院");
        addKey("Xizang Minzu University", "西藏民族大学");
        addKey("North China Institute of Aerospace Engineering", "北华航天工业学院");
        addKey("Hebei GEO University", "河北地质大学");
        addKey("Changchun Institute of Technology", "长春工程学院");
        addKey("Leshan Normal University", "乐山师范学院");
        addKey("Yunnan Minzu University", "云南民族大学");
        addKey("Heilongjiang University of Science and Technology", "黑龙江科技大学");
        addKey("Hunan Institute of Engineering", "湖南工程学院");
        addKey("Jiamusi University", "佳木斯大学");
        addKey("North Minzu University", "北方民族大学");
        addKey("Yunnan University of Finance and Economics", "云南财经大学");
        addKey("Civil Aviation Flight University of China", "中国民用航空飞行学院");
        addKey("Zhengzhou University of Aeronautics", "郑州航空工业管理学院");
        addKey("Longyan University", "龙岩学院");
        addKey("Shangqiu Normal University", "商丘师范学院");
        addKey("Inner Mongolia University of Finance and Economics", "内蒙古财经大学");
        addKey("Putian University", "莆田学院");
        addKey("Xichang College", "西昌学院");
        addKey("Qujing Normal University", "曲靖师范学院");
        addKey("Hangzhou Medical University", "杭州医学院");
        addKey("Chongqing University of Education", "重庆第二师范学院");
        addKey("Zunyi Normal University", "遵义师范学院");
        addKey("Shanxi Datong University", "山西大同大学");
        addKey("Henan University of Urban Construction", "河南城建学院");
        addKey("Chengdu Technological University", "成都工业学院");
        addKey("Qilu Normal University", "齐鲁师范学院");
        addKey("Tonghua Normal University", "通化师范学院");
        addKey("Pingdingshan University", "平顶山学院");
        addKey("Yingkou Institute of Technology", "营口理工学院");
        addKey("Lanzhou Petrochemical University of Vocational Technology", "兰州石化职业技术大学");
        addKey("Jiujiang University", "九江学院");
        addKey("Hainan Tropical Ocean University", "海南热带海洋学院");
        addKey("Yibin University", "宜宾学院");
        addKey("Wuhan Sports University", "武汉体育学院");
        addKey("Hunan University of Humanities, Science and Technology", "湖南人文科技学院");
        addKey("Hexi University", "河西学院");
        addKey("Shaoyang University", "邵阳学院");
        addKey("Tianshui Normal University", "天水师范学院");
        addKey("Hanshan Normal University", "韩山师范学院");
        addKey("Ningxia Normal University", "宁夏师范学院");
        addKey("Jilin Engineering Normal University", "吉林工程技术师范学院");
        addKey("Qinghai Nationalities University", "青海民族大学");
        addKey("China Women's University", "中华女子学院");
        addKey("Bozhou University", "亳州学院");
        addKey("Henan University of Animal Husbandry and Economy", "河南牧业经济学院");
        addKey("Shandong Youth University of Political Science", "山东青年政治学院");
        addKey("Taiyuan Normal University", "太原师范学院");
        addKey("Fujian Polytechnic Normal University", "福建技术师范学院");
        addKey("Hebei University of Chinese Medicine", "河北中医学院");
        addKey("Yulin University", "榆林学院");
        addKey("Baoji University of Arts and Sciences", "宝鸡文理学院");
        addKey("Hebei Normal University of Science and Technology", "河北科技师范学院");
        addKey("West Anhui University", "皖西学院");
        addKey("Inner Mongolia Minzu University", "内蒙古民族大学");
        addKey("Guiyang University", "贵阳学院");
        addKey("Henan University of Engineering", "河南工程学院");
        addKey("Shandong Management University", "山东管理学院");
        addKey("Xinxiang University", "新乡学院");
        addKey("Beibu Gulf University", "北部湾大学");
        addKey("Shandong Technology and Business University", "山东工商学院");
        addKey("Xi'an Aeronautical Institute", "西安航空学院");
        addKey("Weinan Normal University", "渭南师范学院");
        addKey("Liaodong University", "辽东学院");
        addKey("Mudanjiang Normal University", "牡丹江师范学院");
        addKey("Anyang Institute of Technology", "安阳工学院");
        addKey("Tangshan Normal University", "唐山师范学院");
        addKey("Shenyang Sport University", "沈阳体育学院");
        addKey("Tianjin University of Sport", "天津体育学院");
        addKey("Huainan Normal University", "淮南师范学院");
        addKey("Harbin University", "哈尔滨学院");
        addKey("Guiyang Nursing Vocational College", "贵阳康养职业大学");
        addKey("Zhaoqing University", "肇庆学院");
        addKey("Shanghai University of Medicine and Health Sciences", "上海健康医学院");
        addKey("Zhejiang Police College", "浙江警察学院");
        addKey("Taizhou University", "泰州学院");
        addKey("Suzhou University", "宿州学院");
        addKey("Yuxi Normal University", "玉溪师范学院");
        addKey("Shandong Women's University", "山东女子学院");
        addKey("Jiaying University", "嘉应学院");
        addKey("Xianyang Normal University", "咸阳师范学院");
        addKey("Shanghai Business School", "上海商学院");
        addKey("Hubei Minzu University", "湖北民族大学");
        addKey("Shijiazhuang University", "石家庄学院");
        addKey("Sichuan Tourism University", "四川旅游学院");
        addKey("Longdong University", "陇东学院");
        addKey("Xuchang University", "许昌学院");
        addKey("Chengdu Sport University", "成都体育学院");
        addKey("Nanjing Sport Institute", "南京体育学院");
        addKey("Huzhou College", "湖州学院");
        addKey("Anshan Normal University", "鞍山师范学院");
        addKey("Jilin Institute of Chemical Technology", "吉林化工学院");
        addKey("Tianjin Sino-German University of Applied Sciences", "天津中德应用技术大学");
        addKey("Changzhi University", "长治学院");
        addKey("Liaoning Institute of Science and Technology", "辽宁科技学院");
        addKey("Shandong Agricultural and Engineering University", "山东农业工程学院");
        addKey("Guangdong University of Finance", "广东金融学院");
        addKey("Luoyang Institute of Science and Technology", "洛阳理工学院");
        addKey("Pingxiang University", "萍乡学院");
        addKey("Kaili University", "凯里学院");
        addKey("Shaoguan University", "韶关学院");
        addKey("Wenzhou University of Technology", "温州理工学院");
        addKey("Shanxi University of Chinese Medicine", "山西中医药大学");
        addKey("Yunnan University of Chinese Medicine", "云南中医药大学");
        addKey("Hunan University of Technology", "湖南工学院");
        addKey("Suzhou City University", "苏州城市学院");
        addKey("Ningde Normal University", "宁德师范学院");
        addKey("Qiannan Normal University for Nationalities", "黔南民族师范学院");
        addKey("Huangshan University", "黄山学院");
        addKey("Hebei Vocational University of Technology and Engineering", "河北科技工程职业技术大学");
        addKey("Sichuan Police College", "四川警察学院");
        addKey("Ili Normal University", "伊犁师范大学");
        addKey("Jining University", "济宁学院");
        addKey("Guangdong Police College", "广东警官学院");
        addKey("Kunming University", "昆明学院");
        addKey("Chengde Medical University", "承德医学院");
        addKey("Bengbu University", "蚌埠学院");
        addKey("Chifeng University", "赤峰学院");
        addKey("Lanzhou City University", "兰州城市学院");
        addKey("Chaohu University", "巢湖学院");
        addKey("Chizhou University", "池州学院");
        addKey("Shangluo University", "商洛学院");
        addKey("Taiyuan Institute of Technology", "太原工业学院");
        addKey("Honghe University", "红河学院");
        addKey("Jingdezhen University", "景德镇学院");
        addKey("Guangzhou Maritime University", "广州航海学院");
        addKey("Kashgar University", "喀什大学");
        addKey("Sichuan University of Arts and Science", "四川文理学院");
        addKey("Qiqihar Medical University", "齐齐哈尔医学院");
        addKey("Xinjiang University of Finance and Economics", "新疆财经大学");
        addKey("Jiangsu Police Institute", "江苏警官学院");
        addKey("Jilin Agricultural Science and Technology University", "吉林农业科技学院");
        addKey("Zhengzhou Normal University", "郑州师范学院");
        addKey("Henan Police College", "河南警察学院");
        addKey("Heze University", "菏泽学院");
        addKey("Anshun University", "安顺学院");
        addKey("Hebei University of Architecture", "河北建筑工程学院");
        addKey("Wuyi University", "武夷学院"); // Fujian
        addKey("Hulunbuir University", "呼伦贝尔学院");
        addKey("Nanjing Normal University of Special Education", "南京特殊教育师范学院");
        addKey("Chongqing Police College", "重庆警察学院");
        addKey("Guangzhou Sport University", "广州体育学院");
        addKey("Xiamen Medical University", "厦门医学院");
        addKey("Xinyu University", "新余学院");
        addKey("Baicheng Normal University", "白城师范学院");
        addKey("Aba Teachers University", "阿坝师范学院");
        addKey("Shaanxi Xueqian Normal University", "陕西学前师范学院");
        addKey("Daqing Normal University", "大庆师范学院");
        addKey("Jingchu University of Technology", "荆楚理工学院");
        addKey("Zhengzhou Institute of Engineering and Technology", "郑州工程技术学院");
        addKey("Chuxiong Normal University", "楚雄师范学院");
        addKey("Liupanshui Normal University", "六盘水师范学院");
        addKey("Wuzhou University", "梧州学院");
        addKey("Hezhou University", "贺州学院");
        addKey("Mudanjiang Medical University", "牡丹江医学院");
        addKey("Guilin University of Aerospace Technology", "桂林航天工业学院");
        addKey("Changsha Normal University", "长沙师范学院");
        addKey("Hunan University of Finance and Economics", "湖南财政经济学院");
        addKey("Yuzhang Normal University", "豫章师范学院");
        addKey("Ankang University", "安康学院");
        addKey("Henan Institute of Technology", "河南工学院");
        addKey("Tangshan University", "唐山学院");
        addKey("Hebei University of Environmental Engineering", "河北环境工程学院");
        addKey("Yuncheng University", "运城学院");
        addKey("Baoding University", "保定学院");
        addKey("Baoshan University", "保山学院");
        addKey("North China University of Science and Technology", "华北科技学院");
        addKey("Guangxi University of Finance and Economics", "广西财经学院");
        addKey("Lanzhou University of Arts and Science", "兰州文理学院");
        addKey("Lanzhou University of Finance and Economics", "兰州财经大学");
        addKey("Gannan University of Science and Technology", "赣南科技学院");
        addKey("Lanzhou Institute of Technology", "兰州工业学院");
        addKey("Xingyi Normal University for Nationalities", "兴义民族师范学院");
        addKey("Qujing Medical College", "曲靖医学高等专科学校");
        addKey("Jining Normal University", "集宁师范学院");
        addKey("Hebei Normal University for Nationalities", "河北民族师范学院");
        addKey("Shandong Institute of Petroleum and Chemical Technology", "山东石油化工学院");
        addKey("Cangzhou Normal University", "沧州师范学院");
        addKey("Guizhou University of Traditional Chinese Medicine", "贵州中医药大学");
        addKey("Qiongtai Normal University", "琼台师范学院");
        addKey("Xingtai University", "邢台学院");
        addKey("Sichuan Minzu College", "四川民族学院");
        addKey("Shandong Sport University", "山东体育学院");
        addKey("Handan University", "邯郸学院");
        addKey("Gansu University of Chinese Medicine", "甘肃中医药大学");
        addKey("Liaoning Police College", "辽宁警察学院");
        addKey("Guizhou University of Engineering Science", "贵州工程应用技术学院");
        addKey("Heihe University", "黑河学院");
        addKey("Baise University", "百色学院");
        addKey("Hengshui University", "衡水学院");
        addKey("Fujian Jiangxia University", "福建江夏学院");
        addKey("Guangxi Normal University for Nationalities", "广西民族师范学院");
        addKey("Suihua University", "绥化学院");
        addKey("Pu'er University", "普洱学院");
        addKey("Hanjiang Normal University", "汉江师范学院");
        addKey("Jinzhong University", "晋中学院");
        addKey("Wenshan University", "文山学院");
        addKey("Xi'an Medical University", "西安医学院");
        addKey("Xinjiang University of Engineering", "新疆工程学院");
        addKey("Ordos Institute of Technology", "鄂尔多斯应用技术学院");
        addKey("Fujian Police College", "福建警察学院");
        addKey("Gandong University", "赣东学院");
        addKey("Lvliang University", "吕梁学院");
        addKey("Jining Normal University", "集宁师范学院"); // Duplicate name check? One is 济宁 (Jining), one is 集宁 (Jining).
                                                      // Both added.
        addKey("Taiyuan University", "太原学院");
        addKey("Guangxi Science and Technology Normal University", "广西科技师范学院");
        addKey("Shanxi Institute of Technology", "山西工程技术学院");
        addKey("Zhangjiakou University", "张家口学院");
        addKey("Hechi University", "河池学院");
        addKey("Hetao College", "河套学院");
        addKey("West Yunnan University", "滇西科技师范学院");
        addKey("Wuhan Business University", "武汉商学院");
        addKey("Heilongjiang University of Technology", "黑龙江工业学院");
        addKey("Langfang Normal University", "廊坊师范学院");
        addKey("Hebei University of Water Resources and Electric Engineering", "河北水利电力学院");
        addKey("Hubei University of Police", "湖北警官学院");
        addKey("Gansu Normal College for Nationalities", "甘肃民族师范学院");
    }

    private static void addKey(String en, String cn) {
        KEY_INSTITUTIONS.add(en);
        KEY_INSTITUTIONS.add(cn);
    }

    @Override
    public String getSchoolName() {
        return "莱斯特大学";
    }

    @Override
    public String getRegion() {
        return "英国";
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        List<MatchingResult> results = new ArrayList<>();

        School school = dataService.getSchoolByName(getSchoolName());
        if (school == null) {
            List<School> ukSchools = dataService.getSchoolsByRegion("英国");
            school = ukSchools.stream()
                    .filter(s -> s.getEnglishName() != null
                            && s.getEnglishName().equalsIgnoreCase("University of Leicester"))
                    .findFirst()
                    .orElse(null);
            if (school == null)
                return results;
        }

        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 1. 判断院校背景 (重点 vs 非重点)
        // 逻辑：如果包含"学院"且不包含"大学"(排除外交学院等特例) 或者 在独立学院名单中 -> 非重点
        // 莱斯特的List非常广(1000+)，所以绝大多数公办都在Key List中
        boolean isKeyInstitution = isKeyInstitution(undergradSchool);

        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);
        for (Major major : majors) {
            // 2. 判断专业要求的学位等级 (2:1 还是 2:2)
            String degreeReq = getDegreeRequirement(major);

            // 3. 获取分数线
            double requiredGpa = getRequiredGpa(isKeyInstitution, degreeReq);

            // 4. 计算匹配分
            double matchScore = calculateMatchScore(studentInfo, requiredGpa, degreeReq, isKeyInstitution);

            MatchingResult result = new MatchingResult();
            result.setUserId(studentInfo.getUserId());
            result.setSchoolId(school.getId());
            result.setSchoolName(school.getName());
            result.setMajorName(major.getName());
            result.setMatchScore(matchScore);
            result.setMatchLevel(determineMatchLevel(matchScore));
            result.setAdmissionProbability(calculateAdmissionProbability(matchScore));
            result.setRecommendationIndex(calculateRecommendationIndex(matchScore));
            result.setRegion("英国");
            result.setAlgorithmStrategy("UK_LEICESTER_MATCHING_ALGORITHM");

            String analysis = generateRiskAnalysis(studentInfo, isKeyInstitution, degreeReq, requiredGpa, matchScore,
                    major.getName());
            result.setRiskAnalysis(analysis);
            result.setMatchReason(analysis);

            try {
                result.setStudentInfoSnapshot(objectMapper.writeValueAsString(studentInfo));
            } catch (Exception e) {
                result.setStudentInfoSnapshot("{}");
            }

            results.add(result);
        }

        results.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return results;
    }

    private static List<Major> filterMajorsByTokens(List<Major> majors, List<String> tokens) {
        if (majors == null || majors.isEmpty() || tokens == null || tokens.isEmpty()) {
            return Collections.emptyList();
        }
        List<Major> out = new ArrayList<>();
        for (Major m : majors) {
            if (m == null) {
                continue;
            }
            String cn = m.getName() == null ? "" : m.getName().trim().toLowerCase(Locale.ROOT);
            String en = m.getEnglishName() == null ? "" : m.getEnglishName().trim().toLowerCase(Locale.ROOT);
            for (String t : tokens) {
                if ((!cn.isEmpty() && (cn.equals(t) || cn.contains(t) || t.contains(cn)))
                        || (!en.isEmpty() && (en.equals(t) || en.contains(t) || t.contains(en)))) {
                    out.add(m);
                    break;
                }
            }
        }
        return out;
    }

    private static List<String> extractMajorTokens(MatchingRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        Set<String> tokens = new LinkedHashSet<>();
        if (request.getTargetMajors() != null && !request.getTargetMajors().isEmpty()) {
            for (String t : request.getTargetMajors()) {
                tokens.addAll(splitTokens(t));
            }
        }
        String keyword = request.getStudentInfo() == null ? null : request.getStudentInfo().getTargetMajor();
        tokens.addAll(splitTokens(keyword));
        return new ArrayList<>(tokens);
    }

    private static List<String> splitTokens(String raw) {
        if (raw == null) {
            return Collections.emptyList();
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return Collections.emptyList();
        }
        String[] parts = trimmed.split("[,，;；、\\s]+");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p == null) {
                continue;
            }
            String v = p.trim().toLowerCase(Locale.ROOT);
            if (!v.isEmpty()) {
                out.add(v);
            }
        }
        return out;
    }

    /**
     * 判断是否为莱大重点院校 (Key Institution)
     * 莱大 List 1000+，基本覆盖所有公办。
     * 简单逻辑：排除独立学院和明显民办，其余视为 Key。
     */
    private boolean isKeyInstitution(String schoolName) {
        if (schoolName == null)
            return false;
        // 支持中英文模糊匹配
        for (String key : KEY_INSTITUTIONS) {
            if (schoolName.contains(key) || key.contains(schoolName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取专业学位要求 (2:1 or 2:2)
     * 基于图片分类
     */
    private String getDegreeRequirement(Major major) {
        String name = major.getName().toLowerCase();
        String engName = major.getEnglishName() == null ? "" : major.getEnglishName().toLowerCase();

        // === Group 2: Accepts 2:2 (低分录取) ===
        // 商学院, 传媒, 生化, 化学, 教育, 工程, 地理, 博物馆, 心理学, 法学
        if (containsKeyword(name, engName, "business", "management", "finance", "marketing", "economics", "accounting",
                "商", "管", "金融", "市场", "会计", "经济"))
            return "2:2";
        if (containsKeyword(name, engName, "media", "communication", "journalism", "film", "传媒", "新闻", "传播"))
            return "2:2";
        if (containsKeyword(name, engName, "education", "tesol", "教育"))
            return "2:2";
        if (containsKeyword(name, engName, "engineering", "civil", "mechanical", "electronic", "工程", "机械", "电子"))
            return "2:2";
        if (containsKeyword(name, engName, "law", "legal", "法学", "法律"))
            return "2:2"; // 图片右侧列包含 Law
        if (containsKeyword(name, engName, "museum", "gallery", "heritage", "博物馆"))
            return "2:2";
        if (containsKeyword(name, engName, "psychology", "心理"))
            return "2:2";
        if (containsKeyword(name, engName, "biology", "biological", "biomedical", "chemistry", "geography", "geology",
                "environment", "生物", "化学", "地理", "环境"))
            return "2:2";

        // === Group 1: Requires 2:1 (高分录取) ===
        // 计算机, 考古, 英语, 政治, 社会学, 数学, 物理
        if (containsKeyword(name, engName, "computer", "software", "data science", "ai", "计算机", "软件"))
            return "2:1";
        if (containsKeyword(name, engName, "archaeology", "考古"))
            return "2:1";
        if (containsKeyword(name, engName, "english", "linguistics", "translation", "英语", "语言"))
            return "2:1";
        if (containsKeyword(name, engName, "politics", "international relations", "政治", "国关"))
            return "2:1";
        if (containsKeyword(name, engName, "sociology", "社会学"))
            return "2:1";
        if (containsKeyword(name, engName, "mathematics", "physics", "astronomy", "数学", "物理", "天文"))
            return "2:1";

        // 默认 2:2 (莱斯特对大多数专业比较友好)
        return "2:2";
    }

    private boolean containsKeyword(String name, String engName, String... keywords) {
        for (String kw : keywords) {
            if (name.contains(kw) || engName.contains(kw))
                return true;
        }
        return false;
    }

    /**
     * 获取均分要求
     */
    private double getRequiredGpa(boolean isKey, String degreeReq) {
        if ("2:1".equals(degreeReq)) {
            // 英国 2:1 学位
            return isKey ? 73.0 : 75.0;
        } else {
            // 英国 2:2 学位
            return isKey ? 65.0 : 70.0;
        }
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, String degreeReq,
            boolean isKey) {
        if (student.getGpa() == null)
            return 0.0;
        double gpa = student.getGpa();
        double score = 0.0;

        // 1. 均分基础分
        if (gpa >= required + 2)
            score += 85.0; // 稳
        else if (gpa >= required)
            score += 75.0; // 达标
        else if (gpa >= required - 2)
            score += 55.0; // 冲刺
        else
            score += 20.0;

        // 2. 2:2 学位专业加分 (因为录取门槛低，匹配度高)
        if ("2:2".equals(degreeReq)) {
            score += 10.0;
        }

        // 3. Key List 加分 (虽然List很广，但由算法标记为Key的通常更稳)
        if (isKey)
            score += 5.0;

        return Math.min(100.0, score);
    }

    private String determineMatchLevel(double score) {
        if (score >= 90)
            return "保底";
        if (score >= 75)
            return "稳妥";
        if (score >= 60)
            return "冲刺";
        return "不建议";
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.98;
        if (score >= 75)
            return 0.85;
        if (score >= 60)
            return 0.50;
        return 0.10;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }

    private String generateRiskAnalysis(MatchingRequest.StudentInfoDTO student, boolean isKey, String degreeReq,
            double required, double score, String majorName) {
        StringBuilder sb = new StringBuilder();
        sb.append("【莱斯特大学匹配分析】\n");

        sb.append("1. 院校背景：您的院校被判定为 ").append(isKey ? "莱大重点院校名单 (Top 1000+)" : "非重点院校名单").append("。\n");

        sb.append("2. 专业要求：申请 ").append(majorName).append("，该专业属于 ").append(degreeReq).append(" 学位要求类别。\n");
        if ("2:2".equals(degreeReq)) {
            sb.append("   (注：莱斯特大学的商科、传媒、工程等热门专业接受 2:2 学位，录取机会较大)\n");
        }

        sb.append("3. 均分要求：最低 ").append((int) required).append("%。\n");
        sb.append("4. 您的均分：").append(student.getGpa()).append("%。\n");

        if (student.getGpa() >= required) {
            sb.append("结果：成绩达标，录取希望很大。");
        } else if (student.getGpa() >= required - 2) {
            sb.append("结果：成绩略有差距 (2分以内)，建议申请，可尝试 Argue 或作为冲刺校。");
        } else {
            sb.append("结果：成绩差距较大，建议考虑其他学校或预科。");
        }

        return sb.toString();
    }
}
