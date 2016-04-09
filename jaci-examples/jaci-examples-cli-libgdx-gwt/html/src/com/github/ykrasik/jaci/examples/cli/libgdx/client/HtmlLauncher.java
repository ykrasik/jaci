package com.github.ykrasik.jaci.examples.cli.libgdx.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.github.ykrasik.jaci.examples.cli.libgdx.LibGdxCliExample;

public class HtmlLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(1800, 800);
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return new LibGdxCliExample();
    }
}