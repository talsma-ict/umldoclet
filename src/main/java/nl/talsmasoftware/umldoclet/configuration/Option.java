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

import jdk.javadoc.doclet.Doclet;

import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;

import static java.util.Objects.requireNonNull;

abstract class Option implements Doclet.Option {
    private final Configuration config;
    private final String[] names;
    private final String parameters;
    private final String description;
    private final int argCount;

    protected Option(Configuration config, String name, int argCount) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.names = name.trim().split("\\s+");
        String desc = resourceMsg(names[0] + ".description");
        if (desc.isEmpty()) {
            this.description = "<MISSING KEY>";
            this.parameters = "<MISSING KEY>";
        } else {
            this.description = desc;
            this.parameters = resourceMsg(names[0] + ".parameters");
        }
        this.argCount = argCount;
    }

    private String resourceMsg(String key) {
        try {
            return config.resources().getString(key);
        } catch (MissingResourceException ignore) {
            return "";
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Option.Kind getKind() {
        return Doclet.Option.Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList(names);
    }

    @Override
    public String getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return Arrays.toString(names);
    }

    @Override
    public int getArgumentCount() {
        return argCount;
    }

    public boolean matches(String option) {
        for (String name : names) {
            boolean matchCase = name.startsWith("--");
            if (option.startsWith("--") && option.contains("=")) {
                return name.equals(option.substring(option.indexOf("=") + 1));
            } else if (matchCase) {
                return name.equals(option);
            }
            return name.toLowerCase().equals(option.toLowerCase());
        }
        return false;
    }

}