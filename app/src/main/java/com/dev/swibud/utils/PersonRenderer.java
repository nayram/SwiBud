package com.dev.swibud.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.swibud.R;
import com.dev.swibud.pojo.Users;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by nayrammensah on 11/24/17.
 */

public class PersonRenderer extends DefaultClusterRenderer<Users> {

    private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;
    private final ImageView mImageView;
    private final ImageView mClusterImageView;
    private final int mDimension;
    private final TextView tvName;
    private Context ctx;
    String TAG=getClass().getName();
    List<Drawable> profilePhotos;
    ImageView img;
    int width,height;



    public PersonRenderer(Context context, GoogleMap map, ClusterManager<Users> clusterManager) {
        super(context, map, clusterManager);
        this.ctx=context;
        mIconGenerator=new IconGenerator(context);
        mClusterIconGenerator=new IconGenerator(context);
        img=new ImageView(context);
        View multiProfile = LayoutInflater.from(context).inflate(R.layout.custom_marker_pin, null);
        mClusterIconGenerator.setBackground(ctx.getResources().getDrawable(R.drawable.round_maker));
        mClusterIconGenerator.setContentView(multiProfile);
        mClusterImageView = (ImageView) multiProfile.findViewById(R.id.imgPerson);
        tvName=(TextView) multiProfile.findViewById(R.id.tvPerson);
        mImageView = new ImageView(context);
        mDimension = (int) context.getResources().getDimension(R.dimen.mImageDim);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_profile_padding);
        mImageView.setPadding(padding, padding, padding, padding);
//        mIconGenerator.setBackground(ctx.getResources().getDrawable(R.drawable.round_maker));
//        mIconGenerator.setContentView(mImageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(Users person, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        Log.d(TAG,"BeforeClusterItemRendered");
        if (person.userDetails.size()>0){
            Log.d(TAG,"Details size greater than zero"+" "+person.userDetails.get(0).getUser_image());
//                if (person.userDetails.get(0).getUser_image() !=null){
                    Glide.with(mClusterImageView.getContext())
                            .load(person.userDetails.get(0).getUser_image())
                            .into(mClusterImageView);
//                }
            tvName.setText(person.getTitle());

            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.getFirst_name());
        }

    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Users> cluster, MarkerOptions markerOptions)  {
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
       profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
        width = mDimension;
        height = mDimension;

        for (final Users p : cluster.getItems()) {
            // Draw 4 at most.
            if (p.userDetails.size()>0){
                if (p.userDetails.get(0).getUser_image() != null){

                    //Bitmap bmp = getBitmapFromURL(p.userDetails.get(0).getUser_image());
                    Log.d(TAG,p.userDetails.get(0).getUser_image());
                    Toast.makeText(ctx, p.userDetails.get(0).getUser_image(), Toast.LENGTH_SHORT).show();

                    new LoadUrl(p.userDetails.get(0).getUser_image(),ctx,cluster.getSize(),markerOptions).execute();
                }
            }
        }

//        Log.d(TAG,String.valueOf(profilePhotos.size()));

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }

    void setCluster(Drawable drawable,int size,MarkerOptions markerOptions){
        profilePhotos.add(drawable);

        if (profilePhotos.size()==size){
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);


            mClusterImageView.setImageDrawable(multiDrawable);
            Log.d(TAG,String.valueOf(size));
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(size));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//            Toast.makeText(ctx,"Added to marker options",)
        }
    }



    class LoadUrl extends AsyncTask<Void,Void,Bitmap>{

        String url;
        Context context;
        int size;
        MarkerOptions markerOptions;
        public LoadUrl(String url,Context context,int size,MarkerOptions markerOptions) {
            this.url=url;
            this.context=context;
            this.size=size;
            this.markerOptions=markerOptions;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Drawable drawable = new BitmapDrawable(ctx.getResources(), bitmap);
//            Drawable drawable =;
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
            setCluster(drawable,size,markerOptions);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bmp= getBitmapFromURL(url);
            return bmp;
        }

        public Bitmap getBitmapFromURL(String strURL) {
            try {
                final int THUMBNAIL_SIZE = 96;
                URL url = new URL(strURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();


                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                final int REQUIRED_SIZE=70;
                int width_tmp=o.outWidth, height_tmp=o.outHeight;
                int scale=1;
                while(true){
                    if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                        break;
                    width_tmp/=2;
                    height_tmp/=2;
                    scale*=2;
                }
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize=scale;
                Bitmap myBitmap = BitmapFactory.decodeStream(input, null, o2);
                myBitmap = Bitmap.createScaledBitmap(myBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

}
