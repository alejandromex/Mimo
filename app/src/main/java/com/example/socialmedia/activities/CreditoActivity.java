package com.example.socialmedia.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.models.Credito;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.CreditoProvider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.zip.Inflater;

import static java.lang.Integer.parseInt;

public class CreditoActivity extends AppCompatActivity {

    ImageView btnBackCredito;
    View mActionBarView;
    Spinner spinner;
    Spinner spinnerInstitucion;
    Button btnRegistrarCredito;
    TextInputEditText txtCantidadCredito;

    CreditoProvider creditoProvider;
    AuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito);

        spinner = findViewById(R.id.spinnerTipo);
        spinnerInstitucion = findViewById(R.id.spinnerBanco);
        btnRegistrarCredito = findViewById(R.id.btnRegistrarCredito);
        txtCantidadCredito = findViewById(R.id.txtCantidadCredito);

        creditoProvider = new CreditoProvider();
        authProvider = new AuthProvider();

        btnRegistrarCredito.setOnClickListener(v ->{
            createCredito();
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipoCredito, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
        showCustomToolBar(R.layout.tool_bar_main);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = spinner.getSelectedItem().toString();
                if(text.equals("Bancos"))
                {
                    ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(CreditoActivity.this, R.array.institucionesBanco,
                            android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerInstitucion.setAdapter(adapterType);
                }
                else if(text.equals("Departamental"))
                {
                    ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(CreditoActivity.this, R.array.departamentos,
                            android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerInstitucion.setAdapter(adapterType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void createCredito()
    {
        int cantidad = parseInt(txtCantidadCredito.getText().toString());
        String institucion = spinnerInstitucion.getSelectedItem().toString();
        String tipo = spinner.getSelectedItem().toString();

        if(cantidad > 0 && !institucion.isEmpty() && !tipo.isEmpty())
        {
            Credito credito = new Credito();
            credito.setCantidad(cantidad);
            credito.setInstitucion(institucion);
            credito.setTipo(tipo);
            credito.setIdUser(authProvider.GetUid());
            credito.setTarjeta(1);
            
            creditoProvider.save(credito).addOnCompleteListener(saveTask ->{
                if(saveTask.isSuccessful())
                {
                    Toast.makeText(this, "Credito Registrado", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "Error al registrar el credito", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }


    private void showCustomToolBar(int resource)
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);
        btnBackCredito = mActionBarView.findViewById(R.id.btnBackCredito);

        btnBackCredito.setOnClickListener(v ->{
            finish();
        });


    }


}