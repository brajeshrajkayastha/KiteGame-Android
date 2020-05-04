package com.example.bpnac.opengltry;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Networking implements Runnable{
    private final Context context;
    public static ServerSocket serverSocket;
    public static final int SERVERPORT = 1313;
    List<Socket> Sockets= new ArrayList<Socket>();
    //Socket[] socket;
    public static int noOfClients = 1;

    //static public List<String> mStrings = new ArrayList<String>();

    @Override
    public void run() {
        //socket =  new Socket[5];
        try {
            serverSocket = new ServerSocket(SERVERPORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {

                Socket s = null;
                if (noOfClients<=5) {
                    s = serverSocket.accept();
                    Sockets.add(s);
                    //socket[noOfClients-1] = null;
                    //socket[noOfClients-1] = serverSocket.accept();
                }else {
                    serverSocket.close();
                    return;
                }
                noOfClients +=1;
                CommunicationThread commThread = new CommunicationThread(s,noOfClients);
                new Thread(commThread).start();
                abcd(s,noOfClients);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Activity activity;
    public Networking(Context context){
        this.context = context;
        this.activity = (Activity) context;
    }
public void abcd(Socket s,int yourId){
    structPlayerData as=new structPlayerData();
    Alldatas.mPlayerData.add(as);
   /* String[] strings = new String[mStrings.size()];

    ArrayAdapter adapter = new ArrayAdapter<String>(context,
            R.layout.layouthost, mStrings.toArray(strings));*/

   // ListView listView = (ListView) activity.findViewById(R.id.PlayersListView);
    //listView.setAdapter(adapter);
    try {
        MainActivity.mStrings.add("User id"+yourId);
        MainActivity.adapter.notifyDataSetChanged();

        //JsonObject  jsonObj = new JSONObject();
        //jsonObj.addProperty("firstName", "Sergey");
        //jsonObj.addProperty("lastName", "Kargopolov");
        /*String str = "HTTP/1.1 200 OK\n" +
                "Date: Fri, 20 Jul 2018 02:59:43 GMT\n" +
                "Server: Apache/2.4.33 (Win32) OpenSSL/1.1.0h PHP/7.2.6\n" +
                "X-Powered-By: PHP/7.2.6\n" +
                "Expires: Thu, 19 Nov 1981 08:52:00 GMT\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Length: 2\n" +
                "Content-Type: text/html; charset=UTF-8\n" +
                "\n" +
                "-1";*/
        //String str = "{\"YourId\":\""+yourId+"\"}";
        //Socket MySocket = (Socket)(Sockets.get(yourId-1));
        /*PrintWriter out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream())),true);
        out.println(str);
        out.flush();*/
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @SuppressWarnings("deprecation")
    public String getLocalIpAddress(Context context) {

        String iIPv4 = "";

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        iIPv4 = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return iIPv4;
    }


}


class CommunicationThread implements Runnable {
    private Socket clientSocket;
    private int clientId;
    private BufferedReader input;
    private PrintWriter out;
    public CommunicationThread(Socket clientSocket, int ClientId) {
        this.clientId = ClientId;
        this.clientSocket = clientSocket;
        try {
            this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(this.clientSocket.getOutputStream())), true);
            out.println(Alldatas.handshakebeginingString(this.clientId));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            if (MainActivity.firstStart == true){
                try {
                    out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(this.clientSocket.getOutputStream())), true);

                    out.println("{\"Play\":1}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (MainActivity.gamestarted == true) {
                try {
                    String read = input.readLine();
                    structPlayerData as = new structPlayerData();
                    if (read != null) {
                        JSONObject obj;
                            obj = new JSONObject(new JsonParser().parse(read).toString());
                            as.kiteX = (float) obj.getDouble("kiteX");
                            as.kiteY = (float) obj.getDouble("kiteY");
                            as.kiteZ = (float) obj.getDouble("kiteZ");
                            as.isGameOver = (boolean) obj.getBoolean("isGameOver");
                            as.Score = obj.getInt("Score");


                        //Log.d("Alldatas.mPlayerData",Alldatas.mPlayerData.get(this.clientId).isGameOver+"");
                        Alldatas.mPlayerData.set((this.clientId-1), as);
                    }
                    //gamestarted;



                    JsonObject allDatas = new JsonObject();
                    allDatas.addProperty("Play", 1);
                    allDatas.addProperty("NoOfDatas",Networking.noOfClients);
                    JsonObject temp = new JsonObject();
                    temp.addProperty("kiteX", extendsOpenGLRenderer.linedata[0]);
                    temp.addProperty("kiteY", extendsOpenGLRenderer.linedata[1]);
                    temp.addProperty("kiteZ", extendsOpenGLRenderer.linedata[2]);
                    temp.addProperty("isGameOver", allsdata.GameOver);
                    temp.addProperty("Score", allsdata.MyScore);

                    allDatas.add("0", temp);
                    for (int i = 1; i < Networking.noOfClients; i++) {

                        //if (Alldatas.mPlayerData.contains(Integer.toString(i))) {
                            temp = new JsonObject();
                            structPlayerData s;
                            s = Alldatas.mPlayerData.get(i);
                            temp.addProperty("kiteX", s.kiteX);
                            temp.addProperty("kiteY", s.kiteY);
                            temp.addProperty("kiteZ", s.kiteZ);
                            temp.addProperty("isGameOver", s.isGameOver);
                            temp.addProperty("Score", s.Score);
                            allDatas.add(Integer.toString(i), temp);
                        //}
                        //temp.toString();
                    }
                    out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(this.clientSocket.getOutputStream())), true);
                    out.println(allDatas.toString());
                    //out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                } catch (JsonSyntaxException e){
                    e.printStackTrace();
                    continue;
                }



            }
        }
        }

}
class structPlayerData{
    public float kiteX;
    public float kiteY;
    public float kiteZ;
    public boolean isGameOver;
    public int Score;

}
class Alldatas{
    public static int myId=0;
    public static int[] angles = {0,5,10,15,20,25};
    static float playerradius = 10;

    public static List<structPlayerData> mPlayerData= new ArrayList<structPlayerData>();

    public float[] CoinPos;

    public static String handshakebeginingString(int Client_ID){
        JsonObject temp = new JsonObject();
        temp.addProperty("ClientId",  Client_ID);
        return temp.toString();
    }

    public static String getStringa(){
        JsonObject temp = new JsonObject();
        temp.addProperty("kiteX", extendsOpenGLRenderer.linedata[0]);
        temp.addProperty("kiteY", extendsOpenGLRenderer.linedata[1]);
        temp.addProperty("kiteZ", extendsOpenGLRenderer.linedata[2]);
        temp.addProperty("isGameOver",allsdata.GameOver);
        temp.addProperty("Score",allsdata.MyScore);


        return temp.toString();
    }
}
class ClientThread implements Runnable {
    String SERVER_IP = "172.17.1.99";
    boolean firstTime=true;
    Socket socket = null;
    Context context;

    private BufferedReader input;
    private PrintWriter out;

    public ClientThread(Context context,String IP){
        this.SERVER_IP = IP;
        this.context = context;
    }
    @Override
    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            socket = new Socket(serverAddr, Networking.SERVERPORT);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        begin();
        isplay();
        while (!Thread.currentThread().isInterrupted()) {

            String read=null;
            try {
                read = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            structPlayerData as =new structPlayerData();
            if (read!=null) {
                JSONObject obj;
                try {

                    obj = new JSONObject(new JsonParser().parse(read).toString());

                    for (int count=0;count<obj.getInt("NoOfDatas");count++) {
                        //if (count==Alldatas.myId)continue;
                        JSONObject kobj = obj.getJSONObject(Integer.toString(count));
                        as.kiteX = (float) kobj.getDouble("kiteX");
                        as.kiteY = (float) kobj.getDouble("kiteY");
                        as.kiteZ = (float) kobj.getDouble("kiteZ");
                        as.isGameOver = (boolean) kobj.getBoolean("isGameOver");
                        as.Score = kobj.getInt("Score");
                        if (firstTime){
                            Alldatas.mPlayerData.add(as);
                        }
                        else{
                            Alldatas.mPlayerData.set(count, as);
                        }
                    }
                    firstTime=false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String myData = Alldatas.getStringa();
                try {
                    out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(this.socket.getOutputStream())), true);
                    out.println(myData);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Log.d("asda","asd");
        }
    }
    public void isplay(){
        while(true){
            String read=null;
            try {
                read = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (read!=null) {
                JSONObject obj;
                try {
                    obj = new JSONObject(new JsonParser().parse(read).toString());
                    int Play = obj.getInt("Play");
                    if (Play==1){
                        Intent i=new Intent(this.context,GameStart.class);
                        this.context.startActivity(i);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    /*public void onClick(View view) {
        try {
            String str = "Latto it worked";
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),true);
            out.println(str);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void begin(){
        while(true){
            String read=null;
            try {
                read = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
                if (read!=null) {
                    JSONObject obj;
                    int ClientId = 0;
                    try {
                        obj = new JSONObject(new JsonParser().parse(read).toString());
                        ClientId = obj.getInt("ClientId");
                        Alldatas.myId =  ClientId;
                        allsdata.PlayerPosX = (float)(Alldatas.playerradius*Math.cos(Alldatas.angles[ClientId]));
                        allsdata.PlayerPosZ =(float)( Alldatas.playerradius*Math.sin(Alldatas.angles[ClientId]));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            String myData = Alldatas.getStringa();
            try {
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(this.socket.getOutputStream())), true);
                out.println(myData);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
                break;
        }
    }
}

class misc_works{
    public void Fetch_clientdata(String jsonStr){
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray PlayerData = jsonObj.getJSONArray("ClientData");

                // looping through All Contacts
                for (int i = 0; i < PlayerData.length(); i++) {
                    JSONObject c = PlayerData.getJSONObject(i);

                    JSONObject PlayerPosition = c.getJSONObject("MyPos");
                    String PlayerPosX = PlayerPosition.getString("CenterPosX");
                    String PlayerPosY = PlayerPosition.getString("CenterPosY");
                    String PlayerPosZ = PlayerPosition.getString("CenterPosZ");
                    JSONArray KitePosition = c.getJSONArray("MyKitePos");
                    float[] KiteModelMatrix = new float[16];
                    KiteModelMatrix[0] = (float)KitePosition.get(0);

                    // Phone node is JSON Object
                    JSONObject phone = c.getJSONObject("phone");
                    String mobile = phone.getString("mobile");
                    String home = phone.getString("home");
                    String office = phone.getString("office");

                    // tmp hash map for single contact
                    HashMap<String, String> contact = new HashMap<>();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}