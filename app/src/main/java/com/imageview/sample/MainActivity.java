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

import android.os.Build;
import android.os.Bundle;

import com.imageview.sample.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageCircleCheckBox.setChecked(binding.image.isCircle());
        binding.imageCircleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> binding.image.setCircle(isChecked));

        binding.imageOverlappingCheckBox.setEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP); // overlapping doesn't support on pre-lollipop devices
        binding.imageOverlappingCheckBox.setChecked(binding.image.isImageOverlap());
        binding.imageOverlappingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> binding.image.setImageOverlap(isChecked));

        if (binding.image.getCompatElevation() > 0) {
            binding.elevationSlider.setValueTo(binding.image.getCompatElevation() * 2);
            binding.elevationSlider.setValueFrom(binding.image.getCompatElevation());
            binding.elevationSlider.setOnChangeListener((slider, value) -> binding.image.setCompatElevation(value));
        }

        if (binding.image.getStrokeWidth() > 0) {
            binding.strokeSlider.setValueTo(binding.image.getStrokeWidth() * 2);
            binding.strokeSlider.setValueFrom(binding.image.getStrokeWidth());
            binding.strokeSlider.setOnChangeListener((slider, value) -> binding.image.setStrokeWidth(value));
        }

        binding.image.post(() -> {
            float currentValue = binding.image.isCircle() ? binding.image.getHeight() / 2f : binding.image.getCornerRadius();

            binding.cornerSlider.setValueTo(binding.image.getHeight() / 2f);
            binding.cornerSlider.setValue(currentValue);
            binding.cornerSlider.setOnChangeListener((slider, value) -> binding.image.setCornerRadius(value));
        });

        binding.rotationSlider.setOnChangeListener((slider, value) -> binding.image.setRotation(value));

        //binding.loadImageButton.setOnClickListener(v -> binding.image.setImageURL("https://images.pexels.com/photos/326055/pexels-photo-326055.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
/*
        final ImageView imageViewTest1 = findViewById(R.id.image_view_3);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //imageViewTest1.setImageURL("https://avatars2.githubusercontent.com/u/8938207?v=3&s=460");
                //imageViewTest1.setClipToOutline(true);
                //imageViewTest1.setBorderColor(ColorUtils.setAlphaComponent(imageViewTest1.getBorderColor().getDefaultColor(), 15));
            }
        }, 1000);
        //imageViewTest1.setImageURL("https://i.ytimg.com/vi/6lt2JfJdGSY/maxresdefault.jpg");

        final ImageView imageViewTest = findViewById(R.id.image_view_2);
        //imageViewTest.setImageURL("https://avatars2.githubusercontent.com/u/8938207?v=3&s=460");
        imageViewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        //final ImageView imageView = findViewById(R.id.image_view);
        //final FloatingActionButton fab = findViewById(R.id.fab);
        //imageView.setCompatElevation(20f);
/*        new Handler().postDelayed(() -> {
            //imageView.setImageURL("https://avatars2.githubusercontent.com/u/8938207?v=3&s=460");
        }, 1000);*/
        //imageView.setImageURL("https://avatars2.githubusercontent.com/u/8938207?v=3&s=460");
        //imageView.setImageURL("https://pp.vk.me/c604531/v604531553/1d0f6/9gae9OTT_xo.jpg");
        //imageView.setImageResource(R.drawable.ic_noavatar);
        //imageView.setClipToOutline(true);

        //LinearLayout linearLayout = findViewById(R.id.test_view);
        /*linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        //Button button = findViewById(R.id.button_test);
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image, null));
                //imageView.setBackgroundTintList(null);
                //imageViewTest.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null)));
                //imageView.setCompatElevation(20f);
                //imageView.setCircle(!imageView.isCircle());
                //imageView.setBorderWidth(30f);
                //imageView.setBorderColor(Color.GREEN);
                //imageView.setImageURL("https://avatars2.githubusercontent.com/u/8938207?v=3&s=460");
                //imageView.setImageURL("https://pp.vk.me/c604531/v604531553/1d0f6/9gae9OTT_xo.jpg");
                //imageView.setImageURL("https://s3.amazonaws.com/attached-images/point_images/a_15c85ed78d5349e1a2f984180e8efeb1.jpg");
                //imageView.setCircle(!imageView.isCircle());
            }
        });*/
    }
}
