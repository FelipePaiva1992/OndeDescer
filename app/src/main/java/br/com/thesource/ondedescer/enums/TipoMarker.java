package br.com.thesource.ondedescer.enums;

import br.com.thesource.ondedescer.R;

/**
 * Created by Felipe on 08/03/2015.
 */
public enum TipoMarker {

    PONTO_ONIBUS_ATIVO(R.drawable.bus_ativo),
    PONTO_ONIBUS_DESATIVADO(R.drawable.bus_desativado),
    POSICAO_ATUAL(R.drawable.local_atual),
    POSICAO_PASSADA(R.drawable.local_passada);

    private final int valor;

    TipoMarker(int valorOpcao) {
        valor = valorOpcao;
    }

    public int getValor() {
        return valor;
    }

}
