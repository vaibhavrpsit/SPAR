/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/AlterationInfoEnteredAisle.java /main/11 2012/04/25 10:25:37 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Mar 12 2003 12:27:24   DCobb
 * Code review cleanup.
 * Resolution for POS SCR-1753: POS 6.0 Alterations Package
 * 
 *    Rev 1.3   Mar 05 2003 18:18:12   DCobb
 * Generalized names of alterations attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.2   Aug 22 2002 16:09:42   DCobb
 * Set alteration type so that it can be printed and journaled.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.1   Aug 21 2002 11:21:20   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// Java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.AlterationsBeanModel;

//------------------------------------------------------------------------------
/**
   Alteration information has been entered
   @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
public class AlterationInfoEnteredAisle extends PosLaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //--------------------------------------------------------------------------
    /**
       Get the alteration information
       @param
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Reference to the cargo
        AlterationsCargo cargo = (AlterationsCargo)bus.getCargo();

        // Get the uiManager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Reference to the model
        AlterationsBeanModel model = (AlterationsBeanModel) ui.getModel();

        // transaction
        SaleReturnTransactionIfc transaction;

        // Get the alterion object
        AlterationPLUItemIfc item = (AlterationPLUItemIfc) cargo.getPLUItem();
        AlterationIfc alterationObject = null;
        if (item != null)
        {
            alterationObject = (AlterationIfc)item.getAlteration();

            if (alterationObject != null)
            {
                // Set the data in the alterationObject
                if (model.getItemDescription()!=null)
                {
                    alterationObject.setItemDescription(model.getItemDescription());
                }
                if (model.getItemNumber()!=null)
                {
                    alterationObject.setItemNumber(model.getItemNumber());
                }
                if (model.getValue1()!=null)
                {
                    alterationObject.setValue1(model.getValue1());
                }
                if (model.getValue2()!=null)
                {
                    alterationObject.setValue2(model.getValue2());
                }
                if (model.getValue3()!=null)
                {
                    alterationObject.setValue3(model.getValue3());
                }
                if (model.getValue4()!=null)
                {
                    alterationObject.setValue4(model.getValue4());
                }
                if (model.getValue5()!=null)
                {
                    alterationObject.setValue5(model.getValue5());
                }
                if (model.getValue6()!=null)
                {
                    alterationObject.setValue6(model.getValue6());
                }
    
                // set the alteration type
                int alterationType = AlterationIfc.TYPE_UNDEFINED;
                String alterationsModelName = model.getAlterationsModel();
                if (alterationsModelName != null) {
                    if (alterationsModelName.equals(AlterationsCargo.ACTION_PANTS))
                    {
                        alterationType = AlterationIfc.PANTS_TYPE;
                    }
                    else if (alterationsModelName.equals(AlterationsCargo.ACTION_SKIRT))
                    {
                        alterationType = AlterationIfc.SKIRT_TYPE;
                    }
                    else if (alterationsModelName.equals(AlterationsCargo.ACTION_COAT))
                    {
                        alterationType = AlterationIfc.COAT_TYPE;
                    }
                    else if (alterationsModelName.equals(AlterationsCargo.ACTION_DRESS))
                    {
                        alterationType = AlterationIfc.DRESS_TYPE;
                    }
                    else if (alterationsModelName.equals(AlterationsCargo.ACTION_REPAIRS))
                    {
                        alterationType = AlterationIfc.REPAIRS_TYPE;
                    }
                    else if (alterationsModelName.equals(AlterationsCargo.ACTION_SHIRT))
                    {
                        alterationType = AlterationIfc.SHIRT_TYPE;
                    }
                }
                alterationObject.setAlterationType(alterationType);
    
                // Set the alterationObject in the alterationPLUItem
                item.setAlteration(alterationObject);
    
                // Set the plu in the cargo
                cargo.setPLUItem(item);
            }
        }

        // Mail the letter
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

}
