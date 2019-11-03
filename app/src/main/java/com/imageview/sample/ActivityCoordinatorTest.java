package com.imageview.sample;

import android.os.Bundle;

import com.imageview.sample.databinding.ActivityCoordinatorBinding;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Viнt@rь on 27.10.2019
 */
public final class ActivityCoordinatorTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCoordinatorBinding binding = ActivityCoordinatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
