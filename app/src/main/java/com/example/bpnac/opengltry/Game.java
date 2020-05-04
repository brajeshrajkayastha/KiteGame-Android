package com.example.bpnac.opengltry;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
class allsdata{

    public static int myId=0;
    int noOfPlayers=1;
    public static float PlayerPosX=25.0f;
    public static float PlayerPosY=0.0f;
    public static float PlayerPosZ=0.0f;

    float[][] kitepositions;

    public static int MyScore=1000;
    public static boolean GameOver = false;
    public static boolean IsEaten =false;
    public static float[] CoinPos= new float[]{0.0f,1.5f,-1.0f};
}
class SceneData{
    public static float max_radius = 25f;
    public static float myrolling_Speed = 0.01f;
    public static float myupgoing_Speed = 0.1f;
}
class GameDatas{
    public static float KiteX;//=0.0f;
    public static float KiteY;//=-2.0f;
    public static float KiteZ;//=-10.0f;
    public static float xzAngle = 90;
    public static float yzAngle = 144;
    public static float u =-10f;
    public static float radius =10;

    public static boolean ColideWithGround=false;


    public static void setKiteX(){
        //GameDatas.KiteX = (float)(xzAngle);
        GameDatas.KiteX = u*(float)Math.cos(Math.toRadians(xzAngle))+(extendsOpenGLRenderer.lattai.MVshapepredictorcalc(ObjectHandler.maxmiaxis.maxXAxis)[0]+extendsOpenGLRenderer.lattai.MVshapepredictorcalc(ObjectHandler.maxmiaxis.minXAxis)[0])/2;
    }
    public static void setKiteZ(){

        GameDatas.KiteZ = u*(float)Math.sin(Math.toRadians(xzAngle))+(extendsOpenGLRenderer.lattai.MVshapepredictorcalc(ObjectHandler.maxmiaxis.maxZAxis)[2]+extendsOpenGLRenderer.lattai.MVshapepredictorcalc(ObjectHandler.maxmiaxis.minZAxis)[2])/2;
        ;
        //GameDatas.KiteZ = 10*(int)(GameDatas.KiteX/Math.abs(GameDatas.KiteX))*(float)(Math.sqrt(1-Math.pow(Math.abs(KiteX),2)));
    }
    public static void setKiteY(){
        u = radius*(float) Math.cos(Math.toRadians(yzAngle));
        GameDatas.KiteY =radius*(float)Math.sin(Math.toRadians(yzAngle))+(extendsOpenGLRenderer.lattai.MVshapepredictorcalc(ObjectHandler.maxmiaxis.maxYAxis)[1]+extendsOpenGLRenderer.lattai.MVshapepredictorcalc(ObjectHandler.maxmiaxis.minYAxis)[1])/2;

        //Log.d("GameDatas.KiteY ",""+radius*(float)Math.sin(Math.toDegrees(yzAngle*Math.PI/180)));
        //GameDatas.KiteZ = 10*(int)(GameDatas.KiteX/Math.abs(GameDatas.KiteX))*(float)(Math.sqrt(1-Math.pow(Math.abs(KiteX),2)));
    }
    public static void setXzAngle(float angle){
        if (angle>0&&angle<180&&!ColideWithGround){
            xzAngle =angle;
        }

        setKiteX();
        setKiteZ();
    }
    public static void setYzAngle(float angle){
        if (angle>=90&&angle<180){
            yzAngle = angle;
            ColideWithGround = false;
        }
        //yzAngle = angle;
        //Log.d("ANGLE2",""+angle);
        setKiteX();
        setKiteZ();
        setKiteY();
        if (angle>=180){
            ColideWithGround = true;
            allsdata.MyScore -= 5;
            //Log.d("MyScore",""+allsdata.MyScore);
            return;

        }

    }
    public static void setRadius(float radi){
        if (radi<SceneData.max_radius&&!ColideWithGround&&radi>2.0)
            radius = radi;
    }
    public static float getYzAngle(){
        return yzAngle;
    }
    public  static float getXzAngle(){
        return xzAngle;
    }
    public static float getRadius(){
        return radius;
    }

}

public final class Game {


    public Game(){
        startGame();
    }

    public boolean GameOverStatus(){
        if (!allsdata.GameOver)
            return false;
        return true;
    }

    public void startGame(){

        return;
    }
    static float[] ModelTemp=new float[16];
    static int a = 0;
    public static void gameRunner(){

        /*if(a>=20){
            Log.d("X,Y,Z","X:="+GameDatas.KiteX+" Y:="+GameDatas.KiteY+" Z:="+GameDatas.KiteZ );
            Log.d("radius",""+GameDatas.getRadius());
            a=0;
        }*/
        //a+=1;

    /*
        Matrix.rotateM(ModelTemp, 0, (-OpenGLRenderer.ygetViewAngle()/1.7f), 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(ModelTemp, 0, (-OpenGLRenderer.xgetViewAngle()/1.7f), 1.0f, 0.0f, 0.0f);

        Matrix.setIdentityM(ModelTemp,0);
        Matrix.scaleM(ModelTemp,0,0.5f,0.25f,0.5f);

        Matrix.translateM(ModelTemp,0,GameDatas.KiteX,GameDatas.KiteY,GameDatas.KiteZ);
        extendsOpenGLRenderer.Kite.setModelMatrix(ModelTemp);
        extendsOpenGLRenderer.Kite.drawObject();
    */
        //extendsOpenGLRenderer.boundxSurface(extendsOpenGLRenderer.Kite);

        GLES20.glUseProgram(OpenGLRenderer.ProgramHandle);
        Matrix.setIdentityM(ModelTemp,0);
        Matrix.translateM(ModelTemp, 0,GameDatas.KiteX,GameDatas.KiteY,GameDatas.KiteZ);
        Matrix.scaleM(ModelTemp, 0, 0.50f, 0.50f, 01.0f);

        Matrix.rotateM(ModelTemp,0,-(OpenGLRenderer.ygetViewAngle()/2),0.0f,1.0f,0.0f);
        Matrix.rotateM(ModelTemp,0,-(OpenGLRenderer.xgetViewAngle()/2),1.0f,0.0f,0.0f);

        if(GameDatas.ColideWithGround){
            Matrix.rotateM(ModelTemp, 0, 90.0f, 1.0f, 0 ,0);
        }
        extendsOpenGLRenderer.Kite.setModelMatrix(ModelTemp);
        extendsOpenGLRenderer.Kite.drawObject();

        for (int i = 0; i < Alldatas.mPlayerData.size(); i++) {

            if (i==(Alldatas.myId))continue;
            structPlayerData data = Alldatas.mPlayerData.get(i);
            Line eastHorz = new Line();

            eastHorz.SetVerts((float)(Alldatas.playerradius*Math.cos(Alldatas.angles[i])),
                    extendsOpenGLRenderer.lattaiYcenter
                    ,(float)(Alldatas.playerradius*Math.sin(Alldatas.angles[i]))
                    ,data.kiteX,
                    data.kiteY,
                    data.kiteZ);

            eastHorz.SetColor(1f, 1f, 1f, 1.0f);

            float[] tempMVPmatrix = new float[16];
            Matrix.multiplyMM(tempMVPmatrix,0,OpenGLRenderer.ProjectionMatrix,0,OpenGLRenderer.ViewMatrix,0);
            eastHorz.draw(tempMVPmatrix);


            /* GLES20.glUseProgram(OpenGLRenderer.ProgramHandle);
            Matrix.setIdentityM(ModelTemp,0);
            Matrix.translateM(ModelTemp, 0,data.kiteX,data.kiteY,data.kiteZ);
            Matrix.scaleM(ModelTemp, 0, 0.50f, 0.50f, 01.0f);

            Matrix.rotateM(ModelTemp,0,-(OpenGLRenderer.ygetViewAngle()/2),0.0f,1.0f,0.0f);
            Matrix.rotateM(ModelTemp,0,-(OpenGLRenderer.xgetViewAngle()/2),1.0f,0.0f,0.0f);

            extendsOpenGLRenderer.Kite.setModelMatrix(ModelTemp);
            extendsOpenGLRenderer.Kite.drawObject();
            */

            if(Physics.intersectcheck(new float[]{data.kiteX,data.kiteY,data.kiteZ,(float)(Alldatas.playerradius*Math.cos(Alldatas.angles[i])),extendsOpenGLRenderer.lattaiYcenter,(float)(Alldatas.playerradius*Math.sin(Alldatas.angles[i]))},extendsOpenGLRenderer.linedata)) {
                data.Score -= 10;
                allsdata.MyScore -=10;

                if(OpenGLView.Dright){
                    OpenGLView.direction=1;
                }

                if(OpenGLView.Dleft){
                    OpenGLView.direction=2;
                }

                if(OpenGLView.Ddown){
                    OpenGLView.direction=3;
                }

                if(OpenGLView.Dup){
                    OpenGLView.direction=4;
                }

            }
            else{OpenGLView.direction=0;}
        }


        GLES20.glUseProgram(OpenGLRenderer.ProgramHandle);

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);

        Matrix.setIdentityM(ModelTemp,0);
        Matrix.translateM(ModelTemp, 0,allsdata.CoinPos[0],allsdata.CoinPos[1],allsdata.CoinPos[2]);
        Matrix.scaleM(ModelTemp, 0, 0.5f, 0.5f, 0.5f);
        Matrix.rotateM(ModelTemp,0,angle,0f,1,0);
        extendsOpenGLRenderer.coin.setModelMatrix(ModelTemp);

        Physics.ObjectCollision();


        //boundxSurface(coin);



        if(allsdata.MyScore<=0){
            allsdata.GameOver=true;
            // Log.d("gameStatus",""+allsdata.GameOver);
        }
    }
}
