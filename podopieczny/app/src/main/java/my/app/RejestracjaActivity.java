package my.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejestracjaActivity extends AppCompatActivity {

    private EditText et_login, et_haslo, et_pow_haslo, et_imie, et_nazwisko, et_email, et_pow_email, et_telefon;
    private TextView log_lbl, tv_data, ageError;
    private Button btn_zarejestruj;
    private String data;
    private DatePickerDialog datePickerDialog;

    DatePickerDialog.OnDateSetListener setListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int rok, int miesiac, int dzien) {
            miesiac = miesiac + 1;
            data = rok + "-" + miesiac + "-" + dzien;
            String display_data = dzien + "/" + miesiac + "/" + rok;
            tv_data.setText(display_data);
            try {

                if (!obliczDni(datePickerDialog.getDatePicker())) {
                    tv_data.setError("Twój wiek musi być między 15 a 70");
                    tv_data.setBackgroundResource(R.drawable.error);
                    ageError.setVisibility(View.VISIBLE);
                } else {
                    tv_data.setError(null);
                    tv_data.setBackgroundResource(R.drawable.green);
                    ageError.setVisibility(View.INVISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejestracja);

        findFields();
        setEditTextListeners();
        setButtons();


    }

    private void findFields() {
        et_login = findViewById(R.id.et_login_reg);
        Functions.filterLettersNumbersOnly(et_login);
        et_haslo = findViewById(R.id.et_haslo_reg);
        Functions.filterLettersNumbersOnly(et_haslo);
        et_pow_haslo = findViewById(R.id.et_pow_haslo_reg);
        Functions.filterLettersNumbersOnly(et_pow_haslo);
        et_imie = findViewById(R.id.et_imie_reg);
        Functions.filterLetterOnly(et_imie);
        et_nazwisko = findViewById(R.id.et_nazwisko_reg);
        Functions.filterLetterOnly(et_nazwisko);
        et_email = findViewById(R.id.et_email_reg);
        et_pow_email = findViewById(R.id.et_pow_email_reg);
        et_telefon = findViewById(R.id.et_telefon_reg);
        et_telefon.setInputType(InputType.TYPE_CLASS_NUMBER);
        log_lbl = findViewById(R.id.tv_powrot_log);
        btn_zarejestruj = findViewById(R.id.btn_zarejestruj);
        tv_data = findViewById(R.id.tv_data_reg);
        ageError = findViewById(R.id.ageError);
    }

    private void setButtons() {
        Calendar calendar = Calendar.getInstance();
        final int rok = calendar.get(Calendar.YEAR);
        final int miesiac = calendar.get(Calendar.MONTH);
        final int dzien = calendar.get(Calendar.DAY_OF_MONTH);

        btn_zarejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (validation()) registerUser();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        log_lbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivityIntent = new Intent(RejestracjaActivity.this, MainActivity.class);
                startActivity(newActivityIntent);
            }
        });


        tv_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(RejestracjaActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, rok, miesiac, dzien);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                datePickerDialog.show();
            }
        });
    }

    private void setEditTextListeners() {
        et_login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_login.getText().length() < 5 || et_login.getText().length() > 30) {
                        et_login.setError("Login musi zawierać między 5 a 30 znakow!");
                        et_login.setBackgroundResource(R.drawable.error);

                    } else {
                        et_login.setBackgroundResource(R.drawable.green);
                    }
                }
            }
        });

        et_haslo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_haslo.getText().length() < 8 || et_haslo.getText().length() > 30) {
                        et_haslo.setError("Hasło musi zawierać między 8 a 30 znakow!");
                        et_haslo.setBackgroundResource(R.drawable.error);

                    } else {
                        et_haslo.setBackgroundResource(R.drawable.green);

                    }

                    if (et_haslo.getText().toString().equals(et_pow_haslo.getText().toString())) {
                        et_pow_haslo.setBackgroundResource(R.drawable.green);

                    } else {
                        et_pow_haslo.setError("Hasła muszą być takie same!");
                        et_pow_haslo.setBackgroundResource(R.drawable.error);

                    }

                }
            }
        });

        et_pow_haslo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_haslo.getText().toString().equals(et_pow_haslo.getText().toString())) {
                        et_pow_haslo.setBackgroundResource(R.drawable.green);

                    } else {
                        et_pow_haslo.setError("Hasła muszą być takie same!");
                        et_pow_haslo.setBackgroundResource(R.drawable.error);

                    }
                }
            }
        });

        et_imie.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_imie.getText().length() < 3 || et_imie.getText().length() > 13) {
                        et_imie.setError("Imię musi zawierać między 3 a 13 znaków!");
                        et_imie.setBackgroundResource(R.drawable.error);

                    } else {
                        et_imie.setBackgroundResource(R.drawable.green);

                    }
                }
            }
        });

        et_nazwisko.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_nazwisko.getText().length() < 2 || et_nazwisko.getText().length() > 35) {
                        et_nazwisko.setError("Nazwisko musi zawierać między 2 a 35 znaków!");
                        et_nazwisko.setBackgroundResource(R.drawable.error);

                    } else {
                        et_nazwisko.setBackgroundResource(R.drawable.green);
                    }
                }
            }
        });

        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!Functions.emailValid(et_email.getText())) {
                        et_email.setError("Email jest nieprawidłowy!");
                        et_email.setBackgroundResource(R.drawable.error);
                    } else et_email.setBackgroundResource(R.drawable.green);
                }
            }
        });

        et_pow_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_email.getText().toString().equals(et_pow_email.getText().toString())) {
                        et_pow_email.setError("Adresy email muszą być takie same!");
                        et_pow_email.setBackgroundResource(R.drawable.error);
                    } else et_pow_email.setBackgroundResource(R.drawable.green);
                }
            }
        });

        et_telefon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_telefon.getText().length() == 9 || et_telefon.getText().length() == 0) {
                        et_telefon.setBackgroundResource(R.drawable.green);
                    } else {
                        et_telefon.setError("Jeśli już podajesz numer telefonu to zadbaj, żeby miał 9 znaków!");
                        et_telefon.setBackgroundResource(R.drawable.error);
                    }
                }
            }
        });
    }

    private void registerUser() {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.registerUser(et_login.getText().toString(), et_haslo.getText().toString(), et_email.getText().toString(),
                et_imie.getText().toString(), et_nazwisko.getText().toString(), et_telefon.getText().toString(), data);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        if (response.body().contains("Exists")) {
                            Toast.makeText(RejestracjaActivity.this, "Użytkownik z podanym loginem lub adresem e-mail już istnieje! ", Toast.LENGTH_SHORT).show();

                        } else if (response.body().contains("registered")) {

                            Toast.makeText(RejestracjaActivity.this, "Rejestracja zakończona powodzeniem!", Toast.LENGTH_SHORT).show();
                            cleanForm();
                            Intent newActivityIntent = new Intent(RejestracjaActivity.this, MainActivity.class);
                            startActivity(newActivityIntent);

                        }


                    } else {

                        Toast.makeText(RejestracjaActivity.this, "Odpowiedź od serwera jest pusta!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Toast.makeText(RejestracjaActivity.this, "Nie udało się nawiązać połączenia z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean obliczDni(DatePicker datePicker) throws ParseException {

        final Calendar max = Calendar.getInstance();
        final Calendar min = Calendar.getInstance();
        min.add(Calendar.YEAR, -15);
        min.add(Calendar.DAY_OF_YEAR, +1);
        max.add(Calendar.YEAR, -70);
        max.add(Calendar.DAY_OF_YEAR, +1);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar data = Calendar.getInstance();
        data.set(year, month, day);
        Log.e("cos", String.valueOf(data.after(min) && data.before(max)));
        return data.after(max) && data.before(min);


    }

    private boolean validation() throws ParseException {
        int value = 0;
        if (et_login.getText().length() < 5 || et_login.getText().length() > 30) {
            et_login.setError("Login musi zawierać między 5 a 30 znakow!");
            et_login.setBackgroundResource(R.drawable.error);
            value = 1;
        } else {
            et_login.setBackgroundResource(R.drawable.green);
        }

        if (et_haslo.getText().length() < 8 || et_haslo.getText().length() > 30) {
            et_haslo.setError("Hasło musi zawierać między 8 a 30 znakow!");
            et_haslo.setBackgroundResource(R.drawable.error);
            value = 1;
        } else {
            et_haslo.setBackgroundResource(R.drawable.green);

        }

        if (et_haslo.getText().toString().equals(et_pow_haslo.getText().toString())) {
            et_pow_haslo.setBackgroundResource(R.drawable.green);

        } else {
            et_pow_haslo.setError("Hasła muszą być takie same!");
            et_pow_haslo.setBackgroundResource(R.drawable.error);

            value = 1;
        }

        if (et_imie.getText().length() < 3 || et_imie.getText().length() > 13) {
            et_imie.setError("Imię musi zawierać między 3 a 13 znaków!");
            et_imie.setBackgroundResource(R.drawable.error);

            value = 1;
        } else {
            et_imie.setBackgroundResource(R.drawable.green);

        }

        if (et_nazwisko.getText().length() < 2 || et_nazwisko.getText().length() > 35) {
            et_nazwisko.setError("Nazwisko musi zawierać między 2 a 35 znaków!");
            et_nazwisko.setBackgroundResource(R.drawable.error);

            value = 1;
        } else {
            et_nazwisko.setBackgroundResource(R.drawable.green);
        }

        if (!Functions.emailValid(et_email.getText())) {
            et_email.setError("Email jest nieprawidłowy!");
            et_email.setBackgroundResource(R.drawable.error);
            value = 1;
        } else et_email.setBackgroundResource(R.drawable.green);

        if (!et_email.getText().toString().equals(et_pow_email.getText().toString())) {
            et_pow_email.setError("Adresy email muszą być takie same!");
            et_pow_email.setBackgroundResource(R.drawable.error);
            value = 1;
        } else et_pow_email.setBackgroundResource(R.drawable.green);

        if (et_telefon.getText().length() == 9 || et_telefon.getText().length() == 0) {
            et_telefon.setBackgroundResource(R.drawable.green);
        } else {
            et_telefon.setError("Jeśli już podajesz numer telefonu to zadbaj, żeby miał 9 znaków!");
            et_telefon.setBackgroundResource(R.drawable.error);
            value = 1;
        }
        if (tv_data.getText().length() < 1) {
            tv_data.setError("Wybierz datę urodzenia!");
            tv_data.setBackgroundResource(R.drawable.error);
            value = 1;
        } else if (!obliczDni(datePickerDialog.getDatePicker())) {
            tv_data.setError("Twój wiek musi być między 15 a 70");
            tv_data.setBackgroundResource(R.drawable.error);
            ageError.setVisibility(View.VISIBLE);
            value = 1;
        } else {
            tv_data.setError(null);
            tv_data.setBackgroundResource(R.drawable.green);
            ageError.setVisibility(View.INVISIBLE);
        }

        if (value == 1) return false;
        else
            return true;
    }

    private void cleanForm() {
        et_login.setText("");
        et_haslo.setText("");
        et_pow_haslo.setText("");
        et_imie.setText("");
        et_nazwisko.setText("");
        et_email.setText("");
        et_pow_email.setText("");
        et_telefon.setText("");
        tv_data.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent newActivityIntent = new Intent(RejestracjaActivity.this, MainActivity.class);
        startActivity(newActivityIntent);
    }
}