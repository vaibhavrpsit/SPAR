
package max.retail.stores.pos.services.modifytransaction;

import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;

public class MAXModifyTransactionCargo extends ModifyTransactionCargo{
public	String gstin;
public	boolean SystemPos;
public boolean StoreGstin;
public String CustGstin;
public MAXGSTINValidationResponseIfc CygnetResp;
public String GSTINNumber;

public String storeGSTINNumber;
 
public String getStoreGSTINNumber() {
	return storeGSTINNumber;
}
public void setStoreGSTINNumber(String storeGSTINNumber) {
	this.storeGSTINNumber = storeGSTINNumber;
}
public String getGstin() {
	return gstin;
}
public void setGstin(String gstin) {
	this.gstin = gstin;
}
public boolean getSystemPos() {
	return SystemPos;
}
public void setSystemPos(boolean systemPos) {
	SystemPos = systemPos;
}
public boolean isStoreGstin() {
	return StoreGstin;
}
public void setStoreGstin(boolean storeGstin) {
	StoreGstin = storeGstin;
}

public String getCustGstin() {
	return CustGstin;
}
public void setCustGstin(String custGstin) {
	CustGstin = custGstin;
}
public MAXGSTINValidationResponseIfc getCygnetResp() {
	return CygnetResp;
}
public void setCygnetResp(MAXGSTINValidationResponseIfc cygnetResp) {
	CygnetResp = cygnetResp;
}
public String getGSTINNumber() {
	return GSTINNumber;
}
public void setGSTINNumber(String gSTINNumber) {
	GSTINNumber = gSTINNumber;
}




}