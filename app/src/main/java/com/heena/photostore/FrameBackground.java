package com.heena.photostore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class FrameBackground extends Activity{

	InterstitialAd interstitialAd;
	public static int arr[] = {
         R.drawable.frame_7,R.drawable.frame_9,
        R.drawable.frame_10,R.drawable.frame_11,R.drawable.frame_12,
        R.drawable.frame_13,R.drawable.frame_14,R.drawable.frame_15,R.drawable.frame_16
        ,R.drawable.frame_17,R.drawable.frame_18,R.drawable.frame_19,R.drawable.frame_20,
        R.drawable.frame_21,R.drawable.frame_22,R.drawable.frame_23,R.drawable.frame_24,
        R.drawable.frame_25,R.drawable.frame_26,R.drawable.frame_27,R.drawable.frame_28,R.drawable.frame_29
    ,R.drawable.new_fr16,R.drawable.new_fr17,R.drawable.new_fr21,R.drawable.new_fr26,R.drawable.new_fr27,R.drawable.new_fr47
    ,R.drawable.new_fr22,R.drawable.new_fr36,R.drawable.new_fr59};

	GridView grid;
	public static int index;
	public static boolean isSet = false;
	public static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_bck);
        interstitialAd=new InterstitialAd(FrameBackground.this);
        interstitialAd.setAdUnitId("ca-app-pub-1454034498476542/3443619716");
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        }
        AdView adView=(AdView)findViewById(R.id.adView1);
        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        interstitialAd.loadAd(adRequest);
		grid=(GridView)findViewById(R.id.gridView);		
        grid.setAdapter(new ImageAdapter(FrameBackground.this,arr));
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
                Object item = grid.getItemAtPosition(position);
                String itemid = String.valueOf(item);
                Intent intent=new Intent(getApplicationContext(),MyPic.class);
                Bundle b=new Bundle();
                b.putInt("item",position);
                b.putString("frimg",itemid);
                intent.putExtras(b);
                //intent.putExtra("frimg",itemid);
                startActivity(intent);
                //imgBck.setImageResource(res);
            }
        });
	}
	public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        int data[];
        private LayoutInflater inflater = null;
        Activity activity;
        View vi;
        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public ImageAdapter(Context context) {
            mContext = context;
        }
        public ImageAdapter(Activity activity,int[] data){
            mContext=activity.getApplicationContext();
            this.activity=activity;
            this.data=data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(activity);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) convertView;
            }
            //imageView.setImageResource(arr[position]);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(activity.getResources(), data[position],
                    options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            imageView.setImageBitmap(
                    decodeSampledBitmapFromResource(activity.getResources(), data[position], 100, 100));
            return imageView;
        }

        public int calculateInSampleSize(BitmapFactory.Options options,
                                                int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                // Calculate ratios of height and width to requested height and
                // width
                final int heightRatio = Math.round((float) height
                        / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);

                // Choose the smallest ratio as inSampleSize value, this will
                // guarantee
                // a final image with both dimensions larger than or equal to the
                // requested height and width.
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

            return inSampleSize;
        }
        public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                             int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }
    }
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				// ---read the byte from the stream & stores in the byteArray
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				// ---writes the bytes from the byteArray
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {

		}
	}

	/*public static Drawable fetchDrawable(Activity activity, String urlString) {
		Log.d(activity.getApplicationContext().getClass().getSimpleName(),
				"image url:" + urlString);
		try {
			InputStream is = fetch(urlString);
			Drawable drawable = Drawable.createFromStream(is, "src");

			if (drawable != null) {
				Log.d(activity.getApplicationContext().getClass()
						.getSimpleName(),
						"got a thumbnail drawable: " + drawable.getBounds()
								+ ", " + drawable.getIntrinsicHeight() + ","
								+ drawable.getIntrinsicWidth() + ", "
								+ drawable.getMinimumHeight() + ","
								+ drawable.getMinimumWidth());
			} else {
				Log.w(activity.getApplicationContext().getClass()
						.getSimpleName(), "could not get thumbnail");
			}
			return drawable;
		} catch (MalformedURLException e) {
			Log.e(activity.getApplicationContext().getClass().getSimpleName(),
					"fetchDrawable failed", e);
			return null;
		} catch (IOException e) {
			Log.e(activity.getApplicationContext().getClass().getSimpleName(),
					"fetchDrawable failed", e);
			return null;
		}
	}

	private static InputStream fetch(String urlString)
			throws MalformedURLException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		HttpResponse response = httpClient.execute(request);
		return response.getEntity().getContent();
	}*/

	public static void clearCache() {
		System.out.println("In Clear Cache () : ");
		cache.clear();
	}
}
