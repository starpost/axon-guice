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

import javax.inject.Provider;
import javax.sql.DataSource;

import org.axonframework.common.jdbc.DataSourceConnectionProvider;
import org.axonframework.common.jdbc.UnitOfWorkAwareConnectionProviderWrapper;
import org.axonframework.eventstore.SnapshotEventStore;
import org.axonframework.eventstore.jdbc.DefaultEventEntryStore;
import org.axonframework.eventstore.jdbc.GenericEventSqlSchema;
import org.axonframework.eventstore.jdbc.JdbcEventStore;
import org.axonframework.serializer.xml.XStreamSerializer;

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

	final AxonConfig config;

	public JdbcEventStoreProvider(AxonConfig config) {
		this.config = config;
	}

	protected abstract DataSource getDataSource();

	@SuppressWarnings("rawtypes")
	@Override
	public SnapshotEventStore get() {
		GenericEventSqlSchema schema = new GenericEventSqlSchema();
		DataSource ds = getDataSource();
		Connection conn = null;
		try {
			conn = ds.getConnection();
			tryCreateDomainEventEntryTable(schema, conn);
			tryCreateSnapshotEventEntryTable(schema, conn);
		} catch (Exception e) {
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// Converters for EventStore
		XStreamSerializer eventSerializer = new XStreamSerializer();
		XStream xStream = eventSerializer.getXStream();
		for (Class<? extends Converter> cls : config.getEventSerializerConverterClasses()) {
			try {
				xStream.registerConverter(cls.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JdbcEventStore es = new JdbcEventStore(
				new DefaultEventEntryStore(
						new UnitOfWorkAwareConnectionProviderWrapper(new DataSourceConnectionProvider(ds))),
				eventSerializer);

		return es;
	}

	@SuppressWarnings("rawtypes")
	private void tryCreateDomainEventEntryTable(GenericEventSqlSchema schema, Connection conn) {
		try {
			schema.sql_createDomainEventEntryTable(conn).execute();
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("rawtypes")
	private void tryCreateSnapshotEventEntryTable(GenericEventSqlSchema schema, Connection conn) {
		try {
			schema.sql_createSnapshotEventEntryTable(conn).execute();
		} catch (Exception e) {
		}
	}

}
