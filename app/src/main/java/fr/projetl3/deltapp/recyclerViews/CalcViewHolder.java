package fr.projetl3.deltapp.recyclerViews;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetl3.R;

import org.jetbrains.annotations.NotNull;

public class CalcViewHolder extends RecyclerView.ViewHolder {

    protected ImageView modulePicsView;
    protected TextView moduleNameView;
    protected TextView value;
    protected CardView cardView;

    public CalcViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.modulePicsView = itemView.findViewById(R.id.imageView_module);
        this.moduleNameView = itemView.findViewById(R.id.textView_moduleName);
        this.value = itemView.findViewById(R.id.tv_desc);
        this.cardView = itemView.findViewById(R.id.card_view);

    }
}
