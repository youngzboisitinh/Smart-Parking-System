package com.example.smartparking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartparking.Models.ApiClient;
import com.example.smartparking.Models.LoginRequest;
import com.example.smartparking.Models.LoginResponse;
import com.example.smartparking.Models.Userlogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {
    ImageButton back;
    Button signin, signup;
    TextView forgotpass;
    EditText email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        email = findViewById(R.id.edit_email);
        password = findViewById(R.id.edit_password);
        forgotpass = findViewById(R.id.fogotpass);
        signin.setOnClickListener(v -> {
            handle_Signin();
        });
        signup.setOnClickListener(v -> {
            Intent intent1 = new Intent(UserLoginActivity.this, RegisterActivity.class);
            startActivity(intent1);
        });
    }
    private void handle_Signin(){
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        Userlogin userlogin = new Userlogin(email, password);
        authApi.signIn(userlogin).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    String name = response.body().getUser().getName();
                    Toast.makeText(UserLoginActivity.this, "Welcome back: " + name, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserLoginActivity.this, BottomNavi.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserLoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(UserLoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("onFailure: ", t.getMessage());
            }
        });
    }
}