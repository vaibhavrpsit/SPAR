/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/AlterationsUtilities.java /main/20 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nganesh   02/17/09 - Price data is trimmed to remove extra blank line
 *                         coming in EJ
 *    deghosh   02/05/09 - EJ i18n defect fixes
 *    vchengeg  12/08/08 - EJ I18n formatting
 *    deghosh   11/19/08 - Modified the lines which were throwing exception for
 *                         EJ
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/12/2008 6:36:44 PM   Christian Greene
 *         upgrade StringBuffer to StringBuilder for performance
 *    4    360Commerce 1.3         8/7/2007 4:50:27 PM    Charles D. Baker CR
 *         27244 - Formatting for Alteration line was incomplete.
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *    Rev 1.0   Aug 29 2003 15:53:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 25 2003 17:36:30   DCobb
 * Initial revision.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// java imports
import java.text.BreakIterator;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
    AlterationsUtilities contains methods that are shared by more than one
    service.<P>
    @version $Revision: /main/20 $
**/
//------------------------------------------------------------------------------
public class AlterationsUtilities
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.alterations.AlterationsUtilities.class);
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/20 $";
    /**
         item price length
     **/
     protected static final int ITEM_PRICE_LENGTH = 13;
/*     *//**
         spaces buffer
     **//*
     protected static final String SPACES = "                                        ";
     *//**
         standard line length
     **//*
     public static final int LINE_LENGTH = 40;
     *//**
         alteration line length
     **//*
     protected static final int ALT_LINE_LENGTH = LINE_LENGTH - 4;
     *//**
         indention length
     **//*
     protected static final int ALT_INDENT = 4;*/

     /** Alterations Labels for journaling **/
     //protected static final String ALTERATION = "Alteration:";
     protected static final String TAX = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.T_LABEL, null);
     protected static final String COAT = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.COAT_ALTERATIONS_LABEL, null);
     protected static final String DRESS = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DRESS_ALTERATIONS_LABEL, null);
     protected static final String PANTS = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PANTS_ALTERATIONS_LABEL, null);
     protected static final String SHIRT = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SHIRT_ALTERATIONS_LABEL, null);
     protected static final String SKIRT = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SKIRT_ALTERATIONS_LABEL, null);
     protected static final String HEM = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.HEM_LABEL, null);
     protected static final String SLEEVE = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SLEEVE_LABEL, null);
     protected static final String TAPER = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAPER_LABEL, null);
     protected static final String NECK = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NECK_LABEL, null);
     protected static final String WAIST = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.WAIST_LABEL, null);
     protected static final String OTHER = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OTHER_LABEL, null);
     protected static final String REPAIRS = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REPAIRS_LABEL, null);
     protected static final String ONE_AT = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ONE_AT_LABEL, null);

     // configure models
     protected static final String[] COAT_OR_DRESS_LABELS = {
         HEM, SLEEVE, TAPER, NECK, WAIST, OTHER
     };
     protected static final String[] PANTS_OR_SKIRT_LABELS = {
         HEM, TAPER, WAIST, OTHER
     };
     protected static final String[] SHIRT_LABELS = {
         SLEEVE, TAPER, NECK, OTHER
     };
     protected static final String[] REPAIRS_LABELS = {
         REPAIRS, OTHER
    };


    //---------------------------------------------------------------------
    /**
       Returns a journal entry string for the Alteration item. <p>
       @param alterationItem   the alteration item
       @return String   the journal entry string
    **/
    //---------------------------------------------------------------------
    public static String journalAlterationItem(AlterationPLUItemIfc altItem)
    {
        StringBuilder sb = new StringBuilder();
        Object[] dataArgs = null;

        // Get the alteration object
        AlterationIfc alterationObject = altItem.getAlteration();

        sb.append(Util.EOL);

        String price = altItem.getPrice().toGroupFormattedString().trim();
        String itemId = altItem.getItemID();

        String taxFlag = new String(TAX);
        boolean taxable = altItem.getTaxable();
        if (taxable == false)
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
        }
        else
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_STANDARD];
        }

        dataArgs = new Object[]{itemId,price,taxFlag};

        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ALTERATION_LABEL, dataArgs));

        sb.append(Util.EOL);

        // alteration instructions
        sb.append(toJournalString(alterationObject));

        sb.append(Util.EOL);

        dataArgs = new Object[]{ONE_AT,price,taxFlag};

        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ALTERATIONS_LABEL, dataArgs));

        sb.append(Util.EOL);
        sb.append(Util.EOL);

        return sb.toString();
    }

    //---------------------------------------------------------------------
    /**
        Journals the instructions for a single alteration object. <P>
        @param alteration the alteration object.
        @param indentCount the number of spaces to indent
        @param lineLength the line length
        @return the journal string
    **/
   //---------------------------------------------------------------------
    public static String toJournalString(AlterationIfc alteration)
    {
        StringBuilder journalString = new StringBuilder();
        String[] labels = null;

        journalString.append(Util.EOL);

        // ItemDescription
        if (alteration.getItemDescription()!=null &&
            alteration.getItemDescription().length() > 0)
        {
            journalString.append(alteration.getItemDescription()+Util.EOL);
        }

        // ItemNumber
        if (alteration.getItemNumber()!=null &&
            alteration.getItemNumber().length() > 0)
        {
            journalString.append(alteration.getItemNumber()+Util.EOL);
        }

        // Alteration Type
        int alterationType = alteration.getAlterationType();
        if (alterationType != AlterationIfc.TYPE_UNDEFINED)
        {
            String alterationTypeNameText = "";
            switch (alterationType)
            {
                case AlterationIfc.COAT_TYPE :
                {
                    alterationTypeNameText = COAT;
                    labels = COAT_OR_DRESS_LABELS;
                    break;
                }
                case AlterationIfc.DRESS_TYPE :
                {
                    alterationTypeNameText = DRESS;
                    labels = COAT_OR_DRESS_LABELS;
                    break;
                }
                case AlterationIfc.PANTS_TYPE :
                {
                    alterationTypeNameText = PANTS;
                    labels = PANTS_OR_SKIRT_LABELS;
                    break;
                }
                case AlterationIfc.REPAIRS_TYPE :
                {
                    alterationTypeNameText = REPAIRS;
                    labels = REPAIRS_LABELS;
                    break;
                }
                case AlterationIfc.SHIRT_TYPE :
                {
                    alterationTypeNameText = SHIRT;
                    labels = SHIRT_LABELS;
                    break;
                }
                case AlterationIfc.SKIRT_TYPE :
                {
                    alterationTypeNameText = SKIRT;
                    labels = PANTS_OR_SKIRT_LABELS;
                    break;
                }
            }
            journalString.append(alterationTypeNameText+Util.EOL);
        }

        // add alteration model data ...
        if (labels != null)
        {
            for (int i = 0; i < labels.length; i++)
            {
                String text = null;
                switch (i)
                {
                    case (0) : text = alteration.getValue1(); break;
                    case (1) : text = alteration.getValue2(); break;
                    case (2) : text = alteration.getValue3(); break;
                    case (3) : text = alteration.getValue4(); break;
                    case (4) : text = alteration.getValue5(); break;
                    case (5) : text = alteration.getValue6(); break;
                    default :
                }

                if (text != null && text.length() > 0)
                {
                    journalString.append((new MessageFormat(labels[i]).format(new Object[]{text}))+Util.EOL);
                }
            }
        }

        return journalString.toString();
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.   <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  getClass().getName() (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
