package com.express.realname.request;

import android.content.Context;
import android.os.Handler;


import android.telephony.TelephonyManager;
import com.express.realname.domain.Person;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class PostRequest {
    private Handler handler;
    private Person person = null;
    private String remoteIPB = null;
    TelephonyManager telephonyManager = null;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PostRequest(Context context, String remoteIPB, Person person) {
        handler = new Handler(context.getMainLooper());
        this.person = person;
        this.remoteIPB = remoteIPB;
         telephonyManager = (TelephonyManager) context .getSystemService(Context.TELEPHONY_SERVICE);
    }

    public interface OnPostListener {
        public void onPostOk(String msg);

        public void onPostErr(String msg);
    }

    public boolean post(String url)throws Exception{
        HttpPost httpRequest =new HttpPost(url);
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", person.getName().trim()));
        params.add(new BasicNameValuePair("address", person.getAddress().trim()));
        params.add(new BasicNameValuePair("birthday", person.getBirthday().trim()));
        params.add(new BasicNameValuePair("dn", person.getDN().trim()));
        params.add(new BasicNameValuePair("idCard", person.getIdCard().trim()));
        params.add(new BasicNameValuePair("nation", person.getNation().trim()));
        params.add(new BasicNameValuePair("sex", person.getSex().trim()));
        params.add(new BasicNameValuePair("shapeCode", person.getShapeCode().trim()));
        params.add(new BasicNameValuePair("signDepart", person.getSignDepart().trim()));
        params.add(new BasicNameValuePair("validTime", person.getValidTime().trim()));
        params.add(new BasicNameValuePair("latitude", person.getLatitude()));
        params.add(new BasicNameValuePair("longitude", person.getLongitude()));
        params.add(new BasicNameValuePair("sendTime", person.getSendTime()));
        params.add(new BasicNameValuePair("express", person.getExpress().trim()));
        params.add(new BasicNameValuePair("express_company", person.getExpress_company().trim()));
        params.add(new BasicNameValuePair("express_number", person.getExpress_number().trim()));
            httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            if(httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                JSONObject result = new JSONObject(strResult);
                boolean flag = result.getBoolean("success");
                return flag;
            }
        return false;
    }

    public boolean postJson(String url)throws Exception{
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost =new HttpPost(url);
        String IMEI = telephonyManager.getDeviceId();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"xm\":\""+person.getName().trim()+"\"").append(",");
        sb.append("\"xb\":\""+person.getSex().trim()+"\"").append(",");
        sb.append("\"mz\":\""+person.getNation().trim()+"\"").append(",");
        sb.append("\"csrq\":\""+person.getBirthday().trim()+"\"").append(",");
        sb.append("\"zz\":\""+person.getAddress().trim() + "\"").append(",");
        sb.append("\"gmsfhm\":\""+person.getIdCard().trim() + "\"").append(",");
        sb.append("\"fzjg\":\""+person.getSignDepart().trim() + "\"").append(",");
        sb.append("\"yxqx\":\""+person.getValidTime().trim() + "\"").append(",");
        sb.append("\"dn\":\""+person.getDN().trim() + "\"").append(",");
        sb.append("\"shapecode\":\""+person.getShapeCode().trim() + "\"").append(",");
        sb.append("\"longitude\":\""+person.getLongitude() + "\"").append(",");
        sb.append("\"latitude\":\""+person.getLatitude() + "\"").append(",");
        sb.append("\"sendtime\":\""+person.getSendTime().trim() + "\"").append(",");
        sb.append("\"express\":\""+person.getExpress().trim() + "\"").append(",");
        sb.append("\"expressNumber\":\""+person.getExpress_number().trim() + "\"").append(",");
        sb.append("\"expressCompany\":\""+person.getExpress_company().trim() + "\"").append(",");
        sb.append("\"cjsbbh\":\""+IMEI.trim()+"\"");
        sb.append("}");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("style","json" ));
        params.add(new BasicNameValuePair("func", "addSFXX"));
        params.add(new BasicNameValuePair("datetime", format.format(new Date())));
        params.add(new BasicNameValuePair("content",sb.toString()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
         HttpContext context = new BasicHttpContext();
        CookieStore cookieStore =new BasicCookieStore();
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        HttpResponse httpResponse = httpClient.execute(httpPost,context);
        int res = httpResponse.getStatusLine().getStatusCode();
        if (res == 200) {
            String strResult = EntityUtils.toString(httpResponse.getEntity());
            JSONObject result = new JSONObject(strResult);
            boolean flag = result.getBoolean("result");
            return flag;
        }
        return false;
    }


    public void postData(final OnPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://" + remoteIPB + ":8081/ExpressRealNameAction_upload.action";
                String url_json = "http://59.172.104.98:8099/wlsmz/appData/postAppData";

                boolean flag = false;
                try {
                    flag = post(url);
                    if (flag) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostOk("保存快递实名信息成功");
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostErr("保存快递实名信息失败");
                            }
                        });
                    }
                } catch (Exception e1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostErr("请求服务器异常！");
                        }
                    });
                }

                try {
                    flag = postJson(url_json);
                    if (flag) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostOk("保存快递实名JSON信息成功");
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostErr("保存快递实名JSON信息失败");
                            }
                        });
                    }
                } catch (Exception e1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostErr("请求JSON服务器异常！");
                        }
                    });
                }
            }
        }).start();
    }


}
