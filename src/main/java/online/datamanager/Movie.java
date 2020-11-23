package online.datamanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import online.model.Embedding;
import java.util.*;
/*
电影成员依次为
电影编号（主码）
电影名
导演
编剧
电影类型
主演
制片地区
语言
上映时间
片长
评分次数
均分
 */
public class Movie{
    //电影编号（主码）
    int id;
    String title;
    String director;
    String screenwriter;
    ArrayList<String> genres;
    ArrayList<String> Starring;
    String Production_Country;
    String language;
    String release_time;
    int length;
    int rating_num;
    double average_rating;
    //
    String imb_id;
    String tmb_id;

    //
    @JsonIgnore
    Embedding emb;

    //用户-电影-用户评分
    @JsonIgnore
    ArrayList<Rating> ratings;

    @JsonIgnore
    Map<String, String> movieFeatures;

    //评分最高的十部电影
    static final int TOP_RATING_SIZE = 10;

    @JsonSerialize(using = RatingListSerializer.class)
    List<Rating> topRatings;
    //
    public Movie(){
        this.rating_num = 0;
        this.average_rating = 0;
        ratings = new ArrayList<>();
        genres = new ArrayList<>();
        topRatings = new LinkedList<>();
        emb = null;
        movieFeatures = null;
    }
    //Id
    public void setId (int id) { this.id = id; }
    public int getId() { return id; }

    //Title
    public void setTitle (String title) { this.title = title; }
    public String getTitle () { return title; }

    //Director
    public void setDirector(String director) { this.director = director; }
    public String getDirector() { return director; }

    //Screenwriter
    public void setScreenwriter(String screenwriter) { this.screenwriter = screenwriter; }
    public String getScreenwriter() { return screenwriter; }

    //Genre
    public void addGenre (String temp) { this.genres.add(temp); }
    public void setGenres (ArrayList<String> genres) { this.genres = genres; }
    public ArrayList<String> getGenres() { return genres; }

    //Starring
    public void setStarring(ArrayList<String> starring) { Starring = starring; }
    public ArrayList<String> getStarring() { return Starring; }

    //Production_Country
    public void setProduction_Country(String production_Country) { Production_Country = production_Country; }
    public String getProduction_Country() { return Production_Country; }

    //Language
    public void setLanguage(String language) { this.language = language; }
    public String getLanguage() { return language; }

    //Release_time
    public void setRelease_time(String release_time) { this.release_time = release_time; }
    public String getRelease_time () { return release_time; }

    //length
    public void setLength(int length) { this.length = length; }
    public int getLength() { return length; }

    //rating_num
    public int getRating_num() { return rating_num; }

    //average_rating
    public double getAverage_rating() { return average_rating; }

    //Imb_id
    public void setImb_id(String imb_id) { this.imb_id = imb_id; }
    public String getImb_id() { return imb_id; }

    //Tmb_id
    public void setTmb_id(String tmb_id) { this.tmb_id = tmb_id; }
    public String getTmb_id() { return tmb_id; }

    //emb
    public void setEmb(Embedding emb) { this.emb = emb; }
    public Embedding getEmb() { return emb; }

    //ratings
    public ArrayList<Rating> getRatings() { return ratings; }
    public void addRatings (Rating rating) {
        ++ this.rating_num;
        this.ratings.add(rating);
        this.average_rating = (this.average_rating * (this.rating_num - 1) + rating.getRating()) / this.rating_num;
    }

    //movieFeatures
    public void setMovieFeatures(Map<String, String> movieFeatures) { this.movieFeatures = movieFeatures; }
    public Map<String, String> getMovieFeatures() { return movieFeatures; }
}

