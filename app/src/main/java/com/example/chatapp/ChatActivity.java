package com.example.chatapp;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {


    private FirebaseAuth fAuth;
    private FirebaseUser currentUser;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Tabs myTabs;




    private DatabaseReference rootRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        rootRef = FirebaseDatabase.getInstance().getReference();



        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
        toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ChatApp");

        viewPager = findViewById(R.id.chatTabPager);
        myTabs = new Tabs(getSupportFragmentManager());
        viewPager.setAdapter(myTabs);

        tabLayout = findViewById(R.id.chatTab);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setupWithViewPager(viewPager);

        updateDatabase();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SpannableString spannableString = new SpannableString(menu.getItem(i).getTitle().toString());
            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), 0);
            spannableString.setSpan(new BackgroundColorSpan(Color.WHITE), 0, spannableString.length(), 0);
            menuItem.setTitle(spannableString);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.LogoutAction) {
            fAuth.signOut();
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
        }


        if(item.getItemId() == R.id.UserSettingsAction) {
            startActivity(new Intent(ChatActivity.this, UserSettingsActivity.class));
        }

        if(item.getItemId() == R.id.CreateGroup) {
            createGroup();
        }

        return true;
    }


    private void createGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter group name: ");


        EditText groupNameField = new EditText(ChatActivity.this);
        groupNameField.setTextColor(Color.BLACK);
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                if(groupName.isEmpty()) {
                    groupNameField.setError("The group name field is empty");

                } else {
                    creatingNewGroupChat(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void creatingNewGroupChat(String groupName) {
        rootRef.child("Groups").child(groupName).setValue("")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, groupName + " is created successfully", Toast.LENGTH_SHORT);
                    }
                }
            });

    }

    private void updateDatabase() {
        HashMap<String , String> profile = new HashMap<>();
        profile.put("name", currentUser.getDisplayName());
        profile.put("uid", currentUser.getUid());
        rootRef.child("Users").child(currentUser.getUid()).setValue(profile);
    }




}
