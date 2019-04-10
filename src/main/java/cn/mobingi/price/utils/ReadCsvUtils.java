package cn.mobingi.price.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadCsvUtils {

    public static Map<String,Object> getTableNameAndTableData() throws Exception {
        String indexURL = "https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AWSEvents/current/index.csv";
        URL url = new URL(indexURL);
        HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
        urlcon.connect();//获取连接
        InputStream is = urlcon.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        String line = null;
        List<String> allString = new ArrayList<>();
        while((line = buffer.readLine())!=null){
            allString.add(line);
        }
        String tableName = allString.get(4).split(",")[1].toLowerCase();
        String[] fields = allString.get(5).split(",");
        Map<String,Object> map = new HashMap<>();
        map.put("tableName",tableName);
        map.put("fields",fields);
        return map;
    }

    public static void main(String[] args) throws Exception {
        ReadCsvUtils.getTableNameAndTableData();
    }

}
