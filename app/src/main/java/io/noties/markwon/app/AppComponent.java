package io.noties.markwon.app;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = AppModule.class)
@Singleton
interface AppComponent {
    MainActivitySubcomponent mainActivitySubcomponent();
}
