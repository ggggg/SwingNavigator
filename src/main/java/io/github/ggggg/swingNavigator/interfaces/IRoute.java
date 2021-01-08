package io.github.ggggg.swingNavigator.interfaces;

import javax.swing.*;

public interface IRoute {
    JPanel getPanel();

    default void onStarted() {
    }
}
