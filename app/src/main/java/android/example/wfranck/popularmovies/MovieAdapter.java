package android.example.wfranck.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mImageMovies;
    private String[] pathImages;

    String baseUrl = "http://image.tmdb.org/t/p/w342";
    String TAG = "ImageAdapter";

    public  MovieAdapter(Context c, ArrayList<String> images) {
        mContext = c;
        mImageMovies = images;
        try {
            pathImages = new String[mImageMovies.size()];
            for (int i = 0; i < mImageMovies.size(); i++) {
                pathImages[i] = baseUrl + mImageMovies.get(i);
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Error on images path", e);
        }
    }

    @Override
    public int getCount() {
        return pathImages.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(342, 511));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4,4,0,0);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.get().load(pathImages[position]).into(imageView);
        return imageView;
    }
}
