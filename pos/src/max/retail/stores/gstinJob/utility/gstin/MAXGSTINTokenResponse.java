package max.retail.stores.gstinJob.utility.gstin;

public class MAXGSTINTokenResponse implements MAXGSTINTokenResponseIfc{

	private static final long serialVersionUID = -6311048833805203290L;
	
	private String token;
	
	private int expiryTimeInMinutes;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getExpiryTimeInMinutes() {
		return expiryTimeInMinutes;
	}

	public void setExpiryTimeInMinutes(int expiryTimeInMinutes) {
		this.expiryTimeInMinutes = expiryTimeInMinutes;
	}
	
}
