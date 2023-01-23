package my.app;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import my.app.Details.ChangePasswordDialog;
import my.app.Details.ForgotDialogActivity;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView reg_lbl, forget_data;
    private EditText et_login_log, et_haslo_log;
    private Button btn_zaloguj;
    public static String pass;


/*    @Override
    protected void onPostResume() {
        super.onPostResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        findFields();
        setButtons();


    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
    }

    private void findFields() {
        reg_lbl = findViewById(R.id.tv_zarejestruj);
        forget_data = findViewById(R.id.tv_forget_data);
        et_login_log = findViewById(R.id.et_login_log);
        Functions.filterLettersNumbersOnly(et_login_log);
        et_haslo_log = findViewById(R.id.et_haslo_log);
        Functions.filterLettersNumbersOnly(et_haslo_log);
        btn_zaloguj = findViewById(R.id.btn_zaloguj);

        imageView = findViewById(R.id.imageViewLogin);
        imageView.setImageResource(R.drawable.logo);
    }

    private void setButtons() {

        btn_zaloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();
            }
        });

        reg_lbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivityIntent = new Intent(MainActivity.this, RejestracjaActivity.class);
                startActivity(newActivityIntent);
            }
        });

        forget_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotDialogActivity dialog = new ForgotDialogActivity();
                dialog.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    public void parseLoginData(String response) throws ParseException {

        try {
            JSONObject jsonObject = new JSONObject(response);

            //User.setLogin(jsonObject.getString("login"));

            new User(jsonObject.getInt("id"), jsonObject.getString("login"), jsonObject.getString("imie"), jsonObject.getString("nazwisko"),
                    jsonObject.getString("numer_tel"), jsonObject.getString("data_ur"), jsonObject.getString("data_utw"), jsonObject.getString("weryfikacja"));

            pass = jsonObject.getString("password");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loginUser() {
        if (!Functions.loginDataValidation(et_login_log.getText().toString(), et_haslo_log.getText().toString(), MainActivity.this))
            return;
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.loginUser(et_login_log.getText().toString(), et_haslo_log.getText().toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().contains("login")) {
                        String jsonresponse = response.body().toString();
                        if (response.body().contains("id")) {
                            try {
                                parseLoginData(jsonresponse);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            switch (User.weryfikacja) {
                                case "n":
                                    Toast.makeText(MainActivity.this, "Pomyślnie zalogowano! ", Toast.LENGTH_SHORT).show();
                                    Intent newActivityIntent = new Intent(MainActivity.this, WeryfikacjaActivity.class);
                                    startActivity(newActivityIntent);
                                    break;
                                case "a":
                                    Toast.makeText(MainActivity.this, "Pomyślnie zalogowano! ", Toast.LENGTH_SHORT).show();
                                    Intent newActivityIntent2 = new Intent(MainActivity.this, MainMenuActivity.class);
                                    startActivity(newActivityIntent2);
                                    break;
                                case "o":
                                    Toast.makeText(MainActivity.this, "Pomyślnie zalogowano! ", Toast.LENGTH_SHORT).show();
                                    showWaitingMessage();
                                    break;
                                case "e":
                                    Toast.makeText(MainActivity.this, "Twój email jest w trakcie weryfikacji. Sprawdź swoją pocztę." +
                                            " W razie problemów skontaktuj się z recepcją.", Toast.LENGTH_LONG).show();
                                    break;
                                case "ez":
                                    Toast.makeText(MainActivity.this, "Twój email jest w trakcie zmiany. Sprawdź swoją pocztę." +
                                            " W razie problemów skontaktuj się z recepcją.", Toast.LENGTH_LONG).show();
                                    break;
                                case "z":
                                    Toast.makeText(MainActivity.this, "Twoje konto zostało zablokowane. " +
                                            "W razie problemów skontaktuj się z recepcją lub supportem.", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;

                            }
                            clean();

                        }

                    } else if (response.body() != null && response.body().contains("server-side")) {

                        Toast.makeText(MainActivity.this, "Wprowadzone dane są nieprawidłowe! ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Nieprawidłowy login lub hasło! ", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Nie udało się nawiązać połączenia z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWaitingMessage() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Weryfikacja")
                .setMessage("Jesteś po wstępnej weryfikacji. Twoje konto czeka na zaakceptowanie przez trenera. " +
                        "Jak tylko status twojego konta się zmieni, będziesz mógł/mogła się zalogować!")
                .setPositiveButton("Ok", null)
                .setCancelable(true)
                .show();


        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void clean() {
        et_login_log.setText("");
        et_haslo_log.setText("");
    }

}