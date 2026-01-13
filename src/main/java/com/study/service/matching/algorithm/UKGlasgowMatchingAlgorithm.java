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
 * 格拉斯哥大学 (University of Glasgow) 2026 匹配算法
 * 数据来源：University of Glasgow Accepted Chinese University List 2026 (Complete
 * A-Z)
 * 
 * 核心逻辑：
 * 1. 均分直接使用百分制 (0-100)。
 * 2. Band A: 70% (2:1) / 65% (2:2)
 * 3. Band B: 75% (2:1) / 70% (2:2)
 * 4. Band C: 80% (2:1) / 75% (2:2)
 * 5. Band D: 85% (2:1) / 80% (2:2)
 * 6. Band E: 87% (2:1) / 85% (2:2)
 * 7. TNE: 75% (2:1) / 68% (2:2)
 */
@Service
@SuppressWarnings("unused")
public class UKGlasgowMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKGlasgowMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    // 大学 Band 映射表
    private static final Map<String, String> UNIVERSITY_BANDS = new HashMap<>();

    // 静态代码块加载所有学校数据
    static {
        // ==========================================
        // Band A (2:1 -> 70% | 2:2 -> 65%)
        // ==========================================
        String[] bandA = {
                "北京航空航天大学", "Beihang University",
                "北京理工大学", "Beijing Institute of Technology",
                "北京师范大学", "Beijing Normal University",
                "中南大学", "Central South University",
                "大连理工大学", "Dalian University of Technology",
                "华东师范大学", "East China Normal University",
                "复旦大学", "Fudan University",
                "哈尔滨工业大学", "Harbin Institute of Technology",
                "华中科技大学", "Huazhong University of Science and Technology",
                "湖南大学", "Hunan University",
                "吉林大学", "Jilin University",
                "兰州大学", "Lanzhou University",
                "中央民族大学", "Minzu University of China",
                "南京大学", "Nanjing University",
                "南开大学", "Nankai University",
                "国防科技大学", "National University of Defense Technology",
                "东北大学", "Northeastern University",
                "西北农林科技大学", "Northwest A&F University",
                "西北工业大学", "Northwestern Polytechnical University",
                "中国海洋大学", "Ocean University of China",
                "北京大学", "Peking University",
                "中国人民大学", "Renmin University of China",
                "山东大学", "Shandong University",
                "上海交通大学", "Shanghai Jiao Tong University",
                "四川大学", "Sichuan University",
                "华南理工大学", "South China University of Technology",
                "东南大学", "Southeast University",
                "中山大学", "Sun Yat-sen University",
                "天津大学", "Tianjin University",
                "同济大学", "Tongji University",
                "清华大学", "Tsinghua University",
                "中国科学院大学", "University of Chinese Academy of Sciences",
                "电子科技大学", "University of Electronic Science and Technology of China",
                "中国科学技术大学", "University of Science and Technology of China",
                "武汉大学", "Wuhan University",
                "西安交通大学", "Xi'an Jiaotong University",
                "厦门大学", "Xiamen University",
                "浙江大学", "Zhejiang University",
                "重庆大学", "Chongqing University",
                "中国农业大学", "China Agricultural University"
        };
        loadBand(bandA, "A");

        // ==========================================
        // Band B (2:1 -> 75% | 2:2 -> 70%)
        // ==========================================
        String[] bandB = {
                "安徽大学", "Anhui University",
                "安徽医科大学", "Anhui Medical University",
                "安徽师范大学", "Anhui Normal University",
                "安徽财经大学", "Anhui University of Finance and Economics",
                "安徽理工大学", "Anhui University of Science and Technology",
                "安徽工业大学", "Anhui University of Technology",
                "安徽中医药大学", "Anhui University of Chinese Medicine",
                "北京中医药大学", "Beijing University of Chinese Medicine",
                "北京工业大学", "Beijing University of Technology",
                "北京物资学院", "Beijing Wuzi University",
                "北京邮电大学", "Beijing University of Posts and Telecommunications",
                "北京交通大学", "Beijing Jiaotong University",
                "北京科技大学", "University of Science and Technology Beijing",
                "北京化工大学", "Beijing University of Chemical Technology",
                "北京林业大学", "Beijing Forestry University",
                "北京语言大学", "Beijing Language and Culture University",
                "北京体育大学", "Beijing Sport University",
                "北京外国语大学", "Beijing Foreign Studies University",
                "北京第二外国语学院", "Beijing International Studies University",
                "首都医科大学", "Capital Medical University",
                "首都师范大学", "Capital Normal University",
                "首都经济贸易大学", "Capital University of Economics and Business",
                "首都体育学院", "Capital University of Physical Education and Sports",
                "中央美术学院", "Central Academy of Fine Arts",
                "华中师范大学", "Central China Normal University",
                "中央音乐学院", "Central Conservatory of Music",
                "中南林业科技大学", "Central South University of Forestry and Technology",
                "中央财经大学", "Central University of Finance and Economics",
                "长安大学", "Chang'an University",
                "长沙理工大学", "Changsha University of Science and Technology",
                "常州大学", "Changzhou University",
                "成都大学", "Chengdu University",
                "成都理工大学", "Chengdu University of Technology",
                "成都中医药大学", "Chengdu University of Traditional Chinese Medicine",
                "中国美术学院", "China Academy of Art",
                "中国音乐学院", "China Conservatory of Music",
                "中国刑事警察学院", "China Criminal Police University",
                "外交学院", "China Foreign Affairs University",
                "中国计量大学", "China Jiliang University",
                "中国医科大学", "China Medical University",
                "中国药科大学", "China Pharmaceutical University",
                "三峡大学", "China Three Gorges University",
                "中国地质大学", "China University of Geosciences",
                "中国矿业大学", "China University of Mining and Technology",
                "中国石油大学", "China University of Petroleum",
                "中国政法大学", "China University of Political Science and Law",
                "重庆医科大学", "Chongqing Medical University",
                "重庆邮电大学", "Chongqing University of Posts and Telecommunications",
                "重庆理工大学", "Chongqing University of Technology",
                "重庆工商大学", "Chongqing Technology and Business University",
                "中国传媒大学", "Communication University of China",
                "大连海事大学", "Dalian Maritime University",
                "大连医科大学", "Dalian Medical University",
                "大连外国语大学", "Dalian University of Foreign Languages",
                "大连工业大学", "Dalian Polytechnic University",
                "东北财经大学", "Dongbei University of Finance and Economics",
                "东莞理工学院", "Dongguan University of Technology",
                "东华大学", "Donghua University",
                "华东交通大学", "East China Jiaotong University",
                "华东政法大学", "East China University of Political Science and Law",
                "华东理工大学", "East China University of Science and Technology",
                "佛山科学技术学院", "Foshan University",
                "福建农林大学", "Fujian Agriculture and Forestry University",
                "福建医科大学", "Fujian Medical University",
                "福建师范大学", "Fujian Normal University",
                "福建中医药大学", "Fujian University of Traditional Chinese Medicine",
                "福州大学", "Fuzhou University",
                "赣南师范大学", "Gannan Normal University",
                "甘肃农业大学", "Gansu Agricultural University",
                "广东财经大学", "Guangdong University of Finance and Economics",
                "广东外语外贸大学", "Guangdong University of Foreign Studies",
                "广东工业大学", "Guangdong University of Technology",
                "广西师范大学", "Guangxi Normal University",
                "广西大学", "Guangxi University",
                "广西中医药大学", "Guangxi University of Chinese Medicine",
                "广州大学", "Guangzhou University",
                "广州中医药大学", "Guangzhou University of Chinese Medicine",
                "广州医科大学", "Guangzhou Medical University",
                "贵州大学", "Guizhou University",
                "贵州医科大学", "Guizhou Medical University",
                "海南大学", "Hainan University",
                "海南师范大学", "Hainan Normal University",
                "杭州电子科技大学", "Hangzhou Dianzi University",
                "杭州师范大学", "Hangzhou Normal University",
                "哈尔滨工程大学", "Harbin Engineering University",
                "哈尔滨医科大学", "Harbin Medical University",
                "哈尔滨师范大学", "Harbin Normal University",
                "哈尔滨理工大学", "Harbin University of Science and Technology",
                "河北农业大学", "Hebei Agricultural University",
                "河北医科大学", "Hebei Medical University",
                "河北师范大学", "Hebei Normal University",
                "河北大学", "Hebei University",
                "河北科技大学", "Hebei University of Science and Technology",
                "河北工业大学", "Hebei University of Technology",
                "合肥工业大学", "Hefei University of Technology",
                "黑龙江大学", "Heilongjiang University",
                "黑龙江中医药大学", "Heilongjiang University of Chinese Medicine",
                "河南农业大学", "Henan Agricultural University",
                "河南大学", "Henan University",
                "河南理工大学", "Henan Polytechnic University",
                "河南师范大学", "Henan Normal University",
                "湖北大学", "Hubei University",
                "湖北工业大学", "Hubei University of Technology",
                "湖北美术学院", "Hubei Institute of Fine Arts",
                "湖南农业大学", "Hunan Agricultural University",
                "湖南师范大学", "Hunan Normal University",
                "华侨大学", "Huaqiao University",
                "华中农业大学", "Huazhong Agricultural University",
                "内蒙古大学", "Inner Mongolia University",
                "内蒙古师范大学", "Inner Mongolia Normal University",
                "江南大学", "Jiangnan University",
                "江汉大学", "Jianghan University",
                "江苏大学", "Jiangsu University",
                "江苏师范大学", "Jiangsu Normal University",
                "江西农业大学", "Jiangxi Agricultural University",
                "江西师范大学", "Jiangxi Normal University",
                "江西财经大学", "Jiangxi University of Finance and Economics",
                "江西理工大学", "Jiangxi University of Science and Technology",
                "江西中医药大学", "Jiangxi University of Chinese Medicine",
                "嘉兴大学", "Jiaxing University",
                "吉林农业大学", "Jilin Agricultural University",
                "吉林财经大学", "Jilin University of Finance and Economics",
                "昆明医科大学", "Kunming Medical University",
                "兰州交通大学", "Lanzhou Jiaotong University",
                "兰州理工大学", "Lanzhou University of Technology",
                "辽宁师范大学", "Liaoning Normal University",
                "辽宁大学", "Liaoning University",
                "辽宁中医药大学", "Liaoning University of Traditional Chinese Medicine",
                "鲁东大学", "Ludong University",
                "南昌航空大学", "Nanchang Hangkong University",
                "南昌大学", "Nanchang University",
                "南京农业大学", "Nanjing Agricultural University",
                "南京审计大学", "Nanjing Audit University",
                "南京林业大学", "Nanjing Forestry University",
                "南京医科大学", "Nanjing Medical University",
                "南京师范大学", "Nanjing Normal University",
                "南京工业大学", "Nanjing Tech University",
                "南京财经大学", "Nanjing University of Finance and Economics",
                "南京信息工程大学", "Nanjing University of Information Science and Technology",
                "南京邮电大学", "Nanjing University of Posts and Telecommunications",
                "南京理工大学", "Nanjing University of Science and Technology",
                "南京艺术学院", "Nanjing University of the Arts",
                "南通大学", "Nantong University",
                "宁波大学", "Ningbo University",
                "宁夏大学", "Ningxia University",
                "宁夏医科大学", "Ningxia Medical University",
                "华北电力大学", "North China Electric Power University",
                "华北理工大学", "North China University of Science and Technology",
                "东北农业大学", "Northeast Agricultural University",
                "东北电力大学", "Northeast Electric Power University",
                "东北林业大学", "Northeast Forestry University",
                "东北师范大学", "Northeast Normal University",
                "东北石油大学", "Northeast Petroleum University",
                "西北大学", "Northwest University",
                "西北政法大学", "Northwest University of Political Science and Law",
                "西北师范大学", "Northwest Normal University",
                "中国人民公安大学", "People's Public Security University of China",
                "青岛大学", "Qingdao University",
                "青岛科技大学", "Qingdao University of Science and Technology",
                "青岛理工大学", "Qingdao University of Technology",
                "青海大学", "Qinghai University",
                "青海师范大学", "Qinghai Normal University",
                "曲阜师范大学", "Qufu Normal University",
                "陕西师范大学", "Shaanxi Normal University",
                "陕西科技大学", "Shaanxi University of Science and Technology",
                "山东农业大学", "Shandong Agricultural University",
                "山东师范大学", "Shandong Normal University",
                "山东财经大学", "Shandong University of Finance and Economics",
                "山东科技大学", "Shandong University of Science and Technology",
                "山东理工大学", "Shandong University of Technology",
                "山东中医药大学", "Shandong University of Traditional Chinese Medicine",
                "上海对外经贸大学", "Shanghai University of International Business and Economics",
                "上海海事大学", "Shanghai Maritime University",
                "上海师范大学", "Shanghai Normal University",
                "上海海洋大学", "Shanghai Ocean University",
                "上海理工大学", "University of Shanghai for Science and Technology",
                "上海戏剧学院", "Shanghai Theatre Academy",
                "上海大学", "Shanghai University",
                "上海电力大学", "Shanghai University of Electric Power",
                "上海政法学院", "Shanghai University of Political Science and Law",
                "上海体育大学", "Shanghai University of Sport",
                "上海中医药大学", "Shanghai University of Traditional Chinese Medicine",
                "上海科技大学", "ShanghaiTech University",
                "汕头大学", "Shantou University",
                "山西农业大学", "Shanxi Agricultural University",
                "山西医科大学", "Shanxi Medical University",
                "山西大学", "Shanxi University",
                "山西财经大学", "Shanxi University of Finance and Economics",
                "沈阳航空航天大学", "Shenyang Aerospace University",
                "沈阳农业大学", "Shenyang Agricultural University",
                "沈阳建筑大学", "Shenyang Jianzhu University",
                "沈阳药科大学", "Shenyang Pharmaceutical University",
                "沈阳工业大学", "Shenyang University of Technology",
                "深圳大学", "Shenzhen University",
                "石河子大学", "Shihezi University",
                "石家庄铁道大学", "Shijiazhuang Tiedao University",
                "四川农业大学", "Sichuan Agricultural University",
                "四川美术学院", "Sichuan Fine Arts Institute",
                "四川外国语大学", "Sichuan International Studies University",
                "四川师范大学", "Sichuan Normal University",
                "苏州大学", "Soochow University",
                "苏州科技大学", "Suzhou University of Science and Technology",
                "华南农业大学", "South China Agricultural University",
                "华南师范大学", "South China Normal University",
                "中南民族大学", "South-Central Minzu University",
                "南方医科大学", "Southern Medical University",
                "西南交通大学", "Southwest Jiaotong University",
                "西南石油大学", "Southwest Petroleum University",
                "西南大学", "Southwest University",
                "西南政法大学", "Southwest University of Political Science and Law",
                "西南财经大学", "Southwestern University of Finance and Economics",
                "天津中医药大学", "Tianjin University of Traditional Chinese Medicine",
                "天津财经大学", "Tianjin University of Finance and Economics",
                "天津医科大学", "Tianjin Medical University",
                "天津师范大学", "Tianjin Normal University",
                "天津理工大学", "Tianjin University of Technology",
                "天津科技大学", "Tianjin University of Science and Technology",
                "天津工业大学", "Tiangong University",
                "西藏大学", "Tibet University",
                "温州医科大学", "Wenzhou Medical University",
                "温州大学", "Wenzhou University",
                "武汉工程大学", "Wuhan Institute of Technology",
                "武汉理工大学", "Wuhan University of Technology",
                "武汉科技大学", "Wuhan University of Science and Technology",
                "武汉纺织大学", "Wuhan Textile University",
                "西安外国语大学", "Xi'an International Studies University",
                "西安建筑科技大学", "Xi'an University of Architecture and Technology",
                "西安理工大学", "Xi'an University of Technology",
                "西安石油大学", "Xi'an Shiyou University",
                "西安科技大学", "Xi'an University of Science and Technology",
                "西安工业大学", "Xi'an Technological University",
                "西安电子科技大学", "Xidian University",
                "湘潭大学", "Xiangtan University",
                "新疆大学", "Xinjiang University",
                "新疆师范大学", "Xinjiang Normal University",
                "扬州大学", "Yangzhou University",
                "燕山大学", "Yanshan University",
                "烟台大学", "Yantai University",
                "云南师范大学", "Yunnan Normal University",
                "云南财经大学", "Yunnan University of Finance and Economics",
                "浙江农林大学", "Zhejiang A&F University",
                "浙江工商大学", "Zhejiang Gongshang University",
                "浙江师范大学", "Zhejiang Normal University",
                "浙江理工大学", "Zhejiang Sci-Tech University",
                "浙江财经大学", "Zhejiang University of Finance and Economics",
                "浙江工业大学", "Zhejiang University of Technology",
                "郑州大学", "Zhengzhou University",
                "中南财经政法大学", "Zhongnan University of Economics and Law",
                "中央戏剧学院", "The Central Academy of Drama",
                "对外经济贸易大学", "University of International Business and Economics",
                "新乡医学院", "Xinxiang Medical University",
                "浙江科技大学", "Zhejiang University of Science and Technology",
                "遵义医科大学", "Zunyi Medical University"
        };
        loadBand(bandB, "B");

        // ============ Band C (80% / 75%) ============
        String[] bandC = {
                "安徽建筑大学", "Anhui Jianzhu University",
                "安庆师范大学", "Anqing Normal University",
                "北方民族大学", "North Minzu University",
                "北华大学", "Beihua University",
                "北京舞蹈学院", "Beijing Dance Academy",
                "北京联合大学", "Beijing Union University",
                "北京服装学院", "Beijing Institute of Fashion Technology",
                "北京印刷学院", "Beijing Institute of Graphic Communication",
                "北京石油化工学院", "Beijing Institute of Petrochemical Technology",
                "北京农学院", "Beijing University of Agriculture",
                "北京警察学院", "Beijing Police College",
                "滨州医学院", "Binzhou Medical University",
                "渤海大学", "Bohai University",
                "长春大学", "Changchun University",
                "长春中医药大学", "Changchun University of Chinese Medicine",
                "常熟理工学院", "Changshu Institute of Technology",
                "成都体育学院", "Chengdu Sport University",
                "成都信息工程大学", "Chengdu University of Information Technology",
                "重庆文理学院", "Chongqing University of Arts and Sciences",
                "重庆科技大学", "Chongqing University of Science and Technology",
                "大理大学", "Dali University",
                "大连交通大学", "Dalian Jiaotong University",
                "大连大学", "Dalian University",
                "大连民族大学", "Dalian Minzu University",
                "大连海洋大学", "Dalian Ocean University",
                "大连工业大学", "Dalian Polytechnic University",
                "福建工程学院", "Fujian University of Technology",
                "福建理工大学", "Fujian University of Technology",
                "甘肃中医药大学", "Gansu University of Chinese Medicine",
                "甘肃政法大学", "Gansu University of Political Science and Law",
                "赣南医科大学", "Gannan Medical University",
                "广东药科大学", "Guangdong Pharmaceutical University",
                "广东技术师范大学", "Guangdong Polytechnic Normal University",
                "广东医科大学", "Guangdong Medical University",
                "广东海洋大学", "Guangdong Ocean University",
                "广西中医药大学", "Guangxi University of Chinese Medicine",
                "广西科技大学", "Guangxi University of Science and Technology",
                "广西民族大学", "Guangxi University for Nationalities",
                "广西艺术学院", "Guangxi Arts University",
                "贵州民族大学", "Guizhou Minzu University",
                "贵州师范学院", "Guizhou Education University",
                "贵州财经大学", "Guizhou University of Finance and Economics",
                "贵州中医药大学", "Guizhou University of Traditional Chinese Medicine",
                "杭州医学院", "Hangzhou Medical College",
                "杭州城市学院", "Hangzhou City University",
                "河北地质大学", "Hebei GEO University",
                "河北中医药大学", "Hebei University of Chinese Medicine",
                "河南科技大学", "Henan University of Science and Technology",
                "河南工业大学", "Henan University of Technology",
                "黑龙江八一农垦大学", "Heilongjiang Bayi Agricultural University",
                "黑龙江科技大学", "Heilongjiang University of Science and Technology",
                "湖北经济学院", "Hubei University of Economics",
                "湖北美术学院", "Hubei Institute of Fine Arts",
                "湖北民族大学", "Hubei Minzu University",
                "湖北师范大学", "Hubei Normal University",
                "湖北医药学院", "Hubei University of Medicine",
                "湖北汽车工业学院", "Hubei University of Automotive Technology",
                "湖南科技学院", "Hunan University of Science and Engineering",
                "湖南工程学院", "Hunan Institute of Engineering",
                "湖南工业大学", "Hunan University of Technology",
                "湖南工商大学", "Hunan University of Technology and Business",
                "华北水利水电大学", "North China University of Water Resources and Electric Power",
                "华北科技学院", "North China Institute of Science and Technology",
                "华北理工大学", "North China University of Science and Technology",
                "淮北师范大学", "Huaibei Normal University",
                "吉林建筑大学", "Jilin Jianzhu University",
                "吉林化工学院", "Jilin Institute of Chemical Technology",
                "吉林艺术学院", "Jilin University of Arts",
                "济南大学", "University of Jinan",
                "江苏理工学院", "Jiangsu University of Technology",
                "江苏科技大学", "Jiangsu University of Science and Technology",
                "江苏海洋大学", "Jiangsu Ocean University",
                "江西科技师范大学", "Jiangxi Science and Technology Normal University",
                "景德镇陶瓷大学", "Jingdezhen Ceramic University",
                "井冈山大学", "Jinggangshan University",
                "锦州医科大学", "Jinzhou Medical University",
                "昆明理工大学", "Kunming University of Science and Technology",
                "辽宁科技大学", "University of Science and Technology Liaoning",
                "辽宁石油化工大学", "Liaoning Petrochemical University",
                "辽宁工程技术大学", "Liaoning Technical University",
                "聊城大学", "Liaocheng University",
                "临沂大学", "Linyi University",
                "鲁迅美术学院", "LuXun Academy of Fine Arts",
                "闽江学院", "Minjiang University",
                "闽南师范大学", "Minnan Normal University",
                "南昌工程学院", "Nanchang Institute of Technology",
                "南京工程学院", "Nanjing Institute of Technology",
                "南京警察学院", "Nanjing Police University",
                "南京晓庄学院", "Nanjing Xiaozhuang University",
                "南宁师范大学", "Nanning Normal University",
                "南通大学", "Nantong University",
                "内蒙古农业大学", "Inner Mongolia Agricultural University",
                "内蒙古医科大学", "Inner Mongolia Medical University",
                "内蒙古科技大学", "Inner Mongolia University of Science and Technology",
                "内蒙古工业大学", "Inner Mongolia University of Technology",
                "宁波工程学院", "Ningbo University of Technology",
                "青岛农业大学", "Qingdao Agricultural University",
                "青岛理工大学", "Qingdao University of Technology",
                "青海民族大学", "Qinghai Minzu University",
                "山东建筑大学", "Shandong Jianzhu University",
                "山东理工大学", "Shandong University of Technology",
                "山东工商学院", "Shandong Technology and Business University",
                "山东艺术学院", "Shandong University of Arts",
                "山西中医药大学", "Shanxi University of Chinese Medicine",
                "山西师范大学", "Shanxi Normal University",
                "上海工程技术大学", "Shanghai University of Engineering Science",
                "上海立信会计金融学院", "Shanghai Lixin University of Accounting and Finance",
                "上海海关学院", "Shanghai Customs College",
                "上海商学院", "Shanghai Business School",
                "上海政法学院", "Shanghai University of Political Science and Law",
                "上海健康医学院", "Shanghai University of Medicine & Health Sciences",
                "绍兴文理学院", "Shaoxing University",
                "沈阳师范大学", "Shenyang Normal University",
                "沈阳理工大学", "Shenyang Ligong University",
                "沈阳体育学院", "Shenyang Sport University",
                "沈阳音乐学院", "Shenyang Conservatory of Music",
                "沈阳化工大学", "Shenyang University of Chemical Technology",
                "沈阳大学", "Shenyang University",
                "石家庄铁道大学", "Shijiazhuang Tiedao University",
                "四川师范大学", "Sichuan Normal University",
                "四川美术学院", "Sichuan Fine Arts Institute",
                "四川音乐学院", "Sichuan Conservatory of Music",
                "四川轻化工大学", "Sichuan University of Science and Engineering",
                "苏州科技大学", "Suzhou University of Science and Technology",
                "塔里木大学", "Tarim University",
                "天津商业大学", "Tianjin University of Commerce",
                "天津理工大学", "Tianjin University of Technology",
                "天津城建大学", "Tianjin Chengjian University",
                "天津体育学院", "Tianjin University of Sport",
                "天津音乐学院", "Tianjin Conservatory of Music",
                "天津美术学院", "Tianjin Academy of Fine Arts",
                "西藏农牧学院", "Tibet Agricultural and Animal Husbandry University",
                "西藏藏医药大学", "Tibet University of Tibetan Medicine",
                "西藏民族大学", "Xizang Minzu University",
                "西安工程大学", "Xi'an Polytechnic University",
                "西安工业大学", "Xi'an Technological University",
                "西安美术学院", "Xi'an Academy of Fine Arts",
                "西安音乐学院", "Xi'an Conservatory of Music",
                "西安体育学院", "Xi'an Physical Education University",
                "西安邮电大学", "Xi'an University of Posts and Telecommunications",
                "西安医学院", "Xi'an Medical University",
                "西安财经大学", "Xi'an University of Finance and Economics",
                "西华大学", "Xihua University",
                "西华师范大学", "China West Normal University",
                "厦门理工学院", "Xiamen University of Technology",
                "新疆农业大学", "Xinjiang Agricultural University",
                "新疆财经大学", "Xinjiang University of Finance and Economics",
                "新疆艺术学院", "Xinjiang Arts University",
                "信阳师范学院", "Xinyang Normal University",
                "星海音乐学院", "Xinghai Conservatory of Music",
                "徐州医科大学", "Xuzhou Medical University",
                "烟台大学", "Yantai University",
                "盐城工学院", "Yancheng Institute of Technology",
                "延安大学", "Yan'an University",
                "浙江传媒学院", "Communication University of Zhejiang",
                "浙江中医药大学", "Zhejiang Chinese Medical University",
                "浙江水利水电学院", "Zhejiang University of Water Resources and Electric Power",
                "浙江音乐学院", "Zhejiang Conservatory of Music",
                "浙江万里学院", "Zhejiang Wanli University",
                "郑州轻工业大学", "Zhengzhou University of Light Industry",
                "郑州航空工业管理学院", "Zhengzhou University of Aeronautics",
                "中原工学院", "Zhongyuan University of Technology",
                "五邑大学", "Wuyi University",
                "无锡学院", "Wuxi University",
                "济宁医学院", "Jining Medical University",
                "中国劳动关系学院", "China University of Labor Relations",
                "广东警官学院", "Guangdong Police College",
                "湖北警官学院", "Hubei University of Police",
                "南京特殊教育师范学院", "Nanjing Normal University of Special Education",
                "台州学院", "Taizhou University",
                "武夷学院", "Wuyi University",
                "西安文理学院", "Xi'an University",
                "武汉体育学院", "Wuhan Sports University",
                "武汉轻工大学", "Wuhan Polytechnic University",
                "西安交通大学", "Xi'an Jiaotong University",
                "新疆师范大学", "Xinjiang Normal University",
                "云南农业大学", "Yunnan Agricultural University",
                "云南艺术学院", "Yunnan Arts University",
                "云南民族大学", "Yunnan Minzu University",
                "云南财经大学", "Yunnan University of Finance and Economics",
                "云南中医药大学", "Yunnan University of Chinese Medicine",
                "浙江外国语学院", "Zhejiang International Studies University",
                "仲恺农业工程学院", "Zhongkai University of Agriculture and Engineering"
        };
        loadBand(bandC, "C");

        // ============ Band D (85% / 80%) ============
        String[] bandD = {
                "安徽科技学院", "Anhui Science and Technology University",
                "安徽艺术学院", "Anhui University of Arts",
                "安阳工学院", "Anyang Institute of Technology",
                "安阳师范学院", "Anyang Normal University",
                "宝鸡文理学院", "Baoji University of Arts and Sciences",
                "包头医学院", "Baotou Medical College",
                "包头师范学院", "Baotou Teachers' College",
                "北部湾大学", "Beibu Gulf University",
                "长治医学院", "Changzhi Medical College",
                "巢湖学院", "Chaohu University",
                "成都师范学院", "Chengdu Normal University",
                "成都工业学院", "Chengdu Technological University",
                "赤峰学院", "Chifeng University",
                "重庆警察学院", "Chongqing Police College",
                "重庆三峡学院", "Chongqing Three Gorges University",
                "重庆第二师范学院", "Chongqing University of Education",
                "滁州学院", "Chuzhou University",
                "大庆师范学院", "Daqing Normal University",
                "德州学院", "Dezhou University",
                "福建江夏学院", "Fujian Jiangxia University",
                "福建警察学院", "Fujian Police College",
                "福建技术师范学院", "Fujian Polytechnic Normal University",
                "阜阳师范大学", "Fuyang Normal University",
                "广东金融学院", "Guangdong University of Finance",
                "广东石油化工学院", "Guangdong University of Petrochemical Technology",
                "广东第二师范学院", "Guangdong University of Education",
                "广西财经学院", "Guangxi University of Finance and Economics",
                "贵州理工学院", "Guizhou Institute of Technology",
                "贵州警察学院", "Guizhou Police College",
                "哈尔滨音乐学院", "Harbin Conservatory of Music",
                "哈尔滨体育学院", "Harbin Sport University",
                "韩山师范学院", "Hanshan Normal University",
                "河北金融学院", "Hebei Finance University",
                "河北北方学院", "Hebei North University",
                "河北科技师范学院", "Hebei Normal University of Science and Technology",
                "河南工程学院", "Henan University of Engineering",
                "河南警察学院", "Henan Police College",
                "河西学院", "Hexi University",
                "黑龙江工程学院", "Heilongjiang Institute of Technology",
                "衡阳师范学院", "Hengyang Normal University",
                "湖北工程学院", "Hubei Engineering University",
                "湖北警官学院", "Hubei University of Police",
                "湖南城市学院", "Hunan City University",
                "湖南工程学院", "Hunan Institute of Engineering",
                "湖南财政经济学院", "Hunan University of Finance and Economics",
                "湖南警察学院", "Hunan Police Academy",
                "湖南理工学院", "Hunan Institute of Science and Technology",
                "湖南文理学院", "Hunan University of Arts and Science",
                "湖南人文科技学院", "Hunan University of Humanities, Science and Technology",
                "吉林警察学院", "Jilin Police College",
                "嘉兴南湖学院", "Jiaxing Nanhu University",
                "金陵科技学院", "Jinling Institute of Technology",
                "九江学院", "Jiujiang University",
                "丽水学院", "Lishui University",
                "龙岩学院", "Longyan University",
                "洛阳师范学院", "Luoyang Normal University",
                "洛阳理工学院", "Luoyang Institute of Science and Technology",
                "绵阳师范学院", "Mianyang Teachers' College",
                "南昌师范学院", "Nanchang Normal University",
                "南阳师范学院", "Nanyang Normal University",
                "南阳理工学院", "Nanyang Institute of Technology",
                "内江师范学院", "Neijiang Normal University",
                "宁波工程学院", "Ningbo University of Technology",
                "平顶山学院", "Pingdingshan University",
                "黔南民族师范学院", "Qiannan Normal College for Nationalities",
                "钦州学院", "Qinzhou University",
                "曲靖师范学院", "Qujing Normal University",
                "衢州学院", "Quzhou University",
                "泉州师范学院", "Quanzhou Normal University",
                "三明学院", "Sanming University",
                "山东政法学院", "Shandong University of Political Science and Law",
                "山东交通学院", "Shandong Jiaotong University",
                "商丘师范学院", "Shangqiu Normal University",
                "上饶师范学院", "Shangrao Normal University",
                "韶关学院", "Shaoguan University",
                "邵阳学院", "Shaoyang University",
                "沈阳工程学院", "Shenyang Institute of Engineering",
                "石家庄学院", "Shijiazhuang University",
                "太原工业学院", "Taiyuan Institute of Technology",
                "天水师范学院", "Tianshui Normal University",
                "铜陵学院", "Tongling University",
                "铜仁学院", "Tongren University",
                "潍坊学院", "Weifang University",
                "渭南师范学院", "Weinan Normal University",
                "西昌学院", "Xichang College",
                "西安航空学院", "Xi'an Aeronautical University",
                "咸阳师范学院", "Xianyang Normal University",
                "湘南学院", "Xiangnan University",
                "信阳农林学院", "Xinyang Agriculture and Forestry University",
                "徐州工程学院", "Xuzhou University of Technology",
                "许昌学院", "Xuchang University",
                "盐城师范学院", "Yancheng Teachers University",
                "宜宾学院", "Yibin University",
                "宜春学院", "Yichun University",
                "营口理工学院", "Yingkou Institute of Technology",
                "玉林师范学院", "Yulin Normal University",
                "玉溪师范学院", "Yuxi Normal University",
                "运城学院", "Yuncheng University",
                "肇庆学院", "Zhaoqing University",
                "浙江外国语学院", "Zhejiang International Studies University",
                "周口师范学院", "Zhoukou Normal University",
                "遵义师范学院", "Zunyi Normal University",
                "武汉体育学院", "Wuhan Sports University",
                "武汉商学院", "Wuhan Business University",
                "山东女子学院", "Shandong Women's University",
                "山西大同大学", "Shanxi Datong University",
                "沈阳医学院", "Shenyang Medical College",
                "四川旅游学院", "Sichuan Tourism University",
                "宿迁学院", "Suqian University",
                "天津职业技术师范大学", "Tianjin University of Technology and Education",
                "通化师范学院", "Tonghua Normal University",
                "吉林工程技术师范学院", "Jilin Engineering Normal University",
                "吉林化工学院", "Jilin Institute of Chemical Technology",
                "吉林医药学院", "Jilin Medical University",
                "嘉应学院", "Jiaying University",
                "凯里学院", "Kaili University",
                "六盘水师范学院", "Liupanshui Normal University",
                "龙东学院", "Longdong University",
                "牡丹江师范学院", "Mudanjiang Normal University",
                "南昌医学院", "Nanchang Medical College",
                "宁德师范学院", "Ningde Normal University",
                "攀枝花学院", "Panzhihua University",
                "琼台师范学院", "Qiongtai Normal University",
                "浙大宁波理工学院", "NingboTech University",
                "仲恺农业工程学院", "Zhongkai University of Agriculture and Engineering",
                "云南警官学院", "Yunnan Police College",
                "湖南第一师范学院", "Hunan First Normal University",
                "怀化学院", "Huaihua University",
                "黄山学院", "Huangshan University",
                "江西警察学院", "Jiangxi Police Institute",
                "景德镇学院", "Jingdezhen University",
                "九江学院", "Jiujiang University",
                "孝感学院", "Hubei Engineering University",
                "新疆警察学院", "Xinjiang Police College",
                "宿州学院", "Suzhou University",
                "西航学院", "Xihang University",
                "新疆财经大学", "Xinjiang University of Finance and Economics",
                "伊犁师范学院", "Yili Normal University",
                "右江民族医学院", "Youjiang Medical University for Nationalities",
                "榆林学院", "Yulin University",
                "枣庄学院", "Zaozhuang University",
                "郑州警察学院", "Zhengzhou Police College",
                "西安文理学院", "Xi'an University of Arts and Science",
                "西安医学院", "Xi'an Medical University"
        };
        loadBand(bandD, "D");

        // ============ Band E (87% / 85%) ============
        String[] bandE = {
                "阿坝师范学院", "Aba Teachers College",
                "安康学院", "Ankang University",
                "安顺学院", "Anshun University",
                "白城师范学院", "Baicheng Normal University",
                "百色学院", "Baise University",
                "保定学院", "Baoding University",
                "保山学院", "Baoshan University",
                "北海艺术设计学院", "Beihai College of Art and Design",
                "蚌埠学院", "Bengbu University",
                "亳州学院", "Bozhou University",
                "沧州师范学院", "Cangzhou Normal University",
                "昌吉学院", "Changji University",
                "长沙师范学院", "Changsha Normal University",
                "长治学院", "Changzhi University",
                "池州学院", "Chizhou University",
                "楚雄师范学院", "Chuxiong Normal University",
                "大连艺术学院", "Dalian Art College",
                "滇西应用技术大学", "West Yunnan University of Applied Sciences",
                "鄂尔多斯应用技术学院", "Ordos Institute of Technology",
                "赣南科技学院", "Gannan University of Science and Technology",
                "甘肃医学院", "Gansu Medical College",
                "甘肃民族师范学院", "Gansu Normal University for Nationalities",
                "赣东学院", "Gandong University",
                "广西科技师范学院", "Guangxi Science and Technology Normal University",
                "广西民族师范学院", "Guangxi Normal University for Nationalities",
                "广西职业师范学院", "Guangxi Vocational Normal University",
                "桂林航天工业学院", "Guilin University of Aerospace Technology",
                "桂林旅游学院", "Guilin Tourism University",
                "贵州工程应用技术学院", "Guizhou University of Engineering Science",
                "贵州商学院", "Guizhou University of Commerce",
                "哈尔滨金融学院", "Harbin Finance University",
                "海口经济学院", "Haikou University of Economics",
                "汉江师范学院", "Hanjiang Normal University",
                "河北传媒学院", "Hebei Institute of Communications",
                "河北环境工程学院", "Hebei University of Environmental Engineering",
                "河北美术学院", "Hebei Academy of Fine Arts",
                "河北民族师范学院", "Hebei Normal University for Nationalities",
                "河北水利电力学院", "Hebei University of Water Resources and Electric Engineering",
                "河北体育学院", "Hebei Sport University",
                "河池学院", "Hechi University",
                "黑河学院", "Heihe University",
                "黑龙江工业学院", "Heilongjiang University of Technology",
                "河南工学院", "Henan Institute of Technology",
                "河南牧业经济学院", "Henan University of Animal Husbandry and Economy",
                "河南财政金融学院", "Henan Finance University",
                "衡水学院", "Hengshui University",
                "河套学院", "Hetao University",
                "菏泽学院", "Heze University",
                "呼和浩特民族学院", "Hohhot Minzu College",
                "呼伦贝尔学院", "Hulunbuir University",
                "湖南工学院", "Hunan Institute of Technology",
                "湖南女子学院", "Hunan Women's University",
                "湖南医药学院", "Hunan University of Medicine",
                "湖南交通工程学院", "Hunan Institute of Traffic Engineering",
                "吉林工商学院", "Jilin Business and Technology College",
                "吉林农业科技学院", "Jilin Agricultural Science and Technology University",
                "晋中学院", "Jinzhong University",
                "荆楚理工学院", "Jingchu University of Technology",
                "集宁师范学院", "Jining Normal University",
                "喀什大学", "Kashi University",
                "兰州城市学院", "Lanzhou City University",
                "兰州工业学院", "Lanzhou Institute of Technology",
                "兰州文理学院", "Lanzhou University of Arts and Science",
                "黎明职业大学", "Liming Vocational University",
                "丽江文化旅游学院", "Lijiang Culture and Tourism College",
                "陇南师范高等专科学校", "Longnan Teachers College",
                "吕梁学院", "Lyuliang University",
                "马鞍山学院", "Maanshan University",
                "南昌理工学院", "Nanchang Institute of Technology",
                "南京传媒学院", "Communication University of China, Nanjing",
                "南宁学院", "Nanning University",
                "内蒙古艺术学院", "Inner Mongolia Arts University",
                "宁夏理工学院", "Ningxia Institute of Science and Technology",
                "萍乡学院", "Pingxiang University",
                "普洱学院", "Puer University",
                "齐鲁医药学院", "Qilu Medical University",
                "黔南民族医学高等专科学校", "Qiannan Medical College for Nationalities",
                "钦州学院", "Qinzhou University",
                "琼台师范学院", "Qiongtai Normal University",
                "三明学院", "Sanming University",
                "山东农业工程学院", "Shandong Agriculture and Engineering University",
                "山东青年政治学院", "Shandong Youth University of Political Science",
                "山东石油化工学院", "Shandong Institute of Petroleum and Chemical Technology",
                "山东协和学院", "Shandong Xiehe University",
                "山东英才学院", "Shandong Yingcai University",
                "山西传媒学院", "Communication University of Shanxi",
                "山西工程技术学院", "Shanxi Institute of Technology",
                "山西工学院", "Shanxi College of Technology",
                "山西警察学院", "Shanxi Police College",
                "山西科技学院", "Shanxi Institute of Science and Technology",
                "山西能源学院", "Shanxi Institute of Energy",
                "山西应用科技学院", "Shanxi University of Applied Science and Technology",
                "陕西学前师范学院", "Shaanxi Xueqian Normal University",
                "陕西国际商贸学院", "Shaanxi International Trade & Commerce College",
                "陕西服装工程学院", "Shaanxi Fashion Engineering University",
                "商洛学院", "Shangluo University",
                "上海视觉艺术学院", "Shanghai Institute of Visual Arts",
                "沈阳城市学院", "Shenyang City University",
                "沈阳工学院", "Shenyang Institute of Technology",
                "沈阳科技学院", "Shenyang University of Chemical Technology",
                "四川传媒学院", "Sichuan University of Media and Communications",
                "四川电影电视学院", "Sichuan Film and Television College",
                "四川工商学院", "Sichuan Technology and Business University",
                "四川工业科技学院", "Sichuan University of Science & Engineering",
                "四川民族学院", "Sichuan Minzu College",
                "四川外国语大学成都学院", "Chengdu Institute Sichuan International Studies University",
                "四川文化艺术学院", "Sichuan College of Culture and Arts",
                "四川文理学院", "Sichuan University of Arts and Science",
                "太原学院", "Taiyuan University",
                "泰州学院", "Taizhou University",
                "唐山学院", "Tangshan University",
                "天津传媒学院", "Tianjin College of Media and Communication",
                "天津仁爱学院", "Tianjin Renai College",
                "天津天狮学院", "Tianshi College",
                "天津中德应用技术大学", "Tianjin Sino-German University of Applied Sciences",
                "绥化学院", "Suihua University",
                "潍坊科技学院", "Weifang University of Science and Technology",
                "潍坊理工学院", "Weifang Institute of Technology",
                "文山学院", "Wenshan University",
                "无锡太湖学院", "Wuxi Taihu University",
                "武昌工学院", "Wuchang Institute of Technology",
                "武昌理工学院", "Wuchang University of Technology",
                "武昌首义学院", "Wuchang Shouyi University",
                "武汉传媒学院", "Wuhan University of Communication",
                "武汉东湖学院", "Wuhan Donghu University",
                "武汉工商学院", "Wuhan Technology and Business University",
                "武汉华夏理工学院", "Wuhan Huaxia University of Technology",
                "武汉晴川学院", "Wuhan Qingchuan University",
                "武汉设计工程学院", "Wuhan Institute of Design and Engineering",
                "武汉生物工程学院", "Wuhan Bioengineering Institute",
                "武汉体育学院体育科技学院", "Sports Science and Technology College of Wuhan Sports University",
                "武汉文理学院", "Wuhan University of Arts and Science",
                "武汉学院", "Wuhan College",
                "西安财经大学行知学院", "Xingzhi College of Xi'an University of Finance and Economics",
                "西安翻译学院", "Xi'an Fanyi University",
                "西安工商学院", "Xi'an Technology and Business College",
                "西安交通工程学院", "Xi'an Traffic Engineering Institute",
                "西安科技大学高新学院", "Gaoxin College of Xi'an University of Science and Technology",
                "西安理工大学高科学院", "Gaoke College of Xi'an University of Technology",
                "西安明德理工学院", "Xi'an Mingde Institute of Technology",
                "西安欧亚学院", "Xi'an Eurasia University",
                "西安培华学院", "Xi'an Peihua University",
                "西安汽车职业大学", "Xi'an Automotive University",
                "西安思源学院", "Xi'an Siyuan University",
                "西安外事学院", "Xi'an International University",
                "西京学院", "Xijing University",
                "新乡医学院三全学院", "Sanquan College of Xinxiang Medical University",
                "信阳学院", "Xinyang University",
                "兴义民族师范学院", "Xingyi Normal University for Nationalities",
                "新疆工程学院", "Xinjiang Institute of Engineering",
                "新疆科技学院", "Xinjiang Institute of Science and Technology",
                "新疆理工学院", "Xinjiang Institute of Technology",
                "新疆农业大学科学技术学院", "College of Science and Technology, Xinjiang Agricultural University",
                "新疆天山职业技术大学", "Xinjiang Tianshan Vocational and Technical University",
                "新疆政法学院", "Xinjiang Institute of Political Science and Law",
                "新余学院", "Xinyu University",
                "宣城职业技术学院", "Xuancheng Vocational and Technical College",
                "烟台科技学院", "Yantai Institute of Science and Technology",
                "烟台理工学院", "Yantai Institute of Technology",
                "烟台南山学院", "Yantai Nanshan University",
                "燕京理工学院", "Yanjing Institute of Technology",
                "扬州大学广陵学院", "Guangling College of Yangzhou University",
                "阳光学院", "Yango University",
                "仰恩大学", "Yang-En University",
                "银川能源学院", "Yinchuan University of Energy",
                "营口理工学院", "Yingkou Institute of Technology",
                "永城职业学院", "Yongcheng Vocational College",
                "榆林学院", "Yulin University",
                "豫章师范学院", "Yuzhang Normal University",
                "元培学院", "Shaoxing University Yuanpei College",
                "运城学院", "Yuncheng University",
                "云南大学滇池学院", "Dianchi College of Yunnan University",
                "云南大学旅游文化学院", "Lijiang Culture and Tourism College",
                "云南工商学院", "Yunnan Technology and Business University",
                "云南经济管理学院", "Yunnan University of Business Management",
                "云南艺术学院文华学院", "Wenhua College of Yunnan Arts University",
                "枣庄学院", "Zaozhuang University",
                "长春财经学院", "Changchun University of Finance and Economics",
                "长春电子科技学院", "Changchun University of Electronic Technology",
                "长春光华学院", "Changchun Guanghua University",
                "长春建筑学院", "Changchun University of Architecture and Civil Engineering",
                "长春科技学院", "Changchun University of Science and Technology",
                "长春人文学院", "Changchun Humanities and Sciences College",
                "长沙医学院", "Changsha Medical University",
                "浙江广厦建设职业技术大学", "Zhejiang Guangsha Vocational and Technical University of Construction",
                "浙江树人大学", "Zhejiang Shuren University",
                "浙江越秀外国语学院", "Zhejiang Yuexiu University of Foreign Languages",
                "郑州财经学院", "Zhengzhou College of Finance and Economics",
                "郑州工商学院", "Zhengzhou Technology and Business University",
                "郑州工业应用技术学院", "Zhengzhou University of Industrial Technology",
                "郑州经贸学院", "Zhengzhou Business University",
                "郑州科技学院", "Zhengzhou University of Science and Technology",
                "郑州商学院", "Zhengzhou Business College",
                "郑州师范学院", "Zhengzhou Normal University",
                "郑州西亚斯学院", "Sias University",
                "中南林业科技大学涉外学院", "Swan College, Central South University of Forestry and Technology",
                "中原工学院信息商务学院", "Zhongyuan Institute of Technology Information and Business College",
                "中原科技学院", "Zhongyuan Institute of Science and Technology",
                "株洲师范高等专科学校", "Zhuzhou Teachers College",
                "珠海科技学院", "Zhuhai College of Science and Technology",
                "驻马店职业技术学院", "Zhumadian Vocational and Technical College",
                "遵义医科大学医学与科技学院", "Medical and Technology College of Zunyi Medical University",
                "邢台学院", "Xingtai University",
                "济宁学院", "Jining University",
                "红河学院", "Honghe University",
                "呼伦贝尔学院", "Hulunbuir University",
                "宿州学院", "Suzhou University",
                "东莞城市学院", "City College of Dongguan University of Technology",
                "山东石油化工学院", "Shandong Institute of Petroleum and Chemical Technology"
        };
        loadBand(bandE, "E");

        // ============ TNE (Transnational Education) ============
        String[] tne = {
                "西交利物浦大学", "Xi'an Jiaotong-Liverpool University",
                "宁波诺丁汉大学", "University of Nottingham Ningbo China",
                "北京师范大学-香港浸会大学联合国际学院",
                "Beijing Normal University–Hong Kong Baptist University United International College",
                "北师港浸大", "UIC",
                "上海纽约大学", "New York University Shanghai",
                "温州肯恩大学", "Wenzhou-Kean University",
                "昆山杜克大学", "Duke Kunshan University",
                "香港中文大学（深圳）", "The Chinese University of Hong Kong, Shenzhen",
                "深圳北理莫斯科大学", "Shenzhen MSU-BIT University",
                "广东以色列理工学院", "Guangdong Technion-Israel Institute of Technology",
                "香港科技大学（广州）", "The Hong Kong University of Science and Technology (Guangzhou)",
                "香港城市大学（东莞）", "City University of Hong Kong (Dongguan)"
        };
        loadBand(tne, "TNE");
    }

    /**
     * 辅助方法：批量加载学校到 Map 中
     * 将数组中的每一个学校名称（中文和英文）都映射到对应的 Band
     */
    private static void loadBand(String[] schools, String band) {
        for (String school : schools) {
            if (school != null && !school.trim().isEmpty()) {
                UNIVERSITY_BANDS.put(school.trim(), band);
            }
        }
    }

    /**
     * 辅助方法：单个加载学校
     */
    private static void putBand(String schoolName, String band) {
        UNIVERSITY_BANDS.put(schoolName.trim(), band);
    }

    @Override
    public String getSchoolName() {
        return "格拉斯哥大学";
    }

    @Override
    public String getRegion() {
        return "英国";
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        List<MatchingResult> results = new ArrayList<>();

        // 1. 查找学校实体
        School school = dataService.getSchoolByName(getSchoolName());
        if (school == null) {
            List<School> ukSchools = dataService.getSchoolsByRegion("英国");
            school = ukSchools.stream()
                    .filter(s -> s.getEnglishName() != null
                            && s.getEnglishName().equalsIgnoreCase("University of Glasgow"))
                    .findFirst()
                    .orElse(null);

            if (school == null) {
                log.warn("未在数据库中找到格拉斯哥大学");
                return results;
            }
        }

        // 2. 获取学生背景
        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 获取学生学校 Band
        String studentBand = getUniversityBand(undergradSchool);

        // 3. 遍历专业
        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        for (Major major : majors) {
            // 3.1 判断该专业要求的英位等级 (2:1 还是 2:2)
            String ukDegreeReq = determineUKDegreeRequirement(major.getName());

            // 3.2 获取分数线
            double requiredScore = getRequiredScore(studentBand, ukDegreeReq);

            // 3.3 计算匹配度
            double matchScore = calculateMatchScore(studentInfo, requiredScore, school, major.getName());
            String matchLevel = determineMatchLevel(matchScore);
            String matchReason = generateMatchReason(studentInfo, studentBand, requiredScore, ukDegreeReq);

            // 3.4 构建结果
            MatchingResult result = new MatchingResult();
            result.setUserId(studentInfo.getUserId());
            result.setSchoolId(school.getId());
            result.setSchoolName(school.getName());
            result.setMajorName(major.getName());
            result.setMatchScore(matchScore);
            result.setMatchLevel(matchLevel);
            result.setMatchReason(matchReason);
            result.setAdmissionProbability(calculateAdmissionProbability(matchScore));
            result.setRecommendationIndex(calculateRecommendationIndex(matchScore));
            result.setRegion("英国");
            result.setAlgorithmStrategy("UK_GLASGOW_MATCHING_ALGORITHM");

            try {
                result.setStudentInfoSnapshot(objectMapper.writeValueAsString(studentInfo));
            } catch (Exception e) {
                result.setStudentInfoSnapshot("{}");
            }

            results.add(result);
        }

        // 排序
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

    private String getUniversityBand(String schoolName) {
        if (schoolName == null)
            return "UNKNOWN";

        // 1. 直接匹配
        if (UNIVERSITY_BANDS.containsKey(schoolName.trim())) {
            return UNIVERSITY_BANDS.get(schoolName.trim());
        }

        // 2. 模糊匹配
        String cleanName = schoolName.replace("大学", "").replace("学院", "");
        for (Map.Entry<String, String> entry : UNIVERSITY_BANDS.entrySet()) {
            if (entry.getKey().contains(cleanName)) {
                return entry.getValue();
            }
        }

        // 3. TNE 合作办学检查 (作为兜底，防止Map没覆盖到)
        if (schoolName.contains("西交利物浦") || schoolName.contains("宁波诺丁汉") || schoolName.contains("北师港浸大")
                || schoolName.contains("香港中文大学（深圳）")) {
            return "TNE";
        }

        // 默认不在名单内
        return "UNKNOWN";
    }

    private String determineUKDegreeRequirement(String majorName) {
        if (majorName == null)
            return "2:1";
        // 格拉斯哥绝大多数硕士专业要求 2:1
        // 如果未来有 2:2 专业，可在此扩展逻辑
        return "2:1";
    }

    /**
     * 获取要求的均分 (百分制)
     */
    private double getRequiredScore(String band, String ukDegree) {
        // TNE 特殊处理 (GPA 4.0 Scale: 2:1 -> 2.8 / 3.0+, 2:2 -> 2.5 / 2.6+)
        // 转换为百分制：2.8/4.0 ≈ 75-78%
        if ("TNE".equals(band)) {
            if ("2:1".equals(ukDegree))
                return 75.0;
            else
                return 68.0;
        }

        if ("UNKNOWN".equals(band)) {
            return 90.0; // 不在名单内的学校，要求极高或不接受
        }

        // Band A (70% / 65%) - UPDATED from image
        if ("A".equals(band)) {
            if ("2:1".equals(ukDegree))
                return 70.0;
            else
                return 65.0;
        }

        // Band B (75% / 70%)
        if ("B".equals(band)) {
            if ("2:1".equals(ukDegree))
                return 75.0;
            else
                return 70.0;
        }

        // Band C (80% / 75%)
        if ("C".equals(band)) {
            if ("2:1".equals(ukDegree))
                return 80.0;
            else
                return 75.0;
        }

        // Band D (85% / 80%)
        if ("D".equals(band)) {
            if ("2:1".equals(ukDegree))
                return 85.0;
            else
                return 80.0;
        }

        // Band E (87% / 85%)
        if ("E".equals(band)) {
            if ("2:1".equals(ukDegree))
                return 87.0;
            else
                return 85.0;
        }

        return 90.0; // Default safe
    }

    /**
     * 直接获取输入的均分
     */
    private double getStudentAverageScore(MatchingRequest.StudentInfoDTO student) {
        if (student.getGpa() == null)
            return 0.0;
        // 直接返回用户输入的百分制分数
        return student.getGpa();
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, School school,
            String majorName) {
        // 获取百分制均分
        double avgScore = getStudentAverageScore(student);

        if (avgScore == 0.0)
            return 0.0;

        double baseScore = 0.0;

        // 1. 均分评分 (权重 70%)
        if (avgScore >= required) {
            baseScore += 70.0; // 达标
            if (avgScore >= required + 2)
                baseScore += 5; // 高分加成
            if (avgScore >= required + 5)
                baseScore += 5; // 极高分
        } else if (avgScore >= required - 2) {
            // 稍微差一点 (Argue区间)
            baseScore += 50.0 + (avgScore - (required - 2)) * 10.0;
        } else {
            // 差距较大
            baseScore += 20.0;
        }

        // 2. 学校背景评分 (权重 15%)
        baseScore += 15.0;

        // 3. 专业相关度 (权重 15%)
        String target = student.getTargetMajor() != null ? student.getTargetMajor() : "";
        if (majorName.contains(target) || target.isEmpty()) {
            baseScore += 15.0;
        } else {
            baseScore += 5.0;
        }

        return Math.min(100.0, baseScore);
    }

    private String determineMatchLevel(double score) {
        if (score >= 90)
            return "保底";
        if (score >= 80)
            return "稳妥";
        if (score >= 60)
            return "冲刺";
        return "不建议";
    }

    private String generateMatchReason(MatchingRequest.StudentInfoDTO student, String band, double required,
            String ukDegree) {
        double avgScore = getStudentAverageScore(student);

        StringBuilder sb = new StringBuilder();
        sb.append("【格拉斯哥大学】");

        if ("UNKNOWN".equals(band)) {
            sb.append("您的院校不在格拉斯哥大学的 Accepted List 中，申请风险极高。");
            return sb.toString();
        } else if ("TNE".equals(band)) {
            sb.append("您的院校属于中外合作办学 (TNE)。");
        } else {
            sb.append("您的院校属于格拉斯哥大学 Band ").append(band).append(" 类名单。");
        }

        sb.append("该专业要求英国 ").append(ukDegree).append(" 学位，对应 Band ").append(band).append(" 的均分要求为 ")
                .append((int) required).append("%。");
        sb.append(" 您的均分为 ").append(String.format("%.1f", avgScore)).append("%。");

        if (avgScore >= required) {
            sb.append(" 成绩完全达标，录取希望很大。");
        } else if (avgScore >= required - 2) {
            sb.append(" 成绩略有差距，但如果在核心课程上表现优秀，仍有机会。");
        } else {
            sb.append(" 成绩未达标，建议提升均分或考虑其他院校。");
        }

        return sb.toString();
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.95;
        if (score >= 80)
            return 0.85;
        if (score >= 60)
            return 0.40;
        return 0.10;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
