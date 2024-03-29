package fr.projetl3.deltapp.recyclerViews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetl3.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import fr.projetl3.deltapp.Accueil;
import fr.projetl3.deltapp.Modules;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<ModuleViewHolder> {


    private List<Modules> modules;
    private Context context;
    private LayoutInflater mLayoutInflater;

    public CustomRecyclerViewAdapter(Context context, List<Modules> datas) {
        this.context = context;
        this.modules = datas;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @NotNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NotNull final ViewGroup parent, int viewType) {
        // Inflate view from recyclerview_item_layout.xml
        View recyclerViewItem = mLayoutInflater.inflate(R.layout.recyclerview_accueil_layout, parent, false);

        recyclerViewItem.setOnClickListener(v -> handleRecyclerItemClick( (RecyclerView)parent, v));
        return new ModuleViewHolder(recyclerViewItem);
    }

    @Override
    public void onBindViewHolder(ModuleViewHolder holder, int position) {
        Modules modules1 = this.modules.get(position);

        int imageResId = this.getDrawableResIdByName("/drawable/ic_" +modules1.getModuleName().toLowerCase().replaceAll(" ", "_"));
        // Bind data to viewholder
        holder.modulePicsView.setImageResource(imageResId);
        holder.moduleNameView.setText(modules1.getModuleName() );
        holder.moduleDescView.setText(modules1.getDescription() );
        holder.itemView.setOnClickListener(v -> {

            //Toast.makeText(this.context,"Click 2 = " + modules1.getModuleName(), Toast.LENGTH_LONG).show();
            try {
                if(context instanceof Accueil){
                    Accueil acc = (Accueil) context;
                    acc.selectModule(modules1.getModuleName());
                    Toast.makeText(this.context, "Sélection du module: " + modules1.getModuleName(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                Toast.makeText(this.context,"Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return this.modules.size();
    }

    // Find Image ID corresponding to the name of the image (in the directory drawable).
    public int getDrawableResIdByName(String resName)  {
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName , "drawable", pkgName);
        Log.i(Accueil.LOG_TAG, "Res Name: "+ resName+"==> Res ID = "+ resID);
        return resID;
    }

    // Click on RecyclerView Item.
    private void handleRecyclerItemClick(RecyclerView recyclerView, View itemView) {
        int itemPosition = recyclerView.getChildLayoutPosition(itemView);
        Modules modules1  = this.modules.get(itemPosition);

        Toast.makeText(this.context, "Click = " + modules1.getModuleName(), Toast.LENGTH_SHORT).show();

    }
}
