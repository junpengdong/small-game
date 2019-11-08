package com.activity.smallgame.utils;

import com.activity.smallgame.request.UserProfileRequest;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Mr.dong
 * @create: 2019-08-20 16:43
 **/
public class ProveUtil {

    private static final String CHARACTER_ARR[] = {"a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public static String getRandomProve(int count) {
        if (count > CHARACTER_ARR.length) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int random = (int) (Math.random() * CHARACTER_ARR.length);
            sb.append(CHARACTER_ARR[random]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        /*String proveStr = getRandomProve(10);
        System.out.println("当前匹配证明：" + proveStr);
        Long compareCount = 0L;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("开始时间：" + simpleDateFormat.format(new Date()));
        Double total = 0d;
        while (true) {
            compareCount += 1;
            Long time = System.currentTimeMillis();
            String encryptStr = Encrypt.getMD5(time.toString());
            int result = compareStr(proveStr, encryptStr);
            if (result >= 4) {
                total += SettlementUtil.settleByProof(compareCount, 1);
                System.out.println("匹配最长数：" + result);
                System.out.println("计算总数：" + compareCount);
                System.out.println("获得奖励数：" + SettlementUtil.settleByProof(compareCount, 1));
                System.out.println("总奖励数：" + total);
                System.out.println("产出时间：" + simpleDateFormat.format(new Date()));
                System.out.println("------------------------------------------------------------");
                if (total >= 1) {
                    break;
                }
                System.out.println();
                proveStr = getRandomProve(10);
                System.out.println("刷新匹配证明：" + proveStr);
            }
        }

        System.out.println("结束时间：" + simpleDateFormat.format(new Date()));*/
        /*Long time = System.currentTimeMillis();
        System.out.println(time);
        Boolean flag = String.valueOf(time).endsWith("00");
        System.out.println(flag);*/
        /*while (true) {
            Long time = System.currentTimeMillis();
            System.out.println(time);
            Boolean flag = String.valueOf(time).endsWith("00");
            System.out.println(flag);
            if (flag) {
                break;
            }
        }*/
        /*List<UserProfileRequest> listA = new ArrayList<>();
        UserProfileRequest request = new UserProfileRequest();
        request.setName("1");
        request.setAuthCode("a");
        listA.add(request);
        request = new UserProfileRequest();
        request.setName("2");
        request.setAuthCode("b");
        listA.add(request);

        List<UserProfileRequest> listB = new ArrayList<>();
        request = new UserProfileRequest();
        request.setName("1");
        request.setAuthCode("a");
        request.setEmail("e1");
        listB.add(request);
        request = new UserProfileRequest();
        request.setName("2");
        request.setAuthCode("b");
        request.setEmail("e2");
        listB.add(request);

        listA.addAll(listB);

        Set<Object> set = new HashSet<>();
        set.addAll(listA);
        System.out.println(set.size());*/
        /*String str="-14-12-08";
        String birthReg="\\d{4}-\\d{2}-\\d{2}";//定义匹配规则
        Pattern p=Pattern.compile(path);//实例化Pattern
        Matcher m = p.matcher(str);//验证字符串内容是否合法
        if(m.matches())//使用正则验证
        {
            System.out.println("输入的日期格式合法！");
        }
        else
        {
            System.out.println("输入的日期格式不合法！");
        }*/
        /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse("-14-1-1");
            System.out.println(format.format(date));
        }catch (Exception e) {
            e.printStackTrace();
        }*/
        /*Set<String> set = new HashSet<>();
        set.add("g1008");
        set.add("g1048");
        System.out.println(new Gson().toJson(set));
        set.add("g1008");
        System.out.println(new Gson().toJson(set));
        System.out.println(set.contains("g1008"));
        System.out.println(set.contains("g1007"));*/
        /*Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        System.out.println(date.getTime());
        System.out.println(calendar.get(Calendar.DATE));
        calendar.set(Calendar.DAY_OF_MONTH, -30);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(calendar.getTime()));*/
        /*try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse("2019-10-01");
            Date end = format.parse("2019-10-12");
            System.out.println(getDateStringList(start, end));
        }catch (Exception e) {

        }*/



        String gameIdStr = "g1002,g1023,g1008,g1052,g1018,g1014,g1001,g1017,g1042";
        String[] gameIdArr = gameIdStr.split(",");

        String sql = "INSERT INTO game_package_name (create_time, game_id, game_name, package_name, publish_time) VALUES (now(), '%s', '%s', '', now());";

        for (String gameId : gameIdArr) {
            System.out.println(String.format(sql, gameId, gameId));
        }
    }

    public static List<String> getDateStringList(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        List<String> dateStrList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        while (calendar.getTime().before(endDate)) {
            dateStrList.add(format.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
        }

        return dateStrList;
    }

    private static int compareStr(String prove, String encryptStr) {
        int count = 0;
        for (int i = 0; i < encryptStr.length(); i++) {
            if (encryptStr.charAt(i) == prove.charAt(i)) {
                count += 1;
            }else {
                break;
            }
        }
        return count;
    }
}
