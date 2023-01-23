package my.app.TreningRecycle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.app.PlanTreningowyRecycle.PlanTreningowyAdapter;
import my.app.PomiarRecycle.PomiarAdapter;
import my.app.PomiarRecycle.PomiarInterface;
import my.app.R;

public class TreningAdapter extends RecyclerView.Adapter <TreningAdapter.MyViewHolder>{

    ArrayList<String> nazwa,data;
    int wielkosc;
    Context context;
    private final TreningInterface treningInterface;
public TreningAdapter(Context ct, ArrayList<String> nazwa, ArrayList<String> data,int wielk,TreningInterface inter)
{
    this.context=ct;
    this.nazwa=nazwa;
    this.data=data;
    this.wielkosc=wielk;
    this.treningInterface=inter;
}

    public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView rowNazwa,rowData;

        public MyViewHolder(@NonNull View itemView, TreningInterface treningInterface) {
            super(itemView);
            rowNazwa=itemView.findViewById(R.id.trening_row_nazwa);
            rowData=itemView.findViewById(R.id.trening_row_data);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(treningInterface !=null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            treningInterface.onLongItemClick(position);
                        }
                    }
                    return false;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(treningInterface !=null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            treningInterface.onItemClick(position);

                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.trening_row,parent,false);
        return new TreningAdapter.MyViewHolder(view,treningInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.rowNazwa.setText(nazwa.get(position));
        holder.rowData.setText(data.get(position));

    }

    @Override
    public int getItemCount() {
        return wielkosc;
    }


}
