package es.dtgz.controlgastos.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import es.dtgz.controlgastos.R;

public class AjustesFragment extends Fragment {

    private EditText etAhorros;
    private EditText etFormacion;
    private EditText etOcio;
    private EditText etComida;
    private EditText etGasolina;
    private EditText etImprevistos;
    private EditText etInternetMovil;
    private EditText etSueldo;
    private Button btnGuardar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ajustes, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa los campos
        etAhorros = view.findViewById(R.id.et_ahorros);
        etFormacion = view.findViewById(R.id.et_formacion);
        etOcio = view.findViewById(R.id.et_ocio);
        etComida = view.findViewById(R.id.et_comida);
        etGasolina = view.findViewById(R.id.et_gasolina);
        etImprevistos = view.findViewById(R.id.et_imprevistos);
        etInternetMovil = view.findViewById(R.id.et_internet_movil);
        etSueldo = view.findViewById(R.id.et_sueldo);
        btnGuardar = view.findViewById(R.id.btn_guardar);

        etAhorros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAhorros.setText("");
            }
        });
        etFormacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFormacion.setText("");
            }
        });
        etOcio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etOcio.setText("");
            }
        });
        etComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etComida.setText("");
            }
        });
        etGasolina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etGasolina.setText("");
            }
        });
        etImprevistos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etImprevistos.setText("");
            }
        });
        etInternetMovil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etInternetMovil.setText("");
            }
        });
        etSueldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSueldo.setText("");
            }
        });

        // Cargar los valores guardados al inicio
        loadValues();

        btnGuardar.setOnClickListener(v -> {
            if (validateInputs()) {
                saveValues();
                // Opcional: Navegar hacia el fragmento anterior o mostrar un mensaje
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    private void saveValues() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GastosPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat("ahorros", parseFloat(etAhorros.getText().toString()));
        editor.putFloat("formacion", parseFloat(etFormacion.getText().toString()));
        editor.putFloat("ocio", parseFloat(etOcio.getText().toString()));
        editor.putFloat("comida", parseFloat(etComida.getText().toString()));
        editor.putFloat("gasolina", parseFloat(etGasolina.getText().toString()));
        editor.putFloat("imprevistos", parseFloat(etImprevistos.getText().toString()));
        editor.putFloat("internet_movil", parseFloat(etInternetMovil.getText().toString()));
        editor.putFloat("sueldo", parseFloat(etSueldo.getText().toString()));

        editor.apply();
    }

    private void loadValues() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GastosPreferences", Context.MODE_PRIVATE);

        etAhorros.setText(String.valueOf(sharedPreferences.getFloat("ahorros", 0)));
        etFormacion.setText(String.valueOf(sharedPreferences.getFloat("formacion", 0)));
        etOcio.setText(String.valueOf(sharedPreferences.getFloat("ocio", 0)));
        etComida.setText(String.valueOf(sharedPreferences.getFloat("comida", 0)));
        etGasolina.setText(String.valueOf(sharedPreferences.getFloat("gasolina", 0)));
        etImprevistos.setText(String.valueOf(sharedPreferences.getFloat("imprevistos", 0)));
        etInternetMovil.setText(String.valueOf(sharedPreferences.getFloat("internet_movil", 0)));
        etSueldo.setText(String.valueOf(sharedPreferences.getFloat("sueldo", 0)));
    }

    private boolean validateInputs() {
        return !TextUtils.isEmpty(etAhorros.getText()) &&
                !TextUtils.isEmpty(etFormacion.getText()) &&
                !TextUtils.isEmpty(etOcio.getText()) &&
                !TextUtils.isEmpty(etComida.getText()) &&
                !TextUtils.isEmpty(etGasolina.getText()) &&
                !TextUtils.isEmpty(etImprevistos.getText()) &&
                !TextUtils.isEmpty(etInternetMovil.getText()) &&
                !TextUtils.isEmpty(etSueldo.getText());
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
