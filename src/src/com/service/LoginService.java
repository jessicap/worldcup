package src.com.service;


import src.com.util.AutUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

//@WebServlet("/wxLogin")
public class LoginService extends HttpServlet {
    //@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        //���ûص���ַ(ע��������ڹ����Ϸ���)
        System.out.print("dddd");
        String backUrl = "http://laxback.duapp.com/worldcup/testtest/callBack";
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ AutUtil.APPID
                + "&redirect_uri="+URLEncoder.encode(backUrl, "utf-8")
                + "&response_type=code"
                + "&scope=snsapi_userinfo"
                + "&state=STATE#wechat_redirect";
        resp.addHeader("Access-Control-Expose-Headers", "REDIRECT,CONTEXTPATH");
        resp.addHeader("REDIRECT", "REDIRECT");//����ajax�����ض���
        resp.addHeader("CONTEXTPATH", url);//�ض����ַ
        //resp.sendRedirect(url);


    }

}
