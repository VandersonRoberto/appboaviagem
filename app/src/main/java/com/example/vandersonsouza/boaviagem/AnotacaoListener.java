package com.example.vandersonsouza.boaviagem;

import android.os.Bundle;

import com.example.vandersonsouza.boaviagem.domain.Anotacao;

public interface AnotacaoListener {
    void viagemSelecionada(Bundle bundle);
    void anotacaoSelecionada(Anotacao anotacao);
    void novaAnotacao();
}