/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/ParameterCargo.java /main/13 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  06/14/13 - cleanup of parameter groups
 *    acadar    07/16/10 - changes for parameters
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         3/3/2008 2:31:06 AM    Manas Sahu      For
 *         CR # 30277
 *    4    360Commerce 1.3         5/10/2006 9:47:13 PM   Brett J. Larsen CR
 *         17307 - remove inventory
 *    3    360Commerce 1.2         3/31/2005 4:29:18 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:58 PM  Robert Pearse
 *
 *   Revision 1.13  2004/08/26 16:23:01  mweis
 *   @scr 7012 Clump all "Inventory" related params into a hidden group.  Make necessary code changes to honor this new group.
 *
 *   Revision 1.12  2004/07/20 20:29:24  cdb
 *   @scr 6127 Updated behavior of StringLengthValidator.
 *
 *   Revision 1.11  2004/07/20 18:41:52  cdb
 *   @scr 6127 Updated to use validation in validator rather than aisles.
 *
 *   Revision 1.10  2004/07/01 21:27:13  dcobb
 *   @scr 3982 4690: register reboot resets parameter values
 *
 *   Revision 1.9  2004/06/18 12:10:57  tmorris
 *   @scr 5194 -Added Parameter Group Store Credit.
 *
 *   Revision 1.8  2004/06/03 17:52:09  jriggins
 *   @scr 4983 Modifications for "unhiding" the price adjustment parameters
 *
 *   Revision 1.7  2004/04/29 01:30:24  tfritz
 *   @scr 4454 - Added 'Parameter Group - Currency' access parameter
 *
 *   Revision 1.6  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/03/23 20:53:49  mweis
 *   @scr 0 JavaDoc cleanup
 *
 *   Revision 1.4  2004/03/19 21:02:56  mweis
 *   @scr 4113 Enable ISO_DATE datetype
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   Jan 30 2004 15:43:38   rrn
 * Modified convertParameterToBean( ) and convertParameterBeanToParameter to deal
 * with new parameter type -- VAL_COMPLEX_LIST.
 * Modified convertToRetailParameter( ) to deal with new validator type -- EnumeratedComplexListValidator.
 * Modified setParameterGroups( ) to recognize new group -- SECURITY_ACCESS_PM_GROUP.
 * Resolution for 3769: Parameterize Manager Override behavior
 *
 *    Rev 1.5   Jan 07 2004 12:40:40   lzhao
 * change InstantCredit to House Account
 * Resolution for 3617: Role/Security message for House Account indicates Instant Credit
 *
 *    Rev 1.4   Nov 19 2003 12:11:28   cdb
 * If there is no role function associated with this parameter group, then return -1 to invoke the override screen. The alternative is to have the application end unexpectedly. If there is no role function for the group, it needs to be added in any case, but this will make the application more robust.
 * Resolution for 3452: Crash- House Account Parameters
 *
 *    Rev 1.3   Oct 17 2003 16:47:52   rsachdeva
 * DEVICESTATUS_PM_GROUP
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 *
 *    Rev 1.2   Oct 01 2003 13:43:24   lzhao
 * They were sorted by parameter names defined in application.xml rather than the parameter name value in parameterText properties file.
 * Resolution for 3094: List of parameters not in alphabetical order in Tender parameter group
 *
 *    Rev 1.1   Sep 29 2003 13:39:50   bwf
 * Added new queue parameter group.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 *
 *    Rev 1.0   Aug 29 2003 15:52:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 23 2003 11:57:40   RSachdeva
 * TimeMaintenance Parameter Group
 * Resolution for Backoffice SCR-2055: Time Maintenance parameter group missing from POS Parameter Group List
 *
 *    Rev 1.2   07 Jul 2003 19:33:46   baa
 * remove hard code parameter check
 *
 *    Rev 1.1   30 Jun 2003 23:20:56   baa
 * check group access
 *
 *    Rev 1.0   Apr 29 2002 15:39:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:05:02   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:19:48   msg
 * Initial revision.
 *
 *    Rev 1.8   25 Feb 2002 10:35:06   KAC
 * Added oldValues and methods for retrieving old and new
 * values.
 * Resolution for POS SCR-1316: Changing parameter values does not journal
 *
 *    Rev 1.7   07 Feb 2002 10:25:46   KAC
 * Now makes sure that simple LISTs (not list-from-lists) get
 * converted into ReasonCodeBeanModels.
 * Resolution for POS SCR-1128: Deleting all values but 1 on Create List Parameter Editor screen only allows edit to that 1 value
 *
 *    Rev 1.6   31 Jan 2002 13:53:44   KAC
 * Modified for parameters with multi-lined string values
 * Resolution for POS SCR-672: Create List Parameter Editor
 *
 *    Rev 1.5   30 Jan 2002 10:23:38   KAC
 * Adjustments for "list from list"
 * Resolution for POS SCR-672: Create List Parameter Editor
 *
 *    Rev 1.4   24 Jan 2002 14:14:38   KAC
 * Eliminated resetting of modifiability in convertXXXToParameter
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 *
 *    Rev 1.3   21 Jan 2002 17:49:48   baa
 * converting to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.2   Jan 19 2002 10:28:02   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   10 Dec 2001 13:21:16   KAC
 * Revised to work at register level instead of corporate/store.
 * Remove locationLevel and store dependencies.
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 *
 *    Rev 1.0   Sep 21 2001 11:11:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterValidatorIfc;
import oracle.retail.stores.foundation.manager.parameter.EnumeratedComplexListValidator;
import oracle.retail.stores.foundation.manager.parameter.EnumeratedListValidator;
import oracle.retail.stores.foundation.manager.parameter.Parameter;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.parameter.ReasonCodeValue;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.foundation.tour.dtd.ParamSourceScriptIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CurrencyParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.DecimalParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.DiscreteParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.ISODateParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.ListFromListParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.MultilineStringParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.ParametersCommon;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.RetailParameter;
import oracle.retail.stores.pos.ui.beans.StringParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.WholeParameterBeanModel;
/*
 * @author baa
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
//------------------------------------------------------------------------------
/**
    This class maintains the information needed while editing parameters.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class ParameterCargo extends UserAccessCargo implements CargoIfc, TourCamIfc
{
    /** The logger to which log messages will be sent. */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.parametermanager.ParameterCargo.class);

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/13 $";

    // The following are a set of constants used for the category
    // name, a.k.a. location level.
    /** application */
    public static final String APPLICATION = "application";
    /** corporate */
    public static final String CORPORATE = ParametersCommon.CORPORATION;
    /** register */
    public static final String REGISTER = "register";
    /** store */
    public static final String STORE = ParametersCommon.STORE;
    /** userrole */
    public static final String USERROLE = "userrole";

    // The following set of constants are used for the parameter groups
    /** Base */
    public static final String BASE_PM_GROUP = "Base";
    /** Localization */
    public static final String LOCALIZATION_PM_GROUP = "Localization";
    /** Tender */
    public static final String TENDER_GROUP = "Tender";
    /** Item */
    public static final String ITEM_PM_GROUP = "Item";
    /** Discount */
    public static final String DISCOUNT_PM_GROUP = "Discount";
    /** Printing */
    public static final String PRINTING_PM_GROUP = "Printing";
    /** TenderAuthorization */
    public static final String TENDERATH_PM_GROUP = "TenderAuthorization";
    /** DailyOperations */
    public static final String DAILY_OPS_PM_GROUP = "DailyOperations";
    /** Reconciliation */
    public static final String RECONCILIATION_PM_GROUP = "Reconciliation";
    /** Tax */
    public static final String TAX_PM_GROUP = "Tax";
    /** Employee */
    public static final String EMPLOYEE_PM_GROUP = "Employee";
    /** Customer */
    public static final String CUSTOMER_PM_GROUP = "Customer";
    /** Return */
    public static final String RETURN_PM_GROUP = "Return";
    /** Order */
    public static final String ORDER_PM_GROUP = "Order";
    /** Email */
    public static final String EMAIL_PM_GROUP = "Email";
    /** Transaction */
    public static final String TRANSACTION_PM_GROUP = "Transaction";
    /** Layaway */
    public static final String LAYAWAY_PM_GROUP = "Layaway";
    /** Send */
    public static final String SEND_PM_GROUP = "Send";
    /** Queue */
    public static final String QUEUE_PM_GROUP = "Queue";
    /** DeviceStatus */
    public static final String DEVICESTATUS_PM_GROUP = "DeviceStatus";
    /** HouseAccount */
    public static final String HOUSEACCOUNT_PM_GROUP = "HouseAccount";
    /** SecurityAccess */
    public static final String SECURITY_ACCESS_PM_GROUP = "SecurityAccess";
    /** PriceAdjustment */
    public static final String PRICE_ADJUST_ACCESS_PM_GROUP = "PriceAdjustment";
    /** External Order */
    public static final String EXTERNAL_ORDER_PM_GROUP = "ExternalOrder";


    /** The alternative being edited for the category.  For example, for the
        "register" level, the alternative might be "workstation."
        Effectively, the alternative is the file name minus its extension.  **/
    protected String alternative = null;

    protected RegisterIfc register = null;

    /** The permissions of the user; that is, what he is allowed to edit.  **/
    protected String usersPermissions = CORPORATE;

    /** The name of the parameter group (e.g., tender) currently
        selected for parameter setting.  **/
    protected String parameterGroup = null;

    /** The collection of (Retail) parameters in the parameter group
        being edited.  **/
    protected Vector retailParameters = new Vector(0);

    /** The parameter chosen by the user to be edited.  **/
    protected RetailParameter parameter = null;

    /** The values of the parameter prior to editing.  These values
        are used solely for journaling purposes.  **/
    protected Serializable[] oldValues = null;

    /** The parameter chosen by the user to be edited.  **/
    protected ReasonCodeGroupBeanModel reasonCodeGroupBeanModel = null;

    /** This collection records the correspondence between a parameter
        name and the corresponding Parameter object.  **/
    protected Hashtable nameToParameters = new Hashtable();

    /** This collection records the correspondence between a parameter
        group and the corresponding security access function.  **/
    protected Hashtable groupToRoleFn = new Hashtable();
    /**
       Employee currently using this function
    **/
    protected EmployeeIfc accessEmployee = null;
    /**
        employee attempting Security override
    **/
    protected EmployeeIfc securityOverrideRequestEmployee = null;
    /**
        current backup process
    **/
    Process backupProcess = null;

    /**
     * Constructor for an empty parameter cargo.
     */
    public ParameterCargo()
    {
    }

     //--------------------------------------------------------------------------
    /**
       Returns the users permissions. <P>
       @return the users permissions
    **/
    //--------------------------------------------------------------------------
    public String getUsersPermissions()
    {
        return usersPermissions;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the users permissions. <P>
       @param value    the users permissions
    **/
    //--------------------------------------------------------------------------
    public void setUsersPermissions(String value)
    {
        usersPermissions = value;
    }

    //--------------------------------------------------------------------------
    /**
       Returns the current retail parameter group. <P>
       @return the parameter group
    **/
    //--------------------------------------------------------------------------
    public String getParameterGroup()
    {
        return parameterGroup;
    }

    //--------------------------------------------------------------------------
    /**
       Returns the current retail parameter. <P>
       @return the parameter
    **/
    //--------------------------------------------------------------------------
    public RetailParameter getParameter()
    {
        return parameter;
    }

    //--------------------------------------------------------------------------
    /**
       Returns the vector of retail parameters. <P>
       @return Vector
    **/
    //--------------------------------------------------------------------------
    public Vector getRetailParameters()
    {
        return retailParameters;
    }

    //----------------------------------------------------------------------
    /**
       Sets the Reason Code group model to work with.
       <P>
       @return ReasonCodeGroupBeanModel
    **/
    //----------------------------------------------------------------------
    public ReasonCodeGroupBeanModel getReasonCodeGroupBeanModel()
    {
        return reasonCodeGroupBeanModel;
    }

    //--------------------------------------------------------------------------
    /**
       Returns the hashtable of parameters keyed by the parameter name. <P>
       @return table of parameters
    **/
    //--------------------------------------------------------------------------
    public Hashtable getNameToParameters()
    {
        return nameToParameters;
    }

    //--------------------------------------------------------------------------
    /**
       Returns the parameter name. <P>
       @return the parameter name
    **/
    //--------------------------------------------------------------------------
    public String getParameterName()
    {
        String name = null;
        if (parameter != null)
        {
            name = parameter.getParameterName();
        }
        else
        {
            name = reasonCodeGroupBeanModel.getGroupName();
        }

        return name;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the parameter group. <P>
       @param value    the parameter group
    **/
    //--------------------------------------------------------------------------
    public void setParameterGroup(String value)
    {
        parameterGroup = value;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the current retail parameter. <P>
       @param value    the retail parameter
    **/
    //--------------------------------------------------------------------------
    public void setParameter(RetailParameter value)
    {
        parameter = value;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the Vector of retail parameters. <P>
       @param value    list of retail parameters
    **/
    //--------------------------------------------------------------------------
    public void setRetailParameters(Vector value)
    {
        retailParameters = value;
    }

    //----------------------------------------------------------------------
    /**
       Sets the Reason Code group model to work with.
       <P>
       @param value    the reason code group model
    **/
    //----------------------------------------------------------------------
    public void setReasonCodeGroupBeanModel(ReasonCodeGroupBeanModel value)
    {
        reasonCodeGroupBeanModel = value;
    }

    //----------------------------------------------------------------------
    /**
       Returns the employee requesting access to the current function.
       <P>
       @return an EmployeeIfc Object
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }

    //----------------------------------------------------------------------
    /**
       Sets the employee requesting access to the current  function.
       <P>
       @param value an EmployeeIfc Object
    **/
    //----------------------------------------------------------------------
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }


    /**
     * Returns the alternative being edited for the category.
     * @return The alternative being edited for the category.
     */
    public String getAlternative()
    {
        return alternative;
    }

    /**
     * Sets the alternative being edited for the category.
     * @param alt The new alternative.
     */
    public void setAlternative(String alt)
    {
        alternative = alt;
    }


    //----------------------------------------------------------------------
    /**
        Returns the securityOverrideRequestEmployee object. <P>
        @return The securityOverrideRequestEmployee object.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideRequestEmployee()
    {                                   // begin getSecurityOverrideRequestEmployee()
        return securityOverrideRequestEmployee;
    }                                   // end getSecurityOverrideRequestEmployee()

    //----------------------------------------------------------------------
    /**
        Sets the securityOverrideRequestEmployee object. <P>
        @param  value  securityOverrideRequestEmployee object.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideRequestEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideRequestEmployee()
        securityOverrideRequestEmployee = value;
    }

    //--------------------------------------------------------------------------
    /**
       Creates a SnapshotIfc which can subsequently be used to restore
       the cargo to its current state. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>The cargo is able to make a snapshot.
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>A snapshot is returned which contains enough data to restore the
       cargo to its current state.
       </UL>
       @return an object which stores the current state of the cargo.
       @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
    **/
    //--------------------------------------------------------------------------
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    //--------------------------------------------------------------------------
    /**
       Resets the cargo data using the snapshot passed in. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>The snapshot represents the state of the cargo, possibly relative
       to the existing state of the cargo.
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>The cargo state has been restored with the contents of the snapshot.
       </UL>
       @param snapshot is the SnapshotIfc which contains the desired state
       of the cargo.
       @exception ObjectRestoreException is thrown when the cargo cannot
       be restored with this snapshot
    **/
    //--------------------------------------------------------------------------
    public void restoreSnapshot(SnapshotIfc snapshot)
        throws ObjectRestoreException
    {
    }

    //--------------------------------------------------------------------------
    /**
       Converts a Parameter (as used by the business logic) into some kind of
       RetailParameter bean (as used by the UI).  <P>
       <B>Pre-Condition</B><P>
       <B>Post-Condition</B><P>
       <UL>
       <LI>The name of the Parameter is stored in the
       nameToParameters hashtable, so it can be looked up later.
       </UL>
       @param param the Parameter to convert into a bean
       @return a bean object
    **/
    //--------------------------------------------------------------------------
    public Object convertParameterToBean(Parameter param)
    {

        Object result = null;
        ParameterValidatorIfc validator = param.getValidator();

        // Convert a parameter that may have multiple values, where
        // those values are restricted to a set of allowable values
        if (ParamSourceScriptIfc.VAL_MULTILINE_STRING.equals(param.getType()))
        {
            result = convertToRetailParameter(param);
        }
        // Convert a parameter that may have multiple values, where
        // those values are restricted to a set of allowable values
        else if (ParamSourceScriptIfc.VAL_LIST.equals(param.getType())  ||
                 ParamSourceScriptIfc.VAL_COMPLEX_LIST.equals(param.getType()))
        {

            // The "list from list" editor uses a RetailParameterBeanModel
            if ((validator != null)
                && (validator instanceof EnumeratedListValidator ||
                    validator instanceof EnumeratedComplexListValidator))
            {
                result = convertToRetailParameter(param);
            }
            // For historical reasons, "simple" lists use the
            // ReasonCodeBeanModel
            else
            {
                result = convertToReasonCodeModel(param);
            }
        }       // VAL_LIST
        // NOTE: Having multiple values is no longer a sure indication of a
        // reason code.  ReasonCodeModels can be used for other
        // multi-valued parameters as well.  Some rewriting is needed.
        else if (param.getValues().length > 1)
        {
            result = convertToReasonCodeModel(param);
        }
        else
        {
            result = convertToRetailParameter(param);
        }
        return result;
    }

    //--------------------------------------------------------------------------
    /**
       Convert a Parameter (as used by the business logic) into some kind of
       RetailParameter bean (as used by the UI).  <P>
       <B>Pre-Condition</B><P>
       <B>Post-Condition</B><P>
       <UL>
       <LI>The name of the Parameter is stored in the
       nameToParameters hashtable, so it can be looked up later.
       </UL>
       @param param the Parameter to convert into a bean
       @return the RetailParameter bean
    **/
    //--------------------------------------------------------------------------
    public RetailParameter convertToRetailParameter(Parameter param)
    {

        RetailParameter retailParam = null;
        String paramType = param.getType();

        Serializable[] allValues = param.getValues();
        String firstValue = allValues[0].toString();
        ParameterValidatorIfc validator = param.getValidator();

        // Create the correct class of bean based on the parameters
        // type and set the bean specific fields.
        if (ParamSourceScriptIfc.VAL_PRIM_TYPE_BOOLEAN.equals(paramType))
        {
            retailParam = new DiscreteParameterBeanModel(param);
        }
        else if (ParamSourceScriptIfc.VAL_PRIM_TYPE_DOUBLE.equals(paramType)
                 || ParamSourceScriptIfc.VAL_PRIM_TYPE_FLOAT.equals(paramType))
        {
            retailParam = new DecimalParameterBeanModel(param);
        }
        else if (ParamSourceScriptIfc.VAL_PRIM_TYPE_CURRENCY.equals(paramType))
        {
            retailParam = new CurrencyParameterBeanModel(param);
        }
        else if (ParamSourceScriptIfc.VAL_PRIM_TYPE_INTEGER.equals(paramType)
                 || ParamSourceScriptIfc.VAL_PRIM_TYPE_LONG.equals(paramType)
                 || ParamSourceScriptIfc.VAL_PRIM_TYPE_SHORT.equals(paramType))
        {
            retailParam = new WholeParameterBeanModel(param);
        }
        // If the parameter type is a multiline string, use a
        // MultilineStringParameterBeanModel
        else if (ParamSourceScriptIfc.VAL_MULTILINE_STRING.equals(paramType))
        {
            retailParam = new MultilineStringParameterBeanModel(param);
        }
        // If the parameter type is an ISO Date (one that can be stored as
        // YYYY-MM-DD), use an ISODateParameterBeanModel
        else if (ParamSourceScriptIfc.VAL_ISO_DATE.equals(paramType))
        {
            retailParam = new ISODateParameterBeanModel();
        }
        // If the validator specifies a list of allowable values, and
        // the parameter type is a "list from list", use a
        // ListFromListParameterBeanModel
        else if ((validator != null)
                 && (validator instanceof EnumeratedListValidator)
                 && (ParamSourceScriptIfc.VAL_LIST.equals(paramType)))
        {

            retailParam = new ListFromListParameterBeanModel();
            EnumeratedListValidator listValidator =
                (EnumeratedListValidator)validator;

            Serializable[] valVals = listValidator.getAllowableValues();
            Vector values = new Vector();
            String displayValue = null;

            // Put all the values in the list
            for (int i = 0; i < valVals.length; i++)
            {
                displayValue = valVals[i].toString();
                values.addElement(displayValue);
            }

            ListFromListParameterBeanModel listModel =
                (ListFromListParameterBeanModel)retailParam;
            listModel.setNewValues(allValues);
            listModel.setPotentialValues(valVals);
        }
        else if( validator != null &&
                 validator instanceof EnumeratedComplexListValidator)
        {
            retailParam = new ListFromListParameterBeanModel();
            EnumeratedComplexListValidator eclValidator = (EnumeratedComplexListValidator)validator;

            // get values (don't need the databaseIDs)
            Serializable[] valVals = eclValidator.getAllowableValues();

            ListFromListParameterBeanModel listModel = (ListFromListParameterBeanModel)retailParam;

            listModel.setNewValues(allValues);
            listModel.setPotentialValues(valVals);
        }
        // If the validator specifies a list of allowable values, use
        // a DiscreteParameterBeanModel
        else if ((validator != null)
                 && (validator instanceof EnumeratedListValidator))
        {
            retailParam = new DiscreteParameterBeanModel();
            EnumeratedListValidator listValidator =
                (EnumeratedListValidator)validator;

            Serializable[] valVals = listValidator.getAllowableValues();
            Vector values = new Vector();
            String displayValue = null;

            // Put all the values in the list
            for (int i = 0; i < valVals.length; i++)
            {
                displayValue = valVals[i].toString();
                values.addElement(displayValue);
            }

            DiscreteParameterBeanModel discreteParam =
                (DiscreteParameterBeanModel)retailParam;
            discreteParam.setValueChoices(values);
            discreteParam.setNewValue(firstValue);
        }
        // By default, treat as a String value
        else
        {
            retailParam = new StringParameterBeanModel(param);
        }

        // Set the generic fields
        retailParam.setValue(firstValue);
        String paramName = param.getName();
        retailParam.setParameterName(paramName);
        retailParam.setModifiable(!param.isFinal());

        // The name content for sorting
        String nameContent = UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,paramName,paramName);
        retailParam.setParameterNameContent(nameContent);

        // This must be the last adjustment to the retail parameter.
        // (Otherwise, the "sets" would set the modified flag.)
        retailParam.setModified(param.getModified());

        // Arrange to locate the parameter later
        nameToParameters.put(paramName, param);

        return retailParam;
    }


    /**
     * Create a hashtable to contain the mappings between the parameter group values and the
     * corresponding role function security access id
     * @param groups
     */
    void setParameterGroups(Vector groups)
    {
        String groupName = null;
        if (groupToRoleFn.isEmpty())
        {
            for (int i = 0; i < groups.size(); i++)
            {
                groupName = (String)groups.elementAt(i);
                if (groupName.equals(BASE_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_BASE));
                }
                else if (groupName.equals(LOCALIZATION_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_STORE_CRED));
                }
                else if (groupName.equals(TENDER_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_TENDER));
                }
                else if (groupName.equals(ITEM_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_ITEM));
                }
                else if (groupName.equals(DISCOUNT_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_DISCOUNT));
                }
                else if (groupName.equals(SEND_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_SEND));
                }
                else if (groupName.equals(LAYAWAY_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_LAYAWAY));
                }
                else if (groupName.equals(TRANSACTION_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_TRANSACTION));
                }
                else if (groupName.equals(EMAIL_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_E_MAIL));
                }
                else if (groupName.equals(ORDER_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_ORDER));
                }
                else if (groupName.equals(RETURN_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_RETURN));
                }
                else if (groupName.equals(CUSTOMER_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_CUSTOMER));
                }
                else if (groupName.equals(EMPLOYEE_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_EMPLOYEE));
                }
                else if (groupName.equals(TAX_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_TAX));
                }
                else if (groupName.equals(RECONCILIATION_PM_GROUP))
                {
                    groupToRoleFn.put(groupName,new Integer(RoleFunctionIfc.PARAMETER_RECONCILIATION));
                }
                else if (groupName.equals(DAILY_OPS_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_DAILY_OPS));
                }
                else if (groupName.equals(TENDERATH_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_TENDER_AUTH));
                }
                else if (groupName.equals(PRINTING_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_PRINTING));
                }
                else if (groupName.equals(QUEUE_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_QUEUE));
                }
                else if (groupName.equals(DEVICESTATUS_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_DEVICESTATUS));
                }
                else if (groupName.equals(HOUSEACCOUNT_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_HOUSE_ACCOUNT));
                }
                else if (groupName.equals(SECURITY_ACCESS_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_SECURITY_ACCESS));
                }
                else if (groupName.equals(PRICE_ADJUST_ACCESS_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_PRICE_ADJUST));
                }
                else if (groupName.equals(EXTERNAL_ORDER_PM_GROUP))
                {
                    groupToRoleFn.put(groupName, new Integer(RoleFunctionIfc.PARAMETER_EXTERNAL_ORDER));
                }
            }
        }
    }

    /**
     * Returns the role function that corresponds to the specified parameter group
     * @return
     */
    int getRoleFunctionForGroup()
    {
        // If there is no role function associated with this parameter group,
        // then return -1 to invoke the override screen.
        // The alternative is to have the application end unexpectedly.
        // If there is no role function for the group, it needs to be added
        // in any case, but this will make the application more robust.
        int returnValue = -1;
        Integer returnInteger = (Integer)groupToRoleFn.get(parameterGroup);
        if (returnInteger != null)
        {
            returnValue = returnInteger.intValue();
        }
         return returnValue;
    }
    //--------------------------------------------------------------------------
    /**
       Convert a Parameter (as used by the business logic) into
       ReasonCode bean (as used by the UI).  <P>
       <B>Pre-Condition</B><P>
       <B>Post-Condition</B><P>
       <UL>
       <LI>The name of the Parameter is stored in the
       nameToParameters hashtable, so it can be looked up later.
       </UL>
       @param param the Parameter to convert into a bean
       @return the ReasonCode bean
    **/
    //--------------------------------------------------------------------------
    public ReasonCodeGroupBeanModel convertToReasonCodeModel(Parameter param)
    {

        ReasonCodeGroupBeanModel group = new ReasonCodeGroupBeanModel();
        group.setModifyingParameter(true);
        ReasonCode reasonCode = null;
        String paramName = param.getName();
        Serializable[] values = param.getValues();
        Vector reasonCodes = new Vector();

        // Set the generic fields
        group.setGroupName(paramName);
        group.setModifiable(!param.isFinal());

        // Create a reason code from each parameter value, and add it
        // to the reason code group
        for (int i = 0; i < values.length; i++)
        {
            reasonCode = new ReasonCode();
            reasonCode.setModifyingParameter(true);

            // In the optimal world, the value will be a ReasonCodeValue
            if (values[i] instanceof ReasonCodeValue)
            {
                ReasonCodeValue rcValue = (ReasonCodeValue)values[i];
                reasonCode.setReasonCodeName(rcValue.getValue());
            }
            // In a suboptimal world, the value could be anything, but
            // is probably a String.  The code below should work
            // regardless.  The database Id is unobtainable.
            else
            {
                reasonCode.setReasonCodeName(values[i].toString());
            }
            reasonCode.setReasonCodeGroup(paramName);
            //reasonCode.setReasonCodeLevel(locationLevel);
            reasonCodes.addElement(reasonCode);
        }
        group.setReasonCodes(reasonCodes);
        Serializable serValue = param.getDefaultValue();
        String defaultValue = Parameter.getReasonCodeValueValue(serValue);
        group.setDefaultReasonCode(defaultValue);

        // The name content for sorting
        String nameContent = UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,paramName,paramName);
        group.setParameterNameContent(nameContent);

        // Arrange to locate the parameter later
        nameToParameters.put(paramName, param);

        return group;
    }

    //--------------------------------------------------------------------------
    /**
     Convert a RetailParameter bean (as used by the UI) into a
     Parameter (as used by the business logic).  <P>
     <B>Pre-Condition</B><P>
     <UL>
     <LI>The name of the RetailParameter has been stored in the
     nameToParameters hashtable, so the corresponding Parameter
     can be found
     </UL>
     <B>Post-Condition</B><P>
     @param retailParam the RetailParameter to convert
     @return the Parameter corresponding to the RetailParameter
     **/
    //--------------------------------------------------------------------------
    public Parameter convertBeanToParameter(RetailParameter retailParam)
    throws ParameterException
    {
        String paramName = retailParam.getParameterName();
        Parameter param = (Parameter)nameToParameters.get(paramName);
        String paramType = param.getType();

        // Set the generic fields
        String firstValue = retailParam.getValue();

        // For Boolean valued parameters, the retrieved value
        // must be converted
        if (ParamSourceScriptIfc.VAL_PRIM_TYPE_BOOLEAN.equals(paramType))
        {
            if (ParametersCommon.USER_TRUE.equals(firstValue))
            {
                // Change Yes to Y
                firstValue = ParamSourceScriptIfc.VAL_YN_Y;
            }
            else if (ParamSourceScriptIfc.VAL_YN_Y.equals(firstValue))
            {
                // firstValue is already a Y
            }
            else
            {
                // Change No to N
                firstValue = ParamSourceScriptIfc.VAL_YN_N;
            }
        }

        // A multi-valued parameter
        if (ParamSourceScriptIfc.VAL_LIST.equals(paramType)  ||
                ParamSourceScriptIfc.VAL_COMPLEX_LIST.equals(paramType))
        {
            Serializable[] newValueArray =
                ((ListFromListParameterBeanModel)retailParam).getNewValues();
            Vector newValues = new Vector(newValueArray.length);

            for (int i = 0; i < newValueArray.length; i++)
            {
                newValues.addElement(newValueArray[i]);
            }
            param.setValues(newValues);
        }
        // A String-valued parameter spanning multiple lines
        else if (ParamSourceScriptIfc.VAL_MULTILINE_STRING.equals(paramType))
        {
            Serializable[] newValueArray =
                ((MultilineStringParameterBeanModel)retailParam).getAllLines();
            Vector allLines = new Vector(newValueArray.length);

            for (int i = 0; i < newValueArray.length; i++)
            {
                allLines.addElement(newValueArray[i]);
            }
            param.setValues(allLines);
        }
        // Single value
        else
        {
            param.setValue(firstValue, 0);
        }

        // This must be the last adjustment to the parameter.
        // (Otherwise, the "sets" would set the modified flag.)
        param.setModified(retailParam.getModified());

        return param;
    }

    //--------------------------------------------------------------------------
    /**
       Convert a RetailParameter bean (as used by the UI) into a
       Parameter (as used by the business logic).  <P>
       <B>Pre-Condition</B><P>
       <UL>
       <LI>The name of the RetailParameter has been stored in the
       nameToParameters hashtable, so the corresponding Parameter
       can be found
       </UL>
       <B>Post-Condition</B><P>
       @param retailParam the RetailParameter to convert
       @return the Parameter corresponding to the RetailParameter
       @deprecated As of release 7.0, replaced by {@link #convertBeanToParameter(RetailParameter)}
    **/
    //--------------------------------------------------------------------------
    public Parameter convertParameterBeanToParameter(RetailParameter retailParam)
    {
        String paramName = retailParam.getParameterName();
        Parameter param = (Parameter)nameToParameters.get(paramName);

        // Set the generic fields
        try
        {
            param = convertBeanToParameter(retailParam);
        }
        catch (ParameterException e)
        {
            logger.error( "" + e + "");
        }
        return param;
    }

    //--------------------------------------------------------------------------
    /**
       Convert a ReasonCodeGroupBeanModel bean (as used by the UI) into a
       Parameter (as used by the business logic).  <P>

       @param rcGroup the ReasonCodeGroupBeanModel to convert
       @return the Parameter corresponding to the ReasonCodeGroupBeanModel
    **/
    //--------------------------------------------------------------------------
    public Parameter convertReasonCodeGroupBeanToParameter(ReasonCodeGroupBeanModel rcGroup)
    {
        String paramName = rcGroup.getGroupName();
        Parameter param = (Parameter)nameToParameters.get(paramName);

        // Set the generic fields
        try
        {
            Vector beanValues = rcGroup.getReasonCodes();
            ReasonCode bean = null;
            Vector rcValues = new Vector();
            ReasonCodeValue rcValue = null;
            String rcName = null;

            // Convert the ReasonCode beans to ReasonCodeValues
            for (int i = 0; i < beanValues.size(); i++)
            {
                bean = (ReasonCode)beanValues.elementAt(i);
                rcValue = new ReasonCodeValue();
                rcName = bean.getReasonCodeName();
                rcValue.setValue(rcName);
                rcValue.setDatabaseId(-1);

                // Set the default reason code
                if ((rcName != null)
                    && rcName.equals(rcGroup.getDefaultReasonCode()))
                {
                    param.setDefaultIndex(i);
                }
                rcValues.addElement(rcValue);
            }
            param.setValues(rcValues);
        }
        catch (ParameterException e)
        {
            logger.error( "" + e + "");
        }
        return param;
    }

    //--------------------------------------------------------------------------
    /**
       This function determines whether the user is allowed to modify the
       chosen parameter.
       @return true when modifiable; false otherwise
    **/
    //--------------------------------------------------------------------------
    public boolean parameterModificationPermitted()
    {
        /*
          In a two level system (Corporation and Store), the
          corporation level personnel are always allowed to edit the
          parameter values and modifiability indicators; whereas the
          store personnel may only modify the parameter values when
          the modifiability value has been set to true by the
          corporate personnel.  */
        /*
        boolean permitted =
            (CORPORATE.equals(usersPermissions)
             || (STORE.equalsIgnoreCase(locationLevel)
                 && (parameter != null)
                 && parameter.getModifiable()));
        */
        return true;
    }

    //--------------------------------------------------------------------------
    /**
       This function determines whether the user is allowed to modify the
       chosen parameter.
       @return true when modifiable; false otherwise
    **/
    //--------------------------------------------------------------------------
    public boolean listModificationPermitted()
    {
        /*
          In a two level system (Corporation and Store), the
          corporation level personnel are always allowed to edit the
          parameter values and modifiability indicators; whereas the
          store personnel may only modify the parameter values when
          the modifiability value has been set to true by the
          corporate personnel.  */
        /*
        boolean permitted =
            (CORPORATE.equals(usersPermissions)
             || (STORE.equalsIgnoreCase(locationLevel)
                 && (reasonCodeGroupBeanModel != null)
                 && reasonCodeGroupBeanModel.getModifiable()));
        */
        return true;
    }

    //--------------------------------------------------------------------------
    /**
       Return all of the Parameters being edited in a Vector
       @return a Vector of Parameters
    **/
    //--------------------------------------------------------------------------
    public Vector getParameters()
    {
        Vector parameters = new Vector();
        Object paramBean = null;
        Parameter param = null;

        // Convert retailParameters (beans) into Parameters
        for (int i = 0; i < retailParameters.size(); i++)
        {
            paramBean = retailParameters.elementAt(i);

            if (paramBean instanceof RetailParameter)
            {
                // Ensure we have the original parameter in case the conversion fails.
                param = (Parameter)nameToParameters.get(((RetailParameter)paramBean).getParameterName());

                try
                {
                    param = convertBeanToParameter((RetailParameter)paramBean);
                }
                catch (ParameterException e)
                {
                    logger.error( "" + e + "");
                }
            }
            else
            {
                param = convertReasonCodeGroupBeanToParameter(
                                         (ReasonCodeGroupBeanModel)paramBean);
            }
            parameters.addElement(param);
        }

        return parameters;
    }


    //--------------------------------------------------------------------------
    /**
        Record the existing values for the indicated parameter
        @param  paramName the name of the parameter of interest.
    **/
    //--------------------------------------------------------------------------

    public void setOldValuesForParameter(String paramName)
    {
        if (paramName != null)
        {
            Parameter param = (Parameter)nameToParameters.get(paramName);

            if (param != null)
            {
                oldValues = param.getValues();

                // Make sure the oldValues array is distinct from the
                // current values
                if (oldValues != null)
                {
                    oldValues = (Serializable[])oldValues.clone();
                }
            }   // if (param != null)
        }       // if (paramName != null)
    }


    /**
     * Returns the array of old values.
     * @return The array of old values.
     */
    public Serializable[] getOldValues()
    {
        return oldValues;
    }


    //--------------------------------------------------------------------------
    /**
        Get the current values for the indicated parameter
        @param  paramName the name of the parameter of interest.
        @return the parameter values
    **/
    //--------------------------------------------------------------------------

    public Serializable[] getNewValuesForParameter(String paramName)
    {
        Serializable[] newValues = null;
        if (paramName != null)
        {
            Parameter param = (Parameter)nameToParameters.get(paramName);

            if (param != null)
            {
                newValues = param.getValues();
            }   // if (param != null)
        }       // if (paramName != null)
        return newValues;
    }


    //--------------------------------------------------------------------------
    /**
       Return all fields to their initial values. <P>
       <B>Pre-Condition</B><P>
       <B>Post-Condition</B><P>
       All fields are returned to their initial values (existing values
       are erased).
    **/
    //--------------------------------------------------------------------------

    public void reset()
    {
        if (logger.isInfoEnabled()) logger.info(
                    "ParameterCargo.reset");
        parameterGroup = null;
        parameter = null;
        oldValues = null;
        reasonCodeGroupBeanModel = null;
        accessEmployee = null;
        securityOverrideRequestEmployee = null;
        retailParameters = new Vector();
        nameToParameters = new Hashtable();
    }

    //--------------------------------------------------------------------------
    /**
        Sets the current backup process. <P>
        @param p  The currentbackup process
    **/
    //--------------------------------------------------------------------------
    public void setBackupProcess(Process p)
    {
        backupProcess = p;
    }

    //--------------------------------------------------------------------------
    /**
        Returns the current backup process. <P>
        @return Process  The currentbackup process
    **/
    //--------------------------------------------------------------------------
    public Process getBackupProcess()
    {
        return backupProcess;
    }

//  ----------------------------------------------------------------------
    /**
        Returns the register at which operations are being performed.
        <P>
        @return RegisterIfc object
    **/
    //----------------------------------------------------------------------
    public RegisterIfc getRegister()
    {
        return(register);
    }

    //----------------------------------------------------------------------
    /**
        Sets the register at which operations are to be performed.
        <P>
        @param  register The register where operations are being performed.
    **/
    //----------------------------------------------------------------------
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    //----------------------------------------------------------------------

}
