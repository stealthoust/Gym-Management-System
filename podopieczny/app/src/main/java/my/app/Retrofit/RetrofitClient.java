package my.app.Retrofit;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static String ip="192.168.0.116";

    private static Retrofit instance;
    final static OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(1500, TimeUnit.MILLISECONDS)
            .connectTimeout(1500, TimeUnit.MILLISECONDS)
            .build();
    public static Retrofit getScalarInstance()
    {
            instance=new Retrofit.Builder()
                    .baseUrl("http://"+ip+":3000/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build();
        return instance;
    }

    public static Retrofit getGsonInstance()
    {
            instance= new Retrofit.Builder()
            .baseUrl("http://"+ip+":3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();
        return instance;
    }

    public static Retrofit getProductInstance()
    {
        instance=new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/api/v2/product/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        return instance;
    }
}
