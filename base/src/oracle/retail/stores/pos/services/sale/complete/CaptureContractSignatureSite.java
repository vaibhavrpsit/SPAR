/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/CaptureContractSignatureSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/08/10 - cleanup
 *    acadar    06/08/10 - intial version
 *    acadar    06/02/10 - refactoring
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - renamed from _externalorder to externalorder
 *    acadar    05/21/10 - additional changes for process order flow
 *    acadar    05/17/10 - temporarily rename the package
 *    acadar    05/17/10 - pluged in the ExternalOrderManager
 *    acadar    05/17/10 - additional logic added for processing orders
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import java.util.List;

import oracle.retail.stores.domain.externalorder.LegalDocumentIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;




/**
 * This site calls the signature station for each legal document
 *
 * @author acadar
 *
 */
public class CaptureContractSignatureSite extends PosSiteActionAdapter
{


    /**
     *  Serial Version UID
     */
    private static final long serialVersionUID = -4798456117344818482L;

    /**
     * For each order item marked for sale calls the sell order item station
     * @param BusIfc
     */
    public void arrive(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.NEXT);


        //iterate over the line items
        if(cargo.getTransaction() instanceof SaleReturnTransactionIfc)
        {
            List<LegalDocumentIfc> documents = cargo.getTransaction().getLegalDocuments();

            if(cargo.isBeginIterationOverLegalDocuments())
            {
                int currentRecord = 0;
                try
                {
                    cargo.setLegalDocument(documents.get(currentRecord));
                    cargo.setNextLegalDocumentRecord(currentRecord + 1);
                    cargo.setBeginIterationOverLegalDocuments(false);
                }
                catch (IndexOutOfBoundsException ie)
                {
//                  we finished processing all the items, move on
                    letter = new Letter(CommonLetterIfc.CONTINUE);
                    //we may need to reset the data in the cargo
                    resetCargoData(cargo);
                }

            }
            else
            {
                int nextRecord = cargo.getNextLegalDocumentRecord();
                try
                {

                    cargo.setLegalDocument(documents.get(nextRecord));
                    nextRecord = nextRecord + 1;
                    cargo.setNextLegalDocumentRecord(nextRecord);

                }
                catch (IndexOutOfBoundsException ie)
                {
                    //we finished processing all the items, move one to do additional validation
                    letter = new Letter(CommonLetterIfc.CONTINUE);
                    resetCargoData(cargo);
                }

            }

         }

        // go to item validation station
        bus.mail(letter, BusIfc.CURRENT);

    }

    /**
     * Resets the data in the cargo
     */
    private void resetCargoData(SaleCargoIfc cargo)
    {
        cargo.setLegalDocument(null);
        cargo.setNextLegalDocumentRecord(0);
        cargo.setBeginIterationOverLegalDocuments(true);
    }





}
