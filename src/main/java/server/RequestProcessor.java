package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import pojo.Request;
import pojo.Response;

/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2020/12/17
 */
public class RequestProcessor extends Thread {

  private Socket socket;

  private Map<String, Context> servletMap;


  public RequestProcessor(Socket socket, Map<String, Context> servletMap) {
    this.socket = socket;
    this.servletMap = servletMap;
  }


  @Override
  public void run() {
    InputStream inputStream = null;
    try {
      inputStream = socket.getInputStream();
      OutputStream outputStream = socket.getOutputStream();

      Request request = new Request(inputStream); ///demo1/demo1

      Response response = new Response(outputStream);

      String url = request.getUrl();
      if(servletMap.containsKey(url)) {
        //请求的是动态资源
        Context context = servletMap.get(url);
        HttpServlet httpServlet = context.getHttpServlet();
        httpServlet.service(request, response);
      } else {
        //静态资源
        String url1 = request.getUrl();
        for(String key : servletMap.keySet()){
          String s = "/" + key.split("/")[1];
          System.out.println(s);
          if(url1.startsWith(s)) {
            String webappPath = servletMap.get(key).getWebappPath();
            String staticPath = webappPath + "/" + request.getUrl().split("/")[request.getUrl().split("/").length-1];
            response.outputHtml(staticPath);
          }
        }
        //404
        response.outputHtml(request.getUrl());
      }

      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    String url = "/demo1/index.html";
    String s = url.split("/")[url.split("/").length - 1];
    System.out.println(s);
  }
}
