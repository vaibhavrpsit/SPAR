/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/CheckForCaptureContractSignatureSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    07/14/10 - do not prompt for sig cap when refund; include
 *                         restocking fee in the item price; configuration for
 *                         refundPayments
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    acadar    06/03/10 - changes for signature capture and refactoring
 *    acadar    06/03/10 - signature capture for external order contract
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;


import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;



/**
 *
 * If transaction has an external order and the parameter for
 * capturing signature for contract is set to true, this site calls the Signature
 * Capture flow.
 * @author acadar
 *
 */
public class CheckForCaptureContractSignatureSite extends PosSiteActionAdapter
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 6163515664730748990L;

    /**
     * Checks if transaction has an external order and if contract
     * signature for external order is set to true
     */
    public void arrive(BusIfc bus)
    {

        LetterIfc letter = new Letter(CommonLetterIfc.NEXT);
        SaleCargoIfc saleCargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction  = saleCargo.getTransaction();
        if(transaction.hasExternalOrder() && isCaptureContractSignatureRequired(bus) && !transaction.isReturn())
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }

        bus.mail(letter, BusIfc.CURRENT);

    }

    protected boolean isCaptureContractSignatureRequired(BusIfc bus)
    {
        boolean required = false;
        try
        {
           ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
           String value  = pm.getStringValue("ExternalOrderSigCapRequired");
           if (value.equalsIgnoreCase("Y"))
           {
             required = true;
           }
        }
        catch (ParameterException e)
       {
         logger.error("ExternalOrderSigCapRequired parameter could not be read. Using default value of false", e);
       }

       return required;
   }



}
