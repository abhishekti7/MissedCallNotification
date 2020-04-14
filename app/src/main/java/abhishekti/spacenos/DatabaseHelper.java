package abhishekti.spacenos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "call_log_history.db";
    public static final String CALL_TABLE_NAME = "missed_calls_table";
    public static final String SMS_TABLE_NAME = "sms_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "PHONE_NUMBER";
    public static final String COL_3 = "TIME";
    public static final String COL_5 = "SMS_SENT";
    public static final String COL_4 = "BODY";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+CALL_TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, PHONE_NUMBER VARCHAR(15), TIME TEXT, SMS_SENT INTEGER(1)) ");
        sqLiteDatabase.execSQL("CREATE TABLE "+SMS_TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, PHONE_NUMBER VARCHAR(15), TIME TEXT, BODY TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+CALL_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+SMS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertCallRecord(String ph_num, String time){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, ph_num);
        contentValues.put(COL_3, time);
        contentValues.put(COL_5, 1);
        long res = db.insert(CALL_TABLE_NAME, null, contentValues);

        if(res==-1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertSMSRecord(String ph_num, String time, String body){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, ph_num);
        contentValues.put(COL_3, time);
        contentValues.put(COL_4, body);
        long res = db.insert(SMS_TABLE_NAME, null, contentValues);

        if(res==-1){
            return false;
        }else{
            return true;
        }
    }
    public Cursor getAllCallData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * FROM "+CALL_TABLE_NAME+" ORDER BY ID DESC", null);
        return res;
    }
    public Cursor getAllSMSData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * FROM "+SMS_TABLE_NAME+" ORDER BY ID DESC", null);
        return res;
    }

    public boolean checkIfCallMissed(String ph_num){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+CALL_TABLE_NAME+" WHERE "+COL_2+"=?", new String[]{ph_num});
        if(res.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
}
