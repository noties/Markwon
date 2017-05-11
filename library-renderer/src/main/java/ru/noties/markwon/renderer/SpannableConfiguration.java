package ru.noties.markwon.renderer;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.noties.markwon.spans.BlockQuoteSpan;
import ru.noties.markwon.spans.BulletListItemSpan;
import ru.noties.markwon.spans.CodeSpan;
import ru.noties.markwon.spans.HeadingSpan;
import ru.noties.markwon.spans.ThematicBreakSpan;

public class SpannableConfiguration {

    // creates default configuration
    public static SpannableConfiguration create(@NonNull Context context) {
        return new Builder(context).build();
    }

    public static Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    private final BlockQuoteSpan.Config blockQuoteConfig;
    private final CodeSpan.Config codeConfig;
    private final BulletListItemSpan.Config bulletListConfig;
    private final HeadingSpan.Config headingConfig;
    private final ThematicBreakSpan.Config thematicConfig;

    private SpannableConfiguration(Builder builder) {
        this.blockQuoteConfig = builder.blockQuoteConfig;
        this.codeConfig = builder.codeConfig;
        this.bulletListConfig = builder.bulletListConfig;
        this.headingConfig = builder.headingConfig;
        this.thematicConfig = builder.thematicConfig;
    }

    public BlockQuoteSpan.Config getBlockQuoteConfig() {
        return blockQuoteConfig;
    }

    public CodeSpan.Config getCodeConfig() {
        return codeConfig;
    }

    public BulletListItemSpan.Config getBulletListConfig() {
        return bulletListConfig;
    }

    public HeadingSpan.Config getHeadingConfig() {
        return headingConfig;
    }

    public ThematicBreakSpan.Config getThematicConfig() {
        return thematicConfig;
    }

    public static class Builder {

        private final Context context;
        private BlockQuoteSpan.Config blockQuoteConfig;
        private CodeSpan.Config codeConfig;
        private BulletListItemSpan.Config bulletListConfig;
        private HeadingSpan.Config headingConfig;
        private ThematicBreakSpan.Config thematicConfig;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setBlockQuoteConfig(@NonNull BlockQuoteSpan.Config blockQuoteConfig) {
            this.blockQuoteConfig = blockQuoteConfig;
            return this;
        }

        public Builder setCodeConfig(@NonNull CodeSpan.Config codeConfig) {
            this.codeConfig = codeConfig;
            return this;
        }

        public Builder setBulletListConfig(@NonNull BulletListItemSpan.Config bulletListConfig) {
            this.bulletListConfig = bulletListConfig;
            return this;
        }

        public Builder setHeadingConfig(@NonNull HeadingSpan.Config headingConfig) {
            this.headingConfig = headingConfig;
            return this;
        }

        public Builder setThematicConfig(@NonNull ThematicBreakSpan.Config thematicConfig) {
            this.thematicConfig = thematicConfig;
            return this;
        }

        // todo, change to something more reliable
        public SpannableConfiguration build() {
            if (blockQuoteConfig == null) {
                blockQuoteConfig = new BlockQuoteSpan.Config(
                        px(16),
                        0,
                        0
                );
            }
            if (codeConfig == null) {
                codeConfig = CodeSpan.Config.builder()
                        .setMultilineMargin(px(8))
                        .build();
            }
            if (bulletListConfig == null) {
                bulletListConfig = new BulletListItemSpan.Config(0, px(16), px(1));
            }
            if (headingConfig == null) {
                headingConfig = new HeadingSpan.Config(px(1), 0);
            }
            if (thematicConfig == null) {
                thematicConfig = new ThematicBreakSpan.Config(0, px(2));
            }
            return new SpannableConfiguration(this);
        }

        private int px(int dp) {
            return (int) (context.getResources().getDisplayMetrics().density * dp + .5F);
        }
    }
}
