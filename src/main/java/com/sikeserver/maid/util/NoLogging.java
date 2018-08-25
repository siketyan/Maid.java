package com.sikeserver.maid.util;

import org.eclipse.jetty.util.log.Logger;

public class NoLogging implements Logger {
    @Override public void warn(String msg, Object... args) {}
    @Override public void warn(Throwable thrown) {}
    @Override public void warn(String msg, Throwable thrown) {}
    @Override public void info(String msg, Object... args) {}
    @Override public void info(Throwable thrown) {}
    @Override public void info(String msg, Throwable thrown) {}
    @Override public void debug(String msg, Object... args) {}
    @Override public void debug(String s, long l) {}
    @Override public void debug(Throwable thrown) {}
    @Override public void debug(String msg, Throwable thrown) {}
    @Override public void ignore(Throwable ignored) {}
    @Override public void setDebugEnabled(boolean enabled) {}
    @Override public boolean isDebugEnabled() { return false; }
    @Override public String getName() { return "no"; }
    @Override public Logger getLogger(String name) { return this; }
}
