package pojo;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2020/12/17
 */
public class Request {

  private String method; //请求方式：GET/POST等

  private String url; //客户端请求url

  private InputStream inputStream;

  public Request(InputStream inputStream) throws IOException {
    this.inputStream = inputStream;

    //解析输入的流
    //从输入流中获取请求的信息
    int count = 0;
    while (count  == 0) {
      count = inputStream.available();
    }

    byte[] bytes = new byte[count];
    inputStream.read(bytes);

    String inputStr = new String(bytes);

    //获取第一行请求头信息
    String firstLineStr = inputStr.split("\n")[0];
    this.method = firstLineStr.split(" ")[0];
    this.url = firstLineStr.split(" ")[1];

    System.out.println("request method:" + method + ", url : " + url);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}
