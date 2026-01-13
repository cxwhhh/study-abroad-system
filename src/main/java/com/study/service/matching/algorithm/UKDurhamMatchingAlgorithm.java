package com.study.service.matching.algorithm;

import com.study.service.matching.MatchingDataService;
import com.study.service.matching.MatchingResult;
import com.study.service.matching.dto.MatchingRequest;
import com.study.service.schools.School;
import com.study.service.major.Major;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 杜伦大学匹配算法
 * 根据杜伦大学的录取要求进行匹配
 */
@Service
@SuppressWarnings("unused")
public class UKDurhamMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKDurhamMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    // 杜伦大学官方认可的A类大学名单
    private static final Set<String> BAND_A_UNIVERSITIES = new HashSet<>();
    static {
        BAND_A_UNIVERSITIES.add("北京航空航天大学");
        BAND_A_UNIVERSITIES.add("北京理工大学");
        BAND_A_UNIVERSITIES.add("北京交通大学");
        BAND_A_UNIVERSITIES.add("北京师范大学");
        BAND_A_UNIVERSITIES.add("北京化工大学");
        BAND_A_UNIVERSITIES.add("北京邮电大学");
        BAND_A_UNIVERSITIES.add("中南大学");
        BAND_A_UNIVERSITIES.add("中央财经大学");
        BAND_A_UNIVERSITIES.add("中国农业大学");
        BAND_A_UNIVERSITIES.add("重庆大学");
        BAND_A_UNIVERSITIES.add("大连理工大学");
        BAND_A_UNIVERSITIES.add("华东师范大学");
        BAND_A_UNIVERSITIES.add("华东理工大学");
        BAND_A_UNIVERSITIES.add("复旦大学");
        BAND_A_UNIVERSITIES.add("哈尔滨工业大学");
        BAND_A_UNIVERSITIES.add("华中科技大学");
        BAND_A_UNIVERSITIES.add("湖南大学");
        BAND_A_UNIVERSITIES.add("吉林大学");
        BAND_A_UNIVERSITIES.add("暨南大学");
        BAND_A_UNIVERSITIES.add("南京大学");
        BAND_A_UNIVERSITIES.add("南京航空航天大学");
        BAND_A_UNIVERSITIES.add("南京理工大学");
        BAND_A_UNIVERSITIES.add("南开大学");
        BAND_A_UNIVERSITIES.add("国防科技大学");
        BAND_A_UNIVERSITIES.add("东北大学");
        BAND_A_UNIVERSITIES.add("西北工业大学");
        BAND_A_UNIVERSITIES.add("北京大学");
        BAND_A_UNIVERSITIES.add("中国人民大学");
        BAND_A_UNIVERSITIES.add("山东大学");
        BAND_A_UNIVERSITIES.add("上海交通大学");
        BAND_A_UNIVERSITIES.add("上海科技大学");
        BAND_A_UNIVERSITIES.add("上海大学");
        BAND_A_UNIVERSITIES.add("上海财经大学");
        BAND_A_UNIVERSITIES.add("四川大学");
        BAND_A_UNIVERSITIES.add("苏州大学");
        BAND_A_UNIVERSITIES.add("华南理工大学");
        BAND_A_UNIVERSITIES.add("东南大学");

        // Image 2 List
        BAND_A_UNIVERSITIES.add("南方科技大学");
        BAND_A_UNIVERSITIES.add("中山大学");
        BAND_A_UNIVERSITIES.add("天津大学");
        BAND_A_UNIVERSITIES.add("同济大学");
        BAND_A_UNIVERSITIES.add("清华大学");
        BAND_A_UNIVERSITIES.add("中国科学院大学");
        BAND_A_UNIVERSITIES.add("中国社会科学院大学");
        BAND_A_UNIVERSITIES.add("电子科技大学");
        BAND_A_UNIVERSITIES.add("对外经济贸易大学");
        BAND_A_UNIVERSITIES.add("北京科技大学");
        BAND_A_UNIVERSITIES.add("中国科学技术大学");
        BAND_A_UNIVERSITIES.add("武汉大学");
        BAND_A_UNIVERSITIES.add("武汉理工大学");
        BAND_A_UNIVERSITIES.add("西安交通大学");
        BAND_A_UNIVERSITIES.add("厦门大学");
        BAND_A_UNIVERSITIES.add("西安电子科技大学");
        BAND_A_UNIVERSITIES.add("浙江大学");
        // Add English Names (Corresponding to above)
        BAND_A_UNIVERSITIES.add("Beihang University"); // 北京航空航天大学
        BAND_A_UNIVERSITIES.add("Beijing Institute of Technology"); // 北京理工大学
        BAND_A_UNIVERSITIES.add("Beijing Jiaotong University"); // 北京交通大学
        BAND_A_UNIVERSITIES.add("Beijing Normal University"); // 北京师范大学
        BAND_A_UNIVERSITIES.add("Beijing University of Chemical Technology"); // 北京化工大学
        BAND_A_UNIVERSITIES.add("Beijing University of Posts and Telecommunications"); // 北京邮电大学
        BAND_A_UNIVERSITIES.add("Central South University"); // 中南大学
        BAND_A_UNIVERSITIES.add("Central University of Finance and Economics"); // 中央财经大学
        BAND_A_UNIVERSITIES.add("China Agricultural University"); // 中国农业大学
        BAND_A_UNIVERSITIES.add("Chongqing University"); // 重庆大学
        BAND_A_UNIVERSITIES.add("Dalian University of Technology"); // 大连理工大学
        BAND_A_UNIVERSITIES.add("East China Normal University"); // 华东师范大学
        BAND_A_UNIVERSITIES.add("East China University of Science and Technology"); // 华东理工大学
        BAND_A_UNIVERSITIES.add("Fudan University"); // 复旦大学
        BAND_A_UNIVERSITIES.add("Harbin Institute of Technology"); // 哈尔滨工业大学
        BAND_A_UNIVERSITIES.add("Huazhong University of Science and Technology"); // 华中科技大学
        BAND_A_UNIVERSITIES.add("Hunan University"); // 湖南大学
        BAND_A_UNIVERSITIES.add("Jilin University"); // 吉林大学
        BAND_A_UNIVERSITIES.add("Jinan University"); // 暨南大学
        BAND_A_UNIVERSITIES.add("Nanjing University"); // 南京大学
        BAND_A_UNIVERSITIES.add("Nanjing University of Aeronautics and Astronautics"); // 南京航空航天大学
        BAND_A_UNIVERSITIES.add("Nanjing University of Science and Technology"); // 南京理工大学
        BAND_A_UNIVERSITIES.add("Nankai University"); // 南开大学
        BAND_A_UNIVERSITIES.add("National University of Defense Technology"); // 国防科技大学
        BAND_A_UNIVERSITIES.add("Northeastern University"); // 东北大学
        BAND_A_UNIVERSITIES.add("Northwestern Polytechnical University"); // 西北工业大学
        BAND_A_UNIVERSITIES.add("Peking University"); // 北京大学
        BAND_A_UNIVERSITIES.add("Renmin University of China"); // 中国人民大学
        BAND_A_UNIVERSITIES.add("Shandong University"); // 山东大学
        BAND_A_UNIVERSITIES.add("Shanghai Jiao Tong University"); // 上海交通大学
        BAND_A_UNIVERSITIES.add("ShanghaiTech University"); // 上海科技大学
        BAND_A_UNIVERSITIES.add("Shanghai University"); // 上海大学
        BAND_A_UNIVERSITIES.add("Shanghai University of Finance and Economics"); // 上海财经大学
        BAND_A_UNIVERSITIES.add("Sichuan University"); // 四川大学
        BAND_A_UNIVERSITIES.add("Soochow University"); // 苏州大学
        BAND_A_UNIVERSITIES.add("South China University of Technology"); // 华南理工大学
        BAND_A_UNIVERSITIES.add("Southeast University"); // 东南大学
        BAND_A_UNIVERSITIES.add("Southern University of Science and Technology"); // 南方科技大学
        BAND_A_UNIVERSITIES.add("Sun Yat-sen University"); // 中山大学
        BAND_A_UNIVERSITIES.add("Tianjin University"); // 天津大学
        BAND_A_UNIVERSITIES.add("Tongji University"); // 同济大学
        BAND_A_UNIVERSITIES.add("Tsinghua University"); // 清华大学
        BAND_A_UNIVERSITIES.add("University of Chinese Academy of Sciences"); // 中国科学院大学
        BAND_A_UNIVERSITIES.add("University of Chinese Academy of Social Sciences"); // 中国社会科学院大学
        BAND_A_UNIVERSITIES.add("University of Electronic Science and Technology of China"); // 电子科技大学
        BAND_A_UNIVERSITIES.add("University of International Business and Economics"); // 对外经济贸易大学
        BAND_A_UNIVERSITIES.add("University of Science and Technology Beijing"); // 北京科技大学
        BAND_A_UNIVERSITIES.add("University of Science and Technology of China"); // 中国科学技术大学
        BAND_A_UNIVERSITIES.add("Wuhan University"); // 武汉大学
        BAND_A_UNIVERSITIES.add("Wuhan University of Technology"); // 武汉理工大学
        BAND_A_UNIVERSITIES.add("Xi'an Jiaotong University"); // 西安交通大学
        BAND_A_UNIVERSITIES.add("Xiamen University"); // 厦门大学
        BAND_A_UNIVERSITIES.add("Xidian University"); // 西安电子科技大学
        BAND_A_UNIVERSITIES.add("Zhejiang University"); // 浙江大学
    }
    // 音乐学院名单
    private static final Set<String> MUSIC_CONSERVATORIES = new HashSet<>(Arrays.asList(
            "Shanghai Conservatory of Music", "上海音乐学院",
            "Shenyang Conservatory of Music", "沈阳音乐学院",
            "Xi'an Conservatory of Music", "西安音乐学院",
            "Wuhan Conservatory of Music", "武汉音乐学院",
            "Sichuan Conservatory of Music", "四川音乐学院",
            "Xinghai Conservatory of Music", "星海音乐学院",
            "Tianjin Conservatory of Music", "天津音乐学院",
            "Zhejiang Conservatory of Music", "浙江音乐学院",
            "Harbin Conservatory of Music", "哈尔滨音乐学院"));

    // B类大学名单 - 将分批添加
    private static final Set<String> BAND_B_UNIVERSITIES = new HashSet<>();
    static {
        // --- Anhui (安徽) ---
        BAND_B_UNIVERSITIES.add("Anhui Medical University");
        BAND_B_UNIVERSITIES.add("安徽医科大学");
        BAND_B_UNIVERSITIES.add("Anhui Normal University");
        BAND_B_UNIVERSITIES.add("安徽师范大学");
        BAND_B_UNIVERSITIES.add("Anhui University");
        BAND_B_UNIVERSITIES.add("安徽大学");
        BAND_B_UNIVERSITIES.add("Anhui University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("安徽财经大学");
        BAND_B_UNIVERSITIES.add("Anhui University of Science and Technology");
        BAND_B_UNIVERSITIES.add("安徽理工大学");
        BAND_B_UNIVERSITIES.add("Anhui University of Technology");
        BAND_B_UNIVERSITIES.add("安徽工业大学");

        // --- Beijing (北京) ---
        BAND_B_UNIVERSITIES.add("Beijing Film Academy");
        BAND_B_UNIVERSITIES.add("北京电影学院");
        BAND_B_UNIVERSITIES.add("Beijing Foreign Studies University");
        BAND_B_UNIVERSITIES.add("北京外国语大学");
        BAND_B_UNIVERSITIES.add("Beijing Forestry University");
        BAND_B_UNIVERSITIES.add("北京林业大学");
        BAND_B_UNIVERSITIES.add("Beijing International Studies University");
        BAND_B_UNIVERSITIES.add("北京第二外国语学院");
        BAND_B_UNIVERSITIES.add("Beijing Language and Culture University");
        BAND_B_UNIVERSITIES.add("北京语言大学");
        BAND_B_UNIVERSITIES.add("Beijing Sport University");
        BAND_B_UNIVERSITIES.add("北京体育大学");
        BAND_B_UNIVERSITIES.add("Beijing Technology and Business University");
        BAND_B_UNIVERSITIES.add("北京工商大学");
        BAND_B_UNIVERSITIES.add("Beijing University of Chinese Medicine");
        BAND_B_UNIVERSITIES.add("北京中医药大学");
        BAND_B_UNIVERSITIES.add("Beijing University of Civil Engineering and Architecture");
        BAND_B_UNIVERSITIES.add("北京建筑大学");
        BAND_B_UNIVERSITIES.add("Beijing University of Technology");
        BAND_B_UNIVERSITIES.add("北京工业大学");
        BAND_B_UNIVERSITIES.add("Bohai University");
        BAND_B_UNIVERSITIES.add("渤海大学");

        // --- Capital / Central (首都/中央) ---
        BAND_B_UNIVERSITIES.add("Capital Medical University");
        BAND_B_UNIVERSITIES.add("首都医科大学");
        BAND_B_UNIVERSITIES.add("Capital Normal University");
        BAND_B_UNIVERSITIES.add("首都师范大学");
        BAND_B_UNIVERSITIES.add("Capital University of Economics and Business");
        BAND_B_UNIVERSITIES.add("首都经济贸易大学");
        BAND_B_UNIVERSITIES.add("Central Academy of Fine Arts");
        BAND_B_UNIVERSITIES.add("中央美术学院");
        BAND_B_UNIVERSITIES.add("Central China Normal University");
        BAND_B_UNIVERSITIES.add("华中师范大学");
        BAND_B_UNIVERSITIES.add("Central Conservatory of Music");
        BAND_B_UNIVERSITIES.add("中央音乐学院");

        // --- C (其他) ---
        BAND_B_UNIVERSITIES.add("Chang'an University");
        BAND_B_UNIVERSITIES.add("长安大学");
        BAND_B_UNIVERSITIES.add("Changsha University of Science and Technology");
        BAND_B_UNIVERSITIES.add("长沙理工大学");
        BAND_B_UNIVERSITIES.add("Changzhou University");
        BAND_B_UNIVERSITIES.add("常州大学");
        BAND_B_UNIVERSITIES.add("China Academy of Art");
        BAND_B_UNIVERSITIES.add("中国美术学院");
        BAND_B_UNIVERSITIES.add("China Conservatory of Music");
        BAND_B_UNIVERSITIES.add("中国音乐学院");
        BAND_B_UNIVERSITIES.add("China Foreign Affairs University");
        BAND_B_UNIVERSITIES.add("外交学院");
        BAND_B_UNIVERSITIES.add("China Jiliang University");
        BAND_B_UNIVERSITIES.add("中国计量大学");
        BAND_B_UNIVERSITIES.add("China Medical University");
        BAND_B_UNIVERSITIES.add("中国医科大学");
        BAND_B_UNIVERSITIES.add("China Pharmaceutical University");
        BAND_B_UNIVERSITIES.add("中国药科大学");
        BAND_B_UNIVERSITIES.add("China Three Gorges University");
        BAND_B_UNIVERSITIES.add("三峡大学");
        BAND_B_UNIVERSITIES.add("China University of Geosciences (Beijing and Wuhan)");
        BAND_B_UNIVERSITIES.add("中国地质大学");
        BAND_B_UNIVERSITIES.add("China University of Mining and Technology (Beijing and Xuzhou)");
        BAND_B_UNIVERSITIES.add("中国矿业大学");
        BAND_B_UNIVERSITIES.add("China University of Petroleum (Beijing and Qingdao)");
        BAND_B_UNIVERSITIES.add("中国石油大学");
        BAND_B_UNIVERSITIES.add("China University of Political Science and Law");
        BAND_B_UNIVERSITIES.add("中国政法大学");
        BAND_B_UNIVERSITIES.add("Chongqing Jiaotong University");
        BAND_B_UNIVERSITIES.add("重庆交通大学");
        BAND_B_UNIVERSITIES.add("Chongqing Medical University");
        BAND_B_UNIVERSITIES.add("重庆医科大学");
        BAND_B_UNIVERSITIES.add("Chongqing Normal University");
        BAND_B_UNIVERSITIES.add("重庆师范大学");
        BAND_B_UNIVERSITIES.add("Chongqing Technology and Business University");
        BAND_B_UNIVERSITIES.add("重庆工商大学");
        BAND_B_UNIVERSITIES.add("Chongqing University of Posts and Telecommunications");
        BAND_B_UNIVERSITIES.add("重庆邮电大学");
        BAND_B_UNIVERSITIES.add("Civil Aviation University of China");
        BAND_B_UNIVERSITIES.add("中国民航大学");
        BAND_B_UNIVERSITIES.add("Communication University of China");
        BAND_B_UNIVERSITIES.add("中国传媒大学");

        // --- D ---
        BAND_B_UNIVERSITIES.add("Dalian Maritime University");
        BAND_B_UNIVERSITIES.add("大连海事大学");
        BAND_B_UNIVERSITIES.add("Dalian Medical University");
        BAND_B_UNIVERSITIES.add("大连医科大学");
        BAND_B_UNIVERSITIES.add("Dongbei University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("东北财经大学");
        BAND_B_UNIVERSITIES.add("Donghua University");
        BAND_B_UNIVERSITIES.add("东华大学");

        // --- E - F ---
        BAND_B_UNIVERSITIES.add("East China University of Political Science and Law (ECUPL)");
        BAND_B_UNIVERSITIES.add("华东政法大学");
        BAND_B_UNIVERSITIES.add("Fujian Normal University");
        BAND_B_UNIVERSITIES.add("福建师范大学");
        BAND_B_UNIVERSITIES.add("Fuzhou University");
        BAND_B_UNIVERSITIES.add("福州大学");

        // --- G ---
        BAND_B_UNIVERSITIES.add("Guangdong University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("广东财经大学");
        BAND_B_UNIVERSITIES.add("Guangdong University of Foreign Studies");
        BAND_B_UNIVERSITIES.add("广东外语外贸大学");
        BAND_B_UNIVERSITIES.add("Guangdong University of Technology");
        BAND_B_UNIVERSITIES.add("广东工业大学");
        BAND_B_UNIVERSITIES.add("Guangxi Medical University");
        BAND_B_UNIVERSITIES.add("广西医科大学");
        BAND_B_UNIVERSITIES.add("Guangxi Normal University");
        BAND_B_UNIVERSITIES.add("广西师范大学");
        BAND_B_UNIVERSITIES.add("Guangxi University");
        BAND_B_UNIVERSITIES.add("广西大学");
        BAND_B_UNIVERSITIES.add("Guangzhou Medical University");
        BAND_B_UNIVERSITIES.add("广州医科大学");
        BAND_B_UNIVERSITIES.add("Guangzhou University");
        BAND_B_UNIVERSITIES.add("广州大学");
        BAND_B_UNIVERSITIES.add("Guizhou University");
        BAND_B_UNIVERSITIES.add("贵州大学");

        // --- H ---
        BAND_B_UNIVERSITIES.add("Hainan University");
        BAND_B_UNIVERSITIES.add("海南大学");
        BAND_B_UNIVERSITIES.add("Hangzhou Dianzi University");
        BAND_B_UNIVERSITIES.add("杭州电子科技大学");
        BAND_B_UNIVERSITIES.add("Hangzhou Normal University");
        BAND_B_UNIVERSITIES.add("杭州师范大学");
        BAND_B_UNIVERSITIES.add("Harbin Engineering University");
        BAND_B_UNIVERSITIES.add("哈尔滨工程大学");
        BAND_B_UNIVERSITIES.add("Harbin Medical University");
        BAND_B_UNIVERSITIES.add("哈尔滨医科大学");
        BAND_B_UNIVERSITIES.add("Harbin Normal University");
        BAND_B_UNIVERSITIES.add("哈尔滨师范大学");
        BAND_B_UNIVERSITIES.add("Hebei Agricultural University");
        BAND_B_UNIVERSITIES.add("河北农业大学");
        BAND_B_UNIVERSITIES.add("Hebei Medical University");
        BAND_B_UNIVERSITIES.add("河北医科大学");
        BAND_B_UNIVERSITIES.add("Hebei Normal University");
        BAND_B_UNIVERSITIES.add("河北师范大学");
        BAND_B_UNIVERSITIES.add("Hebei University");
        BAND_B_UNIVERSITIES.add("河北大学");
        BAND_B_UNIVERSITIES.add("Hebei University of Science and Technology");
        BAND_B_UNIVERSITIES.add("河北科技大学");
        BAND_B_UNIVERSITIES.add("Hebei University of Technology");
        BAND_B_UNIVERSITIES.add("河北工业大学");
        BAND_B_UNIVERSITIES.add("Hefei University of Technology");
        BAND_B_UNIVERSITIES.add("合肥工业大学");
        BAND_B_UNIVERSITIES.add("Heilongjiang University");
        BAND_B_UNIVERSITIES.add("黑龙江大学");
        BAND_B_UNIVERSITIES.add("Henan Normal University");
        BAND_B_UNIVERSITIES.add("河南师范大学");
        BAND_B_UNIVERSITIES.add("Henan University");
        BAND_B_UNIVERSITIES.add("河南大学");
        BAND_B_UNIVERSITIES.add("Hohai University");
        BAND_B_UNIVERSITIES.add("河海大学");
        BAND_B_UNIVERSITIES.add("Huaqiao University");
        BAND_B_UNIVERSITIES.add("华侨大学");
        BAND_B_UNIVERSITIES.add("Huazhong Agricultural University");
        BAND_B_UNIVERSITIES.add("华中农业大学");
        BAND_B_UNIVERSITIES.add("Hubei University");
        BAND_B_UNIVERSITIES.add("湖北大学");
        BAND_B_UNIVERSITIES.add("Hubei University of Technology");
        BAND_B_UNIVERSITIES.add("湖北工业大学");
        BAND_B_UNIVERSITIES.add("Hunan Agricultural University");
        BAND_B_UNIVERSITIES.add("湖南农业大学");
        BAND_B_UNIVERSITIES.add("Hunan Normal University");
        BAND_B_UNIVERSITIES.add("湖南师范大学");

        // --- I - N ---
        BAND_B_UNIVERSITIES.add("Inner Mongolia University");
        BAND_B_UNIVERSITIES.add("内蒙古大学");
        BAND_B_UNIVERSITIES.add("Jiangnan University");
        BAND_B_UNIVERSITIES.add("江南大学");
        BAND_B_UNIVERSITIES.add("Jiangsu Normal University");
        BAND_B_UNIVERSITIES.add("江苏师范大学");
        BAND_B_UNIVERSITIES.add("Jiangsu University");
        BAND_B_UNIVERSITIES.add("江苏大学");
        BAND_B_UNIVERSITIES.add("Jiangsu University of Science and Technology");
        BAND_B_UNIVERSITIES.add("江苏科技大学");
        BAND_B_UNIVERSITIES.add("Jiangxi Normal University");
        BAND_B_UNIVERSITIES.add("江西师范大学");
        BAND_B_UNIVERSITIES.add("Jiangxi University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("江西财经大学");
        BAND_B_UNIVERSITIES.add("Jiangxi University of Science and Technology");
        BAND_B_UNIVERSITIES.add("江西理工大学");
        BAND_B_UNIVERSITIES.add("Jimei University");
        BAND_B_UNIVERSITIES.add("集美大学");
        BAND_B_UNIVERSITIES.add("Lanzhou University");
        BAND_B_UNIVERSITIES.add("兰州大学");
        BAND_B_UNIVERSITIES.add("Lanzhou University of Technology");
        BAND_B_UNIVERSITIES.add("兰州理工大学");
        BAND_B_UNIVERSITIES.add("Liaoning Normal University");
        BAND_B_UNIVERSITIES.add("辽宁师范大学");
        BAND_B_UNIVERSITIES.add("Liaoning University");
        BAND_B_UNIVERSITIES.add("辽宁大学");
        BAND_B_UNIVERSITIES.add("Liaoning University of Technology");
        BAND_B_UNIVERSITIES.add("辽宁工业大学");
        BAND_B_UNIVERSITIES.add("Ludong University");
        BAND_B_UNIVERSITIES.add("鲁东大学");
        BAND_B_UNIVERSITIES.add("Minzu University of China");
        BAND_B_UNIVERSITIES.add("中央民族大学");
        BAND_B_UNIVERSITIES.add("Nanchang University");
        BAND_B_UNIVERSITIES.add("南昌大学");
        BAND_B_UNIVERSITIES.add("Nanjing Agricultural University");
        BAND_B_UNIVERSITIES.add("南京农业大学");
        BAND_B_UNIVERSITIES.add("Nanjing Audit University");
        BAND_B_UNIVERSITIES.add("南京审计大学");
        BAND_B_UNIVERSITIES.add("Nanjing Forestry University");
        BAND_B_UNIVERSITIES.add("南京林业大学");

        // --- N (Continued) ---
        BAND_B_UNIVERSITIES.add("Nanjing Medical University");
        BAND_B_UNIVERSITIES.add("南京医科大学");
        BAND_B_UNIVERSITIES.add("Nanjing Normal University");
        BAND_B_UNIVERSITIES.add("南京师范大学");
        BAND_B_UNIVERSITIES.add("Nanjing Tech University");
        BAND_B_UNIVERSITIES.add("南京工业大学");
        BAND_B_UNIVERSITIES.add("Nanjing University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("南京财经大学");
        BAND_B_UNIVERSITIES.add("Nanjing University of Information Science and Technology");
        BAND_B_UNIVERSITIES.add("南京信息工程大学");
        BAND_B_UNIVERSITIES.add("Nanjing University of Posts and Telecommunications");
        BAND_B_UNIVERSITIES.add("南京邮电大学");
        BAND_B_UNIVERSITIES.add("Nantong University");
        BAND_B_UNIVERSITIES.add("南通大学");
        BAND_B_UNIVERSITIES.add("Ningbo University");
        BAND_B_UNIVERSITIES.add("宁波大学");
        BAND_B_UNIVERSITIES.add("Ningxia University");
        BAND_B_UNIVERSITIES.add("宁夏大学");
        BAND_B_UNIVERSITIES.add("North China Electric Power University");
        BAND_B_UNIVERSITIES.add("华北电力大学");
        BAND_B_UNIVERSITIES.add("North China University of Technology");
        BAND_B_UNIVERSITIES.add("北方工业大学");
        BAND_B_UNIVERSITIES.add("Northeast Agricultural University");
        BAND_B_UNIVERSITIES.add("东北农业大学");
        BAND_B_UNIVERSITIES.add("Northeast Forestry University");
        BAND_B_UNIVERSITIES.add("东北林业大学");
        BAND_B_UNIVERSITIES.add("Northeast Normal University");
        BAND_B_UNIVERSITIES.add("东北师范大学");
        BAND_B_UNIVERSITIES.add("Northwest A&F University");
        BAND_B_UNIVERSITIES.add("西北农林科技大学");
        BAND_B_UNIVERSITIES.add("Northwest University");
        BAND_B_UNIVERSITIES.add("西北大学");
        BAND_B_UNIVERSITIES.add("Northwest University of Political Science and Law");
        BAND_B_UNIVERSITIES.add("西北政法大学");

        // --- O ---
        BAND_B_UNIVERSITIES.add("Ocean University of China");
        BAND_B_UNIVERSITIES.add("中国海洋大学");

        // --- P ---
        BAND_B_UNIVERSITIES.add("People's Public Security University of China");
        BAND_B_UNIVERSITIES.add("中国人民公安大学");

        // --- Q ---
        BAND_B_UNIVERSITIES.add("Qingdao University");
        BAND_B_UNIVERSITIES.add("青岛大学");
        BAND_B_UNIVERSITIES.add("Qingdao University of Science and Technology");
        BAND_B_UNIVERSITIES.add("青岛科技大学");
        BAND_B_UNIVERSITIES.add("Qinghai University");
        BAND_B_UNIVERSITIES.add("青海大学");
        BAND_B_UNIVERSITIES.add("Qufu Normal University");
        BAND_B_UNIVERSITIES.add("曲阜师范大学");

        // --- S ---
        BAND_B_UNIVERSITIES.add("Shaanxi Normal University");
        BAND_B_UNIVERSITIES.add("陕西师范大学");
        BAND_B_UNIVERSITIES.add("Shandong Normal University");
        BAND_B_UNIVERSITIES.add("山东师范大学");
        BAND_B_UNIVERSITIES.add("Shandong University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("山东财经大学");
        BAND_B_UNIVERSITIES.add("Shandong University of Science and Technology");
        BAND_B_UNIVERSITIES.add("山东科技大学");
        BAND_B_UNIVERSITIES.add("Shanghai International Studies University");
        BAND_B_UNIVERSITIES.add("上海外国语大学");
        BAND_B_UNIVERSITIES.add("Shanghai Maritime University");
        BAND_B_UNIVERSITIES.add("上海海事大学");
        BAND_B_UNIVERSITIES.add("Shanghai Normal University");
        BAND_B_UNIVERSITIES.add("上海师范大学");
        BAND_B_UNIVERSITIES.add("Shanghai Ocean University");
        BAND_B_UNIVERSITIES.add("上海海洋大学");
        BAND_B_UNIVERSITIES.add("Shanghai Theatre Academy");
        BAND_B_UNIVERSITIES.add("上海戏剧学院");
        BAND_B_UNIVERSITIES.add("Shanghai University of International Business and Economics");
        BAND_B_UNIVERSITIES.add("上海对外经贸大学");
        BAND_B_UNIVERSITIES.add("Shanghai University of Political Science and Law");
        BAND_B_UNIVERSITIES.add("上海政法学院");
        BAND_B_UNIVERSITIES.add("Shanghai University of Traditional Chinese Medicine");
        BAND_B_UNIVERSITIES.add("上海中医药大学");
        BAND_B_UNIVERSITIES.add("Shantou University");
        BAND_B_UNIVERSITIES.add("汕头大学");
        BAND_B_UNIVERSITIES.add("Shanxi University");
        BAND_B_UNIVERSITIES.add("山西大学");
        BAND_B_UNIVERSITIES.add("Shanxi University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("山西财经大学");
        BAND_B_UNIVERSITIES.add("Shenyang Aerospace University");
        BAND_B_UNIVERSITIES.add("沈阳航空航天大学");
        BAND_B_UNIVERSITIES.add("Shenyang Pharmaceutical University");
        BAND_B_UNIVERSITIES.add("沈阳药科大学");
        BAND_B_UNIVERSITIES.add("Shenyang University of Technology");
        BAND_B_UNIVERSITIES.add("沈阳工业大学");
        BAND_B_UNIVERSITIES.add("Shenzhen University");
        BAND_B_UNIVERSITIES.add("深圳大学");
        BAND_B_UNIVERSITIES.add("Shihezi University");
        BAND_B_UNIVERSITIES.add("石河子大学");
        BAND_B_UNIVERSITIES.add("Shijiazhuang Tiedao University");
        BAND_B_UNIVERSITIES.add("石家庄铁道大学");
        BAND_B_UNIVERSITIES.add("Sichuan Agricultural University");
        BAND_B_UNIVERSITIES.add("四川农业大学");
        BAND_B_UNIVERSITIES.add("Sichuan International Studies University");
        BAND_B_UNIVERSITIES.add("四川外国语大学");
        BAND_B_UNIVERSITIES.add("South China Agricultural University");
        BAND_B_UNIVERSITIES.add("华南农业大学");
        BAND_B_UNIVERSITIES.add("South China Normal University");
        BAND_B_UNIVERSITIES.add("华南师范大学");
        BAND_B_UNIVERSITIES.add("South-Central Minzu University");
        BAND_B_UNIVERSITIES.add("中南民族大学");
        BAND_B_UNIVERSITIES.add("Southern Medical University");
        BAND_B_UNIVERSITIES.add("南方医科大学");
        BAND_B_UNIVERSITIES.add("Southwest Jiaotong University");
        BAND_B_UNIVERSITIES.add("西南交通大学");
        BAND_B_UNIVERSITIES.add("Southwest Petroleum University");
        BAND_B_UNIVERSITIES.add("西南石油大学");
        BAND_B_UNIVERSITIES.add("Southwest University");
        BAND_B_UNIVERSITIES.add("西南大学");
        BAND_B_UNIVERSITIES.add("Southwest University of Political Science and Law");
        BAND_B_UNIVERSITIES.add("西南政法大学");
        BAND_B_UNIVERSITIES.add("Southwestern University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("西南财经大学");
        BAND_B_UNIVERSITIES.add("Suzhou University of Science and Technology");
        BAND_B_UNIVERSITIES.add("苏州科技大学");

        // --- T ---
        BAND_B_UNIVERSITIES.add("Taiyuan University of Technology");
        BAND_B_UNIVERSITIES.add("太原理工大学");
        BAND_B_UNIVERSITIES.add("The Central Academy of Drama");
        BAND_B_UNIVERSITIES.add("中央戏剧学院");
        BAND_B_UNIVERSITIES.add("The Fourth Military Medical University");
        BAND_B_UNIVERSITIES.add("第四军医大学");
        BAND_B_UNIVERSITIES.add("Naval Medical University / The Second Military Medical University");
        BAND_B_UNIVERSITIES.add("海军军医大学");
        BAND_B_UNIVERSITIES.add("Army Medical University / The Third Military Medical University");
        BAND_B_UNIVERSITIES.add("陆军军医大学");
        BAND_B_UNIVERSITIES.add("Tiangong University");
        BAND_B_UNIVERSITIES.add("天津工业大学");
        BAND_B_UNIVERSITIES.add("Tianjin Medical University");
        BAND_B_UNIVERSITIES.add("天津医科大学");
        BAND_B_UNIVERSITIES.add("Tianjin Normal University");
        BAND_B_UNIVERSITIES.add("天津师范大学");
        BAND_B_UNIVERSITIES.add("Tianjin University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("天津财经大学");
        BAND_B_UNIVERSITIES.add("Tianjin University of Science and Technology");
        BAND_B_UNIVERSITIES.add("天津科技大学");
        BAND_B_UNIVERSITIES.add("Tianjin University of Technology");
        BAND_B_UNIVERSITIES.add("天津理工大学");
        BAND_B_UNIVERSITIES.add("Tibet University");
        BAND_B_UNIVERSITIES.add("西藏大学");

        // --- U ---
        BAND_B_UNIVERSITIES.add("University of International Relations");
        BAND_B_UNIVERSITIES.add("国际关系学院");
        BAND_B_UNIVERSITIES.add("University of Jinan (in Shandong)");
        BAND_B_UNIVERSITIES.add("济南大学");
        BAND_B_UNIVERSITIES.add("University of Shanghai for Science and Technology");
        BAND_B_UNIVERSITIES.add("上海理工大学");

        // --- W ---
        BAND_B_UNIVERSITIES.add("Wenzhou Medical University");
        BAND_B_UNIVERSITIES.add("温州医科大学");
        BAND_B_UNIVERSITIES.add("Wenzhou University");
        BAND_B_UNIVERSITIES.add("温州大学");
        BAND_B_UNIVERSITIES.add("Wuhan Institute of Technology");
        BAND_B_UNIVERSITIES.add("武汉工程大学");
        BAND_B_UNIVERSITIES.add("Wuhan University of Science and Technology");
        BAND_B_UNIVERSITIES.add("武汉科技大学");

        // --- X ---
        BAND_B_UNIVERSITIES.add("Xi'an University of Architecture and Technology");
        BAND_B_UNIVERSITIES.add("西安建筑科技大学");
        BAND_B_UNIVERSITIES.add("Xi'an University of Posts and Telecommunications");
        BAND_B_UNIVERSITIES.add("西安邮电大学");
        BAND_B_UNIVERSITIES.add("Xi'an University of Science and Technology");
        BAND_B_UNIVERSITIES.add("西安科技大学");
        BAND_B_UNIVERSITIES.add("Xi'an University of Technology");
        BAND_B_UNIVERSITIES.add("西安理工大学");
        BAND_B_UNIVERSITIES.add("Xiangtan University");
        BAND_B_UNIVERSITIES.add("湘潭大学");
        BAND_B_UNIVERSITIES.add("Xinjiang University");
        BAND_B_UNIVERSITIES.add("新疆大学");

        // --- Y ---
        BAND_B_UNIVERSITIES.add("Yanbian University");
        BAND_B_UNIVERSITIES.add("延边大学");
        BAND_B_UNIVERSITIES.add("Yangzhou University");
        BAND_B_UNIVERSITIES.add("扬州大学");
        BAND_B_UNIVERSITIES.add("Yanshan University");
        BAND_B_UNIVERSITIES.add("燕山大学");
        BAND_B_UNIVERSITIES.add("Yunnan Normal University");
        BAND_B_UNIVERSITIES.add("云南师范大学");
        BAND_B_UNIVERSITIES.add("Yunnan University");
        BAND_B_UNIVERSITIES.add("云南大学");

        // --- Z ---
        BAND_B_UNIVERSITIES.add("Zhejiang Agriculture and Forestry University (known as Zhejiang A&F University)");
        BAND_B_UNIVERSITIES.add("浙江农林大学");
        BAND_B_UNIVERSITIES.add("Zhejiang Gongshang University");
        BAND_B_UNIVERSITIES.add("浙江工商大学");
        BAND_B_UNIVERSITIES.add("Zhejiang Normal University");
        BAND_B_UNIVERSITIES.add("浙江师范大学");
        BAND_B_UNIVERSITIES.add("Zhejiang Sci-Tech University");
        BAND_B_UNIVERSITIES.add("浙江理工大学");
        BAND_B_UNIVERSITIES.add("Zhejiang University of Finance and Economics");
        BAND_B_UNIVERSITIES.add("浙江财经大学");
        BAND_B_UNIVERSITIES.add("Zhejiang University of Technology");
        BAND_B_UNIVERSITIES.add("浙江工业大学");
        BAND_B_UNIVERSITIES.add("Zhengzhou University");
        BAND_B_UNIVERSITIES.add("郑州大学");
        BAND_B_UNIVERSITIES.add("Zhongnan University of Economics and Law");
        BAND_B_UNIVERSITIES.add("中南财经政法大学");

        // --- Joint Ventures & Others (合办及其他) ---
        BAND_B_UNIVERSITIES.add("The Chinese University of Hong Kong (Shenzhen)");
        BAND_B_UNIVERSITIES.add("香港中文大学（深圳）");
        BAND_B_UNIVERSITIES.add("University of Nottingham Ningbo,China");
        BAND_B_UNIVERSITIES.add("宁波诺丁汉大学");
        BAND_B_UNIVERSITIES.add("Xi'an Jiaotong-Liverpool University");
        BAND_B_UNIVERSITIES.add("西安交通利物浦大学"); // 亦称西交利物浦
        BAND_B_UNIVERSITIES.add("Xi'an Jiaotong-Liverpool University");
        BAND_B_UNIVERSITIES.add("西交利物浦大学");
        BAND_B_UNIVERSITIES
                .add("Beijing Normal University Hong Kong Baptist University United International College (UIC)");
        BAND_B_UNIVERSITIES.add("北师港浸大");
        BAND_B_UNIVERSITIES
                .add("Beijing Normal University Hong Kong Baptist University United International College (UIC)");
        BAND_B_UNIVERSITIES.add("北京师范大学香港浸会大学联合国际学院");
        BAND_B_UNIVERSITIES.add("City University of Macau");
        BAND_B_UNIVERSITIES.add("澳门城市大学");
        BAND_B_UNIVERSITIES.add("Duke Kunshan University");
        BAND_B_UNIVERSITIES.add("昆山杜克大学");
        BAND_B_UNIVERSITIES.add("Macau University of Science and Technology");
        BAND_B_UNIVERSITIES.add("澳门科技大学");
        BAND_B_UNIVERSITIES.add("New York University Shanghai or NYU Shanghai");
        BAND_B_UNIVERSITIES.add("上海纽约大学");
        BAND_B_UNIVERSITIES.add("Shenzhen MSU-BIT University");
        BAND_B_UNIVERSITIES.add("深圳北理莫斯科大学");
        BAND_B_UNIVERSITIES.add("University of Macau");
        BAND_B_UNIVERSITIES.add("澳门大学");
        BAND_B_UNIVERSITIES.add("Wenzhou-Kean University");
        BAND_B_UNIVERSITIES.add("温州肯恩大学");
        BAND_B_UNIVERSITIES.add("Guangdong Technion Israel Institute of Technology");
        BAND_B_UNIVERSITIES.add("广东以色列理工学院");
    }

    // ---------------------------------------------------------
    // C类大学名单 (Band C) - 中英文双录入
    // ---------------------------------------------------------
    private static final Set<String> BAND_C_UNIVERSITIES = new HashSet<>();
    static {
        // --- A - C ---
        BAND_C_UNIVERSITIES.add("Anhui Agricultural University");
        BAND_C_UNIVERSITIES.add("安徽农业大学");
        BAND_C_UNIVERSITIES.add("Beijing Wuzi University");
        BAND_C_UNIVERSITIES.add("北京物资学院");
        BAND_C_UNIVERSITIES.add("Changchun University of Science and Technology");
        BAND_C_UNIVERSITIES.add("长春理工大学");
        BAND_C_UNIVERSITIES.add("Chengdu University of Technology");
        BAND_C_UNIVERSITIES.add("成都理工大学");
        BAND_C_UNIVERSITIES.add("China University of Labor Relations");
        BAND_C_UNIVERSITIES.add("中国劳动关系学院");
        // 注意：浙江传媒学院英文名较长，包含括号内的备注
        BAND_C_UNIVERSITIES.add(
                "Communication University of Zhejiang (Zhejiang Communications University, Zhejiang from Zhejiang University of Media and Communications)");
        BAND_C_UNIVERSITIES.add("浙江传媒学院");
        // 为了方便查询，额外添加简称版本（可选）
        BAND_C_UNIVERSITIES.add("Communication University of Zhejiang");

        // --- D - G ---
        BAND_C_UNIVERSITIES.add("Dalian University of Foreign Languages");
        BAND_C_UNIVERSITIES.add("大连外国语大学");
        BAND_C_UNIVERSITIES.add("Fujian Agriculture and Forestry University");
        BAND_C_UNIVERSITIES.add("福建农林大学");
        BAND_C_UNIVERSITIES.add("Guangdong University of Finance");
        BAND_C_UNIVERSITIES.add("广东金融学院");
        BAND_C_UNIVERSITIES.add("Guizhou University of Finance and Economics");
        BAND_C_UNIVERSITIES.add("贵州财经大学");

        // --- H ---
        BAND_C_UNIVERSITIES.add("Hebei University of Economics and Business");
        BAND_C_UNIVERSITIES.add("河北经贸大学");
        BAND_C_UNIVERSITIES.add("Henan Agricultural University");
        BAND_C_UNIVERSITIES.add("河南农业大学");
        BAND_C_UNIVERSITIES.add("Henan Polytechnic University");
        BAND_C_UNIVERSITIES.add("河南理工大学");
        BAND_C_UNIVERSITIES.add("Henan University of Economics and Law");
        BAND_C_UNIVERSITIES.add("河南财经政法大学");
        BAND_C_UNIVERSITIES.add("Henan University of Science and Technology");
        BAND_C_UNIVERSITIES.add("河南科技大学");
        BAND_C_UNIVERSITIES.add("Henan University of Technology");
        BAND_C_UNIVERSITIES.add("河南工业大学");
        BAND_C_UNIVERSITIES.add("Hubei University of Economics");
        BAND_C_UNIVERSITIES.add("湖北经济学院");
        BAND_C_UNIVERSITIES.add("Hunan University of Science and Technology");
        BAND_C_UNIVERSITIES.add("湖南科技大学");

        // --- I - K ---
        BAND_C_UNIVERSITIES.add("Inner Mongolia Agricultural University");
        BAND_C_UNIVERSITIES.add("内蒙古农业大学");
        BAND_C_UNIVERSITIES.add("Jilin Agricultural University");
        BAND_C_UNIVERSITIES.add("吉林农业大学");
        BAND_C_UNIVERSITIES.add("Jilin University of Finance and Economics");
        BAND_C_UNIVERSITIES.add("吉林财经大学");
        BAND_C_UNIVERSITIES.add("Kunming University of Science and Technology");
        BAND_C_UNIVERSITIES.add("昆明理工大学");

        // --- N - P ---
        BAND_C_UNIVERSITIES.add("Nanchang Hangkong University");
        BAND_C_UNIVERSITIES.add("南昌航空大学");
        BAND_C_UNIVERSITIES.add("North University of China");
        BAND_C_UNIVERSITIES.add("中北大学");
        BAND_C_UNIVERSITIES.add("Northwest Normal University");
        BAND_C_UNIVERSITIES.add("西北师范大学");
        BAND_C_UNIVERSITIES.add("Peking Union Medical College");
        BAND_C_UNIVERSITIES.add("北京协和医学院");

        // --- Q ---
        BAND_C_UNIVERSITIES.add("Qilu University of Technology (Shandong Academy of Sciences)");
        BAND_C_UNIVERSITIES.add("齐鲁工业大学");
        BAND_C_UNIVERSITIES.add("Qingdao University of Technology");
        BAND_C_UNIVERSITIES.add("青岛理工大学");

        // --- S ---
        BAND_C_UNIVERSITIES.add("Shaanxi University of Science and Technology");
        BAND_C_UNIVERSITIES.add("陕西科技大学");
        BAND_C_UNIVERSITIES.add("Shandong Agricultural University");
        BAND_C_UNIVERSITIES.add("山东农业大学");
        BAND_C_UNIVERSITIES.add("Shandong University of Technology");
        BAND_C_UNIVERSITIES.add("山东理工大学");
        BAND_C_UNIVERSITIES.add("Shanghai Lixin University of Accounting and Finance");
        BAND_C_UNIVERSITIES.add("上海立信会计金融学院");
        BAND_C_UNIVERSITIES.add("Shanghai University of Electric Power");
        BAND_C_UNIVERSITIES.add("上海电力大学");
        BAND_C_UNIVERSITIES.add("Shenyang Agricultural University");
        BAND_C_UNIVERSITIES.add("沈阳农业大学");
        BAND_C_UNIVERSITIES.add("Sichuan Normal University");
        BAND_C_UNIVERSITIES.add("四川师范大学");

        // --- T - U ---
        BAND_C_UNIVERSITIES.add("Tianjin Foreign Studies University");
        BAND_C_UNIVERSITIES.add("天津外国语大学");
        BAND_C_UNIVERSITIES.add("Tianjin University of Commerce");
        BAND_C_UNIVERSITIES.add("天津商业大学");
        BAND_C_UNIVERSITIES.add("University of South China");
        BAND_C_UNIVERSITIES.add("南华大学");

        // --- X ---
        BAND_C_UNIVERSITIES.add("Xi'an University of Finance and Economics");
        BAND_C_UNIVERSITIES.add("西安财经大学");
        BAND_C_UNIVERSITIES.add("Xi'an International Studies University");
        BAND_C_UNIVERSITIES.add("西安外国语大学");

        // --- Y ---
        BAND_C_UNIVERSITIES.add("Yangtze University");
        BAND_C_UNIVERSITIES.add("长江大学");
        BAND_C_UNIVERSITIES.add("Yunnan University of Finance and Economics");
        BAND_C_UNIVERSITIES.add("云南财经大学");

        // --- Z ---
        BAND_C_UNIVERSITIES.add("Zhejiang International Studies University");
        BAND_C_UNIVERSITIES.add("浙江外国语学院");
        BAND_C_UNIVERSITIES.add("Zhejiang Ocean University");
        BAND_C_UNIVERSITIES.add("浙江海洋大学");
    }

    // ---------------------------------------------------------
    // D类大学名单 (Band D) - 中英文双录入
    // ---------------------------------------------------------
    private static final Set<String> BAND_D_UNIVERSITIES = new HashSet<>();
    static {
        // --- A ---
        BAND_D_UNIVERSITIES.add("Anhui Jianzhu University");
        BAND_D_UNIVERSITIES.add("安徽建筑大学");
        BAND_D_UNIVERSITIES.add("Anhui Polytechnic University");
        BAND_D_UNIVERSITIES.add("安徽工程大学");
        BAND_D_UNIVERSITIES.add("Anhui Science and Technology University");
        BAND_D_UNIVERSITIES.add("安徽科技学院");
        BAND_D_UNIVERSITIES.add("Anqing Normal University");
        BAND_D_UNIVERSITIES.add("安庆师范大学");
        BAND_D_UNIVERSITIES.add("Anyang Normal University");
        BAND_D_UNIVERSITIES.add("安阳师范学院");

        // --- B ---
        BAND_D_UNIVERSITIES.add("Baoji University of Arts and Sciences");
        BAND_D_UNIVERSITIES.add("宝鸡文理学院");
        BAND_D_UNIVERSITIES.add("Beihua University");
        BAND_D_UNIVERSITIES.add("北华大学");
        BAND_D_UNIVERSITIES.add("Beijing Electronic Science and Technology Institute");
        BAND_D_UNIVERSITIES.add("北京电子科技学院");
        BAND_D_UNIVERSITIES.add("Beijing Information Science & Technology University");
        BAND_D_UNIVERSITIES.add("北京信息科技大学");
        BAND_D_UNIVERSITIES.add("Beijing Institute of Graphic Communication");
        BAND_D_UNIVERSITIES.add("北京印刷学院");
        BAND_D_UNIVERSITIES.add("Beijing Institute of Petrochemical Technology");
        BAND_D_UNIVERSITIES.add("北京石油化工学院");
        BAND_D_UNIVERSITIES.add("Beijing Union University");
        BAND_D_UNIVERSITIES.add("北京联合大学");
        BAND_D_UNIVERSITIES.add("Beijing University of Agriculture");
        BAND_D_UNIVERSITIES.add("北京农学院");

        // --- C ---
        BAND_D_UNIVERSITIES.add("Capital University of Physical Education And Sports");
        BAND_D_UNIVERSITIES.add("首都体育学院");
        BAND_D_UNIVERSITIES.add("Central South University of Forestry and Technology");
        BAND_D_UNIVERSITIES.add("中南林业科技大学");
        BAND_D_UNIVERSITIES.add("Changchun Institute of Technology");
        BAND_D_UNIVERSITIES.add("长春工程学院");
        BAND_D_UNIVERSITIES.add("Changchun Normal University");
        BAND_D_UNIVERSITIES.add("长春师范大学");
        BAND_D_UNIVERSITIES.add("Changchun University");
        BAND_D_UNIVERSITIES.add("长春大学");
        BAND_D_UNIVERSITIES.add("Changchun University of Technology");
        BAND_D_UNIVERSITIES.add("长春工业大学");
        BAND_D_UNIVERSITIES.add("Changsha University");
        BAND_D_UNIVERSITIES.add("长沙学院");
        BAND_D_UNIVERSITIES.add("Changshu Institute of Technology");
        BAND_D_UNIVERSITIES.add("常熟理工学院");
        BAND_D_UNIVERSITIES.add("Changzhou Institute of Technology");
        BAND_D_UNIVERSITIES.add("常州工学院");
        BAND_D_UNIVERSITIES.add("Chengdu University");
        BAND_D_UNIVERSITIES.add("成都大学");
        BAND_D_UNIVERSITIES.add("Chengdu University of Information Technology");
        BAND_D_UNIVERSITIES.add("成都信息工程大学");
        BAND_D_UNIVERSITIES.add("China People's Police University");
        BAND_D_UNIVERSITIES.add("中国人民警察大学");
        BAND_D_UNIVERSITIES.add("China West Normal University");
        BAND_D_UNIVERSITIES.add("西华师范大学");
        BAND_D_UNIVERSITIES.add("Chongqing Three Gorges University");
        BAND_D_UNIVERSITIES.add("重庆三峡学院");
        BAND_D_UNIVERSITIES.add("Chongqing University of Arts and Sciences");
        BAND_D_UNIVERSITIES.add("重庆文理学院");
        BAND_D_UNIVERSITIES.add("Chongqing University of Education");
        BAND_D_UNIVERSITIES.add("重庆第二师范学院");
        BAND_D_UNIVERSITIES.add("Chongqing University of Science and Technology");
        BAND_D_UNIVERSITIES.add("重庆科技学院"); // 现名包含大学，此处按表录入
        BAND_D_UNIVERSITIES.add("Chongqing University of Technology");
        BAND_D_UNIVERSITIES.add("重庆理工大学");
        BAND_D_UNIVERSITIES.add("Chuzhou University");
        BAND_D_UNIVERSITIES.add("滁州学院");
        BAND_D_UNIVERSITIES.add("Civil Aviation Flight University of China");
        BAND_D_UNIVERSITIES.add("中国民用航空飞行学院");

        // --- D ---
        BAND_D_UNIVERSITIES.add("Dali University");
        BAND_D_UNIVERSITIES.add("大理大学");
        BAND_D_UNIVERSITIES.add("Dalian Jiaotong University");
        BAND_D_UNIVERSITIES.add("大连交通大学");
        BAND_D_UNIVERSITIES.add("Dalian Minzu University");
        BAND_D_UNIVERSITIES.add("大连民族大学");
        BAND_D_UNIVERSITIES.add("Dalian Ocean University");
        BAND_D_UNIVERSITIES.add("大连海洋大学");
        BAND_D_UNIVERSITIES.add("Dalian Polytechnic University");
        BAND_D_UNIVERSITIES.add("大连工业大学");
        BAND_D_UNIVERSITIES.add("Dalian University");
        BAND_D_UNIVERSITIES.add("大连大学");
        BAND_D_UNIVERSITIES.add("Dezhou University");
        BAND_D_UNIVERSITIES.add("德州学院");
        BAND_D_UNIVERSITIES.add("Dongguan University of Technology");
        BAND_D_UNIVERSITIES.add("东莞理工学院");

        // --- E ---
        BAND_D_UNIVERSITIES.add("East China Jiaotong University");
        BAND_D_UNIVERSITIES.add("华东交通大学");
        BAND_D_UNIVERSITIES.add("East China University of Technology");
        BAND_D_UNIVERSITIES.add("东华理工大学");

        // --- F ---
        BAND_D_UNIVERSITIES.add("Foshan University");
        BAND_D_UNIVERSITIES.add("佛山科学技术学院");
        BAND_D_UNIVERSITIES.add("Fujian University of Technology");
        BAND_D_UNIVERSITIES.add("福建理工大学");
        BAND_D_UNIVERSITIES.add("Fuyang Normal University");
        BAND_D_UNIVERSITIES.add("阜阳师范大学");

        // --- G ---
        BAND_D_UNIVERSITIES.add("Gannan Normal University");
        BAND_D_UNIVERSITIES.add("赣南师范大学");
        BAND_D_UNIVERSITIES.add("Gansu Agricultural University");
        BAND_D_UNIVERSITIES.add("甘肃农业大学");
        BAND_D_UNIVERSITIES.add("Gansu University of Political Science and Law");
        BAND_D_UNIVERSITIES.add("甘肃政法大学");
        BAND_D_UNIVERSITIES.add("Guangdong Ocean University");
        BAND_D_UNIVERSITIES.add("广东海洋大学");
        BAND_D_UNIVERSITIES.add("Guangdong Polytechnic Normal University");
        BAND_D_UNIVERSITIES.add("广东技术师范大学");
        BAND_D_UNIVERSITIES.add("Guangdong University of Education");
        BAND_D_UNIVERSITIES.add("广东第二师范学院");
        BAND_D_UNIVERSITIES.add("Guangxi University of Science and Technology");
        BAND_D_UNIVERSITIES.add("广西科技大学");
        BAND_D_UNIVERSITIES.add("Guilin University of Electronic Technology");
        BAND_D_UNIVERSITIES.add("桂林电子科技大学");
        BAND_D_UNIVERSITIES.add("Guilin University of Technology");
        BAND_D_UNIVERSITIES.add("桂林理工大学");
        BAND_D_UNIVERSITIES.add("Guizhou Education University");
        BAND_D_UNIVERSITIES.add("贵州师范学院");
        BAND_D_UNIVERSITIES.add("Guizhou Institute of Technology");
        BAND_D_UNIVERSITIES.add("贵州理工学院");
        BAND_D_UNIVERSITIES.add("Guizhou Normal University");
        BAND_D_UNIVERSITIES.add("贵州师范大学");

        // --- H ---
        BAND_D_UNIVERSITIES.add("Hainan Normal University");
        BAND_D_UNIVERSITIES.add("海南师范大学");
        BAND_D_UNIVERSITIES.add("Hangzhou City University");
        BAND_D_UNIVERSITIES.add("浙大城市学院");
        BAND_D_UNIVERSITIES.add("Harbin University of Commerce");
        BAND_D_UNIVERSITIES.add("哈尔滨商业大学");
        BAND_D_UNIVERSITIES.add("Harbin University of Science and Technology");
        BAND_D_UNIVERSITIES.add("哈尔滨理工大学");
        BAND_D_UNIVERSITIES.add("Hebei GEO University");
        BAND_D_UNIVERSITIES.add("河北地质大学");
        BAND_D_UNIVERSITIES.add("Hebei Normal University of Science and Technology");
        BAND_D_UNIVERSITIES.add("河北科技师范学院");
        BAND_D_UNIVERSITIES.add("Hebei University of Engineering");
        BAND_D_UNIVERSITIES.add("河北工程大学");
        BAND_D_UNIVERSITIES.add("Hefei Normal University");
        BAND_D_UNIVERSITIES.add("合肥师范学院");
        BAND_D_UNIVERSITIES.add("Hefei University");
        BAND_D_UNIVERSITIES.add("合肥学院");
        BAND_D_UNIVERSITIES.add("Heilongjiang Bayi Agricultural University");
        BAND_D_UNIVERSITIES.add("黑龙江八一农垦大学");
        BAND_D_UNIVERSITIES.add("Heilongjiang Institute of Technology");
        BAND_D_UNIVERSITIES.add("黑龙江工程学院");
        BAND_D_UNIVERSITIES.add("Heilongjiang University of Science and Technology");
        BAND_D_UNIVERSITIES.add("黑龙江科技大学");
        BAND_D_UNIVERSITIES.add("Henan Institute of Science and Technology");
        BAND_D_UNIVERSITIES.add("河南科技学院");
        BAND_D_UNIVERSITIES.add("Henan University of Engineering");
        BAND_D_UNIVERSITIES.add("河南工程学院");
        BAND_D_UNIVERSITIES.add("Henan University of Urban Construction");
        BAND_D_UNIVERSITIES.add("河南城建学院");
        BAND_D_UNIVERSITIES.add("Hengyang Normal University");
        BAND_D_UNIVERSITIES.add("衡阳师范学院");
        BAND_D_UNIVERSITIES.add("Huaibei Normal University");
        BAND_D_UNIVERSITIES.add("淮北师范大学");
        BAND_D_UNIVERSITIES.add("Huaiyin Institute of Technology");
        BAND_D_UNIVERSITIES.add("淮阴工学院");
        BAND_D_UNIVERSITIES.add("Huaiyin Normal University");
        BAND_D_UNIVERSITIES.add("淮阴师范学院");
        BAND_D_UNIVERSITIES.add("Huanggang Normal University");
        BAND_D_UNIVERSITIES.add("黄冈师范学院");
        BAND_D_UNIVERSITIES.add("Huanghuai University");
        BAND_D_UNIVERSITIES.add("黄淮学院");
        BAND_D_UNIVERSITIES.add("Hubei Engineering University");
        BAND_D_UNIVERSITIES.add("湖北工程学院");
        BAND_D_UNIVERSITIES.add("Hubei Polytechnic University");
        BAND_D_UNIVERSITIES.add("湖北理工学院");
        BAND_D_UNIVERSITIES.add("Hubei Normal University");
        BAND_D_UNIVERSITIES.add("湖北师范大学");
        BAND_D_UNIVERSITIES.add("Hubei University of Arts and Science");
        BAND_D_UNIVERSITIES.add("湖北文理学院");
        BAND_D_UNIVERSITIES.add("Hubei University of Automotive Technology");
        BAND_D_UNIVERSITIES.add("湖北汽车工业学院");
        BAND_D_UNIVERSITIES.add("Hubei University of Education");
        BAND_D_UNIVERSITIES.add("湖北第二师范学院");
        BAND_D_UNIVERSITIES.add("Hubei University of Science and Technology");
        BAND_D_UNIVERSITIES.add("湖北科技学院");
        BAND_D_UNIVERSITIES.add("Huizhou University");
        BAND_D_UNIVERSITIES.add("惠州学院");
        BAND_D_UNIVERSITIES.add("Hunan City University");
        BAND_D_UNIVERSITIES.add("湖南城市学院");
        BAND_D_UNIVERSITIES.add("Hunan First Normal University");
        BAND_D_UNIVERSITIES.add("湖南第一师范学院");
        BAND_D_UNIVERSITIES.add("Hunan Institute of Engineering");
        BAND_D_UNIVERSITIES.add("湖南工程学院");
        BAND_D_UNIVERSITIES.add("Hunan Institute of Science and Technology");
        BAND_D_UNIVERSITIES.add("湖南理工学院");
        BAND_D_UNIVERSITIES.add("Hunan University of Arts and Sciences");
        BAND_D_UNIVERSITIES.add("湖南文理学院");
        BAND_D_UNIVERSITIES.add("Hunan University of Science and Engineering");
        BAND_D_UNIVERSITIES.add("湖南科技学院");
        BAND_D_UNIVERSITIES.add("Hunan University of Technology");
        BAND_D_UNIVERSITIES.add("湖南工业大学");
        BAND_D_UNIVERSITIES.add("Huzhou University");
        BAND_D_UNIVERSITIES.add("湖州师范学院");

        // --- I ---
        BAND_D_UNIVERSITIES.add("Inner Mongolia Minzu University");
        BAND_D_UNIVERSITIES.add("内蒙古民族大学");
        BAND_D_UNIVERSITIES.add("Inner Mongolia Normal University");
        BAND_D_UNIVERSITIES.add("内蒙古师范大学");
        BAND_D_UNIVERSITIES.add("Inner Mongolia University of Finance and Economics");
        BAND_D_UNIVERSITIES.add("内蒙古财经大学");
        BAND_D_UNIVERSITIES.add("Inner Mongolia University of Science and Technology");
        BAND_D_UNIVERSITIES.add("内蒙古科技大学");
        BAND_D_UNIVERSITIES.add("Inner Mongolia University of Technology");
        BAND_D_UNIVERSITIES.add("内蒙古工业大学");

        // --- J ---
        BAND_D_UNIVERSITIES.add("Jiamusi University");
        BAND_D_UNIVERSITIES.add("佳木斯大学");
        BAND_D_UNIVERSITIES.add("Jianghan University");
        BAND_D_UNIVERSITIES.add("江汉大学");
        BAND_D_UNIVERSITIES.add("Jiangsu Ocean University");
        BAND_D_UNIVERSITIES.add("江苏海洋大学");
        BAND_D_UNIVERSITIES.add("Jiangsu Second Normal University");
        BAND_D_UNIVERSITIES.add("江苏第二师范学院");
        BAND_D_UNIVERSITIES.add("Jiangsu University of Technology");
        BAND_D_UNIVERSITIES.add("江苏理工学院");
        BAND_D_UNIVERSITIES.add("Jiangxi Agricultural University");
        BAND_D_UNIVERSITIES.add("江西农业大学");
        BAND_D_UNIVERSITIES.add("Jiangxi Science and Technology Normal University");
        BAND_D_UNIVERSITIES.add("江西科技师范大学");
        BAND_D_UNIVERSITIES.add("Jiaxing University");
        BAND_D_UNIVERSITIES.add("嘉兴学院");
        BAND_D_UNIVERSITIES.add("Jilin Institute of Chemical Technology");
        BAND_D_UNIVERSITIES.add("吉林化工学院");
        BAND_D_UNIVERSITIES.add("Jilin Jianzhu University");
        BAND_D_UNIVERSITIES.add("吉林建筑大学");
        BAND_D_UNIVERSITIES.add("Jilin Normal University");
        BAND_D_UNIVERSITIES.add("吉林师范大学");
        BAND_D_UNIVERSITIES.add("Jingdezhen Ceramic University");
        BAND_D_UNIVERSITIES.add("景德镇陶瓷大学");
        BAND_D_UNIVERSITIES.add("Jinggangshan University");
        BAND_D_UNIVERSITIES.add("井冈山大学");
        BAND_D_UNIVERSITIES.add("Jishou University");
        BAND_D_UNIVERSITIES.add("吉首大学");
        BAND_D_UNIVERSITIES.add("Jiujiang University");
        BAND_D_UNIVERSITIES.add("九江学院");

        // --- L ---
        BAND_D_UNIVERSITIES.add("Lanzhou Jiaotong University");
        BAND_D_UNIVERSITIES.add("兰州交通大学");
        BAND_D_UNIVERSITIES.add("Lanzhou University of Finance and Economics");
        BAND_D_UNIVERSITIES.add("兰州财经大学");
        BAND_D_UNIVERSITIES.add("Liaocheng University");
        BAND_D_UNIVERSITIES.add("聊城大学");
        BAND_D_UNIVERSITIES.add("Liaoning Institute of Science and Technology");
        BAND_D_UNIVERSITIES.add("辽宁科技学院");
        BAND_D_UNIVERSITIES.add("Liaoning Petrochemical University");
        BAND_D_UNIVERSITIES.add("辽宁石油化工大学");
        BAND_D_UNIVERSITIES.add("Liaoning Technical University");
        BAND_D_UNIVERSITIES.add("辽宁工程技术大学");
        BAND_D_UNIVERSITIES.add("Lingnan Normal University");
        BAND_D_UNIVERSITIES.add("岭南师范学院");
        BAND_D_UNIVERSITIES.add("Linyi University");
        BAND_D_UNIVERSITIES.add("临沂大学");
        BAND_D_UNIVERSITIES.add("Luoyang Normal University");
        BAND_D_UNIVERSITIES.add("洛阳师范学院");

        // --- M ---
        BAND_D_UNIVERSITIES.add("Mianyang Normal University (known as Mianyang Teachers' College)");
        BAND_D_UNIVERSITIES.add("绵阳师范学院");
        BAND_D_UNIVERSITIES.add("Minjiang University");
        BAND_D_UNIVERSITIES.add("闽江学院");
        BAND_D_UNIVERSITIES.add("Minnan Normal University");
        BAND_D_UNIVERSITIES.add("闽南师范大学");

        // --- N ---
        BAND_D_UNIVERSITIES.add("Nanchang Institute of Technology");
        BAND_D_UNIVERSITIES.add("南昌工程学院");
        BAND_D_UNIVERSITIES.add("Nanchang Normal University");
        BAND_D_UNIVERSITIES.add("南昌师范学院");
        BAND_D_UNIVERSITIES.add("Nanjing Institute of Technology");
        BAND_D_UNIVERSITIES.add("南京工程学院");
        BAND_D_UNIVERSITIES.add("Nanjing Xiaozhuang University");
        BAND_D_UNIVERSITIES.add("南京晓庄学院");
        BAND_D_UNIVERSITIES.add("Nanning Normal University");
        BAND_D_UNIVERSITIES.add("南宁师范大学");
        BAND_D_UNIVERSITIES.add("Nanyang Institute of Technology");
        BAND_D_UNIVERSITIES.add("南阳理工学院");
        BAND_D_UNIVERSITIES.add("Nanyang Normal University");
        BAND_D_UNIVERSITIES.add("南阳师范学院");
        BAND_D_UNIVERSITIES.add("Neijiang Normal University");
        BAND_D_UNIVERSITIES.add("内江师范学院");
        BAND_D_UNIVERSITIES.add("Ningbo Tech University");
        BAND_D_UNIVERSITIES.add("浙大宁波理工学院");
        BAND_D_UNIVERSITIES.add("Ningbo University of Technology");
        BAND_D_UNIVERSITIES.add("宁波工程学院");
        BAND_D_UNIVERSITIES.add("Ningxia Normal University");
        BAND_D_UNIVERSITIES.add("宁夏师范学院");
        BAND_D_UNIVERSITIES.add("North China Institute of Aerospace Engineering");
        BAND_D_UNIVERSITIES.add("北华航天工业学院");
        BAND_D_UNIVERSITIES.add("North China Institute of Science and Technology");
        BAND_D_UNIVERSITIES.add("华北科技学院");
        BAND_D_UNIVERSITIES.add("North China University of Science and Technology");
        BAND_D_UNIVERSITIES.add("华北理工大学");
        BAND_D_UNIVERSITIES.add("North China University of Water Resources and Electric Power");
        BAND_D_UNIVERSITIES.add("华北水利水电大学");
        BAND_D_UNIVERSITIES.add("North Minzu University");
        BAND_D_UNIVERSITIES.add("北方民族大学");
        BAND_D_UNIVERSITIES.add("Northeast Electric Power University");
        BAND_D_UNIVERSITIES.add("东北电力大学");
        BAND_D_UNIVERSITIES.add("Northeast Petroleum University");
        BAND_D_UNIVERSITIES.add("东北石油大学");
        BAND_D_UNIVERSITIES.add("Northwest Minzu University");
        BAND_D_UNIVERSITIES.add("西北民族大学");

        // --- P ---
        BAND_D_UNIVERSITIES.add("Panzhihua University");
        BAND_D_UNIVERSITIES.add("攀枝花学院");

        // --- Q ---
        BAND_D_UNIVERSITIES.add("Qilu Normal University");
        BAND_D_UNIVERSITIES.add("齐鲁师范学院");
        BAND_D_UNIVERSITIES.add("Qingdao Agricultural University");
        BAND_D_UNIVERSITIES.add("青岛农业大学");
        BAND_D_UNIVERSITIES.add("Qinghai Minzu University");
        BAND_D_UNIVERSITIES.add("青海民族大学");
        BAND_D_UNIVERSITIES.add("Qinghai Normal University");
        BAND_D_UNIVERSITIES.add("青海师范大学");
        BAND_D_UNIVERSITIES.add("Qiqihar University");
        BAND_D_UNIVERSITIES.add("齐齐哈尔大学");
        BAND_D_UNIVERSITIES.add("Quanzhou Normal University");
        BAND_D_UNIVERSITIES.add("泉州师范学院");
        BAND_D_UNIVERSITIES.add("Quzhou University");
        BAND_D_UNIVERSITIES.add("衢州学院");

        // --- S ---
        BAND_D_UNIVERSITIES.add("Shaanxi University of Technology");
        BAND_D_UNIVERSITIES.add("陕西理工大学");
        BAND_D_UNIVERSITIES.add("Shandong Jianzhu University");
        BAND_D_UNIVERSITIES.add("山东建筑大学");
        BAND_D_UNIVERSITIES.add("Shandong Jiaotong University");
        BAND_D_UNIVERSITIES.add("山东交通学院");
        BAND_D_UNIVERSITIES.add("Shandong Management University");
        BAND_D_UNIVERSITIES.add("山东管理学院");
        BAND_D_UNIVERSITIES.add("Shandong University of Aeronautics");
        BAND_D_UNIVERSITIES.add("山东航空学院");
        BAND_D_UNIVERSITIES.add("Shanghai Institute of Technology");
        BAND_D_UNIVERSITIES.add("上海应用技术大学");
        BAND_D_UNIVERSITIES.add("Shanghai Customs College");
        BAND_D_UNIVERSITIES.add("上海海关学院");
        BAND_D_UNIVERSITIES.add("Shanghai Dianji University");
        BAND_D_UNIVERSITIES.add("上海电机学院");
        BAND_D_UNIVERSITIES.add("Shanghai Polytechnic University");
        BAND_D_UNIVERSITIES.add("上海第二工业大学");
        BAND_D_UNIVERSITIES.add("Shanghai University of Engineering Science");
        BAND_D_UNIVERSITIES.add("上海工程技术大学");
        BAND_D_UNIVERSITIES.add("Shanghai University of Sport");
        BAND_D_UNIVERSITIES.add("上海体育大学");
        BAND_D_UNIVERSITIES.add("Shanxi Agricultural University");
        BAND_D_UNIVERSITIES.add("山西农业大学");
        BAND_D_UNIVERSITIES.add("Shanxi Normal University");
        BAND_D_UNIVERSITIES.add("山西师范大学");
        BAND_D_UNIVERSITIES.add("Shaoxing University");
        BAND_D_UNIVERSITIES.add("绍兴文理学院");
        BAND_D_UNIVERSITIES.add("Shenyang Jianzhu University");
        BAND_D_UNIVERSITIES.add("沈阳建筑大学");
        BAND_D_UNIVERSITIES.add("Shenyang Ligong University");
        BAND_D_UNIVERSITIES.add("沈阳理工大学");
        BAND_D_UNIVERSITIES.add("Shenyang Normal University");
        BAND_D_UNIVERSITIES.add("沈阳师范大学");
        BAND_D_UNIVERSITIES.add("Shenyang Sport University");
        BAND_D_UNIVERSITIES.add("沈阳体育学院");
        BAND_D_UNIVERSITIES.add("Shenyang University");
        BAND_D_UNIVERSITIES.add("沈阳大学");
        BAND_D_UNIVERSITIES.add("Shenyang University of Chemical Technology");
        BAND_D_UNIVERSITIES.add("沈阳化工大学");
        BAND_D_UNIVERSITIES.add("Shijiazhuang University");
        BAND_D_UNIVERSITIES.add("石家庄学院");
        BAND_D_UNIVERSITIES.add("Sichuan University of Science and Engineering");
        BAND_D_UNIVERSITIES.add("四川轻化工大学");
        BAND_D_UNIVERSITIES.add("Southwest Forestry University");
        BAND_D_UNIVERSITIES.add("西南林业大学");
        BAND_D_UNIVERSITIES.add("Southwest University of Science and Technology");
        BAND_D_UNIVERSITIES.add("西南科技大学");
        BAND_D_UNIVERSITIES.add("Suqian University");
        BAND_D_UNIVERSITIES.add("宿迁学院");

        // --- T ---
        BAND_D_UNIVERSITIES.add("Taishan University");
        BAND_D_UNIVERSITIES.add("泰山学院");
        BAND_D_UNIVERSITIES.add("Taiyuan University of Science and Technology");
        BAND_D_UNIVERSITIES.add("太原科技大学");
        BAND_D_UNIVERSITIES.add("Taizhou University");
        BAND_D_UNIVERSITIES.add("台州学院");
        BAND_D_UNIVERSITIES.add("Tangshan Normal University");
        BAND_D_UNIVERSITIES.add("唐山师范学院");
        BAND_D_UNIVERSITIES.add("Tarim University");
        BAND_D_UNIVERSITIES.add("塔里木大学");
        BAND_D_UNIVERSITIES.add("Tianjin Agricultural University");
        BAND_D_UNIVERSITIES.add("天津农学院");
        BAND_D_UNIVERSITIES.add("Tianjin Chengjian University");
        BAND_D_UNIVERSITIES.add("天津城建大学");
        BAND_D_UNIVERSITIES.add("Tianjin University of Sport");
        BAND_D_UNIVERSITIES.add("天津体育学院");
        BAND_D_UNIVERSITIES.add("Tibet Agricultural and Animal Husbandry University");
        BAND_D_UNIVERSITIES.add("西藏农牧学院");
        BAND_D_UNIVERSITIES.add("Tongren University");
        BAND_D_UNIVERSITIES.add("铜仁学院");

        // --- U ---
        BAND_D_UNIVERSITIES.add("University of Science and Technology Liaoning");
        BAND_D_UNIVERSITIES.add("辽宁科技大学");

        // --- W ---
        BAND_D_UNIVERSITIES.add("Wuhan Polytechnic University");
        BAND_D_UNIVERSITIES.add("武汉轻工大学");
        BAND_D_UNIVERSITIES.add("Wuhan Sports University");
        BAND_D_UNIVERSITIES.add("武汉体育学院");
        BAND_D_UNIVERSITIES.add("Wuhan Textile University");
        BAND_D_UNIVERSITIES.add("武汉纺织大学");
        BAND_D_UNIVERSITIES.add("Wuyi University");
        BAND_D_UNIVERSITIES.add("五邑大学");

        // --- X ---
        BAND_D_UNIVERSITIES.add("Xi'an Polytechnic University");
        BAND_D_UNIVERSITIES.add("西安工程大学");
        BAND_D_UNIVERSITIES.add("Xi'an Shiyou University");
        BAND_D_UNIVERSITIES.add("西安石油大学");
        BAND_D_UNIVERSITIES.add("Xi'an Technological University");
        BAND_D_UNIVERSITIES.add("西安工业大学");
        BAND_D_UNIVERSITIES.add("Xi'an University");
        BAND_D_UNIVERSITIES.add("西安文理学院");
        BAND_D_UNIVERSITIES.add("Xiamen University of Technology");
        BAND_D_UNIVERSITIES.add("厦门理工学院");
        BAND_D_UNIVERSITIES.add("Xihua University");
        BAND_D_UNIVERSITIES.add("西华大学");
        BAND_D_UNIVERSITIES.add("Xinjiang Agricultural University");
        BAND_D_UNIVERSITIES.add("新疆农业大学");
        BAND_D_UNIVERSITIES.add("Xinjiang Normal University");
        BAND_D_UNIVERSITIES.add("新疆师范大学");
        BAND_D_UNIVERSITIES.add("Xinjiang University of Finance and Economics");
        BAND_D_UNIVERSITIES.add("新疆财经大学");
        BAND_D_UNIVERSITIES.add("Xinyang Normal University");
        BAND_D_UNIVERSITIES.add("信阳师范学院");
        BAND_D_UNIVERSITIES.add("Xizang Minzu University");
        BAND_D_UNIVERSITIES.add("西藏民族大学");
        BAND_D_UNIVERSITIES.add("Xuzhou University of Technology");
        BAND_D_UNIVERSITIES.add("徐州工程学院");

        // --- Y ---
        BAND_D_UNIVERSITIES.add("Yan'an University");
        BAND_D_UNIVERSITIES.add("延安大学");
        BAND_D_UNIVERSITIES.add("Yancheng Institute of Technology");
        BAND_D_UNIVERSITIES.add("盐城工学院");
        BAND_D_UNIVERSITIES.add("Yancheng Teachers University");
        BAND_D_UNIVERSITIES.add("盐城师范学院");
        BAND_D_UNIVERSITIES.add("Yangtze Normal University");
        BAND_D_UNIVERSITIES.add("长江师范学院");
        BAND_D_UNIVERSITIES.add("Yantai University");
        BAND_D_UNIVERSITIES.add("烟台大学");
        BAND_D_UNIVERSITIES.add("Yichun University");
        BAND_D_UNIVERSITIES.add("宜春学院");
        BAND_D_UNIVERSITIES.add("Yulin Normal University");
        BAND_D_UNIVERSITIES.add("玉林师范学院");
        BAND_D_UNIVERSITIES.add("Yunnan Agricultural University");
        BAND_D_UNIVERSITIES.add("云南农业大学");
        BAND_D_UNIVERSITIES.add("Yunnan Minzu University");
        BAND_D_UNIVERSITIES.add("云南民族大学");

        // --- Z ---
        BAND_D_UNIVERSITIES.add("Zaozhuang University");
        BAND_D_UNIVERSITIES.add("枣庄学院");
        BAND_D_UNIVERSITIES.add("Zhaoqing University");
        BAND_D_UNIVERSITIES.add("肇庆学院");
        BAND_D_UNIVERSITIES.add("Zhejiang University of Science and Technology");
        BAND_D_UNIVERSITIES.add("浙江科技学院");
        BAND_D_UNIVERSITIES.add("Zhejiang University of Water Resources and Electric Power");
        BAND_D_UNIVERSITIES.add("浙江水利水电学院");
        BAND_D_UNIVERSITIES.add("Zhejiang Wanli University");
        BAND_D_UNIVERSITIES.add("浙江万里学院");
        BAND_D_UNIVERSITIES.add("Zhengzhou University of Light Industry");
        BAND_D_UNIVERSITIES.add("郑州轻工业大学");
        BAND_D_UNIVERSITIES.add("Zhongkai University of Agriculture and Engineering");
        BAND_D_UNIVERSITIES.add("仲恺农业工程学院");
        BAND_D_UNIVERSITIES.add("Zhongyuan University of Technology");
        BAND_D_UNIVERSITIES.add("中原工学院");
    }

    // ================== 实现接口方法 ==================

    @Override
    public String getSchoolName() {
        return "杜伦大学";
    }

    @Override
    public String getRegion() {
        return "英国";
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        List<MatchingResult> results = new ArrayList<>();
        // 1. 获取杜伦大学实体
        School durham = dataService.getSchoolByName("杜伦大学");

        if (durham == null) {
            log.warn("未在数据库中找到杜伦大学，跳过杜伦大学匹配算法");
            return results;
        }
        // 2. 获取学生本科院校所属的 Band (严格校验)
        String studentUniversity = request.getStudentInfo().getUndergraduateSchool();
        String studentUniversityBand = getUniversityBand(studentUniversity);
        // 3. 严格准入逻辑：如果学校不在A/B/C/D名单内，直接返回拒信
        if ("NOT_LISTED".equals(studentUniversityBand)) {
            MatchingResult result = new MatchingResult();
            result.setUserId(request.getStudentInfo().getUserId());
            result.setSchoolId(durham.getId());
            result.setSchoolName(durham.getName());
            result.setMajorName("All Majors");
            result.setMatchScore(0.0);
            result.setMatchLevel("不符合申请资格"); // 明确告知
            result.setAdmissionProbability(0.0);
            result.setRecommendationIndex(0.0);
            result.setRegion("英国");
            result.setRiskAnalysis("很抱歉，您的本科院校不在杜伦大学认可的 Band A/B/C/D 名单中。根据学校官方政策，不在List内的学校通常不接受申请。");
            result.setAlgorithmStrategy("UK_DURHAM_MATCHING_ALGORITHM");
            results.add(result);
            return results; // 终止后续计算
        }
        // 4. 遍历杜伦大学的所有专业进行匹配
        List<Major> majors = dataService.getMajorsBySchoolId(durham.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        for (Major major : majors) {
            // 4.1 获取专业分类 (修正CS/DS分类逻辑)
            String majorCategory = getMajorCategory(major);

            // 4.2 获取GPA要求 (根据Band和专业分类)
            double requiredGpa = getRequiredGpa(studentUniversityBand, majorCategory, studentUniversity);

            // 4.3 计算匹配分数
            double matchScore = calculateMatchScore(request.getStudentInfo(), requiredGpa, major);

            // 4.4 构建结果对象
            MatchingResult result = new MatchingResult();
            result.setUserId(request.getStudentInfo().getUserId());
            result.setSchoolId(durham.getId());
            result.setSchoolName(durham.getName());
            result.setMajorName(major.getName());
            result.setMatchScore(matchScore);

            // 4.5 确定匹配等级 (修正：高分=稳妥，低分=冲刺)
            result.setMatchLevel(determineMatchLevel(matchScore));

            result.setAdmissionProbability(calculateAdmissionProbability(matchScore));
            result.setRecommendationIndex(calculateRecommendationIndex(matchScore));
            result.setRegion("英国");
            result.setAlgorithmStrategy("UK_DURHAM_MATCHING_ALGORITHM");

            // 4.6 生成详细的分析报告
            String analysis = String.format(
                    "【院校背景】您的本科院校 (%s) 属于杜伦大学 Band %s。\n" +
                            "【专业要求】申请 %s 类专业，Band %s 要求的最低均分为 %.1f%%。\n" +
                            "【您的成绩】您的均分为 %.1f%%，%s。",
                    studentUniversity,
                    studentUniversityBand,
                    majorCategory,
                    studentUniversityBand,
                    requiredGpa,
                    request.getStudentInfo().getGpa(),
                    matchScore >= 85 ? "符合要求" : (matchScore >= 60 ? "稍有差距" : "差距较大"));
            result.setRiskAnalysis(analysis);

            results.add(result);
        }

        // 按匹配分数降序排列
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

    private String getUniversityBand(String universityName) {
        if (universityName == null || universityName.trim().isEmpty()) {
            return "Rest of ARWU"; // 无法识别的学校默认为最高要求
        }
        // 必须按顺序判断，或者因为是Set不重叠，顺序无所谓，但必须包含所有
        if (BAND_A_UNIVERSITIES.stream().anyMatch(uni -> universityName.contains(uni))) {
            return "A";
        }
        if (BAND_B_UNIVERSITIES.stream().anyMatch(uni -> universityName.contains(uni))) {
            return "B";
        }
        // --- 修复：补全 C 类和 D 类判断 ---
        if (BAND_C_UNIVERSITIES.stream().anyMatch(uni -> universityName.contains(uni))) {
            return "C";
        }
        if (BAND_D_UNIVERSITIES.stream().anyMatch(uni -> universityName.contains(uni))) {
            return "D";
        }

        // 默认返回 Rest of ARWU list
        return "NOT_LISTED";
    }

    /**
     * 获取专业分类
     * [FIX] 确保 CS 和 Data Science 归类为 Rest of University，而非 Engineering
     */
    private String getMajorCategory(Major major) {
        String majorName = major.getName().toLowerCase();
        String majorEngName = major.getEnglishName() == null ? "" : major.getEnglishName().toLowerCase();

        // 1. 商科 / 法学 (DUBS / Law School)
        if (majorName.contains("business") || majorEngName.contains("business") ||
                majorName.contains("law") || majorEngName.contains("law") ||
                majorName.contains("商") || majorName.contains("法") ||
                majorName.contains("management") || majorEngName.contains("management") ||
                majorName.contains("finance") || majorEngName.contains("finance") ||
                majorName.contains("marketing") || majorEngName.contains("marketing")) {
            return "DUBS / Law School";
        }

        // 2. 音乐 (Music)
        if (majorName.contains("music") || majorEngName.contains("music") || majorName.contains("音乐")) {
            return "Music";
        }

        // 3. 计算机与数据科学 (必须在判断 Engineering 之前排除)
        // 官方备注：Rest of the Uni includes programmes in Computer Science and Data
        // Science.
        boolean isCSorDS = majorName.contains("computer science") || majorEngName.contains("computer science") ||
                majorName.contains("data science") || majorEngName.contains("data science") ||
                majorName.contains("计算机") || majorName.contains("数据科学") ||
                majorName.contains("software") || majorEngName.contains("software");

        if (isCSorDS) {
            return "Rest of University";
        }

        // 4. 工程 (Engineering)
        // 注意：此处仅指 Engineering Faculty 下的传统工科
        if (majorName.contains("engineering") || majorEngName.contains("engineering") || majorName.contains("工程")) {
            return "Engineering";
        }

        // 5. 其他所有专业 (Rest of University)
        return "Rest of University";
    }

    /**
     * 获取GPA要求
     * 数据来源：杜伦大学官方List矩阵
     */
    private double getRequiredGpa(String band, String majorCategory, String studentUniversity) {
        // 特殊逻辑：音乐学院申请音乐专业
        boolean isMusicConservatory = MUSIC_CONSERVATORIES.stream().anyMatch(studentUniversity::contains);
        if ("Music".equals(majorCategory) && isMusicConservatory) {
            return 80.0;
        }

        switch (band) {
            case "A":
                // Band A: 所有专业统一 75%
                return 75.0;
            case "B":
                // Band B: 商法 80%, 工科 75%, 其他 75%
                if ("DUBS / Law School".equals(majorCategory))
                    return 80.0;
                if ("Engineering".equals(majorCategory))
                    return 75.0;
                return 75.0;
            case "C":
                // Band C: 商法 80%, 工科 75%, 其他 80%
                if ("DUBS / Law School".equals(majorCategory))
                    return 80.0;
                if ("Engineering".equals(majorCategory))
                    return 75.0;
                return 80.0; // Rest of University (包含CS/DS) 要求 80%
            case "D":
                // Band D: 商法 82%, 工科 75%, 其他 82%
                if ("DUBS / Law School".equals(majorCategory))
                    return 82.0;
                if ("Engineering".equals(majorCategory))
                    return 75.0;
                return 82.0; // Rest of University (包含CS/DS) 要求 82%
            default:
                return 999.0; // NOT_LISTED 已经在主流程处理，此处返回非法高分作为兜底
        }
    }

    /**
     * 计算匹配分数 (0-100)
     */
    private double calculateMatchScore(MatchingRequest.StudentInfoDTO studentInfo, double requiredGpa, Major major) {
        double gpa = studentInfo.getGpa() != null ? studentInfo.getGpa() : 0.0;
        if (gpa == 0.0)
            return 0.0;

        double gpaScore;
        if (gpa >= requiredGpa + 2) {
            gpaScore = 95.0; // 远超要求
        } else if (gpa >= requiredGpa) {
            gpaScore = 85.0; // 达到要求
        } else if (gpa >= requiredGpa - 2) {
            gpaScore = 70.0; // 差距在2分以内 (可尝试argue)
        } else if (gpa >= requiredGpa - 5) {
            gpaScore = 50.0; // 差距较大
        } else {
            gpaScore = 30.0; // 差距过大
        }

        // 可以在此添加专业背景匹配逻辑 (Bonus)
        // 目前仅基于GPA
        return Math.min(100.0, gpaScore);
    }

    /**
     * 确定匹配等级
     * [FIX] 逻辑修正：分数高 = 稳妥/保底
     */
    private String determineMatchLevel(double score) {
        if (score >= 90) {
            return "保底"; // 远超要求，非常稳
        } else if (score >= 85) {
            return "稳妥"; // 达到要求
        } else if (score >= 70) {
            return "冲刺"; // 稍有差距，有风险但有机会
        } else {
            return "不建议"; // 差距过大
        }
    }

    /**
     * 计算录取概率 (0.0 - 1.0)
     */
    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.95;
        if (score >= 85)
            return 0.80;
        if (score >= 70)
            return 0.40;
        return 0.10;
    }

    /**
     * 计算推荐指数 (0.0 - 1.0)
     */
    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
