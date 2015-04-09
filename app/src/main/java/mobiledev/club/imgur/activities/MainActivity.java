package mobiledev.club.imgur.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mobiledev.club.imgur.BitmapLruCache;
import mobiledev.club.imgur.R;
import mobiledev.club.imgur.adapters.ImgurAdapter;
import mobiledev.club.imgur.models.ImgurImage;
import mobiledev.club.imgur.networking.ImgurRequest;

/**
 * Test activity
 */
public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{

    private List<ImgurImage> mLoadedImages;

    private RequestQueue mQueue;
    private ImageLoader mImageLoader;

    private ProgressDialog mProgress;
    private ImgurAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static int pageCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        pageCount = 0;

        if (getString(R.string.imgur_clientID).equals("YOUR IMGUR CLIENT ID")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle("Error")
                    .setMessage("Please set your imgur client id in the settings.xml")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .create()
                    .show();
        } else {
            getImagesData();
        }
    }

    private void initAdapter() {
//        mImageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
//            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
//
//            public void putBitmap(String url, Bitmap bitmap) {
//                mCache.put(url, bitmap);
//            }
//
//            public Bitmap getBitmap(String url) {
//                return mCache.get(url);
//            }
//        });
        mImageLoader = new ImageLoader(mQueue, new BitmapLruCache());

        ListView list = (ListView) findViewById(R.id.listView);
        adapter = new ImgurAdapter(this, R.layout.img_item, mLoadedImages, mImageLoader);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imageOnClick(parent, view, position, id);
            }
        });


    }

    public void imageOnClick(AdapterView<?> parent, View view, int position, long id)
    {
        ImgurImage selectedImage = mLoadedImages.get(position);

        //Bundle bundle = new Bundle();
        //bundle.putSerializable("ImgurImage", selectedImage);
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("ImgurImage", selectedImage);
        startActivity(intent);
    }

    private void getImagesData() {

        mQueue = Volley.newRequestQueue(this);
        String url = "https://api.imgur.com/3/gallery/random/random/" + pageCount;
        pageCount++;

        ImgurRequest jsonObjectRequest = new ImgurRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseJSONRespone(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", error.toString());
                        mProgress.dismiss();
                        getImagesData(); //sometimes Volley falls into  E/ERROR: com.android.volley.NoConnectionError: java.io.EOFException
                    }
                }
        );


        jsonObjectRequest.setImgurClientId(getString(R.string.imgur_clientID));
        mQueue.add(jsonObjectRequest);

        mProgress = ProgressDialog.show(this, "Please wait.", "Loading images.");
        swipeRefreshLayout.setRefreshing(false);
    }

    private void parseJSONRespone(JSONObject response) {
        try {
            int status = response.getInt("status");
            if (status == 200) {

                JSONArray arr = response.getJSONArray("data");
                final int len = (arr != null) ? arr.length() : 0;

                mLoadedImages = new ArrayList<ImgurImage>(len);

                for (int i = 0; i < len; i++, i++) {
                    JSONObject json = arr.getJSONObject(i);
                    ImgurImage img = new ImgurImage();

                    img.setUrl(json.getString("link").replace("\\", ""));
                    img.setTitle(json.getString("title"));

                    mLoadedImages.add(img);
                }
            }

            mProgress.dismiss();

            initAdapter();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override public void onRefresh() {
        mLoadedImages.clear();
        adapter.clear();
        initAdapter();
        getImagesData();

    }
}