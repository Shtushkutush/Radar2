package com.radargreen.watch;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Canvas;

public class RadarWallpaperService extends WallpaperService {
    @Override public Engine onCreateEngine() { return new RadarEngine(); }
    private class RadarEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final RadarData data = new RadarData(RadarWallpaperService.this);
        private final RadarRenderer renderer = new RadarRenderer(data);
        private boolean visible;
        private final Runnable frame = new Runnable() {
            @Override public void run() { draw(); }
        };
        private void draw() {
            handler.removeCallbacks(frame);
            if (!visible) return;
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) renderer.draw(canvas,canvas.getWidth(),canvas.getHeight());
            } catch (IllegalArgumentException | IllegalStateException ignored) {
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }
            if (visible) handler.postDelayed(frame,50);
        }
        @Override public void onVisibilityChanged(boolean v) {
            visible=v;
            if(v){data.start();draw();}
            else {data.stop();handler.removeCallbacks(frame);}
        }
        @Override public void onSurfaceChanged(SurfaceHolder h,int format,int w,int ht) {
            super.onSurfaceChanged(h,format,w,ht);draw();
        }
        @Override public void onDestroy() {
            visible=false;data.stop();handler.removeCallbacks(frame);super.onDestroy();
        }
    }
}
