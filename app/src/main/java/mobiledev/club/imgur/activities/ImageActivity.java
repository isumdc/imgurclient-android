package mobiledev.club.imgur.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import mobiledev.club.imgur.BitmapLruCache;
import mobiledev.club.imgur.R;
import mobiledev.club.imgur.models.ImgurImage;

public class ImageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        ImgurImage imgurImage = (ImgurImage)extras.getSerializable("ImgurImage");
        RequestQueue queue = Volley.newRequestQueue(this);
        ImageLoader loader = new ImageLoader(queue, new BitmapLruCache());

        NetworkImageView networkImageView = (NetworkImageView) findViewById(R.id.image);
        networkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
        networkImageView.setErrorImageResId(R.mipmap.ic_launcher);
        networkImageView.setImageUrl(imgurImage.getUrl(), loader);

        TextView titleTextView = (TextView) findViewById(R.id.title_textview);
        titleTextView.setText(imgurImage.getTitle());
        networkImageView.setContentDescription(imgurImage.getTitle());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
