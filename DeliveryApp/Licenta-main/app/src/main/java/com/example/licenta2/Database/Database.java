package com.example.licenta2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licenta2.Model.Item;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    /**
     * Constantele se scriu cu litere mari si underscore
     */
    private static final String DATABASE_NAME = "restaurantDB.db";
    private static final int DB_VER = 1;

    public static final String TABLE = "OrderDetail";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DB_VER);
    }

    public List<Item> getCarts() {
        Cursor c = getOrderDetailCursor(null, null);

        final List<Item> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Item(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("FirebaseRef"))
                ));
            } while(c.moveToNext());
        }
        return result;
    }

    /**
     * Acum teoretic este insert or update
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void addToCart(Item item) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        query.setTables(TABLE);

        ContentValues content = new ContentValues();
        content.put("ProductId", item.getProductId());
        content.put("ProductName", item.getProductName());
        content.put("Price", item.getPrice());
        content.put("FirebaseRef", item.getFirebaseRef());

        Cursor itemPresentCursor = getOrderDetailCursor("ProductId = ?", new String[]{item.getProductId()});
        if (itemPresentCursor.moveToFirst()) {
            String newQuantity = String.valueOf(Integer.parseInt(item.getQuantity()) + Integer.parseInt(itemPresentCursor.getString(itemPresentCursor.getColumnIndex("Quantity"))));

            content.put("Quantity", newQuantity);
            query.update(db, content, "ProductId = ?", new String[]{item.getProductId()});
        } else {
            content.put("Quantity", item.getQuantity());
            query.insert(db, content);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void removeOrderItem(String name) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        query.setTables(TABLE);

        query.delete(db, "ProductName = ?", new String[]{ name });
    }

    public void clearCart() {
        SQLiteDatabase db = getReadableDatabase();

        db.execSQL("delete from " + TABLE);
    }

    /**
    * Vreau sa folosesc bucata asta si la add ca sa vad daca exista deja
    * Argumentele sunt pentru query
    */
    private Cursor getOrderDetailCursor(final String selection, final String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        query.setTables(TABLE);

        String[] sqlSelect = {"ProductId", "ProductName", "Quantity", "Price", "FirebaseRef"};

        return query.query(db, sqlSelect, selection, selectionArgs, null, null, null);
    }

}
