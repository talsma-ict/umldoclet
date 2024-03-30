/*
 * Copyright 2016-2024 Talsma ICT
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
package nl.talsmasoftware.umldoclet.uml;

/**
 * To break Cyclically-dependent Modularization between Namespace and TypeName and to
 * change bidirectional association to unidirectional association this class
 * is created with contains method.
 */
public class NamespaceService {
    private NamespaceService() {
    }

    public static boolean contains(Namespace namespace, TypeName typeName) {
        return typeName != null && typeName.qualified.startsWith(namespace.name + ".");
    }
}
