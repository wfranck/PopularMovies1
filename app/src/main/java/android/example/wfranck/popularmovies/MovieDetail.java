package android.example.wfranck.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetail extends AppCompatActivity {

    Integer id;
    TextView title, rating, release, synopsis;
    ImageView image;

    //api key
    String apiKey = "094c998f1fb614f8c1cae3584191324a";

    String TAG = "MovieDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title  = (TextView) findViewById(R.id.title);
        rating = (TextView) findViewById(R.id.rating);
        release = (TextView) findViewById(R.id.release_date);
        synopsis = (TextView) findViewById(R.id.synopsis);
        image = (ImageView) findViewById(R.id.image);

        Intent intent = getIntent();
        id = intent.getIntExtra("movie_id", 0);

        GiveMeMovieDetails movieDetails = new GiveMeMovieDetails();
        movieDetails.execute();
    }

    public class GiveMeMovieDetails extends AsyncTask<Void, Void, Void> {

        String mTitle, mRelease, mSynopsis, mImage;
        Double mRating;

        @Override
        protected Void doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String jsonMovie = null;

            try {
                String baseUrl = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(baseUrl + Integer.toString(id) + "?api_key=" + apiKey);
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
                jsonMovie = buffer.toString();
                Log.d(TAG, "JSON: " + jsonMovie);

                JSONObject jsonObject = new JSONObject(jsonMovie);
                mTitle = jsonObject.getString("original_title");
                mRelease = jsonObject.getString("release_date");
                mRating = jsonObject.getDouble("vote_average");
                mSynopsis = jsonObject.getString("overview");
                mImage = "http://image.tmdb.org/t/p/w185" + jsonObject.getString("poster_path");

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
            title.setText(mTitle);
            rating.setText("User Ratings: " + Double.toString(mRating));
            release.setText("Release Date: " + mRelease);
            synopsis.setText(mSynopsis);
            Picasso.get().load(mImage).into(image);
        }
    }
}
