package br.com.thesource.ondedescer.util;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.List;

import br.com.thesource.ondedescer.R;
import br.com.thesource.ondedescer.exception.ODException;

/**
 * Created by xxnickfuryxx on 06/03/15.
 */
public class Utils {

    private static final String WEBSERVICE_GMAPS = ""
            + "http://maps.googleapis.com/maps/api/geocode/xml?address=";

    public static SlidingMenu createMenuLateral(Activity activity){
        SlidingMenu slidingPaneLayout = new SlidingMenu(activity);

        //Agora para abrir o menu, o dedo tem que partir da argin
        slidingPaneLayout.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        slidingPaneLayout.setShadowWidthRes(R.dimen.shadow_width);
        slidingPaneLayout.setShadowDrawable(R.drawable.shadow);
        slidingPaneLayout.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingPaneLayout.setFadeDegree(0.35f);
        slidingPaneLayout.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        slidingPaneLayout.setMenu(R.layout.menu_frame);

        return slidingPaneLayout;
    }

    /**
     * Formata o endereco no padrao do google
     *
     * @param endereco
     * @return
     */
    private static String formataEndereco(String endereco) {
        endereco = endereco.replaceAll(" ", "+");
        return WEBSERVICE_GMAPS + Normalizer.normalize(endereco, Normalizer.Form.NFD)
                + "&sensor=true";
    }

    /**
     * Busca a geoLocalizacao pelo endereco por URL.
     *
     * @param enderecoGmaps
     * @return
     * @throws ODException
     */
    public static Location getGeoPositionByAddress(String enderecoGmaps) throws ODException {

        //Permite com que a aplicacao acessa a net para buscar a geoLocalizacao
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Location location = new Location(LocationManager.GPS_PROVIDER);

        try {
            URL url;

            url = new URL(formataEndereco(enderecoGmaps));
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestProperty("Request-Method", "GET");
            connection.setConnectTimeout(10000);
            connection.connect();

            SAXBuilder sb = new SAXBuilder();

            try {
                Document d = sb.build(connection.getInputStream());
                Element directionsResponse = d.getRootElement();
                List<Element> result = directionsResponse.getChildren("result");

                for (Element element : result) {
                    Element geometry = element.getChild("geometry");
                    Element locationChild = geometry.getChild("location");
                    location.setLatitude(Double.parseDouble(locationChild
                            .getChildText("lat")));
                    location.setLongitude(Double.parseDouble(locationChild
                            .getChildText("lng")));

                    break;
                }

            } catch (JDOMException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {

            throw new ODException("Erro ao converter o endereco em GeoPosicao."+e.getMessage());

        }

        return location;
    }

    /**
     * Busca a geoLocalizacao pelo endereco por GEOCODER.
     *
     * @param context, address
     * @return
     * @throws ODException
     */
    public static Location getGeoPositionByAddress2(Context context, String address) throws ODException {

        List<Address> l;
        Geocoder g = new Geocoder(context);
        try {
            l =  g.getFromLocationName("Rua manifesto, 992 ipiranga são paulo", 1);

        } catch (IOException e) {
            throw new ODException("Erro ao converter o endereco em GeoPosicao."+e.getMessage());
        }

        Location location = new Location(LocationManager.GPS_PROVIDER);
        if(l != null){
            if(!l.isEmpty()){
                location.setLatitude(l.get(0).getLatitude());
                location.setLongitude(l.get(0).getLongitude());
            }
        }

        return location;
    }


}
