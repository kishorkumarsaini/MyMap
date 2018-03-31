package mygooglemap.kishor.mygooglemap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kishor on 3/26/18.
 */

public class DbHelper extends SQLiteOpenHelper
{


    public  static  final String DATABASE="location.db";
    public  static  final String TABLE_NAME="loc";
    public  static  final String ID="id";
    public  static  final String  LAT="lat";
    public  static  final String  LNG="lng";
    public  static  final String  ZOOM="zoom";
    SQLiteDatabase database;



    public DbHelper(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String query="create table loc(id int primary key,lat double ,lng double,zoom text)";
        database.execSQL(query);

    }

    public  long insert(ContentValues  contentValues)
    {
        long rowId=database.insert(TABLE_NAME,null,contentValues);
        return  rowId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }
}
