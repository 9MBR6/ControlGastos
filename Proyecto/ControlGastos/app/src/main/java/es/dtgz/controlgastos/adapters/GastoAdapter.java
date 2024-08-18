package es.dtgz.controlgastos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.dtgz.controlgastos.R;
import es.dtgz.controlgastos.data.Gasto;

public class GastoAdapter extends RecyclerView.Adapter<GastoAdapter.GastoViewHolder> {

    private final List<Gasto> gastos;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Gasto gasto);
    }

    public GastoAdapter(List<Gasto> gastos, OnItemClickListener onItemClickListener) {
        this.gastos = gastos;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
        return new GastoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        Gasto gasto = gastos.get(position);
        holder.textViewCategoria.setText(gasto.getCategoria());
        holder.textViewNombre.setText(gasto.getNombre());
        holder.textViewCantidad.setText(String.format("%.2f €", gasto.getCantidad()));
        holder.textViewFecha.setText(gasto.getFecha());

        // Asignar la imagen según la categoría
        switch (gasto.getCategoria()) {
            case "Ahorros":
                holder.imageViewCategoria.setImageResource(R.drawable.ic_ahorros);
                break;
            case "Ocio":
                holder.imageViewCategoria.setImageResource(R.drawable.ic_ocio);
                break;
            case "Comida":
                holder.imageViewCategoria.setImageResource(R.drawable.ic_comida);
                break;
            case "Gasolina":
                holder.imageViewCategoria.setImageResource(R.drawable.ic_gasolina);
                break;
            case "Imprevistos":
                holder.imageViewCategoria.setImageResource(R.drawable.ic_imprevistos);
                break;
            case "Internet+Móvil":
                holder.imageViewCategoria.setImageResource(R.drawable.ic_internet_movil);
                break;
            default:
                holder.imageViewCategoria.setImageResource(R.drawable.ic_defecto);
                break;
        }

        // Configura el listener para manejar el clic
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(gasto));
    }

    @Override
    public int getItemCount() {
        return gastos.size();
    }

    public static class GastoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewCategoria;
        private final TextView textViewCategoria;
        private final TextView textViewNombre;
        private final TextView textViewCantidad;
        private final TextView textViewFecha;

        public GastoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategoria = itemView.findViewById(R.id.image_view_categoria);
            textViewCategoria = itemView.findViewById(R.id.text_view_categoria);
            textViewNombre = itemView.findViewById(R.id.text_view_nombre);
            textViewCantidad = itemView.findViewById(R.id.text_view_cantidad);
            textViewFecha = itemView.findViewById(R.id.text_view_fecha);
        }
    }
}
