package online.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.datamanager.DataManager;
import online.datamanager.Movie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 功能：获取电影信息

public class MovieService extends HttpServlet {
    // 向客户端发送数据找HttpServletResponse,从客户端取数据找HttpServletRequest
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            //设置HTP协议中content-type响应头字段的值
            response.setContentType("application/json");
            // 设置状态码
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            // 第一个参数表示允许跨域（IP和端口号），*表示全部服务都接受跨域
            response.setHeader("Access-Control-Allow-Origin", "*");

            //获取电影id和DataManager中的电影对象
            String movieid = request.getParameter("id");
            Movie movie = DataManager.getInstance().getMovieById(Integer.parseInt(movieid));

            //把电影对象转换成json格式并返回
            if (null != movie) {
                // Jackson objectmapper可以把JSON映射为Java对象，或将Java对象映射到JSON
                ObjectMapper mapper = new ObjectMapper();
                String jsonMovie = mapper.writeValueAsString(movie);
                // 打印输出
                response.getWriter().println(jsonMovie);
            }
            else {
                response.getWriter().println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("");
        }
    }
}
