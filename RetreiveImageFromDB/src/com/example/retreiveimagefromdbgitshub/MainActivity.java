package com.example.retreiveimagefromdbgitshub;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ImageView img=(ImageView)findViewById(R.id.imageView1);
		try
		{
			HttpClient client=new DefaultHttpClient();
			HttpGet request=new HttpGet("http://10.0.2.2/imguploadretreive/show.php");
			HttpResponse response=client.execute(request);
			
			//get response to byte array
			byte[] content = EntityUtils.toByteArray(response.getEntity());
			
			//set byte array to bitmap
			Bitmap bitmap=BitmapFactory.decodeByteArray(content,0,content.length);
			img.setImageBitmap(bitmap);
			
			Toast.makeText(getApplicationContext(), "try", 1).show();
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}
	}
}
