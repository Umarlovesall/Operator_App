package com.moadd.operatorApp.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.moadd.operatorApp.BarcodeResultSend;
import com.moadd.operatorApp.LockBelongsToOperatorOrNot;
import com.moadd.operatorApp.Login;
import com.moaddi.operatorApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static com.moadd.operatorApp.MainActivity.CURRENT_TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SupplierList extends Fragment {
    ListView SupplierList,selectedSuppliers;
    public static ArrayList<String> listSupplier,selectedSuppliersList;
    ArrayAdapter<String> aa,selectedAA;
    public static String sup;
    Button proceed;
    SharedPreferences sp;
    SharedPreferences.Editor et;
    public SupplierList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_supplier_list, container, false);
        proceed= (Button) v.findViewById(R.id.proceed);
        sp=getActivity().getSharedPreferences("SelecetedSuppliers", Context.MODE_PRIVATE);
        et=sp.edit();
        SupplierList = (ListView) v.findViewById(R.id.lv);
        selectedSuppliers = (ListView) v.findViewById(R.id.lvSelected);
        selectedSuppliersList=new ArrayList<String>();
        listSupplier = new ArrayList<String>();
        aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listSupplier);
        selectedAA = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, selectedSuppliersList);
        SupplierList.setAdapter(aa);
        selectedSuppliers.setAdapter(selectedAA);
        new HttpRequestTask().execute();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSuppliersList.size()!=0) {
                    if (selectedSuppliersList.size() <= 3) {
                        SupplierSetup fragment = new SupplierSetup();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG).commit();
                    } else {
                        Toast.makeText(getActivity(), "Select Maximum 3 Suppliers Only", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Select at least 1 Suppliers", Toast.LENGTH_LONG).show();
                }
            }
        });
        SupplierList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String [] ar=listSupplier.get(position).split("  ");
                if (!selectedSuppliersList.contains(ar[1])) {
                    selectedSuppliersList.add(ar[1]);
                    et.putString("SelectedSuppliers", sp.getString("SelectedSuppliers", "") + "$" + ar[1]).apply();
                }
                selectedAA.notifyDataSetChanged();
            }
        });
        selectedSuppliers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSuppliersList.remove(position);
                selectedAA.notifyDataSetChanged();
            }
        });
        return v;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        // String a=null;
        String la = null;

        @Override
        public String doInBackground(Void... params) {
            try {
                //The link on which we have to POST data and in return it will return some data
                //(For all suppliers of the operator)String URL = "https://www.moaddi.com/moaddi/operator/serviesOperatorSupplierDetails.htm";
                //(For Suppliers having activated apps)String URL = "https://www.moaddi.com/moaddi/operator/serviesOperatorSupplierDetails1.htm";
                // String URL = "http://192.168.0.102:8080/Moaddi1/operator/serviesOperatorSupplierDetails1.htm";
                String URL = "https://www.moaddi.com/moaddi/operator/serviesOperatorSupplierDetails1.htm";
                //First Static then Dynamic afterwards
                //Sending static userRoleId as of now.i.e. "13" static which will later be changed to dynamic
                BarcodeResultSend b=new BarcodeResultSend();
                 //b.setUserRoleId("13");
                b.setUserRoleId(Login.userRoleId);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                la = restTemplate.postForObject(URL, b, String.class);
                return la;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String m) {
            if (m != null) {
                try {
                    JSONArray j = new JSONArray(m);
                    for (int i = 0; i < j.length(); i++) {
                        JSONObject l = j.getJSONObject(i);
                        listSupplier.add(l.getString("fullName") + "  " + l.getString("userId"));
                        //listSupplier.add(l.getString("userId"));
                    }
                    aa.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(getActivity(),"Check Internet Connection and Try again",Toast.LENGTH_LONG).show();
            }
        }
    }
}
