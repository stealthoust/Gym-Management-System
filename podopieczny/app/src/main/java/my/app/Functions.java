package my.app;

import android.app.Application;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

public class Functions {

    public static void filterLetterOnly(EditText editText) {
        InputFilter letterFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String filtered = "";
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (!Character.isWhitespace(character) && Character.isLetter(character)) {
                        filtered += character;
                    }
                }

                return filtered;
            }

        };
        editText.setFilters(new InputFilter[]{letterFilter});
    }

    public static void filterLettersNumbersOnly(EditText editText) {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        editText.setFilters(new InputFilter[]{filter});
    }

    public static boolean emailValid(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean loginDataValidation(String login, String password, Context context){


        if (login.length() < 5 || login.length() > 30) {
            Toast.makeText(context,"Login musi zawierać pomiędzy 5 a 30 znaków",Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8 || password.length() > 30) {
            Toast.makeText(context,"Hasło musi zawierać pomiędzy 8 a 30 znaków",Toast.LENGTH_SHORT).show();
            return false;}
        else if (!login.matches("[a-zA-Z0-9]+") || !password.matches("[a-zA-Z0-9]+")) {
            Toast.makeText(context,"Login i hasło musi składać się tylko z liter i cyfr!",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
