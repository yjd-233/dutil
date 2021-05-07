package com.dutil.bigdata.hbase;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

@Slf4j
public class HBaseConn {

    private static final HBaseConn INSTANCE = new HBaseConn();
    private static Configuration configuration;
    private volatile static Connection connection;

    private HBaseConn() {
        if (configuration == null) {
            configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.quorum", "host1,host2,host3");
//            configuration.set("hbase.zookeeper.property.clientPort",port);
        }
    }

    /**
     * 创建数据库连接
     */
    private Connection getConnection() {
        if (connection == null || connection.isClosed()) {
            synchronized (HBaseConn.class) {
                if (connection == null || connection.isClosed()) {
                    try {
                        connection = ConnectionFactory.createConnection(configuration);
                    } catch (IOException e) {
                        log.error("HBaseConn getConnection error", e);
                    }
                }
            }
        }
        return connection;
    }

    /**
     * 获取数据库连接
     */
    public static Connection getHBaseConn() {

        return INSTANCE.getConnection();
    }

    /**
     * 获取表实例
     */
    public static Table getTable(String tableName) throws IOException {
        return INSTANCE.getConnection().getTable(TableName.valueOf(tableName));
    }

    /**
     * 关闭连接
     */
    public static void closeConn() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                log.error("BaseConn closeConn error", e);
            }
        }
    }

}
