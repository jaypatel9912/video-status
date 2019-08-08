package com.jdn.videostatus;

import android.content.Context;
import android.view.OrientationEventListener;

public class OrientationManager extends OrientationEventListener {
    private OrientationListener listener;
    public ScreenOrientation screenOrientation;

    public interface OrientationListener {
        void onOrientationChange(ScreenOrientation screenOrientation);
    }

    public enum ScreenOrientation {
        REVERSED_LANDSCAPE,
        LANDSCAPE,
        PORTRAIT,
        REVERSED_PORTRAIT
    }

    public OrientationManager(Context context, int rate, OrientationListener listener) {
        super(context, rate);
        setListener(listener);
    }

    public OrientationManager(Context context, int rate) {
        super(context, rate);
    }

    public OrientationManager(Context context) {
        super(context);
    }

    public void onOrientationChanged(int orientation) {
        if (orientation != -1) {
            ScreenOrientation newOrientation;
            if (orientation >= 60 && orientation <= 140) {
                newOrientation = ScreenOrientation.REVERSED_LANDSCAPE;
            } else if (orientation >= 140 && orientation <= 220) {
                newOrientation = ScreenOrientation.REVERSED_PORTRAIT;
            } else if (orientation < 220 || orientation > 300) {
                newOrientation = ScreenOrientation.PORTRAIT;
            } else {
                newOrientation = ScreenOrientation.LANDSCAPE;
            }
            if (newOrientation != this.screenOrientation) {
                this.screenOrientation = newOrientation;
                if (this.listener != null) {
                    this.listener.onOrientationChange(this.screenOrientation);
                }
            }
        }
    }

    public void setListener(OrientationListener listener) {
        this.listener = listener;
    }

    public ScreenOrientation getScreenOrientation() {
        return this.screenOrientation;
    }
}
