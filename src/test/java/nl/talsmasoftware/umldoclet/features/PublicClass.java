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
package nl.talsmasoftware.umldoclet.features;

@SuppressWarnings("unused")
public class PublicClass {
    private String privateField;
    String packageProtectedField;
    protected String protectedField;
    public String publicField;

    public PublicClass() {
        this.privateField = packageProtectedField = protectedField = publicField = null;
    }

    private String getPrivateValue() {
        return privateField;
    }

    String getPackageProtectedValue() {
        return packageProtectedField;
    }

    protected String getProtectedValue() {
        return protectedField;
    }

    public String getPublicValue() {
        return publicField;
    }

    private static final class PrivateInnerClass {
    }

    static final class PackageProtectedInnerClass {
    }

    protected static final class ProtectedInnerClass {
    }

    public static final class PublicInnerClass {
    }
}
