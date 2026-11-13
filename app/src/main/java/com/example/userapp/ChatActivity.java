package com.example.userapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {
    EditText messageEditText;
    Button sendButton;
    TextView chatTextView;

    DatabaseReference chatRef;
    String chatId;
    static String currentOpenChatId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        chatTextView = findViewById(R.id.chatTextView);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        chatId = intent.getStringExtra("chat_id");
        if (chatId == null) chatId = "default_chat";

        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageEditText.getText().toString().trim();
                if (!msg.isEmpty()) {
                    ChatMessage chatMessage = new ChatMessage("Вы", msg, System.currentTimeMillis());
                    chatRef.push().setValue(chatMessage);
                    messageEditText.setText("");
                }
            }
        });

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder sb = new StringBuilder();
                for (DataSnapshot msgSnap : snapshot.getChildren()) {
                    try {
                        String sender = msgSnap.child("sender").getValue(String.class);
                        String text = msgSnap.child("text").getValue(String.class);

                        if (text != null) {
                            String senderName = (sender != null) ? sender : "Соня";
                            sb.append(senderName).append(": ").append(text).append("\n");

                            if (!senderName.equals("Вы") && !currentOpenChatId.equals(chatId)) {
                                showNotification(senderName, text, chatId);
                            }
                        }
                    } catch (Exception e) {
                        sb.append("Ошибка чтения базы\n");
                    }
                }
                chatTextView.setText(sb.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                chatTextView.setText("Ошибка подключения к базе: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentOpenChatId = chatId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentOpenChatId = "";
    }

    private void showNotification(String sender, String text, String chatId) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("chat_id", chatId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                chatId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "chat_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Новое сообщение от " + sender)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 500, 200, 500});

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        notificationManager.notify(chatId.hashCode(), builder.build());
    }

    public static void createNotificationChannel(android.content.Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Сообщения";
            String description = "Уведомления о новых сообщениях";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("chat_channel", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class ChatMessage {
        public String sender;
        public String text;
        public long timestamp;

        public ChatMessage() { }

        public ChatMessage(String sender, String text, long timestamp) {
            this.sender = sender;
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}