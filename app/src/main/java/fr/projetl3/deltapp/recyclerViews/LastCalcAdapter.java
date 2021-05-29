package fr.projetl3.deltapp.recyclerViews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetl3.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import fr.projetl3.deltapp.Accueil;

public class LastCalcAdapter extends RecyclerView.Adapter<CalcViewHolder>{

    private ArrayList<HashMap<String, String>> list;
    private Context context;
    private LayoutInflater mLayoutInflater;

    public LastCalcAdapter(ArrayList<HashMap<String, String>> list, Context context){
        this.list = list;
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @NotNull
    @Override
    public CalcViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View recyclerViewItem = mLayoutInflater.inflate(R.layout.recyclerview_item_layout, parent, false);
        return new CalcViewHolder(recyclerViewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CalcViewHolder holder, int position) {
        HashMap<String, String> hashMap = list.get(position);
        if(hashMap.containsKey("moduleName") && hashMap.containsKey("formule") && hashMap.containsKey("resultat")){
            String moduleName = hashMap.get("moduleName"), formule = hashMap.get("formule"), resultat = hashMap.get("resultat");
            int imageResId = this.getDrawableResIdByName("/drawable/ic_" + moduleName.toLowerCase().replaceAll(" ", "_"));

            // Bind data to viewholder
            holder.modulePicsView.setImageResource(imageResId);
            holder.moduleNameView.setText(moduleName);
            String str = formule  + "\n" + resultat;
            holder.value.setText(str);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Find Image ID corresponding to the name of the image (in the directory drawable).
    public int getDrawableResIdByName(String resName)  {
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName , "drawable", pkgName);
        Log.i(Accueil.LOG_TAG, "Res Name: "+ resName+"==> Res ID = "+ resID);
        return resID;
    }
}
