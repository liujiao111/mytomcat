package server;

import java.io.IOException;
import pojo.Request;
import pojo.Response;

/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2020/12/17
 */
public abstract class HttpServlet implements Servlet{

  public abstract void doGet(Request request, Response response)
      throws IOException, InterruptedException;

  public abstract void doPost(Request request, Response response) throws IOException;

  public void service(Request request, Response response) throws IOException, InterruptedException {
    if("GET".equals(request.getMethod())){
      doGet(request, response);
    } else {
      doPost(request, response);
    }
  }
}
