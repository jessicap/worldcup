package src.com.service;

import net.sf.json.JSONObject;
import src.com.pojo.Ticket;
import src.com.pojo.Token;
import src.com.util.AutUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by jessica on 2018/6/20.
 */
public class shareBackService extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        addpv(req);
        System.out.println("nowurl first--:" + req.getParameter("nowurl"));


        //1����ȡ��ͨaccess_token;
        //Token token=getToken();
        Token token;
        String access_token="";
        String expires="";
        String access= getDatafromFile(req,"normal_access_token");
        Ticket ticket;
        String jsapi_ticket = getDatafromFile(req,"jsapi_ticket");
        long nowtime=0,firsttime=0;

        if(access==null||access==""){
            firsttime=System.currentTimeMillis()/1000;
            token=getToken();
            String data="{'normal_access_token':'"+token.getAccessToken()+"','expires_in':'"+token.getExpiresIn()+"','firsttime':'"+firsttime+"'}";
            saveDataToFile(req,"normal_access_token",data);
            access_token=token.getAccessToken();


            System.out.println("firsttime:"+firsttime+"data:"+data);

        }else{
            JSONObject json= JSONObject.fromObject(access);
            access_token= json.getString("normal_access_token");
            expires=json.getString("expires_in");
            firsttime=Long.parseLong(json.getString("firsttime"));
            nowtime=System.currentTimeMillis()/1000;
            if(nowtime-firsttime>7000){
                token=getToken();
                firsttime=nowtime;
                String data="{'normal_access_token':'"+token.getAccessToken()+"','expires_in':'"+token.getExpiresIn()+"','firsttime':'"+firsttime+"'}";
                saveDataToFile(req, "normal_access_token", data);


            }
            System.out.println("nowtime:" + nowtime + "firsttime:" + firsttime );

        }


        System.out.println("NORMAL-----access_token:" + access_token);
        //2����ȡtiket


        if(jsapi_ticket==null||jsapi_ticket==""){

            ticket=getTicket(access_token);
            String data="{'ticket':'"+ticket.getTicket()+"','expires_in':'"+ticket.getExpireSeconds()+"','firsttime':'"+firsttime+"'}";
            saveDataToFile(req,"jsapi_ticket",data);
            jsapi_ticket=ticket.getTicket();


        }else{
            JSONObject json= JSONObject.fromObject(jsapi_ticket);
            jsapi_ticket= json.getString("ticket");
            firsttime=Long.parseLong(json.getString("firsttime"));
            nowtime=System.currentTimeMillis()/1000;
            if(nowtime-firsttime>7000){
                ticket=getTicket(access_token);
                firsttime=nowtime;
                String data="{'ticket':'"+ticket.getTicket()+"','expires_in':'"+ticket.getExpireSeconds()+"','firsttime':'"+firsttime+"'}";
                saveDataToFile(req,"jsapi_ticket",data);
            }
        }






        //3��ʱ���������ַ���
        String noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//����ַ���
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//ʱ���

        System.out.println("accessToken:"+access_token+"\njsapi_ticket:"+jsapi_ticket+"\ntimestamp��"+timestamp+"\nnocestr��"+noncestr);
        //4����ȡurl
      //  String nowurl="http://www.jessicafood.club/testtest/welcome.html";

        String nowurl = URLDecoder.decode(req.getParameter("nowurl"), "UTF-8") ;
        System.out.println(nowurl);
        //5������������ƴ���ַ���
        String str = "jsapi_ticket="+jsapi_ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+nowurl;
        //6�����ַ�������sha1����
        String signature =SHA1(str);
        System.out.println("character��"+str+"\nsignature��"+signature);
        String logoimgurl="http://laxback.duapp.com/worldcup/img/logo.png";
        String shareurl="http://laxback.duapp.com/worldcup/index.html";
        String appId= AutUtil.APPID;
        JSONObject obj = new JSONObject();

        obj.put("appId",appId);
        obj.put("jsapi_ticket",jsapi_ticket);
        obj.put("timestamp",timestamp);
        obj.put("nonceStr",noncestr);
        obj.put("signature", signature);
        obj.put("shareurl",shareurl);
        obj.put("logoimgurl",logoimgurl);
        System.out.println(obj.toString());
        resp.getWriter().write(obj.toString());
     //   String url="http://www.jessicafood.club/testtest/welcome.html";
      //  resp.sendRedirect(url);


    }



    public  void addpv(HttpServletRequest req){
        String pv= getDatafromFile(req,"pageview");
        int pageview;
        if(pv==null||pv==""){
                pageview=1;
            String data="{'pv':'"+pageview+"'}";
            saveDataToFile(req, "pageview", data);




        }else{
            JSONObject json= JSONObject.fromObject(pv);
            pageview= Integer.parseInt(json.getString("pv"));
            pageview++;
            String data="{'pv':'"+pageview+"'}";
            saveDataToFile(req, "pageview", data);

            System.out.println("pageview:"+pageview);


        }




    }




    /**
     * ͨ����ͨ��Ȩ��ȡaccess_token

     * @return  Token
     */
    public Token getToken() throws ServletException, IOException{
        Token token=null;


        //ͨ��code�õ�access_tokenƱ��
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ AutUtil.APPID
                + "&secret="+AutUtil.APPSECRET;
        JSONObject jsonObject = AutUtil.doget(url);

        if (null != jsonObject) {
            try {
                token = new Token();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getInt("expires_in"));

            } catch (Exception e) {
                token = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                System.out.println("��ͨ��Ȩƾ֤ʧ�� errcode:{"+errorCode+"} errmsg:{"+errorMsg+"}");
            }
        }

        return token;
    }

    /**
     * ͨ��accessToken��ȡticket��Ϣ
     * @param accessToken ��ҳ��Ȩ�ӿڵ���ƾ֤
     * @return ticket
     */
    @SuppressWarnings( { "deprecation", "unchecked" })
    public static Ticket getTicket(String accessToken) throws ServletException, IOException {
        Ticket ticket = null;
        System.out.println(" IN ticket ---access_token:"+accessToken);
        // ƴ�������ַ
        String requestUrl 		= "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        requestUrl 		  		= requestUrl.replace("ACCESS_TOKEN", accessToken);
        // ͨ����ҳ��Ȩ��ȡticket
        JSONObject jsonObject 	=  AutUtil.doget(requestUrl);
        System.out.println("--ticket--:" + jsonObject.getString("ticket"));

        if (null != jsonObject) {
            try {
                ticket=new Ticket();
                //ticket
                ticket.setTicket(jsonObject.getString("ticket"));
                //��Чʱ��
                ticket.setExpireSeconds(jsonObject.getInt("expires_in"));

            } catch (Exception e) {
                ticket = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                System.out.println("��ȡtikcet��Ϣʧ�� errcode:{" + errorCode + "} errmsg:{" + errorMsg + "}");
            }
        }
        return ticket;
    }


    /**
     * ͨ���ַ�����ȡSHA1���ܽ�������ǩ�� ǩ���㷨
     * @param decript ƴ�Ӻ���ַ�����Ϣ
     * @return ticket
     */
    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // �ֽ�����ת��Ϊ ʮ������ ��
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
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
