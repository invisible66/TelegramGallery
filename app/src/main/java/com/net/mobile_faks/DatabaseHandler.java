package com.net.mobile_faks;

/**
 * Created by Ismail AY on 31.05.2017.
 */


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ConstFiles.Consts;
import FaxTypes.IncomingFaxType;

public class DatabaseHandler extends SQLiteOpenHelper {


    Consts consts;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "NETGSM.db";

    String CREATE_INCOMINGFAX_TABLE = "CREATE TABLE IF NOT EXISTS " + consts.getGelenFaksTableName() + " ("
            + consts.getGelenFaksIDCol() + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + consts.getGelenFaksGonderenCol() + " TEXT NOT NULL,"
            + consts.getGelenFaksGelenCol() + " TEXT NOT NULL,"
            + consts.getGelenFaksTarihCol() + " DATETIME NOT NULL,"
            + consts.getGelenFaksFaksSizeCol() + " INTEGER NOT NULL,"
            + consts.getGelenFaksDosyaUrlCol() + " TEXT,"
            + consts.getGelenFaksBakildiCol() + " INTEGER DEFAULT 0,"
            + consts.getGelenFaksDosyaYolCol() + " TEXT, "
            + "sildurum INTEGER DEFAULT 0)";

    public DatabaseHandler(Context context, Consts sb) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        consts = sb;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INCOMINGFAX_TABLE);
    }

    public Boolean CheckTable(String tablo) {
        Boolean sonuc = false;
        try {
            String a = "SELECT count(*) as x FROM sqlite_master WHERE type='table' AND name='" + tablo + "';";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(a, null);

            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {
                    sonuc = true;
                } else {
                    sonuc = false;
                }
            } else {
                sonuc = false;
            }
        } catch (Exception e) {

        }
        return sonuc;
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //// Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + consts.getGelenFaksTableName());

        //// Create tables again
        //onCreate(db);
    }

    // Adding new fax
    public void addFax(IncomingFaxType faxFile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(consts.getGelenFaksGonderenCol(), faxFile.getSender());
        values.put(consts.getGelenFaksGelenCol(), faxFile.getReceipent());
        values.put(consts.getGelenFaksDosyaUrlCol(), faxFile.getFileUrl());
        values.put(consts.getGelenFaksFaksSizeCol(), faxFile.getSize());
        values.put(consts.getGelenFaksTarihCol(), faxFile.getDate());

        // Inserting Row
        db.insert(consts.getGelenFaksTableName(), null, values);
        db.close(); // Closing database connection
    }

    public IncomingFaxType checkUserFaxFile(String Gonderen) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(consts.getGelenFaksTableName(), new String[]{consts.getGelenFaksIDCol(),
                        consts.getGelenFaksGonderenCol(),
                        consts.getGelenFaksGelenCol(),
                        consts.getGelenFaksDosyaUrlCol(),
                        consts.getGelenFaksFaksSizeCol(),
                        consts.getGelenFaksTarihCol(),
                        consts.getGelenFaksDosyaYolCol(),
                        consts.getGelenFaksSildurumCol(),
                        consts.getGelenFaksBakildiCol()},
                consts.getGelenFaksGonderenCol() + "  LIKE ?",
                new String[]{"%"+Gonderen}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        IncomingFaxType faxType = null;
        if (cursor.getCount() > 0) {
            faxType = new IncomingFaxType();
            faxType.setID(cursor.getInt(0));
            faxType.setSender(cursor.getString(1));
            faxType.setReceipent(cursor.getString(2));
            faxType.setFileUrl(cursor.getString(3));
            faxType.setSize(cursor.getString(4));
            faxType.setDate(cursor.getString(5));
            faxType.setFilePath(cursor.getString(6));
            faxType.setDeleteStatus(cursor.getInt(7));
            faxType.setSeen(cursor.getInt(8));
        }

        // return fax
        return faxType;
    }

    // Getting single fax
    public IncomingFaxType getFaxFile(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(consts.getGelenFaksTableName(), new String[]{consts.getGelenFaksIDCol(),
                        consts.getGelenFaksGonderenCol(),
                        consts.getGelenFaksGelenCol(),
                        consts.getGelenFaksDosyaUrlCol(),
                        consts.getGelenFaksFaksSizeCol(),
                        consts.getGelenFaksTarihCol()},
                consts.getGelenFaksIDCol() + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        IncomingFaxType faxType = new IncomingFaxType();
        faxType.setID(cursor.getInt(0));
        faxType.setSender(cursor.getString(1));
        faxType.setReceipent(cursor.getString(2));
        faxType.setFileUrl(cursor.getString(3));
        faxType.setSize(cursor.getString(4));
        faxType.setDate(cursor.getString(5));

        // return fax
        return faxType;
    }

    // Getting All Faxes
    public List<IncomingFaxType> getAllFaxes() {
        List<IncomingFaxType> faxList = new ArrayList<IncomingFaxType>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + consts.getGelenFaksTableName();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                IncomingFaxType faxType = new IncomingFaxType();
                faxType.setID(cursor.getInt(0));
                faxType.setSender(cursor.getString(1));
                faxType.setReceipent(cursor.getString(2));
                faxType.setFileUrl(cursor.getString(3));
                faxType.setSize(cursor.getString(4));
                faxType.setDate(cursor.getString(5));
                // Adding fax to list
                faxList.add(faxType);
            } while (cursor.moveToNext());
        }

        // return fax list
        return faxList;
    }

    // Updating single fax
    public int updateFax(IncomingFaxType faxFile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(consts.getGelenFaksGonderenCol(), faxFile.getSender());
        values.put(consts.getGelenFaksGelenCol(), faxFile.getReceipent());
        values.put(consts.getGelenFaksDosyaUrlCol(), faxFile.getFileUrl());
        values.put(consts.getGelenFaksFaksSizeCol(), faxFile.getSize());
        values.put(consts.getGelenFaksTarihCol(), faxFile.getDate());
        values.put(consts.getGelenFaksDosyaYolCol(), faxFile.getFilePath());

        // updating row
        return db.update(consts.getGelenFaksTableName(), values, consts.getGelenFaksIDCol() + " = ?",
                new String[]{String.valueOf(faxFile.getID())});
    }

    // Deleting single fax
    public int deleteFax(IncomingFaxType faxFile) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(consts.getGelenFaksSildurumCol(), faxFile.getDeleteStatus());

        // updating row
        return db.update(consts.getGelenFaksTableName(), values, consts.getGelenFaksIDCol() + " = ?", new String[]{String.valueOf(faxFile.getID())});
    }

    public int readFax(IncomingFaxType faxFile) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(consts.getGelenFaksBakildiCol(), faxFile.getSeen());

        // updating row
        return db.update(consts.getGelenFaksTableName(), values, consts.getGelenFaksIDCol() + " = ?", new String[]{String.valueOf(faxFile.getID())});
    }


    // Getting faxes Count
    public int getFaxesCount() {
        String countQuery = "SELECT  * FROM " + consts.getGelenFaksTableName();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return faxes
        return cursor.getCount();
    }

}