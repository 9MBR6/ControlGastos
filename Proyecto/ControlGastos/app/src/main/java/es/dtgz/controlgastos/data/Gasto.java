package es.dtgz.controlgastos.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gastos")
public class Gasto {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String categoria;
    private String nombre;
    private double cantidad;
    private String fecha;

    // Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
