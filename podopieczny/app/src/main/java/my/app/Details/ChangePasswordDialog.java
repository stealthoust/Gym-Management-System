package my.app.Details;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import my.app.Functions;
import my.app.MainActivity;
import my.app.R;
import my.app.RequestClasses.User;
import my.app.Retrofit.INodeJS;
import my.app.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordDialog extends AppCompatDialogFragment {

    EditText old_password, new_password, repeat_new_password;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_password_dialog, null);
        findFields(view);

        builder.setView(view).setNegativeButton("Cofnij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setPositiveButton("Zmień", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (validatePassword(new_password.getText().toString(), repeat_new_password.getText().toString(), old_password.getText().toString()))
                    changePassword(old_password.getText().toString(), new_password.getText().toString());

            }
        });

        return builder.create();
    }


    private void findFields(View view) {
        old_password = view.findViewById(R.id.old_password_dialog);
        new_password = view.findViewById(R.id.new_password_dialog);
        repeat_new_password = view.findViewById(R.id.repeat_new_password_dialog);

        Functions.filterLettersNumbersOnly(old_password);
        Functions.filterLettersNumbersOnly(new_password);
        Functions.filterLettersNumbersOnly(repeat_new_password);

    }

    private void changePassword(String old_password, String new_password) {
        INodeJS inter = RetrofitClient.getScalarInstance().create(INodeJS.class);
        Call<String> call = inter.updatePassword(User.getId(), old_password, new_password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().contains("Wrong"))
                        Toast.makeText(getContext(), "Podałeś nieprawidłowe hasło", Toast.LENGTH_SHORT).show();
                    else if (response.body().contains("updated")) {
                        User.cleanUser();
                        Intent newActivityIntent = new Intent(getContext(), MainActivity.class);
                        startActivity(newActivityIntent);
                        Toast.makeText(getContext(), "Pomyślnie zmieniono hasło. Nastąpi wylogowanie", Toast.LENGTH_SHORT).show();
                    }
                    else if (response.body().contains("server-side")) {
                        Toast.makeText(getContext(), "Wprowadzono niepoprawne dane!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getContext(), "Nie udało się zmienić hasła", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Nie udało się nawiązać połączenia z serwerem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validatePassword(String p1, String p2, String old) {
        if (old.equals(p1)) {
            Toast.makeText(getContext(), "Nowe i stare hasło nie może być identyczne", Toast.LENGTH_SHORT).show();
            return false;
        } else if (p1.length() < 8) {
            Toast.makeText(getContext(), "Nowe hasło nie może mieć mniej niż 8 znaków", Toast.LENGTH_SHORT).show();
            return false;
        } else if (p1.length() > 30) {
            Toast.makeText(getContext(), "Nowe hasło nie może mieć więcej niż 30 znaków", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!p1.equals(p2)) {
            Toast.makeText(getContext(), "Nowe hasło i jego powtórzenie musi być identyczne!!", Toast.LENGTH_LONG).show();
            return false;
        } else return true;

    }

}
