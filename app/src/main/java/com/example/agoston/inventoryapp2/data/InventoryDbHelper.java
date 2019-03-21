package com.example.agoston.inventoryapp2.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import static com.example.agoston.inventoryapp2.data.InventoryContract.StockEntry.TABLE_NAME;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "shelter.db";

    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE =  "CREATE TABLE " +
                TABLE_NAME + "(" +
                InventoryContract.StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryContract.StockEntry.COLUMN_NAME + " TEXT NOT NULL," +
                InventoryContract.StockEntry.COLUMN_PRICE + " TEXT NOT NULL," +
                InventoryContract.StockEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                InventoryContract.StockEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                InventoryContract.StockEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL," +
                InventoryContract.StockEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL );";


        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}