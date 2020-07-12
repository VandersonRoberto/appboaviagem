package com.example.vandersonsouza.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String MANTER_CONECTADO = "manter_conectado";
    private EditText usuario;
    private EditText senha;
    private CheckBox manterConectado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        usuario = (EditText) findViewById(R.id.usuario);
        senha = (EditText) findViewById(R.id.senha);
        manterConectado = (CheckBox) findViewById(R.id.manterConectado);

        SharedPreferences preferencias = getPreferences(MODE_PRIVATE);
        boolean conectado = preferencias.getBoolean(MANTER_CONECTADO, false);

        if(conectado){
            startActivity(new Intent(this, DashboardActivity.class));
        }

    }

    public void entrarOnClick(View v) {
        String usuarioInformado = usuario.getText().toString();
        String senhaInformada = senha.getText().toString();
        if ("leitor".equals(usuarioInformado) &&
                "123".equals(senhaInformada)) {

            SharedPreferences preferencias = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putBoolean(MANTER_CONECTADO, manterConectado.isChecked());
            editor.commit();

           Intent intent = new Intent(this,DashboardActivity.class);
           startActivity(intent);
        } else {
            String mensagemErro = getString(R.string.erro_autenticacao);
            Toast toast = Toast.makeText(this, mensagemErro,
                    Toast.LENGTH_SHORT);

            toast.show();
        }
    }
}
