package com.example.vandersonsouza.boaviagem.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.vandersonsouza.boaviagem.DatabaseHelper;

import static com.example.vandersonsouza.boaviagem.provider.BoaViagemContract.AUTHORITY;
import static com.example.vandersonsouza.boaviagem.provider.BoaViagemContract.GASTO_PATH;
import static com.example.vandersonsouza.boaviagem.provider.BoaViagemContract.VIAGEM_PATH;

public class BoaViagemProvider extends ContentProvider {

    private DatabaseHelper helper;

    private static final int VIAGENS = 1;
    private static final int VIAGEM_ID = 2;
    private static final int GASTOS = 3;
    private static final int GASTO_ID = 4;
    private static final int GASTOS_VIAGEM_ID = 5;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        uriMatcher.addURI(AUTHORITY,
                VIAGEM_PATH,
                VIAGENS);

        uriMatcher.addURI(AUTHORITY,
                VIAGEM_PATH + "/#",
                VIAGEM_ID);

        uriMatcher.addURI(AUTHORITY,
                GASTO_PATH,
                GASTOS);

        uriMatcher.addURI(AUTHORITY,
                GASTO_PATH + "/#",
                GASTO_ID);

        uriMatcher.addURI(AUTHORITY,
                GASTO_PATH + "/"+ VIAGEM_PATH + "/#",
                GASTOS_VIAGEM_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = helper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case VIAGENS:

                return database.query(VIAGEM_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);

            case VIAGEM_ID:

                selection = BoaViagemContract.Viagem._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.query(VIAGEM_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);

            case GASTOS:

                return database.query(GASTO_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);

            case GASTO_ID:

                selection = BoaViagemContract.Gasto._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.query(GASTO_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);

            case GASTOS_VIAGEM_ID:

                selection = BoaViagemContract.Gasto.VIAGEM_ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.query(GASTO_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);

            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase database = helper.getWritableDatabase();
        long id;

        switch (uriMatcher.match(uri)) {

            case VIAGENS:
                id = database.insert(VIAGEM_PATH, null, values);
                return Uri.withAppendedPath(BoaViagemContract.Viagem.CONTENT_URI,
                        String.valueOf(id));

            case GASTOS:
                id = database.insert(GASTO_PATH, null, values);
                return Uri.withAppendedPath(BoaViagemContract.Gasto.CONTENT_URI,
                        String.valueOf(id));
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = helper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case VIAGEM_ID:

                selection = BoaViagemContract.Viagem._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.delete(VIAGEM_PATH,
                        selection, selectionArgs);

            case GASTO_ID:

                selection = BoaViagemContract.Gasto._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.delete(GASTO_PATH,
                        selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        SQLiteDatabase database = helper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case VIAGEM_ID:

                selection = BoaViagemContract.Viagem._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.update(VIAGEM_PATH, values,
                        selection, selectionArgs);

            case GASTO_ID:

                selection = BoaViagemContract.Gasto._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.update(GASTO_PATH, values,
                        selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {

            case VIAGENS:

                return BoaViagemContract.Viagem.CONTENT_TYPE;

            case VIAGEM_ID:

                return BoaViagemContract.Viagem.CONTENT_ITEM_TYPE;

            case GASTOS:
            case GASTOS_VIAGEM_ID:

                return BoaViagemContract.Gasto.CONTENT_TYPE;

            case GASTO_ID:

                return BoaViagemContract.Gasto.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }
}
