package com.example.smartparking;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class user_extend_fragment extends Fragment {
    private CheckBox checkbox1, checkbox2, checkbox3;
    private Button sendbtn;
    private ImageButton backbtn;
    private EditText editText1, editText2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_user_extend, container, false);
        checkbox1 = view.findViewById(R.id.checkbox1);
        checkbox2 = view.findViewById(R.id.checkbox2);
        checkbox3 = view.findViewById(R.id.checkbox3);
        sendbtn = view.findViewById(R.id.sendbtn);
        backbtn = view.findViewById(R.id.back);
        editText1 = view.findViewById(R.id.edittext1);
        editText2 = view.findViewById(R.id.edittext2);

        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
        @Override
            public void onCheckedChanged(CompoundButton buttonview, boolean b ){
            if (b)
            {
                checkbox2.setChecked(false);
                checkbox3.setChecked(false);
            }
        }
        });
        checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonview, boolean b ){
                if (b)
                {
                    checkbox1.setChecked(false);
                    checkbox3.setChecked(false);
                }
            }
        });
        checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonview, boolean b ){
                if (b)
                {
                    checkbox1.setChecked(false);
                    checkbox2.setChecked(false);
                }
            }
        });
        backbtn.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.action_ticketFragment2_to_ticketFragment1);
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    openFeedbackDialog(Gravity.CENTER);
                }
                else {
                    showErrorDialog();
                }
            }


        });
        return view;
    }

    private void openFeedbackDialog(int gravity){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.provide_mail_code);

        Window window = dialog.getWindow();
        if (window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.show();

    }

    private void showErrorDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("ERROR")
                .setMessage("Vui lòng nhập đủ thông tin!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void text() {
        new AlertDialog.Builder(requireContext())
                .setTitle("hehe")
                .setMessage("Vui lòng nhập đủ thông tin!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private boolean isInputValid() {
        // Kiểm tra null trước khi gọi getText()
        if (editText1 == null) {
            throw new IllegalStateException("editText1 is null");
        }
        if (editText2 == null) {
            throw new IllegalStateException("editText2 is null");
        }
        if (checkbox1 == null || checkbox2 == null || checkbox3 == null) {
            throw new IllegalStateException("One of the CheckBoxes is null");
        }
        boolean EditText1Filled = !editText1.getText().toString().trim().isEmpty();
        boolean EditText2Filled = !editText2.getText().toString().trim().isEmpty();
        boolean AnyCheckBoxChecked =checkbox1.isChecked() || checkbox2.isChecked() || checkbox3.isChecked();
        return EditText1Filled && EditText2Filled && AnyCheckBoxChecked;
    }
}