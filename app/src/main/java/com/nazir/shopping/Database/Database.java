package com.nazir.shopping.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Model.Favourites;
import com.nazir.shopping.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Database is used because it doesn't require internet connection
 * Its stored local in the app
 */

public class Database extends SQLiteAssetHelper {
    private static final String TAG = "Database";

    private static final String DB_NAME = "ShopingDB.db";
    private static final int DB_VER = 3;



    public Database(Context context) {
        super(context, DB_NAME, null,DB_VER);
    }

    public boolean checkHookahExists(String hookahId, String userPhone, String color){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        String SQLQuery = String.format("SELECT * FROM OrderDetail WHERE UserPhone='%s' AND ProductId='%s' AND Color='%s'", userPhone,hookahId, color);

        cursor = db.rawQuery(SQLQuery,null);

        if (cursor.getCount() > 0){
            flag = true;
        }else {
            flag = false;
        }
        cursor.close();
        return flag;
    }


    /**
     * This method searches through the database and gets the item fields based on the hookah added to the cart
     * @return
     */
    public List<Order> getCarts(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ProductName", "ProductId", "Quantity", "Price", "Discount", "Image", "Color"};
        String sqlTable = "OrderDetail";


        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Order> result = new ArrayList<>();

        if (c.moveToFirst()){


            do {
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image")),
                        c.getString(c.getColumnIndex("Color"))));
                    }while (c.moveToNext());
                }
                return result;

            }


    /**
     * This method gets the Hookah detail fields and inserts it into the local sqlite database
      * @param order
     */
    public void addToCart(Order order){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone,ProductId,ProductName,Quantity,Price,Discount, Image, Color) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage(),
                order.getColor());
                db.execSQL(query);



    }

    /**
     * Deletes all the data from the sqlite database
     */

    public void cleanCart(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'", userPhone);
        db.execSQL(query);



    }

    public void updateCart(Order order) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= '%s' WHERE UserPhone = '%s' AND ProductId='%s'", order.getQuantity(),order.getUserPhone(),order.getProductId());
        db.execSQL(query);


    }

    public void increaseCartItem(String userPhone, String hookahId, String color) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= Quantity+1 WHERE UserPhone = '%s' AND ProductId='%s' AND Color='%s'",userPhone,hookahId, color);
        db.execSQL(query);


    }

    //**************************************Cart Button Count*****************************

    public int getCountCart(String userPhone){

        int count = 0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail WHERE UserPhone='%s'",userPhone);

        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()){

            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());

        }
        return count;

    }

    //**************************************Favourites*****************************

    public void addToFavourites(Favourites fav){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favourites(HookahId,HookahName,HookahPrice,HookahMenuId,HookahImage,HookahDiscount,HookahDescription,UserPhone)" +
                "VALUES('%s', '%s','%s', '%s','%s', '%s','%s', '%s');",
                fav.getHookahId(),
                fav.getHookahName(),
                fav.getHookahPrice(),
                fav.getHookahMenuId(),
                fav.getHookahImage(),
                fav.getHookahDiscount(),
                fav.getHookahDescription(),
                fav.getUserPhone());



        db.execSQL(query);

    }

    public void removeFromFavourites(String hookahid, String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favourites WHERE HookahId='%s' and UserPhone='%s';",hookahid, userPhone);
        db.execSQL(query);

    }

    public boolean isFavourites(String hookahid,String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favourites WHERE HookahId='%s'and UserPhone='%s';",hookahid, userPhone);

        Cursor cursor = db.rawQuery(query,null);
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();

        return true;

    }

    public List<Favourites> getAllFavourites(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","HookahId","HookahName","HookahPrice","HookahMenuId","HookahImage","HookahDiscount","HookahDescription"};
        String sqlTable = "Favourites";


        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Favourites> result = new ArrayList<>();

        if (c.moveToFirst()){


            do {
                result.add(new Favourites(
                        c.getString(c.getColumnIndex("HookahId")),
                        c.getString(c.getColumnIndex("HookahName")),
                        c.getString(c.getColumnIndex("HookahPrice")),
                        c.getString(c.getColumnIndex("HookahMenuId")),
                        c.getString(c.getColumnIndex("HookahImage")),
                        c.getString(c.getColumnIndex("HookahDiscount")),
                        c.getString(c.getColumnIndex("HookahDescription")),
                        c.getString(c.getColumnIndex("UserPhone"))
                ));
            }while (c.moveToNext());
        }
        return result;

    }




    //**************************************Swipe Delete Cart Item*****************************

    public void removeFromCart(String productId, String phone) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId='%s'",phone,productId);
        db.execSQL(query);

    }
}
