package com.yrz.pikaqiu.pool;

import javax.sql.DataSource;
import java.sql.Connection;

public class PikaqiuConnectionHolder {

    protected final DataSource dataSource;
    protected final Connection conn;


    public PikaqiuConnectionHolder(DataSource dataSource, Connection conn) {
        this.dataSource = dataSource;
        this.conn = conn;
    }
}
