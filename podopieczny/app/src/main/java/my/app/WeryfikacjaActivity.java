package my.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.app.RequestClasses.Coach;
import my.app.RequestClasses.Lifestyle;
import my.app.RequestClasses.User;
import my.app.RequestClasses.WeightGoals;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeryfikacjaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText waga_obecna, wzrost;
    Spinner plecSpinner, aktywnoscSpinner, celSpinner, trenerSpinner;
    private int plecPos = 0, aktywnoscPos = 0, celPos = 0, trenerPos = 0;
    private Button btnWyloguj, btnZatwierdz;

    List<Coach> trenerzy = new ArrayList<>();
    List<Lifestyle> aktywnosci = new ArrayList<>();
    List<WeightGoals> cele = new ArrayList<>();
    String[] tabPlec = {"m", "k"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weryfikacja);

        notVerifiedMessage();

        findElements();
        fillSpinners();
        setButtons();


    }

    @Override
    public void onBackPressed() {

    }

    private void findElements() {
        waga_obecna = findViewById(R.id.et_waga_obecna_ustawienia);
        wzrost = findViewById(R.id.et_wzrost_ustawienia);

        plecSpinner = findViewById(R.id.spinnerPlec_ustawienia);
        aktywnoscSpinner = findViewById(R.id.spinnerTryb_ustawienia);
        celSpinner = findViewById(R.id.spinnerCel_ustawienia);
        trenerSpinner = findViewById(R.id.spinnerTrener_ustawienia);

        waga_obecna.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        wzrost.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        btnWyloguj = findViewById(R.id.btn_cofnij_ustawienia);
        btnZatwierdz = findViewById(R.id.btn_zatwierdz_ustawienia);
    }

    private void setButtons() {
        btnWyloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.cleanUser();
                Intent newActivityIntent = new Intent(WeryfikacjaActivity.this, MainActivity.class);
                startActivity(newActivityIntent);
                Toast.makeText(WeryfikacjaActivity.this, "Pomyślnie wylogowano", Toast.LENGTH_SHORT).show();
            }
        });

        btnZatwierdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    verifyUser();
                } else {
                    Toast.makeText(WeryfikacjaActivity.this, "Wprowadzone dane są nieprawidłowe", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void fillSpinners() {
        ArrayAdapter<CharSequence> adapterPlec = ArrayAdapter.createFromResource(this, R.array.plec_array, android.R.layout.simple_spinner_item);
        adapterPlec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plecSpinner.setAdapter(adapterPlec);
        loadDataSpinner();
        setSpinnerListener();

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void notVerifiedMessage() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Weryfikacja")
                .setMessage("Nie jesteś jeszcze zweryfikowany. Wciśnij Dalej, aby przejść wstępną weryfikacje")
                .setPositiveButton("Dalej", null)
                .setNegativeButton("Wyloguj", null)
                .setCancelable(false)
                .show();


        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeNutton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        negativeNutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newActivityIntent = new Intent(WeryfikacjaActivity.this, MainActivity.class);
                startActivity(newActivityIntent);
                User.cleanUser();
                Toast.makeText(WeryfikacjaActivity.this, "Pomyślnie wylogowano", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private boolean validateForm() {
        int value = 0;
        if (waga_obecna.getText().length() < 2 || waga_obecna.getText().length() > 6) {
            waga_obecna.setError("Długość wagi jest nieprawidłowa (między 2-6 znaków)");
            waga_obecna.setBackgroundResource(R.drawable.error);
            value = 1;
        } else if (Double.parseDouble(waga_obecna.getText().toString()) < 40 || Double.parseDouble(waga_obecna.getText().toString()) > 210) {
            waga_obecna.setError("Podane wartości odbiegają od norm. Skonsultuj się ze specjalistą!");
            waga_obecna.setBackgroundResource(R.drawable.error);
            value = 1;
        } else {
            waga_obecna.setBackgroundResource(R.drawable.green);
        }

        if (wzrost.getText().length() < 2 || Double.parseDouble(wzrost.getText().toString()) < 130 || Double.parseDouble(wzrost.getText().toString()) > 251) {
            wzrost.setError("Podane wartości odbiegają od norm. Skonsultuj się ze specjalistą!");
            wzrost.setBackgroundResource(R.drawable.error);
            value = 1;
        } else {
            wzrost.setBackgroundResource(R.drawable.green);
        }

        if (value == 1) return false;
        else
            return true;
    }

    private void verifyUser() {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.verifyUserForm(User.id, waga_obecna.getText().toString(), tabPlec[plecPos], wzrost.getText().toString(), aktywnosci.get(aktywnoscPos).getId()
                , cele.get(celPos).getId(), trenerzy.get(trenerPos).getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().contains("User")) {
                            Intent newActivityIntent = new Intent(WeryfikacjaActivity.this, MainActivity.class);
                            startActivity(newActivityIntent);
                            Toast.makeText(WeryfikacjaActivity.this, "Weryfikacja zakończona powodzeniem! Nastąpi wylogowanie", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(WeryfikacjaActivity.this, "Weryfikacja zakończona niepowodzeniem! Spróbuj ponownie!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(WeryfikacjaActivity.this, "Nie można nawiązać połączenia z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
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
                    Toast.makeText(WeryfikacjaActivity.this, "Nie udało się wczytać trenerów", Toast.LENGTH_SHORT).show();
                    return;
                }
                trenerzy = response.body();

                if (trenerzy != null) {
                    ArrayAdapter<Coach> adapter = new ArrayAdapter<Coach>(WeryfikacjaActivity.this, android.R.layout.simple_spinner_item, trenerzy);
                    trenerSpinner.setAdapter(adapter);
                }


            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {

                Toast.makeText(WeryfikacjaActivity.this, "Nie udało się połączyć z serwerem, przepraszamy za niedogodności!", Toast.LENGTH_SHORT).show();

            }
        });

        call2.enqueue(new Callback<List<Lifestyle>>() {
            @Override
            public void onResponse(Call<List<Lifestyle>> call, Response<List<Lifestyle>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(WeryfikacjaActivity.this, "Nie udało się wczytać stylów życia", Toast.LENGTH_SHORT).show();
                    return;
                }
                aktywnosci = response.body();

                if (aktywnosci != null) {
                    ArrayAdapter<Lifestyle> adapter = new ArrayAdapter<Lifestyle>(WeryfikacjaActivity.this, android.R.layout.simple_spinner_item, aktywnosci);
                    aktywnoscSpinner.setAdapter(adapter);

                }


            }

            @Override
            public void onFailure(Call<List<Lifestyle>> call, Throwable t) {
                Toast.makeText(WeryfikacjaActivity.this, "Nie udało się połączyć z serwerem, przepraszamy za niedogodności!", Toast.LENGTH_SHORT).show();
            }
        });

        call3.enqueue(new Callback<List<WeightGoals>>() {
            @Override
            public void onResponse(Call<List<WeightGoals>> call, Response<List<WeightGoals>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(WeryfikacjaActivity.this, "Nie udało się wczytać celów wagowych", Toast.LENGTH_SHORT).show();
                    return;
                }
                cele = response.body();

                if (cele != null) {
                    ArrayAdapter<WeightGoals> adapter = new ArrayAdapter<WeightGoals>(WeryfikacjaActivity.this, android.R.layout.simple_spinner_item, cele);
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
}

