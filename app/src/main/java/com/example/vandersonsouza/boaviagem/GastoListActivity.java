package com.example.vandersonsouza.boaviagem;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vandersonsouza.boaviagem.dao.BoaViagemDAO;
import com.example.vandersonsouza.boaviagem.domain.Gasto;
import com.example.vandersonsouza.boaviagem.domain.Viagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GastoListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private List<Map<String, Object>> gastos;
    private String dataAnterior = "";
    private Viagem viagem;
    private BoaViagemDAO dao;
    private SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new BoaViagemDAO(this);

        if(getIntent().getExtras() != null) {
            Integer viagemId = Integer.parseInt(getIntent().getExtras().getString(Constantes.VIAGEM_ID));
            viagem = dao.buscarViagemPorId(viagemId);
        }

        String[] de = {"data", "descricao", "valor", "categoria"};
        int[] para = {R.id.data, R.id.descricao, R.id.valor, R.id.categoria};

        SimpleAdapter adapter = new SimpleAdapter(this, listarGastos(),
                R.layout.lista_gasto, de, para);

        adapter.setViewBinder((SimpleAdapter.ViewBinder) new GastoViewBinder());

        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        registerForContextMenu(getListView());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Map<String, Object> map = gastos.get(position);
        String descricao = (String) map.get("descricao");
        Toast.makeText(this, "Gasto selecionada: " + descricao,
                Toast.LENGTH_SHORT).show();
    }

    private List<Map<String, Object>> listarGastos() {

        gastos = new ArrayList<Map<String, Object>>();

        List<Gasto> gastosDB = dao.listarGastos(viagem);

        for(Gasto gasto: gastosDB)
        {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("gastoId", gasto.getId());
            item.put("data", dateFormat.format(gasto.getData()));
            item.put("descricao", gasto.getDescricao());
            item.put("valor", gasto.getValor());
            item.put("categoria", ObtemCorCategoria(gasto));
            gastos.add(item);
        }

        return gastos;
    }

    private Object ObtemCorCategoria(Gasto gasto)
    {
        switch (gasto.getCategoria())
        {
            case "Alimentacao":
                return R.color.categoria_alimentacao;
            case "Hospedagem":
                return R.color.categoria_hospedagem;
            case "Transporte":
                return R.color.categoria_transporte;
            default:
                return R.color.categoria_outros;
        }
    }

    private class GastoViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {

            if (view.getId() == R.id.data) {
                if (!dataAnterior.equals(data)) {
                    TextView textView = (TextView) view;
                    textView.setText(textRepresentation);
                    dataAnterior = textRepresentation;
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                return true;
            }

            if (view.getId() == R.id.categoria) {
                Integer id = (Integer) data;
                view.setBackgroundColor(getResources().getColor(id));
                return true;
            }
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        if (item.getItemId() == R.id.remover) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();

            Long gastoId = Long.parseLong(gastos.get(info.position).get("gastoId").toString());
            gastos.remove(info.position);

            getListView().invalidateViews();
            dataAnterior = "";

            dao.removerGasto(gastoId);

            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }

}