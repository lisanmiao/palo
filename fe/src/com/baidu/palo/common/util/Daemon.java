// Modifications copyright (C) 2017, Baidu.com, Inc.
// Copyright 2017 The Apache Software Foundation

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.baidu.palo.common.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class Daemon extends Thread {
    private static final Logger LOG = LogManager.getLogger(Daemon.class);
    private static final int DEFAULT_INTERVAL_SECONDS = 30; // 30 seconds
 
    private long intervalMs;
    private AtomicBoolean isStop;
    private Runnable runnable;
    
    {
        setDaemon(true);
    }

    public Daemon() {
        super();
        intervalMs = DEFAULT_INTERVAL_SECONDS  * 1000L;
        isStop = new AtomicBoolean(false);
    }

    public Daemon(Runnable runnable) {
        super(runnable);
        this.runnable = runnable;
        this.setName(((Object)runnable).toString());
    }

    public Daemon(ThreadGroup group, Runnable runnable) {
        super(group, runnable);
        this.runnable = runnable;
        this.setName(((Object) runnable).toString());
    }
    
    public Daemon(String name) {
        this(name, DEFAULT_INTERVAL_SECONDS  * 1000L);
    }

    public Daemon(String name, long intervalMs) {
        this(intervalMs);
        this.setName(name);
    }
    
    public Daemon(long intervalMs) {
        this();
        this.intervalMs = intervalMs;
    }

    public Runnable getRunnable() {
        return runnable;
    }
    
    public void exit() {
        isStop.set(true);
    }
    
    public long getInterval() {
        return this.intervalMs;
    }

    public void setInterval(long intervalMs) {
        this.intervalMs = intervalMs;
    }
    
    /**
     * implement in child
     */
    protected void runOneCycle() {
        
    }

    @Override
    public void run() {
        while (!isStop.get()) {
            try {
                runOneCycle();
            } catch (Exception e) {
                LOG.error("exception: ", e);
            }

            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                LOG.error("InterruptedException: ", e);
            }
        }
        LOG.error("daemon thread exits. name=" + this.getName());
    }
}
