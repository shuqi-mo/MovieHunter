package online.recprocess;

import online.datamanager.DataManager;
import online.datamanager.Movie;

import java.util.*;

public class SimilarMovieProcess {
    // 获取推荐列表
    public static List<Movie> getRecList(int movieId, int size, String model) {
        Movie movie = DataManager.getInstance().getMovieById(movieId);
        if(null == movie)
            return new ArrayList<>();
        // 单路召回
        List<Movie> candidates = candidateGenerator(movie);
        // 基于embedding的召回
        // List<Movie> candidates = retrievalCandidatesByEmbedding(movie, size);
        List<Movie> rankedList = ranker(movie, candidates, model);
        if(rankedList.size() > size) {
            return rankedList.subList(0, size);
        }
        return rankedList;
    }

    // 单路召回
    public static List<Movie> candidateGenerator(Movie movie) {
        HashMap<Integer, Movie> candidateMap = new HashMap<>();
        for(String genre : movie.getGenres()) {
            List<Movie> oneCandidates = DataManager.getInstance().getMoviesByGenre(genre, 100, "rating");
            for(Movie candidate : oneCandidates) {
                candidateMap.put(candidate.getId(), candidate);
            }
        }
        candidateMap.remove(movie.getId());
        return new ArrayList<>(candidateMap.values());
    }

    public static List<Movie> retrievalCandidatesByEmbedding(Movie movie, int size) {
        if(null == movie || null == movie.getEmb())
            return null;
        List<Movie> allCandidates = DataManager.getInstance().getMovies(10000, "rating");
        HashMap<Movie, Double> movieScoreMap = new HashMap<>();
        for(Movie candidate : allCandidates) {
            double similarity = calculateEmbSimilarScore(movie, candidate);
            movieScoreMap.put(candidate, similarity);
        }
        List<Map.Entry<Movie, Double>> movieScoreList = new ArrayList<>(movieScoreMap.entrySet());
        movieScoreList.sort(Map.Entry.comparingByValue());
        List<Movie> candidates = new ArrayList<>();
        for(Map.Entry<Movie, Double> movieScoreEntry : movieScoreList)
            candidates.add(movieScoreEntry.getKey());
        return candidates.subList(0, Math.min(candidates.size(), size));
    }

    //相似度排序
    public static List<Movie> ranker(Movie movie, List<Movie> candidates, String model) {
        HashMap<Movie, Double> candidateScoreMap = new HashMap<>();
        for(Movie candidate : candidates) {
            double similarity;
            switch (model) {
                case "emb":
                    similarity = calculateEmbSimilarScore(movie, candidate);
                    break;
                default:
                    similarity = calculateSimilarScore(movie, candidate);
            }
            candidateScoreMap.put(candidate, similarity);
        }
        List<Movie> rankedList = new ArrayList<>();
        candidateScoreMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(m -> rankedList.add(m.getKey()));
        return rankedList;
    }

    // 相似度计算
    public static double calculateSimilarScore(Movie movie, Movie candidate) {
        int sameGenreCount = 0;
        for(String genre : movie.getGenres()) {
            if(candidate.getGenres().contains(genre))
                sameGenreCount++;
        }
        double genreSimilarity = (double)sameGenreCount / (movie.getGenres().size() + candidate.getGenres().size()) / 2;
        double ratingScore = candidate.getAverage_rating() / 5;
        double similarityWeight = 0.7;
        double ratingScoreWeight = 0.3;
        return genreSimilarity * similarityWeight + ratingScore * ratingScoreWeight;
    }

    public static double calculateEmbSimilarScore(Movie movie, Movie candidate) {
        if(null == movie || null == candidate)
            return -1;
        return movie.getEmb().calculated_similarity(candidate.getEmb());
    }
}