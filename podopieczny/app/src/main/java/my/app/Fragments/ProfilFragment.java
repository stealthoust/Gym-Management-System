package my.app.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import my.app.Functions;
import my.app.MainActivity;
import my.app.R;
import my.app.RequestClasses.Coach;
import my.app.RequestClasses.Lifestyle;
import my.app.RequestClasses.User;
import my.app.RequestClasses.WeightGoals;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfilFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    EditText waga_obecna, wzrost;
    TextView kalorie, treningToday, treningRok, liczbaPlanow;
    Spinner plecSpinner, aktywnoscSpinner, celSpinner, trenerSpinner;
    private int plecPos = 0, aktywnoscPos = 0, celPos = 0, trenerPos = 0;
    private Button btnZatwierdz;

    private int userPlec, userAktywnosc, userCel, userTrener;
    private double userWaga, userWzrost;

    List<Coach> trenerzy = new ArrayList<>();
    List<Lifestyle> aktywnosci = new ArrayList<>();
    List<WeightGoals> cele = new ArrayList<>();
    String[] tabPlec = {"m", "k"};


    public ProfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_profil, container, false);

        findFields(v);
        getStatistics();
        fillSpinners();
        getUserData();
        setButtons();


        return v;


    }

    private void getStatistics() {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.getUserStatictics(User.id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ColorStateList oldColors = liczbaPlanow.getTextColors();
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        kalorie.setText("Pozostałe kalorie do uzupełnienia: " + jsonObject.getString("kcal"));
                        if (jsonObject.getInt("today") < 1 && jsonObject.getString("data").contains("brak"))
                            treningToday.setText("Nie przeprowadziłeś jeszcze żadnych treningów");
                        else if (jsonObject.getInt("today") < 1)
                            treningToday.setText("Nie przeprowadziłeś jeszcze dzisiaj żadnego treningu. Ostatni trening zanotowano : " + jsonObject.getString("data"));
                        else {
                            treningToday.setText("Zanotowałeś dzisiaj trening. Tak trzymaj!");
                            treningToday.setTextColor(Color.parseColor("#22870b"));
                        }
                        treningRok.setText("Liczba treningów w bieżącym roku: " + jsonObject.getString("rok"));
                        liczbaPlanow.setText("Liczba planów treningowych przypisanych do konta: " + jsonObject.getString("liczba_planow"));

                        if (jsonObject.getInt("kcal") < 0) {
                            kalorie.setTextColor(Color.RED);

                        } else {
                            kalorie.setTextColor(oldColors);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else
                    Toast.makeText(getContext(), "Nie udało się wczytać poprawnej odpowiedzi od serwera!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setButtons() {
        btnZatwierdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnZatwierdz.getText().toString().contains("Zmie")) {
                    unlockFields();
                    btnZatwierdz.setText("Zapisz");
                } else if (checkDifference() || userTrener != trenerSpinner.getSelectedItemPosition()) {
                    if (validateForm()) {
                        if (userTrener != trenerSpinner.getSelectedItemPosition()) {
                            AlertDialog dialog = new AlertDialog.Builder(getContext())
                                    .setTitle("Zmiana trenera")
                                    .setMessage("Czy na pewno chcesz zmienić trenera? Jeśli tak, twoje konto będzię oczekiwało na zaakceptowanie przez trenera." +
                                            " Do tego czasu nie będziesz mógł korzystać z aplikacji.").setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            changeDataTrainer();
                                        }
                                    })
                                    .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .show();
                        } else {
                            changeData();

                        }
                    } else {
                        Toast.makeText(getContext(), "Wprowadzone dane są niepoprawne", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    btnZatwierdz.setText("Zmień");
                    lockFields();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStatistics();
                fillSpinners();
                getUserData();

            }
        });
    }

    private void findFields(View view) {
        waga_obecna = view.findViewById(R.id.et_waga_obecna_ustawienia);
        wzrost = view.findViewById(R.id.et_wzrost_ustawienia);
        waga_obecna.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        wzrost.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        kalorie = view.findViewById(R.id.tv_profil_kalorie);
        treningToday = view.findViewById(R.id.tv_profil_trening);
        treningRok = view.findViewById(R.id.tv_profil_ilosc_rok);
        liczbaPlanow = view.findViewById(R.id.tv_profil_plany);


        plecSpinner = view.findViewById(R.id.spinnerPlec_ustawienia);
        aktywnoscSpinner = view.findViewById(R.id.spinnerTryb_ustawienia);
        celSpinner = view.findViewById(R.id.spinnerCel_ustawienia);
        trenerSpinner = view.findViewById(R.id.spinnerTrener_ustawienia);

        btnZatwierdz = view.findViewById(R.id.btn_zatwierdz_ustawienia);

        swipeRefreshLayout=view.findViewById(R.id.swipeprofil);
    }

    private void loadDataSpinner() {
        INodeJS inter = RetrofitClient.getGsonInstance().create(INodeJS.class);
        Call<List<Coach>> call1 = inter.getCoaches();
        Call<List<Lifestyle>> call2 = inter.getAllLifestyles();
        Call<List<WeightGoals>> call3 = inter.getAlWeightGoals();
        call1.enqueue(new Callback<List<Coach>>() {
            @Override
            public void onResponse(Call<List<Coach>> call, Response<List<Coach>> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Nie udało się wczytać trenerów", Toast.LENGTH_SHORT).show();
                    return;
                }
                trenerzy = response.body();

                if (trenerzy != null) {
                    ArrayAdapter<Coach> adapter = new ArrayAdapter<Coach>(getContext(), android.R.layout.simple_spinner_item, trenerzy);
                    trenerSpinner.setAdapter(adapter);
                }


            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {

                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem, przepraszamy za niedogodności!", Toast.LENGTH_SHORT).show();

            }
        });

        call2.enqueue(new Callback<List<Lifestyle>>() {
            @Override
            public void onResponse(Call<List<Lifestyle>> call, Response<List<Lifestyle>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Nie udało się wczytać stylów życia", Toast.LENGTH_SHORT).show();
                    return;
                }
                aktywnosci = response.body();

                if (aktywnosci != null) {
                    ArrayAdapter<Lifestyle> adapter = new ArrayAdapter<Lifestyle>(getContext(), android.R.layout.simple_spinner_item, aktywnosci);
                    aktywnoscSpinner.setAdapter(adapter);

                }


            }

            @Override
            public void onFailure(Call<List<Lifestyle>> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem, przepraszamy za niedogodności!", Toast.LENGTH_SHORT).show();
            }
        });

        call3.enqueue(new Callback<List<WeightGoals>>() {
            @Override
            public void onResponse(Call<List<WeightGoals>> call, Response<List<WeightGoals>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Nie udało się wczytać celów wagowych", Toast.LENGTH_SHORT).show();
                    return;
                }
                cele = response.body();

                if (cele != null) {
                    ArrayAdapter<WeightGoals> adapter = new ArrayAdapter<WeightGoals>(getContext(), android.R.layout.simple_spinner_item, cele);
                    celSpinner.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<List<WeightGoals>> call, Throwable t) {

            }
        });
    }

    private void setSpinnerListener() {
        plecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                plecPos = plecSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        aktywnoscSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                aktywnoscPos = aktywnoscSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        celSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                celPos = celSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        trenerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                trenerPos = trenerSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void fillSpinners() {
        ArrayAdapter<CharSequence> adapterPlec = ArrayAdapter.createFromResource(getContext(), R.array.plec_array, android.R.layout.simple_spinner_item);
        adapterPlec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plecSpinner.setAdapter(adapterPlec);
        loadDataSpinner();
        setSpinnerListener();

    }

    private void getUserData() {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.getUserInfo(User.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {

                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            userWaga = jsonObject.getDouble("waga");
                            userWzrost = jsonObject.getDouble("wzrost");

                            userPlec = jsonObject.getInt("plec");
                            userAktywnosc = jsonObject.getInt("tryb");
                            userCel = jsonObject.getInt("cel_waga");
                            userTrener = jsonObject.getInt("trener");

                            setAndLockUserData(userWaga, userWzrost, userPlec, userAktywnosc, userCel, userTrener);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    Toast.makeText(getContext(), "Nie udało się wczytać informacji", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void setAndLockUserData(Double waga, Double userWzrost, int posPlec, int posTryb, int posCelWaga, int posTrener) {
        waga_obecna.setText(String.valueOf(waga));
        wzrost.setText(String.valueOf(userWzrost));

        plecSpinner.setSelection(posPlec);
        aktywnoscSpinner.setSelection(posTryb);
        celSpinner.setSelection(posCelWaga);
        trenerSpinner.setSelection(posTrener);

        lockFields();
    }

    private void lockFields() {
        waga_obecna.setInputType(InputType.TYPE_NULL);
        wzrost.setInputType(InputType.TYPE_NULL);

        plecSpinner.setEnabled(false);
        aktywnoscSpinner.setEnabled(false);
        celSpinner.setEnabled(false);
        trenerSpinner.setEnabled(false);
    }

    private void unlockFields() {
        waga_obecna.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        wzrost.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        plecSpinner.setEnabled(true);
        aktywnoscSpinner.setEnabled(true);
        celSpinner.setEnabled(true);
        trenerSpinner.setEnabled(true);
    }

    private boolean checkDifference() {
        if (userWaga != Double.parseDouble(waga_obecna.getText().toString()) ||
                userWzrost != Double.parseDouble(wzrost.getText().toString()) ||
                userPlec != plecSpinner.getSelectedItemPosition() ||
                userAktywnosc != aktywnoscSpinner.getSelectedItemPosition() ||
                userCel != celSpinner.getSelectedItemPosition()) return true;
        else return false;
    }

    private boolean validateForm() {
        int value = 0;

        if (Double.parseDouble(waga_obecna.getText().toString()) < 40 || Double.parseDouble(waga_obecna.getText().toString()) > 210)
            value = 1;
        if (wzrost.getText().length() < 2 || Double.parseDouble(wzrost.getText().toString()) < 130 || Double.parseDouble(wzrost.getText().toString()) > 251)
            value = 1;

        if (value == 1) return false;
        else
            return true;
    }

    private void changeDataTrainer() {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.updateDataTrainer(waga_obecna.getText().toString(), tabPlec[plecPos], wzrost.getText().toString(), aktywnosci.get(aktywnoscPos).getId()
                , cele.get(celPos).getId(), User.id, trenerzy.get(trenerPos).getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().contains("successfully")) {
                        Toast.makeText(getContext(), "Pomyślnie zaaktualizowano dane. Zostaniesz wylogowany", Toast.LENGTH_LONG).show();
                        Intent newActivityIntent = new Intent(getContext(), MainActivity.class);
                        startActivity(newActivityIntent);
                        User.cleanUser();

                    } else
                        Toast.makeText(getContext(), "Nie udało się zaaktualizować danych!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Nie udało się zaaktualizować danych!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeData() {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.updateData(waga_obecna.getText().toString(), tabPlec[plecPos], wzrost.getText().toString(), aktywnosci.get(aktywnoscPos).getId()
                , cele.get(celPos).getId(), User.id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().contains("updated")) {
                        Toast.makeText(getContext(), "Pomyślnie zaaktualizowano dane", Toast.LENGTH_SHORT).show();
                        fillSpinners();
                        getUserData();
                        getStatistics();
                        btnZatwierdz.setText("Zmień");

                    } else
                        Toast.makeText(getContext(), "Nie udało się zaaktualizować danych!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Nie udało się zaaktualizować danych!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}