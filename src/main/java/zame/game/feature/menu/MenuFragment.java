package zame.game.feature.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import zame.game.App;
import zame.game.R;
import zame.game.core.app.BaseFragment;
import zame.game.core.util.Common;
import zame.game.feature.sound.SoundManager;
import zame.game.flavour.config.AppConfig;
import zame.game.flavour.gplay.MenuFragmentGPlayHelper;

public class MenuFragment extends BaseFragment {
    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    private View playWrapperView;
    private final MenuFragmentGPlayHelper gPlayHelper = new MenuFragmentGPlayHelper();
    private ImageView ivBuyVip;

    public MenuFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isForgottenFragment) {
            gPlayHelper.onCreate();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.menu_fragment, container, false);

        if (!isForgottenFragment) {
            playWrapperView = viewGroup.findViewById(R.id.play_wrapper);

            viewGroup.findViewById(R.id.play).setOnClickListener(v -> {
                activity.soundManager.playSound(SoundManager.SOUND_BTN_PRESS);
                activity.continueGame();
            });

            viewGroup.findViewById(R.id.buyCoin).setOnClickListener(v -> {
                showDialogBuyDiamond();
            });

            viewGroup.findViewById(R.id.achievements).setOnClickListener(v -> {
                activity.soundManager.playSound(SoundManager.SOUND_BTN_PRESS);
                activity.showFragment(activity.achievementsFragment);
            });

            gPlayHelper.createFragmentView(viewGroup, activity);
            ivBuyVip = viewGroup.findViewById(R.id.buyCoin);

            if(activity.isVIPUser()) {

            }
        }

        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isForgottenFragment) {
            playWrapperView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bounce));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isForgottenFragment) {
            activity.soundManager.setPlaylist(SoundManager.LIST_MAIN);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus && !isForgottenFragment) {
            gPlayHelper.updateRateWrapVisibility();
        }
    }
}
