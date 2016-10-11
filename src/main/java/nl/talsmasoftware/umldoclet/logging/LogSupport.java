/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package nl.talsmasoftware.umldoclet.logging;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.SourcePosition;

import java.io.Closeable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Sjoerd Talsma
 */
public class LogSupport implements DocErrorReporter {
    private static final Logger LOGGER = Logger.getLogger(LogSupport.class.getName());

    public static final LogSupport INSTANCE = new LogSupport();
    private static final ThreadLocal<SourcePosition> POS = new ThreadLocal<>();
    private static volatile DocErrorReporter reporter;

    public static void setReporter(DocErrorReporter reporter) {
        LogSupport.reporter = reporter;
    }

    public static void setLevel(Object level) {
        switch (Objects.toString(level, "INFO").toUpperCase(Locale.ENGLISH).trim()) {
            case "ALL":
            case "TRACE":
            case "FINEST":
                LOGGER.setLevel(Level.FINEST);
                break;
            case "FINER":
                LOGGER.setLevel(Level.FINER);
                break;
            case "DEBUG":
            case "FINE":
                LOGGER.setLevel(Level.FINE);
                break;
            case "CONFIG":
                LOGGER.setLevel(Level.CONFIG);
                break;
            case "INFO":
                LOGGER.setLevel(Level.INFO);
                break;
            case "WARN":
            case "WARNING":
                LOGGER.setLevel(Level.WARNING);
                break;
            case "ERROR":
            case "FATAL":
            case "SEVERE":
                LOGGER.setLevel(Level.SEVERE);
                break;
            case "OFF":
                LOGGER.setLevel(Level.OFF);
                break;
            default:
                throw new IllegalArgumentException(format("Unsupported log level \"{0}\"!", level));
        }
    }

    private static String format(String msg, Object... args) {
        // TODO: add message resourcebundle support
        return MessageFormat.format(msg, args);
    }

    public static boolean isTraceEnabled() {
        return LOGGER.isLoggable(Level.FINEST);
    }

    public static void trace(String msg, Object... args) {
        if (isTraceEnabled()) {
            if (reporter == null) {
                LOGGER.log(Level.FINEST, format(msg, args), exception(args));
            } else {
                reporter.printNotice(POS.get(), format(msg, args));
            }
        }
    }

    public static void debug(String msg, Object... args) {
        if (LOGGER.isLoggable(Level.FINE)) {
            if (reporter == null) {
                LOGGER.log(Level.FINE, format(msg, args), exception(args));
            } else {
                reporter.printNotice(POS.get(), format(msg, args));
            }
        }
    }

    public static void info(String msg, Object... args) {
        if (LOGGER.isLoggable(Level.INFO)) {
            if (reporter == null) {
                LOGGER.log(Level.INFO, format(msg, args), exception(args));
            } else {
                reporter.printNotice(POS.get(), format(msg, args));
            }
        }
    }

    public static void warn(String msg, Object... args) {
        if (LOGGER.isLoggable(Level.WARNING)) {
            if (reporter == null) {
                LOGGER.log(Level.WARNING, format(msg, args), exception(args));
            } else {
                reporter.printWarning(POS.get(), format(msg, args));
            }
        }
    }

    public static void error(String msg, Object... args) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
            if (reporter == null) {
                LOGGER.log(Level.SEVERE, format(msg, args), exception(args));
            } else {
                reporter.printError(POS.get(), format(msg, args));
            }
        }
    }

    @Override
    public void printNotice(String msg) {
        info(msg);
    }

    @Override
    public void printWarning(String msg) {
        warn(msg);
    }

    @Override
    public void printError(String msg) {
        error(msg);
    }


    @Override
    public void printNotice(SourcePosition pos, String msg) {
        try (GlobalPosition gp = new GlobalPosition(pos)) {
            info(msg);
        }
    }

    @Override
    public void printWarning(SourcePosition pos, String msg) {
        try (GlobalPosition gp = new GlobalPosition(pos)) {
            warn(msg);
        }
    }

    @Override
    public void printError(SourcePosition pos, String msg) {
        try (GlobalPosition gp = new GlobalPosition(pos)) {
            error(msg);
        }
    }

    private static Throwable exception(Object... args) {
        for (int i = args.length - 1; i >= 0; i--) {
            if (args[i] instanceof Throwable) {
                return (Throwable) args[i];
            }
        }
        return null;
    }

    /**
     * For setting a source position within a try-with-resources code block.
     */
    public static class GlobalPosition implements Closeable {
        private final SourcePosition prev = POS.get();

        public GlobalPosition(SourcePosition pos) {
            setPos(pos);
        }

        private void setPos(SourcePosition pos) {
            if (pos == null) {
                POS.remove();
            } else {
                POS.set(pos);
            }
        }

        public void close() {
            setPos(prev);
        }
    }
}
