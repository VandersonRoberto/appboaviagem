package com.example.vandersonsouza.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.vandersonsouza.boaviagem.dao.BoaViagemDAO;
import com.example.vandersonsouza.boaviagem.domain.Viagem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViagemActivity extends Activity {

    private Date dataChegada, dataSaida;
    private int ano, mes, dia;
    private Button dataChegadaButton, dataSaidaButton;
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;
    private String id;
    private BoaViagemDAO dao;
    private SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viagem);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataChegadaButton = (Button) findViewById(R.id.dataChegada);
        dataSaidaButton = (Button) findViewById(R.id.dataSaida);

        destino = (EditText) findViewById(R.id.destino);
        quantidadePessoas = (EditText) findViewById(R.id.quantidadePessoas);
        orcamento = (EditText) findViewById(R.id.orcamento);

        radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);

        dao = new BoaViagemDAO(this);
        id = getIntent().getStringExtra(Constantes.VIAGEM_ID);

        if(id != null){
            prepararEdicao();
        }


    }

    private void prepararEdicao() {

        Viagem viagem = dao.buscarViagemPorId(Integer.parseInt(id));

        destino.setText(viagem.getDestino());
        dataChegada = viagem.getDataChegada();
        dataSaida = viagem.getDataSaida();
        dataChegadaButton.setText(dateFormat.format(viagem.getDataChegada()));
        dataSaidaButton.setText(dateFormat.format(viagem.getDataSaida()));
        quantidadePessoas.setText(viagem.getQuantidadePessoas().toString());
        orcamento.setText(viagem.getOrcamento().toString());
    }

    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case R.id.dataChegada:
                return new DatePickerDialog(this, dataChegadaListener, ano, mes, dia);

            case R.id.dataSaida:
                return new DatePickerDialog(this, dataSaidaListener, ano, mes, dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dataChegadaListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            dataChegada = criarData(anoSelecionado, mesSelecionado, diaSelecionado);
            dataChegadaButton.setText(diaSelecionado + "/" + (mesSelecionado + 1) + "/" + anoSelecionado);
        }
    };

    private DatePickerDialog.OnDateSetListener dataSaidaListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            dataSaida = criarData(anoSelecionado, mesSelecionado, diaSelecionado);
            dataSaidaButton.setText(diaSelecionado + "/" + (mesSelecionado + 1) + "/" + anoSelecionado);
        }
    };

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        return calendar.getTime();
    }

    private void removerViagem(String id) {
        dao.removerViagem(Long.parseLong(id));
    }

    public void salvarViagem(View view){

        Viagem viagem = new Viagem();

        if(id != null) {
            viagem.setId(Integer.parseInt(id));
        }

        viagem.setDestino(destino.getText().toString());
        viagem.setDataChegada(dataChegada);
        viagem.setDataSaida(dataSaida);
        viagem.setOrcamento(Double.parseDouble(orcamento.getText().toString()));
        viagem.setQuantidadePessoas(Integer.parseInt(quantidadePessoas.getText().toString()));

        int tipo = radioGroup.getCheckedRadioButtonId();

        if(tipo == R.id.lazer){
            viagem.setTipoViagem(Constantes.VIAGEM_LAZER);
        }else{
            viagem.setTipoViagem(Constantes.VIAGEM_NEGOCIOS);
        }

        long resultado;

        resultado = dao.salvarViagem(viagem);

        if(resultado != -1 ){
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }

}
