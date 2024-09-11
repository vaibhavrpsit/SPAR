package max.retail.stores.gstinJob.utility.gstin;

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
