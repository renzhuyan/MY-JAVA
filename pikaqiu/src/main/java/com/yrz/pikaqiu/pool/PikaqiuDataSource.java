package com.yrz.pikaqiu.pool;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class PikaqiuDataSource implements DataSource {

    private volatile PikaqiuConnectionHolder[] connections;

    protected volatile int maxActive = 8;

    private final CountDownLatch initedLatch = new CountDownLatch(2);

    Driver driver;

    protected volatile String                          username;
    protected volatile String                          password;
    protected volatile String                          url;
    protected volatile String                          driverClassName;

    private int poolingCount = 0;

    protected volatile boolean                         inited                                    = false;

    protected ReentrantLock lock;
    protected Condition notEmpty;
    protected Condition empty;

    @Autowired
    private DataSourceProperties basicProperties;

    @Autowired
    private PikaqiuProperities pikaqiuProperities;

    public PikaqiuDataSource(){
        lock = new ReentrantLock();

        notEmpty = lock.newCondition();
        empty = lock.newCondition();
    }


    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void init() throws SQLException, InterruptedException {

        if (inited) {
            return;
        }

        // 获取驱动
        driver = JdbcUtils.createDriver(null, JdbcUtils.getDriverClassName(this.getUrl()));

        if (pikaqiuProperities.getMaxActive() != null) {
            maxActive = pikaqiuProperities.getMaxActive();
        }

        // 初始化连接池
        connections = new PikaqiuConnectionHolder[maxActive];
        for (int i = 0; i < maxActive; i++) {
            Connection physicalConn = createPhysicalConnection();
            connections[i] = new PikaqiuConnectionHolder(this, physicalConn);
            poolingCount++;
        }

        // 创建连接线程
        createAndStartCreatorThread();

        // 创建收缩线程
        createAndStartDestroyThread();

        initedLatch.await();

        inited = true;

        System.out.println("Init PikaqiuDataSource");
    }

    private Connection createPhysicalConnection() throws SQLException {
        Properties physicalConnectProperties = new Properties();
        physicalConnectProperties.put("user", this.getUsername());
        physicalConnectProperties.put("password", this.getPassword());
        return this.driver.connect(this.getUrl(), physicalConnectProperties);
    }

    @SneakyThrows
    @Override
    public PikaqiuPooledConnection getConnection() {
        init();

        poolingCount--;
        // 获取最后一个连接
        PikaqiuConnectionHolder pikaqiuConnectionHolder = connections[poolingCount];
        connections[poolingCount]=null;
        Connection conn = pikaqiuConnectionHolder.conn;
        return new PikaqiuPooledConnection(conn, pikaqiuConnectionHolder);
    }

    @Override
    public Connection getConnection(String s, String s1) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public ConnectionBuilder createConnectionBuilder() throws SQLException {
        return DataSource.super.createConnectionBuilder();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    private void put(Connection physicalConn) {
        connections[poolingCount] = new PikaqiuConnectionHolder(this, physicalConn);
        poolingCount++;
    }

    protected void createAndStartCreatorThread() {
         String threadName = "Druid-ConnectionPool-Create-" + System.identityHashCode(this);
         new CreateConnectionThread(threadName).start();
    }

    protected void createAndStartDestroyThread() {
        String threadName = "Druid-ConnectionPool-Destroy-" + System.identityHashCode(this);
        new DestroyConnectionThread(threadName).start();
    }



    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }

    public class CreateConnectionThread extends Thread {

        public CreateConnectionThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @SneakyThrows
        public void run() {
            initedLatch.countDown();

            for (; ; ) {
                // addLast
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e2) {
                    break;
                }

                try {

                    // 防止创建超过maxActive数量的连接,还要考虑正在工作的线程池，这里不做实现
                    if (poolingCount >= maxActive) {
                        empty.await();
                        continue;
                    }

                } catch (InterruptedException e) {
                } finally {
                    lock.unlock();
                }
                Connection physicalConn = createPhysicalConnection();
                put(physicalConn);
            }
        }
    }

    public class DestroyConnectionThread extends Thread {

        public DestroyConnectionThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        public void run() {
            initedLatch.countDown();

            // todo
        }

    }
}
