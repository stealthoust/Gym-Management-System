package my.app.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import my.app.Details.ChangePasswordDialog;
import my.app.Details.ProductDetail;
import my.app.Functions;
import my.app.MainActivity;
import my.app.R;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import my.app.WeryfikacjaActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UstawieniaFragment extends Fragment {

    EditText et_email, et_telefon;

    String email = "", telefon = "";

    private Button btn_email, btn_telefon, btn_haslo;


    public UstawieniaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_ustawienia, container, false);
        findFields(v);
        getAndSetUserData();
        setButtons();

        return v;
    }


    private void findFields(View v) {
        et_email = v.findViewById(R.id.et_email_ust);
        et_telefon = v.findViewById(R.id.et_telefon_ust);
        et_telefon.setInputType(InputType.TYPE_CLASS_NUMBER);

        btn_email = v.findViewById(R.id.btn_email_ust);
        btn_telefon = v.findViewById(R.id.btn_telefon_ust);
        btn_haslo = v.findViewById(R.id.btn_haslo_ust);
    }

    private void setButtons() {
        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_email.getText().toString().contains("Zmie")) {
                    et_email.setEnabled(true);
                    btn_email.setText("Zapisz");
                } else {
                    if (et_email.getText().toString().equals(email)) {
                        btn_email.setText("Zmień");
                        et_email.setEnabled(false);
                    } else if (Functions.emailValid(et_email.getText().toString())) {
                        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
                        Call<String> call = inter.updateEmail(User.getId(), et_email.getText().toString());

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    if (response.body().contains("already"))
                                        Toast.makeText(getContext(), "Podany email jest już w użyciu", Toast.LENGTH_SHORT).show();
                                    else if (response.body().contains("updated")) {
                                        User.cleanUser();
                                        Intent newActivityIntent = new Intent(getContext(), MainActivity.class);
                                        startActivity(newActivityIntent);
                                        Toast.makeText(getContext(), "Pomyślnie zmieniono email. Nastąpi wylogowanie", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getContext(), "Nie udało się zmienić adresu email", Toast.LENGTH_SHORT).show();
                                    }

                                } else
                                    Toast.makeText(getContext(), "Nie udało się zmienić adresu email", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(getContext(), "Podany email jest nieprawidłowy", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_telefon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_telefon.getText().toString().contains("Zmie")) {
                    et_telefon.setEnabled(true);
                    btn_telefon.setText("Zapisz");
                } else {
                    if (et_telefon.getText().toString().equals(telefon)) {
                        btn_telefon.setText("Zmień");
                        et_telefon.setEnabled(false);
                    } else if (et_telefon.getText().length() == 9 || et_telefon.getText().length() == 0) {
                        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
                        Call<String> call = inter.updatePhoneNumber(User.getId(), et_telefon.getText().toString());

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    if (response.body().contains("Error"))
                                        Toast.makeText(getContext(), "Nie udało się zaaktualizować numeru telefonu", Toast.LENGTH_SHORT).show();
                                    else if (response.body().contains("updated")) {
                                        User.cleanUser();
                                        Intent newActivityIntent = new Intent(getContext(), MainActivity.class);
                                        startActivity(newActivityIntent);
                                        Toast.makeText(getContext(), "Pomyślnie zmieniono numer telefonu. Nastąpi wylogowanie", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getContext(), "Nie udało się zmienić numeru telefonu", Toast.LENGTH_SHORT).show();
                                    }

                                } else
                                    Toast.makeText(getContext(), "Nie udało się zmienić numeru telefonu", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(getContext(), "Podany numer telefonu jest nieprawidłowy. Musi zawierać 0 lub 9 znaków", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_haslo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordDialog dialog = new ChangePasswordDialog();
                dialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "cos");
            }
        });
    }

    private void getAndSetUserData() {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.getUserSettingsData(User.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        email = jsonObject.getString("email");
                        telefon = jsonObject.getString("numer_tel");

                        et_email.setText(email);
                        et_telefon.setText(telefon);
                        lockFields();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getContext(), "Nie udało się wczytać danych", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void lockFields() {
        et_email.setEnabled(false);
        et_telefon.setEnabled(false);

    }




}