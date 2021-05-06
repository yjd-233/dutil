package com.dutil.bigdata.bloom.boring.memory;


import com.dutil.bigdata.bloom.boring.BloomFilter;
import com.dutil.bigdata.bloom.boring.FilterBuilder;
import com.dutil.bigdata.bloom.boring.HashProvider.HashMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * <p>Copyright (C) 2017-2019 THL A29 Limited, a Qknode company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at</p>
 *
 * <p>https://opensource.org/licenses/Apache-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.</p>
 *
 * @Auther: chencheng@qknode.com
 * @Date: 2019/2/16 11:12
 * @Description:
 */
@Slf4j
public class BloomFilterFile {

  final int expectedElements;
  final double falsePositiveProbability;
  final long snapshotPeriod;
  final int ttlhour; // when's hour to clear ttl bloomtilter block
  final long ttlPeriod;
  private BloomFilterMemory<String>[] bloomFilters;
  private int bloomNum;
  private int writeIndex = 0;
  private String bloomfilterdir;
  private String currentBloomFilterFile = "current";

  public BloomFilterFile(final int ttlhour, final long ttlPeriod, final int bloomNum,
      final long snapshotPeriod,
      final int expectedElements,
      final double falsePositiveProbability,
      final String bloomfilterdir,
      boolean reload) {
    this.bloomNum = bloomNum;
    this.snapshotPeriod = snapshotPeriod;
    this.bloomfilterdir = bloomfilterdir;
    this.falsePositiveProbability = falsePositiveProbability;
    this.expectedElements = expectedElements;
    this.ttlhour = ttlhour;
    this.ttlPeriod = ttlPeriod;
    bloomFilters = new BloomFilterMemory[this.bloomNum];
    if (reload) {
        new Thread( () -> {loadBloomFilterFromFile();
        log.info("loadBloomFilterFromFile....");
        } ).start();
    } else {
      createBloomFilter();
    }
//        deleteIndex = (writeIndex + 1) % this.bloomNum;

    startTTLBloomFilter(ttlhour, ttlPeriod);

  }

  public BloomFilterFile(boolean reload) {
    this(4, 24 * 3600 * 1000, 7, 30_000, 1_000_000, 0.001, "/tmp/dolphin-bloomfilter", reload);
  }

  public static void main(String[] args) {
    BloomFilterFile filter = new BloomFilterFile(14,
        10 * 1000, 7, 30_000, 1_000_000, 0.001, "/tmp/dolphin-bloomfilter", false);
    for (int i = 0; i < 5; i++) {

      filter.addAndCheck("haha-" + i);
//            filter.addAndCheck("haha-" + i);
    }
//        filter.snapshotBloomFilter();
//        BloomFilterFile otherfilter = new BloomFilterFile(true);
    int tcount = 0;
    int fcount = 0;
    System.out.println("read");
    for (int i = 0; i < 5; i++) {
      if (filter.check("haha-" + i)) {
        tcount++;
      } else {
        fcount++;
      }
//            filter.addAndCheck("haha-" + i);
    }

    System.out.println("true:" + tcount + ", false:" + fcount);
  }

  public int getWriteIndex() {
    return writeIndex;
  }

  public int getDeleteIndex() {
    return (writeIndex + 1) % this.bloomNum;
  }

  public void add(String element) {
    bloomFilters[writeIndex].add(element);
    if (log.isDebugEnabled()) {
      log.debug("[add] current write[{}] bf, data:{}",
          writeIndex, element);
    }
  }

  /**
   * @param element need check
   * @return true if the value did previously exist in the filter. Note, that a false positive may
   * occur, thus the value may not have already been in the filter, but it hashed to a set of bits
   * already in the filter.
   */
  public boolean addAndCheck(String element) {
    boolean exist = false;
    try {
      BloomFilter<String> current = bloomFilters[writeIndex];
      int num = 1;
      exist = current.contains(element);
      if (log.isDebugEnabled()) {
        log.debug("[addAndCheck] current write[{}] check[{}] status[{}]", writeIndex, element,
            exist);
      }
      if (!exist) {
        int index = writeIndex;
        while (((index = (--index + bloomNum) % bloomNum) != writeIndex)) {
          num++;
          exist = bloomFilters[index].contains(element);
          if (log.isDebugEnabled()) {
            log.debug("[addAndCheck] current read[{}] check[{}] status[{}]", index, element, exist);
          }
          if (exist) {
            break;
          }
          if (num > bloomNum) {
            log.warn(
                "[addAndCheck] bloom Check find problem, current {}, is out of size of bloomNum[{}]",
                num, bloomNum);
            break;
          }
        }
      }
      if (!exist) {
        exist = !current.add(element);
      }
    } catch (Exception e) {
      log.error("", e);
    }
    return exist;
  }

  public boolean check(String element) {
    boolean exist = false;
    int index = writeIndex;
    int num = 1;
    try {
      exist = bloomFilters[index].contains(element);
      if (log.isDebugEnabled()) {
        log.debug("[check] current write[{}] check[{}] status[{}]", index, element, exist);
      }
      if (!exist) {
        while (((index = (--index + bloomNum) % bloomNum) != writeIndex)) {
          num++;
          exist = bloomFilters[index].contains(element);
          if (log.isDebugEnabled()) {
            log.debug("[check] current read[{}] check[{}] status[{}]", index, element, exist);
          }
          if (exist) {
            break;
          }
          if (num > bloomNum) {
            log.warn(
                "[check] bloom Check find problem, current {}, is out of size of bloomNum[{}]", num,
                bloomNum);
            break;
          }
        }
      }
    } catch (Exception e) {
      log.error("", e);
    }
    return exist;
  }

  /**
   * backup snapshot current writing bloomfilter per one minute
   */
  void startSnapshotBloomFilterService(long snapshotPeriod) {
    Timer timer = new Timer("SnapshotBloomFilterChore");
    timer.schedule(new SnapshotBloomFilterChore(), 30000, snapshotPeriod);
    log.info(
        "start snapshot bloomfilter server, first task at after {}(s), and crontab period {}(s)",
        30, snapshotPeriod / 1000);
  }

  void startTTLBloomFilter(int hour, long ttlPeriod) {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    calendar.set(year, month, day, hour, 0, 0);
    Date date = calendar.getTime();
    long firstTime = date.getTime();
    long cur = System.currentTimeMillis();
    long delay = firstTime - cur;
    while (delay < 0) {
      delay = ttlPeriod + delay;
    }
    Timer timer = new Timer("TTLBloomFilterChore");
    timer.schedule(new TTLBloomFilterChore(), delay, ttlPeriod);
    log.info("start ttl server, first task at {}, and crontab period {}(h)",
        new Date(firstTime), ttlPeriod / (3600 * 1000));
  }

  public void cleanSnapshot() {
    snapshotBloomFilter();
  }

  public void startSnapshot() {
    startSnapshotBloomFilterService(this.snapshotPeriod);
  }

  private void snapshotBloomFilter() {
    long start = -1;
    File datafile = null;
    try {
      start = System.currentTimeMillis();
      BloomFilterMemory<String> filter = bloomFilters[writeIndex];
      byte[] data = filter.getBitSet().toByteArray();
      long copyTime = System.currentTimeMillis() - start;
      datafile = new File(bloomfilterdir, String.valueOf(writeIndex));
      File currentfile = new File(bloomfilterdir, currentBloomFilterFile);
      FileUtils.writeByteArrayToFile(datafile, data);
      FileUtils.writeStringToFile(currentfile, String.valueOf(writeIndex), "UTF-8");
      log.info(
          "bloomfilter[{}] cost {}(ms), copy:{}(ms) byte len:{}, bf bloomNum:{}, snapshot dump ok:{}",
          writeIndex, (System.currentTimeMillis() - start), copyTime,
          data.length, filter.getSize(), datafile.getPath());
    } catch (IOException e) {
      log.error("bloomfilter[{}] cost {}(ms), backup fail:{}", writeIndex,
          (System.currentTimeMillis() - start), datafile == null ? "null" : datafile.getPath(), e);
    }
  }

  public void createBloomFilter() {
    for (int i = 0; i < bloomNum; i++) {
      bloomFilters[i] = (BloomFilterMemory) new FilterBuilder(expectedElements,
          falsePositiveProbability)
          .hashFunction(HashMethod.Murmur3).buildBloomFilter();
      log.info("create bloomfilter[{}], total num {}", i, bloomNum);
    }
  }

  public boolean loadBloomFilterFromFile() {
    String tmpcurrent = "0";
    File cur = new File(bloomfilterdir, currentBloomFilterFile);
    if (cur.exists()) {
      try {
        tmpcurrent = FileUtils.readFileToString(cur, "UTF-8");
      } catch (IOException e) {
        log.error("writeIndex:{} read fail.", tmpcurrent, e);
      }
    }
    this.writeIndex = Integer.valueOf(tmpcurrent);
    bloomFilters = new BloomFilterMemory[bloomNum];
    for (int i = 0; i < bloomNum; i++) {
      File f = new File(bloomfilterdir, String.valueOf(i));
      bloomFilters[i] = (BloomFilterMemory) new FilterBuilder(expectedElements,
          falsePositiveProbability)
          .hashFunction(HashMethod.Murmur3).buildBloomFilter();
      if (f.exists() && f.isFile()) {
        try {
          byte[] data = FileUtils.readFileToByteArray(f);
          bloomFilters[i].setBitSet(BitSet.valueOf(data));
          log.info("bloomfilter[{}] byte len: {}, bf bloomNum:{} load ok:{}", i,
              data.length, bloomFilters[i].getSize(), f.getPath());
        } catch (IOException e) {
          log.error("bloomfilter[{}] load fail:{}", i, f.getPath(), e);
        }
      }

    }
    return !tmpcurrent.equals("-1");
  }

  private class SnapshotBloomFilterChore extends TimerTask {

    @Override
    public void run() {
      snapshotBloomFilter();
    }

  }

  /**
   * delete ttl bloomfilter per one minute
   */
  private class TTLBloomFilterChore extends TimerTask {

    @Override
    public void run() {
      try {
        long start = System.currentTimeMillis();
        int deleteIndex = (writeIndex + 1) % bloomNum;
        log.info("start process ttl bloomfilter, current writer index[{}], delete index[{}]",
            writeIndex, deleteIndex);
        bloomFilters[deleteIndex].clear();
        writeIndex = deleteIndex;//(writeIndex + 1) % bloomNum;
//                deleteIndex = (writeIndex + 1) % bloomNum;
        log.info("server have processed current write index[{}], " +
                "clean ttl block of bloomfilter[{}] and cost {}(ms)",
            writeIndex, deleteIndex, (System.currentTimeMillis() - start));
      } catch (Exception e) {
        log.error("", e);
      }
    }
  }

}
