/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.inventory.api.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all Hawkular entities.
 *
 * @author Lukas Krejci
 * @since 1.0
 */
public abstract class Entity<B extends Entity.Blueprint, U extends AbstractElement.Update>
        extends AbstractElement<B, U> {

    /** JAXB support */
    Entity() {
    }

    Entity(String id) {
        this(id, null);
    }

    Entity(String id, Map<String, Object> properties) {
        super(id, properties);
    }

    /**
     * Accepts the provided visitor.
     *
     * @param visitor the visitor to visit this entity
     * @param parameter the parameter to pass on to the visitor
     * @param <R> the return type
     * @param <P> the type of the parameter
     * @return the return value provided by the visitor
     */
    public abstract <R, P> R accept(EntityVisitor<R, P> visitor, P parameter);

    /**
     * Use this to append additional information to the string representation of this instance
     * returned from the (final) {@link #toString()}.
     *
     * <p>Generally, one should call the super method first and then only add additional information
     * to the builder.
     *
     * @param toStringBuilder the builder to append stuff to.
     */
    protected void appendToString(StringBuilder toStringBuilder) {

    }


    @Override
    public final String toString() {
        StringBuilder bld = new StringBuilder(getClass().getSimpleName());
        bld.append("[id='").append(getId()).append('\'');
        appendToString(bld);
        bld.append(']');

        return bld.toString();
    }

    public abstract static class Blueprint {
        @XmlAttribute
        private final String id;

        @XmlElement
        private final Map<String, Object> properties;

        protected Blueprint(String id, Map<String, Object> properties) {
            this.id = id;
            this.properties = properties;
        }

        public String getId() {
            return id;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public abstract static class Builder<B, This extends Builder<B, This>> {
            protected String id;
            protected Map<String, Object> properties = new HashMap<>();

            public This withId(String id) {
                this.id = id;
                return castThis();
            }

            public This withProperty(String key, Object value) {
                this.properties.put(key, value);
                return castThis();
            }

            public This withProperties(Map<String, Object> properties) {
                this.properties.putAll(properties);
                return castThis();
            }

            public abstract B build();

            @SuppressWarnings("unchecked")
            protected This castThis() {
                return (This) this;
            }
        }
    }
}
