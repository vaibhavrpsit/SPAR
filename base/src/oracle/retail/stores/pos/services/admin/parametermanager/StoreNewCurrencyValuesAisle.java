/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/StoreNewCurrencyValuesAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:05 mszekely Exp $
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
 *    4    360Commerce 1.3         1/22/2006 11:45:05 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:29 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/20 18:41:52  cdb
 *   @scr 6127 Updated to use validation in validator rather than aisles.
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
 *    Rev 1.0   Aug 29 2003 15:52:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 09 2003 19:42:40   blj
 * corrected a rounding error that was causing the maxValue to be larger when compared the newValue set to the max.
 * Resolution for 2509: Entering max amounts on Numeric Currency parameters returns Invalid Parameter dialog
 * 
 *    Rev 1.1   Jun 21 2002 18:26:16   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 15:39:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:05:16   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:19:56   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:42:14   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   Jan 19 2002 10:28:04   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:11:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

// java imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.ParametersCommon;
import oracle.retail.stores.pos.ui.beans.RetailParameter;
import java.math.BigDecimal;

//------------------------------------------------------------------------------
/**
    Store the parameter that the user has chosen in the cargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreNewCurrencyValuesAisle extends LaneActionAdapter
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
        CurrencyParameterBeanModel model = (CurrencyParameterBeanModel)ui.getModel(POSUIManagerIfc.PARAM_EDIT_CURRENCY);
        BigDecimal newValue = model.getNewValue();
        String newModifiable = (String)model.getModifiableValue();


        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        RetailParameter cargoParam = cargo.getParameter();

        if (cargoParam instanceof CurrencyParameterBeanModel)
        {
            CurrencyParameterBeanModel currencyParam = (CurrencyParameterBeanModel)cargoParam;
            currencyParam.setModifiableValue(newModifiable);
            //This value matters, can be used for computation.
            currencyParam.setNewValue(newValue);

            currencyParam.setValue((newValue.setScale(2, BigDecimal.ROUND_HALF_UP)).toString());

            try
            {
                cargo.convertBeanToParameter(currencyParam);
            }
            catch (ParameterException e)
            {
                letter = new Letter(ParametersCommon.REJECT_DATA);
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
