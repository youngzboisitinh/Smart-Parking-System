package com.example.smartparking;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.smartparking.databinding.ActivityBottomNaviBinding;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavi extends AppCompatActivity {
    private ActivityBottomNaviBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BottomNavi", "onCreate");
        binding = ActivityBottomNaviBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.framelayout);

        if (navHostFragment == null) {
            Log.e("BottomNaviActivity", "NavHostFragment not found! Check your FragmentContainerView and nav_graph.xml");
            return; // Thoát nếu không tìm thấy NavHostFragment
        }
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavi, navController);

        binding.bottomNavi.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home){
                navController.popBackStack(R.id.nav_home, true);
                navController.navigate(R.id.nav_home);
            }
            if(item.getItemId() == R.id.nav_person){
                if (navController.getCurrentDestination().getId() != R.id.nav_person) {
                    // Pop back stack về nav_ticket và làm mới để luôn hiển thị TicketFragment1
                    navController.popBackStack(R.id.nav_person, true);
                    navController.navigate(R.id.nav_person);
                }
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
        binding.bottomNavi.setOnItemReselectedListener(item -> {
            if(item.getItemId() == R.id.nav_home){
                navController.popBackStack(R.id.nav_home, true);
                navController.navigate(R.id.nav_home);
            }
            if (item.getItemId() == R.id.nav_person) {
                // Nếu đang ở TicketFragment2, quay về TicketFragment1
                if (navController.getCurrentDestination().getId() != R.id.nav_person) {
                    navController.popBackStack(R.id.nav_person, true);
                    navController.navigate(R.id.nav_person);
                }
            }
        });


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }
}