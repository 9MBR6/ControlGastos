package es.dtgz.controlgastos.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dtgz.controlgastos.R;
import es.dtgz.controlgastos.data.Gasto;

public class HomeFragment extends Fragment {

    private GastoViewModel gastoViewModel;
    private Calendar calendarDesde = Calendar.getInstance();
    private Calendar calendarHasta = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);

        // Configura el FloatingActionButton
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Fragment fragment = new AddGastoFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Configura los botones de selección de fechas
        Button btnFechaDesde = view.findViewById(R.id.btn_fecha_desde);
        Button btnFechaHasta = view.findViewById(R.id.btn_fecha_hasta);

        btnFechaDesde.setOnClickListener(v -> showDatePickerDialog(true));
        btnFechaHasta.setOnClickListener(v -> showDatePickerDialog(false));

        // Recupera las fechas almacenadas y actualiza los botones
        loadDatesFromPreferences();

        // Configura el observador del ViewModel para actualizar la UI
        setupObservers(view);
    }

    private void setupObservers(View view) {
        gastoViewModel.getAllGastos().observe(getViewLifecycleOwner(), gastos -> {
            if (gastos != null) {
                // Actualiza la tabla con los datos obtenidos
                filterAndUpdateTable();
            } else {
                Toast.makeText(getContext(), "No hay datos disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(boolean isDesde) {
        Calendar calendar = isDesde ? calendarDesde : calendarHasta;
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            String dateStr = sdf.format(selectedDate.getTime());

            if (isDesde) {
                calendarDesde = selectedDate;
                ((Button) getView().findViewById(R.id.btn_fecha_desde)).setText(dateStr);
            } else {
                calendarHasta = selectedDate;
                ((Button) getView().findViewById(R.id.btn_fecha_hasta)).setText(dateStr);
            }

            // Guardar las fechas seleccionadas en SharedPreferences
            saveDatesToPreferences(getContext(), calendarDesde, calendarHasta);

            // Actualiza la tabla con los datos filtrados
            filterAndUpdateTable();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void filterAndUpdateTable() {
        gastoViewModel.getAllGastos().observe(getViewLifecycleOwner(), gastos -> {
            if (gastos != null) {
                List<Gasto> filteredGastos = filterGastosByDate(gastos);
                updateTable(filteredGastos, getView());
            } else {
                Toast.makeText(getContext(), "No hay datos disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Gasto> filterGastosByDate(List<Gasto> gastos) {
        // Define el formato de fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Convierte las fechas desde y hasta a Calendar
        Calendar desde = Calendar.getInstance();
        Calendar hasta = Calendar.getInstance();

        try {
            desde.setTime(sdf.parse(sdf.format(calendarDesde.getTime())));
            hasta.setTime(sdf.parse(sdf.format(calendarHasta.getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Filtra los gastos que están dentro del rango de fechas
        List<Gasto> filteredGastos = new ArrayList<>();
        for (Gasto gasto : gastos) {
            Calendar fechaGasto = Calendar.getInstance();
            try {
                fechaGasto.setTime(sdf.parse(gasto.getFecha()));
            } catch (ParseException e) {
                e.printStackTrace();
                continue; // Si no se puede parsear la fecha, saltamos este gasto
            }

            if ((fechaGasto.after(desde) || fechaGasto.equals(desde)) &&
                    (fechaGasto.before(hasta) || fechaGasto.equals(hasta))) {
                filteredGastos.add(gasto);
            }
        }

        return filteredGastos;
    }

    private void updateTable(List<Gasto> gastos, View view) {
        double ahorros = 0, formacion = 0, ocio = 0, comida = 0, gasolina = 0, imprevistos = 0, internetMovil = 0;

        for (Gasto gasto : gastos) {
            switch (gasto.getCategoria()) {
                case "Ahorros":
                    ahorros += gasto.getCantidad();
                    break;
                case "Formación":
                    formacion += gasto.getCantidad();
                    break;
                case "Ocio":
                    ocio += gasto.getCantidad();
                    break;
                case "Comida":
                    comida += gasto.getCantidad();
                    break;
                case "Gasolina":
                    gasolina += gasto.getCantidad();
                    break;
                case "Imprevistos":
                    imprevistos += gasto.getCantidad();
                    break;
                case "Internet+Móvil":
                    internetMovil += gasto.getCantidad();
                    break;
            }
        }

        ((TextView) view.findViewById(R.id.ahorros_amount)).setText(String.format("%.2f €", ahorros));
        ((TextView) view.findViewById(R.id.formacion_amount)).setText(String.format("%.2f €", formacion));
        ((TextView) view.findViewById(R.id.ocio_amount)).setText(String.format("%.2f €", ocio));
        ((TextView) view.findViewById(R.id.comida_amount)).setText(String.format("%.2f €", comida));
        ((TextView) view.findViewById(R.id.gasolina_amount)).setText(String.format("%.2f €", gasolina));
        ((TextView) view.findViewById(R.id.imprevistos_amount)).setText(String.format("%.2f €", imprevistos));
        ((TextView) view.findViewById(R.id.internet_movil_amount)).setText(String.format("%.2f €", internetMovil));
    }

    private void saveDatesToPreferences(Context context, Calendar fechaDesde, Calendar fechaHasta) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GastosPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editor.putString("fechaDesde", sdf.format(fechaDesde.getTime()));
        editor.putString("fechaHasta", sdf.format(fechaHasta.getTime()));
        editor.apply(); // Guarda los cambios
    }

    private void loadDatesFromPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GastosPrefs", Context.MODE_PRIVATE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String fechaDesdeStr = sharedPreferences.getString("fechaDesde", null);
        String fechaHastaStr = sharedPreferences.getString("fechaHasta", null);

        try {
            if (fechaDesdeStr != null) {
                calendarDesde.setTime(sdf.parse(fechaDesdeStr));
            }
            if (fechaHastaStr != null) {
                calendarHasta.setTime(sdf.parse(fechaHastaStr));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ((Button) getView().findViewById(R.id.btn_fecha_desde)).setText(sdf.format(calendarDesde.getTime()));
        ((Button) getView().findViewById(R.id.btn_fecha_hasta)).setText(sdf.format(calendarHasta.getTime()));
    }
}
