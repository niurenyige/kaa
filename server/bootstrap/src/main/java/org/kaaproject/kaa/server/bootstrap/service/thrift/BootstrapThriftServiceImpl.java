/*
 * Copyright 2014 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.server.bootstrap.service.thrift;

import java.util.List;

import org.apache.thrift.TException;
import org.kaaproject.kaa.server.bootstrap.service.http.BootstrapConfig;
import org.kaaproject.kaa.server.common.thrift.cli.server.BaseCliThriftService;
import org.kaaproject.kaa.server.common.thrift.gen.bootstrap.BootstrapThriftService;
import org.kaaproject.kaa.server.common.thrift.gen.bootstrap.ThriftCommunicationParameters;
import org.kaaproject.kaa.server.common.thrift.gen.bootstrap.ThriftOperationsServer;
import org.kaaproject.kaa.server.common.thrift.gen.bootstrap.ThriftSupportedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * BootstrapThriftServiceImpl Class.
 * CLI command implementation.
 * Operations Servers List update method
 *
 * @author Andrey Panasenko
 */
@Service
public class BootstrapThriftServiceImpl extends BaseCliThriftService implements
    BootstrapThriftService.Iface {

    private static final Logger LOG = LoggerFactory.getLogger(BootstrapThriftServiceImpl.class);
    private BootstrapConfig config;

    /* (non-Javadoc)
     * @see org.kaaproject.kaa.server.common.thrift.cli.server.BaseCliThriftService#getServerShortName()
     */
    @Override
    protected String getServerShortName() {
        return "bootstrap";
    }

    /* (non-Javadoc)
     * @see org.kaaproject.kaa.server.common.thrift.cli.server.BaseCliThriftService#initServiceCommands()
     */
    @Override
    protected void initServiceCommands() {
    }

    /**
     * BootstrapConfig getter.
     * @return BootstrapConfig the config
     */
    public BootstrapConfig getConfig() {
        return config;
    }

    /**
     * @param BootstrapConfig config the config to set
     */
    public void setConfig(BootstrapConfig config) {
        this.config = config;
    }

    /**
     * Thrift method to receive new list of Operations servers
     * Map represent:
     *  key (String) - DNS name of Operations server in format host:port
     *  value (operationsServer) - Operations server parameters, priority and Public RSA Key
     * @param Map<String, operationsServer> operationsMap
     */
    /* (non-Javadoc)
     * @see org.kaaproject.kaa.server.common.thrift.gen.bootstrap.BootstrapThriftService.Iface#onOperationsServerListUpdate(java.util.List)
     */
    @Override
    public void onOperationsServerListUpdate(List<ThriftOperationsServer> operationsServersList) throws TException {
        // TODO Auto-generated method stub
        LOG.info("Operations server list update recived: now {} servers online:", operationsServersList.size());
        for (ThriftOperationsServer server : operationsServersList) {
            LOG.trace("Operations Server {}", server.getName());
            for(ThriftSupportedChannel channel: server.getSupportedChannels()) {
                LOG.trace("SupportedChannel {}", channel.getType().toString());
                switch (channel.getType()) {
                case HTTP:
                    ThriftCommunicationParameters httpParams = channel.getCommunicationParams();
                    LOG.trace("HostName: {}, port {}",
                            httpParams.getHttpParams().getHostName(), 
                            httpParams.getHttpParams().getPort()); 
                    break;
                case HTTP_LP:
                    ThriftCommunicationParameters httpLpParams = channel.getCommunicationParams();
                    LOG.trace("HostName: {}, port {}",
                            httpLpParams.getHttpLpParams().getHostName(), 
                            httpLpParams.getHttpLpParams().getPort());                    
                    break;
                }
            }
        }
        if (getConfig() != null && getConfig().getOperationsServerListService() != null) {
            LOG.trace("Updating OperationsServerListService");
            getConfig().getOperationsServerListService().updateList(operationsServersList);
        } else {
            throw new TException("Bootstrap server not initialized properly, config not set");
        }
    }
}