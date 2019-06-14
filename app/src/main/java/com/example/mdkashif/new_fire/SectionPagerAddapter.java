package com.example.mdkashif.new_fire;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionPagerAddapter extends FragmentPagerAdapter {
    public SectionPagerAddapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatFragment chatFragment=new ChatFragment();
                return chatFragment;
            case 1:
                FriendFragment friendFragment=new FriendFragment();
                return friendFragment;
            case 2:

                RequestFragment requestFragment=new RequestFragment();
                return requestFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
       switch (position){
           case 0:
               return "CHATS";
           case 1:
               return "FRIEND";
           case 2:
               return "REQUESTS";
               default:
                   return null;
       }
    }
}
