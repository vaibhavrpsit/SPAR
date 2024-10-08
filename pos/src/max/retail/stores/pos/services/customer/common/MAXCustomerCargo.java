/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerCargo.java /main/21 2014/02/03 19:30:29 rabhawsa Exp $
 * ===========================================================================
 * Rev 1.0	Aug 24,2016		Ashish Yadav	changes done for cod merging
 * header update
 * ===========================================================================
 */
package max.retail.stores.pos.services.customer.common;

import java.util.List;

import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//--------------------------------------------------------------------------
/**
    Contains common Customer cargo items.
    $Revision: /main/21 $
**/
//--------------------------------------------------------------------------
public class MAXCustomerCargo extends CustomerCargo
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2357677159380402499L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/21 $";

    /**
        constant string for both Link and Done button enabling
    **/
    public static final int LINKANDDONE = 0;

    /**
        constant string for Link
    **/
    public static final int LINK = 1;

    /**
        constant string for Done
    **/
    public static final int DONE = 2;
    /**
        constant string for Add button
    **/
    public static final String ADD = "Add";
    /**
        constant string for Add Business button
    **/
    public static final String ADDBUS = "AddBusiness";
    /**
        constant string for Delete button
    **/

    /**
        constant string for Done button
    **/
    public static final String DONE_BTN = "Done";


    /**
        constant string for Delete button
    **/
    public static final String DELETE = "Delete";
    /**
        constant string for History button
    **/
    public static final String HISTORY = "History";
    /**
        constant string for Employee ID Search button
    **/
    public static final String EMPID = "EmpID";
    /**
        constant string for Customer Information Search button
    **/
    public static final String CUSTINFO = "CustInfo";
    /**
        constant string for Business Information Search button
    **/
    public static final String BUSINFO = "BusInfo";

    /**
     * Array of pricing group object
     */
    protected PricingGroupIfc[] pricingGroups = null;

    /**
     * Array of pricing group name
     */
    protected String[] pricingGroupNames = null;

    /**
     * Tax ID
     */
    protected String taxID=null;

    /**
     * Boolean variable for customer search flow
     */
    protected boolean customerSearchSpec = false;

    /**
         Possible offlineIndicator values.
     **/
    public static final int OFFLINE_UNKNOWN = 0;
    public static final int OFFLINE_ADD     = 1;
    public static final int OFFLINE_LINK    = 2;
    public static final int OFFLINE_EXIT    = 3;
    public static final int OFFLINE_DELETE  = 4;
    public static final int OFFLINE_LAYAWAY = 5;


    /**
     *  Indicates which offline path to take.
     */
    protected int offlineIndicator = OFFLINE_UNKNOWN;

    /**
     * Indicate if enabling only customer find
     */
    protected boolean findOnly = false;
    /**
        Holds the customer id
    **/
    protected String customerID = null;

    /**
        exit service if database is offline.
    **/
    protected boolean offlineExit = false;

     /**
       indicates if add customer  button should be enable.
    **/
    protected boolean enableAddCust = true;

    /**
       indicates if add business button should be enable.
    **/
    protected boolean enableAddBus = true;

    /**
       indicates if delete button should be enable.
    **/
    protected boolean enableDelete = true;

    /**
       indicates if history button should be enable.
    **/
    protected boolean enableHistory = true;

    /**
       indicates if adding new customer.
    **/
    protected boolean newCustomer = false;
    /**
        Customer object
    **/
    protected CustomerIfc customer=null;

    /**
        list of matching Customers returned from database lookup
    **/
    protected List<CustomerIfc> customerList = null;

    /**
        Register object
    **/
    protected RegisterIfc register = null;
    /**
        Flag to show if this customer should be linked to the transaction
    **/
    protected boolean link = false;

    /**
        Flag to show if this customer has been linked
    **/
    protected boolean linkCustomer = false;

    /**
        3 way switch allowing the calling service to determine what is allowed for the return
    **/
    protected int linkDoneSwitch = LINKANDDONE;   // set to constants LINKANDDONE, LINK, DONE

    /**
        Unique transaction number
    **/
    protected String transactionID = null;
    
    /**
        The retail transaction to link to
    **/
    protected RetailTransactionIfc transaction = null;

    /**
        Employee currently using this function
     **/
    protected EmployeeIfc employee = null;
     /**
        operator  currently using this function
    **/
    protected EmployeeIfc operador = null;
    /**
        The result of the an interaction with the data manager
    **/
    protected int dataExceptionErrorCode = DataException.UNKNOWN;

   


    /**
        available customer group
    **/
    protected CustomerGroupIfc[] customerGroups = null;

    /**
        number of customer groups in array
    **/
    protected int numberCustomerGroups = 0;

    /**
       selected customer group index
    **/
    protected int selectedCustomerGroup = 0;

    /**
        default no-customer group
    **/
    protected CustomerGroupIfc[] noCustomerGroup = null;

    /**
        associated employee ID.  This is needed when we read a customer
        from the database, because we only get the employee ID, not the
        employee object.
    **/
    protected String employeeID = null;

    /**
        Clone of original, unedited Customer object
    **/
    protected CustomerIfc originalCustomer = null;
    /**
        previously linked customer
    **/
    protected CustomerIfc previousCustomer = null;

    /*
    *  Screen id
    */
    protected String screen = null;

    /*
    *  dialogName for Too many matches error
    */
    public static String TOO_MANY_MATCHES = "TooManyMatches";
    public static String TOO_MANY_CUSTOMERS = "TooManyCustomers";

    protected String dialogName = TOO_MANY_MATCHES;

    /**
        employee attempting Security override
         @deprecated as of release 5.0.0
    **/
    protected EmployeeIfc securityOverrideRequestEmployee;

    /**
     false if no override is requested, true is override is needed
      @deprecated as of release 5.0.0
    **/
    protected boolean securityOverrideFlag = false;

    /**
        employee Granting Security override
         @deprecated as of release 5.0.0
    **/
    protected EmployeeIfc securityOverrideEmployee;

    /**
        Security override Return Letter
         @deprecated as of release 5.0.0
    **/

    protected String securityOverrideReturnLetter;
    /**
        none label  tag
    **/
    public static final String NONE_LABEL_TAG = "NoneLabel";
    /**
        none label default text
    **/
    public static final String NONE_LABEL_TEXT = "(none)";

    /**
     * Localized Tax Exempt reason codes
     */
    protected CodeListIfc localizedTaxExemptReasonCodes = null;
    /**
     * order type
     */
    protected int orderType;

    /**
     * Flag to see action is pickup delivey
     */
    protected boolean isActionPickupDelivery;

    /**
    manager override is permitted or not
    true if its permitted
	**/
	protected boolean override = false;

	/**
	    Boolean indicating the change of discount type for a customer.
	    false, if discount type is not changed
	**/
	protected boolean discountTypeChanged = false;

	/**
		customer add or find operation; false for add
	**/
	protected boolean addFind = false;

	/**
	 *  if true, save the customer even if customer match found.
	 *  Used for mpos only.
	 */
	protected boolean forceSaveCustomer;

    
	//  changes starts for rev 1.0
		
	protected PhoneIfc ticCustomerPhoneNo;
	
	protected boolean ticCustomerPhoneNoFlag;

	private MAXTICCustomerIfc ticCustomer;
	//Added by Vaibhav for CRM customer serach withou SBI and wallet
	protected boolean CustomerCRMsearch = false;
	
	public boolean isCustomerCRMsearch() {
		return CustomerCRMsearch;
	}

	public void setCustomerCRMsearch(boolean customerCRMsearch) {
		CustomerCRMsearch = customerCRMsearch;
	}//end

	public MAXTICCustomerIfc getTicCustomer() {
		return ticCustomer;
	}

	public void setTicCustomer(MAXTICCustomerIfc ticCustomer) {
		this.ticCustomer = ticCustomer;
	}
	
// changes ends for rev 1.0
	/**
     * Constructs CustomerCargo object. <P>
    */
    public MAXCustomerCargo()
    {
        UtilityManagerIfc utility =
          (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);

        customer = DomainGateway.getFactory().getCustomerInstance();
        noCustomerGroup = new CustomerGroupIfc[1];
        noCustomerGroup[0] = DomainGateway.getFactory().getCustomerGroupInstance();
        noCustomerGroup[0].setName(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),
                                    utility.retrieveText("Common",
                                                        BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                                                        NONE_LABEL_TAG,
                                                        NONE_LABEL_TEXT));
    }

    //---------------------------------------------------------------------
    /**
        Resets some of the CustomerCargo object. This prevents the
        creation of a new transaction when user cancels early. <P>
    **/
    //---------------------------------------------------------------------
    public void resetCargo()
    {
        // reset members
        customerID = null;
        customer = null;
        originalCustomer = null;
        customerList = null;
        link = false;
        customerGroups = null;
        dataExceptionErrorCode = DataException.UNKNOWN;
    }

    //----------------------------------------------------------------------
    /**
        Sets the Register.
        @param  the register
    **/
    //----------------------------------------------------------------------
    public void setRegister(RegisterIfc value)
    {
        register = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the register.
        @return the RegisterIfc value
    **/
    //----------------------------------------------------------------------
    public RegisterIfc getRegister()
    {
        return register;
    }

    //----------------------------------------------------------------------
    /**
        Returns the offlineIndicator.
        <P>
        @return the integer value
    **/
    //----------------------------------------------------------------------
    public int getOfflineIndicator()
    {
        return offlineIndicator;
    }


    //----------------------------------------------------------------------
    /**
        Sets the offlineIndicator.
        <P>
        @param  the integer value
    **/
    //----------------------------------------------------------------------
    public void setOfflineIndicator(int value)
    {
        offlineIndicator = value;
    }
    //----------------------------------------------------------------------
    /**
        Returns boolean for findOnlyMode
        <P>
        @return the boolean value
    **/
    //----------------------------------------------------------------------
    public boolean isFindOnlyMode()
    {
        return findOnly;
    }

    //----------------------------------------------------------------------
    /**
        Sets Find only mode
        <P>
        @Param value the mode
    **/
    //----------------------------------------------------------------------
    public void setFindOnlyMode(boolean value)
    {
        findOnly= value;
    }
    //----------------------------------------------------------------------
    /**
        Returns boolean for AddOperation
        <P>
        @return the boolean value
    **/
    //----------------------------------------------------------------------
    public String getDialogName()
    {
        return dialogName;
    }

    //----------------------------------------------------------------------
    /**
        Sets AddOperation
        <P>
        @Param value the mode
    **/
    //----------------------------------------------------------------------
    public void setDialogName(String value)
    {
        dialogName = value;
    }

//  ----------------------------------------------------------------------
    /**
        Returns boolean for Add or Find Operation for a customer
        <P>
        @return the boolean value
    **/
    //----------------------------------------------------------------------
    public boolean getAddFind() {
		return addFind;
	}

    //----------------------------------------------------------------------
    /**
        Sets boolean for Add or Find Operation for a customer
        <P>
        @param  the boolean value
    **/
    //----------------------------------------------------------------------
	public void setAddFind(boolean addFind) {
		this.addFind = addFind;
	}

	//----------------------------------------------------------------------
    /**
        Returns boolean for manager override
        <P>
        @return the boolean value
    **/
    //----------------------------------------------------------------------
	public boolean getOverride() {
		return override;
	}

	//----------------------------------------------------------------------
    /**
        Sets boolean for manager override
        <P>
        @param  the boolean value
    **/
    //----------------------------------------------------------------------
	public void setOverride(boolean override) {
		this.override = override;
	}

	//----------------------------------------------------------------------
    /**
        Returns boolean indicating the change of discount
        type for a customer
        <P>
        @return the boolean value
    **/
    //----------------------------------------------------------------------
	public boolean getDiscountTypeChanged() {
		return discountTypeChanged;
	}

	//----------------------------------------------------------------------
    /**
        Sets boolean if the discount type is changed for a customer
        <P>
        @param  the boolean value
    **/
    //----------------------------------------------------------------------
	public void setDiscountTypeChanged(boolean discountTypeChanged) {
		this.discountTypeChanged = discountTypeChanged;
	}


   //----------------------------------------------------------------------
    /**
        Returns boolean for newCustomer
        @return the boolean value
    **/
    //----------------------------------------------------------------------
    public boolean isNewCustomer()
    {
        return newCustomer;
    }

    //----------------------------------------------------------------------
    /**
        Sets new customer
        @Param value the mode
    **/
    //----------------------------------------------------------------------
    public void setNewCustomer(boolean value)
    {
        newCustomer= value;
    }

    //----------------------------------------------------------------------
    /**
        Returns boolean for add customer button mode
        <P>
        @return the boolean value - true for button ON and false for OFF
    **/
    //----------------------------------------------------------------------
    public boolean isAddCustomerEnabled()
    {
        return enableAddCust;
    }

    //----------------------------------------------------------------------
    /**
        Sets add customer button mode
        <P>
        @Param the add customer mode as boolean - true for button ON and false for OFF
    **/
    //----------------------------------------------------------------------
    public void setAddCustomerMode(boolean value)
    {
        enableAddCust = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns boolean for add business button mode
        <P>
        @return the boolean value - true for button ON and false for OFF
    **/
    //----------------------------------------------------------------------
    public boolean isAddBusinessEnabled()
    {
        return enableAddBus;
    }

    //----------------------------------------------------------------------
    /**
        Sets add business button mode
        <P>
        @Param the add business mode as boolean - true for button ON and false for OFF
    **/
    //----------------------------------------------------------------------
    public void setAddBusinessMode(boolean value)
    {
        enableAddBus = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns boolean for delete button mode
        <P>
        @return the boolean value - true for button ON and false for OFF
    **/
    //----------------------------------------------------------------------
    public boolean isDeleteEnabled()
    {
        return enableDelete;
    }

    //----------------------------------------------------------------------
    /**
        Sets delete button mode
        <P>
        @Param the delete button mode as boolean - true for button ON and false for OFF
    **/
    //----------------------------------------------------------------------
    public void setDeleteMode(boolean value)
    {
        enableDelete = value;
    }


    //----------------------------------------------------------------------
    /**
        Returns boolean for history mode
        @return the integer value
    **/
    //----------------------------------------------------------------------
    public boolean isHistoryModeEnabled()
    {
        return enableHistory;
    }

    //----------------------------------------------------------------------
    /**
        Sets history mode
        @Param the history mode as boolean
    **/
    //----------------------------------------------------------------------
    public void setHistoryMode(boolean value)
    {
        enableHistory= value;
    }
    //---------------------------------------------------------------------
    /**
        Returns customer identifier.
        @return customer identifier
    **/
    //---------------------------------------------------------------------
    public String getCustomerID()
    {
        return(customerID);
    }

    //---------------------------------------------------------------------
    /**
        Sets customer identifier.
        @param value customer identifier
    **/
    //---------------------------------------------------------------------
    public void setCustomerID(String value)
    {
        customerID = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns customer.
        @return customer
    **/
    //---------------------------------------------------------------------
    public CustomerIfc getCustomer()
    {
        return(customer);
    }

    //---------------------------------------------------------------------
    /**
        Sets customer. <P>
        @param value    the customer
    **/
    //---------------------------------------------------------------------
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the Customer list in the cargo. <P>
        @return list of matching customers returned from lookup
    **/
    //---------------------------------------------------------------------
    public List<CustomerIfc> getCustomerList()
    {
        return customerList;
    }

    

    //---------------------------------------------------------------------
    /**
        Sets the Customer list in the cargo. <P>
        @param customerList  Customer list being set in cargo
    **/
    //---------------------------------------------------------------------
    public void setCustomerList(List<CustomerIfc> customerList)
    {
        this.customerList = customerList;
    }

    //---------------------------------------------------------------------
    /**
        returns whether the customer should be linked to the transaction.
        <P>
        @return true if the customer should be linked to the transaction.
    **/
    //---------------------------------------------------------------------
    public boolean isLink()
    {
        return link;
    }

    //---------------------------------------------------------------------
    /**
        Sets whether the customer should be linked to the transaction. <P>
        @param link  true if linking customer to the current transaction
    **/
    //---------------------------------------------------------------------
    public void setLink(boolean value)
    {
        link = value;
    }

    //---------------------------------------------------------------------
    /**
        returns whether the customer is linked to the transaction.
        <P>
        @return true if the customer should be linked to the transaction.
    **/
    //---------------------------------------------------------------------
    public boolean isCustomerLink()
    {
        return linkCustomer;
    }

    //---------------------------------------------------------------------
    /**
        Sets whether the customer is linked to the transaction. <P>
        @param link  true if customer is linked to the current transaction
    **/
    //---------------------------------------------------------------------
    public void setCustomerLink(boolean value)
    {
        linkCustomer = value;
    }
    //---------------------------------------------------------------------
    /**
        Returns whether the service should exit if the database is offline.
        <P>
        @return true if the service should exit when the database is offline.
    **/
    //---------------------------------------------------------------------
    public boolean getOfflineExit()
    {
        return(offlineExit);
    }

    //---------------------------------------------------------------------
    /**
        Sets whether the service should exit if the database is offline.
        <p>
        @param value    offline option
    **/
    //---------------------------------------------------------------------
    public void setOfflineExit(boolean value)
    {
        offlineExit = value;
    }


    //---------------------------------------------------------------------
    /**
        Returns transaction identifier. <P>
        @return transaction identifier
    **/
    //---------------------------------------------------------------------
    public String getTransactionID()
    {
        return(transactionID);
    }

    //---------------------------------------------------------------------
    /**
        Sets transaction ID. <P>
        @param value transaction identifier
    **/
    //---------------------------------------------------------------------
    public void setTransactionID(String value)
    {
        transactionID = value;
    }
    
    //---------------------------------------------------------------------
    /**
        Returns transaction. <P>
        @return transaction 
    **/
    //---------------------------------------------------------------------
    public RetailTransactionIfc getTransaction() 
    {
        return transaction;
    }

    //---------------------------------------------------------------------
    /**
        Sets transaction. <P>
        @param value transaction
    **/
    //---------------------------------------------------------------------
    public void setTransaction(RetailTransactionIfc transaction) 
    {
        this.transaction = transaction;
    }

    //---------------------------------------------------------------------
    /**
        Returns link and or done switch. <P>
        @return linkDoneSwitch
    **/
    //---------------------------------------------------------------------
    public int getLinkDoneSwitch()
    {
        return(linkDoneSwitch);
    }

    //---------------------------------------------------------------------
    /**
        Sets link and or done switch. <P>
        @param  value   linkDoneSwitch
    **/
    //---------------------------------------------------------------------
    public void setLinkDoneSwitch(int value)
    {
        linkDoneSwitch = value;
    }


    //----------------------------------------------------------------------
    /**
        Returns the error code returned with a DataException.
        <P>
        @return the integer value
    **/
    //----------------------------------------------------------------------
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    //----------------------------------------------------------------------
    /**
        Sets the error code returned with a DataException.
        <P>
        @param  the integer value
    **/
    //----------------------------------------------------------------------
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    //----------------------------------------------------------------------
    /**
        Sets the salesAssociate object.
        <P>
        @param EmployeeIfc the salesAssociate object
    **/
    //----------------------------------------------------------------------
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
    }

  
    //----------------------------------------------------------------------
    /**
        Gets the employee object.
        <P>
        @return EmployeeIfc object
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSalesAssociate()
    {
        return(salesAssociate);
    }


    //----------------------------------------------------------------------
    /**
       Displays customer name on status bar.
       @Param  BusIfc the current bus
       @Param  CustomerIfc the current customer
    **/
    //----------------------------------------------------------------------
    public void displayCustomer(BusIfc bus)
    {
         // Display linked customer if there is one
        String customerName = new String ("");

        if (linkCustomer)
        {
            UtilityManagerIfc utility =
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String[] vars = {previousCustomer.getFirstName(), previousCustomer.getLastName()};
            String pattern = utility.retrieveText("CustomerAddressSpec",
                                                  BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                                  TagConstantsIfc.CUSTOMER_NAME_TAG,
                                                  TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
            customerName=LocaleUtilities.formatComplexMessage(pattern,vars);
        }
        displayCustomerName(bus, customerName);
     }

    public static void displayCustomerName(BusIfc bus, String name)
    {
         // set the customer's name in the status area
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        StatusBeanModel statusModel = new StatusBeanModel();

        statusModel.setCustomerName(name);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
    }

    //---------------------------------------------------------------------
    /**
        Sets customer groups. <P>
        @param value customer groups array
    **/
    //---------------------------------------------------------------------
    public void setCustomerGroups(CustomerGroupIfc[] value)
    {
        customerGroups = value;
        if (customerGroups == null)
        {
            setNumberCustomerGroups(0);
        }
        else
        {
            setNumberCustomerGroups(customerGroups.length);
        }
    }

    //---------------------------------------------------------------------
    /**
        Returns the customer groups. <P>
        @return customer groups array
    **/
    //---------------------------------------------------------------------
    public CustomerGroupIfc[] getCustomerGroups()
    {
        return(customerGroups);
    }

    //---------------------------------------------------------------------
    /**
        Sets number of rules in array (including "(none)").<P>
        @param value number of rules in array (including "(none)")
    **/
    //---------------------------------------------------------------------
    protected void setNumberCustomerGroups(int value)
    {
        numberCustomerGroups = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the number of rules in array (including "(none)").<P>
        @return number of rules in array (including "(none)")
    **/
    //---------------------------------------------------------------------
    public int getNumberCustomerGroups()
    {
        return(numberCustomerGroups);
    }

   //---------------------------------------------------------------------
    /**
        Sets index of selected customer group
        @param value index
    **/
    //---------------------------------------------------------------------
    public void setSelectedCustomerGroup(int value)
    {
        selectedCustomerGroup = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the index of the selected customer group
        @return number the index of the selected customer group
    **/
    //---------------------------------------------------------------------
    public int getSelectedCustomerGroup()
    {
        return(selectedCustomerGroup);
    }
    //---------------------------------------------------------------------
    /**
        Returns the default no-customer group. <P>
        @return default no-customer group array
    **/
    //---------------------------------------------------------------------
    public CustomerGroupIfc[] getNoCustomerGroup()
    {
        return(noCustomerGroup);
    }

    //---------------------------------------------------------------------
    /**
        Sets the employee ID. <P>
        @param value    the employee ID
    **/
    //---------------------------------------------------------------------
    public void setEmployeeID(String value)
    {
        employeeID = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the employee ID. <P>
        @return the employee ID
    **/
    //---------------------------------------------------------------------
    public String getEmployeeID()
    {
        return(employeeID);
    }

    //---------------------------------------------------------------------
    /**
        Sets the employee. <P>
        @param value    the employee

    **/
    //---------------------------------------------------------------------
    public void setEmployee(EmployeeIfc value)
    {
        employee = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the employee. <P>
        @return the employee

    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getEmployee()
    {
        return(employee);
    }

   

   

   //---------------------------------------------------------------------
    /**
        Sets the operator. <P>
        @param value    the employee
    **/
    //---------------------------------------------------------------------
    public void setOperator(EmployeeIfc value)
    {
        operador = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the operator. <P>
        @return the employee
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getOperator()
    {
        return(operador);
    }
    //---------------------------------------------------------------------
    /**
        Returns the original Customer object in the cargo. <P>
        @return     the original customer
    **/
    //---------------------------------------------------------------------
    public CustomerIfc getOriginalCustomer()
    {
        return originalCustomer;
    }

    //---------------------------------------------------------------------
    /**
        Sets the original Customer. <P>
        @param originalCustomer     the original customer
    **/
    //---------------------------------------------------------------------
    public void setOriginalCustomer(CustomerIfc value)
    {
        originalCustomer = (CustomerIfc)value.clone();
    }
 //---------------------------------------------------------------------
    /**
        Returns the prevoulsy link Customer object in the cargo. <P>
        @return     the original customer
    **/
    //---------------------------------------------------------------------
    public CustomerIfc getPreviousCustomer()
    {
        return previousCustomer;
    }

    //---------------------------------------------------------------------
    /**
        Sets the previous Customer. <P>
        @param previousCustomer     the previously link customer
    **/
    //---------------------------------------------------------------------
    public void setPreviousCustomer(CustomerIfc value)
    {
        previousCustomer = (CustomerIfc)value.clone();
    }

    //---------------------------------------------------------------------
    /**
        Returns the screen used to display a list of matching
        Customer records. <P>
        @return     screen ID to display
    **/
    //---------------------------------------------------------------------
    public String getScreen()
    {
        return screen;
    }

    //---------------------------------------------------------------------
    /**
        Sets the screen used to display a list of matching
        Customer records. <P>
        @param screen  ID of screen to display
    **/
    //---------------------------------------------------------------------
    public void setScreen(String screen)
    {
        this.screen = screen;
    }

// Tourcam record is set to OFF. Do not need the following two methods
    //---------------------------------------------------------------------
    /**
        Takes a snapshot of the current state of the cargo. <P>
        @return SnapshotIfc  Snapshot object containing relevent data from the cargo
    **/
    //---------------------------------------------------------------------

    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    //---------------------------------------------------------------------
    /**
        Restores cargo to original state. <P>
        @param snapshot  object that contains cargo state information
        @exception ObjectRestoreException   Bedrock is unable to restore cargo to original state
    **/
    //---------------------------------------------------------------------

    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
        CustomerCargo savedCargo = (CustomerCargo)snapshot.restoreObject();

        // restore each attribute of the cargo to its previous value
        customerID = savedCargo.getCustomerID();
        customer = savedCargo.getCustomer();
        customerList = savedCargo.getCustomerList();
        link = savedCargo.isLink();
        transactionID = savedCargo.getTransactionID();
        salesAssociate = savedCargo.getSalesAssociate();
        offlineIndicator = savedCargo.getOfflineIndicator();

    }


    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  " + getClass().getName()
                                      + "(Revision " + getRevisionNumber()
                                      + ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
                                     
   

   

    // ----------------------------------------------------------------------
    /**
     * Set array of Pricing Group
     * @param Array of PricingGroupIfc
     */
    // ----------------------------------------------------------------------
    public void setPricingGroup(PricingGroupIfc[] value)
    {
        this.pricingGroups = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Get array of pricing group
     * @return Array of PricingGroupIfc
     */
    // ----------------------------------------------------------------------
    public PricingGroupIfc[] getPricingGroup()
    {
        return this.pricingGroups;
    }

    // ----------------------------------------------------------------------
    /**
     * Set array of Pricing Group names
      * @param Array of String
     */
    // ----------------------------------------------------------------------
    public void setPricingGroupNames(String[] value)
    {
        this.pricingGroupNames = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Get array of Pricing Group Names
     * @return Array of String
     */
    // ----------------------------------------------------------------------
    public String[] getPricingGroupNames()
    {
        return this.pricingGroupNames;
    }

    // ----------------------------------------------------------------------
    /**
     * Set Tax ID
     * @param String
     */
    // ----------------------------------------------------------------------
    public void setTaxID(String taxID)
    {
        this.taxID = taxID;
    }

    // ----------------------------------------------------------------------
    /**
     * Get Tax ID
     * @return String
     */
    // ----------------------------------------------------------------------
    public String getTaxID()
    {
        return this.taxID;

    }

    // ----------------------------------------------------------------------
    /**
     * Set Boolean for customer search flow
     * @param boolean value
     */
    // ----------------------------------------------------------------------
    public void setCustomerSearchSpec(boolean value)
    {
        customerSearchSpec = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Get boolean if customer search flow
     * @return boolean value
     */
    // ----------------------------------------------------------------------
    public boolean isCustomerSearchSpec()
    {
        return customerSearchSpec;
    }
    /**
     * Returns exempt reason-code list.
     * <P>
     *
     * @return exempt reason-code list
     */
    public CodeListIfc getLocalizedExemptReasonCodes()
    {
    	return localizedTaxExemptReasonCodes;
    }

    /**
     * Method sets the localizedTaxExemptReasonCodes
     * @param localizedTaxExemptReasonCodes the localizedTaxExemptReasonCodes to set
     */
    public void setLocalizedTaxExemptReasonCodes(CodeListIfc localizedTaxExemptReasonCodes)
    {
        this.localizedTaxExemptReasonCodes = localizedTaxExemptReasonCodes;
    }
    // --------------------------------------------------------------------------
    /**
     * set an order type to the cargo.
     *
     * @param newOrder to set
     */
    // --------------------------------------------------------------------------
    public void setOrderType(int newOrder)
    {
        orderType = newOrder;
    }

    // --------------------------------------------------------------------------
    /**
     * Returns the Order type in cargo.
     *
     * @return orderType
     */
    // --------------------------------------------------------------------------
    public int getOrderType()
    {
        return orderType;
    }
    // --------------------------------------------------------------------------
    /**
     * Sets isPickupDelivery flag true for Pickup or Delivery items
     *
     * @param isActionPickupDelivery
     */
    // --------------------------------------------------------------------------
    public void setActionPickupDelivery(boolean isActionPickupDelivery)
    {
        this.isActionPickupDelivery = isActionPickupDelivery;
    }
    // --------------------------------------------------------------------------
    /**
     * Returns the flag for pickup or delivery items.
     *
     * @return isActionPickupDelivery
     */
    // --------------------------------------------------------------------------
    public boolean getActionPickupDelivery()
    {
        return isActionPickupDelivery;
    }

    
    /**
     * @return value of <code>forceSaveCustomer</code>
     */
    public boolean isForceSaveCustomer()
    {
        return forceSaveCustomer;
    }

    /**
     * <code>forceSaveCustomer</code>
     * <p>
     * if true: save the customer even if possible matches found.
     * <p>
     * if false : look for possible matches before saving the customer.
     * 
     * @return forceSaveCustomer
     */
    public void setForceSaveCustomer(boolean forceSaveCustomer)
    {
        this.forceSaveCustomer = forceSaveCustomer;
    }
	
	//  changes starts for rev 1.0
    
	public PhoneIfc getTicCustomerPhoneNo() {
		return ticCustomerPhoneNo;
	}

	public void setTicCustomerPhoneNo(PhoneIfc ticCustomerPhoneNo) {
		this.ticCustomerPhoneNo = ticCustomerPhoneNo;
	}

	public boolean isTicCustomerPhoneNoFlag() {
		return ticCustomerPhoneNoFlag;
	}

	public void setTicCustomerPhoneNoFlag(boolean ticCustomerPhoneNoFlag) {
		this.ticCustomerPhoneNoFlag = ticCustomerPhoneNoFlag;
	}
    
	
    
    // changes ends for rev 1.0
	protected boolean isCustLinkedThroughDB = false;
	public boolean isCustLinkedThroughDB() {
		return isCustLinkedThroughDB;
	}
	public void setCustLinkedThroughDB(boolean isCustLinkedThroughDB) {
		this.isCustLinkedThroughDB = isCustLinkedThroughDB;
	}
    
	public boolean enrollEdge = false;
	/**
	 * @return the enrollEdge
	 */
	public boolean isEnrollEdge() {
		return enrollEdge;
	}
	/**
	 * @param enrollEdge the enrollEdge to set
	 */
	public void setEnrollEdge(boolean enrollEdge) {
		this.enrollEdge = enrollEdge;
	}
	
}
