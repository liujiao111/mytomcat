package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import util.StaticResourceUtil;
import util.WebClassLoader;

/**
 * @author hgvgh
 * @version 1.0
 * @description 服务器启动类
 * @date 2020/12/17
 */
public class Bootstrap {

  private static int port = 8080; //启动端口号

  private static Map<String, Context> servletMap = new HashMap<String, Context>(); //存放url与servlet的对应关系


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

    try {
      //初始化url与urlpattern对应关系
      loadServlet();
    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
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
    /*try {
      loadServlet();
    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }*/

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
  private static void loadServlet()
      throws DocumentException, ClassNotFoundException, IllegalAccessException, InstantiationException, FileNotFoundException {

    //加载配置文件，解析监听端口，以及部署的app目录
    String absoluteResourcePath = StaticResourceUtil.getAbsolutePath("server.xml");
    InputStream resourceAsStream1 = new FileInputStream(absoluteResourcePath);
    SAXReader saxReader1 = new SAXReader();
    Document document1 = saxReader1.read(resourceAsStream1);
    Element rootElement1 = document1.getRootElement();
    Element serviceElement = (Element) rootElement1.selectSingleNode("//Service");
    Element connElement = (Element) serviceElement.selectSingleNode("//Connector");

    port = Integer.parseInt(connElement.attributeValue("port"));

    Element engineElement = (Element) connElement.selectSingleNode("//Engine");
    Element hostElement = (Element) engineElement.selectSingleNode("//Host");

    String appBase = hostElement.attributeValue("appBase");
    File file = new File(appBase);
    File[] files = file.listFiles();
    if (files.length < 1) {
      return;
    }

    //遍历部署项目的目录，解析每个项目的web.xml将配置映射信息存储到servletMap中
    for (File file1 : files) {
      if (file1.isDirectory()) {
        String appName = file1.getName();
        String webxmlPath = appBase + File.separator + appName + File.separator + "web.xml";

        SAXReader saxReader = new SAXReader();
        InputStream resourceAsStream = new FileInputStream(webxmlPath);
        Document document = saxReader.read(resourceAsStream);
        Element rootElement = document.getRootElement();
        List<Element> servletElements = rootElement.selectNodes("//servlet");

        for (Element servletElement : servletElements) {
          Element serlvetNameElement = (Element) servletElement.selectSingleNode("//servlet-name");
          String sertvletName = serlvetNameElement.getStringValue();

          Element serlvetClassElement = (Element) servletElement
              .selectSingleNode("//servlet-class");
          String serlvetClass = serlvetClassElement.getStringValue(); //servlet.Demo1Servlet

          //查询出当前servletname对应的url-pattern
          Element mappingElement = (Element) rootElement
              .selectSingleNode("/web-app/servlet-mapping[servlet-name='" + sertvletName + "']");
          Element urlPatternElement = (Element) mappingElement.selectSingleNode("//url-pattern");
          String urlPattern = urlPatternElement.getStringValue();

          Context context = new Context();
          String classPath =
              appBase + File.separator + appName + File.separator + "server" + serlvetClass
                  .replaceAll(".", "\\\\");

          //通过自定义类加载器，加载指定目录下的class文件，并创建实例对象，存储到servletMap中
          WebClassLoader webClassLoader = new WebClassLoader();
          Class<?> aClass = webClassLoader.findClass(classPath, serlvetClass);
          context.setHttpServlet((HttpServlet) aClass.newInstance());
          context.setWebappPath(appBase + File.separator + appName);
          context.setWebappName(appName);
          servletMap.put("/" + appName + urlPattern, context);
        }
      }
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.start();
  }

}
