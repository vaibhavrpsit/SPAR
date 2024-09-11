/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/registry/DontAttachRegistryIDAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:43 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:28  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:16:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   13 Mar 2002 17:07:36   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 * 
 *    Rev 1.0   Sep 21 2001 11:30:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.registry;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    ##COMMENT##
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class DontAttachRegistryIDAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets the Registry number in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        RegistryIDIfc giftRegistry = (RegistryIDIfc) DomainGateway.getFactory().getGiftRegistryInstance();
        // retrieve cargo
        ModifyTransactionGiftRegistryCargo cargo = (ModifyTransactionGiftRegistryCargo) bus.getCargo();
        String emptyString = new String("");
        // set in GiftRegistry
        giftRegistry.setID(emptyString);
        // put the gift registry in the cargo
        cargo.setNewRegistry(giftRegistry);
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  DontAttachRegistryIDAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
