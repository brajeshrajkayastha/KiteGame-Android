package com.example.bpnac.opengltry;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Vector;




    public final class ObjRead {

        public final int numFaces;

        public final float[] normals;
        public final float[] textureCoordinates;
        public final float[] positions;

        public ObjRead(Context context, int fileid) {
            Vector<Float> vertices = new Vector<>();
            Vector<Float> normals = new Vector<>();
            Vector<Float> textures = new Vector<>();
            Vector<String> faces = new Vector<>();

            BufferedReader reader = null;
            try {//context.getAssets().open("hello.txt")
                InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(fileid));
                reader = new BufferedReader(in);

                // read file until EOF
                String line;
                while ((line = reader.readLine()) != null) {

                    String[] parts = line.split(" ");
                    switch (parts[0]) {
                        case "v":
                            // vertices
                            vertices.add(Float.valueOf(parts[1]));
                            vertices.add(Float.valueOf(parts[2]));
                            vertices.add(Float.valueOf(parts[3]));
                            break;
                        case "vt":
                            // textures
                            textures.add(Float.valueOf(parts[1]));
                            textures.add(Float.valueOf(parts[2]));
                            break;
                        case "vn":
                            // normals
                            normals.add(Float.valueOf(parts[1]));
                            normals.add(Float.valueOf(parts[2]));
                            normals.add(Float.valueOf(parts[3]));
                            break;
                        case "f":
                            // faces: vertex/texture/normal
                            faces.add(parts[1]);
                            faces.add(parts[2]);
                            faces.add(parts[3]);
                            break;
                    }
                }
            } catch (IOException e) {
                // cannot load or read file
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception

                    }
                }
            }

            numFaces = faces.size();
            this.normals = new float[numFaces * 3];
            textureCoordinates = new float[numFaces * 2];
            positions = new float[numFaces * 3];
            int positionIndex = 0;
            int normalIndex = 0;
            int textureIndex = 0;
            for (String face : faces) {
                String[] parts = face.split("/");

                int index = 3 * (Short.valueOf(parts[0]) - 1);
                positions[positionIndex++] = (float)vertices.get(index++);
                positions[positionIndex++] = (float)vertices.get(index++);
                positions[positionIndex++] = (float)vertices.get(index);

                index = 2 * (Short.valueOf(parts[1]) - 1);
                textureCoordinates[normalIndex++] = textures.get(index++);
                // NOTE: Bitmap gets y-inverted
                textureCoordinates[normalIndex++] = 1 - textures.get(index);

                index = 3 * (Short.valueOf(parts[2]) - 1);
                this.normals[textureIndex++] = normals.get(index++);
                this.normals[textureIndex++] = normals.get(index++);
                this.normals[textureIndex++] = normals.get(index);

            }
        }
    }

