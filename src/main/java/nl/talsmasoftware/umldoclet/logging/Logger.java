/*
 * Copyright 2016-2026 Talsma ICT
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
package nl.talsmasoftware.umldoclet.logging;

/// Basic `Logger` interface to avoid dependencies on the `jdk.logger` module or `Slf4J`.
///
/// @author Sjoerd Talsma
public interface Logger {

    /// Localizes a message using the logger's locale.
    ///
    /// @param key  The message key to localize.
    /// @param args The arguments to format the message with.
    /// @return The localized and formatted message.
    String localize(Message key, Object... args);

    /// Logs a debug message.
    ///
    /// @param message The message to log.
    /// @param args    The arguments to format the message with.
    void debug(Object message, Object... args);

    /// Logs an informational message.
    ///
    /// @param key  The message key to log.
    /// @param args The arguments to format the message with.
    void info(Message key, Object... args);

    /// Logs a warning message.
    ///
    /// @param key  The message key to log.
    /// @param args The arguments to format the message with.
    void warn(Message key, Object... args);

    /// Logs an error message.
    ///
    /// @param key  The message key to log.
    /// @param args The arguments to format the message with.
    void error(Message key, Object... args);

}
