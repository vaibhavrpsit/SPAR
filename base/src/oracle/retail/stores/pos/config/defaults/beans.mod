<!--NOTES:
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.
  $Log: beans.mod,v $
  Revision 1.2  2004/02/12 16:48:32  mcs
  Forcing head revision

  Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
  updating to pvcs 360store-current


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
BEANS.MOD

This file contains a DTD module for specifying EYS POS beans in the UI Framework.
-->
<!ENTITY % enabled "enabled ( true | false ) 'true'">

<!ELEMENT BEANS ( COMMENT?, ( BEAN )*)>
<!ELEMENT BEAN ( COMMENT?, BEANPROPERTY*, BUTTON*, FIELD* )>
<!ATTLIST BEAN %name;>
<!ATTLIST BEAN beanPackage CDATA #REQUIRED >
<!ATTLIST BEAN beanClassName CDATA #REQUIRED>
<!ATTLIST BEAN configuratorPackage CDATA #IMPLIED>
<!ATTLIST BEAN configuratorClassName CDATA #IMPLIED>
<!ATTLIST BEAN cachingScheme (NONE | ONE | ALL) #IMPLIED>

<!ELEMENT BUTTON EMPTY >
<!ATTLIST BUTTON actionName CDATA #REQUIRED>
<!ATTLIST BUTTON keyName CDATA #REQUIRED>
<!ATTLIST BUTTON label CDATA #IMPLIED>
<!ATTLIST BUTTON labelTag CDATA #IMPLIED>
<!ATTLIST BUTTON actionListenerName CDATA #IMPLIED>
<!ATTLIST BUTTON %enabled;>

<!ELEMENT FIELD EMPTY >
<!ATTLIST FIELD fieldName CDATA #REQUIRED>
<!ATTLIST FIELD fieldType CDATA #IMPLIED>
<!ATTLIST FIELD className CDATA #IMPLIED>
<!ATTLIST FIELD labelTag CDATA #IMPLIED>
<!ATTLIST FIELD labelText CDATA #IMPLIED>
<!ATTLIST FIELD paramList CDATA #IMPLIED>
<!ATTLIST FIELD %enabled;>
<!ATTLIST FIELD required ( true | false ) 'false'>