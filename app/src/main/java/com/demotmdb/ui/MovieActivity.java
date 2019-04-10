package com.demotmdb.ui;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.demotmdb.R;
import com.demotmdb.Setting.Config;
import com.demotmdb.BaseActivity;
import com.demotmdb.room.MovieDetail;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieActivity extends BaseActivity {


    TextView desc, date, rating, budget, revenue, language, runningtime,genresList;
    ImageView poster;
    int id;
    final String[] Selected = new String[]{"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
     Toolbar toolbar;
    View loading;
    List<MovieDetail> movieDetail;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        //toolbar setup
         toolbar= findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));


        init();

        //progress bar
        loading.setVisibility(View.VISIBLE);

        Bundle bundle= getIntent().getExtras();
        if (bundle != null) {
             id= bundle.getInt("id");
        }
        try{
            movieDetail = MainActivity.movieDatabase.myDao().getDetail(String.valueOf(id));
            SetViewDetails();

        }catch(Exception e){
            e.printStackTrace();
        }


        //Retrofit api call for movies details
        call= api.GetMovieDetails(String.valueOf(id),Config.api_key);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    Log.e("onResponse: ",response.body()+"" );
                    if(response.code()==200){

                        if (response.body() != null) {
                            loading.setVisibility(View.VISIBLE);

                            JSONObject jsonObject = new JSONObject(response.body().string());

                            MovieDetail movieDetail1 = new MovieDetail();
                            movieDetail1.setId(id);
                            movieDetail1.setGenres(formatGenre(jsonObject));
                            movieDetail1.setBackdrop_path(jsonObject.getString("backdrop_path"));
                            movieDetail1.setBudget(jsonObject.getString("budget"));
                            movieDetail1.setOriginal_language(jsonObject.getString("original_language"));
                            movieDetail1.setOriginal_title(jsonObject.getString("original_title"));
                            movieDetail1.setOverview(jsonObject.getString("overview"));
                            movieDetail1.setRelease_date(jsonObject.getString("release_date"));
                            movieDetail1.setRevenue(jsonObject.getString("revenue"));
                            movieDetail1.setRuntime(jsonObject.getString("runtime"));
                            movieDetail1.setVote_average(jsonObject.getString("vote_average"));

                            MainActivity.movieDatabase.myDao().addMovieDetails(movieDetail1);
                            SetViewDetails();
                        }

                    }
                }catch(Exception e){
                    e.printStackTrace();
                    loading.setVisibility(View.GONE);
                    Toast.makeText(mContext, R.string.ERROR_MSG, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, R.string.ERROR_MSG, Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);

            }
        });

    }

    private void init() {

        poster = findViewById(R.id.poster);
        desc= findViewById(R.id.movieoverview);
        date= findViewById(R.id.daterelease);
        rating= findViewById(R.id.ratingmovie);
        budget= findViewById(R.id.budget);
        revenue= findViewById(R.id.revenue);
        language= findViewById(R.id.languagemovie);
        runningtime= findViewById(R.id.runninnTzime);
        genresList= findViewById(R.id.genre);
        loading= findViewById(R.id.loading);

    }

    private  void SetViewDetails(){
        loading.setVisibility(View.VISIBLE);

        try {

            movieDetail= MainActivity.movieDatabase.myDao().getDetail(String.valueOf(id));

            //placeholder image
            final RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_image_black_24dp);


            toolbar.setTitle(movieDetail.get(0).getOriginal_title());
            runningtime.setText(movieDetail.get(0).getRuntime() + " minutes");

            try {
                String datestr = movieDetail.get(0).getRelease_date();

                String[] arrOfStr = datestr.split("-");
                if (Integer.parseInt(arrOfStr[2]) > 10 && Integer.parseInt(arrOfStr[2]) < 14)
                    arrOfStr[2] = arrOfStr[2] + "th ";
                else {
                    if (arrOfStr[2].endsWith("1")) arrOfStr[2] = arrOfStr[2] + "st";
                    else if (arrOfStr[2].endsWith("2")) arrOfStr[2] = arrOfStr[2] + "nd";
                    else if (arrOfStr[2].endsWith("3")) arrOfStr[2] = arrOfStr[2] + "rd";
                    else arrOfStr[2] = arrOfStr[2] + "th";
                }
                date.setText(arrOfStr[2] + " " + Selected[Integer.parseInt(arrOfStr[1]) - 1] + arrOfStr[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            language.setText(movieDetail.get(0).getOriginal_language());
            rating.setText(movieDetail.get(0).getVote_average());
            desc.setText(movieDetail.get(0).getOverview());

            Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions)
                    .load(Config.ImageBaseUrl + movieDetail.get(0).getBackdrop_path())
                    .into(poster);


            genresList.setText(movieDetail.get(0).getGenres());

            int budgeta = Integer.parseInt(movieDetail.get(0).getBudget());
            int budgetd = budgeta / 1000000;
            budget.setText("$" + budgetd + "Million");

            int revenueA = Integer.parseInt(movieDetail.get(0).getRevenue());
            int revenueD = revenueA / 1000000;
            revenue.setText("$" + revenueD + "Million");

        }catch (Exception e){
            e.printStackTrace();
        }

        loading.setVisibility(View.GONE);

    }

    private String formatGenre(JSONObject jsonObject){
        List<String> gener1= new ArrayList<>();
        String genres="";

        try{
            for(int i=0;i<jsonObject.getJSONArray("genres").length();i++){
                gener1.add(jsonObject.getJSONArray("genres").getJSONObject(i).getString("name"));
            }
            int sizeGenres=jsonObject.getJSONArray("genres").length();
            for(int i= 0;i<sizeGenres;i++){
                if(i!=0){
                    genres=genres+", ";
                }
                genres = genres + jsonObject.getJSONArray("genres").getJSONObject(i).getString("name");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
       return  genres;
    }
}
