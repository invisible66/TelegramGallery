package com.net.mobile_faks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import ConstFiles.Consts;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends AppCompatActivity {

    Context delegate;
    SharedPreferences prefs;
    String UserName, Password;
    Consts consts = Consts.getInstance();

    @BindView(R.id.input_username)
    TextView txtUserName;
    @BindView(R.id.input_password)
    TextView txtPassWord;

    String TokenID = "";

    @BindView(R.id.chk_remember)
    CheckBox RememberMe;

    AsyncHttpClient client = new AsyncHttpClient();

    @BindView(R.id.tool_bar)
    Toolbar toolbarX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        delegate = this;
        ButterKnife.bind(this);

        //toolbar_main = (Toolbar) findViewById(R.id.tool_bar);
        toolbarX.setTitle("Netgsm Faks " + consts.getAppVersion());
        toolbarX.setTitleTextColor(ContextCompat.getColor(delegate, R.color.white));
        setSupportActionBar(toolbarX);

        prefs = this.getSharedPreferences("com.net.mobile_faks", Context.MODE_PRIVATE);
        UserName = prefs.getString("UserName", null);
        Password = prefs.getString("Password", null);

        if (UserName != null)
            if (UserName.length() == 0)
                UserName = txtUserName.getText().toString();
            else
                txtUserName.setText(UserName);

        if (Password != null)
            if (Password.length() == 0)
                Password = txtPassWord.getText().toString();
            else
                txtPassWord.setText(Password);

        if ((UserName != null && Password != null)) {
            try {
                LoginOlmusGidiyorsun(); //-- zamanı gelince açılır
            } catch (Exception e) {
                consts.HataGizle();
                consts.HataVer(delegate, "Hata", "onCreate LoginOlmusGidiyorsunHata:" + e.getMessage(), consts.ALERT_ERROR);
            }
        }
        PhoneInfo();
        TokenID = prefs.getString("tokenid", null);
        consts.setNotificationID(TokenID);

        if (client != null)
            consts.setClient(client);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) { //-- Ekrandaki klavyeyi gizlemek için
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideSoftKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void PhoneInfo() {
        try {
            consts.setOSVersion(Build.VERSION.RELEASE);
            consts.setMarka(Build.BRAND);  // "Sony"
            consts.setModel(Build.MODEL);  // "Xperia Z"
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.btn_login)
    public void LoginOl() {
        try {
            if (checkTextFields()) {
                LoginOlmusGidiyorsun();
            }
        } catch (Exception e) {
            consts.HataGizle();
            consts.HataVer(delegate, "Hata", "LoginOlHata:" + e.getMessage(), consts.ALERT_ERROR);
        }
    }

    private boolean checkTextFields() {
        boolean Sonuc = false;
        try {
            UserName = txtUserName.getText().toString().trim();
            Password = txtPassWord.getText().toString().trim();

            if (UserName.length() < 2 || UserName.trim().isEmpty()) {
                /*new SweetAlertDialog(delegate, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Hata")
                        .setContentText("Lütfen Abone No / Kullanıcı adı bilgisin kontrol edin.")
                        .show();*/
                consts.HataVer(delegate, "Hata", "Lütfen Abone No / Kullanıcı adı bilgisin kontrol edin.", consts.getAlertError());
                return false;
            }

            if (Password.length() < 2 || Password.trim().isEmpty()) {
                /*new SweetAlertDialog(delegate, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Hata")
                        .setContentText("Lütfen parolanızı kontrol edin.")
                        .show();*/
                consts.HataVer(delegate, "Hata", "Lütfen Parolanızı Kontrol Ediniz.", consts.getAlertError());
                return false;
            }
            Sonuc = true;
        } catch (Exception e) {
            consts.HataVer(delegate, "Hata", "Hata:" + e.getMessage(), consts.getAlertError());
            Sonuc = false;
        }
        return Sonuc;
    }

    public void LoginOlmusGidiyorsun() throws Exception {
        consts.HataVer(delegate, "Giriş Yapılıyor..", "", consts.getAlertProgress());

        if (consts.getNotificationID() == null)
            consts.setNotificationID(prefs.getString("tokenid", null));

        if (consts.getNotificationID().length() == 0)
            consts.setNotificationID(prefs.getString("tokenid", null));

        if (RememberMe.isChecked()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UserName", UserName);
            editor.putString("Password", Password);
            editor.apply();
        }

        String jsonStr = consts.getJSONConsts().getLoginJson();
        jsonStr = jsonStr.replace(":username", UserName);
        jsonStr = jsonStr.replace(":password", Password);
        jsonStr = jsonStr.replace(":version", "v2.0");
        jsonStr = jsonStr.replace(":os", consts.getOSVersion());
        jsonStr = jsonStr.replace(":product", consts.getMarka());
        jsonStr = jsonStr.replace(":model", consts.getModel());
        jsonStr = jsonStr.replace(":program", consts.getProgram());
        jsonStr = jsonStr.replace(":notification", consts.getNotificationID());
        StringEntity entity = new StringEntity(jsonStr);

        client.post(delegate, consts.getUrlConsts().getLogin(), entity, "application/json", new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                super.onFinish();
                consts.HataGizle();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LoginSonuc(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                consts.HataGizle();
            }
        });
    }

    private void LoginSonuc(JSONObject json) {
        try {
            JSONObject response = json.getJSONObject("loginresp");
            JSONObject header = response.getJSONObject("header");
            int status = Integer.parseInt(header.getString("status"));
            String mesaj = header.getString("message");
            if (status == 0) {
                consts.HataVer(delegate, "Hata", mesaj, consts.ALERT_ERROR);
            } else {
                JSONObject body = response.getJSONObject("body");
                JSONObject userInfo = body.getJSONObject("userinformation");
                consts.getUserInfo().setName(userInfo.getString("name"));
                consts.getUserInfo().setSurname(userInfo.getString("surname"));
                consts.getUserInfo().setSubscriber(userInfo.getString("subscriber"));
                consts.getUserInfo().setPassword(Password); // ilk giriste yazılan şifre.
                consts.getUserInfo().setTokenID(userInfo.getString("tokenid"));
                consts.getUserInfo().setTokenTimeOut(userInfo.getInt("tokentimeout"));

                // db kontrol edilecek.veritabanı varsa ve veritabanında bu kullanıcıya ait kayıt varsa sadece okunmamıslar fakslar istenecek.

                int tip = (consts.CheckDB(delegate) ? 0:1);

                getIncomingFax gelen = new getIncomingFax(tip,delegate);
                gelen.execute();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        } catch (Exception e) {
            Log.e("****", "hata:" + e.getMessage());
        }
    }

    @OnClick(R.id.btn_passwordforgot)
    public void ForgotPassword() {
        String Mesaj = "Sisteme kayıtlı cep telefonunuzdan sifre yazıp 0850 303 0 303 e gönderin, abone numaranız ve yeni şifreniz cebinize gelsin.";
        consts.HataVer(delegate, "Şifre Hatırlatma", Mesaj, consts.getAlertNormal());
    }
}