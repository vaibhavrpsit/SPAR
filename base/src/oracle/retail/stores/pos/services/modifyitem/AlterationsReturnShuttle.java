/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/AlterationsReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    10/27/08 - use localized price override reason codes
 *
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse
     $
     Revision 1.5  2004/09/23 00:07:12  kmcbride
     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

     Revision 1.4  2004/04/09 16:55:59  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.3  2004/02/12 16:51:01  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:39:28  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:01:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 05 2003 18:19:34   DCobb
 * Genrealized names of alteration attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.1   Aug 21 2002 11:21:24   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.modifyitem;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.alterations.AlterationsCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the modifyItem service to the cargo used in the Alterations service. <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class AlterationsReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2078548209881910603L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.AlterationsReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       transaction
    **/
    protected SaleReturnTransactionIfc transaction;

    /**
       The line item to apply alterations
    **/
    protected PLUItemIfc item = null;

    /**
       add PLU Item flag
    **/
   boolean addPLUItem = false;

    //----------------------------------------------------------------------
    /**
       Loads cargo from alterations service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()

        AlterationsCargo cargo = (AlterationsCargo) bus.getCargo();
        item = cargo.getPLUItem();
        transaction = (SaleReturnTransactionIfc)cargo.getTransaction();
        addPLUItem = cargo.getAddPLUItem();

    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads cargo for modifyItem service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()

        ItemCargo cargo = (ItemCargo) bus.getCargo();
        if (cargo.getPLUItem() != null)
        {
            cargo.setPLUItem(item);
        }
        else if (cargo.getItem() != null)
        {
            LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
            localizedReasonCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
            SaleReturnLineItemIfc returnItem = cargo.getItem();
            returnItem.setAlterationItemFlag(true);
            returnItem.setPLUItem(item);
            returnItem.modifyItemPrice(item.getPrice(),localizedReasonCode);
            returnItem.getItemPrice().calculateItemTotal();
            SaleReturnLineItemIfc[] items = cargo.getItems();
            items[0].setAlterationItemFlag(true);
            items[0].setPLUItem(item);
            items[0].modifyItemPrice(item.getPrice(), localizedReasonCode);
            items[0].getItemPrice().calculateItemTotal();
        }
        cargo.setTransaction(transaction);
        cargo.setAddPLUItem(addPLUItem);
        cargo.setAlterationItemFlag(true);

    }                                   // end unload()

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
        String strResult = new String("Class:  AlterationsReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}
