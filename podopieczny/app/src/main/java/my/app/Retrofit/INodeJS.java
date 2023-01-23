package my.app.Retrofit;



import java.util.ArrayList;
import java.util.List;

import my.app.RequestClasses.Coach;
import my.app.RequestClasses.Exercise;
import my.app.RequestClasses.Lifestyle;
import my.app.RequestClasses.Measurement;
import my.app.RequestClasses.PlanTreningowy;
import my.app.RequestClasses.Product;
import my.app.RequestClasses.Trening;
import my.app.RequestClasses.WeightGoals;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface INodeJS {


    @POST("users/register")
    @FormUrlEncoded
    Call<String> registerUser(@Field("login") String login,
                              @Field("password") String password,
                              @Field("email") String email,
                              @Field("imie") String imie,
                              @Field("nazwisko") String nazwisko,
                              @Field("num_tel") String telefon,
                              @Field("data_ur") String data

    );



    @POST("users/login")
    @FormUrlEncoded
    Call<String> loginUser(@Field("login") String login,
                           @Field("password") String password);


    @GET("staff/")
    Call<List<Coach>> getCoaches();

    @GET("data/lifestyle")
    Call<List<Lifestyle>> getAllLifestyles();

    @GET("data/weight")
    Call<List<WeightGoals>> getAlWeightGoals();

    @POST("users/verify")
    @FormUrlEncoded
    Call<String> verifyUserForm(@Field("id") int id,
                           @Field("waga") String waga,
                                @Field("plec") String plec,
                                @Field("wzrost") String wzrost,
                                @Field("aktywnosc") int aktywnosc,
                                @Field("cel") int cel,
                                @Field("trener") int trener_id);

    @GET("users/measurements/{id}")
    Call<List<Measurement>> getMeasurements(@Path("id") int id);

    @POST("users/measurements/add")
    @FormUrlEncoded
    Call<String> addMeasurement(@Field("id") String user_id,
                                @Field("waga") String waga);

    @DELETE("users/measurements/delete/{id}")
    Call<String> deleteMeasurement(@Path("id") String id);

    @GET("users/product/{id}")
    Call<List<Product>> getProducts(@Path("id") int id);

    @POST("users/product/add")
    @FormUrlEncoded
    Call<String> addProduct(@Field("id") Integer user_id,
                            @Field("nazwa") String nazwa,
                            @Field("kcal") double kcal,
                            @Field("w") double wegle,
                            @Field("b") double bialko,
                            @Field("t") double tluszcz,
                            @Field("waga") double waga);

    @DELETE("users/product/delete/{id}")
    Call<String> deleteProduct(@Path("id") String id);

    @GET("users/calories/{id}")
    Call<String> getCalories(@Path("id") int id);

    @GET("{id}?fields=energy-kcal_100g,carbohydrates_100g,proteins_100g,fat_100g,product_name_pl,product_name")
    Call<String> getProduct(@Path("id") String id);

    @POST("users/weight/update")
    @FormUrlEncoded
    Call<String> updateWeight(@Field("id") String user_id,
                                @Field("waga") String waga);

    @GET("users/plans/{id}")
    Call<List<PlanTreningowy>> getTrainingPlans(@Path("id") int id);

    @GET("users/plan/exercises/{id}")
    Call<List<Exercise>> getExercisesFromTrainingPlan(@Path("id") int id);

    @GET("users/trainings/{id}")
    Call<List<Trening>> getTrainings(@Path("id") int id);

    @DELETE("users/training/delete/{id}")
    Call<String> deleteTraining(@Path("id") int id);

    @POST("users/training/add")
    @FormUrlEncoded
    Call<String> addTraining(@Field("id_planu") int id_planu,
                             @Field("nazwa") String nazwa,
                             @Field("opis") String opis,
                             @Field("id") int id);

    @GET("users/trainings/count/{id}")
    Call<String> getTrainingsCount(@Path("id") int id);

    @GET("users/profile/{id}")
    Call<String> getUserInfo(@Path("id") int id);

    @POST("users/data/update")
    @FormUrlEncoded
    Call<String> updateData(@Field("waga") String waga,
                             @Field("plec") String plec,
                             @Field("wzrost") String wzrost,
                             @Field("tryb") int tryb,
                             @Field("cel_waga") int cel_waga,
                             @Field("id") int id);

    @POST("users/data/updatetrainer")
    @FormUrlEncoded
    Call<String> updateDataTrainer(@Field("waga") String waga,
                            @Field("plec") String plec,
                            @Field("wzrost") String wzrost,
                            @Field("tryb") int tryb,
                            @Field("cel_waga") int cel_waga,
                            @Field("id") int id,
                            @Field("trainer_id") int trener_id);

    @GET("users/statistics/{id}")
    Call<String> getUserStatictics(@Path("id") int id);

    @GET("users/settings/{id}")
    Call<String> getUserSettingsData(@Path("id") int id);


    @POST("users/settings/updateemail")
    @FormUrlEncoded
    Call<String> updateEmail(@Field("id") int id,
                             @Field("email") String email);

    @POST("users/settings/updatephone")
    @FormUrlEncoded
    Call<String> updatePhoneNumber(@Field("id") int id,
                                   @Field("phone_number") String phone_number);

    @POST("users/settings/updatepassword")
    @FormUrlEncoded
    Call<String> updatePassword(@Field("id") int id,
                                @Field("old_password") String old_password,
                                @Field("new_password") String new_password);

    @GET("users/email_exsists/{email}")
    Call<String> checkIfEmailExsists(@Path("email") String email);

    @POST("users/email/resetpassword")
    @FormUrlEncoded
    Call<String> resetPassword(@Field("email") String email);

    @POST("users/email/forgetlogin")
    @FormUrlEncoded
    Call<String> forgetLogin(@Field("email") String email);
}

