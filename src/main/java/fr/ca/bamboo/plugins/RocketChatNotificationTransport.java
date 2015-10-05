package fr.ca.bamboo.plugins;

import fr.ca.bamboo.plugins.rc.RocketChatConnection;
import com.atlassian.bamboo.notification.Notification;
import com.atlassian.bamboo.notification.Notification.HtmlImContentProvidingNotification;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import org.apache.commons.lang.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RocketChat Transport Implementation
 *
 * @author cagarini
 */
public class RocketChatNotificationTransport implements NotificationTransport {

    private static final Logger log = Logger.getLogger(RocketChatNotificationTransport.class.getName());

    // Config
    private String rcServer = null;
    private String rcUser = null;
    private String rcPassword = null;

    private String targetChannel = null;

    // Injectables
    private ResultsSummaryManager resultsSummaryManager;

    public RocketChatNotificationTransport(String rcServer, String rcUser, String rcPassword, String targetChannel,
                                           ResultsSummaryManager resultsSummaryManager) {

        this.rcServer = rcServer;
        this.rcUser = rcUser;
        this.rcPassword = rcPassword;

        this.targetChannel = targetChannel;

        this.resultsSummaryManager = resultsSummaryManager;
    }

    @Override
    public void sendNotification(Notification notification) {

        // IM Messages to be sent
        String plainTextMessage = null;
        String richTextMessage = null;

        HtmlImContentProvidingNotification imContentProvider = (HtmlImContentProvidingNotification) notification;
        plainTextMessage = imContentProvider.getIMContent();

        // Make sure we have a message
        if (StringUtils.isEmpty(richTextMessage)) {
            richTextMessage = plainTextMessage;
        }
        if (StringUtils.isEmpty(plainTextMessage)) {
            plainTextMessage = richTextMessage;
        }

        // Send when we have users to send to and a message to send
        if (targetChannel != null && richTextMessage != null && plainTextMessage != null) {

            RocketChatConnection rcConnection = null;

            try {
                // Establish connection with rocketchat server
                rcConnection = new RocketChatConnection(this.rcServer, this.rcUser, this.rcPassword);
                rcConnection.sendMessage(targetChannel, plainTextMessage);
            } finally {
                if (rcConnection != null && rcConnection.isLoggedIn()) {
                    rcConnection.logout();
                }
            }
        } else {
            log.log(Level.INFO, "Not sending since no targets and/or text is available to be sent.");
        }
    }
}
