package max.retail.stores.domain.gstin;

import java.io.Serializable;

public interface MAXGSTINValidationResponseIfc extends Serializable{

	public String getStatusCode();
	public void setStatusCode(String statusCode);
	public String getStjCd();
	public void setStjCd(String stjCd);
	public String getLgnm();
	public void setLgnm(String lgnm);
	public String getStj();
	public void setStj(String stj);
	public String getDty();
	public void setDty(String dty);
	public String getCxdt();
	public void setCxdt(String cxdt);
	public String getGstin();
	public void setGstin(String gstin);
	public String getLstupdt();
	public void setLstupdt(String lstupdt);
	public String getRgdt();
	public void setRgdt(String rgdt);
	public String getCtb();
	public void setCtb(String ctb);
	public String getTradeNam();
	public void setTradeNam(String tradeNam);
	public String getSts();
	public void setSts(String sts);
	public String getCtjCd();
	public void setCtjCd(String ctjCd);
	public String getCtj();
	public void setCtj(String ctj);
	public String getBnm();
	public void setBnm(String bnm);
	public String getSt();
	public void setSt(String st);
	public String getLoc();
	public void setLoc(String loc);
	public String getBno();
	public void setBno(String bno);
	public String getDst();
	public void setDst(String dst);
	public String getStcd();
	public void setStcd(String stcd);
	public String getCity();
	public void setCity(String city);
	public String getFlno();
	public void setFlno(String flno);
	public String getLt();
	public void setLt(String lt);
	public String getPncd();
	public void setPncd(String pncd);
	public String getLg();
	public void setLg(String lg);
	public String getErrorCode();
	public void setErrorCode(String errorCode);
	public String getErrormsg();
	public void setErrormsg(String errormsg);
	public String getBarcode();
	public void setBarcode(String barcode);
}
