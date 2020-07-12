package com.example.vandersonsouza.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vandersonsouza.boaviagem.dao.BoaViagemDAO;
import com.example.vandersonsouza.boaviagem.domain.Gasto;
import com.example.vandersonsouza.boaviagem.domain.Viagem;

import java.util.Calendar;
import java.util.Date;

public class GastoActivity extends AppCompatActivity {

    private int ano, mes, dia;
    private Button dataGasto;
    private Date dataSelecionada;
    private Spinner categoria;
    private TextView destino;
    private EditText valor;
    private EditText descricao;
    private EditText local;
    private Date data;
    private Viagem viagem;

    private BoaViagemDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasto);

        dao = new BoaViagemDAO(this);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataSelecionada = calendar.getTime();
        dataGasto = (Button) findViewById(R.id.data);
        dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categoria_gasto,
                android.R.layout.simple_spinner_item);
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);

        if(getIntent().getExtras() != null) {
            Integer viagemId = Integer.parseInt(getIntent().getExtras().getString(Constantes.VIAGEM_ID));
            viagem = dao.buscarViagemPorId(viagemId);
        }

        destino = (TextView) findViewById(R.id.destino);
        destino.setText(viagem != null ? viagem.getDestino() : "");

        valor = (EditText) findViewById(R.id.valor);
        descricao = (EditText) findViewById(R.id.descricao);
        local = (EditText) findViewById(R.id.local);
    }

    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (R.id.data == id) {
            return new DatePickerDialog(this, listener, ano, mes, dia);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);
            data = criarData(ano, mes, dia);
        }
    };

    public void registrarGasto(View view){

        Gasto gasto = new Gasto();
        gasto.setCategoria(categoria.getSelectedItem().toString());
        gasto.setData(dataSelecionada);
        gasto.setDescricao(descricao.getText().toString());
        gasto.setValor(Double.parseDouble(valor.getText().toString()));
        gasto.setLocal(local.getText().toString());

        if(viagem == null)
            viagem = dao.buscarViagemPorDestino(destino.getText().toString());

        if(viagem != null)
        {
            gasto.setViagemId(viagem.getId());

            Long resultado = dao.salvarGasto(gasto);

            if(resultado != -1 ){
                Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, getString(R.string.viagem_nao_encontrada), Toast.LENGTH_SHORT).show();
        }

    }

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        dataSelecionada = calendar.getTime();
        return calendar.getTime();
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }

}
