package com.jdn.videostatus;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.firebase.messaging.FirebaseMessaging;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Globals extends MultiDexApplication{

    public static SimpleExoPlayer player;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        createChannel();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.Announcement);

        Alarm.start(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/JosefinSans-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());

        initPLayer(getApplicationContext());
    }

    public static void initPLayer(Context context){
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(), new DefaultLoadControl());
    }


    public static void releasePlayer() {
        if (Globals.player != null) {
            Globals.player.seekTo(Globals.player.getDuration());
            Globals.player.setPlayWhenReady(false);
            Globals.player.stop();
            Globals.player.release();
            Globals.player = null;
        }
    }


    public static ApiInterface initRetrofit(final Context context) {

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        return retrofit.create(ApiInterface.class);
    }

    public void createChannel() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                CharSequence name = getString(R.string.notification_channel_name);
                String cid = getString(R.string.notification_channel_id);
                String description = getString(R.string.notification_channel_name);

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = null;

                assert mNotificationManager != null;
                mChannel = mNotificationManager.getNotificationChannel(cid);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(cid, name, importance);
                    mChannel.setDescription(description);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotificationManager.createNotificationChannel(mChannel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
