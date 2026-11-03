package com.example.userapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {
    EditText NameTextView, StateTextView, AgeTextView;
    User activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        int position = getIntent().getIntExtra(UserStaticInfo.POSITION, 0);
        activeUser = UserStaticInfo.users.get(position);

        Init();
        setUserInfo();
    }

    private void Init() {
        NameTextView = findViewById(R.id.NameTextView);
        StateTextView = findViewById(R.id.StateTextView);
        AgeTextView = findViewById(R.id.AgeTextView);
    }

    private void setUserInfo() {
        NameTextView.setText(activeUser.getName());
        StateTextView.setText(activeUser.getState());
        AgeTextView.setText(String.valueOf(activeUser.getAge()));
    }

    public void Back(View view) {
        onBackPressed();
    }

    public void Save(View view) {
        activeUser.setName(NameTextView.getText().toString());
        activeUser.setState(StateTextView.getText().toString());

        String ageStr = AgeTextView.getText().toString();
        activeUser.setAge(Transform.parseIntOrDefault(ageStr, activeUser.getAge()));

        MainActivity.UpdateListAndUserPanel(activeUser);
        finish();
    }
}