package my.app.PlanTreningowyRecycle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.app.R;

public class PlanTreningowyAdapter extends RecyclerView.Adapter <PlanTreningowyAdapter.MyViewHolder>{

    ArrayList<String> nazwa;
    int wielkosc=0;
    Context context;
    private final PlanTreningowyInterface planTreningowyInterface;

    public PlanTreningowyAdapter(Context ct, ArrayList<String> nazwa, int wielkosc, PlanTreningowyInterface planTreningowyInterface) {
        this.context=ct;
        this.nazwa=nazwa;
        this.wielkosc=wielkosc;
        this.planTreningowyInterface = planTreningowyInterface;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.cwiczenie_row,parent,false);
        return new PlanTreningowyAdapter.MyViewHolder(view, planTreningowyInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.rowNazwa.setText(nazwa.get(position));
    }

    @Override
    public int getItemCount() {
        return wielkosc;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView rowNazwa;
        public MyViewHolder(@NonNull View itemView, PlanTreningowyInterface planTreningowyInterface) {
            super(itemView);
            rowNazwa=itemView.findViewById(R.id.cwiczenie_row_nazwa);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(planTreningowyInterface !=null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            planTreningowyInterface.onItemClick(position);

                        }
                    }
                }
            });
        }
    }
}
