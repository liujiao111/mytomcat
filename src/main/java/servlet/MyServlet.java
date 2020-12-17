package servlet;

import java.io.IOException;
import pojo.Request;
import pojo.Response;
import server.HttpServlet;
import util.HttpProtocolUtil;

/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2020/12/17
 */
public class MyServlet extends HttpServlet {

  public void doGet(Request request, Response response) throws IOException, InterruptedException {
    System.out.println("MiniCat GET请求");
    Thread.sleep(10343434);
    String text = "<h1>MiniCat GET请求</h1>";
    String responseText = HttpProtocolUtil.get200HttpHeader(text.getBytes().length) + text;
    response.output(responseText);
  }

  public void doPost(Request request, Response response) throws IOException {
    System.out.println("MiniCat POST请求");
    String text = "<h1>MiniCat POST请求</h1>";
    String responseText = HttpProtocolUtil.get200HttpHeader(text.getBytes().length) + text;
    response.output(responseText);
  }

  public void init(Request request, Response response) {

  }

  public void destroy(Request request, Response response) {

  }
}
