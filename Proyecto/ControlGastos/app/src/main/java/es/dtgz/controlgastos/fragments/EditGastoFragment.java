package es.dtgz.controlgastos.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import es.dtgz.controlgastos.R;
import es.dtgz.controlgastos.data.Gasto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditGastoFragment extends Fragment {

    private EditText etNombre;
    private EditText etCantidad;
    private EditText etFecha;
    private Spinner spinnerCategoria;
    private Button btnGuardar;
    private Button btnEliminar;
    private GastoViewModel gastoViewModel;

    private long gastoId;
    private Calendar calendarFecha = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_gasto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa las vistas
        etNombre = view.findViewById(R.id.et_nombre_gasto_edit);
        etCantidad = view.findViewById(R.id.et_cantidad_gasto_edit);
        etFecha = view.findViewById(R.id.et_fecha_gasto_edit);
        spinnerCategoria = view.findViewById(R.id.spinner_categoria_edit);
        btnGuardar = view.findViewById(R.id.btn_editar_gasto);
        btnEliminar = view.findViewById(R.id.btn_eliminar_gasto);

        gastoViewModel = new ViewModelProvider(requireActivity()).get(GastoViewModel.class);

        // Configura el Spinner con las categorías
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.categorias_gastos, // Asegúrate de tener esta array en `res/values/strings.xml`
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        // Obtén el ID del gasto desde los argumentos del fragmento
        if (getArguments() != null) {
            gastoId = getArguments().getLong("gastoId", -1);
            if (gastoId != -1) {
                gastoViewModel.getGastoById(gastoId).observe(getViewLifecycleOwner(), gasto -> {
                    if (gasto != null) {
                        etNombre.setText(gasto.getNombre());
                        etCantidad.setText(String.valueOf(gasto.getCantidad()));
                        etFecha.setText(gasto.getFecha());
                        // Selecciona la categoría correcta en el Spinner
                        selectSpinnerCategory(gasto.getCategoria());
                    }
                });
            }
        }

        etFecha.setOnClickListener(v -> showDatePickerDialog());

        btnGuardar.setOnClickListener(v -> {
            if (validateInputs()) {
                Gasto gasto = new Gasto();
                gasto.setId(gastoId);
                gasto.setNombre(etNombre.getText().toString());
                gasto.setCantidad(Double.parseDouble(etCantidad.getText().toString()));
                gasto.setFecha(etFecha.getText().toString());
                gasto.setCategoria(spinnerCategoria.getSelectedItem().toString());
                gastoViewModel.update(gasto); // Método para actualizar el gasto
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnEliminar.setOnClickListener(v -> {
            if (gastoId != -1) {
                gastoViewModel.deleteGastoById(gastoId); // Método para eliminar el gasto
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void selectSpinnerCategory(String category) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerCategoria.getAdapter();
        int position = adapter.getPosition(category);
        spinnerCategoria.setSelection(position >= 0 ? position : 0); // Selecciona la categoría o la primera opción si no se encuentra
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etNombre.getText())) {
            etNombre.setError("Campo requerido");
            return false;
        }
        if (TextUtils.isEmpty(etCantidad.getText())) {
            etCantidad.setError("Campo requerido");
            return false;
        }
        if (TextUtils.isEmpty(etFecha.getText())) {
            etFecha.setError("Campo requerido");
            return false;
        }
        return true;
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendarFecha.set(year, month, dayOfMonth);
                    etFecha.setText(sdf.format(calendarFecha.getTime()));
                },
                calendarFecha.get(Calendar.YEAR),
                calendarFecha.get(Calendar.MONTH),
                calendarFecha.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
