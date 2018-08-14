/*
 * Copyright 2016-2018 Talsma ICT
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

import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

public class TestLogger implements Logger {
    public static final class LogRecord {
        public final Level level;
        public final Message message;
        public final List<Object> arguments;

        private LogRecord(Level level, Message key, Object... args) {
            this.level = level;
            this.message = key;
            this.arguments = asList(args);
        }
    }

    public List<LogRecord> logged = new ArrayList<>();

    public int countMessages(Predicate<Message> predicate) {
        return (int) logged.stream().filter(record -> predicate.test(record.message)).count();
    }

    @Override
    public void debug(Message key, Object... args) {
        logged.add(new LogRecord(Level.DEBUG, key, args));
    }

    @Override
    public void info(Message key, Object... args) {
        logged.add(new LogRecord(Level.INFO, key, args));
    }

    @Override
    public void warn(Message key, Object... args) {
        logged.add(new LogRecord(Level.WARN, key, args));
    }

    @Override
    public void error(Message key, Object... args) {
        logged.add(new LogRecord(Level.ERROR, key, args));
    }

}
