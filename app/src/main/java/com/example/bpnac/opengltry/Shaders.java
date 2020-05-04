package com.example.bpnac.opengltry;

import android.opengl.GLES20;

public class Shaders {
    public static int CompileHandles(int type, String ShaderData){
        int ShaderHandle = GLES20.glCreateShader(type);

        if (ShaderHandle != 0) {
            GLES20.glShaderSource(ShaderHandle, ShaderData);
            GLES20.glCompileShader(ShaderHandle);
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(ShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(ShaderHandle);
                ShaderHandle = 0;
            }
        }
        if (ShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.");
        }
        return ShaderHandle;
    }

    public static int attachbindshaders(int shader1, int shader2, String[] abcd){
        int mProgramHandle = GLES20.glCreateProgram();

        if (mProgramHandle != 0) {
            GLES20.glAttachShader(mProgramHandle, shader1);

            GLES20.glAttachShader(mProgramHandle, shader2);
            //"a_Position",  "a_Normal", "a_TexCoordinate"
            for (int count = 0; count<abcd.length; count++){
                GLES20.glBindAttribLocation(mProgramHandle, count, abcd[count]);
            }

            GLES20.glLinkProgram(mProgramHandle);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(mProgramHandle);
                mProgramHandle = 0;
            }
        }

        if (mProgramHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return mProgramHandle;
    }
}
