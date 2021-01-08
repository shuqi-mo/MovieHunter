package online.service;

import online.datamanager.Movie;
import online.recprocess.SimilarMovieProcess;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// 功能：相似电影推荐列表

public class SimilarMovieService extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");

            // 电影id
            String movieid = request.getParameter("movieId");
            // 相似电影数量
            String size = request.getParameter("size");
            // 计算相似度方法
            String model = request.getParameter("model");
            // 获取相似电影
            List<Movie> similarmovies = SimilarMovieProcess.getRecList(Integer.parseInt(movieid), Integer.parseInt(size), model);

            // 把相似电影列表转换成json格式并返回
            ObjectMapper mapper = new ObjectMapper();
            String jsonMovies = mapper.writeValueAsString(similarmovies);
            response.getWriter().println(jsonMovies);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("");
        }
    }
}
