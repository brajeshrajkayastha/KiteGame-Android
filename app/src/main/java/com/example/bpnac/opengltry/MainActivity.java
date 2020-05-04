package com.example.bpnac.opengltry;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    OpenGLView openglview;
    static Thread serverThread = null;
    public static List<String> mStrings= new ArrayList<String>();
    public static ArrayAdapter adapter;
    static boolean gamestarted = false;
    static boolean firstStart = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        setContentView(R.layout.activity_main);



        final Button PlayButton = (Button) findViewById(R.id.PlayButton);
        final Button Host = (Button) findViewById(R.id.IamHost);
        final Button Client =(Button) findViewById(R.id.Iamclient);

        final TextView ipaddress = (TextView) findViewById(R.id.ipaddress);
        final Networking n = new Networking(this);
        final Context context = this;
        /*String[] strings = new String[mStrings.size()];
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.layouthost, mStrings.toArray(strings));

        ListView listView = (ListView) findViewById(R.id.PlayersListView);

        listView.setAdapter(adapter);*/

        mStrings.add("You");


        //String[] strings = new String[]{};
        adapter = new ArrayAdapter<String>(this,
                R.layout.layouthost, mStrings);

        ListView listView = (ListView) findViewById(R.id.PlayersListView);
        listView.setAdapter(adapter);



        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(true){
                    //MainActivity.serverThread.stop();
                    try {
                        Networking.serverSocket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    gamestarted =true;
                    firstStart = true;
                    Intent i=new Intent(MainActivity.this,GameStart.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please enter the field correctly.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Host start
                ipaddress.setText(n.getLocalIpAddress(context));
                ipaddress.setEnabled(false);
                Host.setEnabled(false);
                Client.setEnabled(false);

                structPlayerData as=new structPlayerData();
                Alldatas.mPlayerData.add(as);

                MainActivity.serverThread = new Thread(new Networking(MainActivity.this));
                MainActivity.serverThread.start();
                PlayButton.setVisibility(View.VISIBLE);
            }
        });
        Client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Host start
                 //Log.d("(String)ipaddres",""+ipaddress.getText().toString());
                ClientThread c = new ClientThread(MainActivity.this,ipaddress.getText().toString());
                new Thread(c).start();
                Host.setEnabled(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

    }


}

