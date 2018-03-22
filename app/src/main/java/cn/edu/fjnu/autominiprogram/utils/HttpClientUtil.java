package cn.edu.fjnu.autominiprogram.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class HttpClientUtil {  
    public static String sendHttpRequest(String httpUrl, JSONObject obj, String requestMethod) {
    	 StringBuffer sb = new StringBuffer("");
    	 try {
			//创建连接
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod(requestMethod);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
//			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.connect();
			
			 //POST请求
			if(obj != null) {
				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
	            System.out.println("====obj.toString()====" + obj.toString());
	            out.writeBytes(obj.toString());
	            out.flush();
	            out.close();
			}
            
            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            reader.close();
            // 断开连接
            connection.disconnect();
            
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return sb.toString();
    }
       
//    public static void main(String[] args) {
//    	String url = "http://api.open.wowgotcha.com/openapi/v1/users";
//    	
//    	JSONObject obj = new JSONObject();
//    	obj.element("appid", "wowa4f120a4f1thw5max06y");
//    	obj.element("binding_id", "1");
//    	
//    	String result = sendHttpRequest(url, obj, "POST");
//    	System.out.println(result);
//    	
//    	JSONObject jsonobject = JSONObject.fromObject(result);
//    	UserResponse response =  (UserResponse)JSONObject.toBean(jsonobject,UserResponse.class);
//    	System.out.println("=========" + response.getData().getUser().getId());
//	}
}  