/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/Log.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:21 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/09/23 00:30:48  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.8  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.7  2004/04/08 16:45:59  cdb
 *   @scr 4204 Removing tabs - again.
 *
 *   Revision 1.6  2004/04/08 06:34:18  smcgrigor
 *   Merge of Kintore POSLog v2.1 code from branch to TopOfTree
 *
 *   Revision 1.5.2.1  2004/03/17 04:13:49  mwright
 *   Initial revision for POSLog v2.1
 *
 *   Revision 1.5  2004/02/17 17:57:42  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:51  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:58:48   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   08 Jun 2002 15:55:56   vpn-mpm
 * Made modifications to improve extensibility.
 *
 *    Rev 1.0   Apr 21 2002 15:25:22   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log;
// XML imports
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;

//--------------------------------------------------------------------------
/**
    This class creates the POS log in IXRetail format. This construct is the
    container for the entire tlog and corresponds to the LogContainer
    schema.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class Log
implements LogIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2740112044575261011L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.ixretail.log.Log.class);
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        schema location
    **/
    protected String schemaLocation = POS_LOG_SCHEMA_LOCATION;

    //----------------------------------------------------------------------------
    /**
        Constructs Log object. <P>
    **/
    //----------------------------------------------------------------------------
    public Log()
    {                                   // begin Log()
    }                                   // end Log()
    //---------------------------------------------------------------------
    /**
        Name of root element varies by version.
        @return XML root element name
    **/
    //---------------------------------------------------------------------
    public String getLogRootElement()
    {
        return "POS360LogContainer";
    }

    //---------------------------------------------------------------------
    /**
        Sets schema location.
        @param value schema location
    **/
    //---------------------------------------------------------------------
    public void setSchemaLocation(String value)
    {                                   // begin setSchemaLocation()
        schemaLocation = value;
    }                                   // end setSchemaLocation()

    //---------------------------------------------------------------------
    /**
        Returns schema location.
        @return schema location
    **/
    //---------------------------------------------------------------------
    public String getSchemaLocation()
    {                                   // begin getSchemaLocation()
        return(schemaLocation);
    }                                   // end getSchemaLocation()

    //---------------------------------------------------------------------
    /**
       Adds attributes to root element. <P>
       @param element root element
    **/
    //---------------------------------------------------------------------
    public void addAttributes(Element element)
    {                                   // begin testCreateRetailTransactionElement()
        element.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_NAMESPACE_TAG,
                             IXRetailConstantsIfc.ATTRIBUTE_NAMESPACE_IXRETAIL);
        element.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_NAMESPACE_TAG,
                             IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_NAMESPACE);
        element.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_LOCATION_TAG,
                             IXRetailConstantsIfc.ATTRIBUTE_NAMESPACE_IXRETAIL +
                              " " + getSchemaLocation());
        /*
        element.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                             ELEMENT_POS_LOG);
        */
    }                                   // end testCreateRetailTransactionElement()


}
