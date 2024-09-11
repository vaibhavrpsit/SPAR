/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/ejournal/EvaluateSearchAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:56 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/07/30 23:25:11  kmcbride
 *   @scr 6639: Fixing up EJ to use only the sequence number portion of the transaction id when comparing transaction ids within the same store, reg. and biz date.
 *
 *   Revision 1.6  2004/07/15 15:46:53  kll
 *   @scr 5824: check length against TransactionID class
 *
 *   Revision 1.5  2004/07/15 15:14:32  kll
 *   @scr 5824: check length against TransactionID class
 *
 *   Revision 1.4  2004/02/18 21:12:16  tfritz
 *   @scr 3632 - EJournal search gives an error if only a start date or an end date is entered
 *   @scr 3635
 *
 *   Revision 1.3  2004/02/12 16:48:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:07  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Mar 07 2003 17:11:06   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Mar 03 2003 09:50:30   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 02 2002 10:16:00   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:40:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.ejournal;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.TransactionLookupBeanModel;
//------------------------------------------------------------------------------
/**
    This aisle is used to validate some of the user input data from the Find
    Transaction screen when in the ejournal service.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class EvaluateSearchAisle extends PosLaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "EvaluateSearchAisle";
    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
      invalid search criteria transaction id tag
    **/
    public static final String INVALID_SEARCH_CRITERIA_TRANSACTIONID_TAG = 
      "InvalidSearchCriteria.transactionID";
    /**
      invalid search criteria date tag
    **/
    public static final String INVALID_SEARCH_CRITERIA_DATE_TAG = 
      "InvalidSearchCriteria.date";
    /**
      invalid search criteria time tag
    **/
    public static final String INVALID_SEARCH_CRITERIA_TIME_TAG = 
      "InvalidSearchCriteria.time";
    //--------------------------------------------------------------------------
    /**
       This aisle validates the start transaction number is after the end
       transaction number, the start date is after the end date, and the start
       time is after the end time. There is no validation performed on the
       cashier id nor the sales associate id.
       <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        EJournalCargo cargo = (EJournalCargo) bus.getCargo();
        TransactionLookupBeanModel model =
            (TransactionLookupBeanModel)ui.getModel(POSUIManagerIfc.FIND_TRANSACTION);

        String next = "Display";

        // Pull out just the sequence numbers for comparison.
        // TransactionID is smart enough to know how to do
        // this based on domain properties.
        //
        TransactionIDIfc startId = DomainGateway.getFactory().getTransactionIDInstance();
        startId.setTransactionID(model.getStartTransaction());

        TransactionIDIfc endId = DomainGateway.getFactory().getTransactionIDInstance();
        endId.setTransactionID(model.getEndTransaction());

        long start = startId.getSequenceNumber();
        long end = endId.getSequenceNumber();

        if ((start > end) ||
            (model.getStartTransaction().compareTo("") == 0 && !(model.getEndTransaction().compareTo("") == 0)) ||
            (!(model.getStartTransaction().compareTo("") == 0) && model.getEndTransaction().compareTo("") == 0)
            || ((model.getStartTransaction().compareTo("") != 0) && (model.getStartTransaction().length() != TransactionID.getTransactionIDLength())) 
            || ((model.getStartTransaction().compareTo("") != 0) && (model.getEndTransaction().length() != TransactionID.getTransactionIDLength())))
        {
            next = "Error";
            model.setFocusField("StartTransactionIDField");
            cargo.setErrorMsg(INVALID_SEARCH_CRITERIA_TRANSACTIONID_TAG);
        }
        //if start date is invalid but end date is ok or date range is invalid show error
        else if ((!(model.getStartDate().isValid()) && (model.getEndDate().isValid())) ||
                (!(model.getEndDate().isValid()) && (model.getStartDate().isValid()))
                ||(model.getStartDate().after(model.getEndDate())))
        {
            model.setFocusField("StartDateField");
            cargo.setErrorMsg(INVALID_SEARCH_CRITERIA_DATE_TAG);
            next = "Error";
        }
        else if ((!(model.getStartTime().isValid()) && (model.getEndTime().isValid())) ||
                (!(model.getEndTime().isValid()) && (model.getStartTime().isValid()))
                ||(model.getStartTime().after(model.getEndTime())))
        {
            model.setFocusField("StartTimeField");
            cargo.setErrorMsg(INVALID_SEARCH_CRITERIA_TIME_TAG);
            next = "Error";
        }
        cargo.setBeanModel(model);

        bus.mail(new Letter(next), BusIfc.CURRENT);

    }
    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------

    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  EvaluateSearchAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()
}
