package com.example.smartparking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartparking.Models.ApiClient;
import com.example.smartparking.Models.LoginRequest;
import com.example.smartparking.Models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
ImageButton back;
Button signin;
EditText username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back = findViewById(R.id.back);
        signin = findViewById(R.id.signin);
        username = findViewById(R.id.edit_username);
        password = findViewById(R.id.edit_password);
        back.setOnClickListener(v -> finish());
        signin.setOnClickListener(v -> {
            handl_Signin();
        });
    }
    private void handl_Signin(){
        String username = this.username.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        LoginRequest loginRequest = new LoginRequest(username, password);
        authApi.signIn(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    String name = response.body().getUser().getName();
                    Toast.makeText(LoginActivity.this, "Welcome back admin: " + name, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, BottomNavi.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("onFailure: ", t.getMessage());
            }
        });
    }
}