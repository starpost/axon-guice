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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.inject.Provider;
import javax.sql.DataSource;

import org.axonframework.common.jdbc.DataSourceConnectionProvider;
import org.axonframework.common.jdbc.UnitOfWorkAwareConnectionProviderWrapper;
import org.axonframework.eventstore.SnapshotEventStore;
import org.axonframework.eventstore.jdbc.DefaultEventEntryStore;
import org.axonframework.eventstore.jdbc.EventSqlSchema;
import org.axonframework.eventstore.jdbc.GenericEventSqlSchema;
import org.axonframework.eventstore.jdbc.JdbcEventStore;
import org.axonframework.eventstore.jdbc.SchemaConfiguration;
import org.axonframework.serializer.xml.XStreamSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;

/**
 * Create JDBC event store using MyBatis Support module exported "quantum"
 * DataSoruce.
 * 
 * @author kmtong
 *
 */
public abstract class JdbcEventStoreProvider implements Provider<SnapshotEventStore> {

	final static Logger logger = LoggerFactory.getLogger(JdbcEventStoreProvider.class);

	protected abstract DataSource getDataSource();

	protected abstract AxonConfig getAxonConfig();

	protected SchemaConfiguration getSchemaConfiguration() {
		return new SchemaConfiguration();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected EventSqlSchema getEventSqlSchema(SchemaConfiguration config) {
		return new GenericEventSqlSchema((Class<?>) byte[].class, config);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SnapshotEventStore get() {

		SchemaConfiguration config = getSchemaConfiguration();
		EventSqlSchema schema = getEventSqlSchema(config);

		DataSource ds = getDataSource();
		Connection conn = null;
		try {
			conn = ds.getConnection();
			tryCreateDomainEventEntryTable(schema, config, conn);
			tryCreateSnapshotEventEntryTable(schema, config, conn);
		} catch (Exception e) {
			logger.error("Error Initializing Event Store Tables", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}

		// Converters for EventStore
		XStreamSerializer eventSerializer = new XStreamSerializer();
		XStream xStream = eventSerializer.getXStream();
		for (Class<? extends Converter> cls : getAxonConfig().getEventSerializerConverterClasses()) {
			try {
				xStream.registerConverter(cls.newInstance());
			} catch (Exception e) {
				logger.error("Error Registering Converter: " + cls.getName(), e);
			}
		}

		JdbcEventStore es = new JdbcEventStore(
				new DefaultEventEntryStore(
						new UnitOfWorkAwareConnectionProviderWrapper(new DataSourceConnectionProvider(ds))),
				eventSerializer);

		return es;
	}

	@SuppressWarnings("rawtypes")
	private void tryCreateDomainEventEntryTable(EventSqlSchema schema, SchemaConfiguration config, Connection conn) {
		try {
			if (!testExists(config.domainEventEntryTable(), conn)) {
				schema.sql_createDomainEventEntryTable(conn).execute();
			}
		} catch (Exception e) {
			logger.error("Error Creating Table " + config.domainEventEntryTable(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	private void tryCreateSnapshotEventEntryTable(EventSqlSchema schema, SchemaConfiguration config, Connection conn) {
		try {
			if (!testExists(config.snapshotEntryTable(), conn)) {
				schema.sql_createSnapshotEventEntryTable(conn).execute();
			}
		} catch (Exception e) {
			logger.error("Error Creating Table " + config.snapshotEntryTable(), e);
		}
	}

	private boolean testExists(String tableName, Connection conn) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM " + tableName + " LIMIT 1");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.getString(1);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
