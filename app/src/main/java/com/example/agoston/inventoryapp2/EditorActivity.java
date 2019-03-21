package com.example.agoston.inventoryapp2;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.agoston.inventoryapp2.data.InventoryDbHelper;
import com.example.agoston.inventoryapp2.data.InventoryContract;
import com.example.agoston.inventoryapp2.data.InventoryItem;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getCanonicalName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private InventoryDbHelper dbHelper;
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentInventoryUri;
    EditText nameEdit;
    EditText priceEdit;
    EditText quantityEdit;
    EditText supplierNameEdit;
    EditText supplierPhoneEdit;
    EditText supplierEmailEdit;
    long currentItemId;
    ImageButton decreaseQuantity;
    ImageButton increaseQuantity;
    Button imageBtn;
    ImageView imageView;
    Uri actualUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean infoItemHasChanged = false;
    private boolean itemHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemHasChanged = true;
            return false;
        }
    };
    boolean hasAllRequiredValues = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        final Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        if (mCurrentInventoryUri == null) {

            setTitle(getString(R.string.new_product));

            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_item));

            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        nameEdit = (EditText) findViewById(R.id.product_name_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);
        quantityEdit = (EditText) findViewById(R.id.quantity_edit);
        supplierNameEdit = (EditText) findViewById(R.id.supplier_name_edit);
        supplierPhoneEdit = (EditText) findViewById(R.id.supplier_phone_edit);
        supplierEmailEdit = (EditText) findViewById(R.id.supplier_email_edit);
        decreaseQuantity = (ImageButton) findViewById(R.id.decrease_quantity);
        increaseQuantity = (ImageButton) findViewById(R.id.increase_quantity);

        nameEdit.setOnTouchListener(mTouchListener);
        priceEdit.setOnTouchListener(mTouchListener);
        quantityEdit.setOnTouchListener(mTouchListener);
        supplierNameEdit.setOnTouchListener(mTouchListener);
        supplierPhoneEdit.setOnTouchListener(mTouchListener);
        supplierEmailEdit.setOnTouchListener(mTouchListener);
        decreaseQuantity.setOnTouchListener(mTouchListener);
        increaseQuantity.setOnTouchListener(mTouchListener);

        dbHelper = new InventoryDbHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumOneToQuantity();
                infoItemHasChanged = true;
            }
        });

    }

    private void subtractOneToQuantity() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            quantityEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    private void sumOneToQuantity() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityEdit.setText(String.valueOf(previousValue + 1));
    }


    private boolean saveInventory() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEdit.getText().toString().trim();
        String priceString = priceEdit.getText().toString().trim();
        String quantityString = quantityEdit.getText().toString().trim();
        int quantity;
        String supplierNameString = supplierNameEdit.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEdit.getText().toString().trim();
        String supplierEmailString = supplierEmailEdit.getText().toString().trim();

        if (mCurrentInventoryUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierEmailString)) {

            hasAllRequiredValues = true;
            return hasAllRequiredValues;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.validation_msg_product_name), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(InventoryContract.StockEntry.COLUMN_NAME, nameString);
        }

        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.validation_msg_product_quantity), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {

            quantity = Integer.parseInt(quantityString);
            values.put(InventoryContract.StockEntry.COLUMN_QUANTITY, quantity);
        }

        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.validation_msg_product_price), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(InventoryContract.StockEntry.COLUMN_PRICE, priceString);
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.validation_msg_supplier_name), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(InventoryContract.StockEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        }

        if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, getString(R.string.validation_msg_supplier_phone), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(InventoryContract.StockEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        }

        if (TextUtils.isEmpty(supplierEmailString)) {
            Toast.makeText(this, getString(R.string.validation_msg_supplier_email), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(InventoryContract.StockEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);
        }

        if (mCurrentInventoryUri == null) {

            Uri newUri = getContentResolver().insert(InventoryContract.StockEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        hasAllRequiredValues = true;
        return hasAllRequiredValues;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveInventory();
                if (hasAllRequiredValues == true) {
                    // Exit activity
                    finish();
                }
                return true;
            case R.id.action_delete_item:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_order:
                orderMore();
                return true;
            case android.R.id.home:
                if (!itemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!itemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.StockEntry._ID,
                InventoryContract.StockEntry.COLUMN_NAME,
                InventoryContract.StockEntry.COLUMN_PRICE,
                InventoryContract.StockEntry.COLUMN_QUANTITY,
                InventoryContract.StockEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                InventoryContract.StockEntry.COLUMN_SUPPLIER_EMAIL};


        return new CursorLoader(this,
                mCurrentInventoryUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(InventoryContract.StockEntry.COLUMN_SUPPLIER_EMAIL);

            String name = cursor.getString(nameColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            nameEdit.setText(name);
            priceEdit.setText(Integer.toString(price));
            quantityEdit.setText(Integer.toString(quantity));
            supplierNameEdit.setText(supplierName);
            supplierPhoneEdit.setText(supplierPhone);
            supplierEmailEdit.setText(supplierEmail);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEdit.setText("");
        priceEdit.setText("");
        quantityEdit.setText("");
        supplierNameEdit.setText("");
        supplierPhoneEdit.setText("");
        supplierEmailEdit.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }

    public void orderMore() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + supplierPhoneEdit.getText().toString().trim()));
        startActivity(intent);
    }

    private void deleteInventory() {
        if (mCurrentInventoryUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}