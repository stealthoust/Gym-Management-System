package my.app.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.SystemClock;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.app.PomiarRecycle.PomiarAdapter;
import my.app.PomiarRecycle.PomiarInterface;
import my.app.R;
import my.app.RequestClasses.Measurement;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PomiarFragment extends Fragment implements PomiarInterface {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<Measurement> pomiary = new ArrayList<>();
    ArrayList<String> s1 = new ArrayList<String>(), s2 = new ArrayList<String>();
    TextView tv_data, tv_waga;
    Button btn_dodaj;


    public PomiarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pomiar, container, false);
        findFields(v);
        loadRecycleData();
        setButtons();

        return v;
    }


    private void findFields(View v) {
        recyclerView = v.findViewById(R.id.recycleViewPomiar);
        tv_data = v.findViewById(R.id.tv_pomiar_data);
        tv_waga = v.findViewById(R.id.tv_pomiar_waga);
        btn_dodaj = v.findViewById(R.id.btn_dodaj_pomiar);
        swipeRefreshLayout=v.findViewById(R.id.swipepomiar);
    }

    private void loadRecycleData() {
        clearArray();
        INodeJS inter = RetrofitClient.getGsonInstance().create(INodeJS.class);
        retrofit2.Call<List<Measurement>> call = inter.getMeasurements(User.getId());

        call.enqueue(new Callback<List<Measurement>>() {
            @Override
            public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
                if (!response.isSuccessful()) {

                    Toast.makeText(getContext(), "Nie udało się wczytać pomiarów", Toast.LENGTH_SHORT).show();
                    return;
                }
                pomiary = response.body();

                if (pomiary.get(0).getWaga() != null) {
                    for (Measurement pomiar : pomiary) {
                        s1.add("Dnia: " + pomiar.getDatap());
                        s2.add("Waga: " + pomiar.getWaga() + " kg");
                    }
                    tv_data.setText(pomiary.get(0).getDatap());
                    tv_waga.setText(pomiary.get(0).getWaga() + " kg");
                    PomiarAdapter pomiarAdapter = new PomiarAdapter(getContext(), s1, s2, s1.size(), PomiarFragment.this);
                    recyclerView.setAdapter(pomiarAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    tv_waga.setText(pomiary.get(0).getWaga_start() + " kg");
                    tv_data.setText("Brak danych");

                    recyclerView.setAdapter(null);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                }


            }

            @Override
            public void onFailure(Call<List<Measurement>> call, Throwable t) {

                Toast.makeText(getContext(), "Nie udało się nawiązać połączenia!", Toast.LENGTH_SHORT).show();


            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void updateWeight(String waga) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Aktualizacja wagi")
                .setMessage("Czy chcesz zaaktualizować swoją wagę w ustawieniach? (kalorie zostaną zaaktualizowane)")
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
                Call<String> call = inter.updateWeight(User.getId().toString(), waga);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().contains("updated")) {
                                Toast.makeText(getContext(), "Udało się zaaktualizować wagę!", Toast.LENGTH_SHORT).show();
                                loadRecycleData();
                            } else
                                Toast.makeText(getContext(), "Nie udało się zaaktualizować wagi!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getContext(), "Nie udało się połączyć z serwerem!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).show();


    }

    private void setButtons() {
        btn_dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Dodaj pomiar z dzisiejszą datą \nIle ważysz?");

                final EditText input = new EditText(getContext());

                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                input.setGravity(Gravity.CENTER);
                builder.setView(input);


                builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String txt = "";
                        txt = input.getText().toString();

                        if (Double.parseDouble(txt) < 40 || Double.parseDouble(txt) > 210)
                            Toast.makeText(getContext(),
                                    "Podane wartości odbiegają od norm. Skonsultuj się ze specjalistą!",
                                    Toast.LENGTH_SHORT).show();
                        else {
                            addMeasurement(txt);
                            updateWeight(txt);
                        }
                    }
                });
                builder.setNegativeButton("Cofnij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadRecycleData();
            }
        });
    }

    @Override
    public void onLongItemClick(int position) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Czy na pewno chcesz usunąć pomiar?")
                .setMessage("Czy chcesz aby pomiar z dnia: " + pomiary.get(position).getDatap() + " z wagą: " + pomiary.get(position).getWaga() + "kg został usunięty?")
                .setPositiveButton("Tak", null)
                .setNegativeButton("Nie", null)
                .setCancelable(true)
                .show();


        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMeasurement(dialog, position);
            }
        });

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void clearArray() {
        s1.clear();
        s2.clear();
    }

    private void addMeasurement(String txt) {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.addMeasurement(User.getId().toString(), txt);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body().contains("added")) {
                        Toast.makeText(getContext(), "Pomiar pomyślnie dodany!", Toast.LENGTH_SHORT).show();
                        SystemClock.sleep(500);
                        loadRecycleData();
                    } else {
                        Toast.makeText(getContext(), "Nie udało się dodać pomiaru!", Toast.LENGTH_SHORT).show();
                    }

                } else
                    Toast.makeText(getContext(), "Odpowiedź od serwera jest pusta!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się nawiązać połączenia z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMeasurement(AlertDialog dialog, int position) {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.deleteMeasurement(pomiary.get(position).getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {


                if (response.body().contains("Pomiar")) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Pomiar usunięty pomyślnie!", Toast.LENGTH_SHORT).show();
                    SystemClock.sleep(500);

                    loadRecycleData();
                } else
                    Toast.makeText(getContext(), "Nie udało się usunąć pomiaru!", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało połączyć się z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}