package src.com.util;

import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * ���ݽӿڵ�ַ������������
 * @author
 */
public class AutUtil {
    public static final String APPID = "wx1208695b805a5c58";//   //wx8f3cb4dc746055b8
    public static final String APPSECRET = "a706c7417f21d105f4618d8744b96186";//   //7432f922c9f39e1a9cf0b968dabac28f
    /**
     *����url��ȡjson����
     * @param url
     * @return  json����
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static JSONObject doget(String url) throws ClientProtocolException, IOException{
        JSONObject jsonObject = null;
        DefaultHttpClient client = new DefaultHttpClient();
        //ͨ��get��ʽ�ύ,�������url
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = client.execute(httpget);
        //����Ӧ�л�ȡ���
        HttpEntity entity = response.getEntity();
        if(entity!=null){
            String result = EntityUtils.toString(entity,"UTF-8");
            jsonObject = JSONObject.fromObject(result);
        }
        //httpget.releaseConnection();//�ͷ���������
        return jsonObject;
    }



}
