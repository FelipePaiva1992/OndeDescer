package br.com.thesource.ondedescer.model;

/**
 * Created by Felipe on 11/03/2015.
 */
public class Parametrizacao {

    private Long id;
    private String ringtone;
    private Boolean isVibrar;
    private Boolean isAtivo;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public Boolean getIsVibrar() {
        return isVibrar;
    }

    public void setIsVibrar(Boolean isVibrar) {
        this.isVibrar = isVibrar;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean isAtivo) {
        this.isAtivo = isAtivo;
    }
}
