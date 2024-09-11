/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/initialize/OperatorIDLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:18  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/28 22:53:23  epd
 *   @scr 6593 fixed training mode issue
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:24:50   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:03:42   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.initialize;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This shuttle carries the required contents in order to login the
    cashier in the Operator ID service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OperatorIDLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8202386715476089869L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.initialize.OperatorIDLaunchShuttle.class);

    /**
       revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "OperatorIDLaunchShuttle";
    /**
       Number of tries constant
    **/
    public static final int NUMBER_OF_TRIES = 1;
    /**
       Prompt to use tag
    **/
    public static final String PROMPT_TAG = "CashierIdPrompt";
    /**
       Prompt to use default text
    **/
    public static final String PROMPT_TEXT = "Enter cashier ID.";

    /**
       The list of employees logged on to all the tills.
    **/
    EmployeeIfc employees[] = null;

    /**
       Indicates whether the operator password is required.
    **/
    boolean passwordRequired = false;
    
    protected RegisterIfc register;

    //--------------------------------------------------------------------------
    /**
       Copies information from the cargo used in the calling service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        employees = cargo.getRegister().getCashiers();
        passwordRequired = cargo.isPasswordRequired();
        register = cargo.getRegister();

    }

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo used in the calling service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // set defaults
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        cargo.setMaximumAttempts(NUMBER_OF_TRIES);
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String promptText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                                 BundleConstantsIfc.POS_BUNDLE_NAME,
                                                 PROMPT_TAG,
                                                 PROMPT_TEXT,
                                                 LocaleConstantsIfc.USER_INTERFACE);
        cargo.setOperatorIdPromptText(promptText);
        cargo.setEmployees(employees);
        cargo.setHandleError(true);
        cargo.setPasswordRequiredWithList(passwordRequired);
        cargo.setRegister(register);
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
        String strResult = new String("Class:  " + SHUTTLENAME + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Returns the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
