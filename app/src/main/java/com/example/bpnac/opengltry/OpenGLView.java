package com.example.bpnac.opengltry;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class OpenGLView extends GLSurfaceView {
    enum Controller{
        Left,
        Right,
        Up,
        Down
    }

    private OpenGLRenderer Myrenderer = new OpenGLRenderer(getContext());
    public OpenGLView(Context context) {
        super(context);
        init();

    }
    public OpenGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setBackgroundResource(R.drawable.sky3);
//        setZOrderOnTop(true);
        setRenderer(Myrenderer);
        touch_thread.start();
    }

    AtomicBoolean actionDown = new AtomicBoolean(false);
    static boolean lattaitouch=false;
    static boolean lattaiRLtouch=false;

    public static boolean Dright = false;
    public static boolean Dleft = false;
    public static boolean Ddown = false;
    public static boolean Dup = false;
    public static int direction =0;

    Controller controller;
    private Thread touch_thread= new Thread(
            new Runnable() {
                @Override
                public void run() {
                    int time = 10;
                    while (true) {
                        while (actionDown.get()) {
                            switch (controller) {
                                case Left:
                                    time = 100;
                                    if(!(direction==2)) {
                                        GameDatas.setXzAngle(GameDatas.getXzAngle() + 0.5f);
                                        OpenGLRenderer.ysetViewAngle(OpenGLRenderer.ygetViewAngle() + 0.25f);
                                        extendsOpenGLRenderer.lattaiRLangle = -2.5f;
                                    }
                                    break;

                                case Right:
                                    time = 100;
                                    if(!(direction==1)) {
                                        GameDatas.setXzAngle(GameDatas.getXzAngle() - 0.5f);
                                        OpenGLRenderer.ysetViewAngle(OpenGLRenderer.ygetViewAngle() - 0.25f);
                                        extendsOpenGLRenderer.lattaiRLangle = +2.5f;
                                    }
                                    break;

                                case Up:
                                    time = 100;
                                    if(!(direction==3)) {
                                        GameDatas.setRadius(GameDatas.getRadius() - SceneData.myupgoing_Speed);
                                        GameDatas.setYzAngle(GameDatas.getYzAngle() - 0.25f);
                                        //GameDatas.radius+=1;
                                        OpenGLRenderer.xsetViewAngle(OpenGLRenderer.xgetViewAngle() + 0.155f);
                                        extendsOpenGLRenderer.lattaiangle -= 5.0f;
                                    }
                                    break;

                                case Down:
                                    time = 50;

                                    if(!(direction==4)) {//GameDatas.setRadius(GameDatas.getRadius() - SceneData.myrolling_Speed);
                                        GameDatas.setYzAngle(GameDatas.getYzAngle() - 0.5f);
                                        //GameDatas.setYzAngle(GameDatas.yzAngle-1f);
                                        OpenGLRenderer.xsetViewAngle(OpenGLRenderer.xgetViewAngle() - 0.15f);

                                        extendsOpenGLRenderer.lattaiangle = 0.0f;
                                        extendsOpenGLRenderer.lattaimove = -1.75f;
                                    }
                                    break;
                            }
                            requestRender();
                            try {
                                Thread.sleep(time);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        time = 10;
                        GameDatas.setRadius(GameDatas.getRadius()+0.01f);
                        GameDatas.setYzAngle(GameDatas.getYzAngle()+0.1f);
                        if(!lattaitouch && !GameDatas.ColideWithGround  && !lattaiRLtouch) {
                            extendsOpenGLRenderer.lattaiangle += 5.0f;
                            extendsOpenGLRenderer.lattaimove = -1.5f;
                            OpenGLRenderer.xsetViewAngle(OpenGLRenderer.xgetViewAngle() + 0.75f);
                            extendsOpenGLRenderer.lattaiangle += 5.0f;
                        }//GameDatas.radius-=0.1;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        //Log.d("Down", "F");
                    }
                }
            }
    );

    public void onResume(){
        super.onResume();
    }
    public void onPause(){
        super.onPause();
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float ViewPreviousX;
    private float ViewPreviousY;


    int lastdoubleTaptime;
    //@Override
   public boolean onTouchEvent(MotionEvent e) {
       float screenHeight = getHeight();
       float screenWidth = getWidth();
       float x = e.getX();
       float y = e.getY();
       int pointcounts = e.getPointerCount();
        //float[] xcoords=new float[2];
        //float[] ycoords=new float[2];

       if(y>screenHeight/2.0&&x>(screenWidth/4.0)&&x<(3*screenWidth/4.0)){
           if(pointcounts==2){
               actionDown.set(true);
               controller = Controller.Up;
               Ddown=true;
               //Game.motion(3);
               lattaitouch=true;
               requestRender();
               return true;
           }
       }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                x = e.getX();
                y = e.getY();
                float subtractor;
                //Log.d("Afsd", "onTouchEvent:x: "+x+"bm:   y:   "+y );
                float dviewx = 0;
                float dviewy = 0;
//(y<screenHeight/2 && x> screenWidth/4)|| (y<screenHeight && x>(3*screenWidth/4))
                //y>screenHeight/2&&(x>(3*screenWidth/4)||x<(screenWidth/4)
                if (screenWidth >= 1080)
                {
                    subtractor = 1;
                }
                else{
                    subtractor =2;
                }
                if (y>screenHeight/2.0&&x>(screenWidth/4.0)&&x<(3*screenWidth/4.0)){

                    return true;
                }
                //for view only
                if (x<(screenWidth/4.0) || x>(3*screenWidth/4.0)){
                    dviewy = y - ViewPreviousY;
                    //Log.d("Y:",""+dviewy);
                    //if (x < screenWidth/4.0) {
                    dviewy = dviewy * -2 ;
                    //}

                    OpenGLRenderer.xsetViewAngle(OpenGLRenderer.xgetViewAngle()-dviewy*TOUCH_SCALE_FACTOR);
                    ViewPreviousY = y;
                }

               /* if (x<(screenWidth/4.0)){
                    dviewx = x - ViewPreviousX;
                    //Log.d("X:",""+dviewx);
                    //if (y < screenHeight/2.0){
                    dviewx = dviewx * -subtractor ;
                    //}
                    OpenGLRenderer.ysetViewAngle(OpenGLRenderer.ygetViewAngle()-dviewx*TOUCH_SCALE_FACTOR);

                    ViewPreviousX = x;
                }
                if (x>(3*screenWidth/4.0)){
                    dviewy = y - ViewPreviousY;
                    //Log.d("Y:",""+dviewy);
                    //if (x < screenWidth/4.0) {
                    dviewy = dviewy * -2 ;
                    //}

                    OpenGLRenderer.xsetViewAngle(OpenGLRenderer.xgetViewAngle()-dviewy*TOUCH_SCALE_FACTOR);
                    ViewPreviousY = y;
                }
                */
                /*if (y > screenHeight / 2) {
                   //dviewx = dviewx * -1 ;
                }
                if (x < screenWidth / 2) {
                    //dviewy = dviewy * -1 ;
                }*/


                requestRender();
                break;

            case MotionEvent.ACTION_DOWN:


                if (y>screenHeight/2.0&&x>(screenWidth/4.0)&&x<(screenWidth/2.0-screenWidth/8.0)) {
                    actionDown.set(true);

                    Dright = true;
                    lattaiRLtouch=true;
                    controller = Controller.Right;
                //actionLeft.set(true);
                //Log.d("left","true");
                return true;
        }

                else if (y>screenHeight/2.0&&x>(screenWidth/2.0+screenWidth/8.0)&&x<(3*screenWidth/4.0)) {
                    actionDown.set(true);
                    controller = Controller.Left;
                    Dleft=true;
                    lattaiRLtouch=true;
                    //actionLeft.set(false);
                   /* if (! touch_thread.isAlive()) {
                        touch_thread.start();
                    }*/
                    //Log.d("right", "true");
                    return true;
                }
                else if (y>screenHeight/2.0&& x>(screenWidth/4.0+screenWidth/8.0)&&x<(3*screenWidth/4.0-screenWidth/8.0) ){
                    actionDown.set(true);
                    controller = Controller.Down;
                    Dup=true;
                    lattaitouch=true;
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                actionDown.set(false);
                controller = Controller.Down;
                lattaitouch=false;
                lattaiRLtouch=false;
                Dleft=false;
                Dright=false;
                Dup=false;
                Ddown=false;
                //Game.motion(4);
                //requestRender();
                //return true;
                break;
        }

        return true;
    }
}