package my.app.PomiarRecycle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.app.R;

public class PomiarAdapter extends RecyclerView.Adapter<PomiarAdapter.MyViewHolder> {

    ArrayList<String> data,waga;
    int wielkosc=0;
    Context context;
    private final PomiarInterface pomiarInterface;
public PomiarAdapter(Context ct, ArrayList<String> d, ArrayList<String> w,int wielk,PomiarInterface inter)
{
    context=ct;
data=d;
waga=w;
wielkosc=wielk;
pomiarInterface=inter;
}



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.pomiar_row,parent,false);
        return new MyViewHolder(view,pomiarInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.rowData.setText(data.get(position));
    holder.rowWaga.setText(waga.get(position));
    }

    @Override
    public int getItemCount() {
        return wielkosc;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView rowData,rowWaga;


        public MyViewHolder(@NonNull View itemView, PomiarInterface pomiarInterface) {
            super(itemView);
            rowData=itemView.findViewById(R.id.pomiar_row_data);
            rowWaga=itemView.findViewById(R.id.pomiar_row_waga);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(pomiarInterface !=null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            pomiarInterface.onLongItemClick(position);
                        }
                    }
                    return false;
                }
            });


        }
    }
}
