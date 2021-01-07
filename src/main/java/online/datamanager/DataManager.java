package online.datamanager;

import online.util.Utility;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;

import java.io.File;
import java.util.*;

public class DataManager {
    private static volatile DataManager instance;
    HashMap<Integer, Movie> movieMap;
    HashMap<Integer, User> userMap;
    HashMap<String, List<Movie>> genreReverseIndexMap;

    private DataManager() {
        this.movieMap = new HashMap<>();
        this.userMap = new HashMap<>();
        this.genreReverseIndexMap = new HashMap<>();
        instance = this;
    }

    public static DataManager getInstance() {
        if (null == instance) {
            synchronized (DataManager.class) {
                if (null == instance) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public void loadData(String movieDataPath, String linkDataPath, String ratingDataPath, String movieEmbPath) throws Exception {
        loadMovieData(movieDataPath);
        loadLinkData(linkDataPath);
        loadRatingData(ratingDataPath);
        loadMovieEmb(movieEmbPath);
    }

    // 加载电影数据
    private void loadMovieData(String movieDataPath) throws Exception {
        System.out.println("Loading movie data from " + movieDataPath + "...");
        boolean skipFirstLine = true;
        try (Scanner scanner = new Scanner(new File(movieDataPath))) {
            while (scanner.hasNextLine()) {
                String movieRawData = scanner.nextLine();
                if (skipFirstLine) {
                    skipFirstLine = false;
                    continue;
                }
                String[] movieData = movieRawData.split(",");
                if (movieData.length == 3) {
                    Movie movie = new Movie();
                    movie.setId(Integer.parseInt(movieData[0]));
                    int releaseYear = parseReleaseYear(movieData[1].trim());
                    if (releaseYear == -1)
                        movie.setTitle(movieData[1].trim());
                    else {
                        movie.setRelease_time(releaseYear);
                        movie.setTitle(movieData[1].trim().substring(0, movieData[1].trim().length() - 6).trim());
                    }
                    String genres = movieData[2];
                    if (!genres.trim().isEmpty()) {
                        String[] genreArray = genres.split("\\|");
                        for (String genre : genreArray) {
                            movie.addGenre(genre);
                            addMovie2GenreIndex(genre, movie);
                        }
                    }
                    this.movieMap.put(movie.getId(), movie);
                }
            }
        }
        System.out.println("Loading movie data complete. " + this.movieMap.size() + " movies in total...");
    }

    // 加载电影embedding
    private void loadMovieEmb(String movieEmbPath) throws Exception {
        System.out.println("Loading movie embedding from " + movieEmbPath + "...");
        int validEmbCount = 0;
        try (Scanner scanner = new Scanner(new File(movieEmbPath))) {
            while (scanner.hasNextLine()) {
                String movieRawEmbData = scanner.nextLine();
                String[] movieEmbData = movieRawEmbData.split(":");
                if (movieEmbData.length == 2) {
                    Movie m = getMovieById(Integer.parseInt(movieEmbData[0]));
                    if (null == m) {
                        continue;
                    }
                    m.setEmb(Utility.parseEmbStr(movieEmbData[1]));
                    validEmbCount++;
                }
            }
        }
        System.out.println("Loading movie embedding complete. " + validEmbCount + " movie embeddings in total.");
    }

    private int parseReleaseYear(String rawTitle) {
        if(null == rawTitle || rawTitle.trim().length() < 6) {
            return -1;
        }
        else {
            String yearString = rawTitle.trim().substring(rawTitle.length()-5, rawTitle.length()-1);
            try {
                return Integer.parseInt(yearString);
            } catch(NumberFormatException exception) {
                return -1;
            }
        }
    }

    private void loadLinkData(String linkDataPath) throws Exception {
        System.out.println("Loading link data from " + linkDataPath + " ...");
        int count = 0;
        boolean skipFirstLine = true;
        try(Scanner scanner = new Scanner(new File(linkDataPath))) {
            while(scanner.hasNextLine()) {
                String linkRawData = scanner.nextLine();
                if (skipFirstLine) {
                    skipFirstLine = false;
                    continue;
                }
                String[] linkData = linkRawData.split(",");
                if(linkData.length == 3) {
                    int movieId = Integer.parseInt(linkData[0]);
                    Movie movie = this.movieMap.get(movieId);
                    if(null != movie) {
                        count++;
                        movie.setImb_id(linkData[1].trim());
                        movie.setTmb_id(linkData[2].trim());
                    }
                }
            }
        }
        System.out.println("Loading link data completed. " + count + " links in total.");
    }

    private void loadRatingData(String ratingDataPath) throws Exception{
        System.out.println("Loading rating data from " + ratingDataPath + " ...");
        boolean skipFirstLine = true;
        int count = 0;
        try(Scanner scanner = new Scanner(new File(ratingDataPath))) {
            while(scanner.hasNextLine()) {
                String ratingRawData = scanner.nextLine();
                if(skipFirstLine) {
                    skipFirstLine = false;
                    continue;
                }
                String[] linkData = ratingRawData.split(",");
                if(linkData.length == 4) {
                    count++;
                    Rating rating = new Rating();
                    rating.setUser_id(Integer.parseInt(linkData[0]));
                    rating.setMovie_id(Integer.parseInt(linkData[1]));
                    rating.setRating(Float.parseFloat(linkData[2]));
                    rating.setTimestamp(Long.parseLong(linkData[3]));
                    Movie movie = this.movieMap.get(rating.getMovie_id());
                    if(null != movie) {
                        movie.addRatings(rating);
                    }
                    if(!this.userMap.containsKey(rating.getUser_id())) {
                        User user = new User();
                        user.setId(rating.getUser_id());
                        this.userMap.put(user.getId(), user);
                    }
                    this.userMap.get(rating.getUser_id()).addRatings(rating);
                }
            }
        }
        System.out.println("Loading rating data completed. " + count + " ratings in total.");
    }

    private void addMovie2GenreIndex(String genre, Movie movie) {
        if(!this.genreReverseIndexMap.containsKey(genre)) {
            this.genreReverseIndexMap.put(genre, new ArrayList<>());
        }
        this.genreReverseIndexMap.get(genre).add(movie);
    }

    public List<Movie> getMoviesByGenre(String genre, int size, String sortBy) {
        if(null != genre) {
            List<Movie> movies = new ArrayList<>(this.genreReverseIndexMap.get(genre));
            switch (sortBy) {
                case "rating": movies.sort((m1, m2) -> Double.compare(m2.getAverage_rating(), m1.getAverage_rating()));break;
                case "releaseYear": movies.sort((m1, m2) -> Integer.compare(m2.getRelease_time(), m1.getRelease_time()));break;
                default:
            }
            if(movies.size() > size) {
                return movies.subList(0, size);
            }
            return movies;
        }
        return null;
    }

    public List<Movie> getMovies(int size, String sortBy) {
        List<Movie> movies = new ArrayList<>(movieMap.values());
        switch(sortBy) {
            case "rating": movies.sort((m1, m2) -> Double.compare(m2.getAverage_rating(), m1.getAverage_rating())); break;
            case "releaseYear": movies.sort((m1, m2) -> Integer.compare(m2.getRelease_time(), m1.getRelease_time())); break;
            default:
        }
        if(movies.size() > size)
            return movies.subList(0, size);
        return movies;
    }

    public Movie getMovieById(int movieId) {
        return this.movieMap.get(movieId);
    }

    public User getUserById(int userId) {
        return this.userMap.get(userId);
    }
}