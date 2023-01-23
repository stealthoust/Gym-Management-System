package my.app.Details;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import my.app.R;

public class ProductDetail extends AppCompatDialogFragment {

    String nazwa;
    Double kalorie,weglowodany,bialko,tluszcz;
    public ProductDetail(String nazwa,double kcal,double weglowodany,double bialko, double tluszcz)
    {
        this.nazwa=nazwa;
        this.kalorie=kcal;
        this.weglowodany=weglowodany;
        this.bialko=bialko;
        this.tluszcz=tluszcz;

    }
    TextView tv_nazwa,tv_kcal,tv_wegle,tv_bialko,tv_tluszcz;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.product_100g,null);

        builder.setView(view)
                .setNegativeButton("Cofnij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

    findAndFill(view);

        return builder.create();
    }

    private void findAndFill(View view)
    {
        tv_nazwa=view.findViewById(R.id.product_detail_nazwa);
        tv_kcal=view.findViewById(R.id.product_detail_kcal);
        tv_wegle=view.findViewById(R.id.product_detail_weglowodany);
        tv_bialko=view.findViewById(R.id.product_detail_bialko);
        tv_tluszcz=view.findViewById(R.id.product_detail_tluszcz);

        tv_nazwa.setText(nazwa);
        tv_kcal.setText(String.valueOf(kalorie));
        tv_wegle.setText(String.valueOf(weglowodany));
        tv_bialko.setText(String.valueOf(bialko));
        tv_tluszcz.setText(String.valueOf(tluszcz));
    }
}
