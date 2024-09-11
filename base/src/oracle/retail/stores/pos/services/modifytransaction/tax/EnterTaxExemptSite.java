/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/EnterTaxExemptSite.java /main/14 2012/03/29 15:26:12 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/02/11 - update deprecated tax methods
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/29/2007 6:43:03 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         4    .v8x      1.2.1.0     3/12/2007 1:12:45 PM   Maisa De Camargo
 *         Fixed
 *         Reason Code Defaults.
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 30 2003 08:58:08   baa
 * display tax cert # if available
 * 
 *    Rev 1.2   Feb 14 2003 14:00:24   HDyer
 * Changed code to use nondeprecated method to set the reason code string in the bean model.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Aug 12 2002 11:35:26   jriggins
 * Changed call to Integer.toString() to using LocaleUtilities.formatNumber().
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:14:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

import java.util.Locale;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.StringWithReasonBeanModel;

/**
 * @version $Revision: /main/14 $
 */
@SuppressWarnings("serial")
public class EnterTaxExemptSite extends PosSiteActionAdapter
{

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * This method shows the UI so the user can enter tax exempt data.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {

        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        // get the cargo
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo)bus.getCargo();
        cargo.setNextFlag(false);
        // set up the ui model
        StringWithReasonBeanModel beanModel = new StringWithReasonBeanModel();
        // get reason codes and set list in bean model
        // set up reason code list
        CodeListIfc reasons = utility.getReasonCodes(cargo.getOperator().getStoreID(),
                CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
        cargo.setLocalizedTaxExemptReasonCodes(reasons);

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String selectedCode = null;

        // get certificate ID, only if tax exemption is place
        TransactionTaxIfc tax = cargo.getTransactionTax();
        if (tax.getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
        {
            selectedCode = tax.getReason().getCode();
            beanModel.setValue(tax.getTaxExemptCertificateID());
        }
        else if (cargo.getCustomer() != null)
        {
            CustomerIfc customer = cargo.getCustomer();
            if (customer.getTaxExemptionReason() != null)
            {
                selectedCode = customer.getTaxExemptionReason().getCode();
            }
            byte[] taxCertificate = new byte[0];
            try{
                taxCertificate = customer.getEncipheredTaxCertificate().getDecryptedNumber();
                if (taxCertificate.length != 0)
                {
                    beanModel.setValue(new String(taxCertificate));
                }
            }
            finally
            {
                Util.flushByteArray(taxCertificate);
            }
        }
        beanModel.inject(reasons, selectedCode, locale);

        // Show the input screen
        uiManager.showScreen(POSUIManagerIfc.TRANSACTION_TAX_EXEMPT, beanModel);
    }
}
