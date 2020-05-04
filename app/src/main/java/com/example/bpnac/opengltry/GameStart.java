package com.example.bpnac.opengltry;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class GameStart extends Activity {
    OpenGLView openglview;
    public TextView score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.firstStart = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        setContentView(R.layout.gameview);


        //Button pause_button = new Button(this);
        //pause_button.setText("Pause");
        //addContentView(pause_button, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView right = (ImageView) findViewById(R.id.imageView4);
        ImageView left = (ImageView) findViewById(R.id.imageView3);

      score = (TextView) findViewById(R.id.textView3);

      right.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    OpenGLRenderer.clickright(true);
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    OpenGLRenderer.clickright(false);
                }

                return true;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    OpenGLRenderer.clickleft(true);
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    OpenGLRenderer.clickleft(false);
                }

                return true;
            }
        });

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            //return;
            openglview = (OpenGLView) findViewById(R.id.openglss);
        }
        else
        {
            return;
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        openglview.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
        openglview.onPause();
    }

}