package com.dutil.bigdata.bloom.boring;

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
public interface TimeToLiveMapAware<T> {

  /**
   * Gets a map of items to their TTLs.
   *
   * @return A time map of TTLs.
   */
  TimeMap<T> getTimeToLiveMap();

  /**
   * Sets a map of items to their TTLs.
   *
   * @param map A time map of TTLs.
   */
  void setTimeToLiveMap(TimeMap<T> map);
}
