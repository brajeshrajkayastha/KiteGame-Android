package com.example.bpnac.opengltry;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import com.google.gson.JsonParser;


public class OpenGLRenderer implements GLSurfaceView.Renderer{

    private Context context;

    private extendsOpenGLRenderer extendsopenglrenderer;


    public static float[] ViewMatrix = new float[16];

    public static float[] ProjectionMatrix = new float[16];

    public static float[] MVPMatrix = new float[16];



    private float[] TempMatrix = new float[16];

    private float[] ModelLightMatrix = new float[16];




    private final float[] LightPositionInModelSpace = new float[] {100.0f, 100.0f, -1000.0f, 1.0f};


    private final float[] LightPositionInWorldSpace = new float[4];


    public static final float[] LightPositionInEyeSpace = new float[4];


    public static int ProgramHandle;


    public static int PointProgramHandle;





    public OpenGLRenderer(Context context){
        this.context = context;
        extendsopenglrenderer = new extendsOpenGLRenderer(context);
    }

    public final String linefragmentShader =
            "precision mediump float;\n" +
                    "       \t\t\t\t\t          \n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "//set point color to white with alpha 1\n" +
                    "\t gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);             \n" +
                    "}   ";
    public final String linevertexShader =
            "uniform mat4 u_MVPMatrix;      \t\t\n" +
                    "//position the point in world\n" +
                    "attribute vec4 a_sourcePosition;\n" +
                    "attribute vec4 a_destinationPosition; \n" +
                    "//uniform float point_size;\n" +
                    "vec4 directedVec;\n" +
                    "\n" +
                    "void calcLamda(){" +
                    "   " +
                    "}" +
                    "void CalcDirectionVector(){" +
                    "   directedVec=a_destinationPosition-a_sourcePosition;" +
                    "}"+
                    "void main()                    \n" +
                    "{                              \n" +
                    "CalcDirectionVector();" +
                    "    gl_Position = u_MVPMatrix * a_Position;   \n" +
                    "//set the size of point\n" +
                    "    gl_PointSize = point_size;         \n" +
                    "}   ";

    public final String pointfragmentShader =
            "precision mediump float;\n" +
                    "       \t\t\t\t\t          \n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "//set point color to white with alpha 1\n" +
                    "\t gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);             \n" +
                    "}   ";
    public final String pointvertexShader =
            "uniform mat4 u_MVPMatrix;      \t\t\n" +
                    "//position the point in world\n" +
                    "attribute vec4 a_Position;     \t\t\n" +
                    "uniform float point_size;\n"+
                    "\n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "gl_Position = u_MVPMatrix * a_Position;\n" +
                    "//set the size of point\n" +
                    "    gl_PointSize = 1.0;         \n" +
                    "}   ";

    private final String vertexShader =

                    "//u_MVPMatrix is a uniform matrix that equals to Model (matrix of 4*4)*View*Projection \n" +
                    " uniform mat4 u_MVPMatrix;\n" +
                    "//u_MVMatrix is a uniform matrix that equals to Model (matrix of 4*4)*View \n" +
                    "uniform mat4 u_MVMatrix;\n" +
                     "mat4 aMat4 = mat4(1.0, 0.0, 0.0, 0.0,  // 1. column\n" +
                            "                  0.0, 1.0, 0.0, 0.0,  // 2. column\n" +
                            "                  0.0, 0.0, 1.0, 0.0,  // 3. column\n" +
                            "                  0.0, 0.0, 0.0, 1.0); // 4. column\n" +


                    "attribute vec4 a_Position;//Pass in the position coordinate(x,y,z,w)\n" +
                    "attribute vec3 a_Normal;//passing normal data this is used in fragment shader\n" +
                    "attribute vec2 a_TexCoordinate;//pass in texture coordinates\n" +

                    "varying vec3 v_Position;\n" +
                    "varying vec3 v_Normal;\n" +
                    "varying vec2 v_TexCoordinate;\n" +
                     "//for shadow\n" +
                    "varying vec4 v_ShadowCoord;\n" +

                    "void main()\n" +
                    "{\n" +
                    "v_Position = vec3(u_MVMatrix * a_Position);\n" +
                    "v_TexCoordinate = a_TexCoordinate;\n" +
                    "v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n" +

                            "v_ShadowCoord = aMat4 * a_Position;\n" +
                    "gl_Position = u_MVPMatrix * a_Position;\n" +
                    "} ";

    private final String fragmentShader =
            "precision mediump float;\n" +
                    "uniform vec3 u_LightPos;\n" +
                    "uniform sampler2D u_Texture;\n"+
                    "varying vec3 v_Position;\n" +
                    "varying vec3 v_Normal;\n" +
                    "varying vec2 v_TexCoordinate;\n/*" +

                    "varying vec4 v_ShadowCoord;\n" +
                    "float x_shadow_shift = 1.0;\n" +
                    "float y_shadow_shift = 1.0;*/\n" +

                    "void main()\n" +
                    "{\n" +
                    "    float distance = length(u_LightPos - v_Position);\n" +
                    "    vec3 lightVector = normalize(u_LightPos - v_Position);\n" +
                    "    float diffuse = max(dot(v_Normal, lightVector), 0.0);\n" +
                    "    diffuse = diffuse * (1.0 / distance);\t//must be equal to Color * diffuse * diffuseFactor\n " +
                    "    //diffuse = diffuse + 0.77 ;  \n" +

                    "    vec3 Reflection = reflect(lightVector, v_Normal);\n" +
                    "    float SpecularFactor =max(dot(v_Position,Reflection),0.0);\n" +
                    "    float SpecularColor = 0.1 * SpecularFactor;\n"+
                    "\n" +
                    "//diffusion light+ ambient light + SpecularLight\n" +
                    "//Addition work is to set reflection of object\n" +
                    "/*if (v_ShadowCoord.w>0.0){\n" +
                    "\tfloat shadow = 1.0;\n" +
                    "\n" +
                    "\tfor (float y = -1.5; y <= 1.5; y = y + 1.0) {\n" +
                    "\t\tfor (float x = -1.5; x <= 1.5; x = x + 1.0) {\n" +
                    "\t\t\t\tvec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;\n" +
                    "\n" +
                    "\t\t\t\tfloat distanceFromLight = texture2D(u_Texture, (shadowMapPosition + \n" +
                    "\t\t\t\t                               vec4(x+x_shadow_shift, y+y_shadow_shift, 0.05, 0.0)).st ).z;\n" +
                    "\t\t\t\tfloat bias = 0.0005;\n" +
                    "\n" +
                    "\t\t\t\tshadow +=float(distanceFromLight > shadowMapPosition.z - bias);\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\t\t\n" +
                    "\tshadow /= 16.0;\n" +
                    "\tshadow += 0.2;\n" +
                    "}\n*/\n" +

                    "    gl_FragColor = ((diffuse+0.77+SpecularColor)* texture2D(u_Texture, v_TexCoordinate*40.0));\t\t\n" +
                    "  } ";
    public final String pahadfragmentShader =
            "precision mediump float;\n" +
                    "       \t\t\t\t\t          \n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "//set point color to white with alpha 1\n" +
                    "\t gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);             \n" +
                    "}   ";
    public final String pahadvertexShader =
            "uniform mat4 u_MVPMatrix;      \t\t\n" +
                    "//position the point in world\n" +
                    "attribute vec4 a_Position;     \t\t\n" +
                    "uniform float point_size;\n"+
                    "\n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "gl_Position = u_MVPMatrix * a_Position;\n" +
                    "}   ";

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        MainActivity.firstStart = false;
        //GLES20.glClearColor(0.3f, 0.3f, 0.5f, 0.0f);

        //GLES20.glEnable(GLES20.GL_CULL_FACE);

        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFunc(GLES20.GL_BLEND_COLOR,GLES20.GL_BLEND_COLOR);//GLES20.GL_ONE, GLES20.GL_ONE

        Matrix.setLookAtM(ViewMatrix, 0, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, -5.0f, 0.0f, 3.0f, 0.0f);



        int vertexShaderHandle = Shaders.CompileHandles(GLES20.GL_VERTEX_SHADER,vertexShader);
        int fragmentShaderHandle = Shaders.CompileHandles(GLES20.GL_FRAGMENT_SHADER,fragmentShader);


        //int fragmentShaderHandle = s.CompileHandles(fragmentShader);
        ProgramHandle = Shaders.attachbindshaders(vertexShaderHandle,fragmentShaderHandle,new String[]{"a_Position","a_Normal","a_TexCoordinate"});
        PointProgramHandle = Shaders.attachbindshaders(Shaders.CompileHandles(GLES20.GL_VERTEX_SHADER,pointvertexShader),Shaders.CompileHandles(GLES20.GL_FRAGMENT_SHADER,pointfragmentShader),new String[]{"a_Position"});

        extendsopenglrenderer.on_create();


        //Matrix.setIdentityM(mAccumulatedRotation, 0);
        ObjectHandler.LoadHandles(ProgramHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 1000.0f;

        Matrix.frustumM(ProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public static float xViewangleInDegrees=0;
    public static float yViewangleInDegrees=0;
    public static void xsetViewAngle(float Angle){
        if (Angle<=-40||Angle>=0) {
            return;
        }
        xViewangleInDegrees = Angle;//*(yViewangleInDegrees+1.0f)/2.0f;
        //OpenGLRenderer.ViewMatrix[0]=1;
        Log.d("X angle",""+xViewangleInDegrees+" Y Angle"+yViewangleInDegrees);

    }
    public static float xgetViewAngle(){
        return xViewangleInDegrees;
    }
    public static void ysetViewAngle(float Angle){
        if (Angle<=-75||Angle>=75) {
            return;
        }
        yViewangleInDegrees = Angle;///75.0f;
        //Log.d("X angle",""+xViewangleInDegrees+" Y Angle"+yViewangleInDegrees);
    }
    public static float ygetViewAngle(){
        return yViewangleInDegrees;
    }

    static boolean btnright=false;
    static boolean btnleft=false;

    public static void clickright(boolean state){
        if(state){
            btnright=true;
        }
        else{
            btnright=false;
        }
    }
    public static void clickleft(boolean state){
        if(state){
            btnleft=true;
        }
        else{
            btnleft=false;
        }
    }


    @Override
    public void onDrawFrame(GL10 gl)
    {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glUseProgram(ProgramHandle);


        Matrix.setIdentityM(ViewMatrix,0);
        Matrix.setLookAtM(ViewMatrix, 0, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, -15.0f, 0.0f, 3.0f, 0.0f);

        if(btnright){
            Matrix.rotateM(ViewMatrix,0,xgetViewAngle(),1.0f,0.0f, 0.0f);
            Matrix.rotateM(ViewMatrix, 0,65, 0.0f, 1.0f, 0.0f);
        }
        else if(btnleft){
            Matrix.rotateM(ViewMatrix,0,xgetViewAngle(),1.0f,0.0f, 0.0f);
            Matrix.rotateM(ViewMatrix, 0,-65, 0.0f, 1.0f, 0.0f);
        }
        else{
            Matrix.rotateM(ViewMatrix,0,xgetViewAngle(),1.0f,0.0f, 0.0f);
            Matrix.rotateM(ViewMatrix, 0, ygetViewAngle(), 0.0f, 1.0f, 0.0f);
        }
        Matrix.translateM(ViewMatrix,0,allsdata.PlayerPosX,allsdata.PlayerPosY,allsdata.PlayerPosZ);
        //Matrix.translateM(ViewMatrix,0,-50f,0f,60f);
        //Matrix.rotateM(ViewMatrix, 0, ((xViewangleInDegrees-yViewangleInDegrees)/(xViewangleInDegrees+yViewangleInDegrees)), 0.0f, 0.0f, 1f);
        //Matrix.translateM(ViewMatrix,0,yViewangleInDegrees,0.0f,0.0f);

        Matrix.setIdentityM(ModelLightMatrix, 0);
        //Matrix.rotateM(ModelLightMatrix,0,angleInDegrees,0.0f,1.0f,0.0f);
        //100.0f, 100.0f, -1000.0f;
        Matrix.translateM(ModelLightMatrix, 0, -3000.0f, 3000.0f, -10000.0f);

        Matrix.multiplyMV(LightPositionInWorldSpace, 0, ModelLightMatrix, 0, LightPositionInModelSpace, 0);
        Matrix.multiplyMV(LightPositionInEyeSpace, 0, ViewMatrix, 0, LightPositionInWorldSpace, 0);

        //Matrix.rotateM(ViewMatrix,0,slowangleinDegrees,1/1000f,0.0f,0.0f);

        //arm.drawObject();
        extendsopenglrenderer.on_draw();
        GLES20.glUseProgram(PointProgramHandle);
        drawLight();

    }


    private void drawLight()
    {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(PointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(PointProgramHandle, "a_Position");
        final int pointsize = GLES20.glGetUniformLocation(PointProgramHandle,"point_size");

        GLES20.glUniform1f(pointsize,0.5f);

        GLES20.glVertexAttrib3f(pointPositionHandle, LightPositionInModelSpace[0], LightPositionInModelSpace[1], LightPositionInModelSpace[2]);


        GLES20.glDisableVertexAttribArray(pointPositionHandle);


        Matrix.multiplyMM(MVPMatrix, 0, ViewMatrix, 0, ModelLightMatrix, 0);
        Matrix.multiplyMM(TempMatrix, 0, ProjectionMatrix, 0, MVPMatrix, 0);
        System.arraycopy(TempMatrix, 0, MVPMatrix, 0, 16);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, MVPMatrix, 0);


        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
}

class extendsOpenGLRenderer{
    Context context;

    private Game game = new Game();
    public static ObjectHandler coin;
    public static ObjectHandler lattai;
    public static ObjectHandler Kite;
    public static ObjectHandler pahad;

    public ObjectHandler arm;

    public extendsOpenGLRenderer(Context context){
        this.context =context;
        lattai = new ObjectHandler(this.context,R.raw.stylishkitehandle);
        coin = new ObjectHandler(this.context,R.raw.coin);
        Kite = new ObjectHandler(this.context,R.raw.kite);
        pahad = new ObjectHandler(this.context,R.raw.pahad);

        arm = new ObjectHandler(this.context,R.raw.arm);
    }
    static public float lattaiXcenter;
    static public float lattaiYcenter;
    static public float lattaiZcenter;

    static public float kiteXcenter;
    static public float kiteYcenter;
    static public float kiteZcenter;

    static public float[] linedata = new float[6];
    static public float lattaiangle =0f;
    static public float lattaiRLangle=0f;
    static public float lattaimove= -1.5f;

    public static void setLinedata(){

        kiteXcenter=(Kite.MVshapepredictor()[0]+Kite.MVshapepredictor()[3])/2;
        kiteYcenter=(Kite.MVshapepredictor()[1]+Kite.MVshapepredictor()[4])/2;
        kiteZcenter=(Kite.MVshapepredictor()[2]+Kite.MVshapepredictor()[5])/2;
        lattaiXcenter=(lattai.MVshapepredictor()[0]+lattai.MVshapepredictor()[3])/2;
        lattaiYcenter=(lattai.MVshapepredictor()[1]+lattai.MVshapepredictor()[4])/2;
        lattaiZcenter=(lattai.MVshapepredictor()[2]+lattai.MVshapepredictor()[5])/2;

        linedata[0]=kiteXcenter;
        linedata[1]=kiteYcenter;
        linedata[2]=kiteZcenter;
        linedata[3]=lattaiXcenter;
        linedata[4]=lattaiYcenter;
        linedata[5]=lattaiZcenter;

    }

    public void on_create(){
        lattai.LoadTexture(R.drawable.wood);

        coin.LoadTexture(R.drawable.armydress);

        pahad.LoadTexture(R.drawable.pahad);

        Kite.LoadTexture(R.drawable.armydress);

        arm.LoadTexture(R.drawable.wood);


    }

    float[] ModelTemp =new float[16];
    public void on_draw(){
        Matrix.setIdentityM(ModelTemp,0);
        //Matrix.scaleM(ModelTemp,0,5.0f,5.0f,5.0f);
        Matrix.translateM(ModelTemp,0,0.0f,-2.0f,0.0f);
        Matrix.rotateM(ModelTemp, 0,22,0.0f,1.0f,0.0f);
        pahad.setModelMatrix(ModelTemp);
        pahad.drawObject();

        Matrix.setIdentityM(ModelTemp,0);
        Matrix.invertM(ModelTemp,0,OpenGLRenderer.ViewMatrix,0);
        //Matrix.translateM(ModelTemp,0,allsdata.PlayerPosX,allsdata.PlayerPosY+lattaimove,allsdata.PlayerPosZ);
        Matrix.translateM(ModelTemp, 0,0.0f,lattaimove,-2.50f);
        Matrix.rotateM(ModelTemp, 0, lattaiangle,1, 0, 0);
        if(OpenGLView.lattaiRLtouch){
            Matrix.rotateM(ModelTemp, 0, lattaiRLangle,0, 1, 0);}
        Matrix.scaleM(ModelTemp,0,0.5f,0.5f,0.5f);
        lattai.setModelMatrix(ModelTemp);
        lattai.drawObject();
        //boundxSurface(lattai);

        setLinedata();

        Line eastHorz = new Line();
        eastHorz.SetVerts(lattaiXcenter,lattaiYcenter,lattaiZcenter,kiteXcenter,kiteYcenter,kiteZcenter);

        eastHorz.SetColor(1f, 1f, 1f, 1.0f);

        float[] tempMVPmatrix = new float[16];
        Matrix.multiplyMM(tempMVPmatrix,0,OpenGLRenderer.ProjectionMatrix,0,OpenGLRenderer.ViewMatrix,0);

        eastHorz.draw(tempMVPmatrix);

        Game.gameRunner();
        Log.d("Hello ","Hello sd");


    }


    private void drawline(float[] source, float[] dest){

        Line eastHorz = new Line();
        eastHorz.SetVerts(source[0],source[1],source[2],dest[0],dest[1],dest[2]);
        eastHorz.SetColor(1f, 0f, 0f, 1.0f);
        float[] tempMVPmatrix = new float[16];
        Matrix.multiplyMM(tempMVPmatrix,0,OpenGLRenderer.ProjectionMatrix,0,OpenGLRenderer.ViewMatrix,0);
        eastHorz.draw(tempMVPmatrix);
    }

    public static void boundxSurface(ObjectHandler objectHandler){
        float[] latt=new float[6];
        latt = objectHandler.MVshapepredictor();
        //Log.d("sfs",""+latt[0]+""+latt[1]+""+latt[2]+""+latt[3]+""+latt[4]+""+latt[5]+"");
/*
        drawline(new float[]{latt[3],latt[4],latt[5]},new float[]{latt[0],latt[4],latt[5]});
        drawline(new float[]{latt[0],latt[4],latt[5]},new float[]{latt[0],latt[4],latt[2]});
        drawline(new float[]{latt[0],latt[4],latt[2]},new float[]{latt[3],latt[4],latt[2]});
        drawline(new float[]{latt[3],latt[4],latt[2]},new float[]{latt[3],latt[4],latt[5]});

        drawline(new float[]{latt[3],latt[4],latt[5]},new float[]{latt[3],latt[1],latt[5]});
        drawline(new float[]{latt[3],latt[1],latt[5]},new float[]{latt[3],latt[1],latt[2]});
        drawline(new float[]{latt[3],latt[1],latt[2]},new float[]{latt[3],latt[4],latt[2]});
        drawline(new float[]{latt[3],latt[1],latt[2]},new float[]{latt[0],latt[1],latt[2]});

        drawline(new float[]{latt[0],latt[1],latt[2]},new float[]{latt[0],latt[4],latt[2]});
        drawline(new float[]{latt[0],latt[1],latt[2]},new float[]{latt[0],latt[1],latt[5]});
        drawline(new float[]{latt[0],latt[1],latt[5]},new float[]{latt[0],latt[4],latt[5]});
        drawline(new float[]{latt[0],latt[1],latt[5]},new float[]{latt[3],latt[1],latt[5]});
        //coin.MVshapepredictor();
        //Kite.MVshapepredictor();
        //pahad.MVshapepredictor();
  */
    }

}
class Line {
    private FloatBuffer VertexBuffer;

    private final String VertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +

                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String FragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    protected int GlProgram;
    protected int PositionHandle;
    protected int ColorHandle;
    protected int MVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float LineCoords[] = {
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f
    };

    private final int VertexCount = LineCoords.length / COORDS_PER_VERTEX;
    private final int VertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };

    public Line() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                LineCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        VertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        VertexBuffer.put(LineCoords);
        // set the buffer to read the first coordinate
        VertexBuffer.position(0);

        int vertexShader = Shaders.CompileHandles(GLES20.GL_VERTEX_SHADER, VertexShaderCode);
        int fragmentShader = Shaders.CompileHandles(GLES20.GL_FRAGMENT_SHADER, FragmentShaderCode);

        GlProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(GlProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(GlProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(GlProgram);                  // creates OpenGL ES program executables
    }

    public void SetVerts(float v0, float v1, float v2, float v3, float v4, float v5) {
        LineCoords[0] = v0;
        LineCoords[1] = v1;
        LineCoords[2] = v2;
        LineCoords[3] = v3;
        LineCoords[4] = v4;
        LineCoords[5] = v5;

        VertexBuffer.put(LineCoords);
        // set the buffer to read the first coordinate
        VertexBuffer.position(0);
    }

    public void SetColor(float red, float green, float blue, float alpha) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(GlProgram);

        // get handle to vertex shader's vPosition member
        PositionHandle = GLES20.glGetAttribLocation(GlProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(PositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(PositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VertexStride, VertexBuffer);

        // get handle to fragment shader's vColor member
        ColorHandle = GLES20.glGetUniformLocation(GlProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(ColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        MVPMatrixHandle = GLES20.glGetUniformLocation(GlProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, VertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(PositionHandle);
    }
}