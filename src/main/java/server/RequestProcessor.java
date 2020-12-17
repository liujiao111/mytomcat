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


  private Map<String, Object> servletMap;


  public RequestProcessor(Socket socket, Map<String, Object> servletMap) {
    this.socket = socket;
    this.servletMap = servletMap;
  }

  @Override
  public void run() {
    InputStream inputStream = null;
    try {
      inputStream = socket.getInputStream();
      OutputStream outputStream = socket.getOutputStream();

      Request request = new Request(inputStream);

      Response response = new Response(outputStream);

      String url = request.getUrl();
      if(servletMap.containsKey(url)) {
        //请求的是动态资源
        HttpServlet httpServlet = (HttpServlet) servletMap.get(url);
        httpServlet.service(request, response);
      } else {
        //静态资源
        response.outputHtml(request.getUrl());
      }

      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }
}
