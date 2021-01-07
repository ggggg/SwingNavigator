package com.ido.interfaces;

import javax.swing.*;

public interface IRoute {
    JPanel getPanel();

    default void onStarted() {
    }
}
