package com.google.code.axonguice.repository.eventsourcing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.axonframework.eventstore.jdbc.GenericEventSqlSchema;
import org.axonframework.eventstore.jdbc.PostgresEventSqlSchema;
import org.axonframework.eventstore.jdbc.SchemaConfiguration;

/**
 * SQL schema supporting MySQL databases.
 * <p/>
 * The difference to the GenericEventSqlSchema is the use of mysql'
 * <code>longblob</code> data type for storing the serialized payload and
 * metaData.
 *
 * <p/>
 * Adapted from PostgresEventSqlSchema
 * 
 * @author Jochen Munz
 * @author kmtong
 * @since 2.4
 * @see PostgresEventSqlSchema
 */
public class MySQLEventSqlSchema<T> extends GenericEventSqlSchema<T> {

	public MySQLEventSqlSchema() {
	}

	public MySQLEventSqlSchema(Class<T> dataType) {
		super(dataType);
	}

	public MySQLEventSqlSchema(Class<T> dataType, SchemaConfiguration schemaConfiguration) {
		super(dataType, schemaConfiguration);
	}

	@Override
	public PreparedStatement sql_createSnapshotEventEntryTable(Connection connection) throws SQLException {
		final String sql = "create table " + schemaConfiguration.snapshotEntryTable() + " ("
				+ "        aggregateIdentifier varchar(255) not null," //
				+ "        sequenceNumber bigint not null," //
				+ "        type varchar(255) not null," //
				+ "        eventIdentifier varchar(255) not null," //
				+ "        metaData longblob," //
				+ "        payload longblob not null," //
				+ "        payloadRevision varchar(255)," //
				+ "        payloadType varchar(255) not null," //
				+ "        timeStamp varchar(255) not null," //
				+ "        primary key (aggregateIdentifier, sequenceNumber, type)" //
				+ "    );";
		return connection.prepareStatement(sql);
	}

	@Override
	public PreparedStatement sql_createDomainEventEntryTable(Connection connection) throws SQLException {
		final String sql = "create table " + schemaConfiguration.domainEventEntryTable() + " ("
				+ "        aggregateIdentifier varchar(255) not null," //
				+ "        sequenceNumber bigint not null," //
				+ "        type varchar(255) not null," //
				+ "        eventIdentifier varchar(255) not null," //
				+ "        metaData longblob," //
				+ "        payload longblob not null," //
				+ "        payloadRevision varchar(255)," //
				+ "        payloadType varchar(255) not null," //
				+ "        timeStamp varchar(255) not null," //
				+ "        primary key (aggregateIdentifier, sequenceNumber, type)" //
				+ "    );";
		return connection.prepareStatement(sql);
	}
}
