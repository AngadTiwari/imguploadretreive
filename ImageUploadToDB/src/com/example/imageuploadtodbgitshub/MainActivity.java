package com.example.imageuploadtodbgitshub;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int SELECT_PICTURE = 0;
	private ImageView imageView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        imageView = (ImageView) findViewById(android.R.id.icon);
    }
    
    //open gallery
    public void pickPhoto(View view) 
    {
    	 /*Intent intent = new Intent();
    	 intent.setType("image/*");
    	 intent.setAction(Intent.ACTION_GET_CONTENT);
    	 Log.i("uploadimg", "before startactivityforresult");
    	 startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);*/
    	Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	startActivityForResult(i, SELECT_PICTURE);
    }
    
    //automatic called when image is selected & set image to imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
   	    Log.i("uploadimg", "inside startactivityforresult");
    	if(requestCode == SELECT_PICTURE)
    	{
    		if(resultCode == RESULT_OK) 
    		{
    			Log.i("uploadimg", "startactivityforresult resultok");
    			Bitmap bitmap = getPath(data.getData());
    			imageView.setImageBitmap(bitmap);
    		}
    	}
    }
    
    private Bitmap getPath(Uri uri) 
    {
    	String[] projection = { MediaStore.Images.Media.DATA };
		Log.i("uploadimg", "startactivityforresult after projection");
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		Log.i("uploadimg", "startactivityforresult after cursor");
		int column_index = cursor.getColumnIndex(projection[0]);
		cursor.moveToFirst();
		String filePath = cursor.getString(column_index);
		Log.i("uploadimg", "startactivityforresult filepath="+filePath);
		cursor.close();
		
		// Convert file path into bitmap image using below line.
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		Log.i("uploadimg", "startactivityforresult before method return");
		return bitmap;
	}
    
    public void uploadPhoto(View view) 
    {
    	try 
    	{
			executeMultipartPost();
		} catch (Exception e) 
		{
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}
    }
    
    public void executeMultipartPost() throws Exception 
    {
    	try 
    	{
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
			BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
			Bitmap bitmap = drawable.getBitmap();
			bitmap.compress(CompressFormat.JPEG, 50, bos);
			byte[] data = bos.toByteArray();

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost("http://10.0.2.2/imguploadretreive/insert.php");

			String fileName = String.format("File_%d.jpg",new Date().getTime());
			ByteArrayBody bab = new ByteArrayBody(data, fileName);

			MultipartEntity reqEntity = new MultipartEntity(
			HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("image", bab);
			postRequest.setEntity(reqEntity);
			int timeoutConnection = 60000;
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
			int timeoutSocket = 60000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpConnectionParams.setTcpNoDelay(httpParameters, true);

			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
			response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();
			
			while ((sResponse = reader.readLine()) != null) 
			{
				s = s.append(sResponse);
			}
			System.out.println("Response: " + s);
    	} 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    		Toast.makeText(getApplicationContext(), e.toString(), 1).show();
		}

	}
}