/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcertificate/EnterDiscountedAmountUISite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       01/13/09 - set amount in tender attributes map to be non locale
 *                         sensitive decimal string
 *    sgu       01/08/09 - convert tab to space
 *    sgu       01/08/09 - change tab to space
 *    sgu       01/08/09 - use currency service to parse string to decimal
 *                         based on locale.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:45:01 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *   Revision 1.9  2004/07/15 21:00:09  crain
 *   @scr 6289 Redeem- System crashed after pressing enter at Discounted Amt screen for gift cert redeem
 *
 *   Revision 1.8  2004/05/24 21:45:39  crain
 *   @scr 5105 Tender Redeem_Gift Cert Redeem w/ Disc. Receipt Incorrect
 *
 *   Revision 1.7  2004/05/19 20:34:41  crain
 *   @scr 5080 Tender Redeem_Disc. Applied Alt Flow not Called from Foreign Gift Cert Alt Flow
 *
 *   Revision 1.6  2004/05/07 22:01:14  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.5  2004/05/04 03:35:44  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.4  2004/05/02 01:54:05  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.3  2004/04/30 21:04:56  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.2  2004/04/29 23:48:50  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.1  2004/04/29 15:07:19  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem.giftcertificate;

import java.math.BigDecimal;
import java.util.HashMap;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.redeem.RedeemCargo;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

/**
  * This site displays the discounted amount screen
  * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EnterDiscountedAmountUISite extends PosSiteActionAdapter
{
/**
     *
     */
    private static final long serialVersionUID = 4900470988800307403L;
    //--------------------------------------------------------------------------
    /**
     Arrive method
     @param bus the bus arriving at this site
     **/
//--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        DataInputBeanModel dModel = new DataInputBeanModel();
        String amount = (String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
        dModel.setValue("PrintedAmountLabel", amount);

        ui.showScreen(POSUIManagerIfc.DISCOUNTED_AMOUNT, dModel);
    }

    /**
     * Collect data from the UI upon depart.
     * @param bus BusIfc
     *
     */
    public void depart(BusIfc bus)
    {
        if (bus.getCurrentLetter().getName().equals("Next"))
        {
            // Get information from UI
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            DataInputBeanModel dModel = (DataInputBeanModel) ui.getModel();
            String amount = LocaleUtilities.parseCurrency(dModel.getValueAsString("discountedAmountField").trim(), LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
            if(!"".equals(amount))
            {
                RedeemCargo cargo = (RedeemCargo)bus.getCargo();
                HashMap tenderAttributes = cargo.getTenderAttributes();

                // the initial amount is actually the face value amount; the amount is going to hold the discounted amount entered
                String faceValue = (String)tenderAttributes.get(TenderConstants.AMOUNT);
                tenderAttributes.put(TenderConstants.FACE_VALUE_AMOUNT, faceValue);
                tenderAttributes.put(TenderConstants.AMOUNT, amount);
                //calculate the discount as String
                String discount = subtractStringAmounts(faceValue,amount);
                tenderAttributes.put(TenderConstants.DISCOUNT_AMOUNT, discount);

            }
        }
    }
    //----------------------------------------------------------------------
    /**
      * Subtracts s1-s2 and returns the result as String
      *  @param s1 String the first number
      *  @param s2 String the second number
      *  @return String the result
    **/
    //----------------------------------------------------------------------
    protected String subtractStringAmounts(String s1, String s2)
    {
        BigDecimal n1 = new BigDecimal(s1);
        BigDecimal n2 = new BigDecimal(s2);

        n1 = n1.subtract(n2);

        return n1.toString();
    }
}
