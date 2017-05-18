package ru.noties.markwon;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = AppModule.class)
@Singleton
interface AppComponent {
    MainActivitySubcomponent mainActivitySubcomponent();
}
