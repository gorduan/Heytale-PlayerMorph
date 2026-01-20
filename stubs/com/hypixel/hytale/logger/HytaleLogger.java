package com.hypixel.hytale.logger;

import java.util.logging.Level;
import javax.annotation.Nonnull;

public class HytaleLogger {
    private static final Api API_IMPL = new ApiImpl();

    public HytaleLogger() {}

    @Nonnull
    public static HytaleLogger get(String loggerName) { return new HytaleLogger(); }

    @Nonnull
    public static HytaleLogger forEnclosingClass() { return new HytaleLogger(); }

    @Nonnull
    public Api at(@Nonnull Level level) { return API_IMPL; }

    @Nonnull
    public Api atFine() { return API_IMPL; }

    @Nonnull
    public Api atInfo() { return API_IMPL; }

    @Nonnull
    public Api atWarning() { return API_IMPL; }

    @Nonnull
    public Api atSevere() { return API_IMPL; }

    // Api must be an INTERFACE to match the real HytaleLogger$Api
    public static interface Api {
        Api withCause(Throwable cause);
        void log(String message);
        void log(String message, Object arg);
        void log(String message, Object arg1, Object arg2);
        void log(String message, Object arg1, Object arg2, Object arg3);
    }

    // Internal implementation of the Api interface
    private static class ApiImpl implements Api {
        @Override
        public Api withCause(Throwable cause) { return this; }
        @Override
        public void log(String message) {}
        @Override
        public void log(String message, Object arg) {}
        @Override
        public void log(String message, Object arg1, Object arg2) {}
        @Override
        public void log(String message, Object arg1, Object arg2, Object arg3) {}
    }
}
