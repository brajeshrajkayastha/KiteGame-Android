package com.example.bpnac.opengltry;

import java.util.Random;

public final class Physics {
    static float coinPosData[][] = {{0.0f,1.5f,-5.0f},{2.0f,2.5f,-5.0f},{-3.0f,-0.5f,-7.0f},{-5.0f,2.5f,-5.0f},{4.25f,1.5f,-3.0f}};
    public static void ObjectCollision(){

        float[] kite = extendsOpenGLRenderer.Kite.MVshapepredictor();
        float[] coin = extendsOpenGLRenderer.coin.MVshapepredictor();

        if(ObjectCollisioncalc(kite,coin)){
            allsdata.IsEaten=true;
        }

    }


    public static boolean ObjectCollisioncalc(float[] obj1, float[] obj2){
        float voldiff1,voldiff2,svol1,svol2, rvol1,rvol2,distance, diff, Xcenter1, Ycenter1, Zcenter1, Xcenter2, Ycenter2, Zcenter2, rad1, rad2;

        Xcenter1=(obj1[0]+obj1[3])/2;
        Ycenter1=(obj1[1]+obj1[4])/2;
        Zcenter1=(obj1[2]+obj1[5])/2;

        Xcenter2=(obj2[0]+obj2[3])/2;
        Ycenter2=(obj2[1]+obj2[4])/2;
        Zcenter2=(obj2[2]+obj2[5])/2;


        rad1=(((obj1[0]-obj1[3])>(obj1[1]-obj1[4])&&(obj1[0]-obj1[3])>(obj1[2]-obj1[5]))?(obj1[0]-obj1[3])/2:(obj1[1]-obj1[4])>(obj1[2]-obj1[5])?(obj1[1]-obj1[4])/2:(obj1[2]-obj1[5])/2);

        rad2=(((obj2[0]-obj2[3])>(obj2[1]-obj2[4])&&(obj2[0]-obj2[3])>(obj2[2]-obj2[5]))?(obj2[0]-obj2[3])
                /2:(obj2[1]-obj2[4])>(obj2[2]-obj2[5])?(obj2[1]-obj2[4])/2:(obj2[2]-obj2[5])/2);

        distance= (float) Math.sqrt(Math.pow((Xcenter2-Xcenter1),2)+Math.pow((Ycenter2-Ycenter1),2)+Math.pow((Zcenter2-Zcenter1),2));
        diff=distance-rad1-rad2;

        if (diff<=0){return true;}else{return false;}

    }

    static boolean intersectcheck(float[] line1, float[] line2){

        float[] p1={}, p2={};
        float x1, x2, y1, y2, z1, z2;


        //YZ
        p1=new float[]{line1[1],line1[2],line1[4],line1[5]};
        p2=new float[]{line2[1],line2[2],line2[4],line2[5]};
        y1=twolineintersect(p1,p2)[0];
        z1=twolineintersect(p1,p2)[1];

        //XY
        p1=new float[]{line1[0],line1[1],line1[3],line1[4]};
        p2=new float[]{line2[0],line2[1],line2[3],line2[4]};
        x1=twolineintersect(p1,p2)[0];
        y2=twolineintersect(p1,p2)[1];

        //XZ
        p1=new float[]{line1[0],line1[2],line1[3],line1[5]};
        p2=new float[]{line2[0],line2[2],line2[3],line2[5]};
        x2=twolineintersect(p1,p2)[0];
        z2=twolineintersect(p1,p2)[1];

       return ((x1==x2 && y1==y2 && z1==z2)?true:false);

    }

    static void endmotion(){

    }

    static float[] twolineintersect(float[] p1, float[] p2){
        float a, b, m1, m2;

        m1=(p1[3]-p1[1])/(p1[2]-p1[0]);
        m2=(p2[3]-p2[1])/(p2[2]-p2[0]);

        a=(p1[1]-p2[1]-(m1*p1[0])+(m2*p2[0]))/(m2-m1);
        b=(p1[1]+(m1*a)-(m1*p1[0]));

        //Log.d("Point:",""+x+","+y);
        return new float[]{a,b};

    }

   // static float prevtime=0;
    public static void drawnewCoin() {
       // prevtime = SystemClock.uptimeMillis();
        //float time = 0;
        //while (true) {
            ///time = SystemClock.uptimeMillis();
            //if ((time - prevtime) >= 5000) {
                allsdata.IsEaten = false;
                Random r = new Random();
                int n = r.nextInt(5-0)+0;
                allsdata.CoinPos= new float[]{coinPosData[n][0],coinPosData[n][1],coinPosData[n][2]};
               // break;
            //}
        //}
    }
}

