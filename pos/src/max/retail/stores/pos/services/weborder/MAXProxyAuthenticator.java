/********************************************************************************
 *   
 *	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
 *	
 *	Rev	1.0 	27-Jan-2016		Nadia Arora		
 *	Changes for WebOrder Functionality
 *
 ********************************************************************************/
package max.retail.stores.pos.services.weborder;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class MAXProxyAuthenticator extends Authenticator {

    private String user, password;

    public MAXProxyAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password.toCharArray());
    }
}
