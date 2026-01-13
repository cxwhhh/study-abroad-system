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
 * 英国大学匹配算法
 * 基于英国大学的录取特点和要求进行匹配
 */
@Service
@SuppressWarnings("unused")
public class UKBirminghamMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKBirminghamMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String getSchoolName() {
        return "伯明翰大学";
    }

    @Override
    public String getRegion() {
        return "英国";
    }

    // G5大学
    private static final Set<String> G5_UNIVERSITIES = new HashSet<>(Arrays.asList(
            "University of Cambridge", "University of Oxford", "Imperial College London",
            "University College London", "London School of Economics and Political Science",
            "剑桥大学", "牛津大学", "帝国理工学院", "伦敦大学学院", "伦敦政治经济学院"));

    // 罗素集团大学
    private static final Set<String> RUSSELL_GROUP = new HashSet<>(Arrays.asList(
            "University of Birmingham", "University of Bristol", "University of Cambridge",
            "Cardiff University", "Durham University", "University of Edinburgh",
            "University of Exeter", "University of Glasgow", "Imperial College London",
            "King's College London", "University of Leeds", "University of Liverpool",
            "London School of Economics and Political Science", "University of Manchester",
            "Newcastle University", "University of Nottingham", "University of Oxford",
            "Queen Mary University of London", "Queen's University Belfast", "University of Sheffield",
            "University of Southampton", "University College London", "University of Warwick",
            "University of York",
            "伯明翰大学", "布里斯托大学", "剑桥大学", "卡迪夫大学", "杜伦大学", "爱丁堡大学",
            "埃克塞特大学", "格拉斯哥大学", "帝国理工学院", "伦敦国王学院", "利兹大学",
            "利物浦大学", "伦敦政治经济学院", "曼彻斯特大学", "纽卡斯尔大学", "诺丁汉大学",
            "牛津大学", "伦敦玛丽女王大学", "贝尔法斯特女王大学", "谢菲尔德大学",
            "南安普顿大学", "伦敦大学学院", "华威大学", "约克大学"));

    // 中国大学分组
    private static final Map<String, Set<String>> CHINESE_UNIVERSITY_GROUPS = new HashMap<>();

    static {
        // Group 1 - 985顶尖大学
        CHINESE_UNIVERSITY_GROUPS.put("Group 1", new HashSet<>(Arrays.asList(
                "清华大学", "北京大学", "浙江大学", "上海交通大学", "复旦大学", "南京大学", "中国科学技术大学", "武汉大学", "华中科技大学", "西安交通大学", "北京航空航天大学",
                "中山大学", "北京理工大学", "北京协和医学院", "哈尔滨工业大学", "四川大学", "东南大学", "中国人民大学", "同济大学", "北京师范大学", "天津大学", "西北工业大学",
                "山东大学", "南开大学", "厦门大学", "中国农业大学", "吉林大学", "中南大学", "大连理工大学", "湖南大学", "华东师范大学", "华南理工大学", "南方科技大学",
                "电子科技大学", "重庆大学", "北京科技大学", "上海财经大学", "首都医科大学", "南京航空航天大学", "东北大学", "南京理工大学", "兰州大学", "南方医科大学",
                "西安电子科技大学", "上海外国语大学", "北京交通大学", "哈尔滨工程大学", "华东理工大学", "东北师范大学", "中央财经大学", "苏州大学", "华中农业大学", "武汉理工大学",
                "中国石油大学（北京）", "郑州大学", "江西大学", "西南交通大学", "南京医科大学", "南京农业大学", "中国地质大学（武汉）", "对外经济贸易大学", "暨南大学", "中国海洋大学",
                "中国政法大学", "南京师范大学", "中国矿业大学", "天津医科大学", "中国医科大学", "北京邮电大学", "上海大学", "北京中医药大学", "浙江工业大学", "中南财经政法大学",
                "北京化工大学", "河海大学", "陕西师范大学", "西南财经大学", "西南大学", "中国石油大学（华东）", "深圳大学", "上海中医药大学", "云南大学", "西北大学", "北京工业大学",
                "南昌大学", "东华大学", "中国地质大学（北京）", "中国矿业大学（北京）", "广州医科大学", "哈尔滨医科大学", "福州大学", "宁波大学", "合肥工业大学", "温州医科大学",
                "中央民族大学", "南京邮电大学", "东北财经大学", "江苏大学", "北京外国语大学", "上海外国语大学", "华北电力大学", "中国传媒大学", "长安大学", "北京林业大学",
                "贵州大学", "重庆医科大学", "中国药科大学", "福建师范大学", "湖南师范大学", "广西大学", "南京林业大学", "扬州大学", "浙江师范大学", "海南大学", "南京中医药大学",
                "南京信息工程大学", "广东工业大学", "杭州电子科技大学", "外交学院", "安徽大学", "首都师范大学", "华南农业大学", "广州中医药大学", "中央音乐学院", "中国科学院大学",
                "中国社会科学院大学", "国防科技大学", "中国人民解放军空军军医大学", "中国人民解放军海军军医大学")));

        // Group 2 - 211大学
        CHINESE_UNIVERSITY_GROUPS.put("Group 2", new HashSet<>(Arrays.asList(
                "山西大学", "江西财经大学", "浙江工商大学", "河南大学", "太原理工大学", "山东师范大学", "大连医科大学", "河北医科大学", "广州大学", "浙江理工大学", "湘潭大学",
                "上海理工大学", "河北工业大学", "大连海事大学", "青岛大学", "燕山大学", "东北林业大学", "杭州师范大学", "西安建筑科技大学", "湖北大学", "福建医科大学",
                "昆明理工大学", "华东政法大学", "首都经济贸易大学", "西南政法大学", "内蒙古大学", "西安理工大学", "北京语言大学", "浙江中医药大学", "武汉科技大学", "天津中医药大学",
                "江西师范大学", "成都中医药大学", "东北农业大学", "西南石油大学", "上海师范大学", "福建农林大学", "温州大学", "长沙理工大学", "新疆大学", "广东外语外贸大学",
                "南京财经大学", "河北大学", "北京工商大学", "山东科技大学", "辽宁大学", "成都理工大学", "上海体育大学", "浙江财经大学", "山东农业大学", "宁夏大学", "华侨大学",
                "陕西科技大学", "安徽医科大学", "山西医科大学", "天津工业大学", "长春理工大学", "安徽师范大学", "中国人民公安大学", "石河子大学", "山东财经大学", "四川农业大学",
                "天津师范大学", "南京审计大学", "上海海洋大学", "江苏科技大学", "齐鲁工业大学", "上海对外经贸大学", "中国计量大学", "黑龙江大学", "广西医科大学", "安徽农业大学",
                "湖北工业大学", "中南民族大学", "浙江农林大学", "商丘大学", "河南农业大学", "重庆邮电大学", "常州大学", "广西师范大学", "湖南农业大学", "曲阜师范大学",
                "吉林农业大学", "徐州医科大学", "武汉纺织大学", "武汉工程大学", "河南师范大学", "天津科技大学", "辽宁师范大学", "山东第一医科大学", "江苏师范大学", "湖南中医药大学",
                "北京建筑大学", "汕头大学", "集美大学", "青岛科技大学", "三峡大学", "延边大学", "山东中医药大学", "沈阳药科大学", "沈阳农业大学", "河南科技大学", "天津财经大学",
                "中北大学", "济南大学", "华东交通大学", "上海海事大学", "辽宁中医药大学", "南昌航空大学", "河北师范大学", "西北师范大学", "长春中医药大学", "河南中医药大学",
                "天津理工大学", "北京体育大学", "宁夏医科大学", "西北政法大学", "湖南科技大学", "国际关系学院", "沈阳航空航天大学", "河南理工大学", "长江大学", "上海电力大学",
                "南华大学", "沈阳工业大学", "重庆交通大学", "安徽工业大学", "江西理工大学", "西南医科大学", "西南科技大学", "安徽理工大学", "山东理工大学", "西藏大学",
                "江西农业大学", "河北农业大学", "安徽财经大学", "青海大学", "兰州理工大学", "西安邮电大学", "安徽中医药大学", "新乡医学院", "桂林电子科技大学", "四川师范大学",
                "云南师范大学", "北京第二外国语学院", "北京信息科技大学", "黑龙江中医药大学", "烟台大学", "昆明医科大学", "苏州科技大学",
                "中南林业科技大学", "新疆医科大学", "兰州交通大学", "青岛理工大学", "滨州医学院", "江西中医药大学", "湖州师范学院", "西南民族大学", "西安工业大学", "上海政法学院",
                "哈尔滨师范大学", "东华理工大学", "北京电子科技学院", "湖北医药学院", "东北石油大学", "重庆师范大学", "山西财经大学", "沈阳建筑大学", "遵义医科大学", "内蒙古农业大学",
                "内蒙古师范大学", "大连工业大学", "广西中医药大学", "石家庄铁道大学", "河南工业大学", "嘉兴大学", "海南师范大学", "山西农业大学", "青岛农业大学", "福建中医药大学",
                "东北电力大学", "浙江海洋大学",
                "内蒙古医科大学", "贵州医科大学", "浙江科技大学", "成都大学", "哈尔滨理工大学", "上海工程技术大学", "吉林财经大学", "西安石油大学", "上海应用技术大学", "重庆工商大学",
                "广东财经大学", "西安外国语大学", "贵州师范大学", "河北科技大学", "佛山大学", "青海师范大学", "甘肃农业大学", "四川外国语大学", "东莞理工学院", "中国民航大学",
                "重庆理工大学", "北京物资学院", "首都体育学院", "华北水利水电大学", "中国刑事警察学院", "武汉轻工大学", "鲁东大学", "大连外国语大学", "赣南师范大学", "陕西中医药大学",
                "山东建筑大学", "江汉大学", "浙江大学城市学院", "河北经贸大学",
                "江苏警官学院", "聊城大学", "广西民族大学", "湖南工商大学", "沈阳化工大学", "桂林理工大学", "成都信息工程大学", "西安工程大学", "郑州轻工业大学", "上海立信会计金融学院",
                "安徽工程大学", "西华师范大学", "锦州医科大学", "大连交通大学", "南京工程学院", "上海海关学院", "辽宁石油化工大学", "沈阳理工大学", "绍兴文理学院", "延安大学",
                "河北中医药大学", "内蒙古工业大学", "沈阳大学", "蚌埠医学院", "大连民族大学", "浙江传媒学院", "厦门理工学院", "西华大学", "长春工业大学", "天津商业大学",
                "郑州航空工业管理学院", "沈阳师范大学", "贵州中医药大学", "大连大学", "华北理工大学", "吉林师范大学", "临沂大学", "淮北师范大学", "南宁师范大学", "盐城工学院",
                "川北医学院", "沈阳医学院", "广东药科大学", "湖北经济学院", "湖南工业大学", "桂林医学院", "中国人民警察大学", "井冈山大学", "山西师范大学", "辽宁工程技术大学",
                "北京农学院", "浙江广厦学院", "贵州财经大学", "福建理工大学", "浙江外国语学院", "常熟理工学院", "中原工学院", "山西中医药大学", "浙江警察学院", "合肥大学",
                "四川警察学院", "重庆科技大学", "台州学院", "太原科技大学", "黑龙江八一农垦大学", "武汉体育学院", "西藏藏医药大学", "云南中医药大学",
                "深圳技术大学", "南京警察学院", "西安财经大学", "云南财经大学", "河北工程大学", "辽宁科技大学", "北京联合大学", "北京石油化工学院", "杭州医学院", "新疆农业大学",
                "贵州民族大学", "皖南医学院", "辽宁工业大学", "北京印刷学院", "闽南师范大学", "沈阳体育学院", "淮阴工学院", "承德医学院", "闽江学院", "哈尔滨商业大学",
                "信阳师范大学")));

        // Group 3 - 其他一本大学
        CHINESE_UNIVERSITY_GROUPS.put("Group 3", new HashSet<>(Arrays.asList(
                "安徽大学", "合肥工业大学", "福州大学", "南昌大学", "郑州大学",
                "湖南师范大学", "华南师范大学", "广西大学", "海南大学",
                "西南交通大学", "四川农业大学", "贵州大学", "云南大学", "西北大学",
                "西安电子科技大学", "长安大学", "陕西师范大学", "青海大学", "宁夏大学",
                "新疆大学", "石河子大学", "内蒙古大学", "辽宁大学", "大连海事大学",
                "延边大学", "东北师范大学", "哈尔滨工程大学", "东北农业大学",
                "东北林业大学", "太原理工大学",
                "榆林学院", "新疆财经大学", "金陵科技学院", "山东交通学院", "南京体育学院", "湖北第二师范学院", "广东警官学院", "徐州工程学院", "天津农学院", "泉州师范学院",
                "牡丹江医学院", "湖北文理学院", "洛阳师范学院", "天津职业技术师范大学", "重庆警察学院", "齐齐哈尔医学院", "陕西理工大学", "湖南第一师范学院", "广州体育学院",
                "内蒙古民族大学", "合肥师范学院", "广东石油化工学院", "安徽科技学院", "绵阳师范学院", "亳州学院", "广东金融学院", "西安医学院", "贵阳学院", "惠州学院", "三明学院",
                "齐齐哈尔大学", "河北北方学院", "重庆三峡学院", "河南警察学院", "长春工程学院", "丽水学院", "衡阳师范学院", "西藏农牧学院", "兰州财经大学", "内蒙古科技大学包头医学院",
                "厦门医学院", "黔南民族师范学院", "湘南学院", "长春大学", "铜仁学院", "湖北科技学院", "洛阳理工学院", "中国民用航空飞行学院", "郑州警察学院", "阜阳师范大学",
                "佳木斯大学", "泰山学院", "山东体育学院", "黑龙江科技大学", "龙岩学院", "防灾科技学院", "成都师范学院", "内江师范学院", "滁州学院", "湖北工程学院", "山东政法学院",
                "周口师范学院", "河北科技师范学院", "江苏第二师范学院", "辽宁警察学院", "河北金融学院", "山东航空学院", "怀化学院", "云南警官学院", "沈阳工程学院", "福建江夏学院",
                "南昌学院", "湖南城市学院", "北华航天工业学院", "湖南工程学院", "曲靖师范学院", "南阳理工学院", "浙江水利水电学院", "潍坊学院", "宜宾学院", "德州学院", "皖西学院",
                "南阳师范学院", "北部湾大学", "贵州理工学院", "湖南科技学院", "福建警察学院", "湖南财政经济学院", "嘉兴南湖学院", "牡丹江师范学院", "宿迁学院", "太原师范学院",
                "邵阳学院", "广东第二师范学院", "玉林师范学院", "石家庄学院", "广西财经学院", "通化师范学院", "西安文理学院", "河南工程学院", "肇庆学院", "遵义师范学院",
                "吉林警察学院", "西安体育学院", "哈尔滨体育学院", "黑龙江工程学院", "昆明学院", "长治医学院", "宁德师范学院", "湖北警官学院", "武夷学院", "黄淮学院", "宜春学院",
                "吉林化工学院", "许昌学院", "吉林医药学院", "韩山师范学院", "上饶师范学院", "湖南警察学院", "宝鸡文理学院", "重庆第二师范学院", "乐山师范学院", "淮南师范学院",
                "温州理工学院", "安阳师范学院", "西昌学院", "广州航海学院", "河南城建学院", "齐齐哈尔师范学院", "湖州学院", "天水师范学院", "湖南人文科技学院", "唐山师范学院",
                "商丘师范学院", "成都工业学院", "六盘水师范学院", "武汉商学院", "赣州学院", "贺州学院", "内蒙古科技大学包头师范学院", "平顶山学院", "南昌师范学院", "宁夏师范学院",
                "吉林体育学院", "湖南工学院", "哈尔滨学院", "南昌医学院", "山东警察学院", "上海公安学院", "吉林工程技术师范学院", "廊坊师范学院", "西安航空学院", "信阳农林学院",
                "山西大同大学", "黄山学院", "嘉应学院", "江西警察学院", "福建技术师范学院", "四川旅游学院", "南京特殊教育师范学院", "宿州学院", "凯里学院", "海南热带海洋学院",
                "九江学院", "山东管理学院", "玉溪师范学院", "安阳工学院", "新乡学院", "铜陵学院", "河西学院", "太原工业学院", "山东女子学院", "伊犁师范大学", "河北建筑工程学院",
                "赤峰学院", "琼台师范学院", "咸宁学院", "苏州城市学院", "贵州警察学院", "营口理工学院", "大庆师范学院", "华北科技学院", "南京艺术学院", "山东艺术学院",
                "上海戏剧学院", "四川美术学院", "西安美术学院", "北京舞蹈学院", "广西艺术学院", "河北传媒学院", "湖北美术学院", "吉林艺术学院", "鲁迅美术学院", "内蒙古艺术学院",
                "山东工艺美术学院", "沈阳音乐学院", "四川音乐学院", "天津美术学院", "天津音乐学院", "武汉音乐学院", "西安音乐学院")));

        // Group 4 - 其他大学
        CHINESE_UNIVERSITY_GROUPS.put("Group 4", new HashSet<>(Arrays.asList(
                "阿坝师范学院", "安康学院", "安顺学院", "白城师范学院", "百色学院", "保定学院", "保山学院", "蚌埠学院", "亳州学院", "沧州师范学院", "昌吉学院", "长沙师范学院",
                "长治学院", "池州学院", "楚雄师范学院", "滇西应用技术大学", "鄂尔多斯应用技术学院", "黔南民族师范学院", "广西科技师范学院", "广西民族师范学院", "桂林航天工业学院",
                "贵州工程应用技术学院", "邯郸学院", "汉江师范学院", "河北环境工程学院", "河北民族师范学院", "河北水利电力学院", "河南工学院", "河南牧业经济学院", "衡水学院", "河套学院",
                "河源学院", "红河学院", "呼伦贝尔学院", "吉林农业科技学院", "荆楚理工学院", "景德镇学院", "集宁师范学院", "济宁学院", "晋中学院", "喀什大学", "兰州城市学院",
                "兰州工业学院", "兰州文理学院", "辽宁科技学院", "吕梁学院", "萍乡学院", "普洱学院", "山东农业工程学院", "山东青年政治学院", "山东石油化工学院", "商洛学院",
                "山西电子科技学院", "山西工程技术学院", "山西工学院", "山西科技学院", "山西能源学院", "陕西学前师范学院", "四川民族学院", "四川文理学院", "太原学院", "泰州学院",
                "唐山学院", "天津中德应用技术大学", "文山学院", "抚州学院", "邢台学院", "兴义民族师范学院", "新疆工程学院", "新疆理工学院", "新疆政法学院", "新余学院",
                "忻州师范学院", "运城学院", "张家口学院", "昭通学院", "郑州工程技术学院", "郑州师范学院", "福建商学院", "甘肃医学院", "广西警察学院", "桂林旅游学院", "贵州商学院",
                "哈尔滨金融学院", "河北体育学院", "河南财政金融学院", "河南体育学院", "呼和浩特民族学院", "湖南女子学院", "湖南医药学院", "吉林工商学院", "山西警察学院", "新疆警察学院",
                "新疆科技学院", "浙江树人学院", "吉林外国语大学", "珠海科技学院", "齐鲁理工学院", "山东协和学院", "无锡太湖学院", "武昌首义学院", "宁波财经学院", "浙江越秀外国语学院",
                "武昌理工学院", "长春财经学院", "西京学院", "黄河科技学院", "西安翻译学院", "大连东软信息学院", "武汉设计工程学院", "广州城市理工学院", "青岛城市学院", "武汉工商学院",
                "潍坊科技学院", "上海建桥学院", "武汉生物工程学院", "重庆人文科技学院", "成都城市学院", "安徽新华学院", "青岛黄海学院", "福州外语外贸学院", "广州南方学院", "茅台学院",
                "三江学院", "长春光华学院", "上海杉达学院", "吉林建筑科技学院", "北京城市学院", "长春科技学院", "烟台南山学院", "辽宁对外经贸学院", "沈阳工学院", "西安培华学院",
                "广州软件学院", "重庆财经学院", "广东石油学院", "长春电子科技学院", "成都文理学院", "齐鲁医药学院", "广州新科学院", "泉州信息工程学院", "广东科技学院", "长沙医学院",
                "郑州升达经贸管理学院", "泸州交通学院", "郑州科技学院", "武汉学院",
                "山东英才学院", "丽江文化旅游学院", "河南开封科技传媒学院", "三亚学院", "大连财经学院", "温州商学院", "西安欧亚学院", "西安外事学院", "青岛滨海学院", "广州理工学院",
                "宁夏理工学院", "黑龙江东方学院", "广州华商学院", "广州商学院", "文华学院", "沈阳城市学院", "辽宁财贸学院", "南宁学院", "西安明德理工学院", "中原科技学院",
                "郑州经贸学院", "燕京理工学院", "南昌工学院", "信阳学院", "厦门华厦学院", "郑州西亚斯学院", "天津仁爱学院", "潍坊理工学院", "阳光学院", "郑州工业应用技术学院",
                "皖江工学院", "长春人文学院", "烟台理工学院", "西安思源学院", "昆明城市学院", "辽宁何氏医学院", "海口经济学院", "湖南涉外经济学院", "哈尔滨华德学院", "长春建筑学院",
                "阜阳理工学院", "成都东软学院", "重庆对外经贸学院", "郑州商学院", "新乡工程学院", "郑州工商学院", "重庆城市科技学院", "商丘学院", "烟台科技学院", "广东培正学院",
                "东莞城市学院", "合肥经济学院", "沈阳科技学院", "四川工商学院", "安阳学院", "武汉华夏理工学院", "闽南理工学院", "南通理工学院", "云南工商学院", "江西科技学院",
                "广东东软学院", "厦门工学院", "上海立达学院", "南昌交通学院", "辽宁传媒学院", "沈阳城市建设学院", "湖北恩施学院", "大连科技学院", "安徽文达信息工程学院", "马鞍山学院",
                "郑州财经学院", "广州应用科技学院", "商丘工商学院", "星海音乐学院", "新疆艺术学院", "云南艺术学院", "浙江音乐学院", "中国戏曲学院", "安徽艺术学院")));
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        List<MatchingResult> results = new ArrayList<>();

        // 1. 查找伯明翰大学实体
        School school = dataService.getSchoolByName(getSchoolName());
        if (school == null) {
            // 尝试用英文名查找，增加鲁棒性
            List<School> ukSchools = dataService.getSchoolsByRegion("英国");
            school = ukSchools.stream()
                    .filter(s -> s.getEnglishName() != null
                            && s.getEnglishName().equalsIgnoreCase("University of Birmingham"))
                    .findFirst()
                    .orElse(null);

            if (school == null) {
                log.warn("未在数据库中找到伯明翰大学，跳过匹配");
                return results;
            }
        }
        // 2. 准备基础数据
        String studentMajorType = determineStudentMajorType(request.getStudentInfo());
        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        boolean hasMajorFilter = majorTokens != null && !majorTokens.isEmpty();
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        // 3. 遍历专业进行匹配
        for (Major major : majors) {
            String majorType = determineMajorType(major.getName());

            // 如果学生申请的是商科，但该专业不是商科，或者反之，是否需要跳过？
            // 您的原始逻辑是：!studentMajorType.equals(majorType) 就跳过。
            // 这里保留原逻辑，意味着：商科背景只能申商科，非商科只能申非商科。
            if (!hasMajorFilter && !studentMajorType.equals(majorType)) {
                continue;
            }

            MatchingResult result = calculateMajorMatch(school, major, request, majorType);
            if (result != null) {
                results.add(result);
            }
        }
        // 4. 排序
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
     * 计算单个学校的匹配结果
     */
    private MatchingResult calculateMajorMatch(School school, Major major, MatchingRequest request, String majorType) {
        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();

        String universityGroup = getUniversityGroup(studentInfo.getUndergraduateSchool());
        Map<String, Object> requirements = getAdmissionRequirements(school, majorType);

        double matchScore = calculateMatchScore(studentInfo, school, universityGroup, majorType, requirements,
                major.getName());
        String matchLevel = determineMatchLevel(matchScore);
        String matchReason = generateMatchReason(school, studentInfo, universityGroup, requirements);

        MatchingResult result = new MatchingResult();
        result.setUserId(studentInfo.getUserId());
        result.setSchoolId(school.getId());
        result.setSchoolName(school.getName());
        result.setMajorName(major.getName());
        result.setMatchScore(matchScore);
        result.setMatchLevel(matchLevel);
        result.setMatchReason(matchReason);
        result.setAdmissionProbability(calculateAdmissionProbability(matchScore));
        result.setRecommendationIndex(calculateRecommendationIndex(matchScore, school));
        result.setRegion("英国");
        result.setAlgorithmStrategy("UK_BIRMINGHAM_MATCHING_ALGORITHM");

        try {
            result.setStudentInfoSnapshot(objectMapper.writeValueAsString(studentInfo));
        } catch (Exception e) {
            result.setStudentInfoSnapshot("{}");
        }

        return result;
    }

    /**
     * 获取学生均分 (统一处理)
     */
    private double getStudentAverageScore(MatchingRequest.StudentInfoDTO student) {
        if (student.getGpa() == null)
            return 0.0;
        return convertGpaToPercentage(student.getGpa(), student.getGpaScale());
    }

    /**
     * 获取大学分组
     */
    private String getUniversityGroup(String universityName) {
        if (universityName == null || universityName.trim().isEmpty()) {
            return "Group 4";
        }

        for (Map.Entry<String, Set<String>> entry : CHINESE_UNIVERSITY_GROUPS.entrySet()) {
            if (entry.getValue().contains(universityName.trim())) {
                return entry.getKey();
            }
        }

        return "Group 4";
    }

    /**
     * 确定专业类型
     */
    private String determineMajorType(String major) {
        if (major == null || major.trim().isEmpty()) {
            return "other_masters";
        }

        String majorLower = major.toLowerCase();

        // 商科经济类专业
        if (majorLower.contains("business") || majorLower.contains("economics") ||
                majorLower.contains("management") || majorLower.contains("finance") ||
                majorLower.contains("accounting") || majorLower.contains("marketing") ||
                majorLower.contains("商") || majorLower.contains("经济") ||
                majorLower.contains("管理") || majorLower.contains("金融") ||
                majorLower.contains("会计") || majorLower.contains("市场")) {
            return "business_economics_non_quantitative";
        }

        return "other_masters";
    }

    /**
     * 获取分数要求
     */
    private double getRequiredScore(String group, String majorType) {
        // 伯明翰商学院要求
        if ("business".equals(majorType)) {
            switch (group) {
                case "Group 1":
                    return 80.0; // 985/211
                case "Group 2":
                    return 85.0; // Top 双非
                case "Group 3":
                    return 85.0; // 其他一本
                default:
                    return 999.0; // Group 4 通常不接受商科申请 (设为极高分)
            }
        }
        // 其他学院要求 (工程、教育等)
        else {
            switch (group) {
                case "Group 1":
                    return 75.0;
                case "Group 2":
                    return 80.0;
                case "Group 3":
                    return 80.0;
                case "Group 4":
                    return 85.0;
                default:
                    return 85.0;
            }
        }
    }

    private String determineStudentMajorType(MatchingRequest.StudentInfoDTO studentInfo) {
        String preferred = studentInfo.getTargetMajor();
        String undergrad = studentInfo.getUndergraduateMajor();
        String src = (preferred != null && !preferred.trim().isEmpty()) ? preferred : undergrad;
        return determineMajorType(src);
    }

    /**
     * 获取录取要求
     */
    private Map<String, Object> getAdmissionRequirements(School school, String majorType) {
        Map<String, Object> requirements = new HashMap<>();
        if ("business_economics_non_quantitative".equals(majorType)) {
            requirements.put("Group 1", 75.0);
            requirements.put("Group 2", 80.0);
            requirements.put("Group 3", 85.0);
            requirements.put("Group 4", "Reject"); // 伯明翰商学院通常不收 Group 4
        } else {
            requirements.put("Group 1", 73.0);
            requirements.put("Group 2", 78.0);
            requirements.put("Group 3", 83.0);
            requirements.put("Group 4", 87.0);
        }
        return requirements;
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO studentInfo, School school,
            String universityGroup, String majorType, Map<String, Object> requirements,
            String currentMajorName) {
        double totalScore = 0.0;
        double gpaScore = calculateGpaScore(studentInfo, universityGroup, requirements);
        totalScore += gpaScore * 0.4;
        double majorScore = calculateMajorScore(studentInfo.getTargetMajor(), currentMajorName);
        totalScore += majorScore * 0.25;
        double reputationScore = calculateReputationScore(school);
        totalScore += reputationScore * 0.2;
        double difficultyScore = calculateDifficultyScore(school, universityGroup);
        totalScore += difficultyScore * 0.15;
        return Math.min(100.0, Math.max(0.0, totalScore));
    }

    private double calculateGpaScore(MatchingRequest.StudentInfoDTO studentInfo, String universityGroup,
            Map<String, Object> requirements) {
        if (studentInfo.getGpa() == null)
            return 40.0;
        double gpa = convertGpaToPercentage(studentInfo.getGpa(), studentInfo.getGpaScale());
        Object requirement = requirements.get(universityGroup);

        if ("Reject".equals(requirement))
            return 0.0;

        double requiredGpa = (Double) requirement;
        if (gpa >= requiredGpa + 10)
            return 95.0;
        else if (gpa >= requiredGpa + 5)
            return 85.0;
        else if (gpa >= requiredGpa)
            return 75.0;
        else if (gpa >= requiredGpa - 5)
            return 60.0;
        else if (gpa >= requiredGpa - 10)
            return 40.0;
        else
            return 20.0;
    }

    private double convertGpaToPercentage(Double gpa, String gpaScale) {
        if (gpa == null)
            return 0.0;
        if ("100".equals(gpaScale))
            return gpa;
        if ("4".equals(gpaScale) || "4.0".equals(gpaScale))
            return (gpa / 4.0) * 100.0;
        if ("5".equals(gpaScale) || "5.0".equals(gpaScale))
            return (gpa / 5.0) * 100.0;
        return gpa;
    }

    private double calculateMajorScore(String targetMajor, String currentMajorName) {
        if (targetMajor == null || targetMajor.trim().isEmpty())
            return 70.0;
        String t = targetMajor.toLowerCase();
        String c = currentMajorName == null ? "" : currentMajorName.toLowerCase();
        if (c.contains(t))
            return 92.0;
        String tType = determineMajorType(targetMajor);
        String cType = determineMajorType(currentMajorName);
        if (tType.equals(cType))
            return 82.0;
        return 60.0;
    }

    private double calculateReputationScore(School school) {
        // 伯明翰是罗素集团成员，固定给高分
        return 85.0;
    }

    private double calculateDifficultyScore(School school, String universityGroup) {
        double baseScore = 60.0; // 罗素基准
        switch (universityGroup) {
            case "Group 1":
                baseScore += 20.0;
                break;
            case "Group 2":
                baseScore += 15.0;
                break;
            case "Group 3":
                baseScore += 10.0;
                break;
            case "Group 4":
                baseScore += 5.0;
                break;
        }
        return Math.min(95.0, baseScore);
    }

    private String determineMatchLevel(double matchScore) {
        // 统一标准：分数高 = 稳妥/保底
        if (matchScore >= 90)
            return "保底";
        if (matchScore >= 80)
            return "稳妥";
        if (matchScore >= 60)
            return "冲刺";
        return "不建议";
    }

    private String generateMatchReason(School school, MatchingRequest.StudentInfoDTO studentInfo,
            String universityGroup, Map<String, Object> requirements) {
        StringBuilder reason = new StringBuilder();
        reason.append("【伯明翰大学】").append("您的院校属于 ").append(universityGroup).append("。");

        Object requirement = requirements.get(universityGroup);
        if ("Reject".equals(requirement)) {
            reason.append(" 很遗憾，该专业通常不接受此类院校背景的申请。");
        } else {
            double requiredGpa = (Double) requirement;
            double studentGpa = convertGpaToPercentage(studentInfo.getGpa(), studentInfo.getGpaScale());
            reason.append(" 录取均分要求约 ").append(requiredGpa).append("%。")
                    .append(" 您的折算均分为 ").append(String.format("%.1f", studentGpa)).append("%。");

            if (studentGpa >= requiredGpa)
                reason.append(" 成绩达标。");
            else
                reason.append(" 成绩略有差距。");
        }
        return reason.toString();
    }

    private double calculateAdmissionProbability(double matchScore) {
        if (matchScore >= 90)
            return 0.95;
        if (matchScore >= 80)
            return 0.80;
        if (matchScore >= 60)
            return 0.40;
        return 0.10;
    }

    private double calculateRecommendationIndex(double matchScore, School school) {
        // 伯明翰基础推荐分
        double baseIndex = matchScore / 100.0;
        baseIndex += 0.05; // 罗素加成
        return Math.min(1.0, baseIndex);
    }
}
