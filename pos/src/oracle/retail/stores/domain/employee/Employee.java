package oracle.retail.stores.domain.employee;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import oracle.retail.stores.commerceservices.security.EmployeeComplianceIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsFormatException;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.utility.I18NHelper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import max.retail.stores.domain.employee.MAXEmployee;

public class Employee implements EmployeeIfc {
	public static final String revisionNumber = "$Revision: /main/25 $";
	protected static final Logger logger = Logger.getLogger(Employee.class);
	static final long serialVersionUID = 4769627265523328119L;
	protected String alternateID;
	protected int daysValid;
	public static int availAmount = 0;
	protected EmployeeComplianceIfc employeeCompliance;
	protected String employeeID;
	protected EYSDate expirationDate;
	/** @deprecated */
	protected String fullName;
	protected String loginID;
	protected int loginStatus;
	protected PersonNameIfc name;
	protected int numberFailedPasswords;
	protected byte[] password;
	protected String employeePasswordSalt;
	protected boolean passwordChangeRequired;
	protected Date passwordCreationDate;
	protected Locale preferredLocale;
	protected RoleIfc role;
	protected String storeID;
	protected EmployeeTypeEnum type;
	protected List<byte[]> fingerprintBiometrics;
	protected Date lastLoginTime;

	public boolean mposFlag;
	public String mposPassword;
	public String mposPassSalt;
	public EmployeeIfc employeeIfc;
	//changage for  Mpos by Abhishek on 09-10-2024
	//public EmployeeIfc employeeIfc;

	public static Employee maxEmployee = null;
	protected String statusCode = "";
	protected String specialEmployeeDisount = "";
	protected String companyName = "";
	protected String eligibleAmount = "";
	protected String availableAmount = "";

	//changage for  Mpos by Abhishek on 09-10-2024 End

	public static void main(String[] args) {
		Employee clsEmployee;
		if (args.length == 5) {
			clsEmployee = new Employee(args[0], args[1], args[2], args[3], args[4]);
		} else {
			clsEmployee = new Employee();
		}

		System.out.println("Employee Object created " + clsEmployee.getLoginID());

		try {
			HardTotalsBuilderIfc builder = null;
			Serializable obj = null;
			EmployeeIfc a2 = null;
			Employee a1 = new Employee();
			builder = DomainGateway.getFactory().getHardTotalsBuilderInstance();
			a1.getHardTotalsData(builder);
			obj = builder.getHardTotalsOutput();
			builder.setHardTotalsInput(obj);
			a2 = (EmployeeIfc) builder.getFieldAsClass();
			a2.setHardTotalsData(builder);
			if (a1.equals(a2)) {
				System.out.println("Empty Employees are equal");
			} else {
				System.out.println("Empty Employees are NOT equal");
			}

			a1.setEmployeeID("1234");
			a1.setAlternateID("5678");
			a1.setPasswordBytes("jgs".getBytes("UTF-8"));
			a1.setLoginID("JGS");
			a1.setFullName("John Gray Swan");
			a1.setLoginStatus(3);
			Role role = new Role();
			role.setRoleID(10);
			role.setTitle("5678");
			RoleFunctionIfc[] funcs = new RoleFunctionIfc[5];

			for (int i = 0; i < 5; ++i) {
				RoleFunctionIfc rf = DomainGateway.getFactory().getRoleFunctionInstance();
				rf.setFunctionID(i);
				rf.setTitle("rolefunction" + i);
				funcs[i] = rf;
			}

			role.setFunctions(funcs);
			PersonNameIfc pn = DomainGateway.getFactory().getPersonNameInstance();
			pn.setFirstName("Wild");
			pn.setMiddleName("Bill");
			pn.setLastName("Hickock");
			pn.setSalutation("Mr.");
			pn.setSurname("None");
			pn.setTitle("Sir");
			a1.setName(pn);
			builder = DomainGateway.getFactory().getHardTotalsBuilderInstance();
			a1.getHardTotalsData(builder);
			obj = builder.getHardTotalsOutput();
			builder.setHardTotalsInput(obj);
			a2 = (EmployeeIfc) builder.getFieldAsClass();
			a2.setHardTotalsData(builder);
			if (a1.equals(a2)) {
				System.out.println("Full Employees are equal");
			} else {
				System.out.println("Full Employees are NOT equal");
			}
		} catch (UnsupportedEncodingException var10) {
			System.out.println("Password conversion failed:");
			var10.printStackTrace();
		} catch (HardTotalsFormatException var11) {
			System.out.println("Employee conversion failed:");
			var11.printStackTrace();
		}

	}

	public Employee() {
		this.alternateID = "";
		this.daysValid = 0;
		this.employeeCompliance = null;
		this.employeeID = "";
		this.expirationDate = null;
		this.fullName = "";
		this.loginID = "";
		this.loginStatus = 2;
		this.numberFailedPasswords = 0;
		this.passwordChangeRequired = true;
		this.preferredLocale = null;
		this.storeID = "";
		this.type = EmployeeTypeEnum.STANDARD;
	}

	/** @deprecated */
	public Employee(String empID, String altID, String login, String pwd, String name) {
		this(empID, altID, login, pwd, (PersonNameIfc) null);
		this.fullName = name;
	}

	/** @deprecated */
	public Employee(String empID, String altID, String login, String pwd, PersonNameIfc pName) {
		this.alternateID = "";
		this.daysValid = 0;
		this.employeeCompliance = null;
		this.employeeID = "";
		this.expirationDate = null;
		this.fullName = "";
		this.loginID = "";
		this.loginStatus = 2;
		this.numberFailedPasswords = 0;
		this.passwordChangeRequired = true;
		this.preferredLocale = null;
		this.storeID = "";
		this.type = EmployeeTypeEnum.STANDARD;
		this.employeeID = empID;
		this.alternateID = altID;
		this.setPassword(pwd);
		this.loginID = login;
		this.name = pName;
	}

	public void assimilate(EmployeeIfc drone) {
		((Employee) drone).setCloneAttributes(this);
	}

	public Object clone() {
		EmployeeIfc c = DomainGateway.getFactory().getEmployeeInstance();
		this.setCloneAttributes(c);
		return c;
	}

	protected void setCloneAttributes(EmployeeIfc clone) {
		if (this.employeeID != null) {
			clone.setEmployeeID(this.employeeID);
		}

		if (this.alternateID != null) {
			clone.setAlternateID(this.alternateID);
		}

		if (this.password != null) {
			clone.setPasswordBytes(this.password);
		}

		if (this.employeePasswordSalt != null) {
			clone.setEmployeePasswordSalt(this.employeePasswordSalt);
		}

		clone.setNumberFailedPasswords(this.numberFailedPasswords);
		if (this.passwordCreationDate != null) {
			clone.setPasswordCreationDate((Date) this.passwordCreationDate.clone());
		}

		if (this.loginID != null) {
			clone.setLoginID(this.loginID);
		}

		if (this.fullName != null) {
			clone.setFullName(this.fullName);
		}

		if (this.name != null) {
			clone.setPersonName((PersonNameIfc) this.name.clone());
		}

		clone.setLoginStatus(this.loginStatus);
		if (this.role != null) {
			clone.setRole((RoleIfc) this.role.clone());
		}

		clone.setDaysValid(this.daysValid);
		if (this.expirationDate != null) {
			clone.setExpirationDate((EYSDate) this.expirationDate.clone());
		}

		clone.setPasswordChangeRequired(this.passwordChangeRequired);
		if (this.storeID != null) {
			clone.setStoreID(this.storeID);
		}

		clone.setType(this.type);
		if (this.preferredLocale != null) {
			clone.setPreferredLocale((Locale) this.getPreferredLocale().clone());
		}

		if (this.fingerprintBiometrics != null) {
			List<byte[]> newfingerprintBiometrics = new ArrayList();
			Iterator i$ = this.fingerprintBiometrics.iterator();

			while (i$.hasNext()) {
				byte[] fp = (byte[]) i$.next();
				newfingerprintBiometrics.add(fp.clone());
			}

			clone.setFingerprintBiometrics(newfingerprintBiometrics);
		}

		if (this.lastLoginTime != null) {
			clone.setLastLoginTime((Date) this.lastLoginTime.clone());
		}

	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else {
			boolean isEqual = true;

			try {
				Employee c = (Employee) obj;
				if (!Util.isObjectEqual(this.employeeID, c.getEmployeeID())) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.alternateID, c.getAlternateID())) {
					isEqual = false;
				} else if (!Arrays.equals(this.password, c.password)) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.employeePasswordSalt, c.employeePasswordSalt)) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.loginID, c.getLoginID())) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.name, c.getPersonName())) {
					isEqual = false;
				} else if (this.loginStatus != c.getLoginStatus()) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.role, c.getRole())) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.preferredLocale, c.getPreferredLocale())) {
					isEqual = false;
				} else if (this.daysValid != c.getDaysValid()) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.expirationDate, c.getExpirationDate())) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.storeID, c.getStoreID())) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.type, c.getType())) {
					isEqual = false;
				} else if (this.passwordChangeRequired != c.isPasswordChangeRequired()) {
					isEqual = false;
				} else if (this.numberFailedPasswords != c.getNumberFailedPasswords()) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.getPasswordCreationDate(), c.getPasswordCreationDate())) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.getFingerprintBiometrics(), c.getFingerprintBiometrics())) {
					isEqual = false;
				} else if (!Util.isObjectEqual(this.getLastLoginTime(), c.getLastLoginTime())) {
					isEqual = false;
				} else {
					isEqual = true;
				}
			} catch (Exception var4) {
				isEqual = false;
			}

			return isEqual;
		}
	}

	public String getAlternateID() {
		return this.alternateID;
	}

	public int getDaysValid() {
		return this.daysValid;
	}

	public EmployeeComplianceIfc getEmployeeCompliance() {
		return this.employeeCompliance;
	}

	public String getEmployeeID() {
		return this.employeeID;
	}

	public EYSDate getExpirationDate() {
		return this.expirationDate;
	}

	/** @deprecated */
	public String getFullName() {
		return this.fullName;
	}

	public void getHardTotalsData(HardTotalsBuilderIfc builder) {
		builder.appendStringObject(this.getClass().getName());
		builder.appendString(this.employeeID);
		builder.appendString(this.alternateID);
		builder.appendString(JdbcUtilities.base64encode(this.getPasswordBytes()));
		builder.appendString(this.loginID);
		builder.appendString(this.fullName);
		builder.appendInt(this.loginStatus);
		if (this.name == null) {
			builder.appendStringObject("null");
		} else {
			this.name.getHardTotalsData(builder);
		}

		if (this.role == null) {
			builder.appendStringObject("null");
		} else {
			this.role.getHardTotalsData(builder);
		}

	}

	public String getLoginID() {
		return this.loginID;
	}

	public int getLoginStatus() {
		return this.loginStatus;
	}

	/** @deprecated */
	public PersonNameIfc getName() {
		return this.getPersonName();
	}

	public int getNumberFailedPasswords() {
		return this.numberFailedPasswords;
	}

	public byte[] getPasswordBytes() {
		return this.password;
	}

	public String getEmployeePasswordSalt() {
		return this.employeePasswordSalt;
	}

	public Date getPasswordCreationDate() {
		return this.passwordCreationDate;
	}

	public PersonNameIfc getPersonName() {
		return this.name;
	}

	public Locale getPreferredLocale() {
		return this.preferredLocale;
	}

	public String getRevisionNumber() {
		return "$Revision: /main/25 $";
	}

	public RoleIfc getRole() {
		return this.role;
	}

	public String getStoreID() {
		return this.storeID;
	}

	public EmployeeTypeEnum getType() {
		return this.type;
	}

	public List<byte[]> getFingerprintBiometrics() {
		return this.fingerprintBiometrics;
	}

	public Date getLastLoginTime() {
		return this.lastLoginTime;
	}

	public boolean hasAccessToFunction(int functionID) {
		return this.getRole().getFunctionAccess(functionID);
	}

	public boolean isPasswordChangeRequired() {
		return this.passwordChangeRequired;
	}

	public String loginStatusToString() {
		String strResult;
		try {
			strResult = EmployeeIfc.LOGIN_STATUS_DESCRIPTORS[this.loginStatus];
		} catch (ArrayIndexOutOfBoundsException var3) {
			strResult = "Invalid login status [" + this.loginStatus + "]";
		}

		return strResult;
	}

	public void setAlternateID(String value) {
		this.alternateID = value;
	}

	public void setDaysValid(int value) {
		this.daysValid = value;
	}

	public void setEmployeeCompliance(EmployeeComplianceIfc employeeCompliance) {
		this.employeeCompliance = employeeCompliance;
	}

	public void setEmployeeID(String value) {
		this.employeeID = value;
	}

	public void setExpirationDate(EYSDate value) {
		this.expirationDate = value;
	}

	/** @deprecated */
	public void setFullName(String value) {
		this.fullName = value;
	}

	public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException {
		this.employeeID = builder.getStringField();
		this.alternateID = builder.getStringField();
		this.setPasswordBytes(JdbcUtilities.base64decode(builder.getStringField()));
		this.loginID = builder.getStringField();
		this.fullName = builder.getStringField();
		this.loginStatus = builder.getIntField();
		this.name = (PersonNameIfc) builder.getFieldAsClass();
		if (this.name != null) {
			this.name.setHardTotalsData(builder);
		}

		this.role = (RoleIfc) builder.getFieldAsClass();
		if (this.role != null) {
			this.role.setHardTotalsData(builder);
		}

	}

	public void setLoginID(String value) {
		this.loginID = value;
	}

	public void setLoginStatus(int value) {
		this.loginStatus = value;
	}

	/** @deprecated */
	public void setName(PersonNameIfc value) {
		this.setPersonName(value);
	}

	public void setNumberFailedPasswords(int numberFailedPasswords) {
		this.numberFailedPasswords = numberFailedPasswords;
	}

	public void setPassword(String value) {
		try {
			this.password = value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException var3) {
			logger.error("Could not convert password from UTF-8 string", var3);
		}

		this.password = value.getBytes();
	}

	public void setPasswordBytes(byte[] value) {
		this.password = value;
	}

	public void setEmployeePasswordSalt(String pwdSalt) {
		this.employeePasswordSalt = pwdSalt;
	}

	public void setPasswordChangeRequired(boolean value) {
		this.passwordChangeRequired = value;
	}

	public void setPasswordCreationDate(Date createDate) {
		this.passwordCreationDate = createDate;
	}

	public void setPersonName(PersonNameIfc value) {
		this.name = value;
	}

	public void setPreferredLocale(Locale preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public void setRole(RoleIfc value) {
		this.role = value;
	}

	public void setStoreID(String value) {
		this.storeID = value;
	}

	public void setType(EmployeeTypeEnum type) {
		this.type = type;
	}

	public void setFingerprintBiometrics(List<byte[]> fingerprintBiometrics) {
		this.fingerprintBiometrics = fingerprintBiometrics;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String toJournalString(Locale locale) {
		StringBuilder buff = new StringBuilder(I18NHelper.getString("EJournal", "JournalEntry.employee.empId",
				new Object[] { this.employeeID }, locale));
		buff.append(Util.EOL);
		buff.append(I18NHelper.getString("EJournal", "JournalEntry.employee.alternateID",
				new Object[] { this.alternateID }, locale));
		buff.append(Util.EOL);
		buff.append(I18NHelper.getString("EJournal", "JournalEntry.employee.loginID", new Object[] { this.loginID },
				locale));
		buff.append(Util.EOL);
		buff.append(I18NHelper.getString("EJournal", "JournalEntry.employee.fullName", new Object[] { this.fullName },
				locale));
		buff.append(Util.EOL);
		buff.append(
				I18NHelper.getString("EJournal", "JournalEntry.employee.ssn", new Object[] { "***-**-****" }, locale));
		buff.append(Util.EOL);
		buff.append(I18NHelper.getString("EJournal", "JournalEntry.employee.loginStatus",
				new Object[] { this.loginStatusToString() }, locale));
		buff.append(Util.EOL);
		if (this.name == null) {
			buff.append(I18NHelper.getString("EJournal", "JournalEntry.employee.name", new Object[] { null }, locale));
			buff.append(Util.EOL);
		} else {
			buff.append(
					I18NHelper.getString("EJournal", "JournalEntry.employee.sub", new Object[] { this.name }, locale));
			buff.append(Util.EOL);
		}

		if (this.role == null) {
			buff.append(I18NHelper.getString("EJournal", "JournalEntry.employee.role", new Object[] { null }, locale));
			buff.append(Util.EOL);
		} else {
			buff.append(I18NHelper.getString("EJournal", "JournalEntry.employee.role",
					new Object[] { this.role.hashCode() }, locale));
			buff.append(Util.EOL);
		}

		return buff.toString();
	}

	public Object getContextValue() {
		StringBuilder builder = new StringBuilder("Employee[id=");
		builder.append(this.getLoginID());
		builder.append("]");
		return builder.toString();
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("name", this.name);
		return builder.toString();
	}

	
	public boolean getMposFlag() {
		return mposFlag;
	}

	
	public void setMposFlag(boolean mposFlag) {
		this.mposFlag = mposFlag;
	}

	
	public String getMposPassword() {
		return mposPassword;
	}

	
	public void setMposPassword(String mposPassowrd) {
		this.mposPassword = mposPassowrd;
	}

	
	public String getMPOSPasswordSalt() {
		return mposPassSalt;
	}

	
	public void setMPOSPasswordSalt(String mposPassSalt) {
		this.mposPassSalt = mposPassSalt;
	}

	
	public EmployeeIfc getEmployee() {
		return this.employeeIfc;
	}

	
	public void setEmployeeIfc(EmployeeIfc employee) {
		this.employeeIfc = employee;
	}
	//Added by Abhishek For Mpos On 09-10-2024
	
	public Employee getMaxEmployee() {
		if (maxEmployee != null)
			return maxEmployee;
		else
			return new Employee();
	}

	public void setEmployee(Employee maxEmployee) {
		Employee.maxEmployee = maxEmployee;
	}
	//Added by Abhishek For Mpos On 09-10-2024 End

	public String getAvailableAmount() {
		// TODO Auto-generated method stub
		return availableAmount;
	}

	public String getEligibleAmount() {
		// TODO Auto-generated method stub
		return eligibleAmount;
	}


	
	public String getCompanyName() {
		// TODO Auto-generated method stub
		return companyName;
	}

	public void setCompanyName(String companyName) {
		// TODO Auto-generated method stub
		this.companyName=companyName;
	}

	
	public String getSpecialEmployeeDiscountValue() {
		// TODO Auto-generated method stub
		return specialEmployeeDisount;
	}

	
	public String getStatusCode() {
		// TODO Auto-generated method stub
		return statusCode;
	}


	
	

}