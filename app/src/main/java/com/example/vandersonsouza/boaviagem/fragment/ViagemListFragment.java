package com.example.vandersonsouza.boaviagem.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import com.example.vandersonsouza.boaviagem.AnotacaoListener;
import com.example.vandersonsouza.boaviagem.Constantes;
import com.example.vandersonsouza.boaviagem.DatabaseHelper;
import com.example.vandersonsouza.boaviagem.provider.BoaViagemContract;

public class ViagemListFragment extends ListFragment
        implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private AnotacaoListener callback;
    private SimpleCursorAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[] { DatabaseHelper.Viagem.DESTINO },
                new int[] { android.R.id.text1 }, 0);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position,
                            long id) {
        long viagem = getListAdapter().getItemId(position);
        Bundle bundle = new Bundle();
        bundle.putLong(Constantes.VIAGEM_SELECIONADA, viagem);
        callback.viagemSelecionada(bundle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (AnotacaoListener) activity;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = BoaViagemContract.Viagem.CONTENT_URI;
        String[] projection = new String[]{DatabaseHelper.Viagem._ID, DatabaseHelper.Viagem.DESTINO};
        return new CursorLoader(getActivity(), uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
