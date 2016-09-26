package fr.ca.bamboo.plugins;

import org.codehaus.plexus.util.StringUtils;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.ww2.BambooActionSupport;
import com.atlassian.bandana.BandanaManager;

/**
 * Store the global Rocket Chat Server configuration
 * 
 * @author cagarini
 *
 */
public class RocketChatGlobalConfiguration extends BambooActionSupport {

	private static final long serialVersionUID = 1965096233541267102L;
	
	public static final String PROP_RC_SERVER = "rc.server.global";
	public static final String PROP_RC_USER = "rc.user.global";
	public static final String PROP_RC_PASSWORD = "rc.password.global";

	private String rcServer = null;
	private String rcUser = null;
	private String rcPassword = null;
	
	private BandanaManager bandanaManager = null;

	@Override
	public String doInput() throws Exception {
		
		return INPUT;
	}
	
	/**
	 * Set values in bandana
	 */
	@Override
	public String doExecute() throws Exception {
		
		bandanaManager.setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, PROP_RC_SERVER, this.rcServer);
		bandanaManager.setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, PROP_RC_USER, this.rcUser);
		bandanaManager.setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, PROP_RC_PASSWORD, this.rcPassword);
		
		return SUCCESS;
	}
	
	/**
	 * Validate values
	 */
	@Override
	public void validate() {
		
		if(StringUtils.isEmpty(this.rcPassword)) {
			addFieldError("rcPassword", "Please (re)enter the password.");
		}
		if(StringUtils.isEmpty(this.rcUser)) {
			addFieldError("rcUser", "Please enter the RocketChat user.");
		}
		if(StringUtils.isEmpty(this.rcServer)) {
			addFieldError("rcServer", "Please enter the RocketChat server.");
		}
	}

	public void setBandanaManager(BandanaManager bandanaManager) {
		this.bandanaManager = bandanaManager;
	}
	
	public String getRcServer() {
		return (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, PROP_RC_SERVER);
	}

	public void setRcServer(String rcServer) {
		this.rcServer = rcServer;
	}

	public String getRcUser() {
		return (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, PROP_RC_USER);
	}

	public void setRcUser(String rcUser) {
		this.rcUser = rcUser;
	}

	public String getRcPassword() {
		return (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, PROP_RC_PASSWORD);
	}

	public void setRcPassword(String rcPassword) {
		this.rcPassword = rcPassword;
	}
}
