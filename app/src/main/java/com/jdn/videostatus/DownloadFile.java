package com.jdn.videostatus;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mypc on 19/03/18.
 */

public class DownloadFile extends AsyncTask<String, Integer, String> {
    String urlOfFile;
    File file;
    DownloadListener listener;
    boolean isError;

    public interface DownloadListener{
        public void onError();
        public void onProgress(int progress);
        public void onDownloadComplete();
    }

    public DownloadFile(String url, File file, DownloadListener listener) {
        this.urlOfFile = url;
        this.file = file;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        int count;
        try {
            Constants.IS_DOWNLOADING = true;
            URL url = new URL(urlOfFile);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(file.getAbsolutePath());

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / lenghtOfFile));
                Log.d("Progress", ("Audio : " + (int) (total * 100 / lenghtOfFile) + "%"));
                listener.onProgress((int) (total * 100 / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(isError)
            listener.onError();
        else
            listener.onDownloadComplete();
        Constants.IS_DOWNLOADING = false;
        Log.d("File", "Audio downloaded on path : " + file.getAbsolutePath());
    }
}

