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

import nl.talsmasoftware.umldoclet.logging.LogSupport;

import java.util.*;

import static java.util.Collections.*;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class ListSetting extends AbstractSetting<List<String>> {

    private final List<String> defaultValue;

    public ListSetting(String name, String... defaultValue) {
        super(name);
        this.defaultValue = unmodifiableCopy(split(false, defaultValue));
    }

    @Override
    public boolean validate(String[] option) {
        if (option.length < 2) {
            LogSupport.error("Expected at least {0} but received {1} values: {2}.", 2, option.length, Arrays.toString(option));
            return false;
        }
        return true;
    }

    @Override
    public List<String> parse(String[] option, Object currentValue) {
        List<String> values = new ArrayList<>();
        if (option.length > 1) {
            if (currentValue instanceof Iterable) {
                for (Object val : (Iterable<?>) currentValue) {
                    values.add(Objects.toString(val));
                }
            }
            values.addAll(split(true, option));
        }
        if (values.isEmpty()) {
            return value(currentValue);
        }
        return unmodifiableCopy(values);
    }

    @Override
    public List<String> value(Object configured) {
        return configured instanceof List ? (List<String>) configured : defaultValue;
    }

    private static List<String> split(boolean skipfirst, String... values) {
        List<String> result = new ArrayList<>();
        boolean skip = skipfirst;
        for (String val : values) {
            if (!skip) {
                for (String part : val.split("\\s*[,;\\n]\\s*")) {
                    result.add(part);
                }
            }
            skip = false;
        }
        return result;
    }

    private static List<String> unmodifiableCopy(Collection<?> source) {
        if (source == null) {
            return null;
        } else if (source.isEmpty()) {
            return emptyList();
        }
        List<String> copy = new ArrayList<>();
        for (Object value : source) {
            if (value != null) {
                copy.add(value.toString());
            }
        }
        return copy.size() == 1 ? singletonList(copy.get(0)) : unmodifiableList(copy);
    }

}
