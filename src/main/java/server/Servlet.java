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
public interface Servlet {
  void init(Request request, Response response);

  void service(Request request, Response response) throws IOException;

  void destroy(Request request, Response response);
}
