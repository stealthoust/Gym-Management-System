package my.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import my.app.Fragments.DietaFragment;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    private EditText et_nazwa, et_wegle, et_bialko, et_tluszcz, et_waga;
    private Button cofnij, dalej;

    private String nazwa;
    private double kcal, wegle, bialko, tluszcz, waga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        findFields();
        setButtons();

    }

    private boolean validation() {
        int value = 0;
        if (et_nazwa.getText().toString().equals("") || et_wegle.getText().toString().equals("") || et_bialko.getText().toString().equals("") || et_tluszcz.getText().toString().equals("") || et_waga.getText().toString().equals("")) {
            Toast.makeText(this, "Uzupełnij wszystkie pola, chociażby wartością 0 !", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (containsTwoDots(et_wegle.getText().toString()) || containsTwoDots(et_bialko.getText().toString()) || containsTwoDots(et_tluszcz.getText().toString()) || containsTwoDots(et_waga.getText().toString())) {
            Toast.makeText(this, "Pola nie moga zawierać dwóch kropek !", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            wegle = Double.parseDouble(et_wegle.getText().toString());
            bialko = Double.parseDouble(et_bialko.getText().toString());
            tluszcz = Double.parseDouble(et_tluszcz.getText().toString());
            waga = Double.parseDouble(et_waga.getText().toString());
        }

        if (et_nazwa.getText().toString().length() < 3||et_nazwa.getText().toString().length() > 40) {
            et_nazwa.setError("Podana nazwa musi zawierać między 3 a 40 znaków!");
            et_nazwa.setBackgroundResource(R.drawable.error);
            value = 1;
        } else {
            et_nazwa.setBackgroundResource(R.drawable.green);
            nazwa = et_nazwa.getText().toString();
        }
        if (wegle > 100 || wegle < 0) {
            et_wegle.setError("Pole zawiera liczbę spoza zakresu ( 0-100 ) lub ponad jedną kropkę!");
            et_wegle.setBackgroundResource(R.drawable.error);
            value = 1;
        } else {
            et_wegle.setBackgroundResource(R.drawable.green);
        }
        if (bialko > 100 || bialko < 0) {
            et_bialko.setError("Pole zawiera liczbę spoza zakresu ( 0-100 ) lub ponad jedną kropkę!");
            et_bialko.setBackgroundResource(R.drawable.error);
            value = 1;
        } else {
            et_bialko.setBackgroundResource(R.drawable.green);
        }
        if (tluszcz > 100 || tluszcz < 0) {
            et_tluszcz.setError("Pole zawiera liczbę spoza zakresu ( 0-100 ) lub ponad jedną kropkę!");
            et_tluszcz.setBackgroundResource(R.drawable.error);
            value = 1;
        } else {
            et_tluszcz.setBackgroundResource(R.drawable.green);
        }
        if (waga > 2500 || waga < 1) {
            et_tluszcz.setError("Pole zawiera liczbę spoza zakresu ( 1-2500 ) lub ponad jedną kropkę!");
            et_tluszcz.setBackgroundResource(R.drawable.error);
            value = 1;
        }
        else {
            et_tluszcz.setBackgroundResource(R.drawable.green);
        }
        if (wegle + bialko + tluszcz > 100) {
            Toast.makeText(this, "Suma gramatury węglowodanów, białka i tłuszczu nie może przekraczać 100 gram!", Toast.LENGTH_LONG).show();
            et_wegle.setBackgroundResource(R.drawable.error);
            et_bialko.setBackgroundResource(R.drawable.error);
            et_tluszcz.setBackgroundResource(R.drawable.error);
            value = 1;
        }

        return value != 1;
    }

    boolean containsTwoDots(String str) {
        return str.indexOf('.') != str.lastIndexOf('.');
    }

    private void findFields() {
        et_nazwa = findViewById(R.id.et_dialog_nazwa);
        et_wegle = findViewById(R.id.et_dialog_weglowodany);
        et_bialko = findViewById(R.id.et_dialog_bialko);
        et_tluszcz = findViewById(R.id.et_dialog_tluszcz);
        et_waga = findViewById(R.id.et_dialog_waga);

        Functions.filterLettersNumbersOnly(et_nazwa);
        et_wegle.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_bialko.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_tluszcz.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_waga.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        cofnij = findViewById(R.id.btn_produkt_cofnij);
        dalej = findViewById(R.id.btn_produkt_dalej);
    }

    private void setButtons() {
        dalej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    addProduct();


                    //getSupportFragmentManager().beginTransaction().replace(R.id.add_product_container,new DietaFragment()).commit();

/*                   Intent i = new Intent(AddProductActivity.this, MainMenuActivity.class);
                   startActivity(i);*/
                }
            }
        });

        cofnij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProductActivity.this.finish();
            }
        });
    }

    private void addProduct() {
        kcal = (wegle * 4) + (bialko * 4) + (tluszcz * 9);
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.addProduct(User.getId(), et_nazwa.getText().toString(), kcal, wegle, bialko, tluszcz, waga);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().contains("added")) {
                            Toast.makeText(AddProductActivity.this, "Pomyślnie dodano produkt!", Toast.LENGTH_SHORT).show();
                            SystemClock.sleep(500);
                            AddProductActivity.this.finish();
                        } else
                            Toast.makeText(AddProductActivity.this, "Nie udało się dodać produktu!", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Nie udało się nawiązać połączenia z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}