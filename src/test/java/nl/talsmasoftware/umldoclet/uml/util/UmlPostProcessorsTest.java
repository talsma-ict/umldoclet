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
package nl.talsmasoftware.umldoclet.uml.util;

import nl.talsmasoftware.umldoclet.uml.Field;
import nl.talsmasoftware.umldoclet.uml.Method;
import nl.talsmasoftware.umldoclet.uml.Namespace;
import nl.talsmasoftware.umldoclet.uml.Type;
import nl.talsmasoftware.umldoclet.uml.Type.Classification;
import nl.talsmasoftware.umldoclet.uml.TypeName;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class UmlPostProcessorsTest {
    private static final Namespace UNNAMED = new Namespace(null, "");

    @Test
    public void testJavaBeanPropertiesAsFieldsPostProcessorAcceptsNull() {
        UmlPostProcessors.javaBeanPropertiesAsFieldsPostProcessor().accept(null);
    }

    @Test
    public void testJavaBeanPropertiesAsFieldsPostProcessorAcceptsEmptyType() {
        Type emptyType = new Type(UNNAMED, Classification.CLASS, typeName("EmptyType"));
        UmlPostProcessors.javaBeanPropertiesAsFieldsPostProcessor().accept(emptyType);
        assertThat(emptyType.getPackagename(), equalTo(""));
        assertThat(emptyType.getClassfication(), is(Classification.CLASS));
        assertThat(emptyType.getName(), equalTo(typeName("EmptyType")));
        assertThat(emptyType.getChildren(), is(empty()));
    }

    @Test
    public void testJavaBeanPropertiesAsFielsPostProcessorSimpleAccessors() {
        Type simpleBean = new Type(UNNAMED, Classification.CLASS, typeName("SimpleBean"));
        Method getter = new Method(simpleBean, "getStringValue", typeName("java.lang.String"));
        Method setter = new Method(simpleBean, "setStringValue", null);
        setter.addParameter("stringValue", typeName("java.lang.String"));
        simpleBean.addChild(getter);
        simpleBean.addChild(setter);

        UmlPostProcessors.javaBeanPropertiesAsFieldsPostProcessor().accept(simpleBean);
        assertThat(simpleBean.getChildren(), hasSize(1));
        assertThat(simpleBean.getChildren().get(0), instanceOf(Field.class));
    }

    private static TypeName typeName(String qualified) {
        int lastDot = qualified.lastIndexOf('.');
        String simpleName = lastDot >= 0 ? qualified.substring(lastDot + 1) : qualified;
        return new TypeName(simpleName, qualified);
    }
}
