package com.demotmdb.ui;

import android.arch.persistence.room.Room;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.demotmdb.Adapter.MovieRecyclerAdapter;
import com.demotmdb.BaseActivity;
import com.demotmdb.R;
import com.demotmdb.Setting.Config;
import com.demotmdb.room.Movie;
import com.demotmdb.room.MovieDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    RecyclerView movieRecycler;
    MovieRecyclerAdapter movieRecyclerAdapter;
    public static MovieDatabase movieDatabase;
    View loading;
    private Spinner spinner;
    private static final String[] paths = new String[50];
   SlidingUpPanelLayout mLayout;
   String year, sortby;
   RadioGroup radioGroup;
   Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tolbar setup
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Populer movies");
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));

        movieDatabase= Room.databaseBuilder(getApplicationContext(),MovieDatabase.class,"moviedb")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();

        init();
        loading.setVisibility(View.VISIBLE);
        initrecycler();
        CallMovieApi("", "");

        int year1 = 2019;
        for(int i=0 ; i < 50 ;i++){
            paths[i]= String.valueOf(year1);
            year1=year1-1;
        }
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // radio group sort
       apply.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int selectedId = radioGroup.getCheckedRadioButtonId();

               // find the radiobutton by returned id
             RadioButton  radioButton = (RadioButton) findViewById(selectedId);
             if(radioButton.getText().toString().equals("Popularity")) sortby="popularity.desc";
                 if(radioButton.getText().toString().equals("Rating")) sortby="vote_average.desc";
                     if(radioButton.getText().toString().equals("Revenue")) sortby="revenue.desc";

                     movieDatabase.myDao().deleteMovie();
                     mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                     CallMovieApi(year,sortby);
           }
       });


    }

    private void initrecycler(){
        movieRecyclerAdapter= new MovieRecyclerAdapter(this);
        movieRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        movieRecycler.setAdapter(movieRecyclerAdapter);

        loading.setVisibility(View.GONE);

    }

    private void init() {

        movieRecycler= findViewById(R.id.movieRecycler);
        loading= findViewById(R.id.loading);
        mLayout =  findViewById(R.id.sliding_layout);
        radioGroup= findViewById(R.id.radioGroup);
        apply= findViewById(R.id.applybtn);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.more_tab_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.Filter){
            if (mLayout != null) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    //    String releaseyear,String rating , String latest ,String popularity
    private void CallMovieApi(String year, String sortby){
        call= api.GetMovieList(Config.api_key,year,sortby);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading.setVisibility(View.VISIBLE);

                try{
                    if(response.code()==200){

                        JSONObject jsonObject = new JSONObject(response.body().string());

                        for(int i= 0 ;i< jsonObject.getJSONArray("results").length();i++){

                            Movie movie= new Movie();
                            movie.setId(Integer.parseInt(jsonObject.getJSONArray("results").getJSONObject(i).getString("id")));
                            movie.setOriginal_language(jsonObject.getJSONArray("results").getJSONObject(i).getString("original_language"));
                            movie.setOverview(jsonObject.getJSONArray("results").getJSONObject(i).getString("overview"));
                            movie.setPoster_path(jsonObject.getJSONArray("results").getJSONObject(i).getString("poster_path"));
                            movie.setRelease_date(jsonObject.getJSONArray("results").getJSONObject(i).getString("release_date"));
                            movie.setTitle(jsonObject.getJSONArray("results").getJSONObject(i).getString("title"));
                            movie.setVote_average(jsonObject.getJSONArray("results").getJSONObject(i).getString("vote_average"));

                            movieDatabase.myDao().addMovie(movie);
                            movieRecyclerAdapter.movieAdd();
                        }
                        loading.setVisibility(View.GONE);

                    }else{

                    }
                }catch (Exception e){
                    e.printStackTrace();
//                    Toast.makeText(mContext, R.string.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, R.string.ERROR_MSG, Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);

            }
        });
            }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      year = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
