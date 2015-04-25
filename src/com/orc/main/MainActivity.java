package com.orc.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView tv;
	private ImageView img;
	
	private final String TESSBASE_PATH = "/mnt/sdcard/";
    private String DEFAULT_LANGUAGE = "digits";
    public TessBaseAPI baseApi;  
    
    private Bitmap bmp;  
    private byte[] mContent;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tv = (TextView)findViewById(R.id.textView1);
        img = (ImageView)findViewById(R.id.imageView1);
        findViewById(R.id.button1).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent intent  = new Intent("android.media.action.IMAGE_CAPTURE");  
                 startActivityForResult(intent, 0);  
			}
			
		});
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ContentResolver contentResolver  =getContentResolver();
		if(requestCode==0){   
            try {  
            	Uri orginalUri = data.getData();  
                mContent = readStream(contentResolver.openInputStream(Uri.parse(orginalUri.toString())));  
                bmp  =getPicFromBytes(mContent,null); 
                mContent = null;
                
//                img.setImageBitmap(bmp);
                Action action = new Action();
                action.execute();
            } catch (Exception e) { 
                e.printStackTrace(); 
                // TODO: handle exception 
            } 
		}
		
	}
    
    public Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {   
	      if (bytes != null)   
	            if (opts != null)   
	                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);   
	            else   
	                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);   
	        return null;   
	}   
	      
	      
	      
	 public byte[] readStream(InputStream in) throws Exception{  
	     byte[] buffer  =new byte[1024];  
	     int len  =-1;  
	     ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	       
	     while((len=in.read(buffer))!=-1){  
	         outStream.write(buffer, 0, len);  
	     }  
	     byte[] data  =outStream.toByteArray();  
	     outStream.close();  
	     in.close();  
	     return data;  
	 }  
	 
	 class Action extends AsyncTask<Void, String, String>{
		 
		public Action( ){
			
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			baseApi = new TessBaseAPI();
            baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
            int newPageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK;
            baseApi.setPageSegMode(newPageSegMode);
            baseApi.setVariable("tessedit_char_whitelist", "0123456789");
            baseApi.setImage(bmp);
            String temp = baseApi.getUTF8Text();
			
			return temp;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			tv.setText(result);
			baseApi.end();
			bmp.recycle();
		}
		 
	 }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
