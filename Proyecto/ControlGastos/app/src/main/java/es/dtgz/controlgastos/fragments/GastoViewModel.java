package es.dtgz.controlgastos.fragments;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import es.dtgz.controlgastos.data.Gasto;
import es.dtgz.controlgastos.data.GastoRepository;

import java.util.List;

public class GastoViewModel extends AndroidViewModel {

    private GastoRepository repository;
    private LiveData<List<Gasto>> allGastos;

    public GastoViewModel(Application application) {
        super(application);
        repository = new GastoRepository(application);
        allGastos = repository.getAllGastos();
    }

    public void insert(Gasto gasto) {
        repository.insert(gasto);
    }

    public LiveData<List<Gasto>> getAllGastos() {
        return allGastos;
    }

    public LiveData<Gasto> getGastoById(long id) {
        return repository.getGastoById(id);
    }

    public void deleteGastoById(long gastoId) {
        repository.deleteGastoById(gastoId);
    }

    public void update(Gasto gasto) {
        repository.update(gasto);
    }
}
