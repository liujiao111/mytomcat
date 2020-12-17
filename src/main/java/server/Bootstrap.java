package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import pojo.Request;
import pojo.Response;
import util.HttpProtocolUtil;

/**
 * @author hgvgh
 * @version 1.0
 * @description 服务器启动类
 * @date 2020/12/17
 */
public class Bootstrap {

  private static int port = 8080; //启动端口号



  public void start() throws IOException {
    //省略读取server.xml中的配置信息
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("minicat start on port:" + port);
    //minicat1.0版本，完成输出字符串到浏览器页面的功能
    /*while (true) {
      Socket accept = serverSocket.accept();

      //有了Socket，接收到请求，获取输出流
      OutputStream outputStream = accept.getOutputStream();
      String data = "Hello MiniCat!";
      String responseText = HttpProtocolUtil.get200HttpHeader(data.getBytes().length) + data;
      System.out.println(responseText);
      outputStream.write(responseText.getBytes()); //无法直接输出字符，因为没有按照浏览器需要的数据格式进行传输
      accept.close();
    }*/


    //minicat2.0版本，封装request和response对象，完成输出html页面静态页，
    while (true) {
      Socket accept = serverSocket.accept();
      InputStream inputStream = accept.getInputStream();
      OutputStream outputStream = accept.getOutputStream();

      Request request = new Request(inputStream);

      Response response = new Response(outputStream);

      response.outputHtml(request.getUrl());

      accept.close();
    }

  }

  public static void main(String[] args) throws IOException {
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.start();
  }

}
