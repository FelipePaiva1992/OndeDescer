package br.com.thesource.ondedescer.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.thesource.ondedescer.model.Alerta;
import br.com.thesource.ondedescer.model.Schedule;

public class AlertaDataBaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "ondedescer.db";
    public static final String TABLE_NAME = "tb_alerta";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE "
												+ TABLE_NAME + "( " 
												+ "id_alerta integer, "
												+ "nome text,  "
                                                + "endereco text,  "
                                                + "metros integer,  "
    											+ "lat text,"
                                                + "long text,"
                                                + "id_schedule interger"
                                                + " ); ";
	private static final String TAG = "AlertaDataBaseHelper";
	
	public AlertaDataBaseHelper(Context context) {
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

	
	public void insertAlerta(
							SQLiteDatabase db, 
							Alerta alerta){
		
		SQLiteStatement liteProgram = 
			db.compileStatement(" INSERT INTO "+TABLE_NAME+" VALUES(?,?,?,?,?,?,?); ");

        int i = 1;

		liteProgram.bindLong(i++, getMax(db));
        liteProgram.bindString(i++, alerta.getNome());
        liteProgram.bindString(i++, alerta.getEndrereco().getAddressLine(0));
        liteProgram.bindLong(i++, alerta.getMetros());
        liteProgram.bindDouble(i++, alerta.getEndrereco().getLatitude());
        liteProgram.bindDouble(i++, alerta.getEndrereco().getLongitude());
        liteProgram.bindLong(i++, alerta.getSchedule().getId());

		try{
			liteProgram.execute();
			
		}catch(SQLException e){
			Log.e(TAG, e.getMessage());
		}finally{
			liteProgram.close();
		}
	
	}


	public List<Alerta> buscaAlerta(Context context, SQLiteDatabase db){

        Geocoder geocoder = new Geocoder(context);
        List<Alerta> list = new ArrayList<Alerta>();
		StringBuffer  query = new StringBuffer();
		query.append(" SELECT * ");
		query.append(" FROM "+TABLE_NAME  );
		
		Cursor cursor = db.rawQuery(query.toString(),null);
		try{
			if(cursor != null){
				if(cursor.moveToFirst()){
					do{
                        int i = 0;

						Alerta alerta = new Alerta();
                        Schedule schedule = new Schedule();
                        alerta.setId(cursor.getLong(i++));
                        alerta.setNome(cursor.getString(i++));
                        alerta.setEndrereco(geocoder.getFromLocationName(cursor.getString(i++), 1).get(0));
                        alerta.setMetros(cursor.getInt(i++));
                        schedule.setId(cursor.getLong(i++));

                        alerta.setSchedule(schedule);
                        list.add(alerta);

					}while(cursor.moveToNext());
				}
			}
		} catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally{
			cursor.close();
		}
		
		return list;
	}

    private int getMax(SQLiteDatabase db){

        int max = 0;
        StringBuffer  query = new StringBuffer();
        query.append(" SELECT ifnull(max(id_alerta),0)+1 FROM "+TABLE_NAME);

        Cursor cursor = db.rawQuery(query.toString(),null);
        try{
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do{
                        max = cursor.getInt(0);

                    }while(cursor.moveToNext());
                }
            }
        }finally{
            cursor.close();
        }

        return max;
    }

}
