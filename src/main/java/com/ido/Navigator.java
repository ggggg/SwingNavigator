package com.ido;

import com.ido.interfaces.IRouteEvent;
import com.ido.exceptions.RouteNotFoundException;
import com.ido.interfaces.IRoute;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * @author Ido
 * @version 1.0
 * Navigator class to navigate between JFrame screens (swing).
 */
public class Navigator {

    // the main frame of the program
    private JFrame frame;
    // execute before navigating to another route
    private final List<IRouteEvent> beforeEach;
    // execute after navigating to another route
    private final List<IRouteEvent> afterEach;
    // all the routes and their paths
    private final HashMap<String, Class<? extends IRoute>> routes;
    // all the paths visited.
    private final Stack<String> history;

    /**
     * Create a navigator object to navigate between JPanel screens.
     *
     * @param frame the main frame of the program.
     */
    public Navigator(JFrame frame) {
        this(new HashMap<>(), frame);
    }

    /**
     * Create a navigator object to navigate between JPanel screens.
     *
     * @param routes the routes that can be navigated to.
     * @apiNote when using this constructor the application's frame must be manually set using the setFrame.
     */
    public Navigator(HashMap<String, Class<? extends IRoute>> routes) {
        this.routes = routes;
        history = new Stack<>();
        beforeEach = afterEach = new ArrayList<>();
    }

    /**
     * Create a navigator object to navigate between JPanel screens.
     *
     * @param routes the routes that can be navigated to.
     * @param frame  the main frame of the program.
     */
    public Navigator(HashMap<String, Class<? extends IRoute>> routes, JFrame frame) {
        this(routes);
        this.frame = frame;
    }

    /**
     * Create a navigator object to navigate between JPanel screens.
     *
     * @apiNote when using this constructor the application's frame must be manually set using the setFrame.
     */
    public Navigator() {
        this(new HashMap<>());
    }

    /**
     * Add a route, can be navigated to using the navigate method.
     *
     * @param path  the path that will be used to navigate to the route.
     * @param route the route displayed when navigated to.
     */
    public void addRoute(String path, Class<? extends IRoute> route) {
        assert path != null;
        assert route != null;
        routes.put(path, route);
    }

    /**
     * Change the currently displayed screen.
     *
     * @param path the path to be navigated to.
     */
    public void navigate(String path) {
        assert path != null;
        if (!routes.containsKey(path)) throw new RouteNotFoundException();
        IRoute route;
        try {
            route = routes.get(path).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        move(path, null, route);
    }

    /**
     * Change the currently displayed screen.
     *
     * @param path  the path to be navigated to.
     * @param args  the args passed the the route's constructor.
     * @param route the route that is being navigated to.
     */
    private void move(String path, Object[] args, IRoute route) {
        beforeEach.forEach(x -> x.run(path, args, route));
        frame.setContentPane(route.getPanel());
        Dimension size;
        if (frame.getContentPane() != null) {
            frame.getRootPane().setVisible(false);
            size = frame.getRootPane().getSize();
        } else {
            size = frame.getSize();
        }
        route.getPanel().setPreferredSize(size);
        frame.setContentPane(route.getPanel());
        frame.getRootPane().setVisible(true);
        history.push(path);
        route.onStarted();
        afterEach.forEach(x -> x.run(path, args, route));
    }

    /**
     * Change the currently displayed screen.
     * Route must have constructor that accepts Object[].
     *
     * @param path the path to be navigated to.
     * @param args the args passed the the route's constructor.
     */
    public void navigate(String path, Object[]... args) {
        if (!routes.containsKey(path)) throw new RouteNotFoundException();
        Constructor<? extends IRoute> ctor;
        IRoute route;
        try {
            ctor = routes.get(path).getConstructor(Object[].class);
            route = ctor.newInstance(new Object[]{args});
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        move(path, args, route);
    }

    /**
     * Go the the previous page.
     */
    public void back() {
        history.pop();
        navigate(history.pop());
    }

    /**
     * @return the navigation history.
     */
    public Stack<String> getHistory() {
        return history;
    }

    /**
     * @return the application's frame.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * @param frame the application's frame.
     */
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    /**
     * @param function a function that runs before each route
     */
    public void beforeEach(IRouteEvent function) {
        beforeEach.add(function);
    }

    /**
     * @param function a function that runs after each route
     */
    public void afterEach(IRouteEvent function) {
        afterEach.add(function);
    }
}
