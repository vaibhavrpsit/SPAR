/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/IXRetailGateway.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
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
 *    4    360Commerce 1.3         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:43 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:30:56  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/04/09 16:55:48  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:44  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:57  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:37  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:29  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 10:33:18   mpm
 * Preliminary merge of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:38   msg
 * Initial revision.
 * 
 *    Rev 1.2   Apr 11 2002 09:19:00   mpm
 * Migrated oracle/retail/stores/domain/translation/ixretail to oracle/retail/stores/domain/ixretail
 * Resolution for Domain SCR-45: TLog facility
 * 
 *    Rev 1.1   Apr 11 2002 08:56:54   mpm
 * Migrated oracle/retail/stores/domain/translation/ixretail to oracle/retail/stores/domain/ixretail.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 08 2002 17:20:54   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail;
// java imports
import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.ixretail.factory.IXRetailFactory;
import oracle.retail.stores.domain.ixretail.factory.IXRetailFactoryIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
    The IXGateway class is a singleton class that provides a common
    access point for IXRetail translation.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class IXRetailGateway implements Serializable
{                                       // begin class IXRetailGateway
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6534437340183491268L;

    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.ixretail.IXRetailGateway.class);
    /**
        IXRetail factory object
    **/
    protected static IXRetailFactoryIfc ixRetailFactory = null;
    /**
        domain property group name
    **/
    public static final String DOMAIN_PROPERTY_GROUP_NAME = "domain.properties";
    /**
        IXRetail factory property name
    **/
    public static final String IXRETAIL_FACTORY_PROPERTY_NAME = "IXRetailFactory";
    /**
        IXRetail factory default class name
    **/
    public static final String IXRETAIL_FACTORY_DEFAULT_CLASS_NAME =
      "oracle.retail.stores.domain.ixretail.factory.IXRetailFactory";

    //---------------------------------------------------------------------
    /**
       Constructs IXRetailGateway object.  The constructor is protected
       to ensure that the IXRetailGateway is created only through the
       singleton pattern. <P>
    **/
    //---------------------------------------------------------------------
    protected IXRetailGateway()
    {                                   // begin IXRetailGateway()
    }                                   // end IXRetailGateway()

    //---------------------------------------------------------------------
    /**
        Returns reference to IXRetail factory. <P>
        The reference for the IXRetail factory is only built once.
        The gateway attempts to retrieve the object factory
        from Gateway.  If this fails, the default is used. <P>
        @return reference to IXRetail factory
    **/
    //---------------------------------------------------------------------
    public static IXRetailFactoryIfc getFactory()
    {                                   // begin getFactory()
        if (ixRetailFactory == null)
        {
            // get class name from DomainGateway
            String className = DomainGateway.
              getProperty(IXRETAIL_FACTORY_PROPERTY_NAME,
                          IXRETAIL_FACTORY_DEFAULT_CLASS_NAME);
            if (logger.isInfoEnabled()) logger.info(
                        "IXRetail factory class name retrieved by Gateway: " + className + "");
            // instantiate class by name
            try
            {
                Class ixRetailFactoryClass = Class.forName(className);
                ixRetailFactory =
                  (IXRetailFactoryIfc) ixRetailFactoryClass.newInstance();
            }
            // if class cannot be instantiated, the default is used
            catch (Exception e)
            {
                // issue message
                logger.error
                  ("Requested IXRetail factory cannot be instantiated; "+
                   e.toString()+
                   "Default IXRetail factory will be used.");
                ixRetailFactory =
                  (IXRetailFactoryIfc) new IXRetailFactory();
            }
        }
        return(ixRetailFactory);

    }                                   // end getFactory()


    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuilder strResult =
          Util.classToStringHeader("IXRetailGateway",
                                    revisionNumber,
                                    hashCode());
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

}                                       // end class IXRetailGateway
