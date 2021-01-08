package online.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.datamanager.DataManager;
import online.datamanager.Movie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * RecommendationService, provide recommendation service based on different input
 */

public class RecommendationService extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
            IOException {
        try {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");

            String genre = request.getParameter("genre");

            String size = request.getParameter("size");

            String sortby = request.getParameter("sortby");

            List<Movie> movies = DataManager.getInstance().getMoviesByGenre(genre, Integer.parseInt(size),sortby);

            ObjectMapper mapper = new ObjectMapper();
            String jsonMovies = mapper.writeValueAsString(movies);
            response.getWriter().println(jsonMovies);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("");
        }
    }
}
