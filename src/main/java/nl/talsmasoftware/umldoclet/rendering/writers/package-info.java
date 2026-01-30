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
/// Package for convenience {@linkplain java.io.Writer} implementations.
///
/// <ul>
/// <li>A generic {@linkplain nl.talsmasoftware.umldoclet.rendering.writers.DelegatingWriter} that can <em>delegate</em>
/// writing to one or more delegate writers.
/// <li>A delegating {@linkplain nl.talsmasoftware.umldoclet.rendering.writers.StringBufferingWriter} that provides
/// access to a {@linkplain java.lang.StringBuffer} of everything that has been written so-far.
/// This class is concurrently <strong>unsafe</strong>; i.e. should not be used from multiple threads.
/// </ul>
package nl.talsmasoftware.umldoclet.rendering.writers;
