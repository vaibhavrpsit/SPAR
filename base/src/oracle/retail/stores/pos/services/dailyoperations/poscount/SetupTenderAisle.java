/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/SetupTenderAisle.java /main/13 2012/10/16 17:37:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/10/12 - Popup menu implementation
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:39  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:30:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:48   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;

/**
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class SetupTenderAisle extends LaneActionAdapter
{
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Sends a Next letter.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        int index = getIndexFromLetter(letter);

        PosCountCargo cargo = (PosCountCargo) bus.getCargo();
        SummaryCountBeanModel[] scbm = cargo.getCountModels();
        cargo.setCurrentActivityOrCharge(scbm[index].getDescription());

        String letterName = "CountSummary";
        if (cargo.getCurrentActivity().equals(PosCountCargo.CHARGE))
        {
            letterName = "CountCharge";
        }
        else if (!cargo.getSummaryFlag())
        {
            if (cargo.currentHasDenominations())
            {
                letterName = "CashDetail";
            }
            else
            {
                letterName = "CountDetail";
            }
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    /**
     * Returns an integer index which has been drived from the name of the
     * letter.
     * 
     * @return String representation of object
     */
    protected int getIndexFromLetter(LetterIfc letter)
    {
        ButtonPressedLetter bpl = (ButtonPressedLetter) letter;

        // Adjust the number to acount for the "More" button
        // on the button bars.
        int number = bpl.getNumber();
        if (number > 15)
        {
            number = number - 2;
        }
        else if (number > 7)
        {
            number = number - 1;
        }

        return (number);
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  SetupTenderAisle (Revision " + getRevisionNumber() + ")" + hashCode());

        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * <P>
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}