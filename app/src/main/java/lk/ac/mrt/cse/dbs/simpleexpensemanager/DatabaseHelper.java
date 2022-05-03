package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static  final String DATABASE_NAME= "190277L";
    public static  final String TABLE_NAME= "account";
    public static  final String Col1= "AccountNo";
    public static  final String Col2= "bank";
    public static  final String Col3= "accountholder";
    public static  final String Col4= "initialbalance";


    public static final String TABLE_NAME_TR = "transaction_table";
    public static final String trCol1 = "date";
    public static final String trCol2 = "AccountNo";
    public static final String trCol3 = "expenseType";
    public static final String trCol4 = "amount";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table "+TABLE_NAME+"(AccountNo TEXT PRIMARY KEY, bank TEXT , accountholder TEXT, initialbalance DOUBLE )");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME_TR+"(AccountNo TEXT ,date DATE,expenseType TEXT,amount DOUBLE ,FOREIGN KEY(AccountNo) REFERENCES account(AccountNo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_TR);

        onCreate(sqLiteDatabase);
    }

    public boolean insertAccountData(String accountNo,String bank, String accHolder,double initBalance ){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col1,accountNo);
        contentValues.put(Col2,bank);
        contentValues.put(Col3,accHolder);
        contentValues.put(Col4,initBalance);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return result!=-1;
    }

    public Map<String, Account> getAccountData() {
        Map<String, Account> accountMap = new HashMap<String, Account>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            accountMap.put(res.getString(res.getColumnIndex(Col1)),new Account(res.getString(res.getColumnIndex(Col1)),
                    res.getString(res.getColumnIndex(Col2)),res.getString(res.getColumnIndex(Col3)),res.getDouble(res.getColumnIndex(Col4))
                    ));
            res.moveToNext();
        }
        return accountMap;

    }

    public boolean removeAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, Col1 + "=" + accountNo, null) > 0;
    }

    public void updateAccount(String accountNo, Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Col2, account.getBankName());
        values.put(Col3, account.getAccountHolderName());
        values.put(Col4, account.getBalance());
        db.update(TABLE_NAME, values, "accountNo=?", new String[]{accountNo});
        db.close();
    }

    public List<Transaction> getTransactionData() {
        List<Transaction> transactionList = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from transaction_table", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            try {
                transactionList.add(new Transaction(new SimpleDateFormat("dd/MM/yyyy").parse(res.getString(res.getColumnIndex(trCol1))),
                        res.getString(res.getColumnIndex(trCol2)),ExpenseType.valueOf(res.getString(res.getColumnIndex(trCol3))) ,res.getDouble(res.getColumnIndex(trCol4))
                ));
            } catch (ParseException e) {

                e.printStackTrace();
            }
            res.moveToNext();
        }
        return transactionList;
    }

    public boolean insertTransaction(Transaction transaction) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(trCol1, new SimpleDateFormat("dd/MM/yyyy").format(transaction.getDate()));
        contentValues.put(trCol2,transaction.getAccountNo());
        contentValues.put(trCol3, String.valueOf(transaction.getExpenseType()));
        contentValues.put(trCol4,transaction.getAmount());
        long result = sqLiteDatabase.insert(TABLE_NAME_TR, null, contentValues);
        return result!=-1;
    }
}
