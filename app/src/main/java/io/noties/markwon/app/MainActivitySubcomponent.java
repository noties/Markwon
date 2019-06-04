package io.noties.markwon.app;

import dagger.Subcomponent;

@Subcomponent
@ActivityScope
interface MainActivitySubcomponent {
    void inject(MainActivity activity);
}
