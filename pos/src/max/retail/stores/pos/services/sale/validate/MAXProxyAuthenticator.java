/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Copyright (c) 2015 Lifestyle.    All Rights Reserved.  */

package max.retail.stores.pos.services.sale.validate;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * @author mohd.arif
 *
 */
public class MAXProxyAuthenticator extends Authenticator {

    private String user, password;

    public MAXProxyAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user,password.toCharArray());
    }
    
}