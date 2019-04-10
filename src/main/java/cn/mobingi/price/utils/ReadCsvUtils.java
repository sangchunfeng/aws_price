package cn.mobingi.price.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReadCsvUtils {

    public static void getIndexJson() throws Exception {
        String indexURL = "https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AWSEvents/current/index.csv";
        URL url = new URL(indexURL);
        HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
        urlcon.connect();//获取连接
        InputStream is = urlcon.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        StringBuffer bs = new StringBuffer();
        String l = null;
        String everyLine = null;
        List<String> allString = new ArrayList<String>();
        while((l=buffer.readLine())!=null){
            everyLine = l;
            allString.add(everyLine);
        }
        System.out.println(allString.get(0));
    }

    public static void readCscInDir() {
        File csvDir = new File("/Users/sang/mobingi/tables"); // CSV文件路径
        File[] fileList = null;
        if (csvDir.exists()) {
           fileList = csvDir.listFiles();
        }
        BufferedReader br = null;
        String line = "";
        String everyLine = "";
        List<String> allString = new ArrayList<>();
        for (File file:fileList) {
            file.setReadable(true);
            file.setWritable(true);
            try {
                br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    everyLine = line;
                    allString.add(everyLine);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(allString.get(0));
    }

    public static void main(String[] args) throws Exception {
        ReadCsvUtils.getIndexJson();
    }

}
