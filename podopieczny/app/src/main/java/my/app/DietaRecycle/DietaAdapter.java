package my.app.DietaRecycle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.app.R;

public class DietaAdapter extends RecyclerView.Adapter<DietaAdapter.MyViewHolder>{

    ArrayList<String> nazwa;
    ArrayList<Double>kcal,weglowodany,bialko,tluszcz,waga;
    ArrayList<Double>kcal_na_100g,weglowodany_na_100g,bialko_na_100g,tluszcz_na_100g;
    int wielkosc=0;
    Context context;
    private final DietaInterface dietaInterface;

    public DietaAdapter(Context ct,ArrayList<String> nazwa,ArrayList<Double> kcal,ArrayList<Double> wegle,ArrayList<Double> bialko,ArrayList<Double> tluszcz,
                        ArrayList<Double> kcal_na_100g,ArrayList<Double> wegle_na_100g,ArrayList<Double> bialko_na_100g,ArrayList<Double> tluszcz_na_100g,
                        ArrayList<Double> waga,int wielkosc,DietaInterface dietaInterface)
    {
    this.context=ct;
    this.nazwa=nazwa;
    this.kcal=kcal;
    this.weglowodany=wegle;
    this.bialko=bialko;
    this.tluszcz=tluszcz;
    this.kcal_na_100g=kcal_na_100g;
    this.weglowodany_na_100g=wegle_na_100g;
    this.bialko_na_100g=bialko_na_100g;
    this.tluszcz_na_100g=tluszcz_na_100g;
    this.waga=waga;
    this.wielkosc=wielkosc;
    this.dietaInterface=dietaInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.dieta_row,parent,false);
        return new MyViewHolder(view,dietaInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.rowNazwa.setText(nazwa.get(position));
        holder.rowKcal.setText(kcal.get(position)+" kcal");
        holder.rowWegle.setText(weglowodany.get(position)+" g");
        holder.rowBialko.setText(bialko.get(position)+" g");
        holder.rowTluszcz.setText(tluszcz.get(position)+" g");
        holder.rowWaga.setText("Waga spożytego produktu: "+waga.get(position)+" g");
    }

    @Override
    public int getItemCount() {
        return wielkosc;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView rowNazwa,rowKcal,rowWegle,rowBialko,rowTluszcz,rowWaga;

        public MyViewHolder(@NonNull View itemView, DietaInterface dietaInterface) {
            super(itemView);
            rowNazwa=itemView.findViewById(R.id.dieta_row_nazwa);
            rowKcal=itemView.findViewById(R.id.dieta_row_kalorie);
            rowWegle=itemView.findViewById(R.id.dieta_row_weglowodany);
            rowBialko=itemView.findViewById(R.id.dieta_row_bialko);
            rowTluszcz=itemView.findViewById(R.id.dieta_row_tluszcz);
            rowWaga=itemView.findViewById(R.id.dieta_row_waga);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(dietaInterface !=null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            dietaInterface.onLongItemClick(position);
                        }
                    }
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dietaInterface !=null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            dietaInterface.onItemClick(position);

                        }
                    }
                }
            });



        }
    }
}
