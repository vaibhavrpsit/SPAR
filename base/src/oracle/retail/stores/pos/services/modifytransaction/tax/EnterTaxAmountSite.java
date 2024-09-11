/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/EnterTaxAmountSite.java /main/14 2011/12/05 12:16:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    acadar 11/03/08 - transaction tax reason codes updates
 *    acadar 11/03/08 - localization of transaction tax reason codes
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         3/29/2007 6:42:35 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           5    .v8x      1.3.1.0     3/12/2007 12:05:51 PM  Brett J. Larsen
           CR 4530
           - adding support for case where default code list value is not
           designated - in this case an empty string/blank should be used (or
           no value should be pre-selected)
      4    360Commerce 1.3         1/22/2006 11:45:14 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:28 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:51:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:37  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:02:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 05 2003 14:42:20   HDyer
 * Fix deprecation warnings.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Aug 12 2002 13:14:48   jriggins
 * Changed call to Integer.toString() to using LocaleUtilities.formatNumber().
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:14:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:42:54   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:31:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:06   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.tax;

//java imports
import java.util.Locale;

import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import java.math.BigDecimal;

//--------------------------------------------------------------------------
/**
    ##COMMENT##
    <p>
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class EnterTaxAmountSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    //----------------------------------------------------------------------
    /**
       Shows the UI so the user can enter an override tax rate. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // get the cargo
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo)bus.getCargo();
        cargo.setNextFlag(false);
        // set up the ui model
        DecimalWithReasonBeanModel beanModel = new DecimalWithReasonBeanModel();
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        //set up reason code list
        CodeListIfc reasons = utility.getReasonCodes(cargo.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_TRANSACTION_TAX_AMOUNT_OVERRIDE_REASON_CODES);
        cargo.setLocalizedOverrideAmountReasonCodes(reasons);

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String selectedCode = null;
        // get default amount, only if transaction tax by amount override is in place
        TransactionTaxIfc tax = cargo.getTransactionTax();
        if (tax != null && tax.getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT)
        {
            selectedCode = tax.getReason().getCode();
            beanModel.setValue(new BigDecimal(tax.getOverrideAmount().getStringValue()));

        }
        beanModel.inject(reasons, selectedCode, locale);

        uiManager.showScreen(POSUIManagerIfc.TRANSACTION_TAX_OVERRIDE_AMOUNT,beanModel);

    }
}
