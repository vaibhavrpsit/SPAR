package max.retail.stores.domain.gstin;

public class MAXGSTINValidationResponse implements MAXGSTINValidationResponseIfc{

	private static final long serialVersionUID = -6311048833805203290L;

	private String statusCode;
	private String stjCd;
	private String lgnm;
	private String stj;
	private String dty;
	private String cxdt;
	private String gstin;
	private String lstupdt;
	private String rgdt;
	private String ctb;
	private String tradeNam;
	private String sts;
	private String ctjCd;
	private String ctj;
	private String barcode;
	// address
	private String bnm;
	private String st;
	private String loc;
	private String bno;
	private String dst;
	private String stcd;
	private String city;
	private String flno;
	private String lt;
	private String pncd;
	private String lg;
	private String errorCode=null;
	private String errormsg=null;

	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStjCd() {
		return stjCd;
	}
	public void setStjCd(String stjCd) {
		this.stjCd = stjCd;
	}
	public String getLgnm() {
		return lgnm;
	}
	public void setLgnm(String lgnm) {
		this.lgnm = lgnm;
	}
	public String getStj() {
		return stj;
	}
	public void setStj(String stj) {
		this.stj = stj;
	}
	public String getDty() {
		return dty;
	}
	public void setDty(String dty) {
		this.dty = dty;
	}
	public String getCxdt() {
		return cxdt;
	}
	public void setCxdt(String cxdt) {
		this.cxdt = cxdt;
	}
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getLstupdt() {
		return lstupdt;
	}
	public void setLstupdt(String lstupdt) {
		this.lstupdt = lstupdt;
	}
	public String getRgdt() {
		return rgdt;
	}
	public void setRgdt(String rgdt) {
		this.rgdt = rgdt;
	}
	public String getCtb() {
		return ctb;
	}
	public void setCtb(String ctb) {
		this.ctb = ctb;
	}
	public String getTradeNam() {
		return tradeNam;
	}
	public void setTradeNam(String tradeNam) {
		this.tradeNam = tradeNam;
	}
	public String getSts() {
		return sts;
	}
	public void setSts(String sts) {
		this.sts = sts;
	}
	public String getCtjCd() {
		return ctjCd;
	}
	public void setCtjCd(String ctjCd) {
		this.ctjCd = ctjCd;
	}
	public String getCtj() {
		return ctj;
	}
	public void setCtj(String ctj) {
		this.ctj = ctj;
	}
	public String getBnm() {
		return bnm;
	}
	public void setBnm(String bnm) {
		this.bnm = bnm;
	}
	public String getSt() {
		return st;
	}
	public void setSt(String st) {
		this.st = st;
	}
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public String getBno() {
		return bno;
	}
	public void setBno(String bno) {
		this.bno = bno;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	public String getStcd() {
		return stcd;
	}
	public void setStcd(String stcd) {
		this.stcd = stcd;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getFlno() {
		return flno;
	}
	public void setFlno(String flno) {
		this.flno = flno;
	}
	public String getLt() {
		return lt;
	}
	public void setLt(String lt) {
		this.lt = lt;
	}
	public String getPncd() {
		return pncd;
	}
	public void setPncd(String pncd) {
		this.pncd = pncd;
	}
	public String getLg() {
		return lg;
	}
	public void setLg(String lg) {
		this.lg = lg;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	

}

