package es.dtgz.controlgastos.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class GastoRepository {

    private GastoDao gastoDao;
    private LiveData<List<Gasto>> allGastos;

    public GastoRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        gastoDao = database.gastoDao();
        allGastos = gastoDao.getAll(); // Asegúrate de que getAll() devuelve LiveData<List<Gasto>>
    }

    public void insert(Gasto gasto) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> gastoDao.insert(gasto));
    }

    public void update(Gasto gasto) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> gastoDao.update(gasto));
    }

    public LiveData<List<Gasto>> getAllGastos() {
        return allGastos;
    }

    public LiveData<Gasto> getGastoById(long id) {
        return gastoDao.getGastoById(id);
    }

    public void deleteGastoById(long gastoId) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> gastoDao.deleteGastoById(gastoId));
    }
}
