package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Tabs extends FragmentPagerAdapter {


    public Tabs(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Messages messages = new Messages();
                return messages;

            case 1:
                GroupMess groupMess = new GroupMess();
                return groupMess;

            case 2:
                Calls calls = new Calls();
                return calls;

            case 3:
                Contacts contacts = new Contacts();
                return contacts;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Calls";

            case 3:
                return "People";

            default:
                return null;
        }
    }
}
