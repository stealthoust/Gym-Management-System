package my.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import my.app.RequestClasses.User;

public class MainMenuActivity extends AppCompatActivity {

    private TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);


        final TextView textTitle = findViewById(R.id.textTitle);

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);



        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        navigationView.getMenu().findItem(R.id.menuWyloguj).setOnMenuItemClickListener(menuItem -> {

            Intent newActivityIntent = new Intent(MainMenuActivity.this, MainActivity.class);
            User.cleanUser();
            startActivity(newActivityIntent);
            return true;
        });

        View headerView = navigationView.getHeaderView(0);
        username = (TextView) headerView.findViewById(R.id.username_header);
        username.setText("Witaj " + User.getImie() + " " + User.getNazwisko());

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);


        NavigationUI.setupWithNavController(navigationView, navController);


        //android:text="@string/imie_nazwisko"

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                textTitle.setText(navDestination.getLabel());

            }
        });
    }


}
