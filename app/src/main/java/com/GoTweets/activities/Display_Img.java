package com.GoTweets.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.GoTweets.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;

public class Display_Img extends AppCompatActivity {

    private GridView gv_img;
    private ImageAdapter adapter;
    ArrayList<String> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__img);

        gv_img =(GridView)findViewById(R.id.gv_img);
        path = getAllPhotoPath(this);
        initImageLoader(getApplicationContext());

        adapter = new ImageAdapter(getApplicationContext());

        adapter.addAll(path);

        gv_img.setAdapter(adapter);

        gv_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(Display_Img.this,path.get(position), Toast.LENGTH_LONG).show();
                //数据是使用Intent返回
                Intent intent = new Intent();
                intent.putExtra("path",path.get(position));

                Display_Img.this.setResult(RESULT_OK, intent);
                Display_Img.this.finish();

            }
        });


    }

    // get all images path
    public static ArrayList<String> getAllPhotoPath(Activity act) {
        ArrayList<String> galleryList = new ArrayList<String>();
        try {
            final String[] columns = { MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID };
            final String orderBy = MediaStore.Images.Media._ID;
            Cursor imagecursor = act.managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);
            if (imagecursor != null && imagecursor.getCount() > 0) {
                while (imagecursor.moveToNext()) {
                    String item = new String();
                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);
                    item = imagecursor.getString(dataColumnIndex);
                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(galleryList);
        return galleryList;
    }

    public static void initImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.twitter_icon)
                .showImageForEmptyUri(R.drawable.twitter_icon)
                .showImageOnFail(R.drawable.twitter_icon).cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300, true, true, true))
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache());
        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
    }

    public class ImageAdapter extends BaseAdapter {

        private LayoutInflater infalter;
        private ArrayList<String> paths = new ArrayList<String>();

        public ImageAdapter(Context context) {
            infalter = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public String getItem(int position) {
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addAll(ArrayList<String> paths) {
            this.paths.clear();
            this.paths.addAll(paths);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = infalter.inflate(R.layout.image, null);
                holder = new ViewHolder();
                holder.iv_item = (ImageView) convertView.findViewById(R.id.d_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage("file://" + paths.get(position), holder.iv_item);
            return convertView;
        }

        class ViewHolder {
            ImageView iv_item;
        }

    }

}
