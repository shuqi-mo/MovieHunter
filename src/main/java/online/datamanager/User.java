package online.datamanager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.*;
import online.model.Embedding;
public class User {
    int id;
    float highest_rating;
    float lowest_rating;
    float average_rating;
    int rating_cnt;

    @JsonSerialize(using = RatingListSerializer.class)
    ArrayList<Rating> ratings;

    //embedding of the movie
    @JsonIgnore
    Embedding emb;

    @JsonIgnore
    Map<String, String> userFeatures;

    //id
    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    //highest_rating
    public void setHighest_rating(float highest_rating) { this.highest_rating = highest_rating; }
    public float getHighest_rating() { return highest_rating; }

    //lowest_rating
    public void setLowest_rating(float lowest_rating) { this.lowest_rating = lowest_rating; }
    public float getLowest_rating() { return lowest_rating; }

    //average_rating
    public float getAverage_rating() { return average_rating; }

    //rating_cnt
    public int getRating_cnt() { return rating_cnt; }

    //ratings
    public void addRatings(Rating rating) {
        ++ rating_cnt;
        if (rating.getRating() > highest_rating) {
            highest_rating = rating.getRating();
        }
        if (rating.getRating() < lowest_rating) {
            lowest_rating = rating.getRating();
        }
        average_rating = (average_rating * (rating_cnt - 1) + rating.getRating()) / rating_cnt;
        this.ratings.add(rating);
    }
    public void setRatings(ArrayList<Rating> ratings) { this.ratings = ratings; }
    public ArrayList<Rating> getRatings() { return ratings; }

    //embedding
    public Embedding getEmb() { return emb; }
    public void setEmb(Embedding emb) { this.emb = emb; }

    //userFeatures
    public Map<String, String> getUserFeatures() { return userFeatures; }
    public void setUserFeatures(Map<String, String> userFeatures) { this.userFeatures = userFeatures; }
}
