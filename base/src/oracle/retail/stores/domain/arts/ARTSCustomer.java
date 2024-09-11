/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSCustomer.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.2  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:44   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AbstractRoutable;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields in the ARTS data model
    which are not defined in the POS domain object model.  <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ARTSCustomer
extends AbstractRoutable
implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1755557052288613418L;

    /** The POS domain Customer **/
    protected CustomerIfc posCustomer = null;

    /** The party ID in the ARTS data model. **/
    protected int partyId = 0;

    //---------------------------------------------------------------------
    /**
        Class constructor. <P>
        @param  transaction
    **/
    //---------------------------------------------------------------------
    public ARTSCustomer(CustomerIfc customer)
    {
        posCustomer = customer;
    }

    //---------------------------------------------------------------------
    /**
        Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public ARTSCustomer()
    {
    }

    //---------------------------------------------------------------------
    /**
        @return  the POS domain Customer
    **/
    //---------------------------------------------------------------------
    public CustomerIfc getPosCustomer()
    {
        return posCustomer;
    }

    //---------------------------------------------------------------------
    /**
        @param  customer
    **/
    //---------------------------------------------------------------------
    public void setPosCustomer(CustomerIfc customer)
    {
        posCustomer = customer;
    }

    //---------------------------------------------------------------------
    /**
        @return  the value of party id
    **/
    //---------------------------------------------------------------------
    public int getPartyId()
    {
        return partyId;
    }

    //---------------------------------------------------------------------
    /**
        @param  id
    **/
    //---------------------------------------------------------------------
    public void setPartyId(int id)
    {
        partyId = id;
    }

}
