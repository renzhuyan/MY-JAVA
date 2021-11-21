package com.yrz.pikaqiu.pool;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PikaqiuXADataSource extends PikaqiuDataSource implements XADataSource {
    @Override
    public XAConnection getXAConnection() throws SQLException {

        PikaqiuPooledConnection pikaqiuPooledConnection = this.getConnection();

        XAConnection physicalXAConnection = createPhysicalXAConnection(pikaqiuPooledConnection.conn);

        return new PikaqiuPooledXAConnection(pikaqiuPooledConnection,physicalXAConnection);
    }

    @Override
    public XAConnection getXAConnection(String s, String s1) throws SQLException {
        return null;
    }

    private XAConnection createPhysicalXAConnection(Connection physicalConn) throws SQLException {
        return MysqlUtils.createXAConnection(driver, physicalConn);
    }
}
