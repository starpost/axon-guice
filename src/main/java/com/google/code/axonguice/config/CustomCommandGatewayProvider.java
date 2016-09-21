/**
 * STARPOST CONFIDENTIAL
 * _____________________
 * 
 * [2014] - [2016] StarPost Supply Chain Management Co. (Shenzhen) Ltd. 
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of
 * StarPost Supply Chain Management Co. (Shenzhen) Ltd. and its suppliers, if
 * any. The intellectual and technical concepts contained herein are proprietary
 * to StarPost Supply Chain Management Co. (Shenzhen) Ltd. and its suppliers and
 * may be covered by China and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from StarPost Supply Chain Management Co. (Shenzhen)
 * Ltd.
 *
 */
package com.google.code.axonguice.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.axonframework.commandhandling.gateway.GatewayProxyFactory;

/**
 * @author kmtong
 *
 */
public class CustomCommandGatewayProvider<T> implements Provider<T> {

	@Inject
	GatewayProxyFactory gatewayFactory;

	final Class<T> api;

	public CustomCommandGatewayProvider(Class<T> api) {
		super();
		this.api = api;
	}

	@Override
	public T get() {
		return gatewayFactory.createGateway(api);
	}

}
