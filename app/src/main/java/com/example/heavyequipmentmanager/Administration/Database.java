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
    public static final String DATABASE_NAME_M = "Manager.db";

    public static final String TABLE_NAME_E = "Engines";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "TREATMENT";
    public static final String COL4 = "NEXTTREATMENT";
    public static final String COL5 = "WORKINGHOURS";
    public static final String COL6 = "TESTDATE";
    public static final String COL7 = "ENSURENCEDATE";
    public static final String COL8 = "IMAGESID";

    public static final String TABLE_NAME_U = "Users";
    public static final String U_COL1 = "ID";
    public static final String U_COL2 = "USERNM";
    public static final String U_COL3 = "PASSWD";
    public static final String U_COL4 = "LOGIN";        // Stored as INT (1 true, 0 false)


    private String switcher = "";


    public Database(Context context, String switcher) {
        super(context, DATABASE_NAME_M, null, 1);this.switcher = switcher;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        if(switcher.matches("ENGN")) {
        String createEnginesTable = "CREATE TABLE " + TABLE_NAME_E + " ( " + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " REAL, " +
                COL6 + " TEXT, " + COL7 + " TEXT)";

        sqLiteDatabase.execSQL(createEnginesTable);
//        }
//        else if(switcher.matches("USRS")){
        String createUsersTable = "CREATE TABLE " + TABLE_NAME_U + " ( " + U_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + U_COL2 + " TEXT, " + U_COL3 + " TEXT, " +
                U_COL4 + " INT)";
        sqLiteDatabase.execSQL(createUsersTable);
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_E);
            onCreate(sqLiteDatabase);

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_U);
            onCreate(sqLiteDatabase);
    }

    public void initUsersTable(){
        String u1 = "mgrash";
        String p1 = "0547706509";

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(U_COL1, 0);
        contentValues.put(U_COL2, u1);
        contentValues.put(U_COL3, p1);
        contentValues.put(U_COL4, 0);
        if(!checkDBUSRS(u1, p1))
             db.insert(TABLE_NAME_U, null, contentValues);
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
            db.insert(TABLE_NAME_E, null, contentValues);

//         db.close();
    }

    public void loadData(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME_E, null);
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
        String query = "DELETE FROM " + TABLE_NAME_E + " WHERE " + COL1 + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
//        db.close();
        Log.d("DATABASE-deleteRecord", "Deleted " + id + " successfully");
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
        db.update(TABLE_NAME_E, contentValues, "ID = ?", new String[] {engineIndex+""});
//        db.close();
        return;
    }


    public void updateTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String drop_table = "DELETE FROM " + TABLE_NAME_E;
        db.execSQL(drop_table);
        db.close();
        // write to database
        for(Integer index: Constants.manager.getEngines().keySet()){
            EngineTool en = Constants.manager.getEngines().get(index);
            if(en != null) {
                saveEngine(en, index);
            }
        }
        Log.d("DATABASE-updateTable()", "Table updated!");
    }

    public boolean checkDB(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor mCursor = null;

        mCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_E + " WHERE " + COL1 + " = " + id + " AND " + COL2 + " = " + "'"+name+"'", null);

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


    /**
     * Login activity helpers
     * */
    public boolean checkDBUSRS(String username, String pass){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor mCursor = null;

        mCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_U + " WHERE " + U_COL2 + " = " + "'"+username+"'" + " AND " + U_COL3 + " = " + "'"+pass+"'", null);

        if(mCursor.getCount() > 0) {
            mCursor.close();
//            db.close();
            return true;
        }else {
            mCursor.close();
//            db.close();
            return false;
        }
    }

    public Client getClientByName(String usernm, String passwd){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_U + " WHERE " + U_COL2 + " = " + "'"+usernm+"'" + " AND " + U_COL3 + " = " + "'"+passwd+"'", null);
        boolean loggedIn;
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                loggedIn = mCursor.getInt(2) == 0? false : true;
                mCursor.close();
                return new Client(usernm, passwd, loggedIn);
            }
        }

        return null;
    }

    public void updateLogIn(String usrnm, String passwd){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String where = U_COL2 + " = " + "'"+usrnm+"'" + " AND " + U_COL3 + " = " + "'"+passwd+"'";
        values.put(U_COL4, 1);     // Logged in
        db.update(TABLE_NAME_U, values, where, null);
    }
}