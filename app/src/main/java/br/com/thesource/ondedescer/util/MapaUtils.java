package br.com.thesource.ondedescer.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import br.com.thesource.ondedescer.enums.TipoMarker;

/**
 * Created by Felipe on 08/03/2015.
 */
public class MapaUtils {

    private static final LatLng latLongSaoPaulo = new LatLng(-23.682818703499485,-46.5952992);

    /**
     * Da Zoom no mapa
     * @param latLng
     * @param zoom
     * @param mapa
     */
    public static void zoomMapa(LatLng latLng, int zoom, GoogleMap mapa){
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Remove marker especifico
     * @param marker
     */
    public static void removerMarker(Marker marker){
        if(marker != null)
            marker.remove();
    }

    /**
     * Remove todos os markers
     * @param markers
     */
    public static void removerTodosMarkers(List<Marker> markers){
        for(Marker marker: markers){
            if(marker != null)
                marker.remove();
        }
    }

    /**
     * Cria um marker com o icone escolhido como default
     * @param tipoMarker
     * @return markerOptions
     */
    public static MarkerOptions defaultMarker(TipoMarker tipoMarker){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(tipoMarker.getValor()));
        return markerOptions;
    }

    public static Marker adicionaMarkerUltimaLocalizacaoSistema(LocationManager locationManager, GoogleMap mapa){
        Location ultimaLocalizacao = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(ultimaLocalizacao != null){
            LatLng latLngUltimaLocalizacao = new LatLng(ultimaLocalizacao.getLatitude(),ultimaLocalizacao.getLongitude());
            MarkerOptions markerOptions = MapaUtils.defaultMarker(TipoMarker.POSICAO_PASSADA);
            markerOptions.position(latLngUltimaLocalizacao);
            MapaUtils.zoomMapa(latLngUltimaLocalizacao, 18, mapa);
            return mapa.addMarker(markerOptions);
        }else{
            MarkerOptions markerOptions = MapaUtils.defaultMarker(TipoMarker.POSICAO_PASSADA);
            markerOptions.position(latLongSaoPaulo);
            MapaUtils.zoomMapa(latLongSaoPaulo, 18, mapa);
            return mapa.addMarker(markerOptions);
        }
    }

    public static LocationManager iniciarLocationManager(Context context, LocationListener locationListener){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (gpsIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, locationListener);
            } else if (networkIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, locationListener);
            } else {
                //GPS desligado....
            }
        } else {
            //Erro LocationManager...
        }
        return locationManager;
    }

    public static double distanciaEntreDoisPontos(Double latOrigem, Double lngOrigem, Double latDestino, Double lngDestino){
        double distancia;

        Location locationA = new Location("point A");

        locationA.setLatitude(latOrigem);

        locationA.setLongitude(lngOrigem);

        Location locationB = new Location("point B");

        locationB.setLatitude(latDestino);

        locationB.setLongitude(lngDestino);

        distancia = locationA.distanceTo(locationB);
        return  distancia;
    }
}
