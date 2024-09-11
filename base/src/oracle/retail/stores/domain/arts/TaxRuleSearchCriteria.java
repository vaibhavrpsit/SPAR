/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TaxRuleSearchCriteria.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:59 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:44 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:34:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Dec 13 2002 14:02:46   sfl
 * Added checkings on selling store state/province and shipping destination address state/province to support Canadian tax calculation.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.1   14 Aug 2002 10:13:56   sfl
 * Added shipping destination postal code based tax rule query method and supporting attribute.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.0   12 Aug 2002 16:04:28   sfl
 * Initial revision.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.stock.ItemIfc;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields to interact with the
    tax rule read.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class TaxRuleSearchCriteria implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1560223181995966323L;

    /**
        The store ID
    **/
    protected String storeID = null;

    /**
        The postal code
    **/
    protected String postalCode = null;

    /**
        The store address state
    **/
    protected String state = null;

    /**
        The shipping destination address state
    **/
    protected String destinationState = null;

    /**
        The list of search criteria
    **/
    protected ItemIfc item = null;


    //---------------------------------------------------------------------
    /**
        Class constructor.
        @param  storeID             The store ID
        @param  item                object of ItemIfc
    **/
    //---------------------------------------------------------------------
    public TaxRuleSearchCriteria(String storeID, ItemIfc item)
    {
        this.storeID = storeID;
        this.item = item;
    }

    //---------------------------------------------------------------------
    /**
        Class constructor.
        @param  storeID             The store ID
        @param state                The store address state
        @param  item                an object of ItemIfc
    **/
    //---------------------------------------------------------------------
    public TaxRuleSearchCriteria(String storeID, String state, ItemIfc item)
    {
        this.storeID = storeID;
        this.state = state;
        this.item = item;
    }

    //---------------------------------------------------------------------
    /**
        Class constructor.
        @param  storeID             The store ID
        @param postalCode           The postal code
        @param state                The store address state
        @param destinationState     The shipping destination address state
        @param  item                an object of ItemIfc
    **/
    //---------------------------------------------------------------------
    public TaxRuleSearchCriteria(String storeID, String postalCode, String state, String destinationState, ItemIfc item)
    {
        this.storeID = storeID;
        this.postalCode = postalCode;
        this.state = state;
        this.destinationState = destinationState;
        this.item = item;
    }

    //---------------------------------------------------------------------
    /**
        Returns the store id
        <p>
        @return  the store id
    **/
    //---------------------------------------------------------------------
    public String getStoreID()
    {
        return(storeID);
    }

    //---------------------------------------------------------------------
    /**
        Sets the store id
        <p>
        @param  value   The store id
    **/
    //---------------------------------------------------------------------
    public void setStoreID(String value)
    {
        storeID = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the postalCode
        <p>
        @return  the postal code
    **/
    //---------------------------------------------------------------------
    public String getPostalCode()
    {
        return(postalCode);
    }

    //---------------------------------------------------------------------
    /**
        Sets the postal code. <p>
        @param  value   The postal code
    **/
    //---------------------------------------------------------------------
    public void setPostalCode(String value)
    {
        postalCode = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the store address state
        <p>
        @return  the store address state
    **/
    //---------------------------------------------------------------------
    public String getState()
    {
        return(state);
    }

    //---------------------------------------------------------------------
    /**
        Sets the store address state. <p>
        @param  value   state
    **/
    //---------------------------------------------------------------------
    public void setState(String value)
    {
        state = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the shipping destination address state
        <p>
        @return  the shipping destination address state
    **/
    //---------------------------------------------------------------------
    public String getDestinationState()
    {
        return(destinationState);
    }

    //---------------------------------------------------------------------
    /**
        Sets the shipping destination address state. <p>
        @param  value   destinationState
    **/
    //---------------------------------------------------------------------
    public void setDestinationState(String value)
    {
        destinationState = value;
    }

    //---------------------------------------------------------------------
    /**
        Get item
        @return ItemIfc item
    **/
    //---------------------------------------------------------------------
    public ItemIfc getItem()
    {
        return this.item;
    }

    //---------------------------------------------------------------------
    /**
        Sets the item
        @param  ItemIfc   item
    **/
    //---------------------------------------------------------------------
    public void setItem(ItemIfc item)
    {
        this.item = item;
    }

}
