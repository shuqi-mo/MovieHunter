package online.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;

// 功能：获取用户点击电影类别标签时为用户推荐的电影列表
public class ReconGenre extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");

            // 电影类别
            String genre = request.getParameter("genre");
            // 返回电影列表的长度
            String size = request.getParameter("size");
            // 排序方法
            String sortby = request.getParameter("sortby");
            List<Movies> movies = DataManager.getInstance().getMoviesByGenre(genre, Integer.parseInt(size), sortby);

            ObjectMapper mapper = new ObjectMapper();
            String jsonMovies = mapper.writeValueAsString(movies);
            response.getWriter().println(jsonMovies);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("");
        }
    }
}
