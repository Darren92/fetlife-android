package com.bitlove.fetlife.model.resource;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bitlove.fetlife.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ImageLoader {

    private static final String TOKEN_MIDFIX = "?token=";

    private static class FetLifeImageDownloader extends OkHttpDownloader {

        Map<String,String> urlTokenMap = new HashMap<>();

        public FetLifeImageDownloader(Context context) {
            super(context);
        }

        public FetLifeImageDownloader(Context context, long maxSize) {
            super(context, maxSize);
        }

        @Override
        public Response load(Uri uri, int networkPolicy) throws IOException {

            String url = uri.toString();

            if (urlTokenMap.containsKey(url)) {
                url += TOKEN_MIDFIX + urlTokenMap.get(url);
                uri = Uri.parse(url);
            }

            return super.load(uri, networkPolicy);
        }
    }

    private FetLifeImageDownloader imageDowloader;

    public ImageLoader(Context context) {
        imageDowloader = new FetLifeImageDownloader(context);

        Picasso.Builder picassoBuilder  = new Picasso.Builder(context);
        picassoBuilder.downloader(imageDowloader);
        Picasso picasso = picassoBuilder.build();
        picasso.setIndicatorsEnabled(false);
        picasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }

    public void loadImage(final Context context, final String imageUrl, final ImageView imageView) {

        final String urlToLoad;

        String[] imageUrlParts = imageUrl.split(Pattern.quote(TOKEN_MIDFIX));

        if (imageUrlParts.length >= 2) {
            urlToLoad = imageUrlParts[0];
            String token = imageUrlParts[1];
            imageDowloader.urlTokenMap.put(urlToLoad, token);
        } else {
            urlToLoad = imageUrl;
        }

        Picasso.with(context)
                .load(urlToLoad)
                .placeholder(R.drawable.dummy_avatar)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(context)
                                .load(urlToLoad)
                                .placeholder(R.drawable.dummy_avatar)
                                .error(R.drawable.dummy_avatar)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
                    }
                });
    }

}
