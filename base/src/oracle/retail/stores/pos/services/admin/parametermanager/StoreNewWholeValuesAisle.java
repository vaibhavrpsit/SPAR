/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/StoreNewWholeValuesAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:05 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:29 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/20 18:41:52  cdb
 *   @scr 6127 Updated to use validation in validator rather than aisles.
 *
 *   Revision 1.4  2004/04/16 18:56:34  tfritz
 *   @scr 4251 - Integer parameters now can except negative and positive integers.
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:26   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:06   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:12:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ParametersCommon;
import oracle.retail.stores.pos.ui.beans.RetailParameter;
import oracle.retail.stores.pos.ui.beans.WholeParameterBeanModel;

//------------------------------------------------------------------------------
/**
    Store the parameter that the user has chosen in the cargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreNewWholeValuesAisle extends LaneActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Stores the parameter that the user has chosen in the cargo.
        <p>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        LetterIfc letter = new Letter(ParametersCommon.ACCEPT_DATA);
        
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        WholeParameterBeanModel model = (WholeParameterBeanModel)ui.getModel(POSUIManagerIfc.PARAM_EDIT_INTEGER);
        long newValue = model.getNewValue();
        String newModifiable = (String)model.getModifiableValue();

        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        RetailParameter cargoParam = cargo.getParameter();

        if (cargoParam instanceof WholeParameterBeanModel)
        {
            WholeParameterBeanModel wholeParam = (WholeParameterBeanModel)cargoParam;
            wholeParam.setModifiableValue(newModifiable);
            wholeParam.setNewValue(newValue);

            wholeParam.setValue(String.valueOf(newValue));

            try
            {
                cargo.convertBeanToParameter(wholeParam);
            }
            catch (ParameterException e)
            {
                letter = new Letter(ParametersCommon.REJECT_DATA);
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
