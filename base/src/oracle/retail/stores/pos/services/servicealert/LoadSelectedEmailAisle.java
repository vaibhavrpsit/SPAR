/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/LoadSelectedEmailAisle.java /main/10 2011/02/16 09:13:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:20 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/12 18:52:57  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:51:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:03:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:05:28   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

// Foundation imports
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EMessageReadDataTransaction;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This Aisle retrieves the complete Email object when the user selects an email
 * item from the Service Alert List screen and presses the next button.
 * 
 * @version $Revision: /main/10 $
 */
public class LoadSelectedEmailAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -1071946661863526539L;

    public static final String LANENAME = "LoadSelectedEmailAisle";

    /**
     * This site retrieves the complete EMail object from the database using the
     * Alert Entry selected from the Service Alert List and mails a letter.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ServiceAlertCargo cargo = (ServiceAlertCargo) bus.getCargo();
        Letter letter = new Letter("EmailDetail");

        // retrieve the selected email from the database and load it into cargo.
        EMessageReadDataTransaction erdt = null;
        
        erdt = (EMessageReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.EMESSAGE_READ_DATA_TRANSACTION);
        
        EMessageIfc message = null;

        try
        {
            message = erdt.retrieveEMessage(cargo.getSelectedEntry());
            cargo.setSelectedMessage(message);
        }
        catch (DataException de)
        {
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            letter = new Letter(CommonLetterIfc.FAILURE);
        }

        bus.mail(letter, BusIfc.CURRENT);
    }
}
