package my.app.Details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import my.app.Functions;
import my.app.R;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotDialogActivity extends AppCompatDialogFragment {

    EditText email;
    CheckBox login,password;

    public static boolean success;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.forgot_dialog, null);
        success=true;
        findFields(view);
        builder.setView(view).setNegativeButton("Cofnij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setPositiveButton("Wyślij wiadomość na email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            if(Functions.emailValid(email.getText().toString()))
            {

                INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
                Call<String> call = inter.checkIfEmailExsists(email.getText().toString());

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().contains("1"))
                            {
                                if(!login.isChecked()&&!password.isChecked())
                                {
                                    Toast.makeText(getContext(), "Nie wybrałeś żadnej opcji!", Toast.LENGTH_SHORT).show();
                                    success=false;
                                }
                                else
                                {
                                    if(login.isChecked())
                                    {
                                        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
                                        Call<String> callLogin = inter.forgetLogin(email.getText().toString());

                                        callLogin.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.isSuccessful() && response.body() != null)
                                                {
                                                    if(!response.body().contains("został")) success=false;
                                                }
                                                else success=false;
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                            success=false;
                                            }
                                        });
                                    }
                                    if(password.isChecked())
                                    {
                                        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
                                        Call<String> callPassword = inter.resetPassword(email.getText().toString());

                                        callPassword.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.isSuccessful() && response.body() != null)
                                                {
                                                    if(!response.body().contains("wysłana")) success=false;
                                                }
                                                else success=false;
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                success=false;
                                            }
                                        });
                                    }
                                }
                            }
                            else
                            {   success=false;
                                Toast.makeText(getContext(), "Podany przez Ciebie email nie istnieje w naszym systemie.", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        success=false;
                    }
                });
            }
            else
            {
                success=false;
                Toast.makeText(getContext(), "Podany przez Ciebie email jest nieprawidłowy", Toast.LENGTH_LONG).show();
            }

           if(success) Toast.makeText(getContext(), "Wysłano wiadomość na podany adres email.", Toast.LENGTH_LONG).show(); }});

        return builder.create();
    }

    private void findFields(View view) {
        email=view.findViewById(R.id.et_reset_email);
        login=view.findViewById(R.id.checkBoxLogin);
        password=view.findViewById(R.id.checkBoxPassword);
    }
}