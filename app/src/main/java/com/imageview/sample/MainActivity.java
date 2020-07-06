/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016. Viнt@rь
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.imageview.sample;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import com.imageview.ImageView;
import com.imageview.sample.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageCircleCheckBox.setChecked(binding.image.isCircle());
        binding.imageCircleCheckBox.setOnClickListener(v -> {
            if (binding.imageCircleAnimatedCheckBox.isChecked()) {
                float value = 0;
                if (!binding.image.isCircle()) {
                    value = binding.image.getHeight() * 0.5f;
                }
                ValueAnimator animator = ObjectAnimator.ofFloat(binding.image, ImageView.RADIUS, value);
                animator.setDuration(500);
                animator.addUpdateListener(animation -> {
                    binding.cornerSlider.setValue((Float) animation.getAnimatedValue());
                });
                animator.start();
            } else {
                binding.image.setCircle(binding.imageCircleCheckBox.isChecked());
                binding.cornerSlider.setValue(binding.image.getCornerRadius());
            }
        });

        binding.imageOverlappingCheckBox.setChecked(binding.image.isImageOverlap());
        binding.imageOverlappingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> binding.image.setImageOverlap(isChecked));

        if (binding.image.getCompatElevation() > 0) {
            binding.elevationSlider.setValueTo(binding.image.getCompatElevation() * 2);
            binding.elevationSlider.setValue(binding.image.getCompatElevation());
            binding.elevationSlider.addOnChangeListener((slider, value, fromUser) -> binding.image.setCompatElevation(value));
        }

        if (binding.image.getStrokeWidth() > 0) {
            binding.strokeSlider.setValueTo(binding.image.getStrokeWidth() * 2);
            binding.strokeSlider.setValue(binding.image.getStrokeWidth());
            binding.strokeSlider.addOnChangeListener((slider, value, fromUser) -> binding.image.setStrokeWidth(value));
        }

        binding.image.post(() -> {
            binding.cornerSlider.setValueTo(binding.image.getHeight() / 2f);
            binding.cornerSlider.setValue(binding.image.getCornerRadius());
            binding.cornerSlider.addOnChangeListener((slider, value, fromUser) -> {
                binding.image.setCornerRadius(value);
                binding.imageCircleCheckBox.setChecked(binding.image.isCircle());
            });
        });

        binding.rotationSlider.addOnChangeListener((slider, value, fromUser) -> binding.image.setRotation(value));
    }
}
