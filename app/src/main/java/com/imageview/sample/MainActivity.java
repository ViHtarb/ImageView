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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.imageview.ImageView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.image_view);
        //imageView.setCompatElevation(20f);
        //imageView.setImageURL("https://avatars2.githubusercontent.com/u/8938207?v=3&s=460");
        //imageView.setImageURL("https://pp.vk.me/c604531/v604531553/1d0f6/9gae9OTT_xo.jpg");
        //imageView.setImageResource(R.drawable.ic_noavatar);
        //imageView.setClipToOutline(true);

        Button button = findViewById(R.id.button_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageURL("https://avatars2.githubusercontent.com/u/8938207?v=3&s=460");
                //imageView.setCircle(!imageView.isCircle());
                //imageView.setBorderWidth(30f);
                //imageView.setBorderColor(Color.GREEN);
                //imageView.setImageURL("https://avatars2.githubusercontent.com/u/8938207?v=3&s=460");
                //imageView.setImageURL("https://pp.vk.me/c604531/v604531553/1d0f6/9gae9OTT_xo.jpg");
                //imageView.setImageURL("https://s3.amazonaws.com/attached-images/point_images/a_15c85ed78d5349e1a2f984180e8efeb1.jpg");
                //imageView.setCircle(!imageView.isCircle());
            }
        });
    }
}
