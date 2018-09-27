package com.whc.winnernumber.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WinnerDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "winnerNDB";
    private static final int DB_VERSION = 1;



    private static final String TABLE_PRICE =
            "CREATE TABLE PRICE ( invoYm TEXT PRIMARY KEY,superPrizeNo TEXT,spcPrizeNo TEXT,firstPrizeNo1 TEXT," +
                    "firstPrizeNo2 TEXT, firstPrizeNo3 TEXT,sixthPrizeNo1 TEXT, sixthPrizeNo2 TEXT, sixthPrizeNo3 TEXT, superPrizeAmt TEXT, spcPrizeAmt TEXT, firstPrizeAmt TEXT ,secondPrizeAmt TEXT, thirdPrizeAmt TEXT, fourthPrizeAmt TEXT, " +
                    "fifthPrizeAmt TEXT, sixthPrizeAmt TEXT, sixthPrizeNo4 TEXT, sixthPrizeNo5 TEXT, sixthPrizeNo6 TEXT);";


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
