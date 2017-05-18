package ru.noties.markwon;

import dagger.Subcomponent;

@Subcomponent
@ActivityScope
interface MainActivitySubcomponent {
    void inject(MainActivity activity);
}
