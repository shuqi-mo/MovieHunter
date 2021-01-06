package online.datamanager;
/*
Rating是一个联系集，用户-电影-用户评分
 */
public class Rating {
    int movie_id;
    int user_id;
    float rating;
    long timestamp;

    //movie_id
    public void setMovie_id(int movie_id) { this.movie_id = movie_id; }
    public int getMovie_id() { return movie_id; }

    //user_id
    public void setUser_id(int user_id) { this.user_id = user_id; }
    public int getUser_id() { return user_id; }

    //rating
    public void setRating(float rating) { this.rating = rating; }
    public float getRating() { return rating; }

    //timestamp
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public long getTimestamp() { return timestamp; }
}
