package android.example.wfranck.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridView gridViewMovies;
    MovieAdapter movieAdapter;
    SharedPreferences sharedPreferences;
    String sortMoviesBy;
    //api key
    String apiKey = "094c998f1fb614f8c1cae3584191324a";

    ArrayList<String> imagesMovies;
    ArrayList<Integer> idsMovies;

    GiveMeMovies giveMeMovies;

    String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridViewMovies = (GridView) findViewById(R.id.gridMovies);

        sharedPreferences = getSharedPreferences("popularMovies",MODE_PRIVATE);
        sortMoviesBy = sharedPreferences.getString("sort_by","popular");

        if (sortMoviesBy.equals("popular")) {
            getSupportActionBar().setTitle(R.string.app_name);
        } else {
            getSupportActionBar().setTitle(R.string.top_rated_movies);
        }

        gridViewMovies = (GridView) findViewById(R.id.gridMovies);
        gridViewMovies.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, MovieDetail.class);
                        Log.d(TAG, "Intent movie Id: " + idsMovies.get(position).toString());
                        intent.putExtra("movie_id", idsMovies.get(position));
//                        intent.putExtra("image_movie", imagesMovies.get(position));
                        startActivity(intent);
                    }
                }
        );

        giveMeMovies = new GiveMeMovies();
        giveMeMovies.execute(sortMoviesBy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        sharedPreferences = getSharedPreferences("popularMovies",MODE_PRIVATE);
        sortMoviesBy = sharedPreferences.getString("sort_by","popular");

        if (sortMoviesBy.equals("popular")) {
            menu.findItem(R.id.popular).setChecked(true);
        } else {
            menu.findItem(R.id.top_rated).setChecked(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        sharedPreferences = getSharedPreferences("popularMovies",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (item.getItemId()) {
            case R.id.popular:
                editor.putString("sort_by", "popular");
                editor.apply();
                recreate();
                return true;
            case R.id.top_rated:
                editor.putString("sort_by", "top_rated");
                editor.apply();
                recreate();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class GiveMeMovies extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String jsonMovies = null;

            idsMovies = new ArrayList<Integer>();
            imagesMovies = new ArrayList<String>();

            try {
                String baseUrl = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(baseUrl + params[0] + "?api_key=" + apiKey);
                Log.d(TAG,"URL: " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0){
                    return null;
                }
                jsonMovies = buffer.toString();
                Log.d(TAG, "JSON: " + jsonMovies);

                JSONObject jsonObject = new JSONObject(jsonMovies);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                JSONObject movie;
                for (int i = 0; i < jsonArray.length(); i++) {
                    movie = jsonArray.getJSONObject(i);
                    idsMovies.add(movie.getInt("id"));
                    imagesMovies.add(movie.getString("poster_path"));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error reading stream", e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            movieAdapter = new MovieAdapter(MainActivity.this, imagesMovies);

            try {
                gridViewMovies.setAdapter(movieAdapter);
            } catch (NullPointerException e) {
                Log.e(TAG, "Error on imageAdapter", e);
            }
        }
    }
}