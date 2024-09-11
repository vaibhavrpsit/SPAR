/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/StoreStatus.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/05/10 - update doc and toString method
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/1/2008 2:29:33 PM    Deepti Sharma   CR
 *         31016 forward port from v12x -> trunk
 *    3    360Commerce 1.2         3/31/2005 4:30:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:31 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.5  2004/07/13 22:33:39  cdb
 *   @scr 5970 in Services Impact Tracker database - removed hardcoding of class names
 *   in all getHardTotalsData methods.
 *
 *   Revision 1.4  2004/02/17 16:18:53  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jun 10 2003 11:50:38   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 * 
 *    Rev 1.1   May 20 2003 07:44:02   jgs
 * Deprecated getHardTotalsData() and setHardTotalsData() methods.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 * 
 *    Rev 1.0   Jun 03 2002 16:52:50   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Mar 2002 12:30:08   epd
 * Jose asked me to check these in.  Updates to use TenderDescriptor
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.1   03 Dec 2001 16:10:54   epd
 * Added code to contain valid list of store safe tender types
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 20 2001 16:14:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.io.Serializable;
import java.util.ArrayList;

import oracle.retail.stores.domain.store.Store;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class represents the status for a store. It is to be differentiated from
 * the {@link oracle.retail.stores.domain.store.Store} class, which represents
 * the physical store and its attributes.
 * <P>
 * The store status class allows an application to set the sign-on operator,
 * status, open and close time and business day. Methods are also provided to
 * maintain the financial totals for the store.
 * <P>
 * In a typical EYS POS implementation, the store status class would be used
 * primarily as a vehicle for database maintenance. The store status would
 * probably not be maintained in cargo through the entire application.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
public class StoreStatus extends AbstractStatusEntity implements StoreStatusIfc
{
    private static final long serialVersionUID = 3346199987418930756L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * physical store object
     */
    protected StoreIfc store = null;

    /**
     * store safe valid tender descriptor. Used to validate storeSafe tenders
     */
    protected ArrayList<String> tendersForStoreSafe = new ArrayList<String>();

    /**
     * store safe valid tender descriptor. Used to validate storeSafe tenders
     */
    protected ArrayList<TenderDescriptorIfc> tenderDescForStoreSafe = new ArrayList<TenderDescriptorIfc>();

    /**
     * This boolean indicates that status (open, closed and business date) may
     * or may not be up to date. This is can happen when the container of this
     * class is out of touch with the the database.
     */
    protected boolean stale = false;

    /**
     * Constructs StoreStatus object.
     */
    public StoreStatus()
    {
    }

    /**
     * Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        StoreStatusIfc st = new StoreStatus();

        // set clone attributes
        setCloneAttributes(st);

        // pass back Object
        return st;
    }

    /**
     * Sets attributes in clone.
     * 
     * @param newClass new instance of class
     */
    protected void setCloneAttributes(StoreStatusIfc newClass)
    {
        super.setCloneAttributes((AbstractStatusEntity) newClass);

        if (store != null)
        {
            newClass.setStore((StoreIfc) store.clone());
        }

        if (tendersForStoreSafe.size() != 0)
        {
            for (int i = 0; i < tendersForStoreSafe.size(); i++)
            {
                newClass.addSafeTenderType(tendersForStoreSafe.get(i));
            }
        }
        if (tenderDescForStoreSafe.size() != 0)
        {
            for (int i = 0; i < tenderDescForStoreSafe.size(); i++)
            {
                newClass.addSafeTenderDesc(tenderDescForStoreSafe.get(i));
            }
        }

        newClass.setStale(isStale());
    }

    /**
     * Sets reference to store interface.
     * 
     * @param value reference to store interface
     */
    public void setStore(StoreIfc value)
    {
        store = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.AbstractStatusEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = false;
        if (obj instanceof StoreStatus)
        {
            StoreStatus c = (StoreStatus) obj; // downcast the input object
            // compare all the attributes of ReconcilableCount
            if (super.equals(obj)
                    && Util.isObjectEqual(getStore(), c.getStore())
                    && stale == c.isStale())
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        return isEqual;
    }

    /**
     * Retrieves reference to store interface.
     * 
     * @return reference to store interface
     */
    public StoreIfc getStore()
    {
        return store;
    }

    /**
     * Gets an arraylist of the valid safe tender types.
     * 
     * @return arraylist of string tender types
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getSafeTenderTypeList()
    {
        return (ArrayList<String>) tendersForStoreSafe.clone();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.StoreStatusIfc#getSafeTenderTypeDescList()
     */
    @SuppressWarnings("unchecked")
    public ArrayList<TenderDescriptorIfc> getSafeTenderTypeDescList()
    {
        return (ArrayList<TenderDescriptorIfc>) tenderDescForStoreSafe.clone();
    }

    /**
     * Gets an array of the valid safe tender types.
     * 
     * @return array of string tender types
     */
    public String[] getSafeTenderTypes()
    {
        String[] returnArray = tendersForStoreSafe.toArray(new String[tendersForStoreSafe.size()]);
        return returnArray;
    }

    /**
     * Gets an array of the valid safe tender types.
     * 
     * @return array of tender descriptors
     */
    public TenderDescriptorIfc[] getSafeTenderTypeDesc()
    {
        TenderDescriptorIfc[] returnArray = tenderDescForStoreSafe
                .toArray(new TenderDescriptorIfc[tenderDescForStoreSafe.size()]);
        return returnArray;
    }

    /**
     * Adds a tender type to the list of valid tender types.
     */
    public void addSafeTenderType(String value)
    {
        tendersForStoreSafe.add(value);
    }

    /**
     * Adds a tender type to the list of valid tender types.
     */
    public void addSafeTenderDesc(TenderDescriptorIfc value)
    {
        tenderDescForStoreSafe.add(value);
    }

    /**
     * This method converts hard totals information to a comma delimited String.
     * 
     * @return String
     */
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        builder.appendStringObject(getClass().getName());
        super.getHardTotalsData(builder);

        if (store == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            store.getHardTotalsData(builder);
        }

        int len = 0;
        Object[] tenderTypes = tendersForStoreSafe.toArray();
        if (tendersForStoreSafe.size() != 0)
        {
            len = tenderTypes.length;
        }
        builder.appendInt(len);

        for (int i = 0; i < len; i++)
        {
            builder.appendString(tenderTypes[i].toString());
        }

    }

    /**
     * This method populates this object from a comma delimited string.
     * 
     * @param String String containing hard totals data.
     */
    public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        super.setHardTotalsData(builder);
        // Get the store
        store = (StoreIfc) builder.getFieldAsClass();
        if (store != null)
        {
            store.setHardTotalsData(builder);
        }

        int number = builder.getIntField();
        for (int i = 0; i < number; i++)
        {
            addSafeTenderType(builder.getStringField());
        }

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("**** START STORE STATUS **** START STORE STATUS **** START STORE STATUS\n");
        strResult.append("Class:  StoreStatus (Revision " + getRevisionNumber() + ") @" + hashCode());
        strResult.append("stale:                                [" + stale + "]\n");

        if (store == null)
        {
            strResult.append("\nstore:                                  [null]\n");
        }
        else
        {
            strResult.append(store).append("\n");
        }

        if (tendersForStoreSafe.size() != 0)
        {

            strResult.append(tendersForStoreSafe.size()).append("\n"); // debug
            for (int i = 0; i < tendersForStoreSafe.size(); i++)
            {
                strResult.append(tendersForStoreSafe.get(i));
                strResult.append("\n");
            }
        }

        strResult.append(attributesToString());

        strResult.append("**** END STORE STATUS **** END STORE STATUS **** END STORE STATUS\n\n");
        // pass back result
        return strResult.toString();
    }

    /**
     * @return Returns the stale boolean.
     */
    public boolean isStale()
    {
        return stale;
    }

    /**
     * @param stale The stale value to set.
     */
    public void setStale(boolean stale)
    {
        this.stale = stale;
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * StoreStatus main method.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>toString() output
     * </UL>
     * 
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        StoreStatusIfc clsStoreStatus = new StoreStatus();
        // output toString()
        System.out.println(clsStoreStatus.toString());

        try
        {
            // instantiate class
            HardTotalsStringBuilder builder = null;
            Serializable obj = null;
            StoreStatus a1 = new StoreStatus();
            StoreStatusIfc a2 = null;

            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            obj = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2 = (StoreStatusIfc) builder.getFieldAsClass();
            a2.setHardTotalsData(builder);

            if (a1.equals(a2))
            {
                System.out.println("Empty StoreStatus are equal");
            }
            else
            {
                System.out.println("Empty StoreStatus are NOT equal");
                System.out.println("RC 1 = " + a1.toString());
                System.out.println("RC 2 = " + a2.toString());
            }

            // instantiate class
            Store store = new Store();
            a1.setStore(store);

            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            obj = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2 = (StoreStatusIfc) builder.getFieldAsClass();
            a2.setHardTotalsData(builder);

            if (a1.equals(a2))
            {
                System.out.println("Full StoreStatus are equal");
            }
            else
            {
                System.out.println("Full StoreStatus are NOT equal");
                System.out.println("RC 1 = " + a1.toString());
                System.out.println("RC 2 = " + a2.toString());
            }
        }
        catch (HardTotalsFormatException iae)
        {
            System.out.println("StoreStaus convertion failed:");
            iae.printStackTrace();
        }
    }
}