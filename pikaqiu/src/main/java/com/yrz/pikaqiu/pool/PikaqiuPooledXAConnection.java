package com.yrz.pikaqiu.pool;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.sql.SQLException;

public class PikaqiuPooledXAConnection implements XAConnection {

    private PikaqiuPooledConnection pooledConnection;
    private XAConnection          xaConnection;

    public PikaqiuPooledXAConnection(PikaqiuPooledConnection pooledConnection, XAConnection xaConnection){
        this.pooledConnection = pooledConnection;
        this.xaConnection = xaConnection;

    }

    @Override
    public XAResource getXAResource() throws SQLException {
        return xaConnection.getXAResource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return pooledConnection;
    }

    @Override
    public void close() throws SQLException {
        pooledConnection.close();
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener connectionEventListener) {

    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener connectionEventListener) {

    }

    @Override
    public void addStatementEventListener(StatementEventListener statementEventListener) {

    }

    @Override
    public void removeStatementEventListener(StatementEventListener statementEventListener) {

    }
}
