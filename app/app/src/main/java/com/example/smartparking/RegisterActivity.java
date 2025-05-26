package com.example.smartparking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartparking.Models.ApiClient;
import com.example.smartparking.Models.LoginRequest;
import com.example.smartparking.Models.LoginResponse;
import com.example.smartparking.Models.UserRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
Button signin,signup;
ImageButton back;
EditText email,password,confirm_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back = findViewById(R.id.back);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        email = findViewById(R.id.edit_email);
        confirm_password = findViewById(R.id.edit_confirm_password);
        password = findViewById(R.id.edit_password);
        signin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, UserLoginActivity.class);
        });
        back.setOnClickListener(v -> {
            finish();
                });
        signup.setOnClickListener(v -> {
            handle_Signup();
        });
    }
    private void handle_Signup(){
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(confirm_password.getText().toString().trim())){
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        UserRequest userRequest = new UserRequest(email, password);
        authApi.signUp(userRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showSuccessDialog(userRequest);
                } else {
                    Toast.makeText(RegisterActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showSuccessDialog(UserRequest userRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.provide_name, null);
        builder.setView(dialogView);

        EditText userNameEditText = dialogView.findViewById(R.id.userNameEditText);
        Button backToLoginButton = dialogView.findViewById(R.id.backToLoginButton);
        AlertDialog dialog = builder.create();

        backToLoginButton.setOnClickListener(v -> {
            String name = userNameEditText.getText().toString().trim();
            if (!name.isEmpty()) {
                updateName(userRequest);
            }
            else {
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        dialog.show();
    }
    private void updateName(UserRequest userRequest){
        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        authApi.updateName(userRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null){
                    Intent intent = new Intent(RegisterActivity.this, UserLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(RegisterActivity.this, "Lỗi mạng: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}