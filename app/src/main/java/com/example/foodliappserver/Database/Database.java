package com.example.foodliappserver.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.foodliappserver.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "foodsdeliveryDB.db";
    private static final int DB_VER=1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCart(){
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ID","ProductName", "ProductID", "Quantity", "Price", "Discount", "Image"};
        String sqlTable = "OrderDetail";

        queryBuilder.setTables(sqlTable);
        Cursor cursor = queryBuilder.query(database, sqlSelect,null,null,null,null,null);
        final List<Order> result = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                result.add(new Order(

                        cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("ProductID")),
                        cursor.getString(cursor.getColumnIndex("ProductName")),
                       cursor.getString(cursor.getColumnIndex("Quantity")),
                       cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Discount")),
                        cursor.getString(cursor.getColumnIndex("Image"))
            ));
            }while (cursor.moveToNext());
        }
        return result;

    }
    public void addToCart(Order order){
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductID,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        database.execSQL(query);
    }
    public void cleanCart(){
        SQLiteDatabase database = getReadableDatabase();
        String query = "DELETE FROM OrderDetail";
        database.execSQL(query);
    }
    // Favorites
    public void addToFavorites(String foodId, String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(foodId, userPhone) VALUES('%s', '%s')", foodId,userPhone);
        db.execSQL(query);
    }
    public void deleteFromFavorites(String foodId, String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE foodId='%s' and userPhone = '%s'", foodId, userPhone);
        db.execSQL(query);
    }
    public boolean isFavorites(String foodId, String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE foodId='%s' and userPhone='%s'", foodId, userPhone);
       Cursor cursor = db.rawQuery(query,null);
       if (cursor.getCount() <= 0){
           cursor.close();
           return false;
       }
       cursor.close();
       return true;
    }
    public int getCount(){
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity='%s' WHERE ID = '%d' ", order.getQuantity(),order.getID());
        db.execSQL(query);
    }
}
