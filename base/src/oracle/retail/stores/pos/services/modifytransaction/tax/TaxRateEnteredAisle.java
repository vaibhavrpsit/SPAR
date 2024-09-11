/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/TaxRateEnteredAisle.java /main/12 2011/02/16 09:13:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 11/03/08 - localization of transaction tax reason codes
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         4/12/2008 5:44:57 PM   Christian Greene
           Upgrade StringBuffer to StringBuilder
      4    360Commerce 1.3         1/22/2006 11:45:14 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:48 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:43 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:51:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:37  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:03:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:15:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:42:56   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:31:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:58   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.tax;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import java.math.BigDecimal;

/**
 * This aisle is executed when a tax rate is entered at the UI and the Next
 * button is pressed. This aisle will set the tax rate number in the cargo.
 * 
 * @version $Revision: /main/12 $
 */
public class TaxRateEnteredAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 3349053392556552794L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Sets the tax rate number in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get cargo handle
        ModifyTransactionTaxCargo cargo =
            (ModifyTransactionTaxCargo) bus.getCargo();
        // get the POS UI manager
        POSUIManagerIfc ui =
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // get bean model
        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel)
            ui.getModel(POSUIManagerIfc.TRANSACTION_TAX_OVERRIDE_RATE);


        // retrieve amount, reason
        BigDecimal rate = beanModel.getValue();
        
        //UI allow up to 4 after decimal point. With division of 100, round it up to 6 decimal places.
        rate = rate.setScale(6, BigDecimal.ROUND_HALF_UP); 
        String reason  = beanModel.getSelectedReasonKey();
        // log results
        String message =
            new String("***** TaxRateEnteredAisle received input [" +
                       rate +
                       "] reason [" +
                       reason +
                       "].");
        if (logger.isInfoEnabled()) logger.info( "" + message + "");

        String maxRate = new String("1.0000");

        CodeEntryIfc reasonEntry = null;

        // retrieve reason code list
        CodeListIfc rcl = cargo.getLocalizedOverrideRateReasonCodes();
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        if (rcl != null)
        {
            reasonEntry = rcl.findListEntryByCode (reason);
            localizedCode.setCode(reason);
            localizedCode.setText(reasonEntry.getLocalizedText());
        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }

        if (rate.abs().compareTo(new BigDecimal(maxRate).abs()) == 1)
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidTaxOverride");
            dialogModel.setType(DialogScreensIfc.ERROR);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            // set values in transaction tax object
            TransactionTaxIfc tax = DomainGateway.getFactory().getTransactionTaxInstance();
            tax.setOverrideRate(rate.doubleValue());
            tax.setReason(localizedCode);
            tax.setTaxMode(TaxIfc.TAX_MODE_OVERRIDE_RATE);
            cargo.setTransactionTax(tax);
            cargo.setDirtyFlag(true);
            cargo.setNextFlag(true);
            // mail a next letter
            bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
        }
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
