package com.yrz.pikaqiu.pool;

import java.sql.PreparedStatement;

public class PikaqiuPreparedStatementHolder {
    public final PreparedStatement statement;

    public PikaqiuPreparedStatementHolder(PreparedStatement statement) {
        this.statement = statement;
    }
}
