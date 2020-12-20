package server;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.Stack;
import util.MyClassLoader;
import util.WebClassLoader;

/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2020/12/20
 */
public class Context {

  private String webappName;

  private HttpServlet httpServlet;

  private String webappPath;

  public String getWebappName() {
    return webappName;
  }

  public void setWebappName(String webappName) {
    this.webappName = webappName;
  }

  public String getWebappPath() {
    return webappPath;
  }

  public void setWebappPath(String webappPath) {
    this.webappPath = webappPath;
  }


  public HttpServlet getHttpServlet() {
    return httpServlet;
  }


  public void setHttpServlet(HttpServlet httpServlet) {
    this.httpServlet = httpServlet;
  }


  public static void main(String[] args) throws ClassNotFoundException {
    String classPath = "C:\\Users\\hgvgh\\Desktop\\lagou\\code\\minicat\\target\\classes\\";
    String className = "Demo02Servlet";
    WebClassLoader webClassLoader = new WebClassLoader();
    Class<?> aClass = (Class<?>) webClassLoader.findClass(classPath, className);
  }



}
