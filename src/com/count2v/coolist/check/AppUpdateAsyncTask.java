package com.count2v.coolist.check;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

class AppUpdateAsyncTask extends AsyncTask<String, Integer, Boolean> {
	public static final int TIMEOUT = 3000;
	
	private Context context;
	private String fileName;
	private String downloadUrl;
	private String savePath;
	private ProgressDialog progressDialog;
	
	private boolean stopRunning = false;
	
	private Callback callback;
	
	public interface Callback {
		void onComplete(boolean result);
	}
	
	
	public AppUpdateAsyncTask(Context context, String downloadUrl, String fileName, Callback callback) {
		this.context = context;
		this.downloadUrl = downloadUrl;
		this.fileName = fileName;
		this.callback = callback;
	}
	
	public void stopRunning() {
		stopRunning = true;
	}
	
	public boolean isStopRunning() {
		return stopRunning;
	}
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("�еy�ݤ���");
		progressDialog.setMessage("�U����s�ɤ�...");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(100);
		
		progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if(progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				stopRunning();
			}
		});
		
		progressDialog.show();
		
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			
            // �P�_�O�_��SD�d���s�b�M�s���v��
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // ���oSD�d���|
                String sdPadth = context.getExternalCacheDir().toString();
                savePath = sdPadth + "/download";
                
            } else {
            	savePath = context.getCacheDir().getAbsolutePath();
            	
            }
            
            Log.d(this.getClass().toString(), "savePath: " + savePath);

    		HttpClient httpClient = new DefaultHttpClient();
    		HttpGet httpGet = new HttpGet(downloadUrl);
    		HttpResponse httpResponse;
    		
    		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT);
    		HttpConnectionParams.setSoTimeout(httpClient.getParams(), TIMEOUT);
    		
    		httpResponse = httpClient.execute(httpGet);
    		
    		StatusLine statusLine = httpResponse.getStatusLine();
    		
    		int statusCode = statusLine.getStatusCode();
    		
    		if (statusCode != 200) {
    			Log.d(this.getClass().toString(), "statusCode : " + statusCode);
    			stopRunning();
    			return Boolean.FALSE;
    		}
    		
			HttpEntity entity = httpResponse.getEntity();
    		
            // �ɮפj�p
            long fileSize = entity.getContentLength();

            InputStream inputStream = entity.getContent();
            File file = new File(savePath);
            // �P�_�U����m����Ƨ��O�_�s�b�A���s�b�N�s�W�@��
            if (!file.exists()) {
                file.mkdir();
            }
            
            File apkFile = new File(savePath, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(apkFile);
            int count = 0;
            // �w��
            byte[] buffer = new byte[1024];
            
            
            while(!isStopRunning()) {
                int numRead = inputStream.read(buffer);
                count += numRead;
                
                // �p��i��
                int progress = (int) (((float) count / fileSize) * 100);
                publishProgress(progress);
                
                // ��s�i��
                if (numRead <= 0) { // �U������
                    
                    break;
                }
                // �g�J�ɮ�
                fileOutputStream.write(buffer, 0, numRead);
            }
            
            inputStream.close();
            
            fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
			stopRunning();
        }
		
		return isStopRunning() ? Boolean.FALSE : Boolean.TRUE;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progs) {
		if(progs.length != 1) {
			return;
		}
		
		int progress = progs[0];
		
		Log.d(this.getClass().toString(), "progress: " + progress);
		
		progressDialog.setProgress(progress);
	}
	
    private boolean installApk() {
        File apk = new File(savePath, fileName);
        if (!apk.exists()) {
        	Log.d(this.getClass().toString(), "�ɮפ��s�b�A����w��");
            return false;
        }
        // �ϥ�Intent�w��APK
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apk.toString()), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        
        return true;
    }

	
	@Override
	protected void onPostExecute(Boolean downloadCompleted) {
		super.onPostExecute(downloadCompleted);
		
		boolean result = false;
		
		if(progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		if(downloadCompleted) {
			result = installApk();
		}
		
		callback.onComplete(result);
		
	}
	
	public void run() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Log.d(this.getClass().toString(), "Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else {
			Log.d(this.getClass().toString(), "Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB");
			execute();
		}
	}


}
