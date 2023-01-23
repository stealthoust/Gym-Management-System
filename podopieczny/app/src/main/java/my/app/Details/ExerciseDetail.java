package my.app.Details;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import my.app.R;

public class ExerciseDetail extends AppCompatDialogFragment {
    String kategoria,pozycja,ruch,wskazowki,video_url;
    TextView tv_kategoria,tv_pozycja,tv_ruch,tv_wskazowki;
    VideoView video;

    public ExerciseDetail(String kategoria,String pozycja,String ruch,String wskazowki,String url)
    {
        this.kategoria=kategoria;
        this.pozycja=pozycja;
        this.ruch=ruch;
        this.wskazowki=wskazowki;
        this.video_url=url;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.exercise_detail,null);

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

    @Override
    public void onResume() {
        super.onResume();
        video.start();
    }

    private void findAndFill(View view)
    {
        tv_kategoria=view.findViewById(R.id.exercise_detail_kategoria);
        tv_pozycja=view.findViewById(R.id.exercise_detail_pozycja);
        tv_ruch=view.findViewById(R.id.exercise_detail_ruch);
        tv_wskazowki=view.findViewById(R.id.exercise_detail_wskazowki);
        video=view.findViewById(R.id.exercise_detail_video);

        tv_kategoria.setText(kategoria);
        tv_pozycja.setText(String.valueOf(pozycja));
        tv_ruch.setText(String.valueOf(ruch));
        tv_wskazowki.setText(String.valueOf(wskazowki));

        if(video_url!=null)
        {
            video.setVideoURI(Uri.parse(video_url));
            video.requestFocus();
            video.start();
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                }
            });
        }



    }
}
