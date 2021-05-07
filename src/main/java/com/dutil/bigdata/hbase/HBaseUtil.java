package com.dutil.bigdata.hbase;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class HBaseUtil {

  /**
   * 创建HBase表.
   *
   * @param tableName 表名
   * @param cfs 列族的数组
   * @return 是否创建成功
   */
  public static boolean createTable(String tableName, String[] cfs) {
    try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
      if (admin.isTableAvailable(TableName.valueOf(tableName))) {
        return false;
      }
      TableDescriptorBuilder tableDescriptor = TableDescriptorBuilder
          .newBuilder(TableName.valueOf(tableName));
      Arrays.stream(cfs).forEach(cf -> tableDescriptor.setColumnFamily(
          ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).build()));
      admin.createTable(tableDescriptor.build());
    } catch (Exception e) {
      log.error("", e);
    }
    return true;
  }


  /**
   * 删除hbase表.
   *
   * @param tableName 表名
   * @return 是否删除成功
   */
  public static boolean deleteTable(String tableName) {
    try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
      admin.disableTable(TableName.valueOf(tableName));
      admin.deleteTable(TableName.valueOf(tableName));
    } catch (Exception e) {
      log.error("", e);
    }
    return true;
  }

  /**
   * hbase插入一条数据.
   *
   * @param tableName 表名
   * @param rowKey 唯一标识
   * @param cfName 列族名
   * @param qualifier 列标识
   * @param data 数据
   * @return 是否插入成功
   */
  public static boolean putRow(String tableName, String rowKey, String cfName, String qualifier,
      String data) {
    try (Table table = HBaseConn.getTable(tableName)) {
      Put put = new Put(Bytes.toBytes(rowKey));
      put.addColumn(Bytes.toBytes(cfName), Bytes.toBytes(qualifier), Bytes.toBytes(data));
      table.put(put);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return true;
  }

  /**
   * hbase插入一条数据.
   *
   * @param tableName 表名
   * @param put 要插入的数据
   * @return 是否插入成功
   */
  public static boolean putRow(String tableName, Put put) {
    try (Table table = HBaseConn.getTable(tableName)) {
      table.put(put);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return true;
  }

  /**
   * hbase 批量插入数据
   *
   * @param tableName 表名
   * @param puts 要插入的数据
   * @return 是否插入成功
   */
  public static boolean putRows(String tableName, List<Put> puts) {
    try (Table table = HBaseConn.getTable(tableName)) {
      table.put(puts);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return true;
  }

  /**
   * 获取单条数据.
   *
   * @param tableName 表名
   * @param rowKey 唯一标识
   * @return 查询结果
   */
  public static Result getRow(String tableName, String rowKey) {
    try (Table table = HBaseConn.getTable(tableName)) {
      Get get = new Get(Bytes.toBytes(rowKey));
      return table.get(get);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return null;
  }

  /**
   * 获取单条数据.
   *
   * @param tableName 表名
   * @param get 要查询数据
   * @return 查询结果
   */
  public static Result getRow(String tableName, Get get) {
    try (Table table = HBaseConn.getTable(tableName)) {
      return table.get(get);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return null;
  }

  /**
   * 获取多条数据.
   *
   * @param tableName 表名
   * @param gets 要获取的数据
   * @return 查询结果
   */
  public static Result[] getRows(String tableName, List<Get> gets) {
    try (Table table = HBaseConn.getTable(tableName)) {
      return table.get(gets);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return null;
  }

  /**
   * 利用过滤器获取单条数据
   *
   * @param tableName 表名
   * @param rowKey 行键
   * @param filterList 过滤器列表
   * @return 查询结果
   */
  public static Result getRow(String tableName, String rowKey, FilterList filterList) {
    try (Table table = HBaseConn.getTable(tableName)) {
      Get get = new Get(Bytes.toBytes(rowKey));
      get.setFilter(filterList);
      return table.get(get);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return null;
  }

  /**
   * scan扫描表操作
   *
   * @param tableName 表名
   * @return ResultScanner实例
   */
  public static ResultScanner getScanner(String tableName) {
    try (Table table = HBaseConn.getTable(tableName)) {
      Scan scan = new Scan();
      scan.setCaching(1000);
      return table.getScanner(scan);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return null;
  }

  /**
   * 批量检索数据.
   *
   * @param tableName 表名
   * @param startRowKey 起始RowKey
   * @param endRowKey 终止RowKey
   * @return ResultScanner实例
   */
  public static ResultScanner getScanner(String tableName, String startRowKey, String endRowKey) {
    try (Table table = HBaseConn.getTable(tableName)) {
      Scan scan = new Scan();
      scan.withStartRow(Bytes.toBytes(startRowKey));
      scan.withStopRow(Bytes.toBytes(endRowKey));
      scan.setCaching(10);
      return table.getScanner(scan);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return null;
  }

  /**
   * 过滤扫描
   *
   * @param tableName 表名
   * @param startRowKey 起始行键
   * @param endRowKey 终止行键
   * @param filterList 过滤器列表
   * @return ResultScanner实例
   */
  public static ResultScanner getScanner(String tableName, String startRowKey, String endRowKey,
      FilterList filterList) {
    try (Table table = HBaseConn.getTable(tableName)) {
      Scan scan = new Scan();
      scan.withStartRow(Bytes.toBytes(startRowKey));
      scan.withStopRow(Bytes.toBytes(endRowKey));
      scan.setFilter(filterList);
      scan.setCaching(10);
      return table.getScanner(scan);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return null;
  }

  /**
   * HBase删除一行记录.
   *
   * @param tableName 表名
   * @param rowKey 唯一标识
   * @return 是否删除成功
   */
  public static boolean deleteRow(String tableName, String rowKey) {
    try (Table table = HBaseConn.getTable(tableName)) {
      Delete delete = new Delete(Bytes.toBytes(rowKey));
      table.delete(delete);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return true;
  }

  /**
   * HBase删除一行记录.
   *
   * @param tableName 表名
   * @param delete 要删除的记录
   * @return 是否删除成功
   */
  public static boolean deleteRow(String tableName, Delete delete) {
    try (Table table = HBaseConn.getTable(tableName)) {
      table.delete(delete);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return true;
  }

  /**
   * hbase删除多行记录
   *
   * @param tableName 表名
   * @param deletes 要删除的记录列表
   * @return 是否删除成功
   */
  public static boolean deleteRows(String tableName, List<Delete> deletes) {
    try (Table table = HBaseConn.getTable(tableName)) {
      table.delete(deletes);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return true;
  }

  /**
   * 删除列族
   *
   * @param tableName 表名
   * @param cfName 列族名
   * @return 是否删除成功
   */
  public static boolean deleteColumnFamily(String tableName, String cfName) {
    try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
      admin.deleteColumnFamily(TableName.valueOf(tableName), Bytes.toBytes(cfName));
    } catch (Exception e) {
      log.error("", e);
    }
    return true;
  }

  /**
   * 删除列限定符
   *
   * @param tableName 表名
   * @param rowKey 行键
   * @param cfName 列族名
   * @param qualifier 列限定符
   * @return 是否删除成功
   */
  public static boolean deleteQualifier(String tableName, String rowKey, String cfName,
      String qualifier) {
    try (Table table = HBaseConn.getTable(tableName)) {
      Delete delete = new Delete(Bytes.toBytes(rowKey));
      delete.addColumn(Bytes.toBytes(cfName), Bytes.toBytes(qualifier));
      table.delete(delete);
    } catch (IOException ioe) {
      log.error("", ioe);
    }
    return true;
  }


}
