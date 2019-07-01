package io.noties.markwon.app;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

abstract class AppBarItem {

    static class State {
        final String title;
        final String subtitle;

        State(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    static class Renderer {

        final TextView title;
        final TextView subtitle;

        Renderer(@NonNull View view, @NonNull View.OnClickListener themeChangeClicked) {
            this.title = view.findViewById(R.id.app_bar_title);
            this.subtitle = view.findViewById(R.id.app_bar_subtitle);
            view.findViewById(R.id.app_bar_theme_changer)
                    .setOnClickListener(themeChangeClicked);
        }

        void render(@NonNull State state) {
            title.setText(state.title);
            subtitle.setText(state.subtitle);
            Views.setVisible(subtitle, !TextUtils.isEmpty(state.subtitle));
        }
    }

    private AppBarItem() {
    }
}
