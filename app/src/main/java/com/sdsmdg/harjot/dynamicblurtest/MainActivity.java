package com.sdsmdg.harjot.dynamicblurtest;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {

    ImageView imgView;
    OverlayView overlayView;
    RelativeLayout imgViewRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.image_view);
        imgViewRoot = (RelativeLayout) findViewById(R.id.image_view_root);
        overlayView = (OverlayView) findViewById(R.id.overlay_view);

        imgView.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(MainActivity.this).radius(25).sampling(2).onto(imgViewRoot);
            }
        });
    }
}
