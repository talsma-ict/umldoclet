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
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class IntegerSetting extends AbstractSetting<Integer> {

    private final int defaultValue;

    public IntegerSetting(String name, int defaultValue) {
        super(name);
        this.defaultValue = defaultValue;
    }

    @Override
    public Integer parse(String[] option, Integer currentValue) {
        return option.length > 1 ? Integer.valueOf(option[1]) : currentValue;
    }
}
