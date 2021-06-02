package com.eightsines.esmediadtor;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eightsines.espromo.PromoView;

import java.util.ArrayList;
import java.util.List;

public class Mediadtor {
    // private static final long ROTATE_INTERVAL_APPODEAL = 30L * 1000L;
    private static final long ROTATE_INTERVAL_ESPROMO = 15L * 1000L;
    private static final long RECHECK_INTERVAL = 5000L;

    // private final String applicationKey;
    // private final boolean isConsentGiven;
    // private final boolean isTestingAds;
    private final String debugLogTag;
    private final Handler handler;
    @SuppressWarnings({ "FieldCanBeLocal", "unused", "RedundantSuppression" }) private MediadtorListener listener;
    private Activity bannerActivity;
    private ViewGroup bannerContainerView;
    private List<View> bannerViews;
    private int bannerIndex;

    @SuppressWarnings({ "unused", "RedundantSuppression" })
    public Mediadtor(
            @Nullable String applicationKey,
            boolean isConsentGiven,
            boolean isTestingAds,
            String debugLogTag) {

        // this.applicationKey = applicationKey;
        // this.isConsentGiven = isConsentGiven;
        // this.isTestingAds = isTestingAds;
        this.debugLogTag = debugLogTag;

        handler = new Handler();
    }

    @SuppressWarnings({ "unused", "RedundantSuppression" })
    public void onActivityCreate(@NonNull Activity activity, @NonNull MediadtorListener listener) {

    }

    @SuppressWarnings({ "unused", "RedundantSuppression" })
    public void onActivityResume(@NonNull Activity activity) {
        if (bannerContainerView == null || bannerViews == null) {
            return;
        }
    }

    public void onActivityPause() {}

    public boolean isInterstitialLoaded() {
        return false;
    }

    @SuppressWarnings({ "unused", "RedundantSuppression" })
    public void showInterstitial(@NonNull Activity activity) {
        // if (applicationKey != null) {
        //     Appodeal.show(activity, Appodeal.INTERSTITIAL);
        // }
    }

    public boolean isRewardedVideoEnabled() {
        // return (applicationKey != null);
        return false;
    }

    public boolean isRewardedVideoLoaded() {
        // return (applicationKey == null ? false : Appodeal.isLoaded(Appodeal.REWARDED_VIDEO));
        return false;
    }

    @SuppressWarnings({ "unused", "RedundantSuppression" })
    public void showRewardedVideo(@NonNull Activity activity) {
        // if (applicationKey == null) {
        //     Appodeal.show(activity, Appodeal.REWARDED_VIDEO);
        // }
    }

    public void showBanner(@NonNull Activity activity, @NonNull ViewGroup containerView) {
    }

    @SuppressWarnings({ "unused", "RedundantSuppression" })
    public void hideBanner(@NonNull Activity activity) {
    }
}
