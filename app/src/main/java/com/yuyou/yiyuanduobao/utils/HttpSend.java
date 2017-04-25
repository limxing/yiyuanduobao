package com.yuyou.yiyuanduobao.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.leefeng.library.utils.LogUtils;


public class HttpSend {
    //private static Handler handler;
    public static class Constant {
        public static String TAG = "HttpSend";
    }

    public static String post(final String url, final String body) {
        //handler = dataHandler;

        Log.i(Constant.TAG, "url = " + url);

        String result = "";
        try {
            URL urll = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            conn.setRequestProperty("Charset", "UTF-8");
            conn.connect();
            DataOutputStream dos=new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(body);
            dos.flush();
            dos.close();
            //获得响应状态
            int resultCode=conn.getResponseCode();
            StringBuffer sb=new StringBuffer();
            if(HttpURLConnection.HTTP_OK==resultCode){

                String readLine=new String();
                BufferedReader responseReader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                while((readLine=responseReader.readLine())!=null){
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                System.out.println(sb.toString());
            }

//
//            OutputStream outStrm = conn.getOutputStream();
//            ObjectOutputStream objOutputStrm = new ObjectOutputStream(outStrm);
//            // 向对象输出流写出数据，这些数据将存到内存缓冲区中
//            objOutputStrm.writeObject(body);
//            // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
//            objOutputStrm.flush();
//            // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,
//            // 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器
//            objOutputStrm.close();
//            InputStream inStrm = conn.getInputStream();
//            BufferedReader reader=new BufferedReader(new InputStreamReader(inStrm));
//
//            StringBuffer sb = new StringBuffer();
//            String str = "";
//            while ((str = reader.readLine()) != null)
//            {
//                sb.append(str).append("\n");
//            }
//            Log.i(Constant.TAG, "post: "+sb);;
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
