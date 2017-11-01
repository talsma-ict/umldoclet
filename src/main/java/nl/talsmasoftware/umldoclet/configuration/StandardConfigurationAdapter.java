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
package nl.talsmasoftware.umldoclet.configuration;

/**
 * Class that serves as an 'anti-corruption' adapter between our Doclet
 * and the {@code internal} configuration of the StandardDoclet.
 * <p>
 * Although we want to use (and where possible adapt to) the standard
 * configuration, we don't want to re-implement all standard options.
 * <p>
 * However, we also don't want to have the UML Doclet blow up in our
 * face at runtime when the standard configuration implementation changes.
 *
 * @author Sjoerd Talsma
 */
public class StandardConfigurationAdapter {
}
