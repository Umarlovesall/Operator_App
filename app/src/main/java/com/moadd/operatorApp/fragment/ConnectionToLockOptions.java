package com.moadd.operatorApp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import com.google.zxing.client.android.CaptureActivity;
import com.moadd.operatorApp.BarcodeResultSend;
import com.moadd.operatorApp.GetDateAndTime;
import com.moadd.operatorApp.LockBelongsToOperatorOrNot;
import com.moadd.operatorApp.LocksecreateCode;
import com.moadd.operatorApp.Login;
import com.moadd.operatorApp.wifiHotSpots;
import com.moaddi.operatorApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

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
        sp = getActivity().getSharedPreferences("Credentials", MODE_PRIVATE);
        transfer =  getActivity().getSharedPreferences("Setup", MODE_PRIVATE);
        hotutil = new wifiHotSpots(getActivity());
        hotutil.startHotSpot(true);
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!barcode.getText().toString().trim().equals("")) {
                    new HttpRequestTask().execute();
                } else {
                    Toast.makeText(getActivity(), "Enter Some Barcode First", Toast.LENGTH_LONG).show();
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
                ap.setUserRoleId("13");
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
                    if (messageFromClient.equals("YES")) {
                        p2.setBackgroundColor(Color.parseColor("#008000"));
                        if (typeOfData == 1) {
                          //  msgReply = GetDateAndTime.timeStamp() + "#" + "Image Encoded String" + "#"+sp.getString("userId","")+"*"+sp.getString("appId","")+"#" + transfer.getString("OpHotspot", null) + "*" + transfer.getString("OpHotpassword", null) + "*" + transfer.getString("OpPwNeeded", null) + "#" + transfer.getString("SuHotspot", null) + "*" + transfer.getString("SuHotpassword", null + "*" + transfer.getString("SuPwNeeded", null)) + "#" + "91su302*945693885#";
                            msgReply = "Image Encoded String";
                        } else if (typeOfData == 2) {
                            msgReply = "RESET";
                        }
                    }
                    else if (messageFromClient.equals("FAIL1")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "Disconnect";
                        Toast.makeText(getActivity(), "Problem saving barcode data", Toast.LENGTH_LONG).show();
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL2")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "Disconnect";
                        Toast.makeText(getActivity(), "Problem saving date and time data", Toast.LENGTH_LONG).show();
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL3")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "Disconnect";
                        Toast.makeText(getActivity(), "Problem saving Operator IDs data", Toast.LENGTH_LONG).show();
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL4")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "Disconnect";
                        Toast.makeText(getActivity(), "Problem saving Operator Hotspot Details", Toast.LENGTH_LONG).show();
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL5")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "Disconnect";
                        Toast.makeText(getActivity(), "Problem saving Supplier Hotspot Details", Toast.LENGTH_LONG).show();
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("FAIL6")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "Disconnect";
                        Toast.makeText(getActivity(), "Problem saving Connected Supplier Details", Toast.LENGTH_LONG).show();
                        //Here send all that data to website too
                    }
                    else if (messageFromClient.equals("SUCCESS1")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = GetDateAndTime.timeStamp();
                    }
                    else if (messageFromClient.equals("SUCCESS2")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply =sp.getString("userId","")+"*"+sp.getString("appId","") ;
                    }
                    else if (messageFromClient.equals("SUCCESS3")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = transfer.getString("OpHotspot", null) + "*" + transfer.getString("OpHotpassword", null) + "*" + transfer.getString("OpPwNeeded", null)  ;
                    }
                    else if (messageFromClient.equals("SUCCESS4")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply =transfer.getString("SuHotspot", null) + "*" + transfer.getString("SuHotpassword", null) + "*" + transfer.getString("SuPwNeeded", null);
                    }
                    else if (messageFromClient.equals("SUCCESS5")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "123456*654321#983938*839389#788888*888887" ;
                    }
                    else if (messageFromClient.equals("SUCCESS6")) {
                        p3.setBackgroundColor(Color.parseColor("#008000"));
                        msgReply = "Disconnect";
                        Toast.makeText(getActivity(), "Successfull Data Transfer Complete", Toast.LENGTH_LONG).show();
                        //Here send all that data to website too
                    } else if (messageFromClient.equals("NO")) {
                        p2.setBackgroundColor(Color.parseColor("#FF0000"));
                        msgReply = "Disconnect";
                        Toast.makeText(getActivity(), "Secret Numbers don't match", Toast.LENGTH_LONG).show();
                        //Here send all that data to website too
                    }
                        //Toast.makeText(getActivity(), "Error in connection",Toast.LENGTH_LONG).show();
                        //Send serial number to the website and based on response(Barcode image and barcode setup status of the lock),send details to the lock
                       //else if (messageFromClient.equals(al.get(al.size()-1)))
                    else if(messageFromClient.equals("1234567890"))
                        {
                           /* l.setLockSnoNew(messageFromClient);
                            new HttpRequestTask1().execute();
                            p1.setBackgroundColor(Color.parseColor("#008000"));*/
                            msgReply = "9839386601";
                            p1.setBackgroundColor(Color.parseColor("#008000"));
                        }
                        else
                    {
                        p1.setBackgroundColor(Color.parseColor("#FF0000"));
                        msgReply="Disconnect";
                        Toast.makeText(getActivity(), "Unexpected response from the lock! Try again", Toast.LENGTH_LONG).show();
                    }

                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n"
                            + "Message from client: " + messageFromClient + "\n";

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
}
