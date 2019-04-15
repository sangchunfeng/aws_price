package cn.mobingi.price.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * <p>读取网页CSV价格文件工具类</p>
 * @author sang
 * @date 2019-04-11
 * @version 1.0
 */
@Component
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

    /**
     * <p>获取AWS index.json文件，通过它获取每个服务价格表的URL</p>
     * @return 返回每个服务的价格表URL
     * @throws Exception 可能出现的异常
     */
    public static List<String> getCsvHttpByIndex() throws Exception {
        String indexURL = "https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/index.json";
        URL url = new URL(indexURL);
        HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
        urlcon.setConnectTimeout(3 * 1000);
        urlcon.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
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
    public static Map<String,Object> getTableNameAndTableData(String csvURL) {
        OutputStream os = null;
        try {
            String indexURL = "https://pricing.us-east-1.amazonaws.com" + csvURL.replace("json","csv");
            //String indexURL = "https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonRedshift/current/index.csv";
            URL url = new URL(indexURL);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setConnectTimeout(3 * 1000);
            urlcon.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            urlcon.connect();//获取连接
            InputStream is = urlcon.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            String line;
            List<String> allString = new ArrayList<>();
            while ((line = buffer.readLine()) != null) {
                allString.add(line.replace("\"", ""));
            }
            String tableName = allString.get(4).split(",")[1].toLowerCase();
            String[] fields = allString.get(5).split(",");
            List<String> lowerFields = new ArrayList<>();
            for (String title : fields) {
                lowerFields.add(upperCaseToLowerCase(title));
            }
            downloadFile(indexURL,"/Users/sang/Desktop/testSQL/",tableName + ".csv");
            Map<String, Object> map = new HashMap<>();
            map.put("tableName", tableName);
            map.put("fields", lowerFields);
            map.put("filePath", "/Users/sang/Desktop/testSQL/" + tableName + ".csv");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>下载AWS服务价格CSV文件至文件夹</p>
     * @param httpURL 网址URL
     * @param path 文件存储路径
     * @param fileName 文件名
     * @throws Exception 可能出现的异常
     */
    private static void downloadFile(String httpURL,String path,String fileName) throws Exception {
        URL url = new URL(httpURL);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(path);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos != null){
            fos.close();
        }
        if(inputStream != null){
            inputStream.close();
        }

    }

    private static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * <p>产出保存CSV文件的文件夹</p>
     * @param path 文件夹路径
     * @return 返回是否删除成功
     */
    public static boolean dropDir(String path) {
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        if(file.isFile()){
            return file.delete();
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if(f.isFile()){
                if(!f.delete()){
                    System.out.println(f.getAbsolutePath()+" delete error!");
                    return false;
                }
            }else{
                if(!dropDir(f.getAbsolutePath())){
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static void main(String[] args) throws Exception {
        //ReadCsvUtils.downloadFile();
        dropDir("/Users/sang/Desktop/aaa/");
        System.out.println("");
    }

}
