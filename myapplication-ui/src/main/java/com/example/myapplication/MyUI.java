package com.example.myapplication;

import javax.servlet.annotation.WebServlet;

import com.example.myapplication.samples.MainScreen;
import com.example.myapplication.samples.authentication.AccessControl;
import com.example.myapplication.samples.authentication.BasicAccessControl;
import com.example.myapplication.samples.authentication.LoginScreen;
import com.example.myapplication.samples.authentication.LoginScreen.LoginListener;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.pro.licensechecker.LicenseChecker;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@Push //(transport = Transport.WEBSOCKET_XHR)
@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("mytheme")
@Widgetset("com.example.myapplication.MyAppWidgetset")
public class MyUI extends UI {

    private AccessControl accessControl = new BasicAccessControl();
    private static final String PROJECT_NAME = "vaadin-framework";

    private CssLayout layout = new CssLayout() {
        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            if (initial) {
                addComponent(buttonToAdd);
                removeComponent(labelToRemove);
            }
        }
    };

    private Button buttonToAdd = new Button("Added from beforeClientResponse",
            new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    layout.addComponent(labelToRemove);
                }
            }) {
        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            setCaption("Add label to layout");
        }
    };

    private Label labelToRemove = new Label("Label to remove") {
        int count = 0;

        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            if (initial) {
                count++;
                setValue("Initial count: " + count);
            }
        }
    };

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("My");
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, new LoginListener() {
                @Override
                public void loginSuccessful() {
                    showMainView();
                }
            }));
        } else {
            showMainView();
        }
    }

    protected void showMainView() {
        addStyleName(ValoTheme.UI_WITH_MENU);
        setContent(new MainScreen(MyUI.this));
        getNavigator().navigateTo(getNavigator().getState());
    }

    public static MyUI get() {
        return (MyUI) UI.getCurrent();
    }

    public AccessControl getAccessControl() {
        return accessControl;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
