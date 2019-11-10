package com.github.monkeywie.proxyee.intercept;

import com.github.monkeywie.proxyee.intercept.common.CertDownIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import com.github.monkeywie.proxyee.util.HttpUtil;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpProxyInterceptInitializer {
    String loginSign="169C3A578A6E6CF9F2D13CF91A12752D";
    public void init(HttpProxyInterceptPipeline pipeline) {
        pipeline.addLast(new CertDownIntercept());
        pipeline.addLast(new FullResponseIntercept() {

            @Override
            public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                //在匹配到百度首页时插入js
//                return HttpUtil.checkUrl(pipeline.getHttpRequest(), "^www.baidu.com$")
//                        && HttpUtil.isHtml(httpRequest, httpResponse);
                return true;
            }

            @Override
            public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                //打印原始响应信息
                System.out.println(">>>>>>>>>>>>>>>RESPONSE>>>>>>>>>>>>");
                System.out.println(httpRequest.method()+" http://"+httpRequest.headers().get("host")+httpRequest.uri());
//                System.out.println(httpResponse.toString());
                String type=httpResponse.headers().get("Content-Type");
                if(type!=null&& (type.toLowerCase().contains("text") ||type.toLowerCase().contains("json")))
                {
                    System.out.println(httpResponse.content().toString(Charset.defaultCharset()));
                }

                if(httpRequest.uri().contains("datau/datau/xrPneumaV.du")){
                    Pattern p= Pattern.compile("(?<=loginSign=).{32}(?=&)");
                    Matcher m= p.matcher(httpRequest.uri());
                    if(m.find()){
                        loginSign=m.group(0);
                    }
                }else if(httpRequest.uri().contains("datau/jsp/spgs/flow/flow_index.html")){
                    InputStream is=getClass().getClassLoader().getResourceAsStream("liuliangguan.js");
                    BufferedInputStream bis=new BufferedInputStream(is);
                    byte[]b= new byte[0];
                    try {
                        b = new byte[bis.available()];
                        bis.read(b);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String s=new String(b);
                    s="var loginSign=\""+loginSign+"\";\n"+s;
                    s="<script>\n"+s+"</script>";

                    httpResponse.content().writeBytes(s.getBytes());
                }

                //修改响应头和响应体
//                httpResponse.headers().set("handel", "edit head");
                    /*int index = ByteUtil.findText(httpResponse.content(), "<head>");
                    ByteUtil.insertText(httpResponse.content(), index, "<script>alert(1)</script>");*/
//                httpResponse.content().writeBytes("<script>alert('hello proxyee')</script>".getBytes());

            }
        });
    }

}
