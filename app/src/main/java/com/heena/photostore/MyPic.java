package com.heena.photostore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MyPic extends Activity implements SeekBar.OnSeekBarChangeListener{

	InterstitialAd interstitialAd;
    double red, green, blue;
	private static int RESULT_LOAD_IMAGE=3;
    private final int RQS_IMAGE=1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri,_fileUri;
    Intent intent;
    String id;
    ImageView img1;
    RelativeLayout relImg;
    FrameLayout frmMnu;
    Bitmap bmp,first,second,thumb,bmpEffect;
    final Context context=this;
    int res,w,h,fr;
    MyView view;
    LinearLayout layoutPhoto,layoutEfect,layoutColorEffects,layoutColorEffect;
    ProgressDialog pd;
    SeekBar seekBlue,seekRed,seekGreen;
    Button btnCancel;
    Thread thread = null;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == 11) {
                while (true) {
                    try {
                        fillImage();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                if (pd != null)
                    pd.dismiss();
                fillImage();
            }
            else if(msg.what==25){}
        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypic);
		relImg=(RelativeLayout)findViewById(R.id.relImg);
        layoutPhoto=(LinearLayout)findViewById(R.id.layoutPhoto);
        layoutEfect=(LinearLayout)findViewById(R.id.layoutEffect);
        frmMnu=(FrameLayout)findViewById(R.id.frmMnu);
        btnCancel=(Button)findViewById(R.id.btnColorCancel);
       // layoutColorEffect.setVisibility(View.GONE);
        seekRed = (SeekBar) findViewById(R.id.seekBar2);
        seekRed.setOnSeekBarChangeListener(this);
        seekGreen = (SeekBar) findViewById(R.id.seekBar1);
        seekGreen.setOnSeekBarChangeListener(this);
        seekBlue = (SeekBar) findViewById(R.id.seekBar3);
        seekBlue.setOnSeekBarChangeListener(this);
        layoutColorEffects=(LinearLayout)findViewById(R.id.layoutColorEffects);
        layoutColorEffects.setVisibility(View.GONE);
        /*try {
            intent = getIntent();
            if (intent != null) {
                Bundle b = intent.getExtras();
                res=b.getInt("item");
                //Bitmap thumb = b.getParcelable("img");
                //img1.setImageBitmap(thumb);
                //img1.setScaleType(ImageView.ScaleType.MATRIX);
                //fillData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception:" + e, Toast.LENGTH_LONG).show();
        }*/
        Bundle bframe=getIntent().getExtras();
        res=bframe.getInt("item");
        fr=Integer.parseInt(bframe.getString("frimg"));
        //Toast toast=Toast.makeText(getApplicationContext(), "Frame:" + fr, Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        //toast.show();
        fillData();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        w = metrics.widthPixels;
        h = metrics.heightPixels;

        first = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        second = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        view=new MyView(getApplicationContext(),first,second,w,h,relImg);
        relImg.addView(view);        
        //bmp = BitmapFactory.decodeResource(MyPic.this.getResources(), fr);
        //fillImage();
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutColorEffects.setVisibility(View.GONE);
            }
        });
        interstitialAd=new InterstitialAd(MyPic.this);
        interstitialAd.setAdUnitId("ca-app-pub-1454034498476542/3443619716");
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        }
        AdView adView=(AdView)findViewById(R.id.adView2);
        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        interstitialAd.loadAd(adRequest);
	}

    private void fillImage() {
        view.setFrameBitmap(bmp);
    }
    private void fillData(){
        pd=ProgressDialog.show(MyPic.this,"","Loading.....",true,false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                        //Bundle bframe=getIntent().getExtras();
                        //res=bframe.getInt("item");
                        //fr=bframe.getInt("frimg");
                        bmp = BitmapFactory.decodeResource(MyPic.this.getResources(), fr);
                        handler.sendEmptyMessage(11);
                }
                catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

        switch (seekBar.getId()) {

            case R.id.seekBar2:
                red = progress * .01;
                break;

            case R.id.seekBar1:
                green = progress * .01;
                break;
            case R.id.seekBar3:
                blue = progress * .01;
                break;
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (thumb != null) {
            System.out.println("In Bitmap if : : : Red = " + red);
            System.out.println("Green is : " + green);
            System.out.println("Blue is :" + blue);
            bmp = doGamma(thumb, red, green, blue);
            view.setBackBitmap(bmp);
            if (red == 0 && green == 0 && blue == 0) {
                view.setBackBitmap(thumb);
            }
        } else {
            System.out.println("In Bitmap Else ::");
        }
    }

    public static Bitmap doGamma(Bitmap src, double red, double green,
                                 double blue) {
        // create output image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                src.getConfig());
        // get image size
        int width = src.getWidth();
        int height = src.getHeight();
        // color information
        int A, R, G, B;
        int pixel;
        // constant value curve
        final int MAX_SIZE = 256;
        final double MAX_VALUE_DBL = 255.0;
        final int MAX_VALUE_INT = 255;
        final double REVERSE = 1.0;

        // gamma arrays
        int[] gammaR = new int[MAX_SIZE];
        int[] gammaG = new int[MAX_SIZE];
        int[] gammaB = new int[MAX_SIZE];

        // setting values for every gamma channels
        for (int i = 0; i < MAX_SIZE; ++i) {
            gammaR[i] = (int) Math.min(
                    MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
                            / red)) + 0.5));
            gammaG[i] = (int) Math.min(
                    MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
                            / green)) + 0.5));
            gammaB[i] = (int) Math.min(
                    MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
                            / blue)) + 0.5));
        }

        // apply gamma table
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // look up gamma
                R = gammaR[Color.red(pixel)];
                G = gammaG[Color.green(pixel)];
                B = gammaB[Color.blue(pixel)];
                // set new color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
    public void shareImg(View v) {
        //ImageView imgv = (ImageView) findViewById(R.id.imgMypic);
        //imgv.setDrawingCacheEnabled(true);
        Bitmap bm=view.getBitmap();// = imgv.getDrawingCache();
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File shareFile = new File(root + "/photo_store");
        if(shareFile.mkdirs()){
        shareFile.mkdirs();
        }
        long time=System.currentTimeMillis();
        String pic = "pic-"+time+".jpeg";
        //OutputStream fout=null;
        Uri outputFileUri;
        try {
            File sdImg = new File(shareFile, pic);
            /*if (sdImg.exists()) {
                sdImg.delete();
            }*/
            FileOutputStream fout = new FileOutputStream(sdImg);//openFileOutput(pic, getApplicationContext().MODE_PRIVATE);//(sdImg);
            //Byte[] buffer=new Byte()
            //fout.write(buffer);
            view.getBitmap().compress(Bitmap.CompressFormat.JPEG, 90, fout);
            fout.flush();
            fout.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://" + sdImg.getAbsolutePath())));
            //Bitmap bmp = view.getBitmap();//((BitmapDrawable) imgv.getDrawable()).getBitmap();
            //File cache = getApplicationContext().getExternalCacheDir();
            //File file = new File(cache, "share.png");
            //if(file.exists()){file.delete();}
           /* try {
              //  FileOutputStream out = new FileOutputStream(file);
                //view.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                //out.flush();
                //out.close();
            } catch (IOException ie) {
                ie.printStackTrace();
            }*/
            Intent in = new Intent(Intent.ACTION_SEND);
            in.setType("image/*");
            in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            in.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + sdImg));
            startActivity(Intent.createChooser(in, "Share  With..."));
        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please Try Again Later", Toast.LENGTH_LONG).show();
        }
    }

    public void frameGal(View v){
        //LayoutInflater li= LayoutInflater.from(context);
        //View promptsView=li.inflate(R.layout.main_menu, null);
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        }
        final Dialog alert=new Dialog(MyPic.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.main_menu);
        LinearLayout camera=(LinearLayout)alert.findViewById(R.id.layoutCamera);
        LinearLayout gallery=(LinearLayout)alert.findViewById(R.id.layoutGallery);
        LinearLayout btnCancel=(LinearLayout)alert.findViewById(R.id.layoutCancel);
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try{
                    startActivityForResult(cameraIntent, RQS_IMAGE);
                }
                catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                alert.dismiss();
            }
        });
        gallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent();
                inte.setType("image/*");
                inte.setAction(Intent.ACTION_GET_CONTENT);
                //inte.setAction(Intent.ACTION_PICK);
                startActivityForResult(
                        Intent.createChooser(inte, "Complete action using"),
                        RESULT_LOAD_IMAGE);
                alert.dismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RQS_IMAGE && resultCode==RESULT_OK && null!=data){
            Bitmap thumb=(Bitmap)data.getExtras().get("data");
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            thumb.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File dest=new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+".jpg");
            mImageCaptureUri=Uri.fromFile(dest);
            FileOutputStream fos;
            try{
                dest.createNewFile();
                fos=new FileOutputStream(dest);
                fos.write(bytes.toByteArray());
                fos.close();
            }
            catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Exception:" + e, Toast.LENGTH_LONG).show();
            }
            //doCrop();
            performCrop(mImageCaptureUri);
        }
        else if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && null!=data){
            //Uri selectedImage=data.getData();
            mImageCaptureUri=data.getData();
            String[] filePathColumn={MediaStore.Images.Media.DATA};
            Cursor cursor=getContentResolver().query(mImageCaptureUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
            String picturePath=cursor.getString(columnIndex);
            cursor.close();
            //img.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/
            //doCrop();
            performCrop(mImageCaptureUri);
        }
        else if(requestCode==CROP_FROM_CAMERA && resultCode==RESULT_OK && null!=data){
            Bundle extr=data.getExtras();
            if(extr!=null){
                thumb=extr.getParcelable("data");
                //		img.setImageBitmap(thumb);
                //Intent intent=new Intent(MyPic.this,MyPic.class);
                //intent.putExtra("img", extr.getParcelable("data"));
                //startActivity(intent);
                view.setBackBitmap(thumb);
            }
            File f = new File(mImageCaptureUri.getPath());
            if (f.exists())
            {	f.delete();	}
		/*	InputMethodManager mgr=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(img, InputMethodManager.SHOW_IMPLICIT);*/
        }
    }
    private void performCrop(Uri uri){
        try{
            Intent cropIntent=new Intent("com.android.camera.action.CROP");
            //cropIntent.setClassName("com.android.camera", "com.android.camera.CropImage");
            cropIntent.setDataAndType(uri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_FROM_CAMERA);
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception:" + e, Toast.LENGTH_LONG).show();
        }
    }
    
    public void popup(View v){
    	int xPos, yPos;
    	int[] location 		= new int[2];
    	WindowManager mWindowManager = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);;
    	LayoutInflater layoutInflater=(LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
    	View popupView=layoutInflater.inflate(R.layout.popup_effect, null);
    	final PopupWindow popupWindow=new PopupWindow(popupView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	popupWindow.setTouchInterceptor(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction()==MotionEvent.ACTION_OUTSIDE){
					popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});
    	Rect anchorRect 	= new Rect(layoutEfect.getLeft()+location[0],layoutEfect.getTop()+location[1], location[0] +layoutEfect.getWidth(),location[1]+layoutEfect.getHeight());
    	popupView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	int scrWidth=mWindowManager.getDefaultDisplay().getWidth();
    	int scrHeight=mWindowManager.getDefaultDisplay().getHeight();
    	int rootHeight=popupView.getMeasuredHeight();
    	int rootWidth=popupView.getMeasuredWidth();
    	if ((anchorRect.left + rootWidth) > scrWidth) {
			xPos 		= anchorRect.left + (rootWidth-layoutEfect.getWidth());			
			xPos 		= (xPos < 0) ? 0 : xPos;
    	}
    	else {
			if (layoutEfect.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() + (rootWidth/2);
			} else {
				xPos = anchorRect.left;
			}
    	}
    	int dyTop			= anchorRect.top;
		int dyBottom		= scrHeight - anchorRect.bottom;

		boolean onTop		= (dyTop < dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight < dyTop) {
				yPos 			= 15;
				//LayoutParams l 	= mScroller.getLayoutParams();
				//l.height		= dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top + rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;
			
			if (rootHeight < dyBottom) { 
				//LayoutParams l 	= mScroller.getLayoutParams();
				//l.height		= dyBottom;
			}
		}
		LinearLayout layoutEffs=(LinearLayout)popupView.findViewById(R.id.layoutEffs);
    	LinearLayout layoutInvertLayout=(LinearLayout)popupView.findViewById(R.id.layoutInvert);
    	LinearLayout layoutGrey=(LinearLayout)popupView.findViewById(R.id.layoutGrey);
    	LinearLayout layoutSpeia=(LinearLayout)popupView.findViewById(R.id.layoutSpeia);
    	LinearLayout layoutEmboss=(LinearLayout)popupView.findViewById(R.id.layoutEmboss);
    	LinearLayout layoutBright=(LinearLayout)popupView.findViewById(R.id.layoutBright);
    	LinearLayout layoutDark=(LinearLayout)popupView.findViewById(R.id.layoutDark);
    	LinearLayout layoutClear=(LinearLayout)popupView.findViewById(R.id.layoutClear);
        LinearLayout layoutColors=(LinearLayout)popupView.findViewById(R.id.layoutColors);
    	layoutInvertLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(thumb!=null){
					bmpEffect=doInvert(thumb);
					view.setBackBitmap(bmpEffect);
				}
				else{
					Toast toast=Toast.makeText(getApplicationContext(), "Please Select/Take Photo", Toast.LENGTH_LONG);
			        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			        toast.show();
				}
				popupWindow.dismiss();
			}
		});
    	layoutGrey.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(thumb!=null){
					bmpEffect=doGreyscale(thumb);
					view.setBackBitmap(bmpEffect);
				}
				else{
					Toast toast=Toast.makeText(getApplicationContext(), "Please Select/Take Photo", Toast.LENGTH_LONG);
			        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			        toast.show();
				}
				popupWindow.dismiss();
			}
		});
    	layoutSpeia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(thumb!=null){
					bmpEffect=createSepiaToningEffect(thumb, 100, 0.90, 0.90, 0.0);
					view.setBackBitmap(bmpEffect);
				}
				else{
					Toast toast=Toast.makeText(getApplicationContext(), "Please Select/Take Photo", Toast.LENGTH_LONG);
			        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			        toast.show();
				}
				popupWindow.dismiss();
			}
		});
    	layoutEmboss.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(thumb!=null){
					bmpEffect=emboss(thumb);
					view.setBackBitmap(bmpEffect);
				}
				else{
					Toast toast=Toast.makeText(getApplicationContext(), "Please Select/Take Photo", Toast.LENGTH_LONG);
			        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			        toast.show();
				}
				popupWindow.dismiss();
			}
		});
    	layoutBright.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(thumb!=null){
					bmpEffect=bright(thumb);
					view.setBackBitmap(bmpEffect);
				}
				else{
					Toast toast=Toast.makeText(getApplicationContext(), "Please Select/Take Photo", Toast.LENGTH_LONG);
			        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			        toast.show();
				}
				popupWindow.dismiss();
			}
		});
    	layoutDark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(thumb!=null){
					bmpEffect=dark(thumb);
					view.setBackBitmap(bmpEffect);
				}
				else{
					Toast toast=Toast.makeText(getApplicationContext(), "Please Select/Take Photo", Toast.LENGTH_LONG);
			        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			        toast.show();
				}
				popupWindow.dismiss();
			}
		});
        layoutColors.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thumb!=null) {
                    layoutColorEffects.setVisibility(View.VISIBLE);
                }
                else{
                    Toast toast=Toast.makeText(getApplicationContext(), "Please Select/Take Photo", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                popupWindow.dismiss();
            }
        });
    	layoutClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(thumb!=null){
					view.setBackBitmap(thumb);					
				}
				else{
					Toast toast=Toast.makeText(getApplicationContext(), "Please Select/Take Photo", Toast.LENGTH_LONG);
			        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			        toast.show();
				}
				popupWindow.dismiss();
			}
		});
    	//popupWindow.showAsDropDown(layoutEfect, 50, 30);
    	popupWindow.showAtLocation(layoutEfect, Gravity.NO_GRAVITY, layoutEfect.getLeft()+scrWidth-layoutEffs.getWidth()-500, layoutEfect.getTop()+scrHeight-layoutEffs.getHeight()-500);
    }
    
    public static Bitmap doInvert(Bitmap src) {
		// create new bitmap with the same settings as source bitmap
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// color info
		int A, R, G, B;
		int pixelColor;
		// image size
		int height = src.getHeight();
		int width = src.getWidth();

		// scan through every pixel
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// get one pixel
				pixelColor = src.getPixel(x, y);
				// saving alpha channel
				A = Color.alpha(pixelColor);
				// inverting byte for each R/G/B channel
				R = 255 - Color.red(pixelColor);
				G = 255 - Color.green(pixelColor);
				B = 255 - Color.blue(pixelColor);
				// set newly-inverted pixel to output image
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final bitmap
		return bmOut;
	}
    
    public static Bitmap doGreyscale(Bitmap src) {
		// constant factors
		final double GS_RED = 0.299;
		final double GS_GREEN = 0.587;
		final double GS_BLUE = 0.114;

		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// pixel information
		int A, R, G, B;
		int pixel;

		// get image size
		int width = src.getWidth();
		int height = src.getHeight();

		// scan through every single pixel
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get one pixel color
				pixel = src.getPixel(x, y);
				// retrieve color of all channels
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// take conversion up to one single value
				R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}
    
    public static Bitmap createSepiaToningEffect(Bitmap src, int depth,
			double red, double green, double blue) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// constant grayscale
		final double GS_RED = 0.3;
		final double GS_GREEN = 0.59;
		final double GS_BLUE = 0.11;
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				// get color on each channel
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// apply grayscale sample
				B = G = R = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);

				// apply intensity level for sepid-toning on each channel
				R += (depth * red);
				if (R > 255) {
					R = 255;
				}

				G += (depth * green);
				if (G > 255) {
					G = 255;
				}

				B += (depth * blue);
				if (B > 255) {
					B = 255;
				}

				// set new pixel color to output image
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}
    public static Bitmap emboss(Bitmap src) {
		double[][] EmbossConfig = new double[][] { { -1, 0, -1 }, { 0, 4, 0 },
				{ -1, 0, -1 } };
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(EmbossConfig);
		convMatrix.Factor = 1;
		convMatrix.Offset = 127;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}
    
    public static Bitmap bright(Bitmap src) {
		Bitmap bmOut=Bitmap.createBitmap(src.getWidth(),src.getHeight(),src.getConfig());
		int A, R, G, B;
		int pixel;
		for(int i=0;i<src.getWidth();i++){
			for(int j=0;j<src.getHeight();j++){
				pixel=src.getPixel(i, j);
				R=Color.red(pixel);
				G=Color.green(pixel);
				B=Color.blue(pixel);
				A=Color.alpha(pixel);
				R=100+R;
				G=100+G;
				B=100+B;
				A=100+A;
				bmOut.setPixel(i, j, Color.argb(A, R, G, B));
			}
		}
		return bmOut;
	}
    
    public static Bitmap dark(Bitmap src){
    	Bitmap bmOut=Bitmap.createBitmap(src.getWidth(),src.getHeight(),src.getConfig());
		int A, R, G, B;
		int pixel;
		for(int i=0;i<src.getWidth();i++){
			for(int j=0;j<src.getHeight();j++){
				pixel=src.getPixel(i, j);
				R=Color.red(pixel);
				G=Color.green(pixel);
				B=Color.blue(pixel);
				A=Color.alpha(pixel);
				R=R-50;
				G=G-50;
				B=B-50;
				A=A-50;
				bmOut.setPixel(i, j, Color.argb(A, R, G, B));
			}
		}
		return bmOut;
    }    
    /*public Bitmap processingBitmap(){
        Bitmap thumb,frame;
        Bitmap newImage;
        int fr=0;
        Bundle b=null;
        try{
            Intent pic1=getIntent();
            Intent picFrame=getIntent();
            if (pic1 != null) {
                b = pic1.getExtras();
            }
            thumb = b.getParcelable("img");
                int w = thumb.getWidth();
                int h = thumb.getHeight();
                newImage=Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas=new Canvas(newImage);
                canvas.drawBitmap(thumb, 0f, 0f, null);
                if(picFrame!=null) {
                    fr = picFrame.getExtras().getInt("item");
                }
                Drawable drawable=getResources().getDrawable(fr);
                drawable.setBounds(0, 0, w, h);
                drawable.draw(canvas);
                return  newImage;
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Exception:"+e,Toast.LENGTH_LONG).show();
            return null;
        }
    }*/

    @Override
    protected void onStop() {

        super.onStop();
        System.gc();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        Runtime.getRuntime().gc();
        first.recycle();
        second.recycle();
        bmp.recycle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // airpush.startAppWall();
            // airpush.startDialogAd();
            // airpush.startLandingPageAd();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
