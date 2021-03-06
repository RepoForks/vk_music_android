package com.icapps.vkmusic;

import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.icapps.vkmusic.activity.LoginActivity;
import com.icapps.vkmusic.di.application.AppComponent;
import com.icapps.vkmusic.di.application.AppModule;
import com.icapps.vkmusic.di.application.DaggerAppComponent;
import com.icapps.vkmusic.di.user.UserComponent;
import com.icapps.vkmusic.di.user.UserModule;
import com.karumi.dexter.Dexter;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiUser;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;

/**
 * Created by maartenvangiel on 13/09/16.
 */
public class VkApplication extends Application {
    private AppComponent appComponent;
    private UserComponent userComponent;

    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(@Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken) {
            if (newToken == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);

        Paper.init(this);

        Dexter.initialize(this);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext())
                        .load(uri)
                        .into(imageView);
            }
        });
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }

    public UserComponent createUserComponent(VKApiUser user) {
        userComponent = appComponent.plus(new UserModule(user));
        return userComponent;
    }

    public void releaseUserComponent() {
        userComponent = null;
    }

}
