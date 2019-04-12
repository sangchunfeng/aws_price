package cn.mobingi.price.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>读取网页CSV价格文件工具类</p>
 * @author sang
 * @date 2019-04-11
 * @version 1.0
 */
public class ReadCsvUtils {

    /**
     * <p>将CSV文件头中的字符串转换为首字母小写</p>
     * @param str 要转换的字符串
     * @return 返回首字母小写的字符串
     */
    private static String toLowerCaseFirstOne(String str){
        if(Character.isLowerCase(str.charAt(0))) {
            return str;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
        }
    }

    /**
     * <p>将字符串中的大写字母转换为小写字母，并添加下划线，组成表的字段名</p>
     * @param str 要转换的字符串
     * @return 返回正确的表字段
     */
    private static String upperCaseToLowerCase(String str) {
        str = str.replace(" ","").trim();
        str = toLowerCaseFirstOne(str);
        char[] chars = str.toCharArray();
        List<String> transfer = new ArrayList<>();
        for(int i = 0 ; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                chars[i] ^= 0x20;
                transfer.add("_" + chars[i]);
            } else {
                transfer.add(String.valueOf(chars[i]));
            }
        }
        StringBuffer sb = new StringBuffer();
        for(String word: transfer){
            sb.append(word);
        }
        return sb.toString().trim();
    }

    public static List<String> getCsvHttpByIndex() throws Exception {
        String indexURL = "https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/index.json";
        URL url = new URL(indexURL);
        HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
        urlcon.connect();//获取连接
        InputStream is = urlcon.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        String line;
        List<String> csvURLList  = new ArrayList<>();
        while((line = buffer.readLine())!=null){
            line = line.trim().replace(" ","");
            if (line.contains("currentVersionUrl")) {
                line = line.split(":")[1].substring(1,line.split(":")[1].length() - 2);
                csvURLList.add(line);
            }
        }
        return csvURLList;
    }

    /**
     * <p>从价格CSV文件中获取数据库表名，字段名以及数据</p>
     * @return 返回数据库表名，字段名，数据组成的Map
     * @throws Exception 可能产生的异常
     */
    public static Map<String,Object> getTableNameAndTableData(String csvURL) throws Exception {
        String indexURL = "https://pricing.us-east-1.amazonaws.com" + csvURL.replace("json","csv");
        URL url = new URL(indexURL);
        HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
        urlcon.connect();//获取连接
        InputStream is = urlcon.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        String line;
        List<String> allString = new ArrayList<>();
        while((line = buffer.readLine())!=null){
            allString.add(line);
        }
        String tableName = allString.get(4).split(",")[1].toLowerCase();
        String[] fields = allString.get(5).split(",");
        List<String> lowerFields = new ArrayList<>();
        for (String title:fields) {
            title = title.substring(1, title.length() - 1);
            lowerFields.add(upperCaseToLowerCase(title));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("tableName", tableName.substring(1,tableName.length() - 1));
        map.put("fields", lowerFields);
        map.put("data", allString.subList(6, allString.size()));
        return map;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(ReadCsvUtils.getCsvHttpByIndex());
    }

}
