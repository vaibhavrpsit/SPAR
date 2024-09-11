package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;

public interface MAXGSTINTokenResponseIfc extends Serializable{

	public String getToken();
	public void setToken(String token);
	public int getExpiryTimeInMinutes();
	public void setExpiryTimeInMinutes(int expiryTimeInMinutes);
}
