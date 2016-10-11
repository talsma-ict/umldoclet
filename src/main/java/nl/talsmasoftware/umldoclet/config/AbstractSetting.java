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

package nl.talsmasoftware.umldoclet.config;

/**
 * @author Sjoerd Talsma
 */
abstract class AbstractSetting<T> {

    final String name;

    protected AbstractSetting(String name) {
        this.name = name;
    }

    public boolean matches(String option) {
        return option != null && this.name.equalsIgnoreCase(option.startsWith("-") ? option.substring(1) : option);
    }

    public abstract boolean validate(String[] option);

    public abstract T parse(String[] option, Object currentValue);

    public abstract T value(Object configured);

}
