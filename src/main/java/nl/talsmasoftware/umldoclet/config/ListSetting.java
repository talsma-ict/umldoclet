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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class ListSetting extends AbstractSetting<List<String>> {

    public ListSetting(String name) {
        super(name);
    }

    @Override
    public List<String> parse(String[] option, List<String> currentValue) {
        List<String> values = new ArrayList<>();
        if (option.length > 1) {
            if (currentValue != null) {
                for (Object val : currentValue) {
                    values.add(Objects.toString(val));
                }
            }
            for (int i = 1; i < option.length; i++) {
                for (String part : option[i].split("\\s*[,;\\n]\\s*")) {
                    if (!part.isEmpty()) {
                        values.add(part);
                    }
                }
            }
        }
        return values.isEmpty() ? currentValue : values;
    }

}
