package com.example.allfilepicker.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.allfilepicker.Adapters.FilePickerPagerAdapter;
import com.example.allfilepicker.R;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewPager setup
        ViewPager viewPager = findViewById(R.id.viewPager);
        FilePickerPagerAdapter adapter = new FilePickerPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // TabLayout setup
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }
}