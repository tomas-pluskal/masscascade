/*
 * Copyright (C) 2013 EMBL - European Bioinformatics Institute
 *
 * This file is part of MassCascade.
 *
 * MassCascade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MassCascade is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MassCascade. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   Stephan Beisken - initial API and implementation
 */

/**
 * MassBankAPIStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1
 */

package uk.ac.ebi.masscascade.ws.massbank;

        

        /*
        *  MassBankAPIStub java implementation
        */

public class MassBankAPIStub extends org.apache.axis2.client.Stub {

    protected org.apache.axis2.description.AxisOperation[] _operations;

    //hashmaps to keep the fault mapping
    private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();
    private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();
    private java.util.HashMap faultMessageMap = new java.util.HashMap();

    private static int counter = 0;

    private static synchronized String getUniqueSuffix() {
        // reset the counter if it is greater than 99999
        if (counter > 99999) {
            counter = 0;
        }
        counter = counter + 1;
        return Long.toString(System.currentTimeMillis()) + "_" + counter;
    }

    private void populateAxisService() throws org.apache.axis2.AxisFault {

        //creating the Service with a unique name
        _service = new org.apache.axis2.description.AxisService("MassBankAPI" + getUniqueSuffix());
        addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[9];

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "searchPeakDiff"));
        _service.addOperation(__operation);

        _operations[0] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "getJobStatus"));
        _service.addOperation(__operation);

        _operations[1] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "searchPeak"));
        _service.addOperation(__operation);

        _operations[2] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "getRecordInfo"));
        _service.addOperation(__operation);

        _operations[3] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "execBatchJob"));
        _service.addOperation(__operation);

        _operations[4] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "searchSpectrum"));
        _service.addOperation(__operation);

        _operations[5] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "getJobResult"));
        _service.addOperation(__operation);

        _operations[6] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "getInstrumentTypes"));
        _service.addOperation(__operation);

        _operations[7] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://api.massbank", "getFeature"));
        _service.addOperation(__operation);

        _operations[8] = __operation;
    }

    //populates the faults
    private void populateFaults() {

    }

    /**
     * Constructor that takes in a configContext
     */

    public MassBankAPIStub(org.apache.axis2.context.ConfigurationContext configurationContext,
            String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(configurationContext, targetEndpoint, false);
    }

    /**
     * Constructor that takes in a configContext  and useseperate listner
     */
    public MassBankAPIStub(org.apache.axis2.context.ConfigurationContext configurationContext, String targetEndpoint,
            boolean useSeparateListener) throws org.apache.axis2.AxisFault {
        //To populate AxisService
        populateAxisService();
        populateFaults();

        _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);

        _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
        _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);

        //Set the soap version
        _serviceClient.getOptions().setSoapVersionURI(
                org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
    }

    /**
     * Default Constructor
     */
    public MassBankAPIStub(
            org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {

        this(configurationContext, "http://www.massbank.jp:80/api/services/MassBankAPI.MassBankAPIHttpSoap12Endpoint/");
    }

    /**
     * Default Constructor
     */
    public MassBankAPIStub() throws org.apache.axis2.AxisFault {

        this("http://www.massbank.jp:80/api/services/MassBankAPI.MassBankAPIHttpSoap12Endpoint/");
    }

    /**
     * Constructor taking the target endpoint
     */
    public MassBankAPIStub(String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null, targetEndpoint);
    }

    /**
     * Auto generated method signature
     *
     * @param searchPeakDiff
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#searchPeakDiff
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiffResponse searchPeakDiff(

            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiff searchPeakDiff)

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[0].getName());
            _operationClient.getOptions().setAction("urn:searchPeakDiff");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), searchPeakDiff,
                    optimizeContent(new javax.xml.namespace.QName("http://api.massbank", "searchPeakDiff")),
                    new javax.xml.namespace.QName("http://api.massbank", "searchPeakDiff"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiffResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiffResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchPeakDiff"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchPeakDiff"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchPeakDiff"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature
     *
     * @param getJobStatus
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#getJobStatus
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatusResponse getJobStatus(

            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatus getJobStatus)

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[1].getName());
            _operationClient.getOptions().setAction("urn:getJobStatus");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getJobStatus,
                    optimizeContent(new javax.xml.namespace.QName("http://api.massbank", "getJobStatus")),
                    new javax.xml.namespace.QName("http://api.massbank", "getJobStatus"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatusResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatusResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getJobStatus"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getJobStatus"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getJobStatus"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature
     *
     * @param searchPeak
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#searchPeak
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakResponse searchPeak(

            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeak searchPeak)

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[2].getName());
            _operationClient.getOptions().setAction("urn:searchPeak");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), searchPeak,
                    optimizeContent(new javax.xml.namespace.QName("http://api.massbank", "searchPeak")),
                    new javax.xml.namespace.QName("http://api.massbank", "searchPeak"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchPeak"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchPeak"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchPeak"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature
     *
     * @param getRecordInfo
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#getRecordInfo
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfoResponse getRecordInfo(

            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfo getRecordInfo)

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[3].getName());
            _operationClient.getOptions().setAction("urn:getRecordInfo");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getRecordInfo,
                    optimizeContent(new javax.xml.namespace.QName("http://api.massbank", "getRecordInfo")),
                    new javax.xml.namespace.QName("http://api.massbank", "getRecordInfo"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfoResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfoResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getRecordInfo"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getRecordInfo"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getRecordInfo"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature
     *
     * @param execBatchJob
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#execBatchJob
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJobResponse execBatchJob(

            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJob execBatchJob)

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[4].getName());
            _operationClient.getOptions().setAction("urn:execBatchJob");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), execBatchJob,
                    optimizeContent(new javax.xml.namespace.QName("http://api.massbank", "execBatchJob")),
                    new javax.xml.namespace.QName("http://api.massbank", "execBatchJob"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJobResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJobResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "execBatchJob"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "execBatchJob"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "execBatchJob"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature
     *
     * @param searchSpectrum
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#searchSpectrum
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrumResponse searchSpectrum(

            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrum searchSpectrum)

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[5].getName());
            _operationClient.getOptions().setAction("urn:searchSpectrum");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), searchSpectrum,
                    optimizeContent(new javax.xml.namespace.QName("http://api.massbank", "searchSpectrum")),
                    new javax.xml.namespace.QName("http://api.massbank", "searchSpectrum"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrumResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrumResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchSpectrum"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchSpectrum"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "searchSpectrum"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature
     *
     * @param getJobResult
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#getJobResult
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResultResponse getJobResult(

            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResult getJobResult)

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[6].getName());
            _operationClient.getOptions().setAction("urn:getJobResult");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getJobResult,
                    optimizeContent(new javax.xml.namespace.QName("http://api.massbank", "getJobResult")),
                    new javax.xml.namespace.QName("http://api.massbank", "getJobResult"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResultResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResultResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getJobResult"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getJobResult"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getJobResult"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature
     *
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#getInstrumentTypes
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetInstrumentTypesResponse getInstrumentTypes(

    )

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[7].getName());
            _operationClient.getOptions().setAction("urn:getInstrumentTypes");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is taken to be "document". No input parameters
            // according to the WS-Basic feature in this case we have to send an empty soap message
            org.apache.axiom.soap.SOAPFactory factory = getFactory(_operationClient.getOptions().getSoapVersionURI());
            env = factory.getDefaultEnvelope();

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetInstrumentTypesResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetInstrumentTypesResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getInstrumentTypes"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getInstrumentTypes"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getInstrumentTypes"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature
     *
     * @param getPeak
     * @see uk.ac.ebi.masscascade.ws.massbank.MassBankAPI#getPeak
     */

    public uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeakResponse getPeak(

            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeak getPeak)

            throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[8].getName());
            _operationClient.getOptions().setAction("urn:getFeature");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getPeak,
                    optimizeContent(new javax.xml.namespace.QName("http://api.massbank", "getFeature")),
                    new javax.xml.namespace.QName("http://api.massbank", "getFeature"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeakResponse.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeakResponse) object;
        } catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getFeature"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getFeature"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        String messageClassName = (String) faultMessageMap.get(
                                new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "getFeature"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }

    /**
     * A utility method that copies the namepaces from the SOAPEnvelope
     */
    private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env) {
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }
        return returnMap;
    }

    private javax.xml.namespace.QName[] opNameArray = null;

    private boolean optimizeContent(javax.xml.namespace.QName opName) {

        if (opNameArray == null) {
            return false;
        }
        for (int i = 0; i < opNameArray.length; i++) {
            if (opName.equals(opNameArray[i])) {
                return true;
            }
        }
        return false;
    }

    //http://www.massbank.jp:80/api/services/MassBankAPI.MassBankAPIHttpSoap12Endpoint/
    public static class JobStatus implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = JobStatus
                Namespace URI = http://api.massbank/xsd
                Namespace Prefix = ns1
                */

        /**
         * field for RequestDate
         */

        protected String localRequestDate;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localRequestDateTracker = false;

        public boolean isRequestDateSpecified() {
            return localRequestDateTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getRequestDate() {
            return localRequestDate;
        }

        /**
         * Auto generated setter method
         *
         * @param param RequestDate
         */
        public void setRequestDate(String param) {
            localRequestDateTracker = true;

            this.localRequestDate = param;
        }

        /**
         * field for Status
         */

        protected String localStatus;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localStatusTracker = false;

        public boolean isStatusSpecified() {
            return localStatusTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getStatus() {
            return localStatus;
        }

        /**
         * Auto generated setter method
         *
         * @param param Status
         */
        public void setStatus(String param) {
            localStatusTracker = true;

            this.localStatus = param;
        }

        /**
         * field for StatusCode
         */

        protected String localStatusCode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localStatusCodeTracker = false;

        public boolean isStatusCodeSpecified() {
            return localStatusCodeTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getStatusCode() {
            return localStatusCode;
        }

        /**
         * Auto generated setter method
         *
         * @param param StatusCode
         */
        public void setStatusCode(String param) {
            localStatusCodeTracker = true;

            this.localStatusCode = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
            return factory.createOMElement(dataSource, parentQName);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank/xsd");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":JobStatus", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "JobStatus", xmlWriter);
                }
            }
            if (localRequestDateTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "requestDate", xmlWriter);

                if (localRequestDate == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localRequestDate);
                }

                xmlWriter.writeEndElement();
            }
            if (localStatusTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "status", xmlWriter);

                if (localStatus == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localStatus);
                }

                xmlWriter.writeEndElement();
            }
            if (localStatusCodeTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "statusCode", xmlWriter);

                if (localStatusCode == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localStatusCode);
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank/xsd")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localRequestDateTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "requestDate"));

                elementList.add(
                        localRequestDate == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localRequestDate));
            }
            if (localStatusTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "status"));

                elementList.add(
                        localStatus == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localStatus));
            }
            if (localStatusCodeTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "statusCode"));

                elementList.add(
                        localStatusCode == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localStatusCode));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static JobStatus parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                JobStatus object = new JobStatus();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"JobStatus".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (JobStatus) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "requestDate").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setRequestDate(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "status").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setStatus(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "statusCode").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setStatusCode(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetPeak implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getFeature", "ns2");

        /**
         * field for Ids
         * This was an Array!
         */

        protected String[] localIds;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIdsTracker = false;

        public boolean isIdsSpecified() {
            return localIdsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getIds() {
            return localIds;
        }

        /**
         * validate the array for Ids
         */
        protected void validateIds(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Ids
         */
        public void setIds(String[] param) {

            validateIds(param);

            localIdsTracker = true;

            this.localIds = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addIds(String param) {
            if (localIds == null) {
                localIds = new String[]{};
            }

            //update the setting tracker
            localIdsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localIds);
            list.add(param);
            this.localIds = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getFeature", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getFeature", xmlWriter);
                }
            }
            if (localIdsTracker) {
                if (localIds != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localIds.length; i++) {

                        if (localIds[i] != null) {

                            writeStartElement(null, namespace, "ids", xmlWriter);

                            xmlWriter.writeCharacters(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIds[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "ids", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "ids", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localIdsTracker) {
                if (localIds != null) {
                    for (int i = 0; i < localIds.length; i++) {

                        if (localIds[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ids"));
                            elementList.add(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIds[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ids"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ids"));
                    elementList.add(null);
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetPeak parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetPeak object = new GetPeak();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getFeature".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetPeak) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "ids").equals(
                            reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);

                            reader.next();
                        } else {
                            list1.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "ids").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);

                                        reader.next();
                                    } else {
                                        list1.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setIds((String[]) list1.toArray(new String[list1.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class RecordInfo implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = RecordInfo
                Namespace URI = http://api.massbank/xsd
                Namespace Prefix = ns1
                */

        /**
         * field for Id
         */

        protected String localId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIdTracker = false;

        public boolean isIdSpecified() {
            return localIdTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getId() {
            return localId;
        }

        /**
         * Auto generated setter method
         *
         * @param param Id
         */
        public void setId(String param) {
            localIdTracker = true;

            this.localId = param;
        }

        /**
         * field for Info
         */

        protected String localInfo;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localInfoTracker = false;

        public boolean isInfoSpecified() {
            return localInfoTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getInfo() {
            return localInfo;
        }

        /**
         * Auto generated setter method
         *
         * @param param Info
         */
        public void setInfo(String param) {
            localInfoTracker = true;

            this.localInfo = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
            return factory.createOMElement(dataSource, parentQName);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank/xsd");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":RecordInfo", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "RecordInfo", xmlWriter);
                }
            }
            if (localIdTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "id", xmlWriter);

                if (localId == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localId);
                }

                xmlWriter.writeEndElement();
            }
            if (localInfoTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "info", xmlWriter);

                if (localInfo == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localInfo);
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank/xsd")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localIdTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "id"));

                elementList.add(
                        localId == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localId));
            }
            if (localInfoTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "info"));

                elementList.add(
                        localInfo == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localInfo));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static RecordInfo parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                RecordInfo object = new RecordInfo();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"RecordInfo".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (RecordInfo) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "id").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "info").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setInfo(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetJobResult implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getJobResult", "ns2");

        /**
         * field for JobId
         */

        protected String localJobId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localJobIdTracker = false;

        public boolean isJobIdSpecified() {
            return localJobIdTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getJobId() {
            return localJobId;
        }

        /**
         * Auto generated setter method
         *
         * @param param JobId
         */
        public void setJobId(String param) {
            localJobIdTracker = true;

            this.localJobId = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getJobResult", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getJobResult",
                            xmlWriter);
                }
            }
            if (localJobIdTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "jobId", xmlWriter);

                if (localJobId == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localJobId);
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localJobIdTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "jobId"));

                elementList.add(
                        localJobId == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localJobId));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetJobResult parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetJobResult object = new GetJobResult();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getJobResult".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetJobResult) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "jobId").equals(
                            reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setJobId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetJobResultResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getJobResultResponse", "ns2");

        /**
         * field for _return
         * This was an Array!
         */

        protected ResultSet[] local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return ResultSet[]
         */
        public ResultSet[] get_return() {
            return local_return;
        }

        /**
         * validate the array for _return
         */
        protected void validate_return(ResultSet[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(ResultSet[] param) {

            validate_return(param);

            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param ResultSet
         */
        public void add_return(ResultSet param) {
            if (local_return == null) {
                local_return = new ResultSet[]{};
            }

            //update the setting tracker
            local_returnTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
            list.add(param);
            this.local_return = (ResultSet[]) list.toArray(new ResultSet[list.size()]);
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getJobResultResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getJobResultResponse",
                            xmlWriter);
                }
            }
            if (local_returnTracker) {
                if (local_return != null) {
                    for (int i = 0; i < local_return.length; i++) {
                        if (local_return[i] != null) {
                            local_return[i].serialize(new javax.xml.namespace.QName("http://api.massbank", "return"),
                                    xmlWriter);
                        } else {

                            writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                            // write the nil attribute
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                if (local_return != null) {
                    for (int i = 0; i < local_return.length; i++) {

                        if (local_return[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                            elementList.add(local_return[i]);
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                    elementList.add(local_return);
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetJobResultResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetJobResultResponse object = new GetJobResultResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getJobResultResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetJobResultResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);
                            reader.next();
                        } else {
                            list1.add(ResultSet.Factory.parse(reader));
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // We should be at the end element, but make sure
                            while (!reader.isEndElement()) reader.next();
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "return").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);
                                        reader.next();
                                    } else {
                                        list1.add(ResultSet.Factory.parse(reader));
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.set_return((ResultSet[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                ResultSet.class, list1));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetJobStatusResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getJobStatusResponse", "ns2");

        /**
         * field for _return
         */

        protected JobStatus local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return JobStatus
         */
        public JobStatus get_return() {
            return local_return;
        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(JobStatus param) {
            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getJobStatusResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getJobStatusResponse",
                            xmlWriter);
                }
            }
            if (local_returnTracker) {
                if (local_return == null) {

                    writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                } else {
                    local_return.serialize(new javax.xml.namespace.QName("http://api.massbank", "return"), xmlWriter);
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));

                elementList.add(local_return == null ? null : local_return);
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetJobStatusResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetJobStatusResponse object = new GetJobStatusResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getJobStatusResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetJobStatusResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            object.set_return(null);
                            reader.next();

                            reader.next();
                        } else {

                            object.set_return(JobStatus.Factory.parse(reader));

                            reader.next();
                        }
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class SearchSpectrum implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "searchSpectrum", "ns2");

        /**
         * field for Mzs
         * This was an Array!
         */

        protected String[] localMzs;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localMzsTracker = false;

        public boolean isMzsSpecified() {
            return localMzsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getMzs() {
            return localMzs;
        }

        /**
         * validate the array for Mzs
         */
        protected void validateMzs(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Mzs
         */
        public void setMzs(String[] param) {

            validateMzs(param);

            localMzsTracker = true;

            this.localMzs = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addMzs(String param) {
            if (localMzs == null) {
                localMzs = new String[]{};
            }

            //update the setting tracker
            localMzsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localMzs);
            list.add(param);
            this.localMzs = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for Intensities
         * This was an Array!
         */

        protected String[] localIntensities;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIntensitiesTracker = false;

        public boolean isIntensitiesSpecified() {
            return localIntensitiesTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getIntensities() {
            return localIntensities;
        }

        /**
         * validate the array for Intensities
         */
        protected void validateIntensities(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Intensities
         */
        public void setIntensities(String[] param) {

            validateIntensities(param);

            localIntensitiesTracker = true;

            this.localIntensities = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addIntensities(String param) {
            if (localIntensities == null) {
                localIntensities = new String[]{};
            }

            //update the setting tracker
            localIntensitiesTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localIntensities);
            list.add(param);
            this.localIntensities = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for Unit
         */

        protected String localUnit;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localUnitTracker = false;

        public boolean isUnitSpecified() {
            return localUnitTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getUnit() {
            return localUnit;
        }

        /**
         * Auto generated setter method
         *
         * @param param Unit
         */
        public void setUnit(String param) {
            localUnitTracker = true;

            this.localUnit = param;
        }

        /**
         * field for Tolerance
         */

        protected String localTolerance;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localToleranceTracker = false;

        public boolean isToleranceSpecified() {
            return localToleranceTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getTolerance() {
            return localTolerance;
        }

        /**
         * Auto generated setter method
         *
         * @param param Tolerance
         */
        public void setTolerance(String param) {
            localToleranceTracker = true;

            this.localTolerance = param;
        }

        /**
         * field for Cutoff
         */

        protected String localCutoff;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localCutoffTracker = false;

        public boolean isCutoffSpecified() {
            return localCutoffTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getCutoff() {
            return localCutoff;
        }

        /**
         * Auto generated setter method
         *
         * @param param Cutoff
         */
        public void setCutoff(String param) {
            localCutoffTracker = true;

            this.localCutoff = param;
        }

        /**
         * field for InstrumentTypes
         * This was an Array!
         */

        protected String[] localInstrumentTypes;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localInstrumentTypesTracker = false;

        public boolean isInstrumentTypesSpecified() {
            return localInstrumentTypesTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getInstrumentTypes() {
            return localInstrumentTypes;
        }

        /**
         * validate the array for InstrumentTypes
         */
        protected void validateInstrumentTypes(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param InstrumentTypes
         */
        public void setInstrumentTypes(String[] param) {

            validateInstrumentTypes(param);

            localInstrumentTypesTracker = true;

            this.localInstrumentTypes = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addInstrumentTypes(String param) {
            if (localInstrumentTypes == null) {
                localInstrumentTypes = new String[]{};
            }

            //update the setting tracker
            localInstrumentTypesTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localInstrumentTypes);
            list.add(param);
            this.localInstrumentTypes = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for IonMode
         */

        protected String localIonMode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIonModeTracker = false;

        public boolean isIonModeSpecified() {
            return localIonModeTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getIonMode() {
            return localIonMode;
        }

        /**
         * Auto generated setter method
         *
         * @param param IonMode
         */
        public void setIonMode(String param) {
            localIonModeTracker = true;

            this.localIonMode = param;
        }

        /**
         * field for MaxNumResults
         */

        protected int localMaxNumResults;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localMaxNumResultsTracker = false;

        public boolean isMaxNumResultsSpecified() {
            return localMaxNumResultsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return int
         */
        public int getMaxNumResults() {
            return localMaxNumResults;
        }

        /**
         * Auto generated setter method
         *
         * @param param MaxNumResults
         */
        public void setMaxNumResults(int param) {

            // setting primitive attribute tracker to true
            localMaxNumResultsTracker = param != Integer.MIN_VALUE;

            this.localMaxNumResults = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":searchSpectrum", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "searchSpectrum",
                            xmlWriter);
                }
            }
            if (localMzsTracker) {
                if (localMzs != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localMzs.length; i++) {

                        if (localMzs[i] != null) {

                            writeStartElement(null, namespace, "mzs", xmlWriter);

                            xmlWriter.writeCharacters(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMzs[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "mzs", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "mzs", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localIntensitiesTracker) {
                if (localIntensities != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localIntensities.length; i++) {

                        if (localIntensities[i] != null) {

                            writeStartElement(null, namespace, "intensities", xmlWriter);

                            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localIntensities[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "intensities", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "intensities", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localUnitTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "unit", xmlWriter);

                if (localUnit == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localUnit);
                }

                xmlWriter.writeEndElement();
            }
            if (localToleranceTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "tolerance", xmlWriter);

                if (localTolerance == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localTolerance);
                }

                xmlWriter.writeEndElement();
            }
            if (localCutoffTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "cutoff", xmlWriter);

                if (localCutoff == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localCutoff);
                }

                xmlWriter.writeEndElement();
            }
            if (localInstrumentTypesTracker) {
                if (localInstrumentTypes != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localInstrumentTypes.length; i++) {

                        if (localInstrumentTypes[i] != null) {

                            writeStartElement(null, namespace, "instrumentTypes", xmlWriter);

                            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localInstrumentTypes[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "instrumentTypes", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "instrumentTypes", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localIonModeTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "ionMode", xmlWriter);

                if (localIonMode == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localIonMode);
                }

                xmlWriter.writeEndElement();
            }
            if (localMaxNumResultsTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "maxNumResults", xmlWriter);

                if (localMaxNumResults == Integer.MIN_VALUE) {

                    throw new org.apache.axis2.databinding.ADBException("maxNumResults cannot be null!!");
                } else {
                    xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMaxNumResults));
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localMzsTracker) {
                if (localMzs != null) {
                    for (int i = 0; i < localMzs.length; i++) {

                        if (localMzs[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                            elementList.add(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMzs[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                    elementList.add(null);
                }
            }
            if (localIntensitiesTracker) {
                if (localIntensities != null) {
                    for (int i = 0; i < localIntensities.length; i++) {

                        if (localIntensities[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "intensities"));
                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localIntensities[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "intensities"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "intensities"));
                    elementList.add(null);
                }
            }
            if (localUnitTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "unit"));

                elementList.add(
                        localUnit == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localUnit));
            }
            if (localToleranceTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "tolerance"));

                elementList.add(
                        localTolerance == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localTolerance));
            }
            if (localCutoffTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "cutoff"));

                elementList.add(
                        localCutoff == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localCutoff));
            }
            if (localInstrumentTypesTracker) {
                if (localInstrumentTypes != null) {
                    for (int i = 0; i < localInstrumentTypes.length; i++) {

                        if (localInstrumentTypes[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localInstrumentTypes[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                    elementList.add(null);
                }
            }
            if (localIonModeTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ionMode"));

                elementList.add(
                        localIonMode == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localIonMode));
            }
            if (localMaxNumResultsTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "maxNumResults"));

                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMaxNumResults));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static SearchSpectrum parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                SearchSpectrum object = new SearchSpectrum();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"searchSpectrum".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SearchSpectrum) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    java.util.ArrayList list2 = new java.util.ArrayList();

                    java.util.ArrayList list6 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "mzs").equals(
                            reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);

                            reader.next();
                        } else {
                            list1.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "mzs").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);

                                        reader.next();
                                    } else {
                                        list1.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setMzs((String[]) list1.toArray(new String[list1.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "intensities").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list2.add(null);

                            reader.next();
                        } else {
                            list2.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone2 = false;
                        while (!loopDone2) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone2 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "intensities").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list2.add(null);

                                        reader.next();
                                    } else {
                                        list2.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone2 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setIntensities((String[]) list2.toArray(new String[list2.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "unit").equals(
                            reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setUnit(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "tolerance").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setTolerance(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "cutoff").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setCutoff(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "instrumentTypes").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list6.add(null);

                            reader.next();
                        } else {
                            list6.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone6 = false;
                        while (!loopDone6) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone6 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list6.add(null);

                                        reader.next();
                                    } else {
                                        list6.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone6 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setInstrumentTypes((String[]) list6.toArray(new String[list6.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "ionMode").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setIonMode(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "maxNumResults").equals(reader.getName())) {

                        String content = reader.getElementText();

                        object.setMaxNumResults(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                        object.setMaxNumResults(Integer.MIN_VALUE);
                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class SearchPeakDiffResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "searchPeakDiffResponse", "ns2");

        /**
         * field for _return
         */

        protected SearchResult local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return SearchResult
         */
        public SearchResult get_return() {
            return local_return;
        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(SearchResult param) {
            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":searchPeakDiffResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "searchPeakDiffResponse",
                            xmlWriter);
                }
            }
            if (local_returnTracker) {
                if (local_return == null) {

                    writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                } else {
                    local_return.serialize(new javax.xml.namespace.QName("http://api.massbank", "return"), xmlWriter);
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));

                elementList.add(local_return == null ? null : local_return);
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static SearchPeakDiffResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                SearchPeakDiffResponse object = new SearchPeakDiffResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"searchPeakDiffResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SearchPeakDiffResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            object.set_return(null);
                            reader.next();

                            reader.next();
                        } else {

                            object.set_return(SearchResult.Factory.parse(reader));

                            reader.next();
                        }
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class Result implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = Result
                Namespace URI = http://api.massbank/xsd
                Namespace Prefix = ns1
                */

        /**
         * field for ExactMass
         */

        protected String localExactMass;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localExactMassTracker = false;

        public boolean isExactMassSpecified() {
            return localExactMassTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getExactMass() {
            return localExactMass;
        }

        /**
         * Auto generated setter method
         *
         * @param param ExactMass
         */
        public void setExactMass(String param) {
            localExactMassTracker = true;

            this.localExactMass = param;
        }

        /**
         * field for Formula
         */

        protected String localFormula;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localFormulaTracker = false;

        public boolean isFormulaSpecified() {
            return localFormulaTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getFormula() {
            return localFormula;
        }

        /**
         * Auto generated setter method
         *
         * @param param Formula
         */
        public void setFormula(String param) {
            localFormulaTracker = true;

            this.localFormula = param;
        }

        /**
         * field for Id
         */

        protected String localId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIdTracker = false;

        public boolean isIdSpecified() {
            return localIdTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getId() {
            return localId;
        }

        /**
         * Auto generated setter method
         *
         * @param param Id
         */
        public void setId(String param) {
            localIdTracker = true;

            this.localId = param;
        }

//        @Override
//        public boolean equals(Object obj) {
//
//            if (obj == null) return false;
//            if (obj == this) return true;
//
//            if (!(obj instanceof Result)) return false;
//
//            Result res = (Result) obj;
//            return res.getScore().equals(this.getScore());
//        }

        /**
         * field for Score
         */

        protected String localScore;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localScoreTracker = false;

        public boolean isScoreSpecified() {
            return localScoreTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getScore() {
            return localScore;
        }

        /**
         * Auto generated setter method
         *
         * @param param Score
         */
        public void setScore(String param) {
            localScoreTracker = true;

            this.localScore = param;
        }

        /**
         * field for Title
         */

        protected String localTitle;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localTitleTracker = false;

        public boolean isTitleSpecified() {
            return localTitleTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getTitle() {
            return localTitle;
        }

        /**
         * Auto generated setter method
         *
         * @param param Title
         */
        public void setTitle(String param) {
            localTitleTracker = true;

            this.localTitle = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
            return factory.createOMElement(dataSource, parentQName);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank/xsd");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":Result", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Result", xmlWriter);
                }
            }
            if (localExactMassTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "exactMass", xmlWriter);

                if (localExactMass == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localExactMass);
                }

                xmlWriter.writeEndElement();
            }
            if (localFormulaTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "formula", xmlWriter);

                if (localFormula == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localFormula);
                }

                xmlWriter.writeEndElement();
            }
            if (localIdTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "id", xmlWriter);

                if (localId == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localId);
                }

                xmlWriter.writeEndElement();
            }
            if (localScoreTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "score", xmlWriter);

                if (localScore == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localScore);
                }

                xmlWriter.writeEndElement();
            }
            if (localTitleTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "title", xmlWriter);

                if (localTitle == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localTitle);
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank/xsd")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localExactMassTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "exactMass"));

                elementList.add(
                        localExactMass == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localExactMass));
            }
            if (localFormulaTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "formula"));

                elementList.add(
                        localFormula == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localFormula));
            }
            if (localIdTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "id"));

                elementList.add(
                        localId == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localId));
            }
            if (localScoreTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "score"));

                elementList.add(
                        localScore == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localScore));
            }
            if (localTitleTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "title"));

                elementList.add(
                        localTitle == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localTitle));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static Result parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                Result object = new Result();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"Result".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (Result) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "exactMass").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setExactMass(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "formula").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setFormula(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "id").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "score").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setScore(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "title").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setTitle(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class ExecBatchJob implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "execBatchJob", "ns2");

        /**
         * field for Type
         */

        protected String localType;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localTypeTracker = false;

        public boolean isTypeSpecified() {
            return localTypeTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getType() {
            return localType;
        }

        /**
         * Auto generated setter method
         *
         * @param param Type
         */
        public void setType(String param) {
            localTypeTracker = true;

            this.localType = param;
        }

        /**
         * field for MailAddress
         */

        protected String localMailAddress;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localMailAddressTracker = false;

        public boolean isMailAddressSpecified() {
            return localMailAddressTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getMailAddress() {
            return localMailAddress;
        }

        /**
         * Auto generated setter method
         *
         * @param param MailAddress
         */
        public void setMailAddress(String param) {
            localMailAddressTracker = true;

            this.localMailAddress = param;
        }

        /**
         * field for QueryStrings
         * This was an Array!
         */

        protected String[] localQueryStrings;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localQueryStringsTracker = false;

        public boolean isQueryStringsSpecified() {
            return localQueryStringsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getQueryStrings() {
            return localQueryStrings;
        }

        /**
         * validate the array for QueryStrings
         */
        protected void validateQueryStrings(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param QueryStrings
         */
        public void setQueryStrings(String[] param) {

            validateQueryStrings(param);

            localQueryStringsTracker = true;

            this.localQueryStrings = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addQueryStrings(String param) {
            if (localQueryStrings == null) {
                localQueryStrings = new String[]{};
            }

            //update the setting tracker
            localQueryStringsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localQueryStrings);
            list.add(param);
            this.localQueryStrings = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for InstrumentTypes
         * This was an Array!
         */

        protected String[] localInstrumentTypes;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localInstrumentTypesTracker = false;

        public boolean isInstrumentTypesSpecified() {
            return localInstrumentTypesTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getInstrumentTypes() {
            return localInstrumentTypes;
        }

        /**
         * validate the array for InstrumentTypes
         */
        protected void validateInstrumentTypes(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param InstrumentTypes
         */
        public void setInstrumentTypes(String[] param) {

            validateInstrumentTypes(param);

            localInstrumentTypesTracker = true;

            this.localInstrumentTypes = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addInstrumentTypes(String param) {
            if (localInstrumentTypes == null) {
                localInstrumentTypes = new String[]{};
            }

            //update the setting tracker
            localInstrumentTypesTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localInstrumentTypes);
            list.add(param);
            this.localInstrumentTypes = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for IonMode
         */

        protected String localIonMode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIonModeTracker = false;

        public boolean isIonModeSpecified() {
            return localIonModeTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getIonMode() {
            return localIonMode;
        }

        /**
         * Auto generated setter method
         *
         * @param param IonMode
         */
        public void setIonMode(String param) {
            localIonModeTracker = true;

            this.localIonMode = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":execBatchJob", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "execBatchJob",
                            xmlWriter);
                }
            }
            if (localTypeTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "type", xmlWriter);

                if (localType == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localType);
                }

                xmlWriter.writeEndElement();
            }
            if (localMailAddressTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "mailAddress", xmlWriter);

                if (localMailAddress == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localMailAddress);
                }

                xmlWriter.writeEndElement();
            }
            if (localQueryStringsTracker) {
                if (localQueryStrings != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localQueryStrings.length; i++) {

                        if (localQueryStrings[i] != null) {

                            writeStartElement(null, namespace, "queryStrings", xmlWriter);

                            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localQueryStrings[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "queryStrings", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "queryStrings", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localInstrumentTypesTracker) {
                if (localInstrumentTypes != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localInstrumentTypes.length; i++) {

                        if (localInstrumentTypes[i] != null) {

                            writeStartElement(null, namespace, "instrumentTypes", xmlWriter);

                            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localInstrumentTypes[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "instrumentTypes", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "instrumentTypes", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localIonModeTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "ionMode", xmlWriter);

                if (localIonMode == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localIonMode);
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localTypeTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "type"));

                elementList.add(
                        localType == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localType));
            }
            if (localMailAddressTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mailAddress"));

                elementList.add(
                        localMailAddress == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localMailAddress));
            }
            if (localQueryStringsTracker) {
                if (localQueryStrings != null) {
                    for (int i = 0; i < localQueryStrings.length; i++) {

                        if (localQueryStrings[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "queryStrings"));
                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localQueryStrings[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "queryStrings"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "queryStrings"));
                    elementList.add(null);
                }
            }
            if (localInstrumentTypesTracker) {
                if (localInstrumentTypes != null) {
                    for (int i = 0; i < localInstrumentTypes.length; i++) {

                        if (localInstrumentTypes[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localInstrumentTypes[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                    elementList.add(null);
                }
            }
            if (localIonModeTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ionMode"));

                elementList.add(
                        localIonMode == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localIonMode));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static ExecBatchJob parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                ExecBatchJob object = new ExecBatchJob();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"execBatchJob".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (ExecBatchJob) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list3 = new java.util.ArrayList();

                    java.util.ArrayList list4 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "type").equals(
                            reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "mailAddress").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setMailAddress(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "queryStrings").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list3.add(null);

                            reader.next();
                        } else {
                            list3.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone3 = false;
                        while (!loopDone3) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone3 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "queryStrings").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list3.add(null);

                                        reader.next();
                                    } else {
                                        list3.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone3 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setQueryStrings((String[]) list3.toArray(new String[list3.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "instrumentTypes").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list4.add(null);

                            reader.next();
                        } else {
                            list4.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone4 = false;
                        while (!loopDone4) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone4 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list4.add(null);

                                        reader.next();
                                    } else {
                                        list4.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone4 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setInstrumentTypes((String[]) list4.toArray(new String[list4.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "ionMode").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setIonMode(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class SearchPeakResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "searchPeakResponse", "ns2");

        /**
         * field for _return
         */

        protected SearchResult local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return SearchResult
         */
        public SearchResult get_return() {
            return local_return;
        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(SearchResult param) {
            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":searchPeakResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "searchPeakResponse",
                            xmlWriter);
                }
            }
            if (local_returnTracker) {
                if (local_return == null) {

                    writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                } else {
                    local_return.serialize(new javax.xml.namespace.QName("http://api.massbank", "return"), xmlWriter);
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));

                elementList.add(local_return == null ? null : local_return);
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static SearchPeakResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                SearchPeakResponse object = new SearchPeakResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"searchPeakResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SearchPeakResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            object.set_return(null);
                            reader.next();

                            reader.next();
                        } else {

                            object.set_return(SearchResult.Factory.parse(reader));

                            reader.next();
                        }
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetInstrumentTypesResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getInstrumentTypesResponse", "ns2");

        /**
         * field for _return
         * This was an Array!
         */

        protected String[] local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] get_return() {
            return local_return;
        }

        /**
         * validate the array for _return
         */
        protected void validate_return(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(String[] param) {

            validate_return(param);

            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void add_return(String param) {
            if (local_return == null) {
                local_return = new String[]{};
            }

            //update the setting tracker
            local_returnTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
            list.add(param);
            this.local_return = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getInstrumentTypesResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            "getInstrumentTypesResponse", xmlWriter);
                }
            }
            if (local_returnTracker) {
                if (local_return != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < local_return.length; i++) {

                        if (local_return[i] != null) {

                            writeStartElement(null, namespace, "return", xmlWriter);

                            xmlWriter.writeCharacters(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(local_return[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "return", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                if (local_return != null) {
                    for (int i = 0; i < local_return.length; i++) {

                        if (local_return[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                            elementList.add(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(local_return[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                    elementList.add(null);
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetInstrumentTypesResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetInstrumentTypesResponse object = new GetInstrumentTypesResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getInstrumentTypesResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetInstrumentTypesResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);

                            reader.next();
                        } else {
                            list1.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "return").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);

                                        reader.next();
                                    } else {
                                        list1.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.set_return((String[]) list1.toArray(new String[list1.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class SearchSpectrumResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "searchSpectrumResponse", "ns2");

        /**
         * field for _return
         */

        protected SearchResult local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return SearchResult
         */
        public SearchResult get_return() {
            return local_return;
        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(SearchResult param) {
            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":searchSpectrumResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "searchSpectrumResponse",
                            xmlWriter);
                }
            }
            if (local_returnTracker) {
                if (local_return == null) {

                    writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                } else {
                    local_return.serialize(new javax.xml.namespace.QName("http://api.massbank", "return"), xmlWriter);
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));

                elementList.add(local_return == null ? null : local_return);
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static SearchSpectrumResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                SearchSpectrumResponse object = new SearchSpectrumResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"searchSpectrumResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SearchSpectrumResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            object.set_return(null);
                            reader.next();

                            reader.next();
                        } else {

                            object.set_return(SearchResult.Factory.parse(reader));

                            reader.next();
                        }
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class Peak implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = Peak
                Namespace URI = http://api.massbank/xsd
                Namespace Prefix = ns1
                */

        /**
         * field for Id
         */

        protected String localId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIdTracker = false;

        public boolean isIdSpecified() {
            return localIdTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getId() {
            return localId;
        }

        /**
         * Auto generated setter method
         *
         * @param param Id
         */
        public void setId(String param) {
            localIdTracker = true;

            this.localId = param;
        }

        /**
         * field for Intensities
         * This was an Array!
         */

        protected String[] localIntensities;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIntensitiesTracker = false;

        public boolean isIntensitiesSpecified() {
            return localIntensitiesTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getIntensities() {
            return localIntensities;
        }

        /**
         * validate the array for Intensities
         */
        protected void validateIntensities(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Intensities
         */
        public void setIntensities(String[] param) {

            validateIntensities(param);

            localIntensitiesTracker = true;

            this.localIntensities = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addIntensities(String param) {
            if (localIntensities == null) {
                localIntensities = new String[]{};
            }

            //update the setting tracker
            localIntensitiesTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localIntensities);
            list.add(param);
            this.localIntensities = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for Mzs
         * This was an Array!
         */

        protected String[] localMzs;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localMzsTracker = false;

        public boolean isMzsSpecified() {
            return localMzsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getMzs() {
            return localMzs;
        }

        /**
         * validate the array for Mzs
         */
        protected void validateMzs(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Mzs
         */
        public void setMzs(String[] param) {

            validateMzs(param);

            localMzsTracker = true;

            this.localMzs = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addMzs(String param) {
            if (localMzs == null) {
                localMzs = new String[]{};
            }

            //update the setting tracker
            localMzsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localMzs);
            list.add(param);
            this.localMzs = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for NumPeaks
         */

        protected int localNumPeaks;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localNumPeaksTracker = false;

        public boolean isNumPeaksSpecified() {
            return localNumPeaksTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return int
         */
        public int getNumPeaks() {
            return localNumPeaks;
        }

        /**
         * Auto generated setter method
         *
         * @param param NumPeaks
         */
        public void setNumPeaks(int param) {

            // setting primitive attribute tracker to true
            localNumPeaksTracker = param != Integer.MIN_VALUE;

            this.localNumPeaks = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
            return factory.createOMElement(dataSource, parentQName);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank/xsd");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":Peak", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Peak", xmlWriter);
                }
            }
            if (localIdTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "id", xmlWriter);

                if (localId == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localId);
                }

                xmlWriter.writeEndElement();
            }
            if (localIntensitiesTracker) {
                if (localIntensities != null) {
                    namespace = "http://api.massbank/xsd";
                    for (int i = 0; i < localIntensities.length; i++) {

                        if (localIntensities[i] != null) {

                            writeStartElement(null, namespace, "intensities", xmlWriter);

                            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localIntensities[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank/xsd";
                            writeStartElement(null, namespace, "intensities", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank/xsd", "intensities", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localMzsTracker) {
                if (localMzs != null) {
                    namespace = "http://api.massbank/xsd";
                    for (int i = 0; i < localMzs.length; i++) {

                        if (localMzs[i] != null) {

                            writeStartElement(null, namespace, "mzs", xmlWriter);

                            xmlWriter.writeCharacters(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMzs[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank/xsd";
                            writeStartElement(null, namespace, "mzs", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank/xsd", "mzs", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localNumPeaksTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "numPeaks", xmlWriter);

                if (localNumPeaks == Integer.MIN_VALUE) {

                    throw new org.apache.axis2.databinding.ADBException("numPeaks cannot be null!!");
                } else {
                    xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNumPeaks));
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank/xsd")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localIdTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "id"));

                elementList.add(
                        localId == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localId));
            }
            if (localIntensitiesTracker) {
                if (localIntensities != null) {
                    for (int i = 0; i < localIntensities.length; i++) {

                        if (localIntensities[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "intensities"));
                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localIntensities[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "intensities"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "intensities"));
                    elementList.add(null);
                }
            }
            if (localMzsTracker) {
                if (localMzs != null) {
                    for (int i = 0; i < localMzs.length; i++) {

                        if (localMzs[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "mzs"));
                            elementList.add(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMzs[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "mzs"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "mzs"));
                    elementList.add(null);
                }
            }
            if (localNumPeaksTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "numPeaks"));

                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNumPeaks));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static Peak parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                Peak object = new Peak();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"Peak".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (Peak) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list2 = new java.util.ArrayList();

                    java.util.ArrayList list3 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "id").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "intensities").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list2.add(null);

                            reader.next();
                        } else {
                            list2.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone2 = false;
                        while (!loopDone2) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone2 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank/xsd", "intensities").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list2.add(null);

                                        reader.next();
                                    } else {
                                        list2.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone2 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setIntensities((String[]) list2.toArray(new String[list2.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "mzs").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list3.add(null);

                            reader.next();
                        } else {
                            list3.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone3 = false;
                        while (!loopDone3) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone3 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank/xsd", "mzs").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list3.add(null);

                                        reader.next();
                                    } else {
                                        list3.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone3 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setMzs((String[]) list3.toArray(new String[list3.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "numPeaks").equals(reader.getName())) {

                        String content = reader.getElementText();

                        object.setNumPeaks(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                        object.setNumPeaks(Integer.MIN_VALUE);
                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class ResultSet implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = ResultSet
                Namespace URI = http://api.massbank/xsd
                Namespace Prefix = ns1
                */

        /**
         * field for NumResults
         */

        protected int localNumResults;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localNumResultsTracker = false;

        public boolean isNumResultsSpecified() {
            return localNumResultsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return int
         */
        public int getNumResults() {
            return localNumResults;
        }

        /**
         * Auto generated setter method
         *
         * @param param NumResults
         */
        public void setNumResults(int param) {

            // setting primitive attribute tracker to true
            localNumResultsTracker = param != Integer.MIN_VALUE;

            this.localNumResults = param;
        }

        /**
         * field for QueryName
         */

        protected String localQueryName;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localQueryNameTracker = false;

        public boolean isQueryNameSpecified() {
            return localQueryNameTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getQueryName() {
            return localQueryName;
        }

        /**
         * Auto generated setter method
         *
         * @param param QueryName
         */
        public void setQueryName(String param) {
            localQueryNameTracker = true;

            this.localQueryName = param;
        }

        /**
         * field for Results
         * This was an Array!
         */

        protected Result[] localResults;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localResultsTracker = false;

        public boolean isResultsSpecified() {
            return localResultsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return Result[]
         */
        public Result[] getResults() {
            return localResults;
        }

        /**
         * validate the array for Results
         */
        protected void validateResults(Result[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Results
         */
        public void setResults(Result[] param) {

            validateResults(param);

            localResultsTracker = true;

            this.localResults = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param Result
         */
        public void addResults(Result param) {
            if (localResults == null) {
                localResults = new Result[]{};
            }

            //update the setting tracker
            localResultsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localResults);
            list.add(param);
            this.localResults = (Result[]) list.toArray(new Result[list.size()]);
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
            return factory.createOMElement(dataSource, parentQName);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank/xsd");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":ResultSet", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ResultSet", xmlWriter);
                }
            }
            if (localNumResultsTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "numResults", xmlWriter);

                if (localNumResults == Integer.MIN_VALUE) {

                    throw new org.apache.axis2.databinding.ADBException("numResults cannot be null!!");
                } else {
                    xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNumResults));
                }

                xmlWriter.writeEndElement();
            }
            if (localQueryNameTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "queryName", xmlWriter);

                if (localQueryName == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localQueryName);
                }

                xmlWriter.writeEndElement();
            }
            if (localResultsTracker) {
                if (localResults != null) {
                    for (int i = 0; i < localResults.length; i++) {
                        if (localResults[i] != null) {
                            localResults[i].serialize(
                                    new javax.xml.namespace.QName("http://api.massbank/xsd", "results"), xmlWriter);
                        } else {

                            writeStartElement(null, "http://api.massbank/xsd", "results", xmlWriter);

                            // write the nil attribute
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    writeStartElement(null, "http://api.massbank/xsd", "results", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank/xsd")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localNumResultsTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "numResults"));

                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNumResults));
            }
            if (localQueryNameTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "queryName"));

                elementList.add(
                        localQueryName == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localQueryName));
            }
            if (localResultsTracker) {
                if (localResults != null) {
                    for (int i = 0; i < localResults.length; i++) {

                        if (localResults[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "results"));
                            elementList.add(localResults[i]);
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "results"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "results"));
                    elementList.add(localResults);
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static ResultSet parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                ResultSet object = new ResultSet();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"ResultSet".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (ResultSet) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list3 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "numResults").equals(reader.getName())) {

                        String content = reader.getElementText();

                        object.setNumResults(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                        object.setNumResults(Integer.MIN_VALUE);
                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "queryName").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setQueryName(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "results").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list3.add(null);
                            reader.next();
                        } else {
                            list3.add(Result.Factory.parse(reader));
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone3 = false;
                        while (!loopDone3) {
                            // We should be at the end element, but make sure
                            while (!reader.isEndElement()) reader.next();
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone3 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank/xsd", "results").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list3.add(null);
                                        reader.next();
                                    } else {
                                        list3.add(Result.Factory.parse(reader));
                                    }
                                } else {
                                    loopDone3 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setResults(
                                (Result[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(Result.class,
                                        list3));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetRecordInfo implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getRecordInfo", "ns2");

        /**
         * field for Ids
         * This was an Array!
         */

        protected String[] localIds;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIdsTracker = false;

        public boolean isIdsSpecified() {
            return localIdsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getIds() {
            return localIds;
        }

        /**
         * validate the array for Ids
         */
        protected void validateIds(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Ids
         */
        public void setIds(String[] param) {

            validateIds(param);

            localIdsTracker = true;

            this.localIds = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addIds(String param) {
            if (localIds == null) {
                localIds = new String[]{};
            }

            //update the setting tracker
            localIdsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localIds);
            list.add(param);
            this.localIds = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getRecordInfo", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getRecordInfo",
                            xmlWriter);
                }
            }
            if (localIdsTracker) {
                if (localIds != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localIds.length; i++) {

                        if (localIds[i] != null) {

                            writeStartElement(null, namespace, "ids", xmlWriter);

                            xmlWriter.writeCharacters(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIds[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "ids", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "ids", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localIdsTracker) {
                if (localIds != null) {
                    for (int i = 0; i < localIds.length; i++) {

                        if (localIds[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ids"));
                            elementList.add(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIds[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ids"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ids"));
                    elementList.add(null);
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetRecordInfo parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetRecordInfo object = new GetRecordInfo();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getRecordInfo".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetRecordInfo) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "ids").equals(
                            reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);

                            reader.next();
                        } else {
                            list1.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "ids").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);

                                        reader.next();
                                    } else {
                                        list1.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setIds((String[]) list1.toArray(new String[list1.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class SearchPeak implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "searchPeak", "ns2");

        /**
         * field for Mzs
         * This was an Array!
         */

        protected String[] localMzs;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localMzsTracker = false;

        public boolean isMzsSpecified() {
            return localMzsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getMzs() {
            return localMzs;
        }

        /**
         * validate the array for Mzs
         */
        protected void validateMzs(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Mzs
         */
        public void setMzs(String[] param) {

            validateMzs(param);

            localMzsTracker = true;

            this.localMzs = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addMzs(String param) {
            if (localMzs == null) {
                localMzs = new String[]{};
            }

            //update the setting tracker
            localMzsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localMzs);
            list.add(param);
            this.localMzs = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for RelativeIntensity
         */

        protected String localRelativeIntensity;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localRelativeIntensityTracker = false;

        public boolean isRelativeIntensitySpecified() {
            return localRelativeIntensityTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getRelativeIntensity() {
            return localRelativeIntensity;
        }

        /**
         * Auto generated setter method
         *
         * @param param RelativeIntensity
         */
        public void setRelativeIntensity(String param) {
            localRelativeIntensityTracker = true;

            this.localRelativeIntensity = param;
        }

        /**
         * field for Tolerance
         */

        protected String localTolerance;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localToleranceTracker = false;

        public boolean isToleranceSpecified() {
            return localToleranceTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getTolerance() {
            return localTolerance;
        }

        /**
         * Auto generated setter method
         *
         * @param param Tolerance
         */
        public void setTolerance(String param) {
            localToleranceTracker = true;

            this.localTolerance = param;
        }

        /**
         * field for InstrumentTypes
         * This was an Array!
         */

        protected String[] localInstrumentTypes;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localInstrumentTypesTracker = false;

        public boolean isInstrumentTypesSpecified() {
            return localInstrumentTypesTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getInstrumentTypes() {
            return localInstrumentTypes;
        }

        /**
         * validate the array for InstrumentTypes
         */
        protected void validateInstrumentTypes(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param InstrumentTypes
         */
        public void setInstrumentTypes(String[] param) {

            validateInstrumentTypes(param);

            localInstrumentTypesTracker = true;

            this.localInstrumentTypes = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addInstrumentTypes(String param) {
            if (localInstrumentTypes == null) {
                localInstrumentTypes = new String[]{};
            }

            //update the setting tracker
            localInstrumentTypesTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localInstrumentTypes);
            list.add(param);
            this.localInstrumentTypes = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for IonMode
         */

        protected String localIonMode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIonModeTracker = false;

        public boolean isIonModeSpecified() {
            return localIonModeTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getIonMode() {
            return localIonMode;
        }

        /**
         * Auto generated setter method
         *
         * @param param IonMode
         */
        public void setIonMode(String param) {
            localIonModeTracker = true;

            this.localIonMode = param;
        }

        /**
         * field for MaxNumResults
         */

        protected int localMaxNumResults;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localMaxNumResultsTracker = false;

        public boolean isMaxNumResultsSpecified() {
            return localMaxNumResultsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return int
         */
        public int getMaxNumResults() {
            return localMaxNumResults;
        }

        /**
         * Auto generated setter method
         *
         * @param param MaxNumResults
         */
        public void setMaxNumResults(int param) {

            // setting primitive attribute tracker to true
            localMaxNumResultsTracker = param != Integer.MIN_VALUE;

            this.localMaxNumResults = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":searchPeak", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "searchPeak", xmlWriter);
                }
            }
            if (localMzsTracker) {
                if (localMzs != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localMzs.length; i++) {

                        if (localMzs[i] != null) {

                            writeStartElement(null, namespace, "mzs", xmlWriter);

                            xmlWriter.writeCharacters(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMzs[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "mzs", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "mzs", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localRelativeIntensityTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "relativeIntensity", xmlWriter);

                if (localRelativeIntensity == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localRelativeIntensity);
                }

                xmlWriter.writeEndElement();
            }
            if (localToleranceTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "tolerance", xmlWriter);

                if (localTolerance == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localTolerance);
                }

                xmlWriter.writeEndElement();
            }
            if (localInstrumentTypesTracker) {
                if (localInstrumentTypes != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localInstrumentTypes.length; i++) {

                        if (localInstrumentTypes[i] != null) {

                            writeStartElement(null, namespace, "instrumentTypes", xmlWriter);

                            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localInstrumentTypes[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "instrumentTypes", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "instrumentTypes", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localIonModeTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "ionMode", xmlWriter);

                if (localIonMode == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localIonMode);
                }

                xmlWriter.writeEndElement();
            }
            if (localMaxNumResultsTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "maxNumResults", xmlWriter);

                if (localMaxNumResults == Integer.MIN_VALUE) {

                    throw new org.apache.axis2.databinding.ADBException("maxNumResults cannot be null!!");
                } else {
                    xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMaxNumResults));
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localMzsTracker) {
                if (localMzs != null) {
                    for (int i = 0; i < localMzs.length; i++) {

                        if (localMzs[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                            elementList.add(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMzs[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                    elementList.add(null);
                }
            }
            if (localRelativeIntensityTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "relativeIntensity"));

                elementList.add(
                        localRelativeIntensity == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localRelativeIntensity));
            }
            if (localToleranceTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "tolerance"));

                elementList.add(
                        localTolerance == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localTolerance));
            }
            if (localInstrumentTypesTracker) {
                if (localInstrumentTypes != null) {
                    for (int i = 0; i < localInstrumentTypes.length; i++) {

                        if (localInstrumentTypes[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localInstrumentTypes[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                    elementList.add(null);
                }
            }
            if (localIonModeTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ionMode"));

                elementList.add(
                        localIonMode == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localIonMode));
            }
            if (localMaxNumResultsTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "maxNumResults"));

                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMaxNumResults));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static SearchPeak parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                SearchPeak object = new SearchPeak();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"searchPeak".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SearchPeak) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    java.util.ArrayList list4 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "mzs").equals(
                            reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);

                            reader.next();
                        } else {
                            list1.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "mzs").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);

                                        reader.next();
                                    } else {
                                        list1.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setMzs((String[]) list1.toArray(new String[list1.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "relativeIntensity").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setRelativeIntensity(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "tolerance").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setTolerance(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "instrumentTypes").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list4.add(null);

                            reader.next();
                        } else {
                            list4.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone4 = false;
                        while (!loopDone4) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone4 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list4.add(null);

                                        reader.next();
                                    } else {
                                        list4.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone4 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setInstrumentTypes((String[]) list4.toArray(new String[list4.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "ionMode").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setIonMode(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "maxNumResults").equals(reader.getName())) {

                        String content = reader.getElementText();

                        object.setMaxNumResults(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                        object.setMaxNumResults(Integer.MIN_VALUE);
                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetRecordInfoResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getRecordInfoResponse", "ns2");

        /**
         * field for _return
         * This was an Array!
         */

        protected RecordInfo[] local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return RecordInfo[]
         */
        public RecordInfo[] get_return() {
            return local_return;
        }

        /**
         * validate the array for _return
         */
        protected void validate_return(RecordInfo[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(RecordInfo[] param) {

            validate_return(param);

            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param RecordInfo
         */
        public void add_return(RecordInfo param) {
            if (local_return == null) {
                local_return = new RecordInfo[]{};
            }

            //update the setting tracker
            local_returnTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
            list.add(param);
            this.local_return = (RecordInfo[]) list.toArray(new RecordInfo[list.size()]);
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getRecordInfoResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getRecordInfoResponse",
                            xmlWriter);
                }
            }
            if (local_returnTracker) {
                if (local_return != null) {
                    for (int i = 0; i < local_return.length; i++) {
                        if (local_return[i] != null) {
                            local_return[i].serialize(new javax.xml.namespace.QName("http://api.massbank", "return"),
                                    xmlWriter);
                        } else {

                            writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                            // write the nil attribute
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                if (local_return != null) {
                    for (int i = 0; i < local_return.length; i++) {

                        if (local_return[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                            elementList.add(local_return[i]);
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                    elementList.add(local_return);
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetRecordInfoResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetRecordInfoResponse object = new GetRecordInfoResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getRecordInfoResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetRecordInfoResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);
                            reader.next();
                        } else {
                            list1.add(RecordInfo.Factory.parse(reader));
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // We should be at the end element, but make sure
                            while (!reader.isEndElement()) reader.next();
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "return").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);
                                        reader.next();
                                    } else {
                                        list1.add(RecordInfo.Factory.parse(reader));
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.set_return(
                                (RecordInfo[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                        RecordInfo.class, list1));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class ExtensionMapper {

        public static Object getTypeObject(String namespaceURI, String typeName,
                javax.xml.stream.XMLStreamReader reader) throws Exception {

            if ("http://api.massbank/xsd".equals(namespaceURI) && "JobStatus".equals(typeName)) {

                return JobStatus.Factory.parse(reader);
            }

            if ("http://api.massbank/xsd".equals(namespaceURI) && "RecordInfo".equals(typeName)) {

                return RecordInfo.Factory.parse(reader);
            }

            if ("http://api.massbank/xsd".equals(namespaceURI) && "Result".equals(typeName)) {

                return Result.Factory.parse(reader);
            }

            if ("http://api.massbank/xsd".equals(namespaceURI) && "SearchResult".equals(typeName)) {

                return SearchResult.Factory.parse(reader);
            }

            if ("http://api.massbank/xsd".equals(namespaceURI) && "Peak".equals(typeName)) {

                return Peak.Factory.parse(reader);
            }

            if ("http://api.massbank/xsd".equals(namespaceURI) && "ResultSet".equals(typeName)) {

                return ResultSet.Factory.parse(reader);
            }

            throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
        }
    }

    public static class SearchPeakDiff implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "searchPeakDiff", "ns2");

        /**
         * field for Mzs
         * This was an Array!
         */

        protected String[] localMzs;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localMzsTracker = false;

        public boolean isMzsSpecified() {
            return localMzsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getMzs() {
            return localMzs;
        }

        /**
         * validate the array for Mzs
         */
        protected void validateMzs(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Mzs
         */
        public void setMzs(String[] param) {

            validateMzs(param);

            localMzsTracker = true;

            this.localMzs = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addMzs(String param) {
            if (localMzs == null) {
                localMzs = new String[]{};
            }

            //update the setting tracker
            localMzsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localMzs);
            list.add(param);
            this.localMzs = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for RelativeIntensity
         */

        protected String localRelativeIntensity;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localRelativeIntensityTracker = false;

        public boolean isRelativeIntensitySpecified() {
            return localRelativeIntensityTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getRelativeIntensity() {
            return localRelativeIntensity;
        }

        /**
         * Auto generated setter method
         *
         * @param param RelativeIntensity
         */
        public void setRelativeIntensity(String param) {
            localRelativeIntensityTracker = true;

            this.localRelativeIntensity = param;
        }

        /**
         * field for Tolerance
         */

        protected String localTolerance;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localToleranceTracker = false;

        public boolean isToleranceSpecified() {
            return localToleranceTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getTolerance() {
            return localTolerance;
        }

        /**
         * Auto generated setter method
         *
         * @param param Tolerance
         */
        public void setTolerance(String param) {
            localToleranceTracker = true;

            this.localTolerance = param;
        }

        /**
         * field for InstrumentTypes
         * This was an Array!
         */

        protected String[] localInstrumentTypes;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localInstrumentTypesTracker = false;

        public boolean isInstrumentTypesSpecified() {
            return localInstrumentTypesTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String[]
         */
        public String[] getInstrumentTypes() {
            return localInstrumentTypes;
        }

        /**
         * validate the array for InstrumentTypes
         */
        protected void validateInstrumentTypes(String[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param InstrumentTypes
         */
        public void setInstrumentTypes(String[] param) {

            validateInstrumentTypes(param);

            localInstrumentTypesTracker = true;

            this.localInstrumentTypes = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param java.lang.String
         */
        public void addInstrumentTypes(String param) {
            if (localInstrumentTypes == null) {
                localInstrumentTypes = new String[]{};
            }

            //update the setting tracker
            localInstrumentTypesTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localInstrumentTypes);
            list.add(param);
            this.localInstrumentTypes = (String[]) list.toArray(new String[list.size()]);
        }

        /**
         * field for IonMode
         */

        protected String localIonMode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localIonModeTracker = false;

        public boolean isIonModeSpecified() {
            return localIonModeTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getIonMode() {
            return localIonMode;
        }

        /**
         * Auto generated setter method
         *
         * @param param IonMode
         */
        public void setIonMode(String param) {
            localIonModeTracker = true;

            this.localIonMode = param;
        }

        /**
         * field for MaxNumResults
         */

        protected int localMaxNumResults;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localMaxNumResultsTracker = false;

        public boolean isMaxNumResultsSpecified() {
            return localMaxNumResultsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return int
         */
        public int getMaxNumResults() {
            return localMaxNumResults;
        }

        /**
         * Auto generated setter method
         *
         * @param param MaxNumResults
         */
        public void setMaxNumResults(int param) {

            // setting primitive attribute tracker to true
            localMaxNumResultsTracker = param != Integer.MIN_VALUE;

            this.localMaxNumResults = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":searchPeakDiff", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "searchPeakDiff",
                            xmlWriter);
                }
            }
            if (localMzsTracker) {
                if (localMzs != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localMzs.length; i++) {

                        if (localMzs[i] != null) {

                            writeStartElement(null, namespace, "mzs", xmlWriter);

                            xmlWriter.writeCharacters(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMzs[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "mzs", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "mzs", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localRelativeIntensityTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "relativeIntensity", xmlWriter);

                if (localRelativeIntensity == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localRelativeIntensity);
                }

                xmlWriter.writeEndElement();
            }
            if (localToleranceTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "tolerance", xmlWriter);

                if (localTolerance == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localTolerance);
                }

                xmlWriter.writeEndElement();
            }
            if (localInstrumentTypesTracker) {
                if (localInstrumentTypes != null) {
                    namespace = "http://api.massbank";
                    for (int i = 0; i < localInstrumentTypes.length; i++) {

                        if (localInstrumentTypes[i] != null) {

                            writeStartElement(null, namespace, "instrumentTypes", xmlWriter);

                            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localInstrumentTypes[i]));

                            xmlWriter.writeEndElement();
                        } else {

                            // write null attribute
                            namespace = "http://api.massbank";
                            writeStartElement(null, namespace, "instrumentTypes", xmlWriter);
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    // write the null attribute
                    // write null attribute
                    writeStartElement(null, "http://api.massbank", "instrumentTypes", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            if (localIonModeTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "ionMode", xmlWriter);

                if (localIonMode == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localIonMode);
                }

                xmlWriter.writeEndElement();
            }
            if (localMaxNumResultsTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "maxNumResults", xmlWriter);

                if (localMaxNumResults == Integer.MIN_VALUE) {

                    throw new org.apache.axis2.databinding.ADBException("maxNumResults cannot be null!!");
                } else {
                    xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMaxNumResults));
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localMzsTracker) {
                if (localMzs != null) {
                    for (int i = 0; i < localMzs.length; i++) {

                        if (localMzs[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                            elementList.add(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMzs[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "mzs"));
                    elementList.add(null);
                }
            }
            if (localRelativeIntensityTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "relativeIntensity"));

                elementList.add(
                        localRelativeIntensity == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localRelativeIntensity));
            }
            if (localToleranceTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "tolerance"));

                elementList.add(
                        localTolerance == null ? null : org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(localTolerance));
            }
            if (localInstrumentTypesTracker) {
                if (localInstrumentTypes != null) {
                    for (int i = 0; i < localInstrumentTypes.length; i++) {

                        if (localInstrumentTypes[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    localInstrumentTypes[i]));
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes"));
                    elementList.add(null);
                }
            }
            if (localIonModeTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "ionMode"));

                elementList.add(
                        localIonMode == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localIonMode));
            }
            if (localMaxNumResultsTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "maxNumResults"));

                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMaxNumResults));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static SearchPeakDiff parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                SearchPeakDiff object = new SearchPeakDiff();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"searchPeakDiff".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SearchPeakDiff) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    java.util.ArrayList list4 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "mzs").equals(
                            reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);

                            reader.next();
                        } else {
                            list1.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "mzs").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);

                                        reader.next();
                                    } else {
                                        list1.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setMzs((String[]) list1.toArray(new String[list1.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "relativeIntensity").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setRelativeIntensity(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "tolerance").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setTolerance(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "instrumentTypes").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list4.add(null);

                            reader.next();
                        } else {
                            list4.add(reader.getElementText());
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone4 = false;
                        while (!loopDone4) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone4 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "instrumentTypes").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list4.add(null);

                                        reader.next();
                                    } else {
                                        list4.add(reader.getElementText());
                                    }
                                } else {
                                    loopDone4 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setInstrumentTypes((String[]) list4.toArray(new String[list4.size()]));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "ionMode").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setIonMode(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "maxNumResults").equals(reader.getName())) {

                        String content = reader.getElementText();

                        object.setMaxNumResults(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                        object.setMaxNumResults(Integer.MIN_VALUE);
                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class ExecBatchJobResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "execBatchJobResponse", "ns2");

        /**
         * field for _return
         */

        protected String local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String get_return() {
            return local_return;
        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(String param) {
            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":execBatchJobResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "execBatchJobResponse",
                            xmlWriter);
                }
            }
            if (local_returnTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "return", xmlWriter);

                if (local_return == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(local_return);
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));

                elementList.add(
                        local_return == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                local_return));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static ExecBatchJobResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                ExecBatchJobResponse object = new ExecBatchJobResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"execBatchJobResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (ExecBatchJobResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.set_return(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class SearchResult implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = SearchResult
                Namespace URI = http://api.massbank/xsd
                Namespace Prefix = ns1
                */

        /**
         * field for NumResults
         */

        protected int localNumResults;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localNumResultsTracker = false;

        public boolean isNumResultsSpecified() {
            return localNumResultsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return int
         */
        public int getNumResults() {
            return localNumResults;
        }

        /**
         * Auto generated setter method
         *
         * @param param NumResults
         */
        public void setNumResults(int param) {

            // setting primitive attribute tracker to true
            localNumResultsTracker = param != Integer.MIN_VALUE;

            this.localNumResults = param;
        }

        /**
         * field for Results
         * This was an Array!
         */

        protected Result[] localResults;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localResultsTracker = false;

        public boolean isResultsSpecified() {
            return localResultsTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return Result[]
         */
        public Result[] getResults() {
            return localResults;
        }

        /**
         * validate the array for Results
         */
        protected void validateResults(Result[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param Results
         */
        public void setResults(Result[] param) {

            validateResults(param);

            localResultsTracker = true;

            this.localResults = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param Result
         */
        public void addResults(Result param) {
            if (localResults == null) {
                localResults = new Result[]{};
            }

            //update the setting tracker
            localResultsTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localResults);
            list.add(param);
            this.localResults = (Result[]) list.toArray(new Result[list.size()]);
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
            return factory.createOMElement(dataSource, parentQName);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank/xsd");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":SearchResult", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "SearchResult",
                            xmlWriter);
                }
            }
            if (localNumResultsTracker) {
                namespace = "http://api.massbank/xsd";
                writeStartElement(null, namespace, "numResults", xmlWriter);

                if (localNumResults == Integer.MIN_VALUE) {

                    throw new org.apache.axis2.databinding.ADBException("numResults cannot be null!!");
                } else {
                    xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNumResults));
                }

                xmlWriter.writeEndElement();
            }
            if (localResultsTracker) {
                if (localResults != null) {
                    for (int i = 0; i < localResults.length; i++) {
                        if (localResults[i] != null) {
                            localResults[i].serialize(
                                    new javax.xml.namespace.QName("http://api.massbank/xsd", "results"), xmlWriter);
                        } else {

                            writeStartElement(null, "http://api.massbank/xsd", "results", xmlWriter);

                            // write the nil attribute
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    writeStartElement(null, "http://api.massbank/xsd", "results", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank/xsd")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localNumResultsTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "numResults"));

                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNumResults));
            }
            if (localResultsTracker) {
                if (localResults != null) {
                    for (int i = 0; i < localResults.length; i++) {

                        if (localResults[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "results"));
                            elementList.add(localResults[i]);
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "results"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank/xsd", "results"));
                    elementList.add(localResults);
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static SearchResult parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                SearchResult object = new SearchResult();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"SearchResult".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SearchResult) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list2 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "numResults").equals(reader.getName())) {

                        String content = reader.getElementText();

                        object.setNumResults(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                        object.setNumResults(Integer.MIN_VALUE);
                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank/xsd",
                            "results").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list2.add(null);
                            reader.next();
                        } else {
                            list2.add(Result.Factory.parse(reader));
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone2 = false;
                        while (!loopDone2) {
                            // We should be at the end element, but make sure
                            while (!reader.isEndElement()) reader.next();
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone2 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank/xsd", "results").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list2.add(null);
                                        reader.next();
                                    } else {
                                        list2.add(Result.Factory.parse(reader));
                                    }
                                } else {
                                    loopDone2 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setResults(
                                (Result[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(Result.class,
                                        list2));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetJobStatus implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getJobStatus", "ns2");

        /**
         * field for JobId
         */

        protected String localJobId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean localJobIdTracker = false;

        public boolean isJobIdSpecified() {
            return localJobIdTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getJobId() {
            return localJobId;
        }

        /**
         * Auto generated setter method
         *
         * @param param JobId
         */
        public void setJobId(String param) {
            localJobIdTracker = true;

            this.localJobId = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getJobStatus", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getJobStatus",
                            xmlWriter);
                }
            }
            if (localJobIdTracker) {
                namespace = "http://api.massbank";
                writeStartElement(null, namespace, "jobId", xmlWriter);

                if (localJobId == null) {
                    // write the nil attribute

                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                } else {

                    xmlWriter.writeCharacters(localJobId);
                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localJobIdTracker) {
                elementList.add(new javax.xml.namespace.QName("http://api.massbank", "jobId"));

                elementList.add(
                        localJobId == null ? null : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localJobId));
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetJobStatus parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetJobStatus object = new GetJobStatus();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getJobStatus".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetJobStatus) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank", "jobId").equals(
                            reader.getName())) {

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

                            String content = reader.getElementText();

                            object.setJobId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                        } else {

                            reader.getElementText(); // throw away text nodes if any.
                        }

                        reader.next();
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    public static class GetPeakResponse implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME =
                new javax.xml.namespace.QName("http://api.massbank", "getPeakResponse", "ns2");

        /**
         * field for _return
         * This was an Array!
         */

        protected Peak[] local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
        protected boolean local_returnTracker = false;

        public boolean is_returnSpecified() {
            return local_returnTracker;
        }

        /**
         * Auto generated getter method
         *
         * @return Peak[]
         */
        public Peak[] get_return() {
            return local_return;
        }

        /**
         * validate the array for _return
         */
        protected void validate_return(Peak[] param) {

        }

        /**
         * Auto generated setter method
         *
         * @param param _return
         */
        public void set_return(Peak[] param) {

            validate_return(param);

            local_returnTracker = true;

            this.local_return = param;
        }

        /**
         * Auto generated add method for the array for convenience
         *
         * @param param Peak
         */
        public void add_return(Peak param) {
            if (local_return == null) {
                local_return = new Peak[]{};
            }

            //update the setting tracker
            local_returnTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
            list.add(param);
            this.local_return = (Peak[]) list.toArray(new Peak[list.size()]);
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource =
                    new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
            return factory.createOMElement(dataSource, MY_QNAME);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
                boolean serializeType) throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {

            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

            if (serializeType) {

                String namespacePrefix = registerPrefix(xmlWriter, "http://api.massbank");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":getPeakResponse", xmlWriter);
                } else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getPeakResponse",
                            xmlWriter);
                }
            }
            if (local_returnTracker) {
                if (local_return != null) {
                    for (int i = 0; i < local_return.length; i++) {
                        if (local_return[i] != null) {
                            local_return[i].serialize(new javax.xml.namespace.QName("http://api.massbank", "return"),
                                    xmlWriter);
                        } else {

                            writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                            // write the nil attribute
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                } else {

                    writeStartElement(null, "http://api.massbank", "return", xmlWriter);

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(String namespace) {
            if (namespace.equals("http://api.massbank")) {
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix, String namespace, String localPart,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix, String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace, String attName, String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace, String attName, javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(
                            prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":").append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(
                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace) throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }

        /**
         * databinding method to get an XML representation of this object
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                if (local_return != null) {
                    for (int i = 0; i < local_return.length; i++) {

                        if (local_return[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                            elementList.add(local_return[i]);
                        } else {

                            elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                            elementList.add(null);
                        }
                    }
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://api.massbank", "return"));
                    elementList.add(local_return);
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and
             * any
             * intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the
             * outer
             * start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GetPeakResponse parse(javax.xml.stream.XMLStreamReader reader) throws Exception {
                GetPeakResponse object = new GetPeakResponse();

                int event;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        String fullTypeName =
                                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"getPeakResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetPeakResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://api.massbank",
                            "return").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list1.add(null);
                            reader.next();
                        } else {
                            list1.add(Peak.Factory.parse(reader));
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // We should be at the end element, but make sure
                            while (!reader.isEndElement()) reader.next();
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("http://api.massbank", "return").equals(
                                        reader.getName())) {

                                    nillableValue =
                                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                                    "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list1.add(null);
                                        reader.next();
                                    } else {
                                        list1.add(Peak.Factory.parse(reader));
                                    }
                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.set_return(
                                (Peak[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(Peak.class,
                                        list1));
                    }  // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        }//end of factory class
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiff param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiff.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiffResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiffResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatus param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatus.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatusResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatusResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeak param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeak.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfo param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfo.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfoResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfoResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJob param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJob.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJobResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJobResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrum param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrum.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrumResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrumResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResult param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResult.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResultResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResultResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetInstrumentTypesResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetInstrumentTypesResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeak param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeak.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeakResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeakResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiff param, boolean optimizeContent,
            javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiff.MY_QNAME,
                            factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }
                                
                             
                             /* methods to provide back word compatibility */

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatus param, boolean optimizeContent,
            javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatus.MY_QNAME,
                            factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }
                                
                             
                             /* methods to provide back word compatibility */

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeak param, boolean optimizeContent,
            javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeak.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }
                                
                             
                             /* methods to provide back word compatibility */

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfo param, boolean optimizeContent,
            javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfo.MY_QNAME,
                            factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }
                                
                             
                             /* methods to provide back word compatibility */

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJob param, boolean optimizeContent,
            javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJob.MY_QNAME,
                            factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }
                                
                             
                             /* methods to provide back word compatibility */

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrum param, boolean optimizeContent,
            javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrum.MY_QNAME,
                            factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }
                                
                             
                             /* methods to provide back word compatibility */

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResult param, boolean optimizeContent,
            javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResult.MY_QNAME,
                            factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }
                                
                             
                             /* methods to provide back word compatibility */

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeak param, boolean optimizeContent,
            javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeak.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }
                                
                             
                             /* methods to provide back word compatibility */

    /**
     * get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private Object fromOM(org.apache.axiom.om.OMElement param, Class type,
            java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {

        try {

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiff.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiff.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiffResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakDiffResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatus.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatus.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatusResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobStatusResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeak.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeak.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchPeakResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfo.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfo.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfoResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetRecordInfoResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJob.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJob.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJobResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.ExecBatchJobResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrum.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrum.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrumResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.SearchSpectrumResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResult.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResult.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResultResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetJobResultResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetInstrumentTypesResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetInstrumentTypesResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeak.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeak.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }

            if (uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeakResponse.class.equals(type)) {

                return uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub.GetPeakResponse.Factory.parse(
                        param.getXMLStreamReaderWithoutCaching());
            }
        } catch (Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
        return null;
    }
}
   