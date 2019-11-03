package androidx.appcompat.widget;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

@RestrictTo(LIBRARY_GROUP)
public final class AppCompatImageHelperUtils {

    @SuppressLint("RestrictedApi")
    public static void setSupportImageTintList(@NonNull AppCompatImageHelper imageHelper, ColorStateList tint) {
        imageHelper.setSupportImageTintList(tint);
    }

    @SuppressLint("RestrictedApi")
    public static ColorStateList getSupportImageTintList(@NonNull AppCompatImageHelper imageHelper) {
        return imageHelper.getSupportImageTintList();
    }

    @SuppressLint("RestrictedApi")
    public static void setSupportImageTintMode(@NonNull AppCompatImageHelper imageHelper, PorterDuff.Mode tintMode) {
        imageHelper.setSupportImageTintMode(tintMode);
    }

    @SuppressLint("RestrictedApi")
    public static PorterDuff.Mode getSupportImageTintMode(@NonNull AppCompatImageHelper imageHelper) {
        return imageHelper.getSupportImageTintMode();
    }

    @SuppressLint("RestrictedApi")
    public static void applySupportImageTint(@NonNull AppCompatImageHelper imageHelper) {
        imageHelper.applySupportImageTint();
    }

    @SuppressLint("RestrictedApi")
    public static boolean hasOverlappingRendering(@NonNull AppCompatImageHelper imageHelper) {
        return imageHelper.hasOverlappingRendering();
    }
}
