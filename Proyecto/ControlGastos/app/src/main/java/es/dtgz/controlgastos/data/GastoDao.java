package es.dtgz.controlgastos.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GastoDao {

    @Query("SELECT * FROM gastos")
    LiveData<List<Gasto>> getAll();

    @Query("SELECT * FROM gastos WHERE id = :id")
    LiveData<Gasto> getGastoById(long id);

    @Insert
    void insert(Gasto gasto);

    @Update
    void update(Gasto gasto); // Método para actualizar un gasto

    @Delete
    void delete(Gasto gasto);

    @Query("DELETE FROM gastos WHERE id = :gastoId")
    void deleteGastoById(long gastoId);
}
