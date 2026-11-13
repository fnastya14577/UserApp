package com.example.userapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Context context;
    LayoutInflater layoutInflater;
    static UserListAdapter userListAdapter;

    static FrameLayout UserPanel;
    static TextView NameTextView, StateTextView, AgeTextView;
    static int positionActiveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UserStaticInfo();
        Init();

        ChatActivity.createNotificationChannel(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void Init() {
        UserPanel = findViewById(R.id.userPanel);
        NameTextView = findViewById(R.id.NameTextView);
        StateTextView = findViewById(R.id.StateTextView);
        AgeTextView = findViewById(R.id.AgeTextView);

        listView = findViewById(R.id.listView);
        context = this;
        layoutInflater = LayoutInflater.from(context);
        userListAdapter = new UserListAdapter();
        listView.setAdapter(userListAdapter);
    }

    private static class UserListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return UserStaticInfo.users.size();
        }

        @Override
        public User getItem(int position) {
            return UserStaticInfo.users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View currentView, ViewGroup parent) {
            if (currentView == null) {
                currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            }

            User currentUser = getItem(position);

            TextView nameView = currentView.findViewById(R.id.NameTextView);
            TextView stateView = currentView.findViewById(R.id.StateTextView);
            FrameLayout stateRound = currentView.findViewById(R.id.StateRound);

            nameView.setText(currentUser.getName());
            stateView.setText(currentUser.getState());

            switch (currentUser.getStateSignal()) {
                case 0:
                    stateRound.setBackgroundResource(R.drawable.back_offline);
                    break;
                case 1:
                    stateRound.setBackgroundResource(R.drawable.back_online);
                    break;
                case 2:
                    stateRound.setBackgroundResource(R.drawable.back_departed);
                    break;
            }

            currentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parent.getContext(), ChatActivity.class);
                    // ВОТ ЗДЕСЬ ИСПРАВЛЕНО: добавлен "_" + position в конце
                    intent.putExtra("chat_id", "chat_" + getItem(position).getName() + "_" + position);
                    parent.getContext().startActivity(intent);
                }
            });

            return currentView;
        }
    }

    public void BackToList(View view) {
        UserVisibility(false);
    }

    private static void UserVisibility(boolean visible) {
        if (visible) {
            UserPanel.setVisibility(View.VISIBLE);
        } else {
            UserPanel.setVisibility(View.GONE);
        }
    }

    private static void InitPanel(User item) {
        NameTextView.setText(item.getName());
        StateTextView.setText(item.getState());
        AgeTextView.setText(String.valueOf(item.getAge()));
    }

    public static void UpdateListAndUserPanel(User user) {
        userListAdapter.notifyDataSetChanged();
        InitPanel(user);
    }

    public void GoToUserProfile(int position) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(UserStaticInfo.POSITION, position);
        startActivity(intent);
    }

    public void EditUser(View view) {
        GoToUserProfile(positionActiveUser);
    }
}