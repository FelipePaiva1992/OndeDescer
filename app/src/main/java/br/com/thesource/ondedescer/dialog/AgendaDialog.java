package br.com.thesource.ondedescer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.com.thesource.ondedescer.R;
import br.com.thesource.ondedescer.activity.MainActivity;
import br.com.thesource.ondedescer.enums.TipoMarker;
import br.com.thesource.ondedescer.exception.ODException;
import br.com.thesource.ondedescer.helper.AlertaDataBaseHelper;
import br.com.thesource.ondedescer.helper.ParametrizacaoDataBaseHelper;
import br.com.thesource.ondedescer.helper.ScheduleDataBaseHelper;
import br.com.thesource.ondedescer.model.Alerta;
import br.com.thesource.ondedescer.model.Parametrizacao;
import br.com.thesource.ondedescer.model.Schedule;
import br.com.thesource.ondedescer.util.MapaUtils;


public class AgendaDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    Context context;
    private TextView tx_nome_agenda = (TextView)findViewById(R.id.tx_nome_agenda);
    private TextView tx_endereco = (TextView)findViewById(R.id.tx_endereco);
    private TextView tx_numero = (TextView)findViewById(R.id.tx_numero);
    private TextView tx_cidade = (TextView)findViewById(R.id.tx_cidade);
    private Spinner cb_estado = (Spinner)findViewById(R.id.cb_estado);
    private SeekBar sk_aviso = (SeekBar)findViewById(R.id.sk_aviso);
    private CheckBox chk_repetir;
    private ToggleButton tg_seg;
    private ToggleButton tg_ter;
    private ToggleButton tg_qua;
    private ToggleButton tg_qui;
    private ToggleButton tg_sex;
    private ToggleButton tg_sab;
    private ToggleButton tg_dom;
    private TextView tx_hora;

    private SQLiteDatabase database;
    private AlertaDataBaseHelper alertaDataBaseHelper;
    private ScheduleDataBaseHelper scheduleDataBaseHelper;
    private ParametrizacaoDataBaseHelper paramDataBaseHelper;

    public AgendaDialog(Context context) {
        super(context, R.style.AppThemeDialogFullScreen);
        onChanged();

        this.context = context;
        getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);

        initialize();
        startDatabase();

    }

    private void initialize(){

        tx_nome_agenda = (TextView)findViewById(R.id.tx_nome_agenda);
        tx_endereco = (TextView)findViewById(R.id.tx_endereco);
        tx_numero = (TextView)findViewById(R.id.tx_numero);
        tx_cidade = (TextView)findViewById(R.id.tx_cidade);
        cb_estado = (Spinner)findViewById(R.id.cb_estado);
        sk_aviso = (SeekBar)findViewById(R.id.sk_aviso);
        tg_seg = (ToggleButton)findViewById(R.id.tg_seg);
        tg_ter = (ToggleButton)findViewById(R.id.tg_ter);
        tg_qua = (ToggleButton)findViewById(R.id.tg_qua);
        tg_qui = (ToggleButton)findViewById(R.id.tg_qui);
        tg_sex = (ToggleButton)findViewById(R.id.tg_sex);
        tg_sab = (ToggleButton)findViewById(R.id.tg_sab);
        tg_dom = (ToggleButton)findViewById(R.id.tg_dom);
        tx_hora = (TextView)findViewById(R.id.tx_hora);

        chk_repetir = (CheckBox)findViewById(R.id.chk_repetir);
        chk_repetir.setOnClickListener(this);

        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Button btn_cancelar = (Button)findViewById(R.id.btn_cancelar);
        btn_cancelar.setOnClickListener(this);

    }

    private void startDatabase(){

        alertaDataBaseHelper = new AlertaDataBaseHelper(context);
        database = alertaDataBaseHelper.getReadableDatabase();
        alertaDataBaseHelper.onCreate(database);
        //alertaDataBaseHelper.onUpgrade(database, 0, 0); //Recria a tabela

        scheduleDataBaseHelper = new ScheduleDataBaseHelper(context);
        database = scheduleDataBaseHelper.getReadableDatabase();
        scheduleDataBaseHelper.onCreate(database);
        //scheduleDataBaseHelper.onUpgrade(database, 0, 0); //Recria a tabela

        paramDataBaseHelper = new ParametrizacaoDataBaseHelper(context);
        database = paramDataBaseHelper.getReadableDatabase();
        //paramDataBaseHelper.onCreate(database);
        paramDataBaseHelper.onUpgrade(database, 0, 0); //Recria a tabela

    }



    //CONVERTE DE AGENDAVO PARA A ESTRUTURA DO SQLITE
    private void adicionarPontoAlerta(Alerta alerta) throws IOException{
        Geocoder geocoder = new Geocoder(getContext());

        //TODO Adicionar SCHEDULE E PARAMETRIZACAO, e adicionar ALERTA no SQLLITE

        MainActivity.alertasAtivos.add(alerta);

        LatLng latLng = new LatLng(alerta.getEndrereco().getLatitude(),alerta.getEndrereco().getLongitude());
        MarkerOptions markerOptions = MapaUtils.defaultMarker(TipoMarker.PONTO_ONIBUS_ATIVO);
        markerOptions.title(tx_nome_agenda.getText().toString());
        markerOptions.position(latLng);
        MainActivity.mapa.addMarker(markerOptions);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(sk_aviso.getProgress());
        circleOptions.fillColor(0x400000ff);
        circleOptions.strokeColor(Color.BLACK);
        MainActivity.mapa.addCircle(circleOptions);

        MapaUtils.zoomMapa(latLng,18,MainActivity.mapa);


    }

    private Alerta preparaAlerta() throws ODException, IOException, ParseException {

        Geocoder geocoder = new Geocoder(context);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        Alerta alerta = new Alerta();
        Schedule schedule = new Schedule();
        Parametrizacao parametrizacao = new Parametrizacao();


        String enderecoCompleto = tx_endereco.getText().toString()+" "+ tx_numero.getText().toString()+" "+
                tx_cidade.getText().toString()+" "+ cb_estado.getSelectedItem().toString();
        alerta.setEndrereco(geocoder.getFromLocationName(enderecoCompleto, 1).get(0));
        alerta.setNome(tx_nome_agenda.getText().toString());
        alerta.setMetros(sk_aviso.getProgress());

        schedule.setHoraInicio(sdf.parse(tx_hora.getText().toString()));
        schedule.setIsDomingo(tg_dom.isSelected());
        schedule.setIsSegunda(tg_seg.isSelected());
        schedule.setIsTerca(tg_ter.isSelected());
        schedule.setIsQuarta(tg_qua.isSelected());
        schedule.setIsQuinta(tg_qui.isSelected());
        schedule.setIsSexta(tg_sex.isSelected());
        schedule.setIsSabado(tg_sab.isSelected());
        schedule.setIsRepetir(chk_repetir.isSelected());
        schedule.setIsAtivo(true); //Default

        parametrizacao.setIsVibrar(true);
        parametrizacao.setIsAtivo(true);

        schedule.setParametrizacao(parametrizacao);
        alerta.setSchedule(schedule);


        return alerta;
    }

    private boolean validaDados(){
        boolean retorno = true;

        if(tx_nome_agenda.getText().toString().equals("")){
            Toast.makeText(context,"Nome do evento é obrigatório",Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if(tx_endereco.getText().toString().equals("")){
            Toast.makeText(context,"Endereço é obrigatório",Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if(tx_numero.getText().toString().equals("")){
            Toast.makeText(context,"Número é obrigatório",Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if(tx_cidade.getText().toString().equals("")){
            Toast.makeText(context,"Cidade é obrigatória",Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if(cb_estado.getSelectedItem().toString().equals("Estado")){
            Toast.makeText(context,"Selecione o estado",Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if(tx_hora.getText().toString().equals("")){
            Toast.makeText(context,"Hora é obrigatória",Toast.LENGTH_SHORT).show();
            retorno = false;
        }


        return retorno;

    }

    private void inserirDadosTabela(){
        if(validaDados()){
            try {
                Alerta alerta = preparaAlerta();
                alerta.getSchedule().getParametrizacao().setId(paramDataBaseHelper.getMax(database));    //Pego o id da parametrizacao
                alerta.getSchedule().setId(scheduleDataBaseHelper.getMax(database));    //Pego o id do schedule
                paramDataBaseHelper.insertParam(database, alerta.getSchedule().getParametrizacao()); //Inseri dados na tb_param
                scheduleDataBaseHelper.insertSchedule(database, alerta.getSchedule());  //Inseri dados na tb_schedule
                alertaDataBaseHelper.insertAlerta(database, alerta);                    //Inseri dados na tb_alerta

                Toast.makeText(context, "Evento "+ alerta.getNome()+" inserido.", Toast.LENGTH_LONG).show();
                adicionarPontoAlerta(alerta);
                dismiss();

            } catch (ODException e) {
                Log.e(AgendaDialog.class.getName(), e.getMessage());
            } catch (IOException e) {
                Log.e(AgendaDialog.class.getName(), e.getMessage());
            } catch (ParseException e) {
                Log.e(AgendaDialog.class.getName(), e.getMessage());
            }
        }


    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.chk_repetir:
                SlidingDrawer sd = (SlidingDrawer)findViewById(R.id.slidingDrawer);
                sd.animateOpen();
                break;

            case R.id.btn_ok:
                inserirDadosTabela();
                break;

            case R.id.btn_cancelar:
                dismiss();
                break;

        }

    }

    public void onChanged() {
        if(getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.agenda_layout_landscape);
            initialize();
        }else{
            setContentView(R.layout.agenda_layout);
            initialize();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TextView txt_metros = (TextView)findViewById(R.id.txt_metros);
        if(progress <= 1){
            seekBar.setProgress(1);
            txt_metros.setText("1 metro");
        }else{
            txt_metros.setText(progress+" metros");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
