/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imageview.core;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.google.android.material.animation.AnimatorSetCompat;
import com.google.android.material.animation.MotionSpec;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;

/**
 * Common functionality for all classes implementing {@link MotionStrategy}
 */
abstract class BaseMotionStrategy implements MotionStrategy {

    private final Context mContext;
    private final ImageView mView;
    private final List<AnimatorListener> mListeners = new ArrayList<>();
    private final AnimatorTracker mTracker;

    @Nullable
    private MotionSpec mMotionSpec;
    @Nullable
    private MotionSpec mDefaultMotionSpec;

    protected BaseMotionStrategy(@NonNull ImageView view, AnimatorTracker tracker) {
        mView = view;
        mContext = view.getContext();
        mTracker = tracker;
    }

    @Override
    public final void setMotionSpec(@Nullable MotionSpec motionSpec) {
        mMotionSpec = motionSpec;
    }

    @Override
    @SuppressLint("RestrictedApi")
    public final MotionSpec getCurrentMotionSpec() {
        if (mMotionSpec != null) {
            return mMotionSpec;
        }

        if (mDefaultMotionSpec == null) {
            mDefaultMotionSpec = MotionSpec.createFromResource(mContext, getDefaultMotionSpecResource());
        }

        return Preconditions.checkNotNull(mDefaultMotionSpec);
    }

    @Override
    public final void addAnimationListener(@NonNull AnimatorListener listener) {
        mListeners.add(listener);
    }

    @Override
    public final void removeAnimationListener(@NonNull AnimatorListener listener) {
        mListeners.remove(listener);
    }

    @NonNull
    @Override
    public final List<AnimatorListener> getListeners() {
        return mListeners;
    }

    @Override
    @Nullable
    public MotionSpec getMotionSpec() {
        return mMotionSpec;
    }

    @Override
    @CallSuper
    public void onAnimationStart(Animator animator) {
        mTracker.onNextAnimationStart(animator);
    }

    @Override
    @CallSuper
    public void onAnimationEnd() {
        mTracker.clear();
    }

    @Override
    @CallSuper
    public void onAnimationCancel() {
        mTracker.clear();
    }

    @Override
    public AnimatorSet createAnimator() {
        return createAnimator(getCurrentMotionSpec());
    }

    @NonNull
    @SuppressLint("RestrictedApi")
    protected AnimatorSet createAnimator(@NonNull MotionSpec spec) {
        List<Animator> animators = new ArrayList<>();

        if (spec.hasPropertyValues("opacity")) {
            animators.add(spec.getAnimator("opacity", mView, View.ALPHA));
        }

        if (spec.hasPropertyValues("scale")) {
            animators.add(spec.getAnimator("scale", mView, View.SCALE_Y));
            animators.add(spec.getAnimator("scale", mView, View.SCALE_X));
        }

/*        if (spec.hasPropertyValues("width")) {
            animators.add(spec.getAnimator("width", fab, ExtendedFloatingActionButton.WIDTH));
        }

        if (spec.hasPropertyValues("height")) {
            animators.add(spec.getAnimator("height", fab, ExtendedFloatingActionButton.HEIGHT));
        }*/

        AnimatorSet set = new AnimatorSet();
        AnimatorSetCompat.playTogether(set, animators);
        return set;
    }
}