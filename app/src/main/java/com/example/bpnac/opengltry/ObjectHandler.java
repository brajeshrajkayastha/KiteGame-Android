package com.example.bpnac.opengltry;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ObjectHandler{
    Context context;
    //define
    private final int BytesInFloat = 4;
    private final int DataSizeofPosition = 3;
    private final int DataSizeofNormal = 3;
    private final int DataSizeofTextureCoordinates = 2;


    public static int mMVPMatrixHandle;
    public static int mMVMatrixHandle;
    public static int mLightPosHandle;
    public static int mTextureUniformHandle;
    public static int mPositionHandle;
    public static int mNormalHandle;
    public static int mTextureCoordinateHandle;

    private final FloatBuffer ObjectPositions;
    private final FloatBuffer ObjectNormals;
    private final FloatBuffer ObjectTextureCoordinates;


    //private ObjectPositionData;
     //ObjectNormalData;
    //final float[] cubeTextureCoordinateData;

    public int TextureFromImg;


    private final int vertexCount;

    public float[] ModelMatrix = new float[16];



    public float x_max,xy_max,xz_max,y_max,yx_max,yz_max,z_max,zx_max,zy_max,x_min,xy_min,xz_min,y_min,yx_min,yz_min,z_min,zx_min,zy_min;

    enum maxmiaxis{
        maxXAxis,
        maxYAxis,
        maxZAxis,
        minXAxis,
        minYAxis,
        minZAxis
    }

    public ObjectHandler(Context context,int RawObjectId){
        this.context = context;
        ObjRead iii = new ObjRead(context, RawObjectId);
        final float[] ObjectPositionData = iii.positions;
        final float[]ObjectNormalData = iii.normals;
        final float[] cubeTextureCoordinateData = iii.textureCoordinates;

        vertexCount = ObjectPositionData.length/3;

        ObjectPositions = ByteBuffer.allocateDirect(ObjectPositionData.length * BytesInFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        ObjectPositions.put(ObjectPositionData).position(0);

        ObjectNormals = ByteBuffer.allocateDirect(ObjectNormalData.length * BytesInFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        ObjectNormals.put(ObjectNormalData).position(0);

        ObjectTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * BytesInFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        ObjectTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        //Matrix.setIdentityM(ModelMatrix, 0);
        //Matrix.translateM(ModelMatrix, 0, 0.0f, -0.0f, -5.0f);
        shapePredicater(iii.positions);
    }

    public void shapePredicater(float[] positions){

        int arr_length =positions.length;
        int posindex=0;
        x_max = positions[0]>positions[arr_length-3]?positions[0]:positions[arr_length-3];
        x_min = positions[0]<positions[arr_length-3]?positions[0]:positions[arr_length-3];
        y_max = positions[1]>positions[arr_length-2]?positions[1]:positions[arr_length-2];
        y_min = positions[1]<positions[arr_length-2]?positions[1]:positions[arr_length-2];
        z_max = positions[2]>positions[arr_length-1]?positions[2]:positions[arr_length-1];
        z_min = positions[2]<positions[arr_length-1]?positions[2]:positions[arr_length-1];

        xy_max = y_max;
        xz_max = z_max;
        yx_max = x_max;
        yz_max = z_max;
        zx_max = x_max;
        zy_max = y_max;

        xy_min = y_min;
        xz_min = z_min;
        yx_min = x_min;
        yz_min = z_min;
        zx_min = x_min;
        zy_min = y_min;

        float value;
        for (int count=0;count<(arr_length/3);count++){
            value = positions[posindex];
            while (true){
                if (x_max<value){
                    x_max=value;
                    xy_max=positions[posindex+1];
                    xz_max=positions[posindex+2];
                    break;
                }
                if (x_min>value){
                    x_min=value;
                    xy_min = positions[posindex+1];
                    xz_min = positions[posindex+2];
                }
                break;
            }
            posindex=posindex+1;
            value = positions[posindex];
            while (true){
                if (y_max<value){
                    y_max=value;

                    yx_max=positions[posindex-1];
                    yz_max=positions[posindex+1];
                    break;
                }
                if (y_min>value){
                    y_min=value;

                    yx_min=positions[posindex-1];
                    yz_min=positions[posindex+1];
                }
                break;
            }
            posindex=posindex+1;
            value = positions[posindex];
            while (true){
                if (z_max<value){
                    z_max=value;

                    zx_max = positions[posindex-2];
                    zy_max = positions[posindex-1];
                    break;
                }
                if (z_min>value){
                    z_min=value;

                    zx_min = positions[posindex-2];
                    zy_min = positions[posindex-1];
                }
                break;
            }
            posindex=posindex+1;
        }
        //return new float[]{x_max,y_max,z_max,x_min,y_min,z_max};
    }
    public float[] MVshapepredictorcalc(maxmiaxis maxmin){
        float[] values=new float[]{};
        switch (maxmin){
            case minXAxis:
                values = new float[]{x_min,xy_min,xz_min,1.0f};
                break;
            case maxXAxis:
                values = new float[]{x_max,xy_max,xz_max,1.0f};
                break;
            case minYAxis:
                values = new float[]{yx_min,y_min,yz_min,1.0f};
                break;
            case maxYAxis:
                values = new float[]{yx_max,y_max,yz_max,1.0f};
                break;
            case minZAxis:
                values = new float[]{zx_min,zy_min,z_min,1.0f};
                break;
            case maxZAxis:
                values = new float[]{zx_max,zy_max,z_max,1.0f};
                break;
        }
        Matrix.multiplyMV(values,0,ModelMatrix,0,values,0);

       /* float[] maxValues=new float[]{x_max,y_max,z_max,1};
        float[] minValues=new float[]{x_min,y_min,z_min,1};
        Matrix.multiplyMV(maxValues,0,ModelMatrix,0,maxValues,0);
        Matrix.multiplyMV(minValues,0,ModelMatrix,0,minValues,0);

        //float[] both = Arrays.copyOf(maxValues, maxValues.length+minValues.length);
        float []temp = new float[6];
        System.arraycopy(maxValues, 0, temp, 0, 3);
        System.arraycopy(minValues, 0, temp, 3, 3);
*/
        return values;
    }

    public float[] MVshapepredictor(){
        float[] temp = new float[6];
        temp[0]= MVshapepredictorcalc(maxmiaxis.maxXAxis)[0];
        temp[1]= MVshapepredictorcalc(maxmiaxis.maxYAxis)[1];
        temp[2]= MVshapepredictorcalc(maxmiaxis.maxZAxis)[2];
        temp[3]= MVshapepredictorcalc(maxmiaxis.minXAxis)[0];
        temp[4]= MVshapepredictorcalc(maxmiaxis.minYAxis)[1];
        temp[5]= MVshapepredictorcalc(maxmiaxis.minZAxis)[2];
        return temp;
    }
    public void LoadTexture(int RawTextureId){
        TextureFromImg = loadTexture.loadingTexture(this.context, RawTextureId);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
    }
    public float[] getModelMatrix(){
        return ModelMatrix;
    }

    public void setModelMatrix(float[] Mat){
        System.arraycopy(Mat, 0, this.ModelMatrix, 0, 16);};

    public static void LoadHandles(int ProgramHandle){
        mMVPMatrixHandle = GLES20.glGetUniformLocation(ProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(ProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(ProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(ProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(ProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(ProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(ProgramHandle, "a_TexCoordinate");
    }

    public void drawObject()
    {
        //Matrix.translateM(ModelMatrix, 0, 0.0f, -0.0f, -5.0f);
        // Matrix.scaleM(mModelMatrix,0,0.5f,0.5f,0.5f);
        //Matrix.rotateM(ModelMatrix,0,angleInDegrees,1.0f,0.0f,0.0f);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.TextureFromImg);

        GLES20.glUniform1i(mTextureUniformHandle, 0);

        ObjectTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, DataSizeofTextureCoordinates, GLES20.GL_FLOAT, false,
                0, ObjectTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        ObjectPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, DataSizeofPosition, GLES20.GL_FLOAT, false,
                0, ObjectPositions);

        GLES20.glEnableVertexAttribArray(mPositionHandle);


        ObjectNormals.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, DataSizeofNormal, GLES20.GL_FLOAT, false,
                0, ObjectNormals);

        GLES20.glEnableVertexAttribArray(mNormalHandle);


        float[] MVPMatrix=new float[16];

        Matrix.multiplyMM(MVPMatrix, 0, OpenGLRenderer.ViewMatrix, 0, ModelMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, MVPMatrix, 0);

        Matrix.multiplyMM(MVPMatrix, 0, OpenGLRenderer.ProjectionMatrix, 0, MVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MVPMatrix, 0);

        GLES20.glUniform3f(mLightPosHandle, OpenGLRenderer.LightPositionInEyeSpace[0], OpenGLRenderer.LightPositionInEyeSpace[1], OpenGLRenderer.LightPositionInEyeSpace[2]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
