package oracle.retail.stores.domain.employee;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import oracle.retail.stores.commerceservices.security.EmployeeComplianceIfc;
import oracle.retail.stores.domain.financial.HardTotalsDataIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.utility.context.MappableContextIfc;

public interface EmployeeIfc extends EYSDomainIfc, HardTotalsDataIfc, MappableContextIfc {
	int LOGIN_STATUS_UNKNOWN = 0;
	int LOGIN_STATUS_ACTIVE = 1;
	int LOGIN_STATUS_INACTIVE = 2;
	String[] LOGIN_STATUS_DESCRIPTORS = new String[] { "Unknown", "Active", "Inactive" };
	int MAXIMUM_TEMP_EMPOYEE_ID = 9999;
	String PASSWORD_CHARSET = "UTF-8";
	String revisionNumber = "$Revision: /main/20 $";

	void assimilate(EmployeeIfc var1);

	String getAlternateID();

	int getDaysValid();

	EmployeeComplianceIfc getEmployeeCompliance();

	String getEmployeeID();

	EYSDate getExpirationDate();

	/** @deprecated */
	String getFullName();

	String getLoginID();

	int getLoginStatus();

	/** @deprecated */
	PersonNameIfc getName();

	int getNumberFailedPasswords();

	byte[] getPasswordBytes();

	String getEmployeePasswordSalt();

	Date getPasswordCreationDate();

	PersonNameIfc getPersonName();

	Locale getPreferredLocale();

	RoleIfc getRole();

	String getStoreID();

	EmployeeTypeEnum getType();

	List<byte[]> getFingerprintBiometrics();

	Date getLastLoginTime();

	boolean hasAccessToFunction(int var1);

	boolean isPasswordChangeRequired();

	String loginStatusToString();

	void setAlternateID(String var1);

	void setDaysValid(int var1);

	void setEmployeeCompliance(EmployeeComplianceIfc var1);

	void setEmployeeID(String var1);

	void setExpirationDate(EYSDate var1);

	/** @deprecated */
	void setFullName(String var1);

	void setLoginID(String var1);

	void setLoginStatus(int var1);

	/** @deprecated */
	void setName(PersonNameIfc var1);

	void setNumberFailedPasswords(int var1);

	/** @deprecated */
	void setPassword(String var1);

	void setPasswordBytes(byte[] var1);

	void setEmployeePasswordSalt(String var1);

	void setPasswordChangeRequired(boolean var1);

	void setPasswordCreationDate(Date var1);

	void setPersonName(PersonNameIfc var1);

	void setPreferredLocale(Locale var1);

	void setRole(RoleIfc var1);

	void setStoreID(String var1);

	void setType(EmployeeTypeEnum var1);

	void setFingerprintBiometrics(List<byte[]> var1);

	void setLastLoginTime(Date var1);

	String toJournalString(Locale var1);

	public boolean getMposFlag();

	public void setMposFlag(boolean mposFlag);

	public String getMposPassword();

	public void setMposPassword(String mposPassowrd);

	String getMPOSPasswordSalt();

	void setMPOSPasswordSalt(String mposPassSalt);

	public EmployeeIfc getEmployee();

	public void setEmployeeIfc(EmployeeIfc employee);
	public void setEmployee(Employee maxEmployee);
	
	public String getAvailableAmount();
	public String getEligibleAmount();
	public String getCompanyName();
	public void setCompanyName(String value);
	public String getSpecialEmployeeDiscountValue();
	public String getStatusCode();
}