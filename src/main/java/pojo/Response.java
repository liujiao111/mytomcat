package pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import util.HttpProtocolUtil;
import util.StaticResourceUtil;

/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2020/12/17
 */
public class Response {

  private OutputStream outputStream;

  public Response(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  /**
   * 向页面输出指定路径下的HTML页面
   * @param url
   */
  public void outputHtml(String path) throws IOException {
    //获取静态资源文件的绝对路径
    String absoluteResourcePath = StaticResourceUtil.getAbsolutePath(path);

    File file =new File(absoluteResourcePath);

    //如果资源存在，并且请求的是文件，
    if(file.exists() && file.isFile()) {
      //读取静态资源内容，并输出
      StaticResourceUtil.outputStaticResource(new FileInputStream(file), outputStream);
    } else {
      //如果资源不存在或者读取的资源是文件夹，则输出404
      String httpHeader404 = HttpProtocolUtil.getHttpHeader404();
      output(httpHeader404);
    }

  }

  /**
   * 向页面输出内容
   * @param httpHeader404
   */
  private void output(String text) throws IOException {
    outputStream.write(text.getBytes());
  }
}
