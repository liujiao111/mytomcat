package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author hgvgh
 * @version 1.0
 * @description 静态资源处理工具类
 * @date 2020/12/17
 */
public class StaticResourceUtil {


  /**
   *
   * @param path 获取静态资源的绝对路径
   * @return
   */
  public static String getAbsolutePath(String path) {
    String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
    return absolutePath.replaceAll("\\\\", "/") + path;
  }

  /**
   * 向页面输出静态资源
   * @param inputStream
   * @param outputStream
   */
  public static void outputStaticResource(InputStream inputStream,
      OutputStream outputStream) throws IOException {
    int count = 0;
    while (count == 0) {
      count = inputStream.available();
    }

    int resourceSize = count;

    //输出http请求头，再输出具体内容
    outputStream.write(HttpProtocolUtil.get200HttpHeader(resourceSize).getBytes());

    //读取内容
    long written = 0; //已经读取的内容长度
    int byteSize = 1024; //计划每次缓冲的长度
    byte[] bytes = new byte[byteSize];

    while (written < resourceSize) {
      if(written + byteSize > resourceSize) {
        byteSize = (int) (resourceSize - written);
        bytes = new byte[byteSize];
      }
      inputStream.read(bytes);
      outputStream.write(bytes);
      outputStream.flush();
      written += byteSize;
    }

  }
}
