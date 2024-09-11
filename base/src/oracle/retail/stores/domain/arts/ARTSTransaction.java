/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
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
 *    Rev 1.1   Mar 18 2002 22:44:52   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:38   msg
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

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields in the ARTS data model
    which are not defined in the POS domain object model.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ARTSTransaction implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9005655421962410470L;

    /** The ARTS Customer **/
    protected ARTSCustomer artsCustomer = null;
    /** The POS domain Transaction **/
    protected TransactionIfc posTransaction = null;
    /** The ARTS party ID **/
    protected int partyID = 0;

    //---------------------------------------------------------------------
    /**
        Class constructor.
        <P>
        @param  transaction
    **/
    //---------------------------------------------------------------------
    public ARTSTransaction(TransactionIfc transaction)
    {
        CustomerIfc customer = null;
        int ID = 0;

        switch (transaction.getTransactionType())
        {
            case TransactionIfc.TYPE_SALE:
            case TransactionIfc.TYPE_RETURN:
                customer = ((TenderableTransactionIfc) transaction).getCustomer();
                break;
            case TransactionIfc.TYPE_VOID:
                TenderableTransactionIfc trans = ((VoidTransactionIfc) transaction).getOriginalTransaction();

                if (trans != null)
                {
                    customer = trans.getCustomer();
                }
                break;
            default:    // do nothing
                break;
        }

        if (customer != null)
        {
            artsCustomer = new ARTSCustomer(customer);
            partyID = artsCustomer.getPartyId();
        }
        posTransaction = transaction;
    }

    //---------------------------------------------------------------------
    /**
        @return  the POS domain Transaction
    **/
    //---------------------------------------------------------------------
    public TransactionIfc getPosTransaction()
    {
        return(posTransaction);
    }

    //---------------------------------------------------------------------
    /**
        @param  transaction
    **/
    //---------------------------------------------------------------------
    public void setPosTransaction(TransactionIfc transaction)
    {
        posTransaction = transaction;
    }

    //---------------------------------------------------------------------
    /**
        @return  the value of party id
    **/
    //---------------------------------------------------------------------
    public int getPartyId()
    {
        return(partyID);
    }

    //---------------------------------------------------------------------
    /**
        @param  id
    **/
    //---------------------------------------------------------------------
    public void setPartyId(int id)
    {
        partyID = id;
    }

    //---------------------------------------------------------------------
    /**
        @return  the value ARTS Customer
    **/
    //---------------------------------------------------------------------
    public ARTSCustomer getArtsCustomer()
    {
        return(artsCustomer);
    }

    //---------------------------------------------------------------------
    /**
        @param  customer
    **/
    //---------------------------------------------------------------------
    public void setArtsCustomer(ARTSCustomer customer)
    {
        artsCustomer = customer;
    }
}
