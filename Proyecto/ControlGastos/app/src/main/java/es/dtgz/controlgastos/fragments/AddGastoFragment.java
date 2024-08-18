package es.dtgz.controlgastos.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import es.dtgz.controlgastos.R;
import es.dtgz.controlgastos.data.Gasto;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddGastoFragment extends Fragment {

    private Spinner spinnerCategoria;
    private EditText etNombreGasto;
    private EditText etCantidadGasto;
    private EditText etFechaGasto;
    private Button btnGuardarGasto;
    private Calendar calendar = Calendar.getInstance();
    private GastoViewModel gastoViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_gastos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa el ViewModel
        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);

        // Inicializa los elementos del layout
        spinnerCategoria = view.findViewById(R.id.spinner_categoria);
        etNombreGasto = view.findViewById(R.id.et_nombre_gasto);
        etCantidadGasto = view.findViewById(R.id.et_cantidad_gasto);
        etFechaGasto = view.findViewById(R.id.et_fecha_gasto);
        btnGuardarGasto = view.findViewById(R.id.btn_guardar_gasto);

        // Configura el DatePickerDialog para el campo de fecha
        etFechaGasto.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                updateLabel();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Configura el listener para el botón de guardar
        btnGuardarGasto.setOnClickListener(v -> {
            String categoria = spinnerCategoria.getSelectedItem().toString();
            String nombreGasto = etNombreGasto.getText().toString();
            String cantidadGastoStr = etCantidadGasto.getText().toString();
            String fechaGasto = etFechaGasto.getText().toString();

            if (nombreGasto.isEmpty() || cantidadGastoStr.isEmpty() || fechaGasto.isEmpty()) {
                // Muestra un mensaje de error si algún campo está vacío
                Toast.makeText(getContext(), "Por favor, rellene todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidadGasto;
            try {
                cantidadGasto = Double.parseDouble(cantidadGastoStr);
            } catch (NumberFormatException e) {
                // Muestra un mensaje de error si la cantidad no es un número válido
                Toast.makeText(getContext(), "La cantidad debe ser un número válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crea un nuevo objeto Gasto
            Gasto gasto = new Gasto();
            gasto.setCategoria(categoria);
            gasto.setNombre(nombreGasto);
            gasto.setCantidad(cantidadGasto);
            gasto.setFecha(fechaGasto);

            // Guarda el gasto en la base de datos
            gastoViewModel.insert(gasto);

            // Limpia los campos
            etNombreGasto.setText("");
            etCantidadGasto.setText("");
            etFechaGasto.setText("");

            // Navega de regreso a la pantalla principal
            navigateToHome();
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // Formato de fecha
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etFechaGasto.setText(sdf.format(calendar.getTime()));
    }

    private void navigateToHome() {
        // Obtén el FragmentManager y realiza la transacción para volver al fragmento de inicio
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment()) // Cambia `HomeFragment` al fragmento que deseas mostrar
                .commit();

        // Muestra el BottomNavigationView nuevamente si es necesario
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Mostrar el BottomNavigationView nuevamente cuando el fragmento se destruya
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}
