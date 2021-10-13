package com.example.heavyequipmentmanager.Administration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.heavyequipmentmanager.Engine.EngineTool;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Manager.db";
    public static final String TABLE_NAME = "Engines";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "TREATMENT";
    public static final String COL4 = "NEXTTREATMENT";
    public static final String COL5 = "WORKINGHOURS";
    public static final String COL6 = "TESTDATE";
    public static final String COL7 = "ENSURENCEDATE";
    public static final String COL8 = "IMAGESID";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createEnginesTable = "CREATE TABLE " + TABLE_NAME + " ( " + COL1 +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     COL2 + " TEXT, " + COL3 + " TEXT, " +  COL4 + " TEXT, " + COL5 + " REAL, " +
                COL6 + " TEXT, " + COL7 + " TEXT)";

        sqLiteDatabase.execSQL(createEnginesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /*
    * This worked! while the previous one (saveData()) for some reason, not worked.
    * */
    public void saveEngine(EngineTool en, int index){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, index);
        contentValues.put(COL2, en.getName());
        contentValues.put(COL3, en.getTreatment());
        contentValues.put(COL4, en.getNextTreatment());
        contentValues.put(COL5, en.getWorkingHours());
        contentValues.put(COL6, en.getTestDate());
        contentValues.put(COL7, en.getEnsurenceDate());
        // check if such row exists:
         if(!checkDB(index, en.getName()))
            db.insert(TABLE_NAME, null, contentValues);

//         db.close();
    }

    public void loadData(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            if (data.getCount() == 0) {
                Log.d("DATABASE-loadData():", " data is empty..");
                return;
            }
            if (data.moveToFirst()) {
                do {
                    String name = data.getString(1);
                    String treatment = data.getString(2);
                    String nextTreatment = data.getString(3);
                    Double workingHours = data.getDouble(4);
                    String testDate = data.getString(5);
                    String ensurenceDate = data.getString(6);
                    EngineTool en = new EngineTool(name, treatment, nextTreatment, workingHours, 0);

                    if (!testDate.matches(""))
                        en.setTestDate(testDate);
                    if (!ensurenceDate.matches(""))
                        en.setEnsurenceDate(ensurenceDate);

                    Constants.manager.addEngine(data.getInt(0), en);

                } while (data.moveToNext());
            }
        }catch (Exception e){
            Log.d("DATABASE-loadData()", "Table doesn't exists");
        }
//        db.close();
    }

    public void deleteRecordID(int id){
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
//        db.close();
        Log.d("Databse: deleteRecordID", "Deleted " + id + " successfully");
    }

    public void updateEngineInformation(int engineIndex, EngineTool en){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, engineIndex);
        contentValues.put(COL2, en.getName());
        contentValues.put(COL3, en.getTreatment());
        contentValues.put(COL4, en.getNextTreatment());
        contentValues.put(COL5, en.getWorkingHours());
        contentValues.put(COL6, en.getTestDate());
        contentValues.put(COL7, en.getEnsurenceDate());
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {engineIndex+""});
//        db.close();
        return;
    }


    public void updateTable(){
        SQLiteDatabase db = this.getWritableDatabase();

        String drop_table = "DELETE FROM " + TABLE_NAME;
        db.execSQL(drop_table);
        db.close();
        // write to database
        for(Integer index: Constants.manager.getEngines().keySet()){
            EngineTool en = Constants.manager.getEngines().get(index);
            if(en != null) {
                saveEngine(en, index);
            }
        }
        Log.d("Databse: updateTable()", "Table updated!");
    }

    public boolean checkDB(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor mCursor = null;

        mCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + id + " AND " + COL2 + " = " + "'"+name+"'", null);

        if(mCursor.getCount() > 0) {
            mCursor.close();
//            db.close();
            return true;
        }else {
            mCursor.close();
//            db.close();
            return false;
        }

        // false means DB is empty
    }

    public boolean isEmpty(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + table_name;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0) {
            mcursor.close();
            return false;
        }
        //leave
        else {
            mcursor.close();
            return true;
        }
        //populate table
    }
}