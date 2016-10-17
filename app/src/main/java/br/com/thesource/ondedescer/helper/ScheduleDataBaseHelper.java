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

public class ScheduleDataBaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "ondedescer.db";
    public static final String TABLE_NAME = "tb_schedule";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE "
												+ TABLE_NAME + "( "
												+ "id_schedule integer, "
												+ "hora_inicio text,  "
                                                + "repetir integer,  "
                                                + "ativo integer,  "
                                                + "dom integer,  "
                                                + "seg integer,  "
                                                + "ter integer,  "
                                                + "qua integer,  "
                                                + "qui integer,  "
                                                + "sex integer,  "
                                                + "sab integer,  "
                                                + "id_parametrizacao integer  "
                                                + " ); ";
	private static final String TAG = "ScheduleDataBaseHelper";

	public ScheduleDataBaseHelper(Context context) {
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

	
	public void insertSchedule(
							SQLiteDatabase db, 
							Schedule schedule){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

		SQLiteStatement liteProgram = 
			db.compileStatement(" INSERT INTO "+TABLE_NAME+" VALUES(?,?,?,?,?,?,?,?,?,?,?,?); ");

        int i = 1;

		liteProgram.bindLong(i++, schedule.getId());
        liteProgram.bindString(i++, sdf.format(schedule.getHoraInicio()));
        liteProgram.bindLong(i++, schedule.getIsRepetir() ? 1L : 0L);
        liteProgram.bindLong(i++, schedule.getIsAtivo()?1L:0L);
        liteProgram.bindLong(i++, schedule.getIsDomingo()?1L:0L);
        liteProgram.bindLong(i++, schedule.getIsSegunda()?1L:0L);
        liteProgram.bindLong(i++, schedule.getIsTerca()?1L:0L);
        liteProgram.bindLong(i++, schedule.getIsQuarta()?1L:0L);
        liteProgram.bindLong(i++, schedule.getIsQuinta()?1L:0L);
        liteProgram.bindLong(i++, schedule.getIsSexta()?1L:0L);
        liteProgram.bindLong(i++, schedule.getIsSabado()?1L:0L);
        liteProgram.bindLong(i++, schedule.getParametrizacao().getId());


		try{
			liteProgram.execute();
			
		}catch(SQLException e){
			Log.e(TAG, e.getMessage());
		}finally{
			liteProgram.close();
		}
	
	}


	public List<Schedule> buscaSchedule(SQLiteDatabase db){

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

        List<Schedule> list = new ArrayList<Schedule>();
		StringBuffer  query = new StringBuffer();
		query.append(" SELECT * ");
		query.append(" FROM "+TABLE_NAME  );
		
		Cursor cursor = db.rawQuery(query.toString(),null);
		try{
			if(cursor != null){
				if(cursor.moveToFirst()){
					do{
                        int i = 0;

                        Schedule schedule = new Schedule();
                        Parametrizacao param = new Parametrizacao();

                        schedule.setId(cursor.getLong(i++));
                        schedule.setHoraInicio(sdf.parse(cursor.getString(i++)));
                        schedule.setIsRepetir(cursor.getInt(i++) == 0 ? false : true);
                        schedule.setIsAtivo(cursor.getInt(i++) == 0 ? false : true);
                        schedule.setIsDomingo(cursor.getInt(i++) == 0 ? false : true);
                        schedule.setIsSegunda(cursor.getInt(i++)==0?false:true);
                        schedule.setIsTerca(cursor.getInt(i++)==0?false:true);
                        schedule.setIsQuarta(cursor.getInt(i++)==0?false:true);
                        schedule.setIsQuinta(cursor.getInt(i++)==0?false:true);
                        schedule.setIsSexta(cursor.getInt(i++)==0?false:true);
                        schedule.setIsSabado(cursor.getInt(i++)==0?false:true);
                        param.setId(cursor.getLong(i++));

                        schedule.setParametrizacao(param);
                        list.add(schedule);

					}while(cursor.moveToNext());
				}
			}
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        } finally{
			cursor.close();
		}
		
		return list;
	}

    public long getMax(SQLiteDatabase db){

        long max = 0;
        StringBuffer  query = new StringBuffer();
        query.append(" SELECT ifnull(max(id_schedule),0)+1 FROM "+TABLE_NAME);

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
