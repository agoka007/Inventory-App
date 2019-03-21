package com.example.agoston.inventoryapp2;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agoston.inventoryapp2.data.InventoryContract;
import com.example.agoston.inventoryapp2.data.InventoryDbHelper;
import com.example.agoston.inventoryapp2.data.InventoryItem;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView inventoryListView = (ListView) findViewById(R.id.list_view);

        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentInventoryUri = ContentUris.withAppendedId(InventoryContract.StockEntry.CONTENT_URI, id);
                intent.setData(currentInventoryUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    private void insertInventory() {

        ContentValues values = new ContentValues();
        values.put(InventoryContract.StockEntry.COLUMN_NAME, "Speaker");
        values.put(InventoryContract.StockEntry.COLUMN_PRICE, 7);
        values.put(InventoryContract.StockEntry.COLUMN_QUANTITY, 5);
        values.put(InventoryContract.StockEntry.COLUMN_SUPPLIER_NAME, "Audio");
        values.put(InventoryContract.StockEntry.COLUMN_SUPPLIER_PHONE, "055465466");
        values.put(InventoryContract.StockEntry.COLUMN_SUPPLIER_EMAIL, "order@audio.com");

        Uri newUri = getContentResolver().insert(InventoryContract.StockEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                insertInventory();
                return true;
            case R.id.action_delete_all_data:
                deleteAllItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.StockEntry._ID,
                InventoryContract.StockEntry.COLUMN_NAME,
                InventoryContract.StockEntry.COLUMN_QUANTITY,
                InventoryContract.StockEntry.COLUMN_PRICE,
                InventoryContract.StockEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                InventoryContract.StockEntry.COLUMN_SUPPLIER_EMAIL
        };

        return new CursorLoader(this,
                InventoryContract.StockEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    private void deleteAllItem() {
        int rowsDeleted = getContentResolver().delete(InventoryContract.StockEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from product database");
    }
}