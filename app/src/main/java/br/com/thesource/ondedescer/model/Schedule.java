package br.com.thesource.ondedescer.model;

import java.util.Date;

/**
 * Created by Felipe on 11/03/2015.
 */
public class Schedule {

    private Long id;
    private Date horaInicio;
    private Boolean isRepetir;
    private Boolean isAtivo;
    private Boolean isDomingo;
    private Boolean isSegunda;
    private Boolean isTerca;
    private Boolean isQuarta;
    private Boolean isQuinta;
    private Boolean isSexta;
    private Boolean isSabado;
    private Parametrizacao parametrizacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Boolean getIsRepetir() {
        return isRepetir;
    }

    public void setIsRepetir(Boolean isRepetir) {
        this.isRepetir = isRepetir;
    }

    public Boolean getIsDomingo() {
        return isDomingo;
    }

    public void setIsDomingo(Boolean isDomingo) {
        this.isDomingo = isDomingo;
    }

    public Boolean getIsSegunda() {
        return isSegunda;
    }

    public void setIsSegunda(Boolean isSegunda) {
        this.isSegunda = isSegunda;
    }

    public Boolean getIsTerca() {
        return isTerca;
    }

    public void setIsTerca(Boolean isTerca) {
        this.isTerca = isTerca;
    }

    public Boolean getIsQuarta() {
        return isQuarta;
    }

    public void setIsQuarta(Boolean isQuarta) {
        this.isQuarta = isQuarta;
    }

    public Boolean getIsQuinta() {
        return isQuinta;
    }

    public void setIsQuinta(Boolean isQuinta) {
        this.isQuinta = isQuinta;
    }

    public Boolean getIsSexta() {
        return isSexta;
    }

    public void setIsSexta(Boolean isSexta) {
        this.isSexta = isSexta;
    }

    public Boolean getIsSabado() {
        return isSabado;
    }

    public void setIsSabado(Boolean isSabado) {
        this.isSabado = isSabado;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean isAtivo) {
        this.isAtivo = isAtivo;
    }

    public Parametrizacao getParametrizacao() {
        return parametrizacao;
    }

    public void setParametrizacao(Parametrizacao parametrizacao) {
        this.parametrizacao = parametrizacao;
    }

}
