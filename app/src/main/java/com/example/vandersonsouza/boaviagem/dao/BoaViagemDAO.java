package com.example.vandersonsouza.boaviagem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vandersonsouza.boaviagem.DatabaseHelper;
import com.example.vandersonsouza.boaviagem.domain.Gasto;
import com.example.vandersonsouza.boaviagem.domain.Viagem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BoaViagemDAO {

    private DatabaseHelper helper;

    private SQLiteDatabase db;

    public BoaViagemDAO(Context context){
        helper = new DatabaseHelper(context);
    }

    private SQLiteDatabase getDb() {
        if (db == null) {
            db = helper.getWritableDatabase();
        }
        return db;
    }

    public void close(){
        helper.close();
    }

    public List<Viagem> listarViagens(){
        Cursor cursor = getDb().query(DatabaseHelper.Viagem.TABELA,
                DatabaseHelper.Viagem.COLUNAS,
                null, null, null, null, null);
        List<Viagem> viagens = new ArrayList<Viagem>();
        while(cursor.moveToNext()){
            Viagem viagem = criarViagem(cursor);
            viagens.add(viagem);
        }
        cursor.close();
        return viagens;
    }

    public Viagem buscarViagemPorId(Integer id){
        Cursor cursor = getDb().query(DatabaseHelper.Viagem.TABELA,
                DatabaseHelper.Viagem.COLUNAS,
                DatabaseHelper.Viagem._ID + " = ?",
                new String[]{ id.toString() },
                null, null, null);
        if(cursor.moveToNext()){
            Viagem viagem = criarViagem(cursor);
            cursor.close();
            return viagem;
        }

        return null;
    }

    public Viagem buscarViagemPorDestino(String destino){
        Cursor cursor = getDb().query(DatabaseHelper.Viagem.TABELA,
                                DatabaseHelper.Viagem.COLUNAS,
                                DatabaseHelper.Viagem.DESTINO + " = ?",
                                 new String[] {destino},
                null,null,null);

        if(cursor.moveToNext()){
            Viagem viagem = criarViagem(cursor);
            cursor.close();
            return viagem;
        }

        return null;
    }

    public long salvarViagem(Viagem viagem){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Viagem.DESTINO,
                viagem.getDestino());

        values.put(DatabaseHelper.Viagem.TIPO_VIAGEM,
                viagem.getTipoViagem());

        values.put(DatabaseHelper.Viagem.DATA_CHEGADA,
                viagem.getDataChegada().getTime());

        values.put(DatabaseHelper.Viagem.DATA_SAIDA,
                viagem.getDataSaida().getTime());

        values.put(DatabaseHelper.Viagem.ORCAMENTO,
                viagem.getOrcamento());

        values.put(DatabaseHelper.Viagem.QUANTIDADE_PESSOAS,
                viagem.getQuantidadePessoas());

        if(viagem.getId() == null)
            return getDb().insert(DatabaseHelper.Viagem.TABELA,
                null, values);
        else
            return getDb().update(DatabaseHelper.Viagem.TABELA, values, "_ID = ?", new String[]{ viagem.getId().toString() });
    }

    public boolean removerViagem(Long id){
        String whereClause = DatabaseHelper.Viagem._ID + " = ?";
        String[] whereArgs = new String[]{id.toString()};
        int removidos = getDb().delete(DatabaseHelper.Viagem.TABELA,
                whereClause, whereArgs);
        return removidos > 0;
    }

    public List<Gasto> listarGastos(Viagem viagem){
        String selection = DatabaseHelper.Gasto.VIAGEM_ID + " = ?";
        String[] selectionArgs = new String[]{viagem.getId().toString()};

        Cursor cursor = getDb().query(DatabaseHelper.Gasto.TABELA,
                DatabaseHelper.Gasto.COLUNAS,
                selection, selectionArgs,
                null, null, DatabaseHelper.Gasto.DATA + " DESC");
        List<Gasto> gastos = new ArrayList<Gasto>();
        while(cursor.moveToNext()){
            Gasto gasto = criarGasto(cursor);
            gastos.add(gasto);
        }
        cursor.close();
        return gastos;
    }

    public Gasto buscarGastoPorId(Integer id){
        Cursor cursor = getDb().query(DatabaseHelper.Gasto.TABELA,
                DatabaseHelper.Gasto.COLUNAS,
                DatabaseHelper.Gasto._ID + " = ?",
                new String[]{ id.toString() },
                null, null, null);
        if(cursor.moveToNext()){
            Gasto gasto = criarGasto(cursor);
            cursor.close();
            return gasto;
        }
        return null;
    }

    public long salvarGasto(Gasto gasto){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Gasto.CATEGORIA,
                gasto.getCategoria());

        values.put(DatabaseHelper.Gasto.DATA,
                gasto.getData().getTime());

        values.put(DatabaseHelper.Gasto.DESCRICAO,
                gasto.getDescricao());

        values.put(DatabaseHelper.Gasto.LOCAL,
                gasto.getLocal());

        values.put(DatabaseHelper.Gasto.VALOR,
                gasto.getValor());

        values.put(DatabaseHelper.Gasto.VIAGEM_ID,
                gasto.getViagemId());

        if(gasto.getId() == null)
            return getDb().insert(DatabaseHelper.Gasto.TABELA,
                    null, values);
        else
            return getDb().update(DatabaseHelper.Gasto.TABELA, values, "_ID = ?", new String[]{ gasto.getId().toString() });

    }

    public boolean removerGasto(Long id){
        String whereClause = DatabaseHelper.Gasto._ID + " = ?";
        String[] whereArgs = new String[]{id.toString()};
        int removidos = getDb().delete(DatabaseHelper.Gasto.TABELA,
                whereClause, whereArgs);
        return removidos > 0;
    }

    public double calcularTotalGasto(Viagem viagem){
        Cursor cursor = getDb().rawQuery(
                "SELECT SUM("+DatabaseHelper.Gasto.VALOR + ") FROM " +
                        DatabaseHelper.Gasto.TABELA + " WHERE " +
                        DatabaseHelper.Gasto.VIAGEM_ID + " = ?",
                new String[]{ viagem.getId().toString() });
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    private Viagem criarViagem(Cursor cursor) {
        Viagem viagem = new Viagem(

                cursor.getInt(cursor.getColumnIndex(
                        DatabaseHelper.Viagem._ID)),

                cursor.getString(cursor.getColumnIndex(
                        DatabaseHelper.Viagem.DESTINO)),

                cursor.getInt(cursor.getColumnIndex(
                        DatabaseHelper.Viagem.TIPO_VIAGEM)),

                new Date(cursor.getLong(cursor.getColumnIndex(
                        DatabaseHelper.Viagem.DATA_CHEGADA))),

                new Date(cursor.getLong(cursor.getColumnIndex(
                        DatabaseHelper.Viagem.DATA_SAIDA))),

                cursor.getDouble(cursor.getColumnIndex(
                        DatabaseHelper.Viagem.ORCAMENTO)),

                cursor.getInt(cursor.getColumnIndex(
                        DatabaseHelper.Viagem.QUANTIDADE_PESSOAS))
        );
        return viagem;
    }

    private Gasto criarGasto(Cursor cursor) {
        Gasto gasto = new Gasto(
                cursor.getInt(cursor.getColumnIndex(
                        DatabaseHelper.Gasto._ID)),

                new Date(cursor.getLong(cursor.getColumnIndex(
                        DatabaseHelper.Gasto.DATA))),

                cursor.getString(cursor.getColumnIndex(
                        DatabaseHelper.Gasto.CATEGORIA)),

                cursor.getString(cursor.getColumnIndex(
                        DatabaseHelper.Gasto.DESCRICAO)),

                cursor.getDouble(cursor.getColumnIndex(
                        DatabaseHelper.Gasto.VALOR)),

                cursor.getString(cursor.getColumnIndex(
                        DatabaseHelper.Gasto.LOCAL)),

                cursor.getInt(cursor.getColumnIndex(
                        DatabaseHelper.Gasto.VIAGEM_ID))
        );
        return gasto;
    }
}
