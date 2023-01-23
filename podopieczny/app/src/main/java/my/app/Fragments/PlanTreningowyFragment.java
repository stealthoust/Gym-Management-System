package my.app.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import my.app.Details.ExerciseDetail;
import my.app.R;
import my.app.RequestClasses.Exercise;
import my.app.RequestClasses.PlanTreningowy;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import my.app.PlanTreningowyRecycle.PlanTreningowyAdapter;
import my.app.PlanTreningowyRecycle.PlanTreningowyInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlanTreningowyFragment extends Fragment implements PlanTreningowyInterface {
    SwipeRefreshLayout swipeRefreshLayout;
    TextView nazwa_planu, opis_planu, zmiana_planu;
    ImageButton btn_left, btn_right;
    Button btn_zanotuj_trening;
    RecyclerView recyclerView;

    List<PlanTreningowy> plany_treningowe = new ArrayList<>();
    List<Exercise> cwiczenia_plan_treningowy = new ArrayList<>();

    ArrayList<String> t_nazwa = new ArrayList<String>();

    int current;

    public PlanTreningowyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plan_treningowy, container, false);

        findFields(v);
        setButtons();

        getTrainingPlans();



        return v;

    }

    private void findFields(View v) {
        nazwa_planu = v.findViewById(R.id.tv_trening_nazwa);
        opis_planu = v.findViewById(R.id.tv_trening_opis);
        zmiana_planu = v.findViewById(R.id.tv_trening_zmiana);
        btn_left = v.findViewById(R.id.btn_changePlan_left);
        btn_right = v.findViewById(R.id.btn_changePlan_right);
        btn_zanotuj_trening = v.findViewById(R.id.btn_zanotuj_trening);
        recyclerView = v.findViewById(R.id.recyclerViewTrening);
        swipeRefreshLayout=v.findViewById(R.id.swipe_plan);
    }

    private void setButtons() {
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (current != 0) {

                    current -= 1;
                    switchTrainingPlan();

                }

            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (current < plany_treningowe.size() - 1) {

                    current += 1;
                    switchTrainingPlan();

                }
            }
        });

        btn_zanotuj_trening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Dodaj opis treningu (opcjonalnie)");

                final EditText input = new EditText(getContext());

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setGravity(Gravity.CENTER);
                builder.setView(input);

                builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String txt = "";
                        txt = input.getText().toString();

                        if (txt.length() > 200)
                            Toast.makeText(getContext(), "Opis jest za długi, max 200 znaków!", Toast.LENGTH_SHORT).show();
                        else {
                            addTraining(plany_treningowe.get(current).getId(),plany_treningowe.get(current).getTytul(), txt, User.id);

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

                getTrainingPlans();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getTrainingPlans() {
        INodeJS inter = RetrofitClient.getGsonInstance().create(INodeJS.class);
        retrofit2.Call<List<PlanTreningowy>> call = inter.getTrainingPlans(User.getId());

        Response<List<PlanTreningowy>> response;
        try {
            response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                plany_treningowe = response.body();
                current = 0;
                for (PlanTreningowy plan : plany_treningowe) {
                    try {
                        //if(getExercisesFromTrainingPlan(plan.id)!=null&&getExercisesFromTrainingPlan(plan.id).size()>0)
                        plan.exercises = getExercisesFromTrainingPlan(plan.id);


                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Nie udało się wczytać cwiczen", Toast.LENGTH_SHORT).show();
                    }
                }
                nazwa_planu.setText(plany_treningowe.get(0).getTytul());
                opis_planu.setText(plany_treningowe.get(0).getOpis());
                //if (plany_treningowe != null && plany_treningowe.size() > 0 && plany_treningowe.get(0).exercises.size() > 0)
                getExercisesName(plany_treningowe.get(0).exercises);
                if (plany_treningowe.size() == 1) {
                    btn_left.setEnabled(false);
                    btn_right.setEnabled(false);
                }
                else {
                    btn_left.setEnabled(true);
                    btn_right.setEnabled(true);
                }
                PlanTreningowyAdapter planTreningowyAdapter = new PlanTreningowyAdapter(getContext(), t_nazwa, t_nazwa.size(), PlanTreningowyFragment.this);
                recyclerView.setAdapter(planTreningowyAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            } else {
                Toast.makeText(getContext(), "Brak planow treningowych", Toast.LENGTH_SHORT).show();

                hide();
            }

        } catch (IOException e) {
            Toast.makeText(getContext(), "Nie udało się nawiązać połączenia z serwerem", Toast.LENGTH_SHORT).show();
        }


    }

    private void getExercisesName(List<Exercise> list) {
        t_nazwa.clear();
        if (list != null && list.size() > 0) {
            for (Exercise exercise : list) {
                t_nazwa.add(exercise.getNazwa());
            }
        }

    }

    private void hide() {
        nazwa_planu.setVisibility(View.INVISIBLE);
        opis_planu.setVisibility(View.INVISIBLE);
        btn_left.setVisibility(View.INVISIBLE);
        btn_right.setVisibility(View.INVISIBLE);
        btn_zanotuj_trening.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        zmiana_planu.setText("Brak planów treningowych");
    }

    private List<Exercise> getExercisesFromTrainingPlan(int id_planu) throws IOException {
        INodeJS inter = RetrofitClient.getGsonInstance().create(INodeJS.class);
        Call<List<Exercise>> call = inter.getExercisesFromTrainingPlan(id_planu);

        Response<List<Exercise>> response = call.execute();
        cwiczenia_plan_treningowy = response.body();
        return cwiczenia_plan_treningowy;

    }

    private void switchTrainingPlan() {
        nazwa_planu.setText(plany_treningowe.get(current).getTytul());
        opis_planu.setText(plany_treningowe.get(current).getOpis());
        getExercisesName(plany_treningowe.get(current).exercises);

        PlanTreningowyAdapter planTreningowyAdapter = new PlanTreningowyAdapter(getContext(), t_nazwa, t_nazwa.size(), PlanTreningowyFragment.this);
        recyclerView.setAdapter(planTreningowyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    private void addTraining(int id_planu,String nazwa, String opis, int id) {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.addTraining(id_planu,nazwa, opis, id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body().contains("added")) {
                        Toast.makeText(getContext(), "Trening pomyślnie dodany!", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getContext(), "Nie udało się dodać treningu!", Toast.LENGTH_SHORT).show();
                    }

                } else
                    Toast.makeText(getContext(), "Nie udało się otrzymać odpowiedzi od serwera!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się nawiązać połączenia z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String k = plany_treningowe.get(current).exercises.get(position).getKategoria();
        String p = plany_treningowe.get(current).exercises.get(position).getPozycja_wyjsciowa();
        String r = plany_treningowe.get(current).exercises.get(position).getRuch();
        String w = plany_treningowe.get(current).exercises.get(position).getWskazowki();
        String url = plany_treningowe.get(current).exercises.get(position).getVideo_url();

        ExerciseDetail dialog = new ExerciseDetail(k, p, r, w, url);
        dialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "Dialog");
    }

}