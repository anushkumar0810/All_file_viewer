package com.example.allfilepicker.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.allfilepicker.Fragments.AudioFragment;
import com.example.allfilepicker.Fragments.DocumentFragment;
import com.example.allfilepicker.Fragments.ImageFragment;
import com.example.allfilepicker.Fragments.VideoFragment;

public class FilePickerPagerAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles = new String[]{"Images", "Videos", /*"Documents",*/ "Audio"};

    public FilePickerPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ImageFragment();
            case 1:
                return new VideoFragment();
            case 2:
                return new AudioFragment();
            /*case 3:
                return new AudioFragment();*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
