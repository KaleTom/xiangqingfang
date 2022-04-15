package com.zg.xqf.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zg.xqf.zego.Zego;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    public final static String TAG = "Utils";

    public static void toast(Activity activity, String msg) {

        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String randomName(boolean isMan) {
        String[] firstList = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜"};
        String[] manList = {"攀晟", "进峰", "玮城", "俊引", "炎遥", "博波", "顷朋", "烨承", "均桐", "慈昭", "霜来", "岳齐", "征祖", "秉本", "昂楠", "唯里", "鸣标", "信怀", "余永", "坤遥", "枝杭", "义翼", "凌影", "锋信", "振凌", "良锐", "浦冠", "瀚振", "本秉", "苑鸽", "散刚", "展忠", "帆谨", "宇音", "桐磊", "圆耿", "扬瑞", "强川", "楠怀", "旭坤", "鼎晓", "雁池", "卫坤", "皓钧", "彰乔", "顺古", "函朋", "朴毅", "存祖", "康鹏", "豫暖", "宇雄", "青颜", "欢伟", "傲德", "新挚", "流致", "净霜", "万宝", "义少", "善常", "孝羽", "宇伯", "物运", "安尊", "浦福", "悠峰", "田东", "肃励", "啸迪", "悠千", "游炫", "栋杭", "西净", "商漾", "青钟", "来量", "封桦", "陌信", "恭施", "磊恭", "瑞志", "曲融", "凡傲", "冰书", "翔勇", "润瞻", "昙钟", "初嘉", "延名", "锦誓", "烨普", "隆凡", "治谨", "楷生", "肖冬", "桦皓", "吉群", "标昆", "朴勋", "誓星", "飘钱", "泉论", "哲翰", "钱海", "散翔", "煊散", "修振", "诚林", "路晋", "翎奉", "萧少", "承雨", "韬雷", "织喻", "迁统", "宝捷", "吟廉", "莫丰", "琢楠", "里逸", "拂勉", "壮统", "杉莫", "均任", "洋秉", "川棕", "向惜", "意琦", "祺标"};
        String[] womanList = {"嫣瑜", "涵笛", "霄沛", "露淑", "娅迪", "梅曼", "超岚", "邑蛟", "艳泽", "玲波", "薇冬", "雯瑶", "慧云", "浩夜", "海沛", "娟采", "嫣珊", "芬沛", "楠柳", "正黛", "依莹", "春兔", "萍珊", "涵莹", "海茵", "令欢", "欢邑", "露沁", "翠馥", "柯涵", "甜萍", "傲怜", "安韵", "岚婉", "嫣桑", "琦芝", "又迪", "盈媚", "敏竹", "荷迪", "怡琦", "初语", "羽新", "菊薇", "明茜", "芍佩", "皎艳", "葵虞", "瑶碧", "荣婷", "雯凉", "妹羽", "玲缘", "夏亦", "笑瑾", "璐曼", "悠婉", "觅歆", "蝶舞", "痴娥", "妍梦", "丽红", "韵仙", "泽影", "菱筠", "痴柏", "沛普", "沁凝", "艳依", "沐虹", "真莺", "璐萍", "绿林", "普迎", "波婷", "真痴", "婷彦", "梅芹", "阳娟", "铃任", "露蝶", "琳薇", "婷妮", "优阳", "帆菡", "曦蝶", "恬荣", "傲冰", "初翔", "醉凌", "杏慕", "婉靖", "向琴", "采丹", "羽海", "玉媛", "寒佳", "夜筠", "槐润", "筠瑜", "兔莉", "瑾竹", "傲靖", "万冰", "艺妙", "妍娇", "菱净", "霞灵", "露洁", "韵润", "慕琳", "缘问", "莹浩", "旋琼", "芹茜", "悠泽", "彦缘", "珊沁", "姗桂", "自柯", "丹盈", "瑾霜", "爽宁", "珠怜", "枝幼", "瑾忆", "清瑜", "浩芳", "瑜寒", "雁彦", "清痴", "夏佑", "蕊翔", "邑缦", "醉笑", "可爽", "沐娟", "虞醉", "妍任", "丝荷", "菊凉", "珠宛", "妙竹", "夏雨", "凉翠", "珍娅", "蛟靖", "雪冬", "翠怡", "玲歆", "蛟惠"};
        String first = firstList[Zego.randInt(0, firstList.length)];
        String second = isMan ? manList[Zego.randInt(0, manList.length)] : womanList[Zego.randInt(0, womanList.length)];
        return first + second;
    }

    public static String randProvince() {
        String[] provices = {"河北省", "山西省", "辽宁省", "吉林省", "黑龙江省", "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省", "广东省", "海南省", "四川省", "贵州省", "云南省", "陕西省", "甘肃省", "青海省", "台湾省", "内蒙古自治区", "广西壮族自治区", "西藏自治区", "宁夏回族自治区", "新疆维吾尔自治区", "北京市", "天津市", "上海市", "重庆市", "香港特别行政区", "澳门特别行政区"};

        return provices[Zego.randInt(0, provices.length)];
    }

    public static Object getJsonWithDef(JSONObject json, String key, Object defVal) {
        String[] keyArr = key.split("/");
        Object out = defVal;
        try {
            for (int i = 0; i < keyArr.length; ++i) {
                String k = keyArr[i];
                if (i == keyArr.length - 1) out = json.get(k);
                else {
                    if (k.endsWith("]")) {
                        int left = k.indexOf('[');
                        String sk = k.substring(0, left);
                        String cond = k.substring(left + 1, k.length() - 1);
                        String[] condArr = cond.split("=");
                        JSONArray arr = json.getJSONArray(sk);
                        boolean isFound = false;
                        int len = arr.length();
                        for (int j = 0; j < len; ++j) {
                            JSONObject item = arr.getJSONObject(j);
                            if (item.get(condArr[0]).toString().equals(condArr[1])) {
                                json = item;
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) {
                            Log.e(TAG, cond + " is not found in " + arr.toString());
                            return defVal;
                        }
                    } else {
                        json = (JSONObject) json.get(k);
                    }
                }
            }
        } catch (JSONException e) {
            return defVal;
        }
        return out;
    }

    /**
     * 在Json获取指定key的val，字符串类型，
     */
    public static String getStr(JSONObject json, String key, String defVal) {
        Object obj = getJsonWithDef(json, key, defVal);
        return obj.toString();
    }

    /**
     * 在Json获取指定key的val，整数类型，
     */
    public static int getInt(JSONObject json, String key, int defVal) {
        Object obj = getJsonWithDef(json, key, defVal);
        return (Integer) obj;
    }

}
