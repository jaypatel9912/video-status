package com.jdn.videostatus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.wang.avi.AVLoadingIndicatorView;
import com.white.progressview.HorizontalProgressView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class VideoViewActivity extends BaseActivity {

    PlayerView playerView;
    AVLoadingIndicatorView loadingVideo;
    OrientationManager orientationManager;
    String url;
    private AdView mAdView;
    Button download, share;
    HorizontalProgressView progressBar;
    boolean isShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        setContentView(R.layout.activity_new_video_view);

        Utils.setIntPreference(VideoViewActivity.this, Constants.AD_SHOW_COUNT, (Utils.getIntPreference(VideoViewActivity.this, Constants.AD_SHOW_COUNT) + 1));

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("491ADC364AC32187B25F8B6C8FCCDD85").build();
        mAdView.loadAd(adRequest);

        progressBar = findViewById(R.id.progressBar);
        share = findViewById(R.id.share);
        download = findViewById(R.id.download);

        if (Utils.appInstalledOrNot(this, "com.whatsapp")) {
            share.setVisibility(View.VISIBLE);
        } else {
            share.setVisibility(View.GONE);
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShare = true;
                downloadVideo();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShare = false;
                downloadVideo();
            }
        });

        url = getIntent().getExtras().getString(Constants.VIDEO);

        if (url == null)
            finish();

        url = url.replaceAll(" ", "%20");

//        orientationManager = new OrientationManager(VideoViewActivity.this, 3, this);
//        this.orientationManager.enable();
        loadingVideo = findViewById(R.id.avi);
        playerView = findViewById(R.id.video_view);

        if (Globals.player == null) {
            Globals.initPLayer(VideoViewActivity.this);
        }

        initializePlayer();
    }

    private void initializePlayer() {

        playerView.setPlayer(Globals.player);

        Globals.player.setPlayWhenReady(true);
        Globals.player.seekTo(currentWindow, playbackPosition);
        Globals.player.addListener(listener);

        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri);
        Globals.player.prepare(mediaSource, true, false);

    }

    Player.EventListener listener = new Player.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == 4) {
                finish();
            } else if (playbackState == 3) {
                loadingVideo.setVisibility(View.GONE);
            } else if (playbackState == 2) {
                loadingVideo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || Globals.player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT > 23)
            releasePlayer();
    }

    int currentWindow;
    long playbackPosition;
    boolean playWhenReady;

    private void releasePlayer() {
        if (Globals.player != null) {
            playbackPosition = Globals.player.getCurrentPosition();
            currentWindow = Globals.player.getCurrentWindowIndex();
            playWhenReady = Globals.player.getPlayWhenReady();
            Globals.player.removeListener(listener);
        }
    }

//    @SuppressLint("WrongConstant")
//    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
//        switch (screenOrientation) {
//            case REVERSED_LANDSCAPE:
//                setRequestedOrientation(8);
//                return;
//            case LANDSCAPE:
//                Log.e("", "LANDSCAPE");
//                setRequestedOrientation(0);
//                return;
//            case REVERSED_PORTRAIT:
//                setRequestedOrientation(9);
//                return;
//            case PORTRAIT:
//                Log.e("", "PORTRAIT");
//                setRequestedOrientation(1);
//                return;
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23)
            releasePlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Util.SDK_INT > 23)
            releasePlayer();
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        this.playerView.setSystemUiVisibility(4357);
    }

    public void downloadVideo() {
        if (Constants.IS_DOWNLOADING) {
            Toast.makeText(VideoViewActivity.this, R.string.download_in_progress, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int writeStoragePermission = ContextCompat.checkSelfPermission(VideoViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
                return;
            }


            if (url == null)
                return;

            String filename = null;
            filename = url.substring(url.lastIndexOf('/') + 1, url.length());

            final File file = new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_CLIPS + "/" + filename);
            if (!file.exists()) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                new DownloadFile(url, file, new DownloadFile.DownloadListener() {
                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onProgress(int progress) {
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onDownloadComplete() {
                        progressBar.setVisibility(View.GONE);
                        if (isShare) {
                            shareVideo(file);
                        } else {
                            Toast.makeText(VideoViewActivity.this, getString(R.string.download_done), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute();
            } else {
                if (isShare) {
                    shareVideo(file);
                } else {
                    Toast.makeText(VideoViewActivity.this, R.string.video_already_downloaded, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void shareVideo(File file) {
        try {
            Uri uri = Uri.fromFile(file);
            Intent videoshare = new Intent(Intent.ACTION_SEND);
            videoshare.setType("*/*");
            videoshare.setPackage("com.whatsapp");
            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoshare.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(videoshare);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

