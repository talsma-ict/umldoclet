/*
 * Copyright 2016-2017 Talsma ICT
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
 */
package nl.talsmasoftware.umldoclet.v1.logging;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.SourcePosition;
import jdk.javadoc.doclet.Reporter;
import nl.talsmasoftware.umldoclet.configuration.DocletConfig;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.logging.Message;

import javax.tools.Diagnostic;
import java.text.MessageFormat;

import static java.lang.Character.toLowerCase;

/**
 * Simple support class for logging towards a configured {@link DocErrorReporter}, while still being able to use
 * convenient logging methods and having to keep track of the <code>position()</code> everywhere in the code.
 * This is backed by a regular old-fashioned 'java.util.logging.Logger' instance.
 * The 'doclet-global' configured log-level is also maintained in this logger instance, allowing us a convenient way
 * to test whether we must actually write to the reporter or not.
 * Furthermore, it is also used as a 'backup' output when there is no reporter registered (yet). This should not happen
 * during doclet execution but may occur in exceptional situations during startup.
 *
 * @author Sjoerd Talsma
 * @deprecated switch from logger to reporter semantics.
 */
public class LogSupport {
//    private static final Logger LOGGER = Logger.getLogger(LogSupport.class.getName());

    private static volatile Reporter reporter;

    // Compatible logger with the registered reporter.
    public static final Logger LOGGER = new Logger() {
        private void log(Diagnostic.Kind kind, Message key, Object... args) {
            if (reporter != null) {
                String message = key.toString();
                if (args.length > 0) message = MessageFormat.format(message, args);
                reporter.print(kind, message);
            }
        }

        public void debug(Message key, Object... args) {
            log(Diagnostic.Kind.OTHER, key, args);
        }

        public void info(Message key, Object... args) {
            log(Diagnostic.Kind.NOTE, key, args);
        }

        public void warn(Message key, Object... args) {
            log(Diagnostic.Kind.WARNING, key, args);
        }

        public void error(Message key, Object... args) {
            log(Diagnostic.Kind.ERROR, key, args);
        }
    };

    public static void setReporter(Reporter reporter) {
        LogSupport.reporter = reporter;
    }

    /**
     * No longer does anything, please use {@link DocletConfig Configuration}
     * log methods instead.
     *
     * @param level ignored
     * @deprecated switch from logger to reporter semantics.
     */
    public static void setLevel(Object level) {
        // No-op, please switch to Reporter semantics!
    }

    private static String format(String msg, Object... args) {
        return MessageFormat.format(msg, args);
    }

    /**
     * No longer does anything, please use {@link DocletConfig Configuration}
     * log methods instead.
     *
     * @return Always {@code false}
     * @deprecated Switch from logger to reporter semantics.
     */
    public static boolean isTraceEnabled() {
        return false;
    }

    /**
     * No longer does anything, please use {@link DocletConfig Configuration}
     * log methods instead.
     *
     * @param msg  ignored
     * @param args ignored
     * @deprecated Switch from logger to reporter semantics.
     */
    public static void trace(String msg, Object... args) {
        // No-op, please switch to Reporter semantics!
    }

    /**
     * Debugs the message, arguments will replace message placeholders like <code>"{0}"</code>, <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     * @deprecated Switch from logger to reporter semantics.
     */
    public static void debug(String msg, Object... args) {
        if (reporter != null) reporter.print(Diagnostic.Kind.OTHER, format(msg, args));
    }

    /**
     * Prints the message as INFO statement, arguments will replace message placeholders like <code>"{0}"</code>,
     * <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     * @deprecated Switch from logger to reporter semantics.
     */
    public static void info(String msg, Object... args) {
        if (reporter != null) reporter.print(Diagnostic.Kind.NOTE, format(msg, args));
    }

    /**
     * Prints the message as WARNING statement, arguments will replace message placeholders like <code>"{0}"</code>,
     * <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     * @deprecated Switch from logger to reporter semantics.
     */
    public static void warn(String msg, Object... args) {
        if (reporter != null) reporter.print(Diagnostic.Kind.WARNING, format(msg, args));
    }

    /**
     * Prints the error, arguments will replace message placeholders like <code>"{0}"</code>, <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     * @deprecated Switch from logger to reporter semantics.
     */
    public static void error(String msg, Object... args) {
        if (reporter != null) reporter.print(Diagnostic.Kind.ERROR, format(msg, args));
    }

    /**
     * Reporter alias method for {@link #info(String, Object...)}
     *
     * @param msg The message (without parameters).
     * @see #info(String, Object...)
     * @deprecated Switch from logger to reporter semantics.
     */
    public void printNotice(String msg) {
        info(msg);
    }

    /**
     * Reporter alias method for {@link #warn(String, Object...)}
     *
     * @param msg The message (without parameters).
     * @see #warn(String, Object...)
     * @deprecated Switch from logger to reporter semantics.
     */
    public void printWarning(String msg) {
        warn(msg);
    }

    /**
     * Reporter alias method for {@link #error(String, Object...)}
     *
     * @param msg The message (without parameters).
     * @see #error(String, Object...)
     * @deprecated Switch from logger to reporter semantics.
     */
    public void printError(String msg) {
        error(msg);
    }

    /**
     * @param pos The source position
     * @param msg The message
     * @deprecated Switch from logger to reporter semantics.
     */
    public void printNotice(SourcePosition pos, String msg) {
        try (GlobalPosition gp = new GlobalPosition(pos)) {
            info(msg);
        }
    }

    /**
     * @param pos The source position
     * @param msg The message
     * @deprecated Switch from logger to reporter semantics.
     */
    public void printWarning(SourcePosition pos, String msg) {
        try (GlobalPosition gp = new GlobalPosition(pos)) {
            warn(msg);
        }
    }

    /**
     * @param pos The source position
     * @param msg The message
     * @deprecated Switch from logger to reporter semantics.
     */
    public void printError(SourcePosition pos, String msg) {
        try (GlobalPosition gp = new GlobalPosition(pos)) {
            error(msg);
        }
    }

    /**
     * This method finds the last exception (actually, {@link Throwable}) from the given arguments.
     *
     * @param args The arguments to inspect whether one of them is a <code>Throwable</code> object.
     * @return The <code>Throwable</code> or <code>null</code> if no throwable objects were given.
     */
    private static Throwable findException(Object... args) {
        for (int i = args.length - 1; i >= 0; i--) {
            if (args[i] instanceof Throwable) {
                return (Throwable) args[i];
            }
        }
        return null;
    }

    /**
     * This method concatenates the specified parts and separates them by a single space, while making sure that
     * all but the first non-empty, non-null part starts with a lowercase letter.
     *
     * @param parts The parts to concatenate. Nulls and empty string will be skipped.
     * @return The concatenated string.
     */
    public static String concatLowercaseParts(String... parts) {
        StringBuilder result = new StringBuilder();
        if (parts != null) for (String part : parts) {
            part = part != null ? part.trim() : "";
            if (part.length() > 0) {
                if (result.length() == 0) result.append(part);
                else result.append(' ').append(toLowerCase(part.charAt(0))).append(part.substring(1));
            }
        }
        return result.toString();
    }

}
