package org.vaadin.solidportlet;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedPortletSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import org.osgi.service.component.annotations.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Theme("solidportlettheme")
@SuppressWarnings("serial")
@Widgetset("org.vaadin.solidportlet.AppWidgetSet")
@Component(service = UI.class, property = {
        "com.liferay.portlet.display-category=category.sample",
        "javax.portlet.name=SolidVaadinPortlet",
        "javax.portlet.display-name=Solid Vaadin portlet",
        "javax.portlet.security-role-ref=power-user,user",
        "com.vaadin.osgi.liferay.portlet-ui=true"})
public class SolidportletthemePortletUI extends UI {

    private static Log log = LogFactoryUtil.getLog(SolidportletthemePortletUI.class);

    @Override
    protected void init(VaadinRequest request) {
        final String portletContextName = getPortletContextName(request);
        final Integer numOfRegisteredUsers = getPortalCountOfRegisteredUsers();
        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        setContent(layout);

        final Button button = new Button("Click Me");
        button.addClickListener(event -> {
                    layout.addComponent(new Label(
                            "Hello, World!<br>This is portlet "
                                    + portletContextName
                                    + ".<br>This portal has "
                                    + numOfRegisteredUsers
                                    + " registered users (according to the data returned by Liferay API call).",
                            ContentMode.HTML));

                }
        );//todo remove upload
        layout.addComponents(button, new Upload("Test upload", new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String s, String s1) {
                try {
                    return new FileOutputStream("c:\\work\\received" + System.currentTimeMillis() + ".bin");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    private String getPortletContextName(VaadinRequest request) {
        WrappedPortletSession wrappedPortletSession = (WrappedPortletSession) request
                .getWrappedSession();
        PortletSession portletSession = wrappedPortletSession
                .getPortletSession();

        final PortletContext context = portletSession.getPortletContext();
        final String portletContextName = context.getPortletContextName();
        return portletContextName;
    }

    private Integer getPortalCountOfRegisteredUsers() {
        Integer result = null;

        try {
            result = UserLocalServiceUtil.getUsersCount();
        } catch (SystemException e) {
            log.error(e);
        }

        return result;
    }
}
