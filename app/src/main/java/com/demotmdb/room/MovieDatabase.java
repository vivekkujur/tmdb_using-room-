package com.demotmdb.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {
        Movie.class, MovieDetail.class
}, version = 2)
public abstract class MovieDatabase extends RoomDatabase {
    public  abstract  MyDao myDao();


}
