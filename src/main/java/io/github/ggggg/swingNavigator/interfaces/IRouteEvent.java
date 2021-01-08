package io.github.ggggg.swingNavigator.interfaces;

@FunctionalInterface
public interface IRouteEvent {
    void run(String path, Object[] args, IRoute panel);
}
