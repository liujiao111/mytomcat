package util;

import java.io.OutputStream;

/**
 * @author hgvgh
 * @version 1.0
 * @description Http请求封装工具类
 * @date 2020/12/17
 */
public class HttpProtocolUtil {

  private OutputStream outputStream;

  /**
   * 返回200
   * @param DataLength
   * @return
   */
  public static String get200HttpHeader(int DataLength) {

    return "HTTP/1.1 200 ok \n" +
        "Content-Type: text/html;charset=utf-8\n"
        + "Content-Length: " + DataLength + "\n" +"\r\n";

  }

  /**
   * 返回404
   * @return
   */
  public static String getHttpHeader404() {
    String str404 = "<h1>404 not found</h1>";
    return "HTTP/1.1 404 NOT Found \n" +
        "Content-Type: text/html \n" +
        "Content-Length: " + str404.getBytes().length + " \n" +
        "\r\n" + str404;
  }

}
