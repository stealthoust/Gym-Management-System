package my.app.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import my.app.DietaRecycle.DietaAdapter;
import my.app.PomiarRecycle.PomiarAdapter;
import my.app.R;
import my.app.RequestClasses.Measurement;
import my.app.RequestClasses.Product;
import my.app.RequestClasses.Trening;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import my.app.TreningRecycle.TreningAdapter;
import my.app.TreningRecycle.TreningInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TreningFragment extends Fragment implements TreningInterface {

    RecyclerView recyclerView;
    List<Trening> treningi =new ArrayList<>();
    ArrayList<String> s1=new ArrayList<String>(),s2 = new ArrayList<String>();
    TextView tv_tydzien, tv_miesiac,title;

    public TreningFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_trening, container, false);

        recyclerView=v.findViewById(R.id.recycleTreningi);
        tv_tydzien =v.findViewById(R.id.trening_tydzien);
        tv_miesiac =v.findViewById(R.id.trening_miesiac);
        title=v.findViewById(R.id.tv_treningi_title);

        clearArray();
        loadTrainingsInfo();
        loadRecycleData();

        return v;
    }


    private void loadRecycleData()
    {
        clearArray();
        INodeJS inter = RetrofitClient.getGsonInstance().create(INodeJS.class);
        retrofit2.Call<List<Trening>> call=inter.getTrainings(User.getId());

        call.enqueue(new Callback<List<Trening>>() {
            @Override
            public void onResponse(Call<List<Trening>> call, Response<List<Trening>> response) {
                if(!response.isSuccessful())
                {

                    Toast.makeText(getContext(),"Nie udało się wczytać pomiarów",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body() != null)
                {
                    treningi=response.body();


                    for(Trening trening : treningi)
                    {
                        s1.add(trening.getNazwa());
                        s2.add(trening.getData());
                    }
                    TreningAdapter treningAdapter=new TreningAdapter(getContext(),s1,s2,s1.size(),TreningFragment.this);
                    recyclerView.setAdapter(treningAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }

            }

            @Override
            public void onFailure(Call<List<Trening>> call, Throwable t) {

            }
        });

    }

    public void loadTrainingsInfo()
    {
        INodeJS inter=RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call=inter.getTrainingsCount(User.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {

                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            tv_tydzien.setText("Liczba treningów w bieżącym tygodniu: "+jsonObject.getString("per_week"));
                            tv_miesiac.setText("Liczba treningów w bieżącym miesiącu: "+jsonObject.getString("per_month"));

                            if(jsonObject.getInt("per_month")<1) title.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else Toast.makeText(getContext(),"Nie udało się wczytać informacji",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(),"Nie udało się połączyć z serwerem",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearArray()
    {
        s1.clear();
        s2.clear();
    }


    @Override
    public void onItemClick(int position) {
String opis;
try {
    if(treningi.get(position).getOpis()==null||treningi.get(position).getOpis().isEmpty()||treningi.get(position).getOpis().equals(""))
        opis="Ten trening nie zawiera opisu";
    else opis=treningi.get(position).getOpis();
    AlertDialog dialog =new AlertDialog.Builder(getContext())
            .setTitle("Opis treningu")
            .setMessage(opis)
            .setNegativeButton("Cofnij",null)
            .setCancelable(true)
            .show();

    Button negativeButton=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
    negativeButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
        }
    });
}
catch (Exception e)
{
    Log.e("error",e.toString() );
}


    }

    @Override
    public void onLongItemClick(int position) {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                .setTitle("Czy na pewno chcesz usunąć trening?")
                .setMessage("Czy chcesz aby "+treningi.get(position).getNazwa()+" z dnia "+treningi.get(position).getData()+" został usunięty?")
                .setPositiveButton("Tak",null)
                .setNegativeButton("Nie",null)
                .setCancelable(true)
                .show();


        Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTraining(dialog,position);
            }
        });

        Button negativeButton=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void deleteTraining(AlertDialog dialog, int position) {

        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call= inter.deleteTraining(treningi.get(position).getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.body().contains("successfully"))

                {
                    loadTrainingsInfo();
                    dialog.dismiss();
                    Toast.makeText(getContext(),"Trening usunięty pomyślnie!",Toast.LENGTH_SHORT).show();
                    SystemClock.sleep(500);

                    loadRecycleData();
                }
                else Toast.makeText(getContext(),"Nie udało się usunąć treningu!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(),"Nie udało się połączyć z serwerem!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}