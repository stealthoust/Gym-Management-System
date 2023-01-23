package my.app.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import my.app.R;


public class InfoFragment extends Fragment {

ImageView logo,fs,off,icons;
TextView offLbl,fsLbl,iconsLbl;
    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v=inflater.inflate(R.layout.fragment_info, container, false);
         findFields(v);
         setLabels();
        return v;
    }

    private void findFields(View view)
    {
        logo=view.findViewById(R.id.imageViewInfoLogo);
        off=view.findViewById(R.id.imageViewInfoFood);
        fs=view.findViewById(R.id.imageViewInfoFs);
        icons=view.findViewById(R.id.imageViewInfoIcons);

        offLbl=view.findViewById(R.id.offLbl);
        fsLbl=view.findViewById(R.id.fsLbl);
        iconsLbl=view.findViewById(R.id.iLbl);

        logo.setImageResource(R.drawable.logo);
        off.setImageResource(R.drawable.off);
        fs.setImageResource(R.drawable.fs);
        icons.setImageResource(R.drawable.icons);
    }
    private void setLabels()
    {
        offLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(offLbl.getText().toString()));
                startActivity(browserIntent);
            }
        });

        fsLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fsLbl.getText().toString()));
                startActivity(browserIntent);
            }
        });

        iconsLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(iconsLbl.getText().toString()));
                startActivity(browserIntent);
            }
        });
    }
}