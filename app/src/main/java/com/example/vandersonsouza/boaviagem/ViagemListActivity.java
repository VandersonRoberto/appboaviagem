package com.example.vandersonsouza.boaviagem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.vandersonsouza.boaviagem.dao.BoaViagemDAO;
import com.example.vandersonsouza.boaviagem.domain.Viagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener , Dialog.OnClickListener    {

    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private AlertDialog dialogConfirmacao;
    private int viagemSelecionada;
    private boolean modoSelecionarViagem;
    private SimpleDateFormat dateFormat;
    private Double valorLimite;
    private BoaViagemDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new BoaViagemDAO(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        String valor = preferencias.getString("valor_limite", "-1");
        valorLimite = Double.valueOf(valor);

        String[] de = { "imagem", "destino", "data", "total", "barraProgresso" };
        int[] para = { R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor, R.id.barraProgresso };

        SimpleAdapter adapter = new SimpleAdapter(this, listarViagens(), R.layout.lista_viagem, de, para);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.barraProgresso) {
                    Double valores[] = (Double[]) data;
                    ProgressBar progressBar = (ProgressBar) view;
                    progressBar.setMax(valores[0].intValue());
                    progressBar.setSecondaryProgress(valores[1].intValue());
                    progressBar.setProgress(valores[2].intValue());
                    return true;
                }
                return false;
            }
        });

        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);
        registerForContextMenu(getListView());

        this.alertDialog = criaAlertDialog();
        this.dialogConfirmacao = criaDialogConfirmacao();

        if (getIntent().hasExtra(Constantes.MODO_SELECIONAR_VIAGEM)) {
            modoSelecionarViagem =
                    getIntent().getExtras()
                            .getBoolean(Constantes.MODO_SELECIONAR_VIAGEM);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (modoSelecionarViagem) {
            String destino = (String) viagens.get(position).get("destino");
            Integer idViagem = (Integer) viagens.get(position).get("id");

            Intent data = new Intent();
            data.putExtra(Constantes.VIAGEM_ID, idViagem);
            data.putExtra(Constantes.VIAGEM_DESTINO, destino);
            setResult(Activity.RESULT_OK, data);
            finish();
        } else {
            viagemSelecionada = position;
            alertDialog.show();
        }
    }
    private List<Map<String, Object>> listarViagens() {

        viagens = new ArrayList<Map<String, Object>>();

        List<Viagem> listaViagens = dao.listarViagens();

        for (Viagem viagem : listaViagens) {

            Map<String, Object> item = new HashMap<String, Object>();

            item.put("id", viagem.getId());

            if (viagem.getTipoViagem() == Constantes.VIAGEM_LAZER) {
                item.put("imagem", R.drawable.lazer);
            } else {
                item.put("imagem", R.drawable.negocios);
            }

            item.put("destino", viagem.getDestino());

            String periodo = dateFormat.format(viagem.getDataChegada()) + " a "
                    + dateFormat.format(viagem.getDataSaida());

            item.put("data", periodo);

            double totalGasto = dao.calcularTotalGasto(viagem);

            item.put("total", "Gasto total R$ " + totalGasto);

            double alerta = viagem.getOrcamento() * valorLimite / 100;
            Double [] valores = new Double[] { viagem.getOrcamento(), alerta, totalGasto };
            item.put("barraProgresso", valores);
            viagens.add(item);

        }
        return viagens;
    }

    private double calcularTotalGasto(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery("SELECT SUM(VALOR) FROM GASTO WHERE VIAGEM_ID = ?",
                new String[]{ id });
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viagem_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.novo_gasto:
                startActivity(new Intent(this, GastoActivity.class));
                return true;
            case R.id.remover:

                Long id = Long.parseLong(viagens.get(viagemSelecionada).get("id").toString());
                dao.removerViagem(id);
                finish();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    private AlertDialog criaAlertDialog() {
        final CharSequence[] items = {
                getString(R.string.editar),
                getString(R.string.novo_gasto),
                getString(R.string.gastos_realizados),
                getString(R.string.remover) };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes);
        builder.setItems(items, this);
        return builder.create();
    }

    private AlertDialog criaDialogConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmacao_exclusao_viagem);
        builder.setPositiveButton(getString(R.string.sim), this);
        builder.setNegativeButton(getString(R.string.nao), this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent;
        Long id = Long.parseLong(viagens.get(viagemSelecionada).get("id").toString());

        switch (item) {
             case 0:
                 intent = new Intent(this, ViagemActivity.class);
                 intent.putExtra(Constantes.VIAGEM_ID, id.toString());
                 startActivity(intent);
                 break;
             case 1:
                 intent = new Intent(this, GastoActivity.class);
                 intent.putExtra(Constantes.VIAGEM_ID, id.toString());
                 startActivity(intent);
                break;
             case 2:
                 intent = new Intent(this, GastoListActivity.class);
                 intent.putExtra(Constantes.VIAGEM_ID, id.toString());
                 startActivity(intent);
                break;
             case 3:
                dialogConfirmacao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(this.viagemSelecionada);
                dao.removerViagem(id);
                getListView().invalidateViews();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialogConfirmacao.dismiss();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }

}