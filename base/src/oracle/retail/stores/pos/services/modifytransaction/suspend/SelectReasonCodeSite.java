/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/SelectReasonCodeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
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
 *    mdecama   11/04/08 - I18N - Fixed the way the locale was being retrieved
 *    mdecama   10/27/08 - It is not necessary to call BestMatch when injecting
 *                         the codeList into the model
 *    mdecama   10/24/08 - I18N updates for Suspend Transaction Reason Codes.
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         3/29/2007 6:41:34 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           4    .v8x      1.2.1.0     3/10/2007 4:59:21 PM   Maisa De Camargo
           Fixed
           Reason Code Default Settings.
      3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:51:16  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:47  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:02:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 21 2003 12:29:46   HDyer
 * Remove use of SelectReasonBeanModel to standardize on use of ReasonBeanModel.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Apr 29 2002 15:15:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:54   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.suspend;
// java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonBeanModel;

//------------------------------------------------------------------------------
/**
    If required, display a list of reasons for suspending the transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SelectReasonCodeSite extends PosSiteActionAdapter
{                                                                               // begin class SelectReasonCodeSite

    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -3193075118154952704L;
    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       site name constant
    **/
    public static final String SITENAME = "SelectReasonCodeSite";
    /**
       suspend-reason-required parameter name constant
    **/
    public static final String SUSPEND_REASON_REQUIRED = "SuspendReasonRequired";
    /**
       suspend-reason-codes parameter name constant
    **/
    public static final String SUSPEND_REASON_CODES = "TransactionSuspendReasonCodes";

    //--------------------------------------------------------------------------
    /**
       If necessary, display a list of reasons for suspending the transaction.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                                                   // begin arrive()

        // letter to be mailed
        String letterName = null;
        // get parameter to determine if necessary to display reasons
        boolean required = false;
        // get the cargo
        ModifyTransactionSuspendCargo cargo =
            (ModifyTransactionSuspendCargo) bus.getCargo();

        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        try
        {                                                               // begin retrieve reason-required parameter try block
            // determine if reason codes required
            String value  = pm.getStringValue(SUSPEND_REASON_REQUIRED);
            if (logger.isInfoEnabled()) logger.info(
                        "" + "Suspend reason parameter: [" + "" + value + "]");
            if (value.equalsIgnoreCase("Y"))
            {
                required = true;
            }
        }                                                               // end retrieve reason-required parameter try block
        catch (ParameterException e)
        {
            // if parameter not found, default value is yes
            required = true;
        }

        // if reason codes are required, display screen
        if (required)
        {
            // display screen with reason list
            ReasonBeanModel model = new ReasonBeanModel();
            UtilityManagerIfc utilityManager = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            CodeListIfc reasonCodesList = utilityManager.getReasonCodes(cargo.getOperator().getStoreID(),
                    CodeConstantsIfc.CODE_LIST_TRANSACTION_SUSPEND_REASON_CODES);
            Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            model.inject(reasonCodesList, "", lcl);

            cargo.setReasonCodes(reasonCodesList);

            POSUIManagerIfc ui =
                (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.SELECT_SUSPEND_REASON_CODE, model);
        }
        // if not required, send letter to skip ui
        else
        {
            // issue suspend letter to bypass reason-code entry
            letterName = "Suspend";
        }

        // mail letter, if necessary
        if (letterName != null)
        {
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }

    }                                                                   // end arrive()

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object. <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append("SelectReasonCodeSite (Revision ")
            .append(getRevisionNumber())
            .append(") @").append(hashCode());
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}                                                                               // end class class SelectReasonCodeSite
