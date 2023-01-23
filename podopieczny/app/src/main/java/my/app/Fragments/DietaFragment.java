package my.app.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import android.widget.TextView;
import android.widget.Toast;


import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import my.app.AddProductActivity;
import my.app.CaptureAct;
import my.app.DietaRecycle.DietaAdapter;
import my.app.DietaRecycle.DietaInterface;
import my.app.Details.ProductDetail;
import my.app.R;
import my.app.RequestClasses.Product;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DietaFragment extends Fragment implements DietaInterface {
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tv_cpm, tv_kcal, tv_roznica;
    Button btn_dodaj_produkt, btn_qr;
    RecyclerView recyclerView;

    String responseNazwa;
    Double responseWegle, responseBialko, responseTluszcz, mojeKalorie, responseKalorie;


    List<Product> produkty = new ArrayList<>();
    ArrayList<String> t_nazwa = new ArrayList<String>();
    ArrayList<Double> t_kcal = new ArrayList<Double>(), t_wegle = new ArrayList<Double>(), t_bialko = new ArrayList<Double>(), t_tluszcz = new ArrayList<Double>(), t_waga = new ArrayList<Double>();
    ArrayList<Double> t_kcal_na_100g = new ArrayList<Double>(), t_wegle_na_100g = new ArrayList<Double>(), t_bialko_na_100g = new ArrayList<Double>(), t_tluszcz_na_100g = new ArrayList<Double>();
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if (result.getContents() != null) {
            getProductApi(result.getContents());

        }
    });

    public DietaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dieta, container, false);

        findFields(v);
        setButtons(v);
        clearArray();
        loadKcalInfo();
        loadRecycleData();
        return v;
    }

    @Override
    public void onResume() {
        clearArray();
        loadKcalInfo();
        loadRecycleData();
        super.onResume();
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Podglosnij zeby uruchomic latarke");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void findFields(View v) {
        tv_cpm = v.findViewById(R.id.tv_dieta_cpm);
        tv_kcal = v.findViewById(R.id.tv_dieta_kcal);
        tv_roznica = v.findViewById(R.id.tv_dieta_roznica);
        btn_dodaj_produkt = v.findViewById(R.id.btn_dodaj_produkt);
        btn_qr = v.findViewById(R.id.btn_scan_qr);
        recyclerView = v.findViewById(R.id.recycleViewDieta);
        swipeRefreshLayout = v.findViewById(R.id.swipe_dieta);
    }

    private void setButtons(View v) {
        btn_dodaj_produkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogProduct();
            }
        });
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                clearArray();
                loadKcalInfo();
                loadRecycleData();
            }
        });
    }

    public void loadKcalInfo() {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.getCalories(User.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            tv_cpm.setText("Dzienne zapotrzebowanie: " + jsonObject.getString("cpm"));
                            tv_kcal.setText("Spożyte kalorie: " + jsonObject.getString("kcal_today"));
                            ColorStateList oldColors = tv_cpm.getTextColors();
                            if (jsonObject.getInt("roznica") < 0) {
                                tv_roznica.setText("Pozostałe kalorie: " + jsonObject.getString("roznica"));
                                tv_roznica.setTextColor(Color.RED);

                            } else {
                                tv_roznica.setText("Pozostałe kalorie: " + jsonObject.getString("roznica"));
                                tv_roznica.setTextColor(oldColors);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    Toast.makeText(getContext(), "Nie udało się wczytać informacji", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadRecycleData() {
        clearArray();
        INodeJS inter = RetrofitClient.getGsonInstance().create(INodeJS.class);
        retrofit2.Call<List<Product>> call = inter.getProducts(User.getId());

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful()) {

                    Toast.makeText(getContext(), "Nie udało się wczytać produktów", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body() != null) {
                    produkty = response.body();


                    for (Product produkt : produkty) {
                        t_nazwa.add(produkt.getNazwa());
                        t_kcal.add(produkt.getKcal());
                        t_wegle.add(produkt.getWeglowodany());
                        t_bialko.add(produkt.getBialko());
                        t_tluszcz.add(produkt.getTluszcz());
                        t_kcal_na_100g.add(produkt.getKcal_na_100g());
                        t_wegle_na_100g.add(produkt.getWeglowodany_na_100g());
                        t_bialko_na_100g.add(produkt.getBialko_na_100g());
                        t_tluszcz_na_100g.add(produkt.getTluszcz_na_100g());
                        t_waga.add(produkt.getWaga());
                    }
                    DietaAdapter dietaAdapter = new DietaAdapter(getContext(), t_nazwa, t_kcal, t_wegle, t_bialko, t_tluszcz,
                            t_kcal_na_100g, t_wegle_na_100g, t_bialko_na_100g, t_tluszcz_na_100g, t_waga, t_bialko.size(), DietaFragment.this);
                    recyclerView.setAdapter(dietaAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    recyclerView.setAdapter(null);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem", Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void clearArray() {
        t_nazwa.clear();
        t_kcal.clear();
        t_wegle.clear();
        t_bialko.clear();
        t_tluszcz.clear();
        t_waga.clear();
    }

    private void deleteProduct(AlertDialog dialog, int position) {

        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.deleteProduct(produkty.get(position).getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.body().contains("Produkt")) {
                    loadKcalInfo();
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Produkt usunięty pomyślnie!", Toast.LENGTH_SHORT).show();
                    SystemClock.sleep(500);

                    loadRecycleData();
                } else
                    Toast.makeText(getContext(), "Nie udało się usunąć produktu!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się połączyć z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getProductApi(String barcode) {
        INodeJS inter = RetrofitClient.getProductInstance().create(INodeJS.class);
        Call<String> call = inter.getProduct(barcode);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        responseWegle = jsonObject.getJSONObject("product").getDouble("carbohydrates_100g");
                        responseBialko = jsonObject.getJSONObject("product").getDouble("proteins_100g");
                        responseTluszcz = jsonObject.getJSONObject("product").getDouble("fat_100g");
                        responseKalorie = jsonObject.getJSONObject("product").getDouble("energy-kcal_100g");
                        mojeKalorie = (responseBialko * 4) + (responseWegle * 4) + (responseTluszcz * 9);
                        if (response.body().contains("product_name_pl"))
                            responseNazwa = jsonObject.getJSONObject("product").getString("product_name_pl");
                        else
                            responseNazwa = jsonObject.getJSONObject("product").getString("product_name");
                        if (responseNazwa.length() < 3) responseNazwa = "Brak nazwy produktu";
                        dialogProduct(responseNazwa);


                    } catch (JSONException e) {
                        openDialogProductAlert();
                        e.printStackTrace();
                    }
                } else openDialogProductAlert();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie można nawiązać połączenia!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openDialogProductAlert() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Wystąpił problem")
                .setMessage("Nie znaleziono produktu lub informacji na jego temat. Chcesz dodać produkt ręcznie?").setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openDialogProduct();

                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    private void openDialogProduct() {
        Intent newActivityIntent = new Intent(getContext(), AddProductActivity.class);
        startActivity(newActivityIntent);
    }

    private void dialogProduct(String nazwa) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Znaleziono produkt!")
                .setMessage("Chcesz dodać " + nazwa + " do jadłospisu?").setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogProductWeight();

                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    private void dialogProductWeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Waga spożytej porcji").setMessage("Podaj wagę spożytej porcji między 1-2500 g");


        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setGravity(Gravity.CENTER);
        builder.setView(input);


        builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String txt = "";
                Double waga = Double.parseDouble(input.getText().toString());

                if (waga < 1 || waga > 2500)
                    Toast.makeText(getContext(), "Podałeś nieprawidłową wielkość porcji. Spróbuj ponownie!", Toast.LENGTH_SHORT).show();
                else {
                    addProductFromApi(responseNazwa, mojeKalorie, responseWegle, responseBialko, responseTluszcz, waga);

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

    private void addProductFromApi(String nazwa, double kcal, double weglowodany, double bialko, double tluszcz, double waga) {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.addProduct(User.getId(), nazwa, kcal, weglowodany, bialko, tluszcz, waga);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().contains("added")) {
                            Toast.makeText(getContext(), "Pomyślnie dodano produkt!", Toast.LENGTH_SHORT).show();
                            SystemClock.sleep(500);
                            loadKcalInfo();
                            loadRecycleData();

                        } else {
                            Toast.makeText(getContext(), "Nie udało się dodać produktu!", Toast.LENGTH_SHORT).show();
                            Log.e("ERROR", "onResponse: " + response.body());
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się nawiązać połączenia z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLongItemClick(int position) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Czy na pewno chcesz usunąć produkt?")
                .setMessage("Czy chcesz aby " + produkty.get(position).getNazwa() + " został usunięty z dzisiejszego jadłospisu?")
                .setPositiveButton("Tak", null)
                .setNegativeButton("Nie", null)
                .setCancelable(true)
                .show();


        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct(dialog, position);
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

    @Override
    public void onItemClick(int position) {
        ProductDetail dialog = new ProductDetail(produkty.get(position).getNazwa(), produkty.get(position).getKcal_na_100g(), produkty.get(position).getWeglowodany_na_100g(),
                produkty.get(position).getBialko_na_100g(), produkty.get(position).getTluszcz_na_100g());
        dialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "cos");

    }


}