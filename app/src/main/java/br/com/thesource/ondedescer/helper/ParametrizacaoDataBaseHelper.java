package br.com.thesource.ondedescer.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.thesource.ondedescer.model.Parametrizacao;
import br.com.thesource.ondedescer.model.Schedule;

public class ParametrizacaoDataBaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "ondedescer.db";
    public static final String TABLE_NAME = "tb_param";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE "
												+ TABLE_NAME + "( "
												+ "id_param integer, "
												+ "ringtone text,  "
                                                + "vibrar integer,  "
                                                + "ativo integer  "
                                                + " ); ";
	private static final String TAG = "ParamDataBaseHelper";

	public ParametrizacaoDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if(!isExist(db, TABLE_NAME)){
			db.execSQL(DATABASE_CREATE);

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS '" + TABLE_NAME + "'");
		onCreate(db);
	}
	
	private boolean isExist(SQLiteDatabase db, String table){
		
		boolean retorno = false;
		
		Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+table+"'", null);
		try{
		    if(cursor!=null) {
		        if(cursor.getCount()>0) {
		            retorno = true;
		        }else {
		        	
		        	retorno = false;
		        	
		        }
		    }	
		}finally{
			cursor.close();
		}
		
	    return retorno;
	}

	
	public void insertParam(
							SQLiteDatabase db, 
							Parametrizacao param){

		SQLiteStatement liteProgram = 
			db.compileStatement(" INSERT INTO "+TABLE_NAME+" VALUES(?,?,?,?); ");

        int i = 1;

		liteProgram.bindLong(i++, param.getId());
        liteProgram.bindNull(i++);
        liteProgram.bindLong(i++, param.getIsVibrar() ? 1L : 0L);
        liteProgram.bindLong(i++, param.getIsAtivo()?1L:0L);


		try{
			liteProgram.execute();
			
		}catch(SQLException e){
			Log.e(TAG, e.getMessage());
		}finally{
			liteProgram.close();
		}
	
	}


	public List<Parametrizacao> buscaParam(SQLiteDatabase db){

        List<Parametrizacao> list = new ArrayList<Parametrizacao>();
		StringBuffer  query = new StringBuffer();
		query.append(" SELECT * ");
		query.append(" FROM "+TABLE_NAME  );
		
		Cursor cursor = db.rawQuery(query.toString(),null);
		try{
			if(cursor != null){
				if(cursor.moveToFirst()){
					do{
                        int i = 0;

                        Parametrizacao param = new Parametrizacao();

                        param.setId(cursor.getLong(i++));
                        param.setRingtone(cursor.getString(i++));
                        param.setIsVibrar(cursor.getInt(i++) == 0 ? false : true);
                        param.setIsAtivo(cursor.getInt(i++) == 0 ? false : true);

                        list.add(param);

					}while(cursor.moveToNext());
				}
			}
        } finally{
			cursor.close();
		}
		
		return list;
	}

    public long getMax(SQLiteDatabase db){

        long max = 0;
        StringBuffer  query = new StringBuffer();
        query.append(" SELECT ifnull(max(id_param),0)+1 FROM "+TABLE_NAME);

        Cursor cursor = db.rawQuery(query.toString(),null);
        try{
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do{
                        max = cursor.getLong(0);

                    }while(cursor.moveToNext());
                }
            }
        }finally{
            cursor.close();
        }

        return max;
    }

}
