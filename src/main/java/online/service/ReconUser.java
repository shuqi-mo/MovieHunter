package online.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// 功能：获取用户点击user时的电影推荐列表

public class ReconUser extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");

            String userid = request.getParameter("id");
            String size = request.getParameter("size");
            String model = request.getParameter("model");

            List<Movie> movies = ReconUserProcess.getRecList(Integer.parseInt(userid), Integer.parseInt(size), model);

            ObjectMapper mapper = new ObjectMapper();
            String jsonMovies = mapper.writeValueAsString(movies);
            response.getWriter().println(jsonMovies);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("");
        }
    }
}
