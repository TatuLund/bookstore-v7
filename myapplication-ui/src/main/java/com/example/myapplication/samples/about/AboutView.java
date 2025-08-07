package com.example.myapplication.samples.about;

import java.util.Date;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AboutView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "About";

    public AboutView() {
        CustomLayout aboutContent = new CustomLayout("aboutview");
        aboutContent.setStyleName("about-content");

        // you can add Vaadin components in predefined slots in the custom
        // layout
        aboutContent.addComponent(new Label(FontAwesome.INFO_CIRCLE.getHtml()
                + " This application is using Vaadin "
                + Version.getFullVersion(), ContentMode.HTML), "info");

        setSizeFull();
        setStyleName("about-view");
        Chart chart = new Chart(ChartType.PIE);
        DataSeries series = new DataSeries();
        series.setData(20.5, 10.5, 7.5);
        chart.getConfiguration().addSeries(series);

        addComponents(aboutContent, chart);
        setComponentAlignment(aboutContent, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

}
