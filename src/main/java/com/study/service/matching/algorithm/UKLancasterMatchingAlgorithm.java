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
 * 兰卡斯特大学 (Lancaster University) 2026/27 匹配算法
 * 数据来源：Lancaster University List of Chinese Institution Categories 2026/27
 *
 * 核心逻辑：
 * 1. Band 1 (Prestigious): 2:1 -> 75% | 2:2 -> 70%
 * 2. Band 2 (Accepted List): 2:1 -> 80% | 2:2 -> 75%
 * 3. Band 3 (Others): 2:1 -> 85% | 2:2 -> 80%
 */
@Service
@SuppressWarnings("unused")
public class UKLancasterMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKLancasterMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    // Band 1 院校名单 (Set 用于快速查找)
    private static final Set<String> BAND_1_UNIVERSITIES = new HashSet<>();
    // Band 2 院校名单 (Set 用于快速查找)
    private static final Set<String> BAND_2_UNIVERSITIES = new HashSet<>();

    static {
        // ==========================================
        // Band 1 List (985/211/Double First Class - Core List)
        // ==========================================
        String[] band1 = {
                "安徽大学", "Anhui University",
                "合肥工业大学", "Hefei University of Technology",
                "中国科学技术大学", "University of Science and Technology of China",
                "北京航空航天大学", "Beihang University",
                "北京外国语大学", "Beijing Foreign Studies University",
                "北京林业大学", "Beijing Forestry University",
                "北京理工大学", "Beijing Institute of Technology",
                "北京交通大学", "Beijing Jiaotong University",
                "北京语言大学", "Beijing Language and Culture University",
                "北京师范大学", "Beijing Normal University",
                "北京体育大学", "Beijing Sport University",
                "北京化工大学", "Beijing University of Chemical Technology",
                "北京中医药大学", "Beijing University of Chinese Medicine",
                "北京邮电大学", "Beijing University of Posts and Telecommunications",
                "北京工业大学", "Beijing University of Technology",
                "首都医科大学", "Capital Medical University",
                "首都师范大学", "Capital Normal University",
                "中央戏剧学院", "Central Academy of Drama",
                "中央美术学院", "Central Academy of Fine Arts",
                "中央音乐学院", "Central Conservatory of Music",
                "中央财经大学", "Central University of Finance and Economics",
                "中国农业大学", "China Agricultural University",
                "中国地质大学（北京）", "China University of Geosciences (Beijing)",
                "中国矿业大学（北京）", "China University of Mining and Technology (Beijing)",
                "中国石油大学（北京）", "China University of Petroleum",
                "中国政法大学", "China University of Political Science and Law",
                "中国传媒大学", "Communication University of China",
                "中央民族大学", "Minzu University of China",
                "华北电力大学", "North China Electric Power University",
                "北京协和医学院", "Peking Union Medical College",
                "北京大学", "Peking University",
                "中国人民大学", "Renmin University of China",
                "清华大学", "Tsinghua University",
                "中国科学院大学", "University of Chinese Academy of Sciences",
                "中国社会科学院大学", "University of Chinese Academy of Social Sciences",
                "对外经济贸易大学", "University of International Business and Economics",
                "北京科技大学", "University of Science and Technology Beijing",
                "福建师范大学", "Fujian Normal University",
                "福州大学", "Fuzhou University",
                "厦门大学", "Xiamen University",
                "兰州大学", "Lanzhou University",
                "广东工业大学", "Guangdong University of Technology",
                "广州医科大学", "Guangzhou Medical University",
                "广州大学", "Guangzhou University",
                "暨南大学", "Jinan University",
                "深圳大学", "Shenzhen University",
                "华南农业大学", "South China Agricultural University",
                "华南师范大学", "South China Normal University",
                "华南理工大学", "South China University of Technology",
                "南方医科大学", "Southern Medical University",
                "南方科技大学", "Southern University of Science and Technology",
                "中山大学", "Sun Yat-sen University",
                "广西大学", "Guangxi University",
                "贵州大学", "Guizhou University",
                "海南大学", "Hainan University",
                "河北医科大学", "Hebei Medical University",
                "河北工业大学", "Hebei University of Technology",
                "燕山大学", "Yanshan University",
                "河南大学", "Henan University",
                "郑州大学", "Zhengzhou University",
                "哈尔滨工程大学", "Harbin Engineering University",
                "哈尔滨工业大学", "Harbin Institute of Technology",
                "哈尔滨医科大学", "Harbin Medical University",
                "东北农业大学", "Northeast Agricultural University",
                "东北林业大学", "Northeast Forestry University",
                "华中师范大学", "Central China Normal University",
                "中国地质大学（武汉）", "China University of Geosciences (Wuhan)",
                "华中农业大学", "Huazhong Agricultural University",
                "华中科技大学", "Huazhong University of Science and Technology",
                "湖北大学", "Hubei University",
                "武汉大学", "Wuhan University",
                "武汉科技大学", "Wuhan University of Science and Technology",
                "武汉理工大学", "Wuhan University of Technology",
                "中南财经政法大学", "Zhongnan University of Economics and Law",
                "中南大学", "Central South University",
                "湖南师范大学", "Hunan Normal University",
                "湖南大学", "Hunan University",
                "湘潭大学", "Xiangtan University",
                "吉林大学", "Jilin University",
                "东北师范大学", "Northeast Normal University",
                "延边大学", "Yanbian University",
                "中国药科大学", "China Pharmaceutical University",
                "中国矿业大学", "China University of Mining and Technology",
                "河海大学", "Hohai University",
                "江南大学", "Jiangnan University",
                "江苏大学", "Jiangsu University",
                "南京农业大学", "Nanjing Agricultural University",
                "南京林业大学", "Nanjing Forestry University",
                "南京医科大学", "Nanjing Medical University",
                "南京师范大学", "Nanjing Normal University",
                "南京工业大学", "Nanjing Tech University",
                "南京大学", "Nanjing University",
                "南京航空航天大学", "Nanjing University of Aeronautics and Astronautics",
                "南京信息工程大学", "Nanjing University of Information Science and Technology",
                "南京邮电大学", "Nanjing University of Posts and Telecommunications",
                "南京理工大学", "Nanjing University of Science and Technology",
                "苏州大学", "Soochow University",
                "东南大学", "Southeast University",
                "扬州大学", "Yangzhou University",
                "南昌大学", "Nanchang University",
                "中国医科大学", "China Medical University",
                "大连海事大学", "Dalian Maritime University",
                "大连医科大学", "Dalian Medical University",
                "大连理工大学", "Dalian University of Technology",
                "东北财经大学", "Dongbei University of Finance and Economics",
                "辽宁大学", "Liaoning University",
                "东北大学", "Northeastern University",
                "内蒙古大学", "Inner Mongolia University",
                "宁夏大学", "Ningxia University",
                "青海大学", "Qinghai University",
                "中国石油大学（华东）", "China University of Petroleum (East China)",
                "中国海洋大学", "Ocean University of China",
                "青岛大学", "Qingdao University",
                "山东师范大学", "Shandong Normal University",
                "山东大学", "Shandong University",
                "太原理工大学", "Taiyuan University of Technology",
                "长安大学", "Chang'an University",
                "西北农林科技大学", "Northwest A&F University",
                "西北大学", "Northwest University",
                "西北工业大学", "Northwestern Polytechnical University",
                "陕西师范大学", "Shaanxi Normal University",
                "西安交通大学", "Xi'an Jiaotong University",
                "西安电子科技大学", "Xidian University",
                "东华大学", "Donghua University",
                "华东师范大学", "East China Normal University",
                "华东理工大学", "East China University of Science and Technology",
                "复旦大学", "Fudan University",
                "上海外国语大学", "Shanghai International Studies University",
                "上海交通大学", "Shanghai Jiao Tong University",
                "上海师范大学", "Shanghai Normal University",
                "上海大学", "Shanghai University",
                "上海财经大学", "Shanghai University of Finance and Economics",
                "上海中医药大学", "Shanghai University of Traditional Chinese Medicine",
                "上海科技大学", "ShanghaiTech University",
                "同济大学", "Tongji University",
                "上海理工大学", "University of Shanghai for Science and Technology",
                "四川农业大学", "Sichuan Agricultural University",
                "四川大学", "Sichuan University",
                "西南交通大学", "Southwest Jiaotong University",
                "西南财经大学", "Southwestern University of Finance and Economics",
                "电子科技大学", "University of Electronic Science and Technology of China",
                "南开大学", "Nankai University",
                "天津医科大学", "Tianjin Medical University",
                "天津大学", "Tianjin University",
                "西藏大学", "Tibet University",
                "石河子大学", "Shihezi University",
                "新疆大学", "Xinjiang University",
                "云南大学", "Yunnan University",
                "杭州电子科技大学", "Hangzhou Dianzi University",
                "宁波大学", "Ningbo University",
                "温州医科大学", "Wenzhou Medical University",
                "浙江师范大学", "Zhejiang Normal University",
                "浙江大学", "Zhejiang University",
                "浙江理工大学", "Zhejiang University of Technology",
                "重庆医科大学", "Chongqing Medical University",
                "重庆大学", "Chongqing University",
                "西南大学", "Southwest University"
        };
        loadBand(band1, BAND_1_UNIVERSITIES);

        // ==========================================
        // Band 2 List (Complete Accepted List)
        // ==========================================
        String[] band2 = {
                // --- Anhui ---
                "安徽农业大学", "Anhui Agricultural University",
                "安徽建筑大学", "Anhui Jianzhu University",
                "安徽医科大学", "Anhui Medical University",
                "安徽师范大学", "Anhui Normal University",
                "安徽工程大学", "Anhui Polytechnic University",
                "安徽科技学院", "Anhui Science and Technology University",
                "安徽中医药大学", "Anhui University of Chinese Medicine",
                "安徽财经大学", "Anhui University of Finance and Economics",
                "安徽理工大学", "Anhui University of Science and Technology",
                "安徽工业大学", "Anhui University of Technology",
                "安庆师范大学", "Anqing Normal University",
                "蚌埠医科大学", "Bengbu Medical University",
                "阜阳师范大学", "Fuyang Normal University",
                "合肥师范学院", "Hefei Normal University",
                "合肥大学", "Hefei University",
                "淮北师范大学", "Huaibei Normal University",
                "皖南医学院", "Wannan Medical College",

                // --- Beijing ---
                "北京电影学院", "Beijing Film Academy",
                "北京信息科技大学", "Beijing Information Science and Technology University",
                "北京服装学院", "Beijing Institute of Fashion Technology",
                "北京印刷学院", "Beijing Institute of Graphic Communication",
                "北京石油化工学院", "Beijing Institute of Petrochemical Technology",
                "北京第二外国语学院", "Beijing International Studies University",
                "北京工商大学", "Beijing Technology and Business University",
                "北京联合大学", "Beijing Union University",
                "北京农学院", "Beijing University of Agriculture",
                "北京建筑大学", "Beijing University of Civil Engineering and Architecture",
                "北京物资学院", "Beijing Wuzi University",
                "首都经济贸易大学", "Capital University of Economics and Business",
                "首都体育学院", "Capital University of Physical Education and Sports",
                "中国音乐学院", "China Conservatory of Music",
                "外交学院", "China Foreign Affairs University",
                "中国劳动关系学院", "China University of Labor Relations",
                "中华女子学院", "China Women's University",
                "中国戏曲学院", "National Academy of Chinese Theatre Art",
                "北方工业大学", "North China University of Technology",
                "中国人民公安大学", "People's Public Security University of China",
                "国际关系学院", "University of International Relations",

                // --- Fujian ---
                "福建农林大学", "Fujian Agricultural and Forestry University",
                "福建医科大学", "Fujian Medical University",
                "福建理工大学", "Fujian University of Technology",
                "福建中医药大学", "Fujian University of Traditional Chinese Medicine",
                "华侨大学", "Huaqiao University",
                "集美大学", "Jimei University",
                "闽南师范大学", "Minnan Normal University",
                "厦门理工学院", "Xiamen University of Technology",

                // --- Gansu ---
                "甘肃农业大学", "Gansu Agricultural University",
                "甘肃政法大学", "Gansu University of Political Science and Law",
                "兰州交通大学", "Lanzhou Jiaotong University",
                "兰州理工大学", "Lanzhou University of Technology",
                "兰州财经大学", "Lanzhou University of Finance and Economics",
                "西北民族大学", "Northwest Minzu University",
                "西北师范大学", "Northwest Normal University",

                // --- Guangdong ---
                "东莞理工学院", "Dongguan University of Technology",
                "佛山大学", "Foshan University",
                "广东外语外贸大学", "Guangdong University of Foreign Studies",
                "广东财经大学", "Guangdong University of Finance and Economics",
                "广东医科大学", "Guangdong Medical University",
                "广东海洋大学", "Guangdong Ocean University",
                "广东药科大学", "Guangdong Pharmaceutical University",
                "广东技术师范大学", "Guangdong Polytechnic Normal University",
                "广东金融学院", "Guangdong University of Finance",
                "广州美术学院", "Guangzhou Academy of Fine Arts",
                "广州中医药大学", "Guangzhou University of Chinese Medicine",
                "广州体育学院", "Guangzhou Sport University",
                "韩山师范学院", "Hanshan Normal University",
                "岭南师范学院", "Lingnan Normal University",
                "汕头大学", "Shantou University",
                "深圳技术大学", "Shenzhen Technology University",
                "五邑大学", "Wuyi University",
                "星海音乐学院", "Xinghai Conservatory of Music",
                "仲恺农业工程学院", "Zhongkai University of Agriculture and Engineering",

                // --- Guangxi ---
                "广西医科大学", "Guangxi Medical University",
                "广西民族大学", "Guangxi Minzu University",
                "广西师范大学", "Guangxi Normal University",
                "广西中医药大学", "Guangxi University of Chinese Medicine",
                "广西财经学院", "Guangxi University of Finance and Economics",
                "广西科技大学", "Guangxi University of Science and Technology",
                "桂林电子科技大学", "Guilin University of Electronic Technology",
                "桂林理工大学", "Guilin University of Technology",
                "桂林医学院", "Guilin Medical University",
                "南宁师范大学", "Nanning Normal University",

                // --- Guizhou ---
                "贵州医科大学", "Guizhou Medical University",
                "贵州民族大学", "Guizhou Minzu University",
                "贵州师范大学", "Guizhou Normal University",
                "贵州财经大学", "Guizhou University of Finance and Economics",
                "贵州中医药大学", "Guizhou University of Traditional Chinese Medicine",
                "遵义医科大学", "Zunyi Medical University",

                // --- Hainan ---
                "海南师范大学", "Hainan Normal University",
                "海南医科大学", "Hainan Medical University",
                "海南热带海洋学院", "Hainan Tropical Ocean University",

                // --- Hebei ---
                "承德医学院", "Chengde Medical University",
                "河北农业大学", "Hebei Agricultural University",
                "河北地质大学", "Hebei GEO University",
                "河北师范大学", "Hebei Normal University",
                "河北大学", "Hebei University",
                "河北工程大学", "Hebei University of Engineering",
                "河北经贸大学", "Hebei University of Economics and Business",
                "河北科技大学", "Hebei University of Science and Technology",
                "河北中医药大学", "Hebei University of Chinese Medicine",
                "华北理工大学", "North China University of Science and Technology",
                "石家庄铁道大学", "Shijiazhuang Tiedao University",

                // --- Heilongjiang ---
                "哈尔滨师范大学", "Harbin Normal University",
                "哈尔滨体育学院", "Harbin Sport University",
                "哈尔滨学院", "Harbin University",
                "哈尔滨商业大学", "Harbin University of Commerce",
                "哈尔滨理工大学", "Harbin University of Science and Technology",
                "黑河学院", "Heihe University",
                "黑龙江八一农垦大学", "Heilongjiang Bayi Agricultural University",
                "黑龙江工程学院", "Heilongjiang Institute of Technology",
                "黑龙江外国语学院", "Heilongjiang International Studies University",
                "黑龙江大学", "Heilongjiang University",
                "黑龙江工商学院", "Heilongjiang University of Business and Technology",
                "黑龙江中医药大学", "Heilongjiang University of Chinese Medicine",
                "黑龙江财经学院", "Heilongjiang University of Finance and Economics",
                "黑龙江科技大学", "Heilongjiang University of Science and Technology",
                "黑龙江工业学院", "Heilongjiang University of Technology",
                "佳木斯大学", "Jiamusi University",
                "牡丹江医科大学", "Mudanjiang Medical University",
                "牡丹江师范学院", "Mudanjiang Normal University",
                "东北石油大学", "Northeast Petroleum University",
                "齐齐哈尔大学", "Qiqihar University",
                "齐齐哈尔医学院", "Qiqihar Medical University",
                "绥化学院", "Suihua University",

                // --- Hubei ---
                "三峡大学", "China Three Gorges University",
                "长江大学", "Yangtze University",
                "汉江师范学院", "Hanjiang Normal University",
                "湖北工程学院", "Hubei Engineering University",
                "湖北美术学院", "Hubei Institute of Fine Arts",
                "湖北民族大学", "Hubei Minzu University",
                "湖北师范大学", "Hubei Normal University",
                "湖北文理学院", "Hubei University of Arts and Science",
                "湖北汽车工业学院", "Hubei University of Automotive Technology",
                "湖北中医药大学", "Hubei University of Chinese Medicine",
                "湖北经济学院", "Hubei University of Economics",
                "湖北第二师范学院", "Hubei University of Education",
                "湖北医药学院", "Hubei University of Medicine",
                "湖北警官学院", "Hubei University of Police",
                "湖北科技学院", "Hubei University of Science and Technology",
                "湖北工业大学", "Hubei University of Technology",
                "江汉大学", "Jianghan University",
                "荆楚理工学院", "Jingchu University of Technology",
                "武汉纺织大学", "Wuhan Textile University",
                "武汉轻工大学", "Wuhan Polytechnic University",
                "武汉体育学院", "Wuhan Sports University",
                "武汉工程大学", "Wuhan Institute of Technology",
                "武汉商学院", "Wuhan Business University",
                "武汉音乐学院", "Wuhan Conservatory of Music",
                "武汉生物工程学院", "Wuhan University of Bioengineering",
                "中南民族大学", "South-Central Minzu University",
                "黄冈师范学院", "Huanggang Normal University",

                // --- Hunan ---
                "长沙学院", "Changsha University",
                "长沙理工大学", "Changsha University of Science and Technology",
                "衡阳师范学院", "Hengyang Normal University",
                "怀化学院", "Huaihua University",
                "湖南农业大学", "Hunan Agricultural University",
                "湖南城市学院", "Hunan City University",
                "湖南第一师范学院", "Hunan First Normal University",
                "湖南工程学院", "Hunan Institute of Engineering",
                "湖南理工学院", "Hunan Institute of Science and Technology",
                "湖南工学院", "Hunan Institute of Technology",
                "湖南警察学院", "Hunan Police Academy",
                "湖南文理学院", "Hunan University of Arts and Science",
                "湖南中医药大学", "Hunan University of Chinese Medicine",
                "湖南财政经济学院", "Hunan University of Finance and Economics",
                "湖南人文科技学院", "Hunan University of Humanities, Science and Technology",
                "湖南医药学院", "Hunan University of Medicine",
                "湖南科技大学", "Hunan University of Science and Technology",
                "湖南科技学院", "Hunan University of Science and Engineering",
                "湖南工业大学", "Hunan University of Technology",
                "湖南工商大学", "Hunan University of Technology and Business",
                "湖南女子学院", "Hunnan Women's University",
                "吉首大学", "Jishou University",
                "南华大学", "University of South China",
                "湘南学院", "Xiangnan University",
                "湘潭理工学院", "Xiangtan Institute of Technology",
                "邵阳学院", "Shaoyang University",
                "中南林业科技大学", "Central South University of Forestry and Technology",

                // --- Jilin ---
                "白城师范学院", "Baicheng Normal University",
                "北华大学", "Beihua University",
                "长春师范大学", "Changchun Normal University",
                "长春工程学院", "Changchun Institute of Technology",
                "长春大学", "Changchun University",
                "长春中医药大学", "Changchun University of Chinese Medicine",
                "长春财经大学", "Changchun University of Finance and Economics",
                "长春理工大学", "Changchun University of Science and Technology",
                "长春工业大学", "Changchun University of Technology",
                "吉林农业大学", "Jilin Agricultural University",
                "吉林艺术学院", "Jilin University of Arts",
                "吉林化工学院", "Jilin Institute of Chemical Technology",
                "吉林建筑大学", "Jilin Jianzhu University",
                "吉林医药学院", "Jilin Medical University",
                "吉林师范大学", "Jilin Normal University",
                "吉林警察学院", "Jilin Police College",
                "吉林体育学院", "Jilin Sport University",
                "吉林财经大学", "Jilin University of Finance and Economics",
                "东北电力大学", "Northeast Electric Power University",
                "通化师范学院", "Tonghua Normal University",
                "延边大学", "Yanbian University",

                // --- Jiangxi ---
                "东华理工大学", "East China University of Technology",
                "华东交通大学", "East China Jiaotong University",
                "赣南医科大学", "Gannan Medical University",
                "赣南师范大学", "Gannan Normal University",
                "江西农业大学", "Jiangxi Agricultural University",
                "江西服装学院", "Jiangxi Institute of Fashion Technology",
                "江西师范大学", "Jiangxi Normal University",
                "江西警察学院", "Jiangxi Police Institute",
                "江西科技师范大学", "Jiangxi Science and Technology Normal University",
                "江西中医药大学", "Jiangxi University of Chinese Medicine",
                "江西财经大学", "Jiangxi University of Finance and Economics",
                "江西理工大学", "Jiangxi University of Science and Technology",
                "江西科技学院", "Jiangxi University of Technology",
                "景德镇陶瓷大学", "Jingdezhen Ceramic University",
                "井冈山大学", "Jinggangshan University",
                "九江学院", "Jiujiang University",
                "南昌航空大学", "Nanchang Hangkong University",
                "南昌工程学院", "Nanchang Institute of Technology",
                "南昌师范学院", "Nanchang Normal University",
                "萍乡学院", "Pingxiang University",
                "上饶师范学院", "Shangrao Normal University",
                "新余学院", "Xinyu University",
                "宜春学院", "Yichun University",

                // --- Liaoning ---
                "鞍山师范学院", "Anshan Normal University",
                "渤海大学", "Bohai University",
                "大连交通大学", "Dalian Jiaotong University",
                "大连民族大学", "Dalian Minzu University",
                "大连海洋大学", "Dalian Ocean University",
                "大连工业大学", "Dalian Polytechnic University",
                "大连大学", "Dalian University",
                "大连外国语大学", "Dalian University of Foreign Languages",
                "锦州医科大学", "Jinzhou Medical University",
                "辽东学院", "Liaodong University",
                "辽宁传媒学院", "Liaoning Communication University",
                "辽宁工程技术大学", "Liaoning Technical University",
                "辽宁科技大学", "University of Science and Technology Liaoning",
                "辽宁师范大学", "Liaoning Normal University",
                "辽宁石油化工大学", "Liaoning Petrochemical University",
                "辽宁中医药大学", "Liaoning University of Traditional Chinese Medicine",
                "鲁迅美术学院", "Luxun Academy of Fine Arts",
                "沈阳航空航天大学", "Shenyang Aerospace University",
                "沈阳农业大学", "Shenyang Agricultural University",
                "沈阳音乐学院", "Shenyang Conservatory of Music",
                "沈阳工程学院", "Shenyang Institute of Engineering",
                "沈阳建筑大学", "Shenyang Jianzhu University",
                "沈阳理工大学", "Shenyang Ligong University",
                "沈阳医学院", "Shenyang Medical College",
                "沈阳师范大学", "Shenyang Normal University",
                "沈阳药科大学", "Shenyang Pharmaceutical University",
                "沈阳体育学院", "Shenyang Sport University",
                "沈阳大学", "Shenyang University",
                "沈阳化工大学", "Shenyang University of Chemical Technology",
                "沈阳工业大学", "Shenyang University of Technology",
                "营口理工学院", "Yingkou Institute of Technology",

                // --- Inner Mongolia ---
                "包头医学院", "Baotou Medical College",
                "赤峰学院", "Chifeng University",
                "河套学院", "Hetao University",
                "呼和浩特民族学院", "Hohhot Minzu College",
                "呼伦贝尔学院", "Hulunbuir University",
                "内蒙古农业大学", "Inner Mongolia Agricultural University",
                "内蒙古艺术学院", "Inner Mongolia Arts University",
                "内蒙古医科大学", "Inner Mongolia Medical University",
                "内蒙古民族大学", "Inner Mongolia Minzu University",
                "内蒙古师范大学", "Inner Mongolia Normal University",
                "内蒙古科技大学", "Inner Mongolia University of Science and Technology",
                "内蒙古工业大学", "Inner Mongolia University of Technology",
                "内蒙古财经大学", "Inner Mongolia University of Finance and Economics",
                "集宁师范学院", "Jining Normal University",

                // --- Ningxia ---
                "宁夏理工学院", "Ningxia Institute of Science and Technology",
                "宁夏医科大学", "Ningxia Medical University",
                "宁夏师范学院", "Ningxia Normal University",
                "北方民族大学", "North Minzu University",
                "银川能源学院", "Yinchuan University of Energy",

                // --- Qinghai ---
                "青海民族大学", "Qinghai Minzu University",
                "青海师范大学", "Qinghai Normal University",

                // --- Shandong ---
                "滨州医学院", "Binzhou Medical University",
                "德州学院", "Dezhou University",
                "菏泽学院", "Heze University",
                "济宁医学院", "Jining Medical University",
                "济宁学院", "Jining University",
                "聊城大学", "Liaocheng University",
                "临沂大学", "Linyi University",
                "鲁东大学", "Ludong University",
                "齐鲁工业大学", "Qilu University of Technology",
                "齐鲁医药学院", "Qilu Medical University",
                "齐鲁师范学院", "Qilu Normal University",
                "青岛农业大学", "Qingdao Agricultural University",
                "青岛滨海学院", "Qingdao Binhai University",
                "青岛城市学院", "Qingdao City University",
                "青岛电影学院", "Qingdao Film Academy",
                "青岛理工大学", "Qingdao University of Technology",
                "曲阜师范大学", "Qufu Normal University",
                "山东农业大学", "Shandong Agricultural University",
                "山东第一医科大学", "Shandong First Medical University",
                "山东建筑大学", "Shandong Jianzhu University",
                "山东交通学院", "Shandong Jiaotong University",
                "山东管理学院", "Shandong Management University",
                "山东警察学院", "Shandong Police College",
                "山东第二医科大学", "Shandong Second Medical University",
                "山东体育学院", "Shandong Sport University",
                "山东工商学院", "Shandong Technology and Business University",
                "山东艺术学院", "Shandong University of Arts",
                "山东工艺美术学院", "Shandong University of Art and Design",
                "山东财经大学", "Shandong University of Finance and Economics",
                "山东政法学院", "Shandong University of Political Science and Law",
                "山东科技大学", "Shandong University of Science and Technology",
                "山东理工大学", "Shandong University of Technology",
                "山东中医药大学", "Shandong University of Traditional Chinese Medicine",
                "山东女子学院", "Shandong Women's University",
                "泰山学院", "Taishan University",
                "潍坊学院", "Weifang University",
                "潍坊医学院", "Weifang Medical University",
                "烟台大学", "Yantai University",
                "枣庄学院", "Zaozhuang University",

                // --- Shanxi ---
                "长治医学院", "Changzhi Medical College",
                "长治学院", "Changzhi University",
                "山西传媒学院", "Communication University of Shanxi",
                "晋中学院", "Jinzhong University",
                "吕梁学院", "Lvliang University",
                "中北大学", "North University of China",
                "山西农业大学", "Shanxi Agricultural University",
                "山西大同大学", "Shanxi Datong University",
                "山西能源学院", "Shanxi Institute of Energy",
                "山西科技学院", "Shanxi Institute of Science and Technology",
                "山西工学院", "Shanxi Institute of Technology",
                "山西医科大学", "Shanxi Medical University",
                "山西师范大学", "Shanxi Normal University",
                "山西警察学院", "Shanxi Police College",
                "山西工商学院", "Shanxi Technology and Business University",
                "山西中医药大学", "Shanxi University of Chinese Medicine",
                "山西财经大学", "Shanxi University of Finance and Economics",
                "太原工业学院", "Taiyuan Institute of Technology",
                "太原师范学院", "Taiyuan Normal University",
                "太原学院", "Taiyuan University",
                "太原科技大学", "Taiyuan University of Science and Technology",
                "忻州师范学院", "Xinzhou Normal University",
                "运城学院", "Yuncheng University",

                // --- Shaanxi ---
                "安康学院", "Ankang University",
                "宝鸡文理学院", "Baoji University of Arts and Science",
                "西北政法大学", "Northwest University of Political Science and Law",
                "陕西科技大学", "Shaanxi University of Science and Technology",
                "陕西中医药大学", "Shaanxi University of Chinese Medicine",
                "陕西理工大学", "Shaanxi University of Technology",
                "商洛学院", "Shangluo University",
                "渭南师范学院", "Weinan Normal University",
                "西安欧亚学院", "Xi'an Eurasia University",
                "西安文理学院", "Xi'an University",
                "西安美术学院", "Xi'an Academy of Fine Arts",
                "西安音乐学院", "Xi'an Conservatory of Music",
                "西安外国语大学", "Xi'an International Studies University",
                "西安外事学院", "Xi'an International University",
                "西安医学院", "Xi'an Medical University",
                "西安培华学院", "Xi'an Peihua University",
                "西安体育学院", "Xi'an Physical Education University",
                "西安石油大学", "Xi'an Shiyou University",
                "西安工业大学", "Xi'an Technological University",
                "西安建筑科技大学", "Xi'an University of Architecture and Technology",
                "西安财经大学", "Xi'an University of Finance and Economics",
                "西安邮电大学", "Xi'an University of Posts and Telecommunications",
                "西安科技大学", "Xi'an University of Science and Technology",
                "西安理工大学", "Xi'an University of Technology",
                "咸阳师范学院", "Xianyang Normal University",
                "西京学院", "Xijing University",
                "延安大学", "Yan'an University",
                "榆林学院", "Yulin University",

                // --- Shanghai ---
                "上海杉达学院", "Sanda University",
                "上海商学院", "Shanghai Business School",
                "上海音乐学院", "Shanghai Conservatory of Music",
                "上海海关学院", "Shanghai Customs College",
                "上海电机学院", "Shanghai Dianji University",
                "上海应用技术大学", "Shanghai Institute of Technology",
                "上海视觉艺术学院", "Shanghai Institute of Visual Arts",
                "上海建桥学院", "Shanghai Jian Qiao University",
                "上海立达学院", "Shanghai Lida University",
                "上海立信会计金融学院", "Shanghai Lixin University of Accounting and Finance",
                "上海海事大学", "Shanghai Maritime University",
                "上海海洋大学", "Shanghai Ocean University",
                "上海公安学院", "Shanghai Police College",
                "上海第二工业大学", "Shanghai Polytechnic University",
                "上海戏剧学院", "Shanghai Theatre Academy",
                "上海电力大学", "Shanghai University of Electric Power",
                "上海工程技术大学", "Shanghai University of Engineering Science",
                "上海对外经贸大学", "Shanghai University of International Business and Economics",
                "上海健康医学院", "Shanghai University of Medicine and Health Sciences",
                "上海政法学院", "Shanghai University of Political Science and Law",
                "上海体育大学", "Shanghai University of Sport",
                "上海兴伟学院", "Xingwei College",

                // --- Sichuan ---
                "阿坝师范学院", "Aba Teachers University",
                "成都航空职业技术学院", "Chengdu Aeronautic Polytechnic University",
                "成都文理学院", "Chengdu College of Arts and Sciences",
                "成都外国语学院", "Chengdu International Studies University",
                "成都锦城学院", "Chengdu Jincheng College",
                "成都医学院", "Chengdu Medical College",
                "成都东软学院", "Chengdu Neusoft University",
                "成都师范学院", "Chengdu Normal University",
                "成都体育学院", "Chengdu Sport University",
                "成都工业学院", "Chengdu Technological University",
                "成都大学", "Chengdu University",
                "成都信息工程大学", "Chengdu University of Information Technology",
                "成都理工大学", "Chengdu University of Technology",
                "成都中医药大学", "Chengdu University of Traditional Chinese Medicine",
                "西华师范大学", "China West Normal University",
                "中国民用航空飞行学院", "Civil Aviation Flight University of China",
                "吉利学院", "Geely University of China",
                "成都银杏酒店管理学院", "Gingko College of Hospitality Management",
                "乐山师范学院", "Leshan Normal University",
                "绵阳城市学院", "Mianyang City College",
                "绵阳师范学院", "Mianyang Teachers' College",
                "内江师范学院", "Neijiang Normal University",
                "川北医学院", "North Sichuan Medical College",
                "攀枝花学院", "Panzhihua University",
                "四川音乐学院", "Sichuan Conservatory of Music",
                "四川电影电视学院", "Sichuan Film and Television University",
                "四川民族学院", "Sichuan Minzu College",
                "四川警察学院", "Sichuan Police College",
                "四川工商学院", "Sichuan Technology and Business University",
                "四川旅游学院", "Sichuan Tourism University",
                "四川文理学院", "Sichuan University of Arts and Science",
                "四川文化艺术学院", "Sichuan University of Culture and Arts",
                "四川传媒学院", "Sichuan University of Media and Communications",
                "四川轻化工大学", "Sichuan University of Science and Engineering",
                "西南医科大学", "Southwest Medical University",
                "西南民族大学", "Southwest Minzu University",
                "西南石油大学", "Southwest Petroleum University",
                "西南科技大学", "Southwest University of Science and Technology",
                "西昌学院", "Xichang University",
                "西华大学", "Xihua University",
                "宜宾学院", "Yibin University",

                // --- Tianjin ---
                "中国民航大学", "Civil Aviation University of China",
                "天津农学院", "Tianjin Agricultural University",
                "天津城建大学", "Tianjin Chengjian University",
                "天津传媒学院", "Tianjin College of Media and Arts",
                "天津音乐学院", "Tianjin Conservatory of Music",
                "天津外国语大学", "Tianjin Foreign Studies University",
                "天津师范大学", "Tianjin Normal University",
                "天津警察学院", "Tianjin Police College",
                "天津仁爱学院", "Tianjin Renai College",
                "天津中德应用技术大学", "Tianjin Sino-German University of Applied Sciences",
                "天津商业大学", "Tianjin University of Commerce",
                "天津财经大学", "Tianjin University of Finance and Economics",
                "天津科技大学", "Tianjin University of Science and Technology",
                "天津体育学院", "Tianjin University of Sport",
                "天津理工大学", "Tianjin University of Technology",
                "天津职业技术师范大学", "Tianjin University of Technology and Education",
                "天津中医药大学", "Tianjin University of Traditional Chinese Medicine",
                "天狮学院", "Tianshi College",

                // --- Tibet ---
                "西藏藏医药大学", "University of Tibetan Medicine",
                "西藏农牧学院", "Xizang Agricultural and Animal Husbandry University",
                "西藏民族大学", "Xizang Minzu University",

                // --- Xinjiang ---
                "昌吉学院", "Changji University",
                "喀什大学", "Kashi University",
                "塔里木大学", "Tarim University",
                "新疆农业大学", "Xinjiang Agricultural University",
                "新疆艺术学院", "Xinjiang Arts University",
                "新疆科技学院", "Xinjiang College of Science & Technology",
                "新疆工程学院", "Xinjiang Institute of Engineering",
                "新疆理工学院", "Xinjiang Institute of Technology",
                "新疆医科大学", "Xinjiang Medical University",
                "新疆师范大学", "Xinjiang Normal University",
                "新疆警察学院", "Xinjiang Police College",
                "新疆第二医学院", "Xinjiang Second Medical College",
                "新疆天山职业技术大学", "Xinjiang Tianshan Vocational and Technical University",
                "新疆财经大学", "Xinjiang University of Finance and Economics",
                "新疆政法学院", "Xinjiang University of Political Science and Law",
                "伊犁师范大学", "Yili Normal University",

                // --- Yunnan ---
                "保山学院", "Baoshan University",
                "楚雄师范学院", "Chuxiong Normal University",
                "大理大学", "Dali University",
                "滇池学院", "Dianchi College",
                "红河学院", "Honghe University",
                "昆明城市学院", "Kunming City College",
                "昆明医科大学", "Kunming Medical University",
                "昆明学院", "Kunming University",
                "昆明理工大学", "Kunming University of Science and Technology",
                "丽江文化旅游学院", "Lijiang Culture and Tourism College",
                "丽江师范学院", "Lijiang Normal University",
                "普洱学院", "Pu'er University",
                "曲靖师范学院", "Qujing Normal University",
                "西南林业大学", "Southwest Forestry University",
                "文山学院", "Wenshan University",
                "滇西科技师范学院", "West Yunnan University",
                "滇西应用技术大学", "West Yunnan University of Applied Sciences",
                "云南农业大学", "Yunnan Agricultural University",
                "云南艺术学院", "Yunnan Arts University",
                "云南经济管理学院", "Yunnan College of Business Management",
                "云南民族大学", "Yunnan Minzu University",
                "云南师范大学", "Yunnan Normal University",
                "云南警官学院", "Yunnan Police College",
                "云南工商学院", "Yunnan Technology and Business University",
                "云南中医药大学", "Yunnan University of Chinese Medicine",
                "云南财经大学", "Yunnan University of Finance and Economics",
                "玉溪师范学院", "Yuxi Normal University",
                "昭通学院", "Zhaotong University",

                // --- Zhejiang ---
                "中国美术学院", "China Academy of Art",
                "中国计量大学", "China Jiliang University",
                "浙江传媒学院", "Communication University of Zhejiang",
                "宁波东方理工大学", "Eastern Institute of Technology, Ningbo",
                "浙大城市学院", "Hangzhou City University",
                "杭州电子科技大学", "Hangzhou Dianzi University",
                "杭州医学院", "Hangzhou Medical College",
                "杭州师范大学", "Hangzhou Normal University",
                "湖州学院", "Huzhou College",
                "湖州师范学院", "Huzhou University",
                "嘉兴南湖学院", "Jiaxing Nanhu University",
                "嘉兴大学", "Jiaxing University",
                "丽水学院", "Lishui University",
                "宁波财经学院", "Ningbo University of Finance & Economics",
                "宁波工程学院", "Ningbo University of Technology",
                "浙大宁波理工学院", "NingboTech University",
                "衢州学院", "Quzhou University",
                "绍兴文理学院", "Shaoxing University",
                "台州学院", "Taizhou University",
                "温州商学院", "Wenzhou Business College",
                "温州医科大学", "Wenzhou Medical University",
                "温州大学", "Wenzhou University",
                "温州理工学院", "Wenzhou University of Technology",
                "西湖大学", "Westlake University",
                "浙江师范大学行知学院", "Xingzhi College Zhejiang Normal University",
                "浙江农林大学", "Zhejiang A&F University",
                "浙江中医药大学", "Zhejiang Chinese Medical University",
                "浙江音乐学院", "Zhejiang Conservatory of Music",
                "浙江工商大学", "Zhejiang Gongshang University",
                "浙江外国语学院", "Zhejiang International Studies University",
                "浙江海洋大学", "Zhejiang Ocean University",
                "浙江药科职业大学", "Zhejiang Pharmaceutical University",
                "浙江警察学院", "Zhejiang Police College",
                "浙江理工大学", "Zhejiang Sci-Tech University",
                "浙江树人学院", "Zhejiang Shuren University",
                "浙江财经大学", "Zhejiang University of Finance and Economics",
                "浙江科技大学", "Zhejiang University of Science and Technology",
                "浙江水利水电学院", "Zhejiang University of Water Resources and Electric Power",
                "浙江万里学院", "Zhejiang Wanli University",
                "浙江越秀外国语学院", "Zhejiang Yuexiu University",
                "浙江工业大学", "Zhejiang University of Technology",

                // --- Chongqing ---
                "重庆人文科技学院", "Chongqing College of Humanities, Science and Technology",
                "重庆对外经贸学院", "Chongqing College of International Business and Economics",
                "重庆移通学院", "Chongqing College of Mobile Communication",
                "重庆财经学院", "Chongqing Finance and Economics College",
                "重庆工程学院", "Chongqing Institute of Engineering",
                "重庆外语外事学院", "Chongqing Institute of Foreign Studies",
                "重庆交通大学", "Chongqing Jiaotong University",
                "重庆城市科技学院", "Chongqing Metropolitan College of Science and Technology",
                "重庆师范大学", "Chongqing Normal University",
                "重庆警察学院", "Chongqing Police College",
                "重庆电子科技职业大学", "Chongqing Polytechnic University of Electronic Technology",
                "重庆工商大学", "Chongqing Technology and Business University",
                "重庆三峡学院", "Chongqing Three Gorges University",
                "重庆文理学院", "Chongqing University of Arts and Sciences",
                "重庆中医药学院", "Chongqing University of Chinese Medicine",
                "重庆第二师范学院", "Chongqing University of Education",
                "重庆邮电大学", "Chongqing University of Posts and Telecommunications",
                "重庆科技大学", "Chongqing University of Science and Technology",
                "重庆理工大学", "Chongqing University of Technology",
                "四川美术学院", "Sichuan Fine Arts Institute",
                "四川外国语大学", "Sichuan International Studies University",
                "西南政法大学", "Southwest University of Political Science and Law",
                "长江师范学院", "Yangtze Normal University"
        };
        loadBand(band2, BAND_2_UNIVERSITIES);
    }

    private static void loadBand(String[] schools, Set<String> targetSet) {
        for (String school : schools) {
            targetSet.add(school.trim());
        }
    }

    @Override
    public String getSchoolName() {
        return "兰卡斯特大学";
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
                            && s.getEnglishName().equalsIgnoreCase("Lancaster University"))
                    .findFirst()
                    .orElse(null);
            if (school == null) {
                return results;
            }
        }

        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 判断学生院校 Band
        String studentBand = getUniversityBand(undergradSchool);

        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        for (Major major : majors) {
            // 3.1 判断专业要求 (2:1 还是 2:2)
            String ukDegreeReq = determineUKDegreeRequirement(major.getName());

            // 3.2 获取分数线
            double requiredScore = getRequiredScore(studentBand, ukDegreeReq);

            // 3.3 计算匹配
            double matchScore = calculateMatchScore(studentInfo, requiredScore, school, major.getName());

            MatchingResult result = new MatchingResult();
            result.setUserId(studentInfo.getUserId());
            result.setSchoolId(school.getId());
            result.setSchoolName(school.getName());
            result.setMajorName(major.getName());
            result.setMatchScore(matchScore);
            result.setMatchLevel(determineMatchLevel(matchScore));
            result.setMatchReason(generateMatchReason(studentInfo, studentBand, requiredScore, ukDegreeReq));
            result.setAdmissionProbability(calculateAdmissionProbability(matchScore));
            result.setRecommendationIndex(calculateRecommendationIndex(matchScore));
            result.setRegion("英国");
            result.setAlgorithmStrategy("UK_LANCASTER_MATCHING_ALGORITHM");

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
     * 判断学校所属 Band
     * 逻辑：
     * 1. 优先检查 Band 1 (985/211 等核心院校)
     * 2. 其次检查 Band 2 (广泛的认可院校)
     * 3. 如果都不在，归为 Band 3 (其他院校)
     */
    private String getUniversityBand(String schoolName) {
        if (schoolName == null)
            return "Band 3";

        // Check Band 1
        for (String band1School : BAND_1_UNIVERSITIES) {
            if (schoolName.contains(band1School) || band1School.contains(schoolName)) {
                return "Band 1";
            }
        }

        // Check Band 2
        for (String band2School : BAND_2_UNIVERSITIES) {
            if (schoolName.contains(band2School) || band2School.contains(schoolName)) {
                return "Band 2";
            }
        }

        return "Band 3"; // 非名单内院校
    }

    private String determineUKDegreeRequirement(String majorName) {
        if (majorName == null)
            return "2:1";
        // 兰卡斯特大学管理学院 (LUMS) 及热门专业通常要求 2:1
        // 统一高标准 2:1 以确保匹配结果的稳妥性
        return "2:1";
    }

    private double getRequiredScore(String band, String ukDegree) {
        if ("Band 1".equals(band)) {
            // Band 1: 75% (2:1) | 70% (2:2)
            if ("2:1".equals(ukDegree))
                return 75.0;
            else
                return 70.0;
        } else if ("Band 2".equals(band)) {
            // Band 2: 80% (2:1) | 75% (2:2)
            if ("2:1".equals(ukDegree))
                return 80.0;
            else
                return 75.0;
        } else {
            // Band 3 (Rest of world/Non-list): 85% (2:1) | 80% (2:2)
            // 通常兰卡斯特对List外要求较高
            if ("2:1".equals(ukDegree))
                return 85.0;
            else
                return 80.0;
        }
    }

    private double getStudentAverageScore(MatchingRequest.StudentInfoDTO student) {
        if (student.getGpa() == null)
            return 0.0;
        return student.getGpa();
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, School school,
            String majorName) {
        double avgScore = getStudentAverageScore(student);
        if (avgScore == 0.0)
            return 0.0;

        double baseScore = 0.0;

        // 1. 均分评分 (权重高)
        if (avgScore >= required) {
            baseScore += 75.0;
            if (avgScore >= required + 2)
                baseScore += 5;
            if (avgScore >= required + 5)
                baseScore += 5;
        } else if (avgScore >= required - 2) {
            baseScore += 60.0; // Argue 区间
        } else {
            baseScore += 30.0;
        }

        // 2. 院校背景 (Band 1 加分)
        String band = getUniversityBand(student.getUndergraduateSchool());
        if ("Band 1".equals(band)) {
            baseScore += 10.0;
        } else if ("Band 2".equals(band)) {
            baseScore += 5.0;
        }

        // 3. 专业相关
        String target = student.getTargetMajor() != null ? student.getTargetMajor() : "";
        if (majorName.contains(target)) {
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
        sb.append("【兰卡斯特大学】");

        if ("Band 1".equals(band)) {
            sb.append("您的院校属于 Band 1 核心名单（重点院校）。");
        } else if ("Band 2".equals(band)) {
            sb.append("您的院校属于 Band 2 认可名单。");
        } else {
            sb.append("您的院校未在 Lancaster 核心名单中 (Band 3)，分数要求较高。");
        }

        sb.append("该专业要求 UK ").append(ukDegree).append("，对应均分要求 ").append((int) required).append("%。");
        sb.append(" 您的均分为 ").append(String.format("%.1f", avgScore)).append("%。");

        if (avgScore >= required) {
            sb.append(" 成绩达标，录取希望很大。");
        } else if (avgScore >= required - 2) {
            sb.append(" 成绩略有差距，可尝试申请。");
        } else {
            sb.append(" 成绩未达标。");
        }

        return sb.toString();
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.95;
        if (score >= 80)
            return 0.85;
        if (score >= 60)
            return 0.45;
        return 0.10;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
