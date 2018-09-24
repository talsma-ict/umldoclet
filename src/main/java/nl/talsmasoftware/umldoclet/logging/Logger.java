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

/**
 * Basic {@code Logger} interface to avoid dependencies on the {@code jdk.logger} module or {@code Slf4J}.
 *
 * @author Sjoerd Talsma
 */
public interface Logger {

    void debug(Message key, Object... args);

    void info(Message key, Object... args);

    void warn(Message key, Object... args);

    void error(Message key, Object... args);

}
