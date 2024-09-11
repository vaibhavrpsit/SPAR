/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSTill.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:02 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields in the ARTS data model
    which are not defined in the POS domain object model.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ARTSTill implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7901306347053607990L;

    /** The POS domain Till **/
    protected TillIfc posTill = null;

    /** The register **/
    protected RegisterIfc register = null;

    /** The till ID **/
    protected String tillID = null;

    /** The store ID **/
    protected String storeID = null;

    /** The business day **/
    protected EYSDate businessDate = null;

    //---------------------------------------------------------------------
    /**
        Class constructor.
        <P>
        @param  tillID      The till id
        @param  storeID     The store ID
    **/
    //---------------------------------------------------------------------
    public ARTSTill(String tillID, String storeID)
    {
        this.tillID = tillID;
        this.storeID = storeID;
    }

    //---------------------------------------------------------------------
    /**
        Class constructor.
        <P>
        @param  till        The POS domain till
        @param  register The POS domain register
    **/
    //---------------------------------------------------------------------
    public ARTSTill(TillIfc till, RegisterIfc register)
    {
        posTill = till;
        this.register = register;
    }

    //---------------------------------------------------------------------
    /**
        Returns the POS domain Till
        <p>
        @return  the POS domain Till
    **/
    //---------------------------------------------------------------------
    public TillIfc getPosTill()
    {
        return(posTill);
    }

    //---------------------------------------------------------------------
    /**
        Sets the POS domain Till
        <p>
        @param  till    The POS domain Till
    **/
    //---------------------------------------------------------------------
    public void setPosTill(TillIfc till)
    {
        posTill = till;
    }

    //---------------------------------------------------------------------
    /**
        Returns the register
        <p>
        @return  the register
    **/
    //---------------------------------------------------------------------
    public RegisterIfc getRegister()
    {
        return(register);
    }

    //---------------------------------------------------------------------
    /**
        Sets the register
        <p>
        @param  register    The new value for register
    **/
    //---------------------------------------------------------------------
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    //---------------------------------------------------------------------
    /**
        Returns the till ID
        <p>
        @return  the till ID
    **/
    //---------------------------------------------------------------------
    public String getTillID()
    {
        if (tillID == null && posTill != null)
        {
            return(posTill.getTillID());
        }

        return(tillID);
    }

    //---------------------------------------------------------------------
    /**
        Sets the tillID
        <p>
        @param  tillID    The new value for till ID
    **/
    //---------------------------------------------------------------------
    public void setTillID(String tillID)
    {
        this.tillID = tillID;
    }

    //---------------------------------------------------------------------
    /**
        Returns the store ID
        <p>
        @return  the store ID
    **/
    //---------------------------------------------------------------------
    public String getStoreID()
    {
        if (storeID == null && register != null)
        {
            return(register.getWorkstation().getStoreID());
        }

        return(storeID);
    }

    //---------------------------------------------------------------------
    /**
        Sets the store ID
        <p>
        @param  storeID    The new value for store ID
    **/
    //---------------------------------------------------------------------
    public void setStoreID(String storeID)
    {
        this.storeID = storeID;
    }

    //---------------------------------------------------------------------
    /**
        Returns the business day
        <p>
        @return  the business day
    **/
    //---------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {
        return(businessDate);
    }

    //---------------------------------------------------------------------
    /**
        Sets the business day
        <p>
        @param  businessDate    The new value for business day
    **/
    //---------------------------------------------------------------------
    public void setBusinessDate(EYSDate businessDate)
    {
        this.businessDate = businessDate;
    }
}
