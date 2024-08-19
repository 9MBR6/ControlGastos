package es.dtgz.controlgastos.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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
import es.dtgz.controlgastos.utils.ColorUtils;

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
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
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
                filterAndUpdateTable(view);
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
            filterAndUpdateTable(getView());
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void filterAndUpdateTable(View view) {
        gastoViewModel.getAllGastos().observe(getViewLifecycleOwner(), gastos -> {
            if (gastos != null) {
                List<Gasto> filteredGastos = filterGastosByDate(gastos);
                updateTable(filteredGastos, view);
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
                case "Imprevisto":
                    imprevistos += gasto.getCantidad();
                    break;
                case "Mensual":
                    internetMovil += gasto.getCantidad();
                    break;
            }
        }

        // Obtén los valores ajustados desde SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GastosPreferences", Context.MODE_PRIVATE);
        float valorAjusteAhorros = sharedPreferences.getFloat("ahorros", 0);
        float valorAjusteFormacion = sharedPreferences.getFloat("formacion", 0);
        float valorAjusteOcio = sharedPreferences.getFloat("ocio", 0);
        float valorAjusteComida = sharedPreferences.getFloat("comida", 0);
        float valorAjusteGasolina = sharedPreferences.getFloat("gasolina", 0);
        float valorAjusteImprevistos = sharedPreferences.getFloat("imprevistos", 0);
        float valorAjusteInternetMovil = sharedPreferences.getFloat("internet_movil", 0);
        float valorAjusteSueldo = sharedPreferences.getFloat("sueldo", 0);


        updateTextViewMAX(view, R.id.ahorros_max, valorAjusteAhorros);
        updateTextViewMAX(view, R.id.formacion_max, valorAjusteFormacion);
        updateTextViewMAX(view, R.id.ocio_max, valorAjusteOcio);
        updateTextViewMAX(view, R.id.comida_max, valorAjusteComida);
        updateTextViewMAX(view, R.id.gasolina_max, valorAjusteGasolina);
        updateTextViewMAX(view, R.id.imprevistos_max, valorAjusteImprevistos);
        updateTextViewMAX(view, R.id.internet_movil_max, valorAjusteInternetMovil);

        updateTextViewRESTANTE(view, R.id.ahorros_restante, valorAjusteAhorros, ahorros);
        updateTextViewRESTANTE(view, R.id.formacion_restante, valorAjusteFormacion, formacion);
        updateTextViewRESTANTE(view, R.id.ocio_restante, valorAjusteOcio, ocio);
        updateTextViewRESTANTE(view, R.id.comida_restante, valorAjusteComida, comida);
        updateTextViewRESTANTE(view, R.id.gasolina_restante, valorAjusteGasolina, gasolina);
        updateTextViewRESTANTE(view, R.id.imprevistos_restante, valorAjusteImprevistos, imprevistos);
        updateTextViewRESTANTE(view, R.id.internet_movil_restante, valorAjusteInternetMovil, internetMovil);

        // Actualiza los TextView con los valores y colores
        updateTextViewColor(view, R.id.ahorros_amount, ahorros, valorAjusteAhorros, true);
        updateTextViewColor(view, R.id.formacion_amount, formacion, valorAjusteFormacion, false);
        updateTextViewColor(view, R.id.ocio_amount, ocio, valorAjusteOcio, false);
        updateTextViewColor(view, R.id.comida_amount, comida, valorAjusteComida, false);
        updateTextViewColor(view, R.id.gasolina_amount, gasolina, valorAjusteGasolina, false);
        updateTextViewColor(view, R.id.imprevistos_amount, imprevistos, valorAjusteImprevistos, false);
        updateTextViewColor(view, R.id.internet_movil_amount, internetMovil, valorAjusteInternetMovil, false);

        totalGastado(view, R.id.total_gastos,ahorros,formacion,ocio,comida,gasolina,imprevistos,internetMovil);
        sueldoRestante(view, R.id.sueldo_restante,valorAjusteSueldo,ahorros,formacion,ocio,comida,gasolina,imprevistos,internetMovil);
    }

    private void sueldoRestante(View view, int textViewId, double sueldo, double ahorros, double formacion, double ocio, double comida, double gasolina, double imprevistos, double internet_movil) {
        TextView textView = view.findViewById(textViewId);
        double totalGastado = ahorros + formacion + ocio + comida + gasolina + imprevistos + internet_movil;
        double sueldoRestante = sueldo - totalGastado;

        // Actualiza el texto del TextView con el valor del sueldo restante
        textView.setText(String.format("%.2f €", sueldoRestante));

        // Calcula el color basado en el sueldo restante
        int color;
        if (sueldo > 0) {
            color = ColorUtils.getColorBasedOnValueRESTANTE((float) sueldoRestante, (float) sueldo);
        } else {
            color = Color.GREEN; // Color por defecto si no hay sueldo o presupuesto definido
        }

        // Aplica el color al TextView
        textView.setTextColor(color);
    }

    private void totalGastado(View view, int textViewId, double ahorros, double formacion, double ocio, double comida, double gasolina, double imprevistos, double internet_movil) {
        TextView textView = view.findViewById(textViewId);
        double totalGastado = ahorros + formacion + ocio + comida + gasolina + imprevistos + internet_movil;

        // Obtén el valor del sueldo o presupuesto total desde SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GastosPreferences", Context.MODE_PRIVATE);
        float sueldo = sharedPreferences.getFloat("sueldo", 0);

        // Actualiza el texto del TextView con el valor total gastado
        textView.setText(String.format("%.2f €", totalGastado));

        // Calcula el color basado en el total gastado
        int color;
        if (sueldo > 0) {
            color = ColorUtils.getColorBasedOnValue((float) totalGastado, sueldo);
        } else {
            color = Color.GREEN; // Color por defecto si no hay sueldo o presupuesto definido
        }

        // Aplica el color al TextView
        textView.setTextColor(color);
    }

    private void updateTextViewMAX(View view, int textViewId, float targetValue) {
        TextView textView = view.findViewById(textViewId);
        textView.setText(String.format("%.2f €", targetValue));
    }

    private void updateTextViewRESTANTE(View view, int textViewId, float valorMaximo, double valorGastado) {
        TextView textView = view.findViewById(textViewId);
        double resultado = valorMaximo - valorGastado;
        textView.setText(String.format("%.2f €", resultado));

        // Calcula el color basado en el valor restante
        int color;
        if (valorMaximo > 0) {
            color = ColorUtils.getColorBasedOnValueRESTANTE((float) resultado, valorMaximo);
        } else {
            color = Color.GREEN;
        }
        textView.setTextColor(color);
    }

    private void updateTextViewColor(View view, int textViewId, double amount, float targetValue, boolean isAhorros) {
        TextView textView = view.findViewById(textViewId);
        textView.setText(String.format("%.2f €", amount));

        // Evita la división por cero y casos de valor objetivo no definido
        if (targetValue > 0) {
            // Calcula el color basado en el valor
            int color;
            if (isAhorros) {
                // Para "Ahorros", el color va de rojo a verde
                color = ColorUtils.getColorBasedOnValueAhorros((float) amount, targetValue);
            } else {
                // Para otras categorías, el color es verde por defecto si el monto es 0
                color = amount > 0 ? ColorUtils.getColorBasedOnValue((float) amount, targetValue) : Color.GREEN;
            }
            textView.setTextColor(color);
        } else {
            // Si el valor objetivo es 0 o negativo, usa un color predeterminado
            textView.setTextColor(Color.parseColor("#E0E0E0"));
        }
    }

    private void updateTextViewColorRestante(View view, int textViewId, double amount, float targetValue, boolean isAhorros) {
        TextView textView = view.findViewById(textViewId);
        textView.setText(String.format("%.2f €", amount));

        // Evita la división por cero y casos de valor objetivo no definido
        if (targetValue > 0) {
            // Calcula el color basado en el valor
            int color;
            if (isAhorros) {
                // Para "Ahorros", el color va de rojo a verde
                color = ColorUtils.getColorBasedOnValueAhorros((float) amount, targetValue);
            } else {
                // Para otras categorías, el color es verde por defecto si el monto es 0
                color = amount > 0 ? ColorUtils.getColorBasedOnValue((float) amount, targetValue) : Color.GREEN;
            }
            textView.setTextColor(color);
        } else {
            // Si el valor objetivo es 0 o negativo, usa un color predeterminado
            textView.setTextColor(Color.parseColor("#E0E0E0"));
        }
    }


    private void loadDatesFromPreferences() {
        SharedPreferences preferences = getContext().getSharedPreferences("GastosPreferences", Context.MODE_PRIVATE);
        String fechaDesde = preferences.getString("fecha_desde", sdf.format(calendarDesde.getTime()));
        String fechaHasta = preferences.getString("fecha_hasta", sdf.format(calendarHasta.getTime()));

        try {
            calendarDesde.setTime(sdf.parse(fechaDesde));
            calendarHasta.setTime(sdf.parse(fechaHasta));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ((Button) getView().findViewById(R.id.btn_fecha_desde)).setText(sdf.format(calendarDesde.getTime()));
        ((Button) getView().findViewById(R.id.btn_fecha_hasta)).setText(sdf.format(calendarHasta.getTime()));
    }

    private void saveDatesToPreferences(Context context, Calendar desde, Calendar hasta) {
        SharedPreferences preferences = context.getSharedPreferences("GastosPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("fecha_desde", sdf.format(desde.getTime()));
        editor.putString("fecha_hasta", sdf.format(hasta.getTime()));
        editor.apply();
    }
}
