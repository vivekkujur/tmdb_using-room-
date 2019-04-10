package com.demotmdb.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addMovie(Movie movie);


    @Query("select * from movies")
    public List<Movie> getMovies();

    @Query("DELETE FROM movies")
    public void deleteMovie();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addMovieDetails(MovieDetail movieDetail);

    @Query("select * from moviesdetails where id LIKE :id1")
    public List<MovieDetail> getDetail(String id1);


}
