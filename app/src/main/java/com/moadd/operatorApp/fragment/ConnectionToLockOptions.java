package com.moadd.operatorApp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.client.android.CaptureActivity;
import com.moadd.operatorApp.AppDetailsPojo;
import com.moadd.operatorApp.BarcodeResultSend;
import com.moadd.operatorApp.GetDateAndTime;
import com.moadd.operatorApp.Image;
import com.moadd.operatorApp.LockBelongsToOperatorOrNot;
import com.moadd.operatorApp.LocksecreateCode;
import com.moadd.operatorApp.Login;
import com.moadd.operatorApp.MainActivity;
import com.moadd.operatorApp.OperatorLockSetupDetails;
import com.moadd.operatorApp.wifiHotSpots;
import com.moaddi.operatorApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.moadd.operatorApp.MainActivity.CURRENT_TAG;
import static com.moadd.operatorApp.fragment.HomeFragment.typeOfData;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionToLockOptions extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    Button bar,add;
    String toSendToServer="";
    TextView p1,p2,p3;
    ListView lv;
    ArrayAdapter<String> aa;
    public static ArrayList<String> al;
    EditText barcode;
    String contents;
    SharedPreferences sp;
    SharedPreferences transfer;
    wifiHotSpots hotutil;
    String message = "";
    ServerSocket serverSocket;
    String msgReply;
    LocksecreateCode l;
    public static int flag=0;
    String serialNumber,lastMessage="";
    SharedPreferences supplierSelected;
    SharedPreferences.Editor et;
    OperatorLockSetupDetails lsd;
    int length;
    public ConnectionToLockOptions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connection_to_lock_options, container, false);
        //CAMERA PERMISSION NECESSARY FOR BARCODE
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        lv = (ListView) v.findViewById(R.id.lv);
        al = new ArrayList<String>();
        aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, al);
        lv.setAdapter(aa);
        l=new LocksecreateCode();
        bar = (Button) v.findViewById(R.id.bar);
        barcode = (EditText) v.findViewById(R.id.bartext);
        add = (Button) v.findViewById(R.id.add);
        p1= (TextView) v.findViewById(R.id.p1);
        p2= (TextView) v.findViewById(R.id.p2);
        p3= (TextView) v.findViewById(R.id.p3);
        lsd=new OperatorLockSetupDetails();
        sp = getActivity().getSharedPreferences("Credentials", MODE_PRIVATE);
        supplierSelected=getActivity().getSharedPreferences("Setup",MODE_PRIVATE);
        et=supplierSelected.edit();
        transfer =  getActivity().getSharedPreferences("Setup", MODE_PRIVATE);
        hotutil = new wifiHotSpots(getActivity());
        //hotutil.startHotSpot(true);
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                al.clear();
                aa.notifyDataSetChanged();
                p1.setBackgroundColor(Color.parseColor("#ffffff"));
                p2.setBackgroundColor(Color.parseColor("#ffffff"));
                p3.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( barcode.getText().toString().trim().equals("A020A605EBF3") && al.size()==0) {
                    //new HttpRequestTask().execute();
                    al.add("A020A605EBF3");
                    aa.notifyDataSetChanged();
                    hotutil.startHotSpot(true);
                    toSendToServer="";
                } else {
                    Toast.makeText(getActivity(), "Enter Some Barcode/Serial Number First", Toast.LENGTH_LONG).show();
                }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                al.remove(position);
                aa.notifyDataSetChanged();
            }
        });
        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, 0);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                contents = data.getStringExtra("result");
                barcode.setText(contents);
                new HttpRequestTask().execute();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast.makeText(getActivity(), "No Output", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        // String a=null;
        String la = null;

        @Override
        public String doInBackground(Void... params) {
            try {
                //The link on which we have to POST data and in return it will return some data
                String URL = "https://www.moaddi.com/moaddi/operator/serviesisLockBelongsToOperator.htm";
                //String URL = "http://192.168.0.104:8081/Moaddi1/operator/serviesisLockBelongsToOperator.htm";
                //String URL = "http://192.168.0.109:8080/Moaddi1/operator/serviesisLockBelongsToOperator.htm";
                LockBelongsToOperatorOrNot ap = new LockBelongsToOperatorOrNot();
                ap.setLockBarcode(barcode.getText().toString());
                //First Static then Dynamic afterwards
                //ap.setUserRoleId("13");
                ap.setUserRoleId(Login.userRoleId);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                la = restTemplate.postForObject(URL, ap, String.class);
                return la;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String m) {
            if (m.equals("true")) {

                al.add(al.size() + 1 + ". " + barcode.getText().toString());
                aa.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Lock Added", Toast.LENGTH_LONG).show();
                barcode.setText("");
                barcode.setTextColor(Color.parseColor("#000000"));
            } else if (m.equals("false")) {
                Toast.makeText(getActivity(), "This Lock Doesn't belong to this Operator", Toast.LENGTH_LONG).show();
                barcode.setTextColor(Color.parseColor("#FF0000"));
            } else if (m.equals("lockIdnotthere")) {
                Toast.makeText(getActivity(), "Lock doesn't belong to Moaddi.com", Toast.LENGTH_LONG).show();
                barcode.setTextColor(Color.parseColor("#FF0000"));
            } else {
                Toast.makeText(getActivity(), "Internet Problem", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 5000;
        int count = 0;

        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // info.setText("I'm waiting here: "+ serverSocket.getLocalPort());
                       // Toast.makeText(getActivity(), serverSocket.getInetAddress().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(
                            socket.getInputStream());
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());

                    String messageFromClient = "";

                    //If no message sent from client, this code will block the program
                    messageFromClient = dataInputStream.readUTF();
                   /* if (messageFromClient.length()==10)
                    {
                        //hotutil.shredAllWifi();
                        //static as of now
                        msgReply ="9839386601";
                       // msgReply= "Hotspot Name : "+transfer.getString("OpHotspot",null)+"\n"+"Hotspot Password : "+transfer.getString("OpHotpassword",null)+"\n"+"Password Requirement : "+transfer.getString("OpPwNeeded",null)+"\n"+"Operator Setup Status : "+transfer.getInt("OperatorSetupStatus",0);
                    }*/
                    //else if (HomeFragment.al.contains(messageFromClient))

                    if (messageFromClient.equals("FAIL1")) {
                      //  p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                        //Toast.makeText(getActivity(), "Secret Numbers don't match", Toast.LENGTH_LONG).show();
                        messageFromClient = "Secret Numbers don't match : "+messageFromClient;
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL2")) {
                       // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                        //Toast.makeText(getActivity(), "Problem saving date and time data", Toast.LENGTH_LONG).show();
                        messageFromClient = "Problem saving date and time data : "+messageFromClient;
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL3")) {
                        //p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                        //Toast.makeText(getActivity(), "Problem saving Operator IDs data", Toast.LENGTH_LONG).show();
                        messageFromClient = "Problem saving Operator IDs data : "+messageFromClient;
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL4")) {
                       // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                       // Toast.makeText(getActivity(), "Problem saving Operator Hotspot Details", Toast.LENGTH_LONG).show();
                        messageFromClient = "Problem saving Operator Hotspot Details : "+messageFromClient;
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL5")) {
                       // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                       // Toast.makeText(getActivity(), "Problem saving Supplier Hotspot Details", Toast.LENGTH_LONG).show();
                        messageFromClient = "Problem saving Supplier Hotspot Details : "+messageFromClient;
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL6")) {
                       // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                       // Toast.makeText(getActivity(), "Problem saving Connected Supplier Details", Toast.LENGTH_LONG).show();
                        messageFromClient = "Problem saving Connected Supplier Details : "+messageFromClient;
                        et.putString("LockFailHistory",supplierSelected.getString("LockFailHistory","")+"#"+al.get(0)).apply();
                       /* al.remove(0);
                        aa.notifyDataSetChanged();*/
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL7")) {
                        // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                        // Toast.makeText(getActivity(), "Problem saving Connected Supplier Details", Toast.LENGTH_LONG).show();
                        messageFromClient = "Problem saving Barcode Details : "+messageFromClient;
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAILRESET")) {
                        // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                        //Toast.makeText(getActivity(), "Problem saving date and time data", Toast.LENGTH_LONG).show();
                        messageFromClient = "Problem resetting lock details : "+messageFromClient;
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("SUCCESS1")) {
                       // p3.setBackgroundColor(Color.parseColor("#008000"));
                        if (typeOfData == 1) {
                            msgReply = "&"+GetDateAndTime.timeStamp()+"#"+transfer.getString("wifiTimeLimit","1");
                        } else if (typeOfData == 2) {
                            msgReply = "RESET";
                        }
                        //toSendToServer=toSendToServer+msgReply+",";
                        lsd.setTimeAndLimit(msgReply);
                        lastMessage=messageFromClient;
                    }
                    else if (messageFromClient.equals("SUCCESS2")) {
                        //p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply ="%"+sp.getString("userId","")+"*"+sp.getString("appId","") ;
                        //toSendToServer=toSendToServer+msgReply+",";
                        lsd.setUserIDandAppId(msgReply);
                        lastMessage=messageFromClient;
                    }
                    else if (messageFromClient.equals("SUCCESS3")) {
                       // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "$"+transfer.getString("OpHotspot", null) + "*" + transfer.getString("OpHotpassword", "");
                        //toSendToServer=toSendToServer+msgReply+",";
                        lsd.setOpSetup(msgReply);
                        lastMessage=messageFromClient;
                    }
                    else if (messageFromClient.equals("SUCCESS4")) {
                       // p3.setBackgroundColor(Color.parseColor("#008000"));
                       // msgReply ="@"+transfer.getString("SuHotspot", null) + "*" + transfer.getString("SuHotpassword", null) + "*" + transfer.getString("SuPwNeeded", null);
                        msgReply ="@"+transfer.getString("SuHotspot", null) + "*" + transfer.getString("SuHotpassword", "") ;
                        //toSendToServer=toSendToServer+msgReply+",";
                        lsd.setSupSetup(msgReply);
                        lastMessage=messageFromClient;
                    }
                    else if (messageFromClient.equals("SUCCESS5")) {
                         // msgReply = "!"+"1234567890*0987654321#9839381234*4321839389#7888888888*8888888887" ;
                       /* msgReply="";
                        String v= sp.getString("SelectedSuppliers","");
                        if (!v.equals("") && v.charAt(0)=='$') {
                            v.replaceFirst("$", "").trim();
                            String[] arr = v.split("$");
                            for (int i = 0; i < arr.length; i++) {
                                msgReply = arr[i] + "*" + arr[i] + "#";
                                //Here I have to replace SupplierList.selectedSupplierList.get(i) with App Id of that supplier
                            }
                        }
                        msgReply="!"+msgReply;*/
                        msgReply = "";
                        String arr[] = supplierSelected.getString("SuSelectedIds", "").trim().split(" ");
                        for (int i = 0; i < arr.length; i++) {
                            if (msgReply.equals("")) {
                                msgReply = msgReply + arr[i] + "*" + arr[i];
                            } else {
                                msgReply = msgReply + "#" + arr[i] + "*" + arr[i];
                            }
                        }
                        msgReply="!"+msgReply.trim();
                        //toSendToServer=toSendToServer+msgReply+",";
                        lsd.setConnectedSuppIds(msgReply);
                        lastMessage=messageFromClient;
                    }
                    else if (messageFromClient.equals("SUCCESSRESET")) {
                        // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT" ;
                        hotutil.startHotSpot(false);
                        lastMessage=messageFromClient;
                        /*al.remove(0);
                        aa.notifyDataSetChanged();*/
                    }
                    else if (messageFromClient.equals("SUCCESS6")) {
                       // p3.setBackgroundColor(Color.parseColor("#008000"));
                        //SUCCESS6 means that the Supplier Ids that we linked with this lock was successfull so we will update the status of the lock :
                        et.putString("LockStatus",supplierSelected.getString("LockStatus","")+"#"+"A020A605EBF3"+"-"+supplierSelected.getString("SuSelectedIds", "").trim().replaceAll(" ",",")).apply();
                        //Sending barcode image encoded data :
                        Bitmap bm =  BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.barcodestatic);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        /*StringBuilder sb=new StringBuilder();
                        sb.append(b);*/
                        String encodedImage = Base64.encodeToString(b ,Base64.DEFAULT);
                        //msgReply="-"+encodedImage;
                        msgReply="-"+"Encoded Image";
                        //length=encodedImage.length();
                        lastMessage=messageFromClient;
                      /*  Bitmap bm =  BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.bar);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b ,Base64.DEFAULT);
                        Image m=new Image();
                        m.setData(encodedImage);
                        ObjectMapper mapper = new ObjectMapper();
                        //Object to JSON in String
                        String jsonInString = mapper.writeValueAsString(m);
                        msgReply="-"+jsonInString;*/
                    }
                    else if (messageFromClient.equals("SUCCESS7")) {
                        // p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "DISCONNECT";
                        messageFromClient= "Successfull Data Transfer Complete.";
                        hotutil.startHotSpot(false);
                        //  Toast.makeText(getActivity(), "Successfull Data Transfer Complete", Toast.LENGTH_LONG).show();
                        et.putString("LockSuccessHistory",supplierSelected.getString("LockSuccessHistory","")+"#"+al.get(0)).apply();
                        //Here send all that data to website too
                       // new HttpRequestTask3().execute();
                        lastMessage=messageFromClient;
                    }
                        //Toast.makeText(getActivity(), "Error in connection",Toast.LENGTH_LONG).show();
                        //Send serial number to the website and based on response(Barcode image and barcode setup status of the lock),send details to the lock
                       //else if (messageFromClient.equals(al.get(al.size()-1)))
                    else if(messageFromClient.equals("A020A605EBF3"))
                        {
                           /* l.setLockSnoNew(messageFromClient);
                            new HttpRequestTask1().execute();
                            p1.setBackgroundColor(Color.parseColor("#008000"));*/
                           //toSendToServer=toSendToServer+messageFromClient+",";
                           serialNumber=messageFromClient;
                            msgReply = "9839386601";
                            lsd.setLockSno(messageFromClient);
                            lastMessage=messageFromClient;
                           // p1.setBackgroundColor(Color.parseColor("#008000"));
                        }
                    /*else if (messageFromClient.contains("FAIL"))
                    {
                        messageFromClient = "Data exchange failed"+ messageFromClient;
                    }*/
                        else
                    {
                        //p1.setBackgroundColor(Color.parseColor("#FF0000"));
                        msgReply="DISCONNECT";
                        //Toast.makeText(getActivity(), "Unexpected response from the lock! Try again", Toast.LENGTH_LONG).show();
                    }

                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n"
                            + "Message from client: " + messageFromClient + "\n";
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(),serverSocket.getInetAddress().toString(),Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (lastMessage.contains("SUCCESS1"))
                            {
                                p1.setBackgroundColor(Color.parseColor("#008000"));
                            }
                            if (lastMessage.equals("SUCCESS6"))
                            {
                                p2.setBackgroundColor(Color.parseColor("#008000"));

                            }
                            if (lastMessage.equals("Successfull Data Transfer Complete."))
                            {
                                p3.setBackgroundColor(Color.parseColor("#008000"));
                                al.remove(0);
                                aa.notifyDataSetChanged();
                                p1.setBackgroundColor(Color.parseColor("#ffffff"));
                                p2.setBackgroundColor(Color.parseColor("#ffffff"));
                                p3.setBackgroundColor(Color.parseColor("#ffffff"));
                                new HttpRequestTask3().execute();

                            }
                            if (lastMessage.equals("FAIL1"))
                            {
                                p1.setBackgroundColor(Color.parseColor("#FF0000"));

                            }
                            if (lastMessage.equals("FAIL6"))
                            {
                                p2.setBackgroundColor(Color.parseColor("#FF0000"));

                            }
                            if (lastMessage.equals("FAIL7"))
                            {
                                p3.setBackgroundColor(Color.parseColor("#FF0000"));
                                al.remove(0);
                                aa.notifyDataSetChanged();
                            }
                            if (lastMessage.equals("SUCCESSRESET"))
                            {
                                p1.setBackgroundColor(Color.parseColor("#008000"));
                                p2.setBackgroundColor(Color.parseColor("#008000"));
                                p3.setBackgroundColor(Color.parseColor("#008000"));
                                p1.setBackgroundColor(Color.parseColor("#ffffff"));
                                p2.setBackgroundColor(Color.parseColor("#ffffff"));
                                p3.setBackgroundColor(Color.parseColor("#ffffff"));

                            }
                            if (lastMessage.equals("FAILRESET"))
                            {
                                al.remove(0);
                                aa.notifyDataSetChanged();
                                p1.setBackgroundColor(Color.parseColor("#FF0000"));
                                p2.setBackgroundColor(Color.parseColor("#FF0000"));
                                p3.setBackgroundColor(Color.parseColor("#FF0000"));
                            }
                        }
                    });

                    /*String msgReply = "Hotspot Name : "+" Umar "+"\n" + "Hotspot Password : "+" 1234ab "+"\n"+"Password Requirement : "+" Yes "+"\n";*/
                    dataOutputStream.writeUTF(msgReply);

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                final String errMsg = e.toString();
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                    }
                });

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private class HttpRequestTask1 extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                //The link on which we have to POST data and in return it will return some data
                //String URL = "http://192.168.0.104:8081/Moaddi1/operator/serviesoperatorlockSerialnumber";
                String URL = "https://www.moaddi.com/moaddi/operator/serviesoperatorlockdetails.htm";
                //Use RestTemplate to POST(within Asynctask)
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                //postforobject method POSTs data to server and brings back LoginForm object format data.
                String lf = restTemplate.postForObject(URL, l, String.class);
                return lf;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String lf) {
            if (lf!=null)
            {
                msgReply=lf;

            }
            else
            {
              msgReply="Disconnect";

            }

        }
    }
    private class HttpRequestTask2 extends AsyncTask<Void, Void,String> {
        // String a=null;
        String la=null;
        @Override
        public  String doInBackground(Void... params) {
            try {
                //The link on which we have to POST data and in return it will return some data
                //String URL = "https://192.168.0.104:8081/Moaddi1/operator/serviesforoperatorLogin.htm";
                String URL = "https://www.moaddi.com/moaddi/operator/serviesoperatorlocknewSerialnumber";
                LocksecreateCode lsc=new LocksecreateCode();
                lsc.setLockSnoNew(serialNumber);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                la = restTemplate.postForObject(URL,lsc,String.class);
                return la;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String m) {
            if (m!=null)
            {
                try {
                    JSONObject l =new JSONObject(m);
                    String st=l.getString("lockSno");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msgReply=m;
            }
           else
            {
                msgReply="";
            }
        }
    }
    private class HttpRequestTask3 extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                //The link on which we have to POST data and in return it will return some data
                 String URL = "http://192.168.0.109:8080/Moaddi1/operator/serviesoperatorlocksetupdetails.htm";
               // String URL = "https://www.moaddi.com/moaddi/operator/serviesoperatorlocksetupdetails.htm";
                //Use RestTemplate to POST(within Asynctask)
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                //postforobject method POSTs data to server and brings back LoginForm object format data.
                String lf = restTemplate.postForObject(URL, lsd, String.class);
                return lf;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String lf) {
            Toast.makeText(getActivity(),lf,Toast.LENGTH_SHORT).show();
        }
    }
}
