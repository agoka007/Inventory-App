package com.example.agoston.inventoryapp2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agoston.inventoryapp2.data.InventoryContract;

import static android.content.ContentValues.TAG;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageButton buyImageButton = (ImageButton) view.findViewById(R.id.sale);

        final int productIdColumnIndex = cursor.getInt(cursor.getColumnIndex(InventoryContract.StockEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_QUANTITY);

        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        final int itemQuantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice);
        quantityTextView.setText(String.valueOf(itemQuantity));

        buyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri productUri = ContentUris.withAppendedId(InventoryContract.StockEntry.CONTENT_URI, productIdColumnIndex);
                adjustProductQuantity(context, productUri, itemQuantity);
            }
        });
    }

    private void adjustProductQuantity(Context context, Uri productUri, int currentQuantityInStock) {

        int newQuantityValue = (currentQuantityInStock >= 1) ? currentQuantityInStock - 1 : 0;

        if (currentQuantityInStock == 0) {
            Toast.makeText(context.getApplicationContext(), "out of stock", Toast.LENGTH_SHORT).show();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.StockEntry.COLUMN_QUANTITY, newQuantityValue);
        int numRowsUpdated = context.getContentResolver().update(productUri, contentValues, null, null);
        if (numRowsUpdated > 0) {
            Log.i(TAG, context.getString(R.string.editor_insert_item_successful));
        } else {
            Toast.makeText(context.getApplicationContext(), "No product in stock", Toast.LENGTH_SHORT).show();
            Log.e(TAG, context.getString(R.string.editor_update_item_failed));
        }
    }
}