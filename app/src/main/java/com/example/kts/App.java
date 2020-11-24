package com.example.kts;

import android.app.Application;

public class App extends Application {
    private static App app;

    public App() {
        app = this;
    }
}
