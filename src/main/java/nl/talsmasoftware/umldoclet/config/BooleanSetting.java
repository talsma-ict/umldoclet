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

import java.util.Arrays;

/**
 * @author Sjoerd Talsma
 */
class BooleanSetting extends AbstractSetting<Boolean> {

    private final boolean defaultValue;

    BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean validate(String[] option) {
        if (option.length != 2) {
            LogSupport.error("Expected {0} but received {1} values: {2}.", 2, option.length, Arrays.toString(option));
            return false;
        } else if (!"true".equalsIgnoreCase(option[1]) && !"false".equalsIgnoreCase(option[1])) {
            LogSupport.error("Expected boolean value, but got \"{0}\" for option \"{1}\".", option[1], option[0]);
            return false;
        }
        return true;
    }

    @Override
    public Boolean parse(String[] option, Object currentValue) {
        // TODO: Error reporting!
        return option.length > 1 ? Boolean.valueOf(option[1]) : value(currentValue);
    }

    @Override
    public Boolean value(Object configured) {
        return configured instanceof Boolean ? (Boolean) configured
                : defaultValue;
    }

}
