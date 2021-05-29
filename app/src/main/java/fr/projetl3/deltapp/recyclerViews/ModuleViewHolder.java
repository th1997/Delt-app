package fr.projetl3.deltapp.recyclerViews;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetl3.R;

public class ModuleViewHolder extends RecyclerView.ViewHolder {
    protected ImageView modulePicsView;
    protected TextView moduleNameView;
    protected TextView moduleDescView;
    protected CardView cardView;

    // @itemView: recyclerview_item_layout.xml
    public ModuleViewHolder(@NonNull View itemView) {
        super(itemView);

        this.modulePicsView = itemView.findViewById(R.id.imageView_moduleAccueil);
        this.moduleNameView = itemView.findViewById(R.id.textView_moduleNameAccueil);
        this.cardView = itemView.findViewById(R.id.cv_Accueil);
        this.moduleDescView = itemView.findViewById(R.id.tv_descAccueil);

    }
}
