package br.com.thesource.ondedescer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.thesource.ondedescer.R;
import br.com.thesource.ondedescer.adapter.AlertaAdapter;
import br.com.thesource.ondedescer.dialog.AgendaDialog;
import br.com.thesource.ondedescer.enums.TipoMarker;
import br.com.thesource.ondedescer.helper.AlertaDataBaseHelper;
import br.com.thesource.ondedescer.helper.ParametrizacaoDataBaseHelper;
import br.com.thesource.ondedescer.helper.ScheduleDataBaseHelper;
import br.com.thesource.ondedescer.model.Alerta;
import br.com.thesource.ondedescer.util.MapaUtils;
import br.com.thesource.ondedescer.util.Utils;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        SlidingMenu.OnOpenListener,
        SlidingMenu.OnCloseListener,
        LocationListener,
        LocationSource {

    private Context context;
    private SlidingMenu menu;
    public static GoogleMap mapa;
    private OnLocationChangedListener mListener;
    private LocationManager locationManager;
    Marker markerPosicaoAtual;
    private static int DISTANCIA_MINIMA_ATUALIZACAO = 0;
    public static Collection<Marker> markersAtivos = new ArrayList<>();
    private AgendaDialog agenda;
    private AlertaDataBaseHelper alertaDataBaseHelper;
    private SQLiteDatabase database;
    private List<Alerta> listaAlerta;
    private AlertaAdapter alertaAdapter;
    private ImageButton btn_agenda;

    public static Collection<Alerta> alertasAtivos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        this.context = this;

        //MAPA
        locationManager = MapaUtils.iniciarLocationManager(this,this);
        mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        cargaInicialMapa();
        mapa.setLocationSource(this);
        mapa.setMyLocationEnabled(true);

        //INICIA BANCO DE DADOS
        startDatabase();

        //MENU
        initialize();

   }

    //MENU
    private void initialize() {
        menu = Utils.createMenuLateral(this);
        menu.setOnOpenListener(this);
        menu.setOnCloseListener(this);
        agenda = new AgendaDialog(this);

        agenda.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listaAlerta = alertaDataBaseHelper.buscaAlerta(context, database);
                alertaAdapter.clear();
                alertaAdapter.addAll(listaAlerta);
                alertaAdapter.notifyDataSetChanged();
            }
        });

        btn_agenda = (ImageButton) findViewById(R.id.btn_agenda);
        btn_agenda.setOnClickListener(this);


        ImageView btn_menu = (ImageView) findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(this);

        alertaAdapter = new AlertaAdapter(context, R.layout.alerta_adapter);
        alertaAdapter.addAll(listaAlerta);

        ListView lv_agenda = (ListView)findViewById(R.id.lv_agenda);
        lv_agenda.setAdapter(alertaAdapter);


    }

    private void startDatabase(){

        alertaDataBaseHelper = new AlertaDataBaseHelper(context);
        database = alertaDataBaseHelper.getReadableDatabase();
        alertaDataBaseHelper.onCreate(database);
        //alertaDataBaseHelper.onUpgrade(database, 0, 0); //Recria a tabela

        listaAlerta = alertaDataBaseHelper.buscaAlerta(context, database);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_agenda) {
           agenda.show();
        } else if (view.getId() == R.id.btn_menu) {

            menu.showMenu(true);
        }

    }

    @Override
    public void onOpen() {
        ImageView btn_menu = (ImageView) findViewById(R.id.btn_menu);

        TranslateAnimation translateAnimation = new TranslateAnimation(0, -50, 0, 0);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(500);

        btn_menu.startAnimation(translateAnimation);
        btn_agenda.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_right_left_appear));
    }

    @Override
    public void onClose() {
        ImageView btn_menu = (ImageView) findViewById(R.id.btn_menu);

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(500);

        btn_menu.startAnimation(translateAnimation);
    }

    //MAPA
    @Override
    public void onLocationChanged(Location location) {
        if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).distanceTo(location) >= this.DISTANCIA_MINIMA_ATUALIZACAO){
            MapaUtils.removerMarker(markerPosicaoAtual);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = MapaUtils.defaultMarker(TipoMarker.POSICAO_ATUAL);
            markerOptions.position(latLng);
            markerPosicaoAtual = mapa.addMarker(markerOptions);

            for(Marker marker:markersAtivos){
                double distancia = MapaUtils.distanciaEntreDoisPontos(location.getLatitude(), location.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude);
                Toast.makeText(this, "Distancia do ponto " + marker.getTitle() + " é: " + Math.round(distancia) + " metros", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "status changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    private void cargaInicialMapa() {
        markerPosicaoAtual = MapaUtils.adicionaMarkerUltimaLocalizacaoSistema(locationManager, mapa);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(agenda != null)
            agenda.onChanged();
    }
}
