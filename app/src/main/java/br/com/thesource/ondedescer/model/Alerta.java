package br.com.thesource.ondedescer.model;

import android.location.Address;

/**
 * Created by Felipe on 11/03/2015.
 */
public class Alerta {

    private Long id;
    private String nome;
    private Address endrereco;
    private int metros;
    private Schedule schedule;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Address getEndrereco() {
        return endrereco;
    }

    public void setEndrereco(Address endrereco) {
        this.endrereco = endrereco;
    }

    public int getMetros() {
        return metros;
    }

    public void setMetros(int metros) {
        this.metros = metros;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
