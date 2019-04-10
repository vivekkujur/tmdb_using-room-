package com.demotmdb.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.demotmdb.room.Movie;
import com.demotmdb.ui.MainActivity;
import com.demotmdb.ui.MovieActivity;
import com.demotmdb.R;
import com.demotmdb.Setting.Config;

import java.util.List;

public class MovieRecyclerAdapter  extends RecyclerView.Adapter<MovieRecyclerAdapter.ViewHolder> {

    MainActivity mainActivity;
    final String[] Selected = new String[]{"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    List<Movie> movies =MainActivity.movieDatabase.myDao().getMovies();

    public MovieRecyclerAdapter(MainActivity mainActivity  ) {
        this.mainActivity= mainActivity;
    }

    public MovieRecyclerAdapter() {

    }
    @NonNull
    @Override
    public MovieRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MovieRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_list, parent, false));
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MovieRecyclerAdapter.ViewHolder viewHolder, final int i) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_image_black_24dp);

            Glide.with(mainActivity).setDefaultRequestOptions(requestOptions).load(Config.ImageBaseUrl+movies.get(i).getPoster_path())
                    .into(viewHolder.poster);

            viewHolder.language.setText(movies.get(i).getOriginal_language());
            viewHolder.desc.setText(movies.get(i).getOverview());
            viewHolder.title.setText(movies.get(i).getTitle());
            viewHolder.rating.setText(movies.get(i).getVote_average());

            try{
                String datestr = movies.get(i).getRelease_date();

                String[] arrOfStr = datestr.split("-");
                if(Integer.parseInt(arrOfStr[2]) > 10 && Integer.parseInt(arrOfStr[2])<14)
                    arrOfStr[2]   = arrOfStr[2] + "th ";
                else{
                    if(arrOfStr[2].endsWith("1")) arrOfStr[2] = arrOfStr[2] + "st";
                    else if(arrOfStr[2].endsWith("2")) arrOfStr[2] = arrOfStr[2] + "nd";
                    else if(arrOfStr[2].endsWith("3")) arrOfStr[2] = arrOfStr[2] + "rd";
                    else arrOfStr[2] = arrOfStr[2] + "th";
                }

                viewHolder.date.setText(arrOfStr[2]+" "+Selected[Integer.parseInt(arrOfStr[1])-1]+arrOfStr[0]);

            }catch(Exception e){
                e.printStackTrace();
            }

        viewHolder.mainlv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, MovieActivity.class);
                intent.putExtra("id",movies.get(i).getId());

                mainActivity.startActivity(intent);
            }
        });
    }
    public void movieAdd(){
        movies=MainActivity.movieDatabase.myDao().getMovies();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
            return movies.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, date, rating , language;
         ImageView poster;
         ConstraintLayout mainlv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title= itemView.findViewById(R.id.titleMovie);
            desc= itemView.findViewById(R.id.descMovie);
            date= itemView.findViewById(R.id.date);
            rating = itemView.findViewById(R.id.rating);
            language= itemView.findViewById(R.id.language);
            poster= itemView.findViewById(R.id.imageMovie);
            mainlv= itemView.findViewById(R.id.mainlv);
        }
    }


}
