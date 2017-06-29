package com.net.mobile_faks;

import android.content.Context;
import android.database.Cursor;

import ConstFiles.Consts;
import io.requery.android.database.DatabaseErrorHandler;
import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ismail AY on 30.05.2017.
 */

public class DatabaseHandler2 extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NETGSM";

    SQLiteDatabase DbX;
    Consts consts;

    public DatabaseHandler2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private Boolean CreateDB() {
        Boolean Sonuc = false;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + consts.getGelenFaksTableName() + " ("
                    + consts.getGelenFaksIDCol() + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + consts.getGelenFaksGonderenCol() + " TEXT NOT NULL,"
                    + consts.getGelenFaksGelenCol() + " TEXT NOT NULL,"
                    + consts.getGelenFaksTarihCol() + " DATETIME NOT NULL,"
                    + consts.getGelenFaksFaksSizeCol() + " INTEGER NOT NULL,"
                    + consts.getGelenFaksDosyaUrlCol() + " TEXT,"
                    + consts.getGelenFaksBakildiCol() + " INTEGER DEFAULT 0,"
                    + consts.getGelenFaksDosyaYolCol() + " TEXT, "
                    + "sildurum INTEGER DEFAULT 0)";
            DbX.execSQL(sql);
            Sonuc = true;
        } catch (Exception e) {
            Sonuc = false;
        }
        return Sonuc;
    }

    public Boolean CheckDB() {
        Boolean tip = false;
        try {
            //DbX = SQLiteDatabase.openDatabase(DATABASE_NAME, null, 0);

            if (DbX != null && CreateDB() && checkUser()) {
                tip = true;
            } else {
                tip = false;
            }
        } catch (Exception e) {
            tip = false;
        }
        return tip;
    }

    private Boolean checkUser() { // bu kullanıcı için db de kayıt varmı? yoksa tüm fakslar çekilmeli.
        Boolean Sonuc = false;
        String selectQuery = "SELECT " + consts.getGelenFaksIDCol() + " FROM " + consts.getGelenFaksTableName() + " WHERE " + consts.getGelenFaksGelenCol() + "=?";
        Cursor c = DbX.rawQuery(selectQuery, new String[]{consts.getUserInfo().getSubscriber()});
        if (c.moveToFirst()) {
            Sonuc = true;
        }
        c.close();
        return Sonuc;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DbX = db;
        this.CheckDB();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
