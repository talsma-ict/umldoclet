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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 */
public class LogSupport implements DocErrorReporter {
    private static final Logger LOGGER = Logger.getLogger(LogSupport.class.getName());

    private static volatile DocErrorReporter reporter;

    public static void setReporter(DocErrorReporter reporter) {
        LogSupport.reporter = reporter;
    }

    /**
     * Sets the loglevel for the doclet.
     * This supports both the 'olg.slf4j' style levels as the 'java.util.logging' style levels and breaks them up into the
     * following 'java.util.logging' levels:
     * <ol>
     * <li><b><code>FINEST</code></b>, <code>ALL</code>, <code>TRACE</code></li>
     * <li><b><code>FINER</code></b></li>
     * <li><b><code>FINE</code></b>, <code>DEBUG</code></li>
     * <li><b><code>CONFIG</code></b></li>
     * <li><b><code>INFO</code></b></li>
     * <li><b><code>WARNING</code></b>, <code>WARN</code></li>
     * <li><b><code>SEVERE</code></b>, <code>ERROR</code>, <code>FATAL</code></li>
     * <li><b><code>OFF</code></b></li>
     * </ol>
     *
     * @param level The level to set the log support to (or <code>null</code> to default back to INFO).
     */
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

    /**
     * Formats the log message according to the {@link MessageFormat} class, so numbered placeholders are replaced by
     * paramters like: <code>{0}</code>, <code>{1}</code>, etc.
     *
     * @param msg  The message, possibly containing placeholders.
     * @param args The parameters to replace placeholders, if any.
     * @return The formatted message.
     */
    private static String format(String msg, Object... args) {
        return MessageFormat.format(msg, args);
    }

    /**
     * @return Whether trace logging is enabled.
     */
    public static boolean isTraceEnabled() {
        return LOGGER.isLoggable(Level.FINEST);
    }

    /**
     * Traces the message, arguments will replace message placeholders like <code>"{0}"</code>, <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     */
    public static void trace(String msg, Object... args) {
        if (isTraceEnabled()) {
            if (reporter == null) {
                LOGGER.log(Level.FINEST, format(msg, args), findException(args));
            } else {
                reporter.printNotice(GlobalPosition.current(), format(msg, args));
            }
        }
    }

    /**
     * Debugs the message, arguments will replace message placeholders like <code>"{0}"</code>, <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     */
    public static void debug(String msg, Object... args) {
        if (LOGGER.isLoggable(Level.FINE)) {
            if (reporter == null) {
                LOGGER.log(Level.FINE, format(msg, args), findException(args));
            } else {
                reporter.printNotice(GlobalPosition.current(), format(msg, args));
            }
        }
    }

    /**
     * Prints the message as INFO statement, arguments will replace message placeholders like <code>"{0}"</code>,
     * <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     */
    public static void info(String msg, Object... args) {
        if (LOGGER.isLoggable(Level.INFO)) {
            if (reporter == null) {
                LOGGER.log(Level.INFO, format(msg, args), findException(args));
            } else {
                reporter.printNotice(GlobalPosition.current(), format(msg, args));
            }
        }
    }

    /**
     * Prints the message as WARNING statement, arguments will replace message placeholders like <code>"{0}"</code>,
     * <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     */
    public static void warn(String msg, Object... args) {
        if (LOGGER.isLoggable(Level.WARNING)) {
            if (reporter == null) {
                LOGGER.log(Level.WARNING, format(msg, args), findException(args));
            } else {
                reporter.printWarning(GlobalPosition.current(), format(msg, args));
            }
        }
    }

    /**
     * Prints the error, arguments will replace message placeholders like <code>"{0}"</code>, <code>"{1}"</code>, etc.
     * (See {@link MessageFormat} for more details).
     *
     * @param msg  The message or message pattern in case of arguments.
     * @param args The message arguments, if any.
     * @see MessageFormat
     */
    public static void error(String msg, Object... args) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
            if (reporter == null) {
                LOGGER.log(Level.SEVERE, format(msg, args), findException(args));
            } else {
                reporter.printError(GlobalPosition.current(), format(msg, args));
            }
        }
    }

    /**
     * Reporter alias method for {@link #info(String, Object...)}
     *
     * @param msg The message (without parameters).
     * @see #info(String, Object...)
     */
    @Override
    public void printNotice(String msg) {
        info(msg);
    }

    /**
     * Reporter alias method for {@link #warn(String, Object...)}
     *
     * @param msg The message (without parameters).
     * @see #warn(String, Object...)
     */
    @Override
    public void printWarning(String msg) {
        warn(msg);
    }

    /**
     * Reporter alias method for {@link #error(String, Object...)}
     *
     * @param msg The message (without parameters).
     * @see #error(String, Object...)
     */
    @Override
    public void printError(String msg) {
        error(msg);
    }

    /**
     * Reporter alias method for {@link #info(String, Object...)} with position info.
     *
     * @param msg The message (without parameters).
     * @see #info(String, Object...)
     */
    @Override
    public void printNotice(SourcePosition pos, String msg) {
        try (GlobalPosition gp = new GlobalPosition(pos)) {
            info(msg);
        }
    }

    /**
     * Reporter alias method for {@link #warn(String, Object...)} with position info.
     *
     * @param msg The message (without parameters).
     * @see #warn(String, Object...)
     */
    @Override
    public void printWarning(SourcePosition pos, String msg) {
        try (GlobalPosition gp = new GlobalPosition(pos)) {
            warn(msg);
        }
    }

    /**
     * Reporter alias method for {@link #error(String, Object...)} with position info.
     *
     * @param msg The message (without parameters).
     * @see #error(String, Object...)
     */
    @Override
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
