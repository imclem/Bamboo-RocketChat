package fr.ca.bamboo.plugins;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.notification.recipients.AbstractNotificationRecipient;
import com.atlassian.bamboo.plugin.descriptor.NotificationRecipientModuleDescriptor;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.bandana.BandanaManager;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RocketChat Recipient Configuration
 * 
 * @author cagarini
 *
 */
public class RocketChatNotificationRecipient extends AbstractNotificationRecipient {
	
	// Constants
	private static final String XML_CHANNEL = "channel";
	public static final String PROP_RC_CHANNEL = "rc.channel";
	public static final String VAL_RC_CHANNEL = "rcchannel";

	// Config
	private String rcChannel = null;

	// Injectables
	private TemplateRenderer templateRenderer;
    private ResultsSummaryManager resultsSummaryManager;
    private BandanaManager bandanaManager;
    
	/**
	 * Return our transport implementation
	 */
	@Override
	public List<NotificationTransport> getTransports() {
		
		// Get configuration from bandana
		String sameTimeServer = (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, RocketChatGlobalConfiguration.PROP_RC_SERVER);
		String sameTimeUser = (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, RocketChatGlobalConfiguration.PROP_RC_USER);
		String sameTimePassword = (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, RocketChatGlobalConfiguration.PROP_RC_PASSWORD);
		
		// Add to collection n return
		List<NotificationTransport> list = new  ArrayList<NotificationTransport>();
		list.add(new RocketChatNotificationTransport(sameTimeServer, sameTimeUser, sameTimePassword, this.rcChannel, this.resultsSummaryManager));
		
		return list;
	}
	
	/**
	 * Take the map of config data from action and extracts the parameters
	 */
	@Override
	public void populate(Map<String, String[]> params) {
		
		// RocketChat Channel
		if(params.containsKey(PROP_RC_CHANNEL))
			this.rcChannel = params.get(PROP_RC_CHANNEL)[0];
	}
	
	/**
	 * Init based on persisted configuration data
	 */
	@Override
	public void init(String configurationData) {
		
		// Skip when nothing to process
		if(configurationData==null || configurationData.length()==0)
			return;
		
		// Parse XML
		SAXBuilder sb = new SAXBuilder();
		
		try {
			
			Document doc = sb.build(new StringReader(configurationData));
			Element root = doc.getRootElement();
			
			this.rcChannel = root.getChildText(XML_CHANNEL);
			
		} catch (JDOMException e) {
			// Ignore, cant happen
		} catch (IOException e) {
			// Ignore, cant happen
		}
	}
	
	/**
	 * Generate serialized configuration to be persisted
	 */
	@Override
	public String getRecipientConfig() {

		// Root
		Document doc = new Document();
		Element root = new Element(RocketChatNotificationRecipient.class.getName());
		doc.addContent(root);
		
		// User
		Element xmlUser = new Element(XML_CHANNEL);
		if(this.rcChannel !=null)xmlUser.setText(this.rcChannel);
		root.addContent(xmlUser);	
		
		// Serialize
		Format prettyFormat = Format.getPrettyFormat();
		prettyFormat.setOmitDeclaration(true);
		XMLOutputter outputter = new XMLOutputter(prettyFormat);
        String xmlString = outputter.outputString(doc);
        
        return xmlString;
	}
	
	/**
	 * Generate the edit html
	 */
	@Override
	public String getEditHtml() {
		
		// Get our template
        String editTemplateLocation = ((NotificationRecipientModuleDescriptor)getModuleDescriptor()).getEditTemplate();
        
        // Inject settings into the template context
        Map<String, Object> context = new HashMap<String, Object>();

        // User
        if (this.rcChannel != null)
            context.put(VAL_RC_CHANNEL, this.rcChannel);
        
        // Render html
        return templateRenderer.render(editTemplateLocation, context);
	}
	
	/**
	 * Generate the view html
	 */
	@Override
	public String getViewHtml() {
		
		StringBuilder b = new StringBuilder();
		b.append("RocketChat Notification Configuration").append("<br/>");
		b.append("<b>Send to channel:</b>").append("<br/>").append(this.rcChannel).append("<br/>");
		return b.toString();
	}
	
	@Override
	public ErrorCollection validate(Map<String, String[]> params) {
		
		ErrorCollection errors = new SimpleErrorCollection();
		
		// Master Configuration should be present
		String rocketChatServer = (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, RocketChatGlobalConfiguration.PROP_RC_SERVER);
		String rocketChatUser = (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, RocketChatGlobalConfiguration.PROP_RC_USER);
		String rocketChatPassword = (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, RocketChatGlobalConfiguration.PROP_RC_PASSWORD);
		
		if(StringUtils.isEmpty(rocketChatServer)
				|| StringUtils.isEmpty(rocketChatUser)
				|| StringUtils.isEmpty(rocketChatPassword)){
			errors.addErrorMessage("Please verify the RocketChat server configuration, it is incomplete.");
		}

		return errors;
	}
	
    public void setTemplateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public void setResultsSummaryManager(ResultsSummaryManager resultsSummaryManager) {
        this.resultsSummaryManager = resultsSummaryManager;
    }
    
    public void setBandanaManager(BandanaManager bandanaManager) {
		this.bandanaManager = bandanaManager;
	}
}
