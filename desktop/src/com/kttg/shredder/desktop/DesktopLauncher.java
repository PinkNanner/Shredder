package com.kttg.shredder.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kttg.shredder.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.resizable = false;
//		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width/2;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height/2;
		config.fullscreen = false;
		new LwjglApplication(new Main(), config);
	}
}
