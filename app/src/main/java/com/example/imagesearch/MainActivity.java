package com.example.imagesearch;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity {
    String API_URL = "https://pixabay.com/";
    String q = "bad dog";
    String key = "18604876-1e4ea04639a93002532affbbe";
    EditText inputSearch;
    int width, height;
    Spinner image_type;
    TextView searchResult;

    interface PixabayAPI {
        @GET("/api")
        Call<Response> search(@Query("q") String q, @Query("key") String key, @Query("image_type") String image_type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputSearch = findViewById(R.id.input);

        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        image_type = findViewById(R.id.image_type);
        searchResult = findViewById(R.id.searchResult);
    }

    public void startSearch(String text) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        PixabayAPI api = retrofit.create(PixabayAPI.class);

        Call<Response> get = api.search(text.replace(" ", "+"), key, image_type.getSelectedItem().toString());
        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response r = response.body();
                displayResults(r.hits);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d("mytag", t.getLocalizedMessage());
            }
        };
        get.enqueue(callback);
    }

    public void displayResults(Hit[] hits) {
        searchResult.setText("По вашему запросу найдено " + hits.length + " изображений");
        new DownloadImageTask().execute(hits);
    }

    public void onSearchClick(View v) {
        String text = inputSearch.getText().toString();
        startSearch(text);
    }

    private class DownloadImageTask extends AsyncTask<Hit, Void, ArrayList<Bitmap>> {
        ArrayList<Bitmap> bitmaps;

        public DownloadImageTask(){
            bitmaps = new ArrayList<>();
        }

        protected ArrayList<Bitmap> doInBackground(Hit... hits) {
            for (Hit hit: hits){
                Bitmap bitmap;
                try {
                    InputStream in = new java.net.URL(hit.previewURL).openStream();
                    bitmap = BitmapFactory.decodeStream(in);

                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, (width*bitmap.getHeight())/bitmap.getWidth(), false);
                    bitmaps.add(scaled);
                } catch (Exception e) {
                    Log.e("TAG", e.getMessage());
                    e.printStackTrace();
                }
            }
            return bitmaps;
        }

        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
            ListView list = findViewById(R.id.imagelist);
            ImageListAdapter adapter = new ImageListAdapter(bitmaps, MainActivity.this, width);
            list.setAdapter(adapter);
        }
    }
}
