package com.whc.winnernumber.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WinnerDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "winnerNDB";
    private static final int DB_VERSION = 1;


    private static final String TABLE_CREATE =
            "CREATE TABLE Type ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
                    "name TEXT, image INTEGER ) ;";
    private static final String TABLE_CARRIER =
            "CREATE TABLE CARRIER ( id INTEGER PRIMARY KEY AUTOINCREMENT, CARNUL TEXT NOT NULL," +
                    "PASSWORD TEXT) ;";

    private static final String TABLE_PRICE =
            "CREATE TABLE PRICE ( invoYm TEXT PRIMARY KEY,superPrizeNo TEXT,spcPrizeNo TEXT,firstPrizeNo1 TEXT," +
                    "firstPrizeNo2 TEXT, firstPrizeNo3 TEXT,sixthPrizeNo1 TEXT, sixthPrizeNo2 TEXT, sixthPrizeNo3 TEXT, superPrizeAmt TEXT, spcPrizeAmt TEXT, firstPrizeAmt TEXT ,secondPrizeAmt TEXT, thirdPrizeAmt TEXT, fourthPrizeAmt TEXT, " +
                    "fifthPrizeAmt TEXT, sixthPrizeAmt TEXT, sixthPrizeNo4 TEXT, sixthPrizeNo5 TEXT, sixthPrizeNo6 TEXT);";

    private static final String TABLE_HERATYTEAM =
            "CREATE TABLE HEARTYTEAM ( id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT NOT NULL," +
                    "NUMBER TEXT) ;";

    private static final String TYPE_DETAIL=" CREATE TABLE TypeDetail ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
            "name TEXT, image INTEGER , keyword text); ";

    private static final String TYPE_COMSUMER=" CREATE TABLE Consumer ( id INTEGER PRIMARY KEY AUTOINCREMENT, maintype TEXT NOT NULL," +
            "secondtype TEXT, money INTEGER, date Date , number TEXT , fixdate TEXT , fixdatedetail TEXT ,notify TEXT, detailname TEXT, iswin TEXT" +
            ",auto text,autoId INTEGER, isWinNul TEXT);";

    private static final String TYPE_BANK=" CREATE TABLE BANK ( id INTEGER PRIMARY KEY AUTOINCREMENT, maintype TEXT NOT NULL," +
            "money INTEGER, date Date , fixdate TEXT , fixdatedetail TEXT , detailname TEXT ,auto text,autoId INTEGER);";

    private static final String TABLE_BANK_TYPE =
            "CREATE TABLE BANKTYPE ( id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber TEXT NOT NULL," +
                    "name TEXT, image INTEGER ) ;";

    private static final String TABLE_INVOICE =
            "CREATE TABLE INVOICE ( id INTEGER PRIMARY KEY AUTOINCREMENT, invNum TEXT NOT NULL," +
                    "cardType TEXT, cardNo TEXT, cardEncrypt TEXT, time DATETIME, amount INTEGER, detail TEXT, sellerName TEXT, invDonatable TEXT , donateMark TEXT, carrier TEXT, maintype TEXT, secondtype TEXT ,heartyteam TEXT,donateTime DATETIME,isWin TEXT,sellerBan TEXT, sellerAddress TEXT, isWinNul TEXT);";

    private static final String TABLE_GOAL =
            "CREATE TABLE Goal ( id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT NOT NULL," +
                    "name TEXT, money INTEGER , timeStatue text, startTime DATETIME ,endTime DATETIME, notify TEXT , notifyStatue TEXT , notifyDate TEXT , noWeekend TEXT ,statue INTEGER) ;";


    private static final String TABLE_ElePeriod =
            "CREATE TABLE ElePeriod ( id INTEGER PRIMARY KEY AUTOINCREMENT, CARNUL TEXT," +
                    "year INTEGER,month INTEGER, download TEXT) ;";

    public WinnerDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_PRICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "PRICE");
        onCreate(db);
    }

}
