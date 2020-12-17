package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
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

  private Map<String, Object> servletMap = new HashMap<String, Object>(); //存放url与servlet的对应关系

  private static ThreadPoolExecutor threadPoolExecutor;
  static {
    /**
     * 初始化线程池
     */
      int corePoolSize = 10;
      int maximumPoolSize = 50;
      long keepAliveTime = 100L;
      TimeUnit unit = TimeUnit.SECONDS;
      BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue(50);
      ThreadFactory threadFactory = Executors.defaultThreadFactory();
      RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
      threadPoolExecutor = new ThreadPoolExecutor(
          corePoolSize,
          maximumPoolSize,
          keepAliveTime,
          unit,
          workQueue,
          threadFactory,
          handler
      );
  }



  public void start() throws IOException, InterruptedException {
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


    //minicat2.0版本，封装request和response对象，完成输出请求路径对应的html页面静态页
    /*while (true) {
      Socket accept = serverSocket.accept();
      InputStream inputStream = accept.getInputStream();
      OutputStream outputStream = accept.getOutputStream();

      Request request = new Request(inputStream);

      Response response = new Response(outputStream);

      response.outputHtml(request.getUrl());

      accept.close();
    }*/

    /**
     * minicat3.0版本，加入Servlet处理动态请求
     *
     */

    //加载配置的Servlet
    try {
      loadServlet();
    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    /*while (true) {
      Socket accept = serverSocket.accept();

      InputStream inputStream = accept.getInputStream();
      OutputStream outputStream = accept.getOutputStream();

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

      accept.close();
    }
*/

    /**
     * minicat4.0版本-使用多线程改造项目
     */
    /*while (true) {
      Socket accept = serverSocket.accept();
      RequestProcessor requestProcessor = new RequestProcessor(accept, servletMap);
      requestProcessor.start();
    }*/

    /**
     * minicat5.0版本-使用线程池改造项目
     */
    while (true) {
      Socket accept = serverSocket.accept();
      RequestProcessor requestProcessor = new RequestProcessor(accept, servletMap);
      threadPoolExecutor.execute(requestProcessor);
    }

  }



  /**
   * 加载配置的Servlet
   */
  private void loadServlet()
      throws DocumentException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    SAXReader saxReader = new SAXReader();
    InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
    Document document = saxReader.read(resourceAsStream);
    Element rootElement = document.getRootElement();
    List<Element> servletElements = rootElement.selectNodes("//servlet");
    for (Element servletElement : servletElements) {
      Element serlvetNameElement = (Element) servletElement.selectSingleNode("//servlet-name");
      String sertvletName = serlvetNameElement.getStringValue();

      Element serlvetClassElement = (Element) servletElement.selectSingleNode("//servlet-class");
      String serlvetClass = serlvetClassElement.getStringValue();

      //查询出当前servletname对应的url-pattern

      Element mappingElement = (Element) rootElement
          .selectSingleNode("/web-app/servlet-mapping[servlet-name='" + sertvletName + "']");
      Element urlPatternElement = (Element) mappingElement.selectSingleNode("//url-pattern");
      String urlPattern = urlPatternElement.getStringValue();

      servletMap.put(urlPattern, (HttpServlet) Class.forName(serlvetClass).newInstance());
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.start();
  }

}
