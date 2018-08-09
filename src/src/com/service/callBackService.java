package src.com.service;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import src.com.pojo.SNSUserInfo;
import src.com.pojo.WeixinOauth2Token;
import src.com.util.AutUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * �ص���ַ
 * @author
 *
 */
//@WebServlet("/callBack")
public class callBackService extends HttpServlet{
    public static final String ACCESS_TOKEN = "";//

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

      //  WeixinOauth2Token jsonObject =getOauth2Token(req,resp) ;

        WeixinOauth2Token jsonObject;
        String access_token="";
        String openid="";

        // String access= getDatafromFile(req,"access_token");

       // if(access==null||access==""){

            jsonObject=getOauth2Token(req, resp);
            String data="{'access_token':'"+jsonObject.getAccessToken()+"','expires_in':'"+jsonObject.getExpiresIn()+"'}";
            saveDataToFile(req,"access_token",data);
            access_token=jsonObject.getAccessToken();
            openid=jsonObject.getOpenId();


        /*}else{
            JSONObject json= JSONObject.fromObject(access);
            access_token= json.getString("access_token");




        }
*/








        SNSUserInfo  userinfo=getSNSUserInfo(access_token,openid);
       // Token token=getToken();

        System.out.println("WEB-----access_token:"+access_token);
     //
        /*System.out.println(jsonObject.getString("access_token"));
        System.out.println(jsonObject);
        String openid = jsonObject.getString("openid");
        System.out.println(openid);
        String access_token = jsonObject.getString("access_token");
        //��ȡ�û���Ϣ�ӿ�
        String infourl = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token
                + "&openid="+openid
                + "&lang=zh_CN";
        JSONObject userinfo = AutUtil.doget(infourl);
        System.out.println(userinfo);*/
        System.out.println(getSNSUserInfo(access_token,openid).getCity());
        System.out.println(getSNSUserInfo(access_token,openid).getLanguage());

        String strURL="http://115.233.208.56/worldcup/UserServlet/addUser";
               openid=userinfo.getOpenId();
        String nickname=userinfo.getNickname();
        int sex=userinfo.getSex();
        String language=userinfo.getLanguage();
        String city=userinfo.getCity();
        String province=userinfo.getProvince();
        String country=userinfo.getCountry();
        String headimgurl=userinfo.getHeadImgUrl();
        List privilege=userinfo.getPrivilegeList();
        recStatus(strURL,openid,nickname,sex,language,city,province,country,headimgurl,privilege);



        String url="http://laxback.duapp.com/worldcup/index.html?redirect=true&openid="+openid ;
              /*  "appId="+obj.getString("appId")
                +"&jsapi_ticket=" +obj.getString("jsapi_ticket")
                +"&timestamp="+obj.getString("timestamp")
                +"&nonceStr="+obj.getString("nonceStr")
                +"&signature="+obj.getString("signature");
*/

        resp.sendRedirect(url);


    }


    protected String recStatus(String strURL, String openid, String nickname,int sex,String language,String city,String province,String country,String headimgurl,List privilege){

        try{

            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept","application/json");
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("wechat_id",openid);
            jsonObject.put("wechat_nickname",nickname);
            jsonObject.put("wechat_sex",sex);
            jsonObject.put("wechat_language",language);
            jsonObject.put("wechat_city",city);
            jsonObject.put("wechat_province",province);
            jsonObject.put("wechat_country",country);
            jsonObject.put("wechat_headimgurl", headimgurl);

            out.append(jsonObject.toString());
            out.flush();
            out.close();
            System.out.println(connection.getResponseCode());

            int responseCode=connection.getResponseCode();
            String encode="UTF-8";

            if (responseCode == 200) {
                // ȡ����Ӧ�Ľ��
                return changeInputStream(connection.getInputStream(),
                       encode);
            }

            System.out.println(connection.getResponseMessage());
            System.out.println(changeInputStream(connection.getInputStream(),encode));

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ��һ��������ת����ָ��������ַ���
     *
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeInputStream(InputStream inputStream,
                                            String encode) {

        // �ڴ���
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = null;
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);
                }
                result = new String(byteArrayOutputStream.toByteArray(), encode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * ͨ����ҳ��Ȩ��ȡaccess_token
     * @param req ����
     * @param resp ��Ӧ
     * @return  WeixinOauth2Token
     */
    public WeixinOauth2Token getOauth2Token(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        WeixinOauth2Token wat = null;

        //΢�ŷ���������callBack�����code
        String code = req.getParameter("code");
        System.out.println(code);
        //ͨ��code�õ�access_tokenƱ��
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ AutUtil.APPID
                + "&secret="+AutUtil.APPSECRET
                + "&code="+code
                + "&grant_type=authorization_code";
        JSONObject jsonObject = AutUtil.doget(url);

        if (null != jsonObject) {
            try {
                wat = new WeixinOauth2Token();
                wat.setAccessToken	(jsonObject.getString("access_token"));
                wat.setExpiresIn	(jsonObject.getInt("expires_in"));
                wat.setRefreshToken	(jsonObject.getString("refresh_token"));
                wat.setOpenId		(jsonObject.getString("openid"));
                wat.setScope		(jsonObject.getString("scope"));
            } catch (Exception e) {
                wat = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                System.out.println("��ҳ��Ȩƾ֤ʧ�� errcode:{" + errorCode + "} errmsg:{" + errorMsg + "}");
            }
        }

        return wat;



    }







    /**
     * ͨ����ҳ��Ȩ��ȡ�û���Ϣ
     * @param accessToken ��ҳ��Ȩ�ӿڵ���ƾ֤
     * @param openId �û���ʶ
     * @return SNSUserInfo
     */
    @SuppressWarnings( { "deprecation", "unchecked" })
    public static SNSUserInfo getSNSUserInfo(String accessToken, String openId) throws ServletException, IOException {
        SNSUserInfo snsUserInfo = null;
        // ƴ�������ַ
        String requestUrl 		= "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        requestUrl 		  		= requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // ͨ����ҳ��Ȩ��ȡ�û���Ϣ
        JSONObject jsonObject 	=  AutUtil.doget(requestUrl);
        System.out.println("nickname:"+jsonObject.getString("nickname"));
        if (null != jsonObject) {
            try {
                snsUserInfo = new SNSUserInfo();
                // �û��ı�ʶ
                snsUserInfo.setOpenId(jsonObject.getString("openid"));
                // �ǳ�
                snsUserInfo.setNickname(jsonObject.getString("nickname"));
                // �Ա�1�����ԣ�2��Ů�ԣ�0��δ֪��
                snsUserInfo.setSex(jsonObject.getInt("sex"));
                // �û����ڹ���
                snsUserInfo.setCountry(jsonObject.getString("country"));
                // �û�����ʡ��
                snsUserInfo.setProvince(jsonObject.getString("province"));
                // �û����ڳ���
                snsUserInfo.setCity(jsonObject.getString("city"));
                // �û�����
                snsUserInfo.setLanguage(jsonObject.getString("language"));
                // �û�ͷ��
                snsUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
                // �û���Ȩ��Ϣ
                snsUserInfo.setPrivilegeList(JSONArray.toList(jsonObject.getJSONArray("privilege"), List.class));
            } catch (Exception e) {
                snsUserInfo = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                System.out.println("��ȡ�û���Ϣʧ�� errcode:{" + errorCode + "} errmsg:{" + errorMsg + "}");
            }
        }
        return snsUserInfo;
    }

    private   void saveDataToFile(HttpServletRequest request,String fileName,String data) {
        BufferedWriter writer = null;

        ServletContext context = request.getSession().getServletContext();
        String fullPath = context.getRealPath("/WEB-INF/" + fileName + ".json");

        File file = new File(fullPath);
        //����ļ������ڣ����½�һ��
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //д��
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("�ļ�д��ɹ���");
    }

    private String getDatafromFile(HttpServletRequest request,String fileName) {

        ServletContext context = request.getSession().getServletContext();
        String fullPath = context.getRealPath("/WEB-INF/"+fileName+".json");
        System.out.println(fullPath);
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(fullPath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }
}
