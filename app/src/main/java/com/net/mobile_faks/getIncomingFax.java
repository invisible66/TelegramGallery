package com.net.mobile_faks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import ConstFiles.Consts;
import FaxTypes.IncomingFaxType;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Ismail AY on 29.05.2017.
 */

public class getIncomingFax extends AsyncTask<String, Void, Void> {

    Consts consts = Consts.getInstance();
    SyncHttpClient client = new SyncHttpClient();

    Context delegate;

    String xml = "";

    public getIncomingFax(int page, Context context) {
        delegate = context;
        xml = consts.getJSONConsts().getIncomingFaxXML();
        xml = xml.replace(":subscriber", consts.getUserInfo().getSubscriber());
        xml = xml.replace(":password", consts.getUserInfo().getPassword());
        System.out.println(xml);

//        json = consts.getJSONConsts().getIncomingFaxJson();
//        String header = consts.getJSONConsts().getHeaderJson();
//        header = header.replace(":subscriber", consts.getUserInfo().getSubscriber());
//        header = header.replace(":notification", consts.getNotificationID());
//        header = header.replace(":tokenid", consts.getUserInfo().getTokenID());
//
//        json = json.replace(":header", header);
//        json = json.replace(":page", page + "");

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    private void GeleniIsle(String GelenFaxlar) {
        try {
            Log.i("****",GelenFaxlar);
            if (!GelenFaxlar.equals("40")) {
                String[] Faxlar = GelenFaxlar.split("<br>");
                for (int i = 0; i < Faxlar.length - 1; i++) {
                    String[] Bilgi = Faxlar[i].split("|");
                    if (Bilgi.length > 0) {
                        IncomingFaxType faxType = new IncomingFaxType();
                        faxType.setID(Integer.parseInt(Bilgi[0]));

                        consts.getDb().addFax(faxType);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            StringEntity entity = new StringEntity(xml);

            AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    Log.i("****", "OnStart");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    consts.HataGizle();
                    Log.i("****", "OnFinish");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i("****", "OnSuccess");
                    GeleniIsle(new String(responseBody));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("****", "OnFailure response:" + new String(responseBody));
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    Log.i("****", "OnProgress");
                }
            };
            responseHandler.setUseSynchronousMode(true);
            client.post(delegate, consts.getUrlConsts().getIncomingFax(), entity, "application/xml", responseHandler);
        } catch (Exception e) {
            Log.i("****", "Exception :" + e.getMessage());
        }

        return null;
    }
}
