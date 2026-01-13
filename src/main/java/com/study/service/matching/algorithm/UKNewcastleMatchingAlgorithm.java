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
 * 纽卡斯尔大学 (Newcastle University) 2025 匹配算法
 * 
 * 规则来源：官方入学要求图片 (2025 Entry)
 * 
 * 1. 院校分类：
 * - Tier 1 & 2: 985/211及网大排名前列院校
 * - Tier 3 & 4: 其他院校 (双非/独立学院)
 * 
 * 2. 分数线档位 (基于图片)：
 * 【低分档 - 2:2 Degress Equivalent】
 * - T1&2: 70% | T3&4: 75%
 * - 包含: MSc Computer Science (CS转码), 大部分工程(Engineering),
 * 部分金融(Banking/Finance/Accounting), 创业学(Entrepreneurship), 生物信息等。
 * 
 * 【高分档 - 2:1 Degrees Equivalent】
 * - T1&2: 75% | T3&4: 80%
 * - 包含: 高级CS, 网络安全, 数据科学, 云计算, 人机交互,
 * 商科管理类(Management/Marketing/HR/MBA), 经济学(Economics),
 * 传媒(Media), 教育(Education), 法学(Law), 艺术(Arts), 社会学,
 * 医学MRes, 统计学。
 */
@Service
public class UKNewcastleMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKNewcastleMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Set<String> TIER_1_UNIVERSITIES = new HashSet<>();

    static {
        // --- 985 & 211 & 双一流 (全量) ---
        addTier1("Anhui Agricultural University", "安徽农业大学");
        addTier1("Anhui Medical University", "安徽医科大学");
        addTier1("Anhui Normal University", "安徽师范大学");
        addTier1("Anhui University", "安徽大学");
        addTier1("Anhui University of Chinese Medicine", "安徽中医药大学");
        addTier1("Anhui University of Finance and Economics", "安徽财经大学");
        addTier1("Anhui University of Science & Technology", "安徽理工大学");
        addTier1("Anhui University of Technology", "安徽工业大学");
        addTier1("Beihang University", "北京航空航天大学");
        addTier1("Beijing Foreign Studies University", "北京外国语大学");
        addTier1("Beijing Forestry University", "北京林业大学");
        addTier1("Beijing Information Science & Technology University", "北京信息科技大学");
        addTier1("Beijing Institute of Technology", "北京理工大学");
        addTier1("Beijing International Studies University", "北京第二外国语学院");
        addTier1("Beijing Jiaotong University", "北京交通大学");
        addTier1("Beijing Language and Culture University", "北京语言大学");
        addTier1("Beijing Normal University", "北京师范大学");
        addTier1("Beijing Sport University", "北京体育大学");
        addTier1("Beijing Technology and Business University", "北京工商大学");
        addTier1("Beijing University of Chemical Technology", "北京化工大学");
        addTier1("Beijing University of Chinese Medicine", "北京中医药大学");
        addTier1("Beijing University of Posts and Telecommunications", "北京邮电大学");
        addTier1("Beijing University of Technology", "北京工业大学");
        addTier1("Capital Medical University", "首都医科大学");
        addTier1("Capital Normal University", "首都师范大学");
        addTier1("Capital University of Economics and Business", "首都经济贸易大学");
        addTier1("Central China Normal University", "华中师范大学");
        addTier1("Central Conservatory of Music", "中央音乐学院");
        addTier1("Central South University", "中南大学");
        addTier1("Central South University of Forestry and Technology", "中南林业科技大学");
        addTier1("Central University of Finance and Economics", "中央财经大学");
        addTier1("Chang'an University", "长安大学");
        addTier1("Changchun University of Chinese Medicine", "长春中医药大学");
        addTier1("Changchun University of Science and Technology", "长春理工大学");
        addTier1("Changsha University of Science and Technology", "长沙理工大学");
        addTier1("ChangZhou University", "常州大学");
        addTier1("Chengdu University of Technology", "成都理工大学");
        addTier1("Chengdu University of Traditional Chinese Medicine", "成都中医药大学");
        addTier1("China Agricultural University", "中国农业大学");
        addTier1("China Coast Guard Academy", "中国人民武装警察部队海警学院");
        addTier1("China Fire and Rescue Institute", "中国消防救援学院");
        addTier1("China Foreign Affairs University", "外交学院");
        addTier1("China Jiliang University", "中国计量大学");
        addTier1("China Medical University", "中国医科大学");
        addTier1("China People's Police University", "中国人民警察大学");
        addTier1("China Pharmaceutical University", "中国药科大学");
        addTier1("China Three Gorges University", "三峡大学");
        addTier1("China University of Geosciences", "中国地质大学");
        addTier1("China University of Mining and Technology", "中国矿业大学");
        addTier1("China University of Petroleum", "中国石油大学");
        addTier1("China University of Political Science and Law", "中国政法大学");
        addTier1("China Youth University of Political Studies", "中国青年政治学院");
        addTier1("Chongqing Jiaotong University", "重庆交通大学");
        addTier1("Chongqing Medical University", "重庆医科大学");
        addTier1("Chongqing Normal University", "重庆师范大学");
        addTier1("Chongqing Technology and Business University", "重庆工商大学");
        addTier1("Chongqing University", "重庆大学");
        addTier1("Chongqing University of Posts and Telecommunications", "重庆邮电大学");
        addTier1("Chongqing University of Technology", "重庆理工大学");
        addTier1("City University of Hong Kong (Dongguan)", "香港城市大学（东莞）");
        addTier1("Civil Aviation University of China", "中国民航大学");
        addTier1("Communication University of China", "中国传媒大学");
        addTier1("Dalian Maritime University", "大连海事大学");
        addTier1("Dalian Medical University", "大连医科大学");
        addTier1("Dalian Polytechnic University", "大连工业大学");
        addTier1("Dalian University of Foreign Languages", "大连外国语大学");
        addTier1("Dalian University of Technology", "大连理工大学");
        addTier1("Dongbei University of Finance and Economics", "东北财经大学");
        addTier1("Donghua University", "东华大学");
        addTier1("Duke Kunshan University", "昆山杜克大学");
        addTier1("East China Jiaotong University", "华东交通大学");
        addTier1("East China Normal University", "华东师范大学");
        addTier1("East China University of Political Science and Law", "华东政法大学");
        addTier1("East China University of Science and Technology", "华东理工大学");
        addTier1("East China University of Technology", "东华理工大学");
        addTier1("Fudan University", "复旦大学");
        addTier1("Fujian Agriculture and Forestry University", "福建农林大学");
        addTier1("Fujian Normal University", "福建师范大学");
        addTier1("Fujiang Medical University", "福建医科大学");
        addTier1("Fuzhou University", "福州大学");
        addTier1("Guangdong Technion Israel Institute of Technology", "广东以色列理工学院");
        addTier1("Guangdong University of Finance and Economics", "广东财经大学");
        addTier1("Guangdong University of Foreign Studies", "广东外语外贸大学");
        addTier1("Guangdong University of Technology", "广东工业大学");
        addTier1("Guangxi Medical University", "广西医科大学");
        addTier1("Guangxi Normal University", "广西师范大学");
        addTier1("Guangxi University", "广西大学");
        addTier1("Guangzhou Medical University", "广州医科大学");
        addTier1("Guangzhou University", "广州大学");
        addTier1("Guangzhou University of Chinese Medicine", "广州中医药大学");
        addTier1("Guilin University of Electronic Technology", "桂林电子科技大学");
        addTier1("Guizhou University", "贵州大学");
        addTier1("Hainan Bielefeld University of Applied Sciences", "海南比勒费尔德应用科学大学");
        addTier1("Hainan Normal University", "海南师范大学");
        addTier1("Hainan University", "海南大学");
        addTier1("Hangzhou Dianzi University", "杭州电子科技大学");
        addTier1("Hangzhou Normal University", "杭州师范大学");
        addTier1("Harbin Engineering University", "哈尔滨工程大学");
        addTier1("Harbin Institute of Technology", "哈尔滨工业大学");
        addTier1("Harbin Medical University", "哈尔滨医科大学");
        addTier1("Harbin University of Science and Technology", "哈尔滨理工大学");
        addTier1("Hebei Agricultural University", "河北农业大学");
        addTier1("Hebei Medical University", "河北医科大学");
        addTier1("Hebei Normal University", "河北师范大学");
        addTier1("Hebei University", "河北大学");
        addTier1("Hebei University of Science and Technology", "河北科技大学");
        addTier1("Hebei University of Technology", "河北工业大学");
        addTier1("Hefei University of Technology", "合肥工业大学");
        addTier1("Heilongjiang University", "黑龙江大学");
        addTier1("Heilongjiang University of Chinese Medicine", "黑龙江中医药大学");
        addTier1("Henan Agricultural University", "河南农业大学");
        addTier1("Henan Medical University", "河南医科大学");
        addTier1("Henan Normal University", "河南师范大学");
        addTier1("Henan Polytechnic University", "河南理工大学");
        addTier1("Henan University", "河南大学");
        addTier1("Henan University of Chinese Medicine", "河南中医药大学");
        addTier1("Henan University of Economics and Law", "河南财经政法大学");
        addTier1("Henan University of Science and Technology", "河南科技大学");
        addTier1("Henan University of Technology", "河南工业大学");
        addTier1("Hohai University", "河海大学");
        addTier1("Huaqiao University", "华侨大学");
        addTier1("Huazhong Agricultural University", "华中农业大学");
        addTier1("Huazhong University of Science and Technology", "华中科技大学");
        addTier1("Hubei University", "湖北大学");
        addTier1("Hubei University of Technology", "湖北工业大学");
        addTier1("Hunan Agricultural University", "湖南农业大学");
        addTier1("Hunan Normal University", "湖南师范大学");
        addTier1("Hunan University", "湖南大学");
        addTier1("Hunan University of Chinese Medicine", "湖南中医药大学");
        addTier1("Hunan University of Science and Technology", "湖南科技大学");
        addTier1("Inner Mongolia University", "内蒙古大学");
        addTier1("Jiangnan University", "江南大学");
        addTier1("Jiangsu Normal University", "江苏师范大学");
        addTier1("Jiangsu University", "江苏大学");
        addTier1("Jiangsu University of Science and Technology", "江苏科技大学");
        addTier1("Jiangxi Agricultural University", "江西农业大学");
        addTier1("Jiangxi Normal University", "江西师范大学");
        addTier1("Jiangxi University of Finance and Economics", "江西财经大学");
        addTier1("Jiangxi University of Science and Technology", "江西理工大学");
        addTier1("Jilin Agricultural University", "吉林农业大学");
        addTier1("Jilin University", "吉林大学");
        addTier1("Jimei University", "集美大学");
        addTier1("Jinan University", "暨南大学");
        addTier1("Kunming University of Science and Technology", "昆明理工大学");
        addTier1("Lanzhou University", "兰州大学");
        addTier1("Lanzhou University of Technology", "兰州理工大学");
        addTier1("Liaoning Normal University", "辽宁师范大学");
        addTier1("Liaoning University", "辽宁大学");
        addTier1("Liaoning University of Traditional Chinese Medicine", "辽宁中医药大学");
        addTier1("Minzu University of China", "中央民族大学");
        addTier1("Nanchang Hangkong University", "南昌航空大学");
        addTier1("Nanchang University", "南昌大学");
        addTier1("Nanjing Agricultural University", "南京农业大学");
        addTier1("Nanjing Audit University", "南京审计大学");
        addTier1("Nanjing Forestry University", "南京林业大学");
        addTier1("Nanjing Medical University", "南京医科大学");
        addTier1("Nanjing Normal University", "南京师范大学");
        addTier1("Nanjing Tech University", "南京工业大学");
        addTier1("Nanjing University", "南京大学");
        addTier1("Nanjing University of Aeronautics and Astronautics", "南京航空航天大学");
        addTier1("Nanjing University of Chinese Medicine", "南京中医药大学");
        addTier1("Nanjing University of Finance & Economics", "南京财经大学");
        addTier1("Nanjing University of Information Science & Technology", "南京信息工程大学");
        addTier1("Nanjing University of Posts and Telecommunications", "南京邮电大学");
        addTier1("Nanjing University of Science and Technology", "南京理工大学");
        addTier1("Nankai University", "南开大学");
        addTier1("Nantong University", "南通大学");
        addTier1("National Defence University PLA China", "中国人民解放军国防大学");
        addTier1("National University of Defense Technology", "国防科技大学");
        addTier1("Ningbo University", "宁波大学");
        addTier1("Ningxia Mediacal University", "宁夏医科大学");
        addTier1("Ningxia University", "宁夏大学");
        addTier1("North China Electric Power University", "华北电力大学");
        addTier1("North China University of Technology", "北方工业大学");
        addTier1("North University of China", "中北大学");
        addTier1("Northeast Agricultural University", "东北农业大学");
        addTier1("Northeast Electric Power University", "东北电力大学");
        addTier1("Northeast Forestry University", "东北林业大学");
        addTier1("Northeast Normal University", "东北师范大学");
        addTier1("Northeast Petroleum University", "东北石油大学");
        addTier1("Northeastern University", "东北大学");
        addTier1("Northwest A&F University", "西北农林科技大学");
        addTier1("Northwest Normal University", "西北师范大学");
        addTier1("Northwest University", "西北大学");
        addTier1("Northwest University of Political Science and Law", "西北政法大学");
        addTier1("Northwestern Polytechnical University", "西北工业大学");
        addTier1("NYU Shanghai", "上海纽约大学");
        addTier1("Ocean University of China", "中国海洋大学");
        addTier1("Peking Union Medical College", "北京协和医学院");
        addTier1("Peking University", "北京大学");
        addTier1("People's Public Security University of China", "中国人民公安大学");
        addTier1("Qingdao University", "青岛大学");
        addTier1("Qingdao University of Science & Technology", "青岛科技大学");
        addTier1("Qingdao University of Technology", "青岛理工大学");
        addTier1("Qinghai University", "青海大学");
        addTier1("Qufu Normal University", "曲阜师范大学");
        addTier1("Renmin University of China", "中国人民大学");
        addTier1("Shaanxi Normal University", "陕西师范大学");
        addTier1("Shaanxi University of Science and Technology", "陕西科技大学");
        addTier1("Shandong Agricultural University", "山东农业大学");
        addTier1("Shandong First Medical University", "山东第一医科大学");
        addTier1("Shandong Normal University", "山东师范大学");
        addTier1("Shandong University", "山东大学");
        addTier1("Shandong University of Finance & Economics", "山东财经大学");
        addTier1("Shandong University of Science and Technology", "山东科技大学");
        addTier1("Shandong University of Technology", "山东理工大学");
        addTier1("Shandong University of Traditional Chinese Medicine", "山东中医药大学");
        addTier1("Shanghai International Studies University", "上海外国语大学");
        addTier1("Shanghai Jiao Tong University", "上海交通大学");
        addTier1("Shanghai Maritime University", "上海海事大学");
        addTier1("Shanghai Normal University", "上海师范大学");
        addTier1("Shanghai Ocean University", "上海海洋大学");
        addTier1("Shanghai University", "上海大学");
        addTier1("Shanghai University of Electric Power", "上海电力大学");
        addTier1("Shanghai University of Finance and Economics", "上海财经大学");
        addTier1("Shanghai University of International Business and Economics", "上海对外经贸大学");
        addTier1("Shanghai University of Sport", "上海体育学院");
        addTier1("Shanghai University of Traditional Chinese Medicine", "上海中医药大学");
        addTier1("ShanghaiTech University", "上海科技大学");
        addTier1("Shantou University", "汕头大学");
        addTier1("Shanxi Medical University", "山西医科大学");
        addTier1("Shanxi University", "山西大学");
        addTier1("Shanxi University of Finance & Economics", "山西财经大学");
        addTier1("Shenyang Aerospace University", "沈阳航空航天大学");
        addTier1("Shenyang Agricultural University", "沈阳农业大学");
        addTier1("Shenyang Jianzhu University", "沈阳建筑大学");
        addTier1("Shenyang Pharmaceutical University", "沈阳药科大学");
        addTier1("Shenyang University of Technology", "沈阳工业大学");
        addTier1("Shenzhen MSU-BIT University", "深圳北理莫斯科大学");
        addTier1("Shenzhen University", "深圳大学");
        addTier1("Shihezi University", "石河子大学");
        addTier1("Shijiazhuang Tiedao University", "石家庄铁道大学");
        addTier1("Sichuan Agricultural University", "四川农业大学");
        addTier1("Sichuan International Studies University", "四川外国语大学");
        addTier1("Sichuan Normal University", "四川师范大学");
        addTier1("Sichuan University", "四川大学");
        addTier1("Soochow University", "苏州大学");
        addTier1("South China Agricultural University", "华南农业大学");
        addTier1("South China Normal University", "华南师范大学");
        addTier1("South China University of Technology", "华南理工大学");
        addTier1("South-Central Minzu University", "中南民族大学");
        addTier1("Southeast University", "东南大学");
        addTier1("Southern Medical University", "南方医科大学");
        addTier1("Southern University of Science and Technology", "南方科技大学");
        addTier1("Southwest Jiaotong University", "西南交通大学");
        addTier1("Southwest Medical University", "西南医科大学");
        addTier1("Southwest Minzu University", "西南民族大学");
        addTier1("Southwest Petroleum University", "西南石油大学");
        addTier1("Southwest University", "西南大学");
        addTier1("Southwest University of Political Science and Law", "西南政法大学");
        addTier1("Southwest University of Science and Technology", "西南科技大学");
        addTier1("Southwestern University of Finance and Economics", "西南财经大学");
        addTier1("Sun Yat-Sen University", "中山大学");
        addTier1("Suzhou University of Science and Technology", "苏州科技大学");
        addTier1("Taiyuan University of Technology", "太原理工大学");
        addTier1("The Army Infantry Academy of PLA", "中国人民解放军陆军步兵学院");
        addTier1("The Chinese University of Hong Kong, Shenzhen", "香港中文大学（深圳）");
        addTier1("The Fourth Military Medical University", "中国人民解放军空军军医大学");
        addTier1("The Hong Kong University of Science and Technology (Guangzhou)", "香港科技大学（广州）");
        addTier1("The PAP Command College", "中国人民武警警察部队指挥学院");
        addTier1("The PAP Engineering University", "中国人民武警警察部队工程大学");
        addTier1("The PAP Logistics College", "中国人民武警警察部队后勤学院");
        addTier1("The PAP Officers College", "中国人民武警警察部队士官学校");
        addTier1("The PAP Police Academy", "中国人民武警警察部队警官学院");
        addTier1("The PAP Special Police College", "中国人民武警警察部队特种警察学院");
        addTier1("The PLA Academy of Military Transportation", "中国人民解放军陆军军事交通学院");
        addTier1("The PLA Aerospace Engineering University", "中国人民解放军战略支援部队航天工程大学");
        addTier1("The PLA Air Force Aviation University", "中国人民解放军空军航空大学");
        addTier1("The PLA Air Force Command College", "中国人民解放军空军指挥学院");
        addTier1("The PLA Air Force Communications Officers College", "中国人民解放军空军通信士官学校");
        addTier1("The PLA Air Force Early Warning Academy", "中国人民解放军空军预警学院");
        addTier1("The PLA Air Force Engineering University", "中国人民解放军空军工程大学");
        addTier1("The PLA Air Force Flight Academy Harbin", "中国人民解放军哈尔滨飞行学院");
        addTier1("The PLA Air Force Flight Academy Shijiazhuang", "中国人民解放军石家庄飞行学院");
        addTier1("The PLA Air Force Flight Academy Xi'an", "中国人民解放军西安飞行学院");
        addTier1("The PLA Air Force Logistics University", "中国人民解放军空军勤务学院");
        addTier1("The PLA Army Academy of Boarder and Coastal Defence", "中国人民解放军陆军边海防学院");
        addTier1("The PLA Army Armored Forces Academy", "中国人民解放军陆军装甲兵学院");
        addTier1("The PLA Army Artillery Air Defense Academy", "中国人民解放军陆军炮兵防空兵学院");
        addTier1("The PLA Army Aviation College", "中国人民解放军陆军航空兵学院");
        addTier1("The PLA Army Command College", "中国人民解放军陆军指挥学院");
        addTier1("The PLA Army Engineering University", "中国人民解放军陆军工程大学");
        addTier1("The PLA Army Logistics University", "中国人民解放军陆军勤务学院");
        addTier1("The PLA Army Medical University", "中国人民解放军陆军军医大学");
        addTier1("The PLA Army Special Operations College", "中国人民解放军陆军特种作战学院");
        addTier1("The PLA Dalian Naval Academy", "中国人民解放军海军大连舰艇学院");
        addTier1("The PLA Information Engineering University", "中国人民解放军战略支援部队信息工程大学");
        addTier1("The PLA Insititute of NBC Defence", "中国人民解放军陆军防化学院");
        addTier1("The PLA Naval Aeronautical University", "中国人民解放军海军航空大学");
        addTier1("The PLA Naval University of Engineering", "中国人民解放军海军工程大学");
        addTier1("The PLA Navy Command College", "中国人民解放军海军指挥学院");
        addTier1("The PLA Navy Logistics University", "中国人民解放军海军勤务学院");
        addTier1("The PLA Navy Officer School", "中国人民解放军海军士官学校");
        addTier1("The PLA Navy Submarine Academy", "中国人民解放军海军潜艇学院");
        addTier1("The PLA Rocket Force Command College", "中国人民解放军火箭军指挥学院");
        addTier1("The PLA Rocket Force Officers College", "中国人民解放军火箭军士官学校");
        addTier1("The PLA Rocket Force University of Engineering", "中国人民解放军火箭军工程大学");
        addTier1("The Second Military Medical University", "中国人民解放军海军军医大学");
        addTier1("Tiangong University", "天津工业大学");
        addTier1("Tianjin Medical University", "天津医科大学");
        addTier1("Tianjin Normal University", "天津师范大学");
        addTier1("Tianjin University", "天津大学");
        addTier1("Tianjin University of Finance and Economics", "天津财经大学");
        addTier1("Tianjin University of Science & Technology", "天津科技大学");
        addTier1("Tianjin University of Technology", "天津理工大学");
        addTier1("Tianjin University of Traditional Chinese Medicine", "天津中医药大学");
        addTier1("Tibet University", "西藏大学");
        addTier1("Tongji University", "同济大学");
        addTier1("Tsinghua University", "清华大学");
        addTier1("University of Chinese Academy of Sciences", "中国科学院大学");
        addTier1("University of Chinese Academy of Social Sciences", "中国社会科学院大学");
        addTier1("University of Electronic Science and Technology of China", "电子科技大学");
        addTier1("University of Health and Rehabilitation Sciences", "康复大学");
        addTier1("University of International Business and Economics", "对外经济贸易大学");
        addTier1("University of International Relations", "国际关系学院");
        addTier1("University of Jinan", "济南大学");
        addTier1("University of Nottingham Ningbo China", "宁波诺丁汉大学");
        addTier1("University of Science and Technology Beijing", "北京科技大学");
        addTier1("University of Science and Technology of China", "中国科学技术大学");
        addTier1("University of Shanghai for Science and Technology", "上海理工大学");
        addTier1("University of South China", "南华大学");
        addTier1("Wenzhou Medical University", "温州医科大学");
        addTier1("Wenzhou University", "温州大学");
        addTier1("Wenzhou-Kean University", "温州肯恩大学");
        addTier1("Westlake University", "西湖大学");
        addTier1("Wuhan Institute of Technology", "武汉工程大学");
        addTier1("Wuhan Textile University", "武汉纺织大学");
        addTier1("Wuhan University", "武汉大学");
        addTier1("Wuhan University of Science and Technology", "武汉科技大学");
        addTier1("Wuhan University of Technology", "武汉理工大学");
        addTier1("Xiamen University", "厦门大学");
        addTier1("Xi'an International Studies University", "西安外国语大学");
        addTier1("Xi'an Jiaotong University", "西安交通大学");
        addTier1("Xi'an Jiaotong-Liverpool University", "西交利物浦大学");
        addTier1("Xi'an Shiyou University", "西安石油大学");
        addTier1("Xi'an Technological University", "西安工业大学");
        addTier1("Xi'an Univerisity of Post & Telecommunications", "西安邮电大学");
        addTier1("Xi'an University of Architecture and Technology", "西安建筑科技大学");
        addTier1("Xi'an University of Science and Technology", "西安科技大学");
        addTier1("Xi'an University of Technology", "西安理工大学");
        addTier1("Xiangtan University", "湘潭大学");
        addTier1("Xidian University", "西安电子科技大学");
        addTier1("Xinjiang University", "新疆大学");
        addTier1("Xuzhou Medical University", "徐州医科大学");
        addTier1("Yanbian University", "延边大学");
        addTier1("Yangtze University", "长江大学");
        addTier1("Yangzhou University", "扬州大学");
        addTier1("Yanshan University", "燕山大学");
        addTier1("Yunnan Normal University", "云南师范大学");
        addTier1("Yunnan University", "云南大学");
        addTier1("Zhejiang A&F University", "浙江农林大学");
        addTier1("Zhejiang Chinese Medical University", "浙江中医药大学");
        addTier1("Zhejiang Gongshang University", "浙江工商大学");
        addTier1("Zhejiang Normal University", "浙江师范大学");
        addTier1("Zhejiang Ocean University", "浙江海洋大学");
        addTier1("Zhejiang Sci-Tech University", "浙江理工大学");
        addTier1("Zhejiang University", "浙江大学");
        addTier1("Zhejiang University of Finance & Economics", "浙江财经大学");
        addTier1("Zhejiang University of Science and Technology", "浙江科技大学");
        addTier1("Zhejiang University of Technology", "浙江工业大学");
        addTier1("Zhengzhou University", "郑州大学");
        addTier1("Zhongnan University of Economics and Law", "中南财经政法大学");
    }

    // =================================================================================
    // Tier 2: 普通公办本科 (Ordinary Public Universities)
    // =================================================================================
    private static final Set<String> TIER_2_UNIVERSITIES = new HashSet<>();
    static {
        // 包括所有未在 Tier 1 列出但属于公办本科的学校
        // 此处列举常见 Tier 2 学校
        addTier2("Aba Teachers University", "阿坝师范学院");
        addTier2("Anhui Jianzhu University", "安徽建筑大学");
        addTier2("Anhui Polytechnic University", "安徽工程大学");
        addTier2("Anhui Science and Technology University", "安徽科技学院");
        addTier2("Ankang University", "安康学院");
        addTier2("Anqing Normal University", "安庆师范大学");
        addTier2("Anshan Normal University Liaoning China", "鞍山师范学院");
        addTier2("Anshun University", "安顺学院");
        addTier2("Anyang Institute of Technology", "安阳工学院");
        addTier2("Anyang Normal University", "安阳师范学院");
        addTier2("Baicheng Normal University", "白城师范学院");
        addTier2("Baise University", "百色学院");
        addTier2("Baoding University", "保定学院");
        addTier2("Baoji University of Arts and Sciences", "宝鸡文理学院");
        addTier2("Baoshan University", "保山学院");
        addTier2("Beibu Gulf University", "北部湾大学");
        addTier2("Beihua University", "北华大学");
        addTier2("Beijing City University", "北京城市学院");
        addTier2("Beijing Dance Academy", "北京舞蹈学院");
        addTier2("Beijing Electronic Science and Technology Institute", "北京电子科技学院");
        addTier2("Beijing Film Academy", "北京电影学院");
        addTier2("Beijing Institute of Fashion Technology", "北京服装学院");
        addTier2("Beijing Institute of Graphic Communication", "北京印刷学院");
        addTier2("Beijing Institute of Petrochemical Technology", "北京石油化工学院");
        addTier2("Beijing Police College", "北京警察学院");
        addTier2("Beijing Union University", "北京联合大学");
        addTier2("Beijing University of Agriculture", "北京农学院");
        addTier2("Beijing Wuzi University", "北京物资学院");
        addTier2("Bengbu Medical University", "蚌埠医学院");
        addTier2("Bengbu University", "蚌埠学院");
        addTier2("Binzhou Medical University", "滨州医学院");
        addTier2("Bohai University", "渤海大学");
        addTier2("Bozhou University", "亳州学院");
        addTier2("Cangzhou Normal University", "沧州师范学院");
        addTier2("CAPITAL UNIVERSITY OF PHYSICAL EDUCATION AND SPORTS", "首都体育学院");
        addTier2("Central Academy of Fine Arts", "中央美术学院");
        addTier2("Changchun Institute of Technology", "长春工程学院");
        addTier2("Changchun Normal University", "长春师范大学");
        addTier2("Changchun University", "长春大学");
        addTier2("Changchun University of Technology", "长春工业大学");
        addTier2("Changji University", "昌吉学院");
        addTier2("Changsha Normal University", "长沙师范学院");
        addTier2("Changsha University", "长沙学院");
        addTier2("Changzhi Medical College", "长治医学院");
        addTier2("Changzhi University", "长治学院");
        addTier2("Changzhou Institute of Technology", "常州工学院");
        addTier2("Chaohu University", "巢湖学院");
        addTier2("Chaoyang Normal University", "朝阳师范学院");
        addTier2("Chengde Medical University", "承德医学院");
        addTier2("Chengdu Medical College", "成都医学院");
        addTier2("Chengdu Normal University", "成都师范学院");
        addTier2("Chengdu Sport University", "成都体育学院");
        addTier2("Chengdu University", "成都大学");
        addTier2("Chengdu University of Information Technology", "成都信息工程大学");
        addTier2("Chifeng University", "赤峰学院");
        addTier2("China Academy of Art", "中国美术学院");
        addTier2("China University of Labor Relations", "中国劳动关系学院");
        addTier2("China West Normal University", "西华师范大学");
        addTier2("China Women's University", "中华女子学院");
        addTier2("Chizhou University", "池州学院");
        addTier2("Chongqing Police College", "重庆警察学院");
        addTier2("Chongqing University of Arts and Sciences", "重庆文理学院");
        addTier2("Chongqing University of Education", "重庆第二师范学院");
        addTier2("Chongqing University of Science and Technology", "重庆科技大学");
        addTier2("Chuxiong Normal University", "楚雄师范学院");
        addTier2("Chuzhou University", "滁州学院");
        addTier2("Civil Aviation Flight University of China", "中国民用航空飞行学院");
        addTier2("Communication University of China, Nanjing", "南京传媒学院");
        addTier2("Communication University of Zhejiang", "浙江传媒学院");
        addTier2("Dali University", "大理大学");
        addTier2("Dalian Jiaotong University", "大连交通大学");
        addTier2("Dalian Minzu University", "大连民族大学");
        addTier2("Dalian Ocean University", "大连海洋大学");
        addTier2("Dalian University", "大连大学");
        addTier2("Daqing Normal University", "大庆师范学院");
        addTier2("Dezhou University", "德州学院");
        addTier2("Dongguan University of Technology", "东莞理工学院");
        addTier2("Foshan University", "佛山大学");
        addTier2("Fujian Business Universiy", "福建商学院");
        addTier2("Fujian Jiangxia University", "福建江夏学院");
        addTier2("Fujian Police College", "福建警察学院");
        addTier2("Fujian Polytechnic Normal University", "福建技术师范学院");
        addTier2("Fujian University of Technology", "福建理工大学");
        addTier2("Fujian University of Traditional Chinese Medicine", "福建中医药大学");
        addTier2("Fuyang Normal University", "阜阳师范大学");
        addTier2("Gandong University", "赣东学院");
        addTier2("Gannan Medical University", "赣南医科大学");
        addTier2("Gannan Normal University", "赣南师范大学");
        addTier2("Gannan University of Science and Technology", "赣南科技学院");
        addTier2("Gansu Agricultural University", "甘肃农业大学");
        addTier2("Gansu Medical College", "甘肃医学院");
        addTier2("Gansu Normal College of Nationalities", "甘肃民族师范学院");
        addTier2("Gansu University of Chinese Medicine", "甘肃中医药大学");
        addTier2("Gansu University of Political Science and Law", "甘肃政法大学");
        addTier2("Guangdong Medical University", "广东医科大学");
        addTier2("Guangdong Ocean University", "广东海洋大学");
        addTier2("Guangdong Pharmaceutical University", "广东药科大学");
        addTier2("Guangdong Police College", "广东警官学院");
        addTier2("Guangdong Polytecnic Normal University", "广东技术师范大学");
        addTier2("Guangdong University Of Education", "广东第二师范学院");
        addTier2("Guangdong University of Finance", "广东金融学院");
        addTier2("Guangdong University of Petrochemical Technology", "广东石油化工学院");
        addTier2("Guangxi Arts University", "广西艺术学院");
        addTier2("Guangxi Cadres University of Economics and Management", "广西职业师范学院");
        addTier2("Guangxi Minzu Normal University", "广西民族师范学院");
        addTier2("Guangxi Minzu University", "广西民族大学");
        addTier2("Guangxi Police College", "广西警察学院");
        addTier2("Guangxi Science & Technology Normal University", "广西科师范学院");
        addTier2("Guangxi University of Chinese Medicine", "广西中医药大学");
        addTier2("Guangxi University of Finance and Economics", "广西财经学院");
        addTier2("Guangzhou Academy of Fine Arts", "广州美术学院");
        addTier2("Guangzhou Maritime University", "广州航海学院");
        addTier2("Guangzhou Sport University", "广州体育学院");
        addTier2("Guilin Medical University", "桂林医学院");
        addTier2("Guilin Tourism University", "桂林旅游学院");
        addTier2("Guilin University Of Aerospace Technology", "桂林航天工业学院");
        addTier2("Guilin University of Technology", "桂林理工大学");
        addTier2("Guiyang University", "贵阳学院");
        addTier2("Guizhou Education University", "贵州师范学院");
        addTier2("Guizhou Institute of Technology", "贵州理工学院");
        addTier2("Guizhou Medical University", "贵州医科大学");
        addTier2("Guizhou Minzu University", "贵州民族大学");
        addTier2("Guizhou Normal University", "贵州师范大学");
        addTier2("Guizhou Police College", "贵州警察学院");
        addTier2("Guizhou University of Commerce", "贵州商学院");
        addTier2("Guizhou University of Engineering Science", "贵州工程应用技术学院");
        addTier2("Guizhou University of Finance and Economics", "贵州财经大学");
        addTier2("Guizhou University of Traditional Chinese Medicine", "贵州中医药大学");
        addTier2("Hainan Medical University", "海南医科大学");
        addTier2("Hainan Tropical Ocean University", "海南热带海洋学院");
        addTier2("Handan University", "邯郸学院");
        addTier2("Hangzhou City University", "浙大城市学院");
        addTier2("Hangzhou Medical College", "杭州医学院");
        addTier2("Hanjiang Normal University", "汉江师范学院");
        addTier2("Hanshan Normal University", "韩山师范学院");
        addTier2("Harbin Conservatory of Music", "哈尔滨音乐学院");
        addTier2("Harbin Finance University", "哈尔滨金融学院");
        addTier2("Harbin Normal University", "哈尔滨师范大学");
        addTier2("Harbin Sport University", "哈尔滨体育学院");
        addTier2("Harbin University", "哈尔滨学院");
        addTier2("Harbin University of Commerce", "哈尔滨商业大学");
        addTier2("Hebei Finance University", "河北金融学院");
        addTier2("Hebei GEO University", "河北地质大学");
        addTier2("Hebei Minzu Normal University", "河北民族师范学院");
        addTier2("Hebei Normal University of Science & Technology", "河北科技师范学院");
        addTier2("Hebei North University", "河北北方学院");
        addTier2("Hebei Sport University", "河北体育学院");
        addTier2("Hebei University of Architecture", "河北建筑工程学院");
        addTier2("Hebei University of Chinese Medicine", "河北中医药大学");
        addTier2("Hebei University of Economics and Business", "河北经贸大学");
        addTier2("Hebei University of Engineering", "河北工程大学");
        addTier2("Hebei University of Environmental Engineering", "河北环境工程学院");
        addTier2("Hebei University of Water Resources and Electric Engineering", "河北水利电力学院");
        addTier2("Hechi University", "河池学院");
        addTier2("Hefei Institute of Technology", "合肥理工学院");
        addTier2("Hefei Normal University", "合肥师范学院");
        addTier2("Hefei University", "合肥大学");
        addTier2("Hei He University", "黑河学院");
        addTier2("Heilongjiang Bayi Agricultural University", "黑龙江八一农垦大学");
        addTier2("Heilongjiang Institute of Technology", "黑龙江工程学院");
        addTier2("Heilongjiang University of Science and Technology", "黑龙江科技大学");
        addTier2("Heilongjiang University Of Technology", "黑龙江工业学院");
        addTier2("Henan Finance University", "河南财政金融学院");
        addTier2("Henan Institute of Science and Technology", "河南科技学院");
        addTier2("Henan Institute of Technology", "河南工学院");
        addTier2("Henan Police College", "河南警察学院");
        addTier2("Henan Sport University", "河南体育学院");
        addTier2("Henan University of Animal Husbandry and Economy", "河南牧业经济学院");
        addTier2("Henan University of Engineering", "河南工程学院");
        addTier2("Henan University of Urban Construction", "河南城建学院");
        addTier2("Hengshui University", "衡水学院");
        addTier2("Hengyang Normal University", "衡阳师范学院");
        addTier2("Hetao College", "河套学院");
        addTier2("Hexi University", "河西学院");
        addTier2("Heze University", "菏泽学院");
        addTier2("Hezhou University", "贺州学院");
        addTier2("Hohhot Minzu College", "呼和浩特民族学院");
        addTier2("Honghe University", "红河学院");
        addTier2("Huaibei Normal University", "淮北师范大学");
        addTier2("Huaihua University", "怀化学院");
        addTier2("Huainan Normal University", "淮南师范学院");
        addTier2("Huaiyin Institute of Technology", "淮阴工学院");
        addTier2("Huaiyin Normal University", "淮阴师范学院");
        addTier2("Huanggang Normal University", "黄冈师范学院");
        addTier2("Huanghuai University", "黄淮学院");
        addTier2("HuangShan University", "黄山学院");
        addTier2("Hubei Engineering University", "湖北工程学院");
        addTier2("Hubei Institute of Fine Arts", "湖北美术学院");
        addTier2("Hubei Minzu University", "湖北民族大学");
        addTier2("Hubei Normal University", "湖北师范大学");
        addTier2("Hubei Polytechnic University", "湖北理工学院");
        addTier2("Hubei University of Arts and Science", "湖北文理学院");
        addTier2("Hubei University of Automotive Technology", "湖北汽车工业学院");
        addTier2("Hubei University of Chinese Medicine", "湖北中医药大学");
        addTier2("Hubei University of Economics", "湖北经济学院");
        addTier2("Hubei University of Education", "湖北第二师范学院");
        addTier2("Hubei University of Medicine", "湖北医药学院");
        addTier2("Hubei University Of Police", "湖北警官学院");
        addTier2("Hubei University of Science and Techonology", "湖北科技学院");
        addTier2("Huizhou University", "惠州学院");
        addTier2("Hulunbuir University", "呼伦贝尔学院");
        addTier2("Hunan City University", "湖南城市学院");
        addTier2("Hunan First Normal University", "湖南第一师范学院");
        addTier2("Hunan Institute of Engineering", "湖南工程学院");
        addTier2("Hunan Institute of Science and Technology", "湖南理工学院");
        addTier2("Hunan Institute of Technology", "湖南工学院");
        addTier2("Hunan Police Academy", "湖南警察学院");
        addTier2("Hunan University of Arts and Science", "湖南文理学院");
        addTier2("Hunan University of Finance and Economics", "湖南财政经济学院");
        addTier2("Hunan University of Humanities, Science and Technology", "湖南人文科技学院");
        addTier2("Hunan University of Medicine", "湖南医药学院");
        addTier2("Hunan University of Science and Engineering", "湖南科技学院");
        addTier2("Hunan University of Technology", "湖南工业大学");
        addTier2("Hunan University of Technology and Business", "湖南工商大学");
        addTier2("Hunan Women's University", "湖南女子学院");
        addTier2("Huzhou College", "湖州学院");
        addTier2("Huzhou University", "湖州师范学院");
        addTier2("Inner Mongolia Agricultural University", "内蒙古农业大学");
        addTier2("Inner Mongolia Arts University", "内蒙古艺术学院");
        addTier2("Inner Mongolia Medical University", "内蒙古医科大学");
        addTier2("Inner Mongolia Minzu University", "内蒙古民族大学");
        addTier2("Inner Mongolia Normal University", "内蒙古师范大学");
        addTier2("Inner Mongolia University of Finance and Economics", "内蒙古财经大学");
        addTier2("Inner Mongolia University of Science and Technolgogy", "内蒙古科技大学");
        addTier2("Inner Mongolia University of Technology", "内蒙古工业大学");
        addTier2("Institute of Disaster Prevention", "防灾科技学院");
        addTier2("Jiamusi University", "佳木斯大学");
        addTier2("Jianghan University", "江汉大学");
        addTier2("Jiangsu Ocean University", "江苏海洋大学");
        addTier2("Jiangsu Police Institute", "江苏警官学院");
        addTier2("Jiangsu Second Normal University", "江苏第二师范学院");
        addTier2("Jiangsu University of Technology", "江苏理工学院");
        addTier2("Jiangxi Police Institute", "江西警察学院");
        addTier2("Jiangxi Science & Technology Normal University", "江西科技师范学院");
        addTier2("Jiangxi University of Traditional Chinese Medicine", "江西中医药大学");
        addTier2("Jiangxi University of Water Resources and Electric Power", "江西水利电力大学");
        addTier2("Jiaxing Nanhu University", "嘉兴南湖学院");
        addTier2("Jiaxing University", "嘉兴学院");
        addTier2("Jiaying University", "嘉应学院");
        addTier2("Jilin Agriculture Science and Technology University", "吉林农业科技学院");
        addTier2("Jilin Animation Institute", "吉林动画学院");
        addTier2("Jilin Business and Technology College", "吉林工商学院");
        addTier2("JILIN ENGINEERING NORMAL UNIVERSITY", "吉林工程技术师范学院");
        addTier2("Jilin International Studies University", "吉林外国语大学");
        addTier2("Jilin Jianzhu University", "吉林建筑大学");
        addTier2("Jilin Medical College", "吉林医药学院");
        addTier2("Jilin Normal University", "吉林师范大学");
        addTier2("Jilin Police College", "吉林警察学院");
        addTier2("Jilin Sport University", "吉林体育学院");
        addTier2("Jilin University of Arts", "吉林艺术学院");
        addTier2("Jilin University of Chemical Technology", "吉林化工学院");
        addTier2("JiLin University of Finance and Economics", "吉林财经大学");
        addTier2("Jingchu University of Technology", "荆楚理工学院");
        addTier2("Jingdezhen Ceramic University", "景德镇陶瓷大学");
        addTier2("Jingdezhen University", "景德镇学院");
        addTier2("Jinggangshan University", "井冈山大学");
        addTier2("Jining Medical University", "济宁医学院");
        addTier2("Jining Normal University", "集宁师范学院");
        addTier2("Jining University", "济宁学院");
        addTier2("Jinling Institute Of Technology", "金陵科技学院");
        addTier2("Jinzhong University", "晋中学院");
        addTier2("Jinzhou Medical University", "锦州医科大学");
        addTier2("Jishou University", "吉首大学");
        addTier2("Jiujiang University", "九江学院");
        addTier2("Kaili University", "凯里学院");
        addTier2("Kashi University", "喀什大学");
        addTier2("Kunming Medical University", "昆明医科大学");
        addTier2("Kunming University", "昆明学院");
        addTier2("Langfang Normal University", "廊坊师范学院");
        addTier2("Lanzhou City University", "兰州城市学院");
        addTier2("Lanzhou Institute of Technology", "兰州工业学院");
        addTier2("Lanzhou Jiaotong University", "兰州交通大学");
        addTier2("Lanzhou University of Arts and Science", "兰州文理学院");
        addTier2("Lanzhou University of Finance and Economics", "兰州财经大学");
        addTier2("Leshan Normal University", "乐山师范学院");
        addTier2("Liaocheng University", "聊城大学");
        addTier2("Liaoning Institute of Science and Technology", "辽宁科技学院");
        addTier2("Liaoning Petrochemical University", "辽宁石油化工大学");
        addTier2("Liaoning Police College", "辽宁警察学院");
        addTier2("Liaoning Technical University", "辽宁工程技术大学");
        addTier2("Liaoning University of Technology", "辽宁工业大学");
        addTier2("Lingnan Normal University", "岭南师范学院");
        addTier2("Linyi University", "临沂大学");
        addTier2("Lishui University", "丽水学院");
        addTier2("LiuPanShui Normal University", "六盘水师范学院");
        addTier2("Longdong University", "陇东学院");
        addTier2("Longyan University", "龙岩学院");
        addTier2("Ludong University", "鲁东大学");
        addTier2("Luoyang Institute of Science and Technology", "洛阳理工学院");
        addTier2("Luoyang Normal University", "洛阳师范学院");
        addTier2("LuXun Academy of Fine Arts", "鲁迅美术学院");
        addTier2("Lyuliang University", "吕梁学院");
        addTier2("Mianyang Teachers' College", "绵阳师范学院");
        addTier2("Minjiang University", "闽江学院");
        addTier2("Minnan Normal University", "闽南师范大学");
        addTier2("Minzu Normal University of Xingyi", "兴义民族师范学院");
        addTier2("Mudanjiang Medical University", "牡丹江医学院");
        addTier2("Mudanjiang Normal University", "牡丹江师范学院");
        addTier2("Nanchang Medical College", "南昌医学院");
        addTier2("Nanchang Normal University", "南昌师范学院");
        addTier2("Nanfang College Guangzhou", "广州南方学院");
        addTier2("Nanjing Institute of Technology", "南京工程学院");
        addTier2("Nanjing Normal University Of Special Education", "南京特殊教育师范学院");
        addTier2("Nanjing Police University", "南京警察学院");
        addTier2("Nanjing Sport Institute", "南京体育学院");
        addTier2("Nanjing University of the Arts", "南京艺术学院");
        addTier2("Nanjing Xiaozhuang University", "南京晓庄学院");
        addTier2("Nanning Normal University", "南宁师范大学");
        addTier2("Nanyang Institute of Technology", "南阳理工学院");
        addTier2("Nanyang Normal University", "南阳师范学院");
        addTier2("National Academy of Chinese Theater Arts", "中国戏曲学院");
        addTier2("Neijiang Normal University", "内江师范学院");
        addTier2("Ningbo University of Finance and Economics", "宁波财经学院");
        addTier2("Ningbo University of Technology", "宁波工程学院");
        addTier2("NingboTech University", "浙大宁波理工学院");
        addTier2("Ningde Normal University", "宁德师范学院");
        addTier2("Ningxia Normal University", "宁夏师范学院");
        addTier2("North China Institute of Aerospace Engineering", "北华航天工业学院");
        addTier2("North China Institute of Science and Technology", "华北科技学院");
        addTier2("North China University of Science and Technology", "华北理工大学");
        addTier2("North China University of Water Resources and Electric Power", "华北水利水电大学");
        addTier2("North Minzu University", "北方民族大学");
        addTier2("North Sichuan Medical College", "川北医学院");
        addTier2("Northwest Minzu University", "西北民族大学");
        addTier2("Ordos Institute of Technology", "鄂尔多斯应用技术学院");
        addTier2("Panzhihua University", "攀枝花学院");
        addTier2("Pingdingshan University", "平顶山学院");
        addTier2("Pingxiang University", "萍乡学院");
        addTier2("Puer University", "普洱学院");
        addTier2("Putian University", "莆田学院");
        addTier2("Qiannan Normal University for Nationalities", "黔南民族师范学院");
        addTier2("Qilu Institute of Technology", "齐鲁理工学院");
        addTier2("Qilu Normal University", "齐鲁师范学院");
        addTier2("Qingdao Agricultural University", "青岛农业大学");
        addTier2("Qinghai Minzu University", "青海民族大学");
        addTier2("Qinghai Normal University", "青海师范大学");
        addTier2("Qiongtai Normal University", "琼台师范学院");
        addTier2("Qiqihar Medical University", "齐齐哈尔医学院");
        addTier2("Qiqihar University", "齐齐哈尔大学");
        addTier2("Quanzhou Normal University", "泉州师范学院");
        addTier2("Qujing Normal University", "曲靖师范学院");
        addTier2("Quzhou University", "衢州学院");
        addTier2("Sanming University", "三明学院");
        addTier2("Shaanxi University of Chinese Medicine", "陕西中医药大学");
        addTier2("Shaanxi University of Technology", "陕西理工大学");
        addTier2("SHAANXI XUEQIAN NORMAL UNIVERSITY", "陕西学前师范学院");
        addTier2("Shandong Agriculture And Engineering University", "山东农业工程学院");
        addTier2("Shandong Institute of Petroleum and Chemical Technology", "山东石油化工学院");
        addTier2("Shandong Jianzhu University", "山东建筑大学");
        addTier2("Shandong Jiaotong University", "山东交通大学");
        addTier2("Shandong Management University", "山东管理学院");
        addTier2("Shandong Police College", "山东警察学院");
        addTier2("Shandong Second Medical University", "山东第二医科大学");
        addTier2("Shandong Sport University", "山东体育学院");
        addTier2("Shandong Technology and Business University", "山东工商学院");
        addTier2("Shandong University of Aeronautics", "山东航空学院");
        addTier2("Shandong University of Art & Design", "山东工艺美术学院");
        addTier2("Shandong University Of Arts", "山东艺术学院");
        addTier2("Shandong University of Political Science and Law", "山东政法学院");
        addTier2("Shandong Women's University", "山东女子学院");
        addTier2("Shandong Xiehe University", "山东协和学院");
        addTier2("Shandong Youth University Of Political Science", "山东青年政治学院");
        addTier2("Shanghai Business School", "上海商学院");
        addTier2("Shanghai Conservatory of Music", "上海音乐学院");
        addTier2("Shanghai Customs College", "上海海关学院");
        addTier2("Shanghai Dianji University", "上海电机学院");
        addTier2("Shanghai Institute of Technology", "上海应用技术大学");
        addTier2("Shanghai Lixin University of Accounting and Finance", "上海立信会计金融学院");
        addTier2("Shanghai Police College", "上海公安学院");
        addTier2("Shanghai Polytechnic University", "上海第二工业大学");
        addTier2("Shanghai Theatre Academy", "上海戏剧学院");
        addTier2("Shanghai University of Engineering Science", "上海工程技术大学");
        addTier2("Shanghai University of Medicine & Health Sciences", "上海健康医学院");
        addTier2("Shanghai University of Political Science and Law", "上海政法学院");
        addTier2("Shangluo University", "商洛学院");
        addTier2("Shangqiu Normal University", "商丘师范学院");
        addTier2("Shangrao Normal University", "上饶师范学院");
        addTier2("Shanxi Agricultural University", "山西农业大学");
        addTier2("Shanxi College of Technology", "山西工学院");
        addTier2("Shanxi Datong University", "山西大同大学");
        addTier2("Shanxi Institute of Energy", "山西能源学院");
        addTier2("Shanxi Institute of Science and Technology", "山西科技学院");
        addTier2("Shanxi Institute of Technology", "山西工程技术学院");
        addTier2("Shanxi Normal University", "山西师范大学");
        addTier2("Shanxi Police College", "山西警察学院");
        addTier2("Shanxi University of Chinese Medicine", "山西中医药大学");
        addTier2("Shanxi University of Electronic Science and Technology", "山西电子科技学院");
        addTier2("Shaoguan University", "韶关学院");
        addTier2("Shaoxing University", "绍兴文理学院");
        addTier2("Shaoyang University", "邵阳学院");
        addTier2("Shenyang Conservatory of Music", "沈阳音乐学院");
        addTier2("Shenyang Institute of Engineering", "沈阳工程学院");
        addTier2("Shenyang Ligong University", "沈阳理工大学");
        addTier2("Shenyang Medical College", "沈阳医学院");
        addTier2("Shenyang Normal University", "沈阳师范大学");
        addTier2("Shenyang Sport University", "沈阳体育学院");
        addTier2("Shenyang University", "沈阳大学");
        addTier2("Shenyang University of Chemical Technology", "沈阳化工大学");
        addTier2("Shenzhen University of Advanced Technology", "深圳理工大学");
        addTier2("Shenzhen Technology University", "深圳技术大学");
        addTier2("Shijiazhuang University", "石家庄学院");
        addTier2("Sichuan Conservatory of Music", "四川音乐学院");
        addTier2("Sichuan Fine Arts Institute", "四川美术学院");
        addTier2("Sichuan Minzu College", "四川民族学院");
        addTier2("Sichuan Police College", "四川警察学院");
        addTier2("Sichuan Tourism University", "四川旅游学院");
        addTier2("Sichuan University of Arts and Science", "四川文理学院");
        addTier2("Sichuan University of Media and Communications", "四川传媒学院");
        addTier2("Sichuan University of Science and Engineering", "四川轻化工大学");
        addTier2("Southwest Forestry University", "西南林业大学");
        addTier2("Sui Hua University", "绥化学院");
        addTier2("Suqian University", "宿迁学院");
        addTier2("Suzhou City University", "苏州城市学院");
        addTier2("Suzhou University", "宿州学院");
        addTier2("Suzhou University of Technology", "苏州理工学院");
        addTier2("Taishan University", "泰山学院");
        addTier2("Taiyuan Institute of Technology", "太原工业学院");
        addTier2("Taiyuan Normal University", "太原师范学院");
        addTier2("Taiyuan University", "太原学院");
        addTier2("Taiyuan University of Science and Technology", "太原科技大学");
        addTier2("Taizhou University", "台州学院");
        addTier2("Tangshan Normal University", "唐山师范学院");
        addTier2("Tangshan University", "唐山学院");
        addTier2("Tarim University", "塔里木大学");
        addTier2("The Central Academy Of Drama", "中央戏剧学院");
        addTier2("The National Police University for Criminal Justice", "中央司法警官学院");
        addTier2("Tianjin Academy of Fine Arts", "天津美术学院");
        addTier2("Tianjin Agricultural University", "天津农学院");
        addTier2("Tianjin Chengjian University", "天津城建大学");
        addTier2("Tianjin Conservatory of Music", "天津音乐学院");
        addTier2("Tianjin Foreign Studies University", "天津外国语大学");
        addTier2("Tianjin Sino-German University of Applied Sciences", "天津中德应用技术大学");
        addTier2("Tianjin University of Commerce", "天津商业大学");
        addTier2("Tianjin University of Sport", "天津体育学院");
        addTier2("Tianjin University of Technology and Education", "天津职业技术师范大学");
        addTier2("Tianshui Normal University", "天水师范学院");
        addTier2("Tibetan Traditional Medicine College", "西藏藏医药大学");
        addTier2("Tonghua Normal University", "通化师范学院");
        addTier2("Tongling University", "铜陵学院");
        addTier2("Tongren University", "铜仁学院");
        addTier2("University of Science and Technology LiaoNing", "辽宁科技大学");
        addTier2("Wannan Medical College", "皖南医学院");
        addTier2("Weifang University", "潍坊学院");
        addTier2("Weinan Normal University", "渭南师范学院");
        addTier2("WenShan University", "文山学院");
        addTier2("Wenzhou University of Technology", "温州理工学院");
        addTier2("West Anhui University", "皖西学院");
        addTier2("West Yunnan University", "滇西科技师范学院");
        addTier2("West Yunnan University of Applied Sciences", "滇西应用技术大学");
        addTier2("Wuchang Shouyi University", "武昌首义学院");
        addTier2("Wuchang University of Technology", "武昌理工学院");
        addTier2("Wuhan Business University", "武汉商学院");
        addTier2("Wuhan Conservatory Of Music", "武汉音乐学院");
        addTier2("Wuhan Polytechnic University", "武汉轻工大学");
        addTier2("Wuhan Sports University", "武汉体育学院");
        addTier2("Wuxi Taihu University", "无锡太湖学院");
        addTier2("Wuxi University", "无锡学院");
        addTier2("Wuyi University", "五邑大学"); // Guangdong
        addTier2("Wuyi University", "武夷学院"); // Fujian
        addTier2("Wuzhou University", "梧州学院");
        addTier2("Xiamen Medical College", "厦门医学院");
        addTier2("Xiamen University of Technology", "厦门理工大学");
        addTier2("Xi'an Academy of Fine Arts", "西安美术学院");
        addTier2("Xi'an Aeronautical Institute", "西安航空学院");
        addTier2("Xi'an Conservatory of Music", "西安音乐学院");
        addTier2("Xi'an Medical University", "西安医学院");
        addTier2("Xi'an Physical Education University", "西安体育学院");
        addTier2("Xi'an Polytechnic University", "西安工程大学");
        addTier2("Xi'an University", "西安文理学院");
        addTier2("Xi'an University of Finance and Economics", "西安财经大学");
        addTier2("Xiangnan University", "湘南学院");
        addTier2("Xianyang Normal University", "咸阳师范学院");
        addTier2("Xichang University", "西昌学院");
        addTier2("Xihua University", "西华大学");
        addTier2("Xinghai Conservatory of Music", "星海音乐学院");
        addTier2("Xingtai University", "邢台学院");
        addTier2("Xinjiang Agricultural University", "新疆农业大学");
        addTier2("Xinjiang Arts University", "新疆艺术学院");
        addTier2("Xinjiang College of Political Science and Law", "新疆政法学院");
        addTier2("Xinjiang Institute of Engineering", "新疆工程学院");
        addTier2("Xinjiang Institute of Technology", "新疆理工学院");
        addTier2("Xinjiang Medical University", "新疆医科大学");
        addTier2("Xinjiang Normal University", "新疆师范大学");
        addTier2("Xinjiang police college", "新疆警察学院");
        addTier2("Xinjiang Second Medical College", "新疆第二医学院");
        addTier2("Xinjiang University of Finance and Economics", "新疆财经大学");
        addTier2("Xinjiang University of Science and Technology", "新疆科技学院");
        addTier2("Xinxiang University", "新乡学院");
        addTier2("Xinyang Agriculture and Forestry University", "信阳农林学院");
        addTier2("Xinyang Normal University", "信阳师范学院");
        addTier2("Xinyu  University", "新余学院");
        addTier2("Xinzhou Normal University", "忻州师范学院");
        addTier2("Xizang Agricultural and Animal Husbandry University", "西藏农牧学院");
        addTier2("Xizang Minzu University", "西藏民族大学");
        addTier2("Xuchang University", "许昌学院");
        addTier2("Xuzhou University of Technology", "徐州工程学院");
        addTier2("Yan'an University", "延安大学");
        addTier2("Yancheng Institute of Technology", "盐城工学院");
        addTier2("Yancheng Teachers University", "盐城师范学院");
        addTier2("Yangtze Normal University", "长江师范学院");
        addTier2("Yantai University", "烟台大学");
        addTier2("Yibin University", "宜宾学院");
        addTier2("Yichun University", "宜春学院");
        addTier2("Yili Normal University", "伊犁师范大学");
        addTier2("YingKou Institute of Technology", "营口理工学院");
        addTier2("Youjiang Medical University for Nationalities", "右江民族医学院");
        addTier2("Yulin Normal University", "玉林师范学院");
        addTier2("Yulin University", "榆林学院");
        addTier2("Yuncheng University", "运城学院");
        addTier2("Yunnan Agricultural University", "云南农业大学");
        addTier2("Yunnan Arts University", "云南艺术学院");
        addTier2("Yunnan Minzu University", "云南民族大学");
        addTier2("Yunnan Poice College", "云南警官学院");
        addTier2("Yunnan University of Chinese Medicine", "云南中医药大学");
        addTier2("Yunnan University of Finance and Economics", "云南财经大学");
        addTier2("Yuxi Normal University", "玉溪师范学院");
        addTier2("Yuzhang Normal University", "豫章师范学院");
        addTier2("Zaozhuang University", "枣庄学院");
        addTier2("Zhangjiakou University", "张家口学院");
        addTier2("Zhaoqing University", "肇庆学院");
        addTier2("Zhaotong University", "昭通学院");
        addTier2("Zhejiang Conservatory of Music", "浙江音乐学院");
        addTier2("Zhejiang International Studies University", "浙江外国语学院");
        addTier2("Zhejiang Police College", "浙江警察学院");
        addTier2("Zhejiang Shuren University", "浙江树人大学");
        addTier2("Zhejiang University of Water Resources and Electric Power", "浙江水利水电学院");
        addTier2("Zhejiang Wanli University", "浙江万里学院");
        addTier2("Zhejiang Yuexiu University", "浙江越秀外国语学院");
        addTier2("Zhengzhou Normal University", "郑州师范学院");
        addTier2("Zhengzhou Police University", "郑州警察学院");
        addTier2("Zhengzhou University of Aeronautics", "郑州航空工业管理学院");
        addTier2("Zhengzhou University of Light Industry", "郑州轻工业大学");
        addTier2("Zhengzhou University of Technology", "郑州工程技术学院");
        addTier2("Zhongkai University of Agriculture and Engineering", "仲恺农业工程学院");
        addTier2("Zhongyuan University of Technology", "中原工学院");
        addTier2("Zhoukou Normal University", "周口师范学院");
        addTier2("Zhuhai College of Science and Technology", "珠海科技学院");
        addTier2("Zunyi Medical University", "遵义医科大学");
        addTier2("Zunyi Normal University", "遵义师范学院");
        // ... (其他公办本科默认为 Tier 2)
    }

    // =================================================================================
    // Tier 3: 独立学院 + 民办院校 (Independent Colleges / Private)
    // =================================================================================
    private static final Set<String> TIER_3_UNIVERSITIES = new HashSet<>();
    static {
        // --- 知名独立学院 ---
        addTier3("Anhui Institute of Information Technology", "安徽信息工程学院");
        addTier3("Anhui Institute of Medicine", "安徽第二医学院");
        addTier3("Anhui International Studies University", "安徽外国语学院");
        addTier3("Anhui Sanlian University", "安徽三联学院");
        addTier3("Anhui University of Arts", "安徽艺术学院");
        addTier3("Anhui Wenda University Of Information Engineering", "安徽文达信息工程学院");
        addTier3("Anhui Xinhua University", "安徽新华学院");
        addTier3("Anyang University", "安阳学院");
        addTier3("Applied Technology College of Soochow University", "苏州大学应用技术学院");
        addTier3("Baoding University of Technology", "保定理工学院");
        addTier3("Beihai College of Beihang University", "北京航空航天大学北海学院");
        addTier3("Beihai University of Art and Design", "北海艺术设计学院");
        addTier3("Beijing Hospitality Institute", "北京第二外国语学院中瑞酒店管理学院");
        addTier3("Beijing Institute of Technology, Zhuhai", "北京理工大学珠海学院");
        addTier3("Beijing Normal University, Zhuhai", "北京师范大学珠海分校");
        addTier3("Beijing University of Chinese Medicine Dongfang College", "北京中医药大学东方学院");
        addTier3("Beijing University of Financial Technology", "北京金融科技学院");
        addTier3("Bengbu University Of Technology and Business", "蚌埠工商学院");
        addTier3("BinHai School Of Foreign Affairs Of Tianjin Foreign Studies University", "天津外国语大学滨海外事学院");
        addTier3("Binjiang College of Zhejiang Chinese Medical University", "浙江中医药大学滨江学院");
        addTier3("Boda College of Jilin Normal University", "吉林师范大学博达学院");
        addTier3("Cangzhou Jiaotong College", "沧州交通学院");
        addTier3("Century College, Beijing University of Posts and Telecommunications", "北京邮电大学世纪学院");
        addTier3("Chang'an College Of Xidian University", "西安电子科技大学长安学院");
        addTier3("Chang'an University Xinghua College", "长安大学兴华学院");
        addTier3("Changchun College Of Electronic Technology", "长春电子科技学院");
        addTier3("Changchun Guanghua University", "长春光华学院");
        addTier3("Changchun Humanities and Sciences College", "长春人文学院");
        addTier3("Changchun Sci-Tech University", "长春科技学院");
        addTier3("Changchun University of Architecture & Civil Engineering", "长春建筑学院");
        addTier3("Changchun University of Finance and Economics", "长春财经学院");
        addTier3("Changde College", "常德学院");
        addTier3("Changsha Institute of Technology", "长沙工业学院");
        addTier3("Changsha Medical University", "长沙医学院");
        addTier3("Changzhou University Huaide College", "常州大学怀德学院");
        addTier3("Chengdu College of Arts and Sciences", "成都文理学院");
        addTier3("Chengdu College of University of Electronic Science And Technology of China", "电子科技大学成都学院");
        addTier3("Chengdu Jincheng College", "成都锦城学院");
        addTier3("Chengdu Neusoft University", "成都东软学院");
        addTier3("Chengnan College, Changsha University of Science and Technology", "长沙理工大学城南学院");
        addTier3("Chengyi College, Jimei University", "集美大学诚毅学院");
        addTier3("CHINA JILIANG UNIVERSITY College of Modern Science and Technology", "中国计量大学现代科技学院");
        addTier3("Chongqing College of Humanities, Science and Technology", "重庆人文科技学院");
        addTier3("Chongqing College of International Business and Economics", "重庆对外经贸学院");
        addTier3("Chongqing College of Mobile Communication", "重庆移通学院");
        addTier3("Chongqing College of Traditional Chinese Medicine", "重庆中医药学院");
        addTier3("Chongqing Finance and Economics College", "重庆财经学院");
        addTier3("Chongqing Institute of Engineering", "重庆工程学院");
        addTier3("Chongqing Institute of Foreign Studies", "重庆外语外事学院");
        addTier3("Chongqing Metropolitan College of Science and Technology", "重庆城市科技学院");
        addTier3("Chuanshan collage, University of South China", "南华大学船山学院");
        addTier3("City Institute,Dalian University of Technology", "大连理工大学城市学院");
        addTier3("City University of Hefei", "合肥城市学院");
        addTier3("Clinic Medical College of Anhui Medical University", "安徽医科大学临床医学院");
        addTier3("Clinical College of Hebei Medical University", "河北医科大学临床学院");
        addTier3("Clinical Medical College of Tianjin Medical University", "天津医科大学临床医学院");
        addTier3("College of Applied Technology, Hunan Institute of Engineering", "湖南工程学院应用技术学院");
        addTier3("College of Arts and Science of Hubei Normal University", "湖北师范大学文理学院");
        addTier3("College of Economics and Management Hebei University of Economics", "河北经贸大学经济管理学院");
        addTier3("College of Engineering, Shenyang University of Technology", "沈阳工业大学工程学院");
        addTier3("College of Humanities & Information Changchun University of Technology", "长春工业大学人文信息学院");
        addTier3("College of International Business Economics, WTU", "武汉纺织大学外经贸学院");
        addTier3("College of Law & Business of Hubei University of Economics", "湖北经济学院法商学院");
        addTier3("College of Modern Economics & Management JUFE", "江西财经大学现代经济管理学院");
        addTier3("College of Northern Science and Technology", "沈阳航空航天大学北方科技学院");
        addTier3("COLLEGE OF SCIENCE AND TECHNOLOGY HNUT", "湖南工业大学科技学院");
        addTier3("College of Science and Technology of China Three Gorges University", "三峡大学科技学院");
        addTier3("College of Science and Technology Xinjiang Agricultural University", "新疆农业大学科学技术学院");
        addTier3("College of Science&Techology Ningbo University", "宁波大学科学技术学院");
        addTier3("College of Technology, Hubei Engineering University", "湖北工程学院新技术学院");
        addTier3("Communication University of Kunming", "昆明传媒学院");
        addTier3("Communication University of Shanxi", "山西传媒学院");
        addTier3("Concord University College Fujian Normal University", "福建师范大学协和学院");
        addTier3("Dalian Art College", "大连艺术学院");
        addTier3("Dalian Neusoft University of Information", "大连东软信息学院");
        addTier3("Dalian Polytechnic University Arts and Information Engineering", "大连工业大学艺术与信息工程学院");
        addTier3("Dalian University of Finance and Economics", "大连财经学院");
        addTier3("Dalian University of Science and Technology", "大连科技学院");
        addTier3("Dehong Normal University", "德宏师范学院");
        addTier3("Dianchi College", "滇池学院");
        addTier3("Dongchang College of Liaocheng University", "聊城大学东昌学院");
        addTier3("Dongfang College, ShanDong University of Finance and Economics", "山东财经大学东方学院");
        addTier3("Dongguan City University", "东莞城市学院");
        addTier3("East University of Heilongjiang", "黑龙江东方学院");
        addTier3("Faculty of Chinese Medicine Science Guangxi University of Chinese", "广西中医药大学赛恩斯新医药学院");
        addTier3("Fuyang University of Technology", "阜阳理工学院");
        addTier3("Fuzhou Institute of Technology", "福州理工学院");
        addTier3("Fuzhou Medical College", "抚州医学院");
        addTier3("Fuzhou Technology and Business University", "福州工商学院");
        addTier3("Fuzhou University of International Studies and Trade", "福州外语外贸学院");
        addTier3("Fuzhou University Zhicheng College", "福州大学至诚学院");
        addTier3("Geely University", "吉利学院");
        addTier3("Gengdan Institute of Beijing University of Technology", "北京工业大学耿丹学院");
        addTier3("Ginko College of Hospitality Management", "成都银杏酒店管理学院");
        addTier3("Gongqing College of Nanchang University", "南昌大学共青学院");
        addTier3("Guangdong Baiyun University", "广东白云学院");
        addTier3("Guangdong Peizheng College", "广东培正学院");
        addTier3("Guangdong Technology College", "广东理工学院");
        addTier3("Guangdong University of Foreign Studies South China Business College", "广东外语外贸大学南国商学院");
        addTier3("Guangdong University of Science & Technology", "广东科技学院");
        addTier3("Guangxi University Of Foreign Languages", "广西外国语学院");
        addTier3("Guangzhou City University of Technology", "广州城市理工学院");
        addTier3("Guangzhou College of Applied Science and Technology", "广州应用科技学院");
        addTier3("Guangzhou College of Commerce", "广州商学院");
        addTier3("Guangzhou College of Technology and Business", "广州工商学院");
        addTier3("Guangzhou Huali College", "广州华立学院");
        addTier3("Guangzhou Huashang College", "广州华商学院");
        addTier3("Guangzhou Institute of Science and Technology", "广州理工学院");
        addTier3("Guangzhou Xinhua University", "广州新华学院");
        addTier3("Guilin Institute of Information Technology", "桂林信息科技学院");
        addTier3("Guilin Normal University", "桂林师范高等专科学校");
        addTier3("Guilin University", "桂林学院");
        addTier3("Guiyang Institute of Humanities and Technology", "贵阳人文科技学院");
        addTier3("Guiyang Institute of Information Science and Technology", "贵阳信息科技学院");
        addTier3("Guizhou Qiannan College of Science and Technology", "贵州黔南科技学院");
        addTier3("Guizhou Qiannan Economic College", "贵州黔南经济学院");
        addTier3("Haidu College, Qingdao Agricultural University", "青岛农业大学海都学院");
        addTier3("Haikou University of Economics", "海口经济学院");
        addTier3("Hangzhou Dianzi University Information Engineering College", "杭州电子科技大学信息工程学院");
        addTier3("Hangzhou Normal University Qianjiang College", "杭州师范大学钱江学院");
        addTier3("Hankou University", "汉口学院");
        addTier3("Hao Jing College of Shaanxi University of Science and Technology", "陕西科技大学镐京学院");
        addTier3("Harbin Cambridge University", "哈尔滨剑桥学院");
        addTier3("Harbin Far East Institute of Technology", "哈尔滨远东理工学院");
        addTier3("Harbin Guangsha College", "哈尔滨广厦学院");
        addTier3("Harbin Huade University", "哈尔滨华德学院");
        addTier3("Harbin Institute of Information Technology", "哈尔滨信息工程学院");
        addTier3("Harbin Institute of Petroleum", "哈尔滨石油学院");
        addTier3("He University", "辽宁何氏医学院");
        addTier3("Hebei Academy Of Fine Arts", "河北美术学院");
        addTier3("Hebei Agricultural University Department of modern Science and", "河北农业大学现代科技学院");
        addTier3("Hebei College of Science and Technology", "河北科技学院");
        addTier3("Hebei Institute of Communications", "河北传媒学院");
        addTier3("Hebei International Studies University", "河北外国语学院");
        addTier3("Hebei Oriental University", "河北东方学院");
        addTier3("Hebei University of Engineering Science", "河北工程技术学院");
        addTier3("Hefei University of Economics", "合肥经济学院");
        addTier3("Heilongjiang College of Business and Technology", "黑龙江工商学院");
        addTier3("Heilongjiang International University", "黑龙江外国语学院");
        addTier3("Heilongjiang University of Finance and Economics", "黑龙江财经学院");
        addTier3("Hengxing University", "青岛恒星科技学院");
        addTier3("Huaibei Institute of Technology", "淮北理工学院");
        addTier3("Huanghe Jiaotong University", "黄河交通学院");
        addTier3("Huanghe S&T University", "黄河科技学院");
        addTier3("Huaxin College of Hebei Geo University", "河北地质大学华信学院");
        addTier3("Hubei Business College", "湖北商贸学院");
        addTier3("Hubei Enshi College", "湖北恩施学院");
        addTier3("Hubei University of Technology Engineering and Technology College", "湖北工业大学工程技术学院");
        addTier3("Huihua College of Hebei Normal University", "河北师范大学汇华学院");
        addTier3("Hunan Applied Technology University", "湖南应用技术学院");
        addTier3("Hunan Institute Of Traffic Engineering", "湖南交通工程学院");
        addTier3("Hunan International Economics University", "湖南涉外经济学院");
        addTier3("Hunan University of Information Technology", "湖南信息学院");
        addTier3("Industrial and Commercial College, Hebei University", "河北大学工商学院");
        addTier3("Inner Mongolia Honder College of Arts and Sciences", "内蒙古鸿德文理学院");
        addTier3("Institute of Medicine and Nursing, Hubei University of Medicine", "湖北医药学院药护学院");
        addTier3("JiangSu Normal University KeWen College", "江苏师范大学科文学院");
        addTier3("Jiangsu University Jingjiang College", "江苏大学京江学院");
        addTier3("Jiangxi Flight University", "江西飞行学院");
        addTier3("Jiangxi Institute Of Fashion Technology", "江西服装学院");
        addTier3("Jiangxi Normal University Science and Technology College", "江西师范大学科学技术学院");
        addTier3("Jiangxi University of Applied Science", "江西应用科技学院");
        addTier3("Jiangxi University of Engineering", "江西工程学院");
        addTier3("Jiangxi University of Technology", "江西科技学院");
        addTier3("Jilin University of Architecture and Technology", "吉林建筑科技学院");
        addTier3("Jingdezhen Vocational University of Art", "景德镇艺术职业大学");
        addTier3("Jingzhou University", "荆州学院");
        addTier3("Jinshan College of Fujian Agriculture and Forestry University", "福建农林大学金山学院");
        addTier3("Jinzhong College of Health", "晋中健康乡村学院");
        addTier3("Jinzhong College of Information", "晋中信息学院");
        addTier3("Jitang College of North China University of Science and Technology", "华北理工大学冀唐学院");
        addTier3("Jiyang Collage of Zhejiang A&F University", "浙江农林大学暨阳学院");
        addTier3("Kangda College of Nanjing Medical University", "南京医科大学康达学院");
        addTier3("Kede College of Capital Normal University", "首都师范大学科德学院");
        addTier3("Kexin College, Hebei University of Engineering", "河北工程大学科信学院");
        addTier3("Keyi College of Zhejiang Sci-Tech University", "浙江理工大学科技与艺术学院");
        addTier3("Kunlun College of Qinghai University", "青海大学昆仑学院");
        addTier3("Kunlun Tourism College of Heilongjiang Institute of Technology", "黑龙江工程学院昆仑旅游学院");
        addTier3("Kunming City College", "昆明城市学院");
        addTier3("Kunming Medical University Haiyuan College", "昆明医科大学海源学院");
        addTier3("KUNMING UNIVERSITY OF SCIENCE AND TECHNOLOGY OXBRIDGE COLLEGE", "昆明理工大学津桥学院");
        addTier3("Lanzhou Bowen College of Science and Technology", "兰州博文科技学院");
        addTier3("Lanzhou College of Information Science and Technology", "兰州信息科技学院");
        addTier3("Lanzhou Technology and Business College", "兰州工商学院");
        addTier3("Lasa Normal University", "拉萨师范高等专科学校");
        addTier3("Lianyungang Normal University", "连云港师范高等专科学校");
        addTier3("LIAONING COMMUNICATION UNIVERSITY", "辽宁传媒学院");
        addTier3("Liaoning Finance and Trade College", "辽宁财贸学院");
        addTier3("Liaoning Institute of Science and Engineering", "辽宁理工学院");
        addTier3("Liaoning Normal University Haihua College", "辽宁师范大学海华学院");
        addTier3("Liaoning University of International Business and Economics", "辽宁对外经贸学院");
        addTier3("Lijiang Culture and Tourism College", "丽江文化旅游学院");
        addTier3("Lijiang Normal University", "丽江师范高等专科学校");
        addTier3("Liren College, Yanshan University", "燕山大学里仁学院");
        addTier3("Liuzhou Institute of Technology", "柳州工学院");
        addTier3("Maanshan University", "马鞍山学院");
        addTier3("Medical College of Jinzhou Medical University", "锦州医科大学医疗学院");
        addTier3("Medicine and Technology School of Zunyi Medical University", "遵义医科大学医学与科技学院");
        addTier3("Mianyang City College", "绵阳城市学院");
        addTier3("Minnan Science and Technology College", "闽南科技学院");
        addTier3("Minnan University of Science and Technology", "闽南理工学院");
        addTier3("Modern College of Northwest University", "西北大学现代学院");
        addTier3("Moutai Institute", "茅台学院");
        addTier3("NanChang Business College of JXAU", "江西农业大学南昌商学院");
        addTier3("Nanchang Institute of Science & Technology", "南昌理工学院");
        addTier3("Nanchang Institute of Technology", "南昌工学院");
        addTier3("Nanchang Jiaotong Institute", "南昌交通学院");
        addTier3("Nanchang Normal College of Applied Technology", "南昌应用技术师范学院");
        addTier3("Nanchang University College of Science and Technology", "南昌大学科学技术学院");
        addTier3("Nanhang Jincheng College", "南京航空航天大学金城学院");
        addTier3("Nanjing Audit University Jinshen College", "南京审计大学金审学院");
        addTier3("Nanjing Normal University Taizhou College", "南京师范大学泰州学院");
        addTier3("Nanjing Normal University Zhongbei College", "南京师范大学中北学院");
        addTier3("Nanjing Tech University Pujiang Institute", "南京工业大学浦江学院");
        addTier3("Nanjing University Jinling College", "南京大学金陵学院");
        addTier3("Nanjing University of Chinese Medicine Hanlin College", "南京中医药大学翰林学院");
        addTier3("Nanjing University of Finance and Economics Hongshan College", "南京财经大学红山学院");
        addTier3("Nanjing University of Science and Technology ZiJin College", "南京理工大学紫金学院");
        addTier3("Nankai University Binhai College", "南开大学滨海学院");
        addTier3("Nanning College of Technology", "南宁理工学院");
        addTier3("Nanning University", "南宁学院");
        addTier3("Nantong Institute of Technology", "南通理工学院");
        addTier3("Nantong University Xinglin College", "南通大学杏林学院");
        addTier3("Nanyue College of Hengyang Normal University", "衡阳师范学院南岳学院");
        addTier3("Neusoft Institute Guangdong", "广东东软学院");
        addTier3("Ningxia Institute of Science and Technology", "宁夏理工学院");
        addTier3("North Henan Medical University", "豫北医学院");
        addTier3("Orient Science and Technology College of Hunan Agricultural University", "湖南农业大学东方科技学院");
        addTier3("Pass College of Chongqing Technology and Business University", "重庆工商大学派斯学院");
        addTier3("Pioneer College, Inner Mongolia University", "内蒙古大学创业学院");
        addTier3("Qilu Medical University", "齐鲁医药学院");
        addTier3("Qingdao Binhai University", "青岛滨海学院");
        addTier3("Qingdao City University", "青岛城市学院");
        addTier3("Qingdao Film Academy", "青岛电影学院");
        addTier3("Qingdao Huanghai University", "青岛黄海学院");
        addTier3("Qingdao Institute of Technology", "青岛工学院");
        addTier3("Qinggong College, North China University of Science and Technology", "华北理工大学轻工学院");
        addTier3("Qinghai Institute of Technology", "青海理工学院");
        addTier3("Qiqihar Institute of Engineering", "齐齐哈尔工程学院");
        addTier3("Quanzhou University of Information Engineering", "泉州信息工程学院");
        addTier3("Qujing University of Medicin & Health Sciences", "曲靖医学高等专科学校");
        addTier3("Sanda University", "上海杉达学院");
        addTier3("Sanjiang University", "三江学院");
        addTier3("Science and Technology College GanNan Normal University", "赣南师范大学科技学院");
        addTier3("Science and Technology College of Hubei University of Arts and Science", "湖北文理学院理工学院");
        addTier3("Science and Technology College of Hubei University of Automotive", "湖北汽车工业学院科技学院");
        addTier3("Science and Technology College of NCHU", "南昌航空大学科技学院");
        addTier3("SHAANXI FASHION ENGINEERING UNIVERSITY", "陕西服装工程学院");
        addTier3("Shaanxi Institute of International Trade & Commerce", "陕西国际商贸学院");
        addTier3("SHANDONG HUAYU UNIVERSITY OF TECHNOLOGY", "山东华宇工学院");
        addTier3("Shanghai Institute of Visual Arts", "上海视觉艺术学院");
        addTier3("Shanghai Jian Qiao University", "上海建桥学院");
        addTier3("Shanghai Lida University", "上海立达学院");
        addTier3("Shanghai Normal University Tianhua College", "上海师范大学天华学院");
        addTier3("Shanghai University of Finance and Economics Zhejiang College", "上海财经大学浙江学院");
        addTier3("Shangqiu Institute of Technology", "商丘工学院");
        addTier3("Shangqiu University", "商丘学院");
        addTier3("Shanxi College Of Applied Science And Technology", "山西应用科技学院");
        addTier3("Shanxi Jinzhong Institute of Technology", "山西晋中理工学院");
        addTier3("Shanxi Technology and Business College", "山西工商学院");
        addTier3("Shanxi University of Finance and Economics Huashang College", "山西财经大学华商学院");
        addTier3("Shanxi University of Medicine", "山西医科大学晋祠学院");
        addTier3("Shaoxing Institute of Technology", "绍兴理工学院");
        addTier3("ShenQi Ethnic Medicine College of Guizhou Medical University", "贵州医科大学神奇民族医药学院");
        addTier3("Shenyang City University", "沈阳城市学院");
        addTier3("Shenyang Institute of Science and Technology", "沈阳科技学院");
        addTier3("Shenyang Institute of Technology", "沈阳工学院");
        addTier3("Shenyang Urban Construction University", "沈阳城市建设学院");
        addTier3("Shijiazhuang Tiedao University SiFang College", "石家庄铁道大学四方学院");
        addTier3("Shiyuan College of Nanning Normal University", "南宁师范大学师园学院");
        addTier3("Shizhen College of Guizhou University of Traditional Chinese Medicine", "贵州中医药大学时珍学院");
        addTier3("Sias University", "郑州西亚斯学院");
        addTier3("SICHUAN FILM AND TELEVISION UNIVERSITY", "四川电影电视学院");
        addTier3("Sichuan Institute of Industrial technology", "四川工业科技学院");
        addTier3("Sichuan Technology and Business University", "四川工商学院");
        addTier3("Sichuan University Jinjiang College", "四川大学锦江学院");
        addTier3("Sichuan University of Culture and Arts", "四川文化艺术学院");
        addTier3("Software Engineering Institute of Guangzhou", "广州软件学院");
        addTier3("Southwest Jiaotong University Hope College", "西南交通大学希望学院");
        addTier3("Suzhou Institute of Technology, Jiangsu University of Science and Technology", "江苏科技大学苏州理工学院");
        addTier3("Swan College, Central South University of Forestry and Technology", "中南林业科技大学涉外学院");
        addTier3("Taishan College of Science and Technology", "泰山科技学院");
        addTier3("Taizhou Institute of Sci.& Tech., NUST", "南京理工大学泰州科技学院");
        addTier3("Technology & Media University of Henan Kaifeng", "河南开封科技传媒学院");
        addTier3("The College of Arts and Sciences Kunming", "昆明文理学院");
        addTier3("The College of Post and Telecommunication of WIT", "武汉工程大学邮电与信息工程学院");
        addTier3("The College of Sports Science and Technology of Wuhan Sports", "武汉体育学院体育科技学院");
        addTier3("The Engineering&Technical College of Chengdu University of Technology", "成都理工大学工程技术学院");
        addTier3("The Hi-tech College of Xi'an University of Technology", "西安理工大学高科学院");
        addTier3("The Tourism College of Changchun University", "长春大学旅游学院");
        addTier3("Tianfu College of SWUFE", "西南财经大学天府学院");
        addTier3("Tianjin College of Media and Arts", "天津传媒学院");
        addTier3("Tianjin College, University of Science and Technology Beijing", "北京科技大学天津学院");
        addTier3("Tianjin Normal University Jingu College", "天津师范大学津沽学院");
        addTier3("Tianjin Renai College", "天津仁爱学院");
        addTier3("Tianjin University of Commerce Boustead College", "天津商业大学宝德学院");
        addTier3("Tianjin University of Finance and Economics Pearl River College", "天津财经大学珠江学院");
        addTier3("Tianping College of Suzhou University of Science and Technology", "苏州科技大学天平学院");
        addTier3("Tianshi College", "天津天狮学院");
        addTier3("Tongda College of Nanjing University of Posts & Telecommunications", "南京邮电大学通达学院");
        addTier3("TongJi ZheJiang College", "同济大学浙江学院");
        addTier3("United International College", "北京师范大学-香港浸会大学联合国际学院");
        addTier3("University of Electronic Science and Technology of China, Zhongshan Institute", "电子科技大学中山学院");
        addTier3("University of Nottingham Ningbo China", "宁波诺丁汉大学");
        addTier3("University of Sanya", "三亚学院");
        addTier3("Wanjiang University of Technology", "皖江工学院");
        addTier3("Weifang Institute of Technology", "潍坊理工学院");
        addTier3("Weifang University of Science & Technology", "潍坊科技学院");
        addTier3("Wenhua College", "文华学院");
        addTier3("Wenzhou Business College", "温州商学院");
        addTier3("Wenzhou Medical University Renji College", "温州医科大学仁济学院");
        addTier3("Wenzhou-Kean University", "温州肯恩大学");
        addTier3("Wuchang Institute of Technology", "武昌工学院");
        addTier3("Wuhan City College", "武汉城市学院");
        addTier3("Wuhan College", "武汉学院");
        addTier3("Wuhan College of Arts & Science", "武汉文理学院");
        addTier3("Wuhan Donghu University", "武汉东湖学院");
        addTier3("Wuhan Huaxia Institute of Technology", "武汉华夏理工学院");
        addTier3("Wuhan Institute of Design and Sciences", "武汉设计工程学院");
        addTier3("Wuhan Qingchuan University", "武汉晴川学院");
        addTier3("Wuhan Technology and Business University", "武汉工商学院");
        addTier3("Wuhan University of Bioengineering", "武汉生物工程学院");
        addTier3("Wuhan University of Communication", "武汉传媒学院");
        addTier3("Wuhan University of Engineering Science", "武汉工程科技学院");
        addTier3("Wuhu University", "芜湖学院");
        addTier3("Xiamen Huaxia University", "厦门华厦学院");
        addTier3("Xiamen Institute of Technology", "厦门工学院");
        addTier3("Xiamen University Tan Kah Kee College", "厦门大学嘉庚学院");
        addTier3("Xi'an Eurasia University", "西安欧亚学院");
        addTier3("Xi'an Fanyi University", "西安翻译学院");
        addTier3("Xi'an Innovation College of Yan'an University", "延安大学西安创新学院");
        addTier3("Xi'an International University", "西安外事学院");
        addTier3("Xi'an Jiaotong University City College", "西安交通大学城市学院");
        addTier3("Xi'an Jiaotong-Liverpool University", "西交利物浦大学");
        addTier3("Xi'an Kedagaoxin University", "西安科技大学高新学院");
        addTier3("Xi'an Mingde Institute of Technology", "西安明德理工学院");
        addTier3("Xi'an Peihua University", "西安培华学院");
        addTier3("Xi'an Siyuan University", "西安思源学院");
        addTier3("Xi'an Technology and Business College", "西安工商学院");
        addTier3("Xi'an Traffic Enginering Institute", "西安交通工程学院");
        addTier3("Xian University of Architecture and Technology Huaqing College", "西安建筑科技大学华清学院");
        addTier3("Xianda College of Economics and Humanities Shanghai International", "上海外国语大学贤达经济人文学院");
        addTier3("Xiangsihu College of Guangxi Minzu University", "广西民族大学相思湖学院");
        addTier3("Xiangtan Institute of Technology", "湘潭理工学院");
        addTier3("Xiangxing College of Hunan University of Chinese Medicine", "湖南中医药大学湘杏学院");
        addTier3("Xiaoxiang College of Hunan University of Science and Technology", "湖南科技大学潇湘学院");
        addTier3("Xijing University", "西京学院");
        addTier3("Xing Wei College", "上海兴伟学院");
        addTier3("Xing Zhi College of Xi'an University of Finance and Economics", "西安财经大学行知学院");
        addTier3("Xinglin College of Liaoning University of Traditional Chinese Medicine", "辽宁中医药大学杏林学院");
        addTier3("Xingtai Medical College", "邢台医学高等专科学校");
        addTier3("Xingxiang College of Xiangtan University", "湘潭大学兴湘学院");
        addTier3("XINHUA COLLEGE OF NINGXIA UNIVERSITY", "宁夏大学新华学院");
        addTier3("Xinjiang Hetian College", "新疆和田学院");
        addTier3("Xinxiang Institute of Engineering", "新乡工程学院");
        addTier3("Xinyang University", "信阳学院");
        addTier3("Xuhai College, China University of Mining & Technology", "中国矿业大学徐海学院");
        addTier3("Yanching Institute of Technology", "燕京理工学院");
        addTier3("Yang-En University", "仰恩大学");
        addTier3("Yango College", "阳光学院");
        addTier3("Yangtze University College of Arts and Sciences", "长江大学文理学院");
        addTier3("Yangzhou University Guangling College", "扬州大学广陵学院");
        addTier3("Yanshan College Shandong University of Finance and Economics", "山东财经大学燕山学院");
        addTier3("Yantai Institute of Science and Technology", "烟台科技学院");
        addTier3("Yantai Institute of Technology", "烟台理工学院");
        addTier3("Yantai Nanshan University", "烟台南山学院");
        addTier3("Yinchuan University of Energy", "银川能源学院");
        addTier3("Yinchuan University of Science and Technology", "银川科技学院");
        addTier3("Yunnan College Of Bussiness Management", "云南经济管理学院");
        addTier3("Yunnan Technology and Business University", "云南工商学院");
        addTier3("Zhangjiajie College", "张家界学院");
        addTier3("Zhanjiang University of Science and Technology", "湛江科技学院");
        addTier3("Zhaoqing Medical College", "肇庆医学高等专科学校");
        addTier3("Zhejiang Gongshang University Hangzhou College of Commerce", "浙江工商大学杭州商学院");
        addTier3("Zhejiang Normal University Xingzhi College", "浙江师范大学行知学院");
        addTier3("Zhejiang University of Finance and Economics Dongfang College", "浙江财经大学东方学院");
        addTier3("Zhengzhou Academy of Fine Arts", "郑州美术学院");
        addTier3("Zhengzhou Business University", "郑州经贸学院");
        addTier3("Zhengzhou College of Economics and Business", "郑州经贸学院");
        addTier3("Zhengzhou College of Finance and Economics", "郑州财经学院");
        addTier3("Zhengzhou Health College", "郑州卫生健康职业学院");
        addTier3("Zhengzhou Shengda University", "郑州升达经贸管理学院");
        addTier3("Zhengzhou Technology and Business University", "郑州工商学院");
        addTier3("Zhengzhou University of Industrial Technology", "郑州工业应用技术学院");
        addTier3("Zhengzhou University of Science and Technology", "郑州科技学院");
        addTier3("Zhijiang college of zhejiang university of technology", "浙江工业大学之江学院");
        addTier3("ZhiXing College of HuBei University", "湖北大学知行学院");
        addTier3("Zhonghuan Information College Tianjin University of Technology", "天津理工大学中环信息学院");
        addTier3("Zhongshan College of Dalian Medical University", "大连医科大学中山学院");
        addTier3("Zhongyuan Institute of Science and Technology", "中原科技学院");
        addTier3("Zhuhai College of Jilin University", "吉林大学珠海学院");
        addTier3("Zhujiang College, South China Agricultural University", "华南农业大学珠江学院");
    }

    private static void addTier1(String en, String cn) {
        TIER_1_UNIVERSITIES.add(en);
        TIER_1_UNIVERSITIES.add(cn);
    }

    private static void addTier2(String en, String cn) {
        TIER_2_UNIVERSITIES.add(en);
        TIER_2_UNIVERSITIES.add(cn);
    }

    private static void addTier3(String en, String cn) {
        TIER_3_UNIVERSITIES.add(en);
        TIER_3_UNIVERSITIES.add(cn);
    }

    @Override
    public String getSchoolName() {
        return "纽卡斯尔大学";
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
                            && s.getEnglishName().equalsIgnoreCase("Newcastle University"))
                    .findFirst()
                    .orElse(null);

            if (school == null) {
                log.warn("未在数据库中找到纽卡斯尔大学");
                return results;
            }
        }

        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 判断学生院校 Tier
        String studentTier = getUniversityTier(undergradSchool);

        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = extractMajorTokens(request);
        boolean hasMajorFilter = majorTokens != null && !majorTokens.isEmpty();
        if (majors != null && !majors.isEmpty() && hasMajorFilter) {
            List<Major> filteredMajors = filterMajorsByTokens(majors, majorTokens);
            if (!filteredMajors.isEmpty()) {
                majors = filteredMajors;
            }
        }
        if (majors == null) {
            majors = Collections.emptyList();
        }
        if (!hasMajorFilter && majors.size() > 10) {
            majors = majors.subList(0, 10);
        }

        for (Major major : majors) {
            // 1. 确定该专业属于低分档 (70/75) 还是高分档 (75/80)
            boolean isLowRequirement = isLowRequirementMajor(major.getName(), major.getEnglishName());

            // 2. 获取具体分数线
            double requiredGpa = getRequiredGpa(studentTier, isLowRequirement);

            // 3. 计算匹配度
            double matchScore = calculateMatchScore(studentInfo, requiredGpa, school, major.getName());
            String matchLevel = determineMatchLevel(matchScore);

            // 4. 生成分析报告
            String matchReason = generateMatchReason(studentInfo, studentTier, requiredGpa, isLowRequirement,
                    matchScore, major.getName());

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
            result.setAlgorithmStrategy("UK_NEWCASTLE_MATCHING_ALGORITHM");

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
     * 判断院校 Tier
     */
    private String getUniversityTier(String schoolName) {
        if (schoolName == null || schoolName.trim().isEmpty())
            return "UNKNOWN";
        String cleanName = schoolName.trim();

        if (checkSet(TIER_1_UNIVERSITIES, cleanName))
            return "Tier 1";
        if (checkSet(TIER_2_UNIVERSITIES, cleanName))
            return "Tier 2";
        if (checkSet(TIER_3_UNIVERSITIES, cleanName))
            return "Tier 3";

        // 默认逻辑：如果不在 T1/T2/T3，但名字包含 "大学" 且不包含 "学院"，大概率是公办 Tier 2
        // 为了稳妥，暂时归为 UNKNOWN，或者可以默认归为 Tier 2 (Loose Mode)
        // 这里保持严格模式
        return "UNKNOWN";
    }

    private boolean checkSet(Set<String> set, String name) {
        for (String s : set) {
            if (s.equalsIgnoreCase(name) || name.contains(s) || (s.length() > 4 && s.contains(name))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为低分要求专业 (T1&2=70%, T3&4=75%)
     * 依据：提供的四张图片中的专业列表
     */
    private boolean isLowRequirementMajor(String majorName, String majorEnName) {
        String name = (majorName + " " + (majorEnName != null ? majorEnName : "")).toLowerCase();

        // --- 1. 计算机类 ---
        // "MSc Computer Science" (转码项目) 是 70/75
        // "Advanced CS", "Cyber Security", "Cloud", "Data Science", "AI" 是 75/80 (High)
        if (name.contains("computer science")) {
            if (name.contains("advanced"))
                return false; // High
            return true; // MSc Computer Science is Low
        }
        if (name.contains("bioinformatics"))
            return true; // 生物信息学 70/75

        // --- 2. 工程类 (大部分为 70/75) ---
        // 包括：Mechanical, Civil, Electrical, Renewable Energy, Transport, Naval,
        // Automation, Microelectronics
        if (name.contains("engineering") || name.contains("renewable energy") ||
                name.contains("transport") || name.contains("automation") ||
                name.contains("electronics") || name.contains("electrical") ||
                name.contains("naval") || name.contains("marine")) {

            // 特例：Marine Conservation 是 75/80
            if (name.contains("conservation"))
                return false;
            return true;
        }
        // 水利、农业 (70/75)
        if (name.contains("hydrology") || name.contains("water management") || name.contains("agriculture"))
            return false; // Wait, image says "Sustainable Agriculture" is 75/80. Let's fix.
        if (name.contains("sustainable agriculture"))
            return false; // High
        if (name.contains("agricultural and environmental"))
            return false; // High (75/80)

        // --- 3. 科学类 ---
        // Drug Chemistry (70/75)
        if (name.contains("drug chemistry"))
            return true;
        // Chemistry (75/80), Psychology (75/80), MRes (75/80) -> All High

        // --- 4. 商科类 ---
        // 70/75: Accounting, Finance and Financial Analysis; Strategic Investment;
        // Financial Analytics; Banking and Finance; Finance;
        // Economics and Finance; Entrepreneurship.
        if (name.contains("banking") ||
                name.contains("entrepreneurship") ||
                name.contains("financial analysis") ||
                name.contains("strategic investment") ||
                name.contains("financial analytics")) {
            return true;
        }
        // 单独的 "Finance" 或 "MSc Finance"
        if (name.matches(".*\\bfinance\\b.*") && !name.contains("data science")) {
            // 需排除包含 "Economics" 的纯经济学 (75/80), 但 "Economics and Finance" 是 70/75
            if (name.contains("economics") && !name.contains("and finance"))
                return false;
            // "Data Science and Finance" 是 High (75/80) - To be approved but likely high
            return true;
        }

        // --- 5. 其他 ---
        // 特例: Digital Media, Technology and Society (数字传媒、科技与社会)
        // 图片显示此专业有两行，一行TBC，一行 70/75。我们按 70/75 处理。
        if (name.contains("digital media, technology and society"))
            return true;
        if (name.contains("oral sciences"))
            return true; // 口腔科学 70/75
        // 特例：Digital Media, Technology and Society 是 70/75
        if (name.contains("digital media, technology and society") || name.contains("数字传媒、科技与社会")) {
            return true;
        }

        // === Low Requirement Group (70% / 75%) ===

        // 1. Accounting / Finance / Banking
        if (name.contains("finance") || name.contains("accounting") || name.contains("banking") ||
                name.contains("金融") || name.contains("会计") || name.contains("银行")) {
            // 排除 "Economics" (如果是纯 Economics 是高分，但 Economics and Finance 是低分)
            // 图片显示: MSc Economics and Finance -> 70/75
            // MSc Economics -> 75/80
            if (name.contains("economics") && !name.contains("finance")) {
                return false; // Pure Economics is High
            }
            return true;
        }

        // 2. Entrepreneurship
        if (name.contains("entrepreneurship") || name.contains("创业")) {
            return true;
        }

        // 3. Computer Science (MSc Computer Science)
        if (name.contains("computer science") || name.contains("计算机科学")) {
            return true;
        }

        // 4. Engineering (Transport, Renewable, Biomedical, etc.)
        if (name.contains("engineering") || name.contains("工程")) {
            return true;
        }

        // 5. Science (Bio...)
        if (name.contains("biotechnology") || name.contains("bioinformatics") ||
                name.contains("生物科技") || name.contains("生物信息")) {
            return true;
        }
        // 默认为 High Requirement (75% / 80%)
        // 包括：Management, Marketing, HR, MBA, Logistics, E-Commerce, Digital Business,
        // Education, Media (Journalism/PR), Law, Arts, Sociology, Politics,
        // Statistics, Medical Stats, MRes Medicine.
        return false;
    }

    /**
     * 获取分数线
     */
    private double getRequiredGpa(String tier, boolean isLowReq) {
        if ("Tier 1&2".equals(tier)) {
            return isLowReq ? 70.0 : 75.0;
        } else {
            return isLowReq ? 75.0 : 80.0;
        }
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, School school,
            String majorName) {
        if (student.getGpa() == null)
            return 0.0;
        double gpa = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
        double score = 0.0;

        // 1. 均分评分
        if (gpa >= required + 2) {
            score += 85.0; // 稳妥
        } else if (gpa >= required) {
            score += 75.0; // 达标
        } else if (gpa >= required - 2) {
            score += 60.0; // 冲刺 (纽卡对分数卡得较严，但差一点有机会Argue)
        } else {
            score += 30.0; // 差距较大
        }

        // 2. 院校背景加分 (Tier 1/2 更有优势)
        String tier = getUniversityTier(student.getUndergraduateSchool());
        if ("Tier 1&2".equals(tier)) {
            score += 10.0;
        }

        // 3. 专业匹配度 (简单判断)
        String target = student.getTargetMajor() != null ? student.getTargetMajor() : "";
        if (majorName.contains(target)) {
            score += 5.0;
        }

        // 4. 特殊加分：CS转码专业对无背景友好
        if (majorName.contains("Computer Science") && !majorName.contains("Advanced")) {
            score += 5.0;
        }

        return Math.min(100.0, score);
    }

    private double convertGpaToPercentage(Double gpa, String scaleRaw) {
        if (gpa == null) {
            return 0.0;
        }
        if (scaleRaw == null || scaleRaw.trim().isEmpty() || "0".equals(scaleRaw.trim())) {
            return clamp(gpa, 0.0, 100.0);
        }
        String scale = scaleRaw.trim();
        if ("100".equals(scale)) {
            return clamp(gpa, 0.0, 100.0);
        }
        Double s;
        try {
            s = Double.parseDouble(scale);
        } catch (NumberFormatException e) {
            return clamp(gpa, 0.0, 100.0);
        }
        if (s <= 0.0) {
            return clamp(gpa, 0.0, 100.0);
        }
        return clamp((gpa / s) * 100.0, 0.0, 100.0);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private String determineMatchLevel(double score) {
        if (score >= 88)
            return "保底";
        if (score >= 75)
            return "稳妥";
        if (score >= 60)
            return "冲刺";
        return "不建议";
    }

    private String generateMatchReason(MatchingRequest.StudentInfoDTO student, String tier, double required,
            boolean isLowReq, double score, String majorName) {
        double gpaPercent = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
        String gpaText = student.getGpa() == null ? "未填写"
                : (student.getGpaScale() == null || student.getGpaScale().trim().isEmpty()
                        ? String.format("%.1f", student.getGpa())
                        : String.format("%.2f/%s", student.getGpa(), student.getGpaScale().trim()));

        StringBuilder sb = new StringBuilder();
        sb.append("【纽卡斯尔大学】");

        sb.append("您的本科院校属于 ").append(tier).append(" 类别。");

        sb.append("申请专业：").append(majorName).append("，属于");
        if (isLowReq) {
            sb.append("【标准一 (Low)】档位（如工程、计算机转码、部分金融等），");
        } else {
            sb.append("【标准二 (High)】档位（如管理、市场、传媒、教育、法学等），");
        }

        sb.append("均分要求为 ").append((int) required).append("%。");
        sb.append("您的均分为 ").append(String.format("%.1f", gpaPercent)).append("%（").append(gpaText).append("）。");

        if (gpaPercent >= required) {
            sb.append(" 成绩达标，录取机会较大。");
        } else if (gpaPercent >= required - 2) {
            sb.append(" 成绩略有差距，建议提升软背景或作为冲刺申请。");
        } else {
            sb.append(" 成绩未达标，建议考虑其他院校或预科。");
        }

        return sb.toString();
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 88)
            return 0.95;
        if (score >= 75)
            return 0.85;
        if (score >= 60)
            return 0.40;
        return 0.10;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
