package com.radargreen.watch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Canvas;

public class RadarActivity extends Activity {
    private View radarView;
    private RadarData data;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(5894 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // The screen is allowed to turn off normally; do not hold a wakelock.
        data = new RadarData(this);
        radarView = new View(this) {
            final RadarRenderer radar = new RadarRenderer(data);
            @Override protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                radar.draw(canvas,getWidth(),getHeight());
            }
        };
        setContentView(radarView);
    }
    @Override protected void onResume() {
        super.onResume();data.start();radarView.post(frame);
    }
    private final Runnable frame = new Runnable() {
        @Override public void run() {
            if (radarView == null) return;
            radarView.invalidate();radarView.postDelayed(this, 50); // ~20 FPS
        }
    };
    @Override protected void onPause() {
        radarView.removeCallbacks(frame);data.stop();super.onPause();
    }
}
