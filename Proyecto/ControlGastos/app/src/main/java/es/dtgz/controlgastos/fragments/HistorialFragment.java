package es.dtgz.controlgastos.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;

import es.dtgz.controlgastos.R;
import es.dtgz.controlgastos.adapters.GastoAdapter;
import es.dtgz.controlgastos.data.Gasto;

public class HistorialFragment extends Fragment {

    private GastoViewModel gastoViewModel;
    private RecyclerView recyclerView;
    private GastoAdapter gastoAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_gastos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);
        gastoViewModel.getAllGastos().observe(getViewLifecycleOwner(), gastos -> {
            if (gastos != null) {
                // Ordena los gastos de más nuevo a más viejo
                Collections.sort(gastos, new Comparator<Gasto>() {
                    @Override
                    public int compare(Gasto o1, Gasto o2) {
                        return o2.getFecha().compareTo(o1.getFecha());
                    }
                });

                gastoAdapter = new GastoAdapter(gastos, gasto -> {
                    // Maneja el clic en el ítem
                    openEditGastoFragment(gasto);
                });
                recyclerView.setAdapter(gastoAdapter);
            }
        });
    }

    private void openEditGastoFragment(Gasto gasto) {
        Bundle bundle = new Bundle();
        bundle.putLong("gastoId", gasto.getId());

        EditGastoFragment editGastoFragment = new EditGastoFragment();
        editGastoFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editGastoFragment)
                .addToBackStack(null)
                .commit();
    }
}
