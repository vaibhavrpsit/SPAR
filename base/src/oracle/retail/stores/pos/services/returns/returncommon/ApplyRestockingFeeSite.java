/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ApplyRestockingFeeSite.java /rgbustores_13.4x_generic_branch/1 2011/04/18 16:32:45 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   04/18/11 - fix for wrong calculation of restocking fee
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:15 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    4    360Commerce 1.3         1/22/2006 11:45:17 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/06/04 22:35:56  mweis
 *   @scr 4250 Return's restocking fee incorrectly calculated
 *
 *   Revision 1.7  2004/05/13 19:38:41  jdeleau
 *   @scr 4862 Support timeout for all screens in the return item flow.
 *
 *   Revision 1.6  2004/03/10 00:13:04  aarvesen
 *   @scr 3561 use the extended restocking fee
 *
 *   Revision 1.5  2004/03/04 20:50:28  baa
 *   @scr 3561 returns add support for units sold
 *
 *   Revision 1.4  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.3  2004/02/12 16:51:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 12 2002 16:14:10   jriggins
 * Changed CurrencyIfc.toString() call to CurrencyIfc.toFormattedString().
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:06:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 05 2002 16:43:12   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.0   Sep 21 2001 11:24:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

// java imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import java.math.BigDecimal;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;

import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

//--------------------------------------------------------------------------
/**
    
    This site displays the dialog screen for applying or declining the 
    restocking fee.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ApplyRestockingFeeSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        This site displays the dialog screen for applying or declining the 
        restocking fee.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
                // get the cargo
        ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
        Letter letter = null;

        // get the return item from the cargo
        PLUItemIfc pluItem = cargo.getPLUItem(); 
        ItemClassificationIfc itemClassification = pluItem.getItemClassification(); 

        if (itemClassification != null)
        {
            if (itemClassification.getRestockingFeeFlag())
            {
                // get the return item from the cargo
                ReturnItemIfc returnItem = cargo.getReturnItem(); 

                // Attempt to get the restocking fee percentage parameter 
                ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
                BigDecimal restockingFeePercentage = null;

                try
                {
                    double dbl  =  pm.getDoubleValue(ParameterConstantsIfc.RETRUN_RestockingFee).doubleValue();
                    dbl = dbl/100;
                    restockingFeePercentage = new BigDecimal(dbl);
                }
                catch (ParameterException pe)
                {
                    logger.error("Could not determine restocking fee.", pe);
                }

                if (restockingFeePercentage == null)
                {
                    letter = new Letter(CommonLetterIfc.SUCCESS);
                }
                else
                {
                	SaleReturnLineItemIfc[] saleItem = cargo.getReturnSaleLineItems();
                    CurrencyIfc itemPrice = returnItem.getPrice();
                    for(int i=0;i<saleItem.length;i++)
                    {
                        if(saleItem[i].getItemID()!=null && saleItem[i].getItemID().equals(pluItem.getItemID()))
                        {
                            itemPrice=saleItem[i].getExtendedDiscountedSellingPrice().divide(saleItem[i].getItemQuantityDecimal());
                        }
                  	}
                    
                    // calculate the restocking fee then multiply by quantity
                    CurrencyIfc restockingFee = itemPrice.multiply(restockingFeePercentage);
                    BigDecimal itemQuantity = cargo.getItemQuantity();
                    CurrencyIfc extendedRestockingFee = restockingFee.multiply(itemQuantity);

                    returnItem.setRestockingFee(restockingFee);  // SCR 4250: Use the restocking fee per item.
                    
                    //Set security check point
                    cargo.setAccessFunctionID(RoleFunctionIfc.OVERRIDE_RESTOCKING_FEE);
                    cargo.setResourceID("RestockingFeeSecurityError");
                        // Get the ui manager
                    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
    
                    // Use the "generic dialog bean".  
                    DialogBeanModel model = new DialogBeanModel();
        
                    // set the model
                    model.setResourceID("RestockingFeeApplicable");
                    model.setType(DialogScreensIfc.CONFIRMATION);
                    String args[] = new String[1];
                    args[0] = extendedRestockingFee.toFormattedString();
                    model.setArgs(args);
                    model.setTimerModel(new DefaultTimerModel(bus, true));
                      
                    // show the screen
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                }
            }
            else
            {
                letter = new Letter(CommonLetterIfc.SUCCESS);
            }
        }
        else
        {
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
