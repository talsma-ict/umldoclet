/*
 * Copyright 2016-2019 Talsma ICT
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
/**
 * Package containing an internal minimal logging abstraction that can be easily mapped to common logging frameworks
 * (such as {@code java.util.logging} or {@code Slf4J}) but also to the Javadoc {@code Reporter}.
 *
 * <p>
 * All formal logging {@linkplain nl.talsmasoftware.umldoclet.logging.Message messages}
 * are included as enumeration to ensure a resource is provided for each of them.
 * This makes internationalization of the doclet easier and safer.
 */
package nl.talsmasoftware.umldoclet.logging;
