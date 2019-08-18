package com.example.chatapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAccessorAdapter extends FragmentPagerAdapter {
    public TabAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                ChatFragment cf = new ChatFragment();
                return cf;

            case 1:
                ContactFragment cnf = new ContactFragment();
                return cnf;

            case 2:
                GroupFragment gf = new GroupFragment();
                return gf;

            default:
                return null;
        }
//        return null;null null
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        return super.getPageTitle(position);

        switch (position) {
            case 0:
//                ChatFragment cf = new ChatFragment();
                return "Chats";

            case 1:
                // ContactFragment cnf = new ContactFragment();
                return "Contacts";

            case 2:
                // GroupsFragment gf = new GroupsFragment();
                return "Groups";

            default:
                return null;
        }
    }
}
