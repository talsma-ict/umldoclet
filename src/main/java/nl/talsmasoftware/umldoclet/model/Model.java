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
 */
package nl.talsmasoftware.umldoclet.model;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ProgramElementDoc;

/**
 * @author Sjoerd Talsma
 */
public class Model {

    /**
     * Returns whether the the given element is deprecated;
     * it has the {@literal @}{@link Deprecated} annotation
     * or the {@literal @}deprecated JavaDoc tag.
     * <p/>
     * If the element itself is not deprecated, the method checks whether the superclass or containing class
     * is deprecated.
     *
     * @param element The element being inspected for deprecation.
     * @return {@code true} if the specified {@code element} is deprecated, {@code false} if it is not.
     */
    public static boolean isDeprecated(ProgramElementDoc element) {
        // Is the element itself deprecated?
        if (element == null) {
            return false;
        } else if (element.tags("deprecated").length > 0) {
            return true;
        }
        for (AnnotationDesc annotation : element.annotations()) {
            if (Deprecated.class.getName().equals(annotation.annotationType().qualifiedName())) {
                return true;
            }
        }
        // Element itself is not deprecated.
        // Could it be contained in a deprecated class or extend a deprecated superclass?
        return isDeprecated(element.containingClass())
                || (element instanceof ClassDoc && isDeprecated(((ClassDoc) element).superclass()));
    }

}
