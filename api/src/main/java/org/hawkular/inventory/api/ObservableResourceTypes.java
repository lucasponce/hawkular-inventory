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
package org.hawkular.inventory.api;

import org.hawkular.inventory.api.model.ResourceType;

import java.util.function.BiFunction;

/**
 * @author Lukas Krejci
 * @since 0.0.1
 */
final class ObservableResourceTypes {

    private ObservableResourceTypes() {

    }

    static final class Read
            extends ObservableBase.Read<ResourceTypes.Single, ResourceTypes.Multiple, ResourceTypes.Read>
            implements ResourceTypes.Read {


        Read(ResourceTypes.Read wrapped, ObservableContext context) {
            super(wrapped, context);
        }

        @Override
        protected BiFunction<ResourceTypes.Single, ObservableContext, ? extends ResourceTypes.Single> singleCtor() {
            return ObservableResourceTypes.Single::new;
        }

        @Override
        protected BiFunction<ResourceTypes.Multiple, ObservableContext, ? extends ResourceTypes.Multiple>
        multipleCtor() {

            return ObservableResourceTypes.Multiple::new;
        }
    }

    static final class ReadWrite
            extends ObservableBase.ReadWrite<ResourceType, ResourceType.Blueprint, ResourceType.Update,
            ResourceTypes.Single, ResourceTypes.Multiple, ResourceTypes.ReadWrite>
            implements ResourceTypes.ReadWrite {


        ReadWrite(ResourceTypes.ReadWrite wrapped, ObservableContext context) {
            super(wrapped, context);
        }

        @Override
        protected BiFunction<ResourceTypes.Single, ObservableContext, ? extends ResourceTypes.Single> singleCtor() {
            return ObservableResourceTypes.Single::new;
        }

        @Override
        protected BiFunction<ResourceTypes.Multiple, ObservableContext, ? extends ResourceTypes.Multiple>
        multipleCtor() {

            return ObservableResourceTypes.Multiple::new;
        }
    }

    static final class Single extends ObservableBase.RelatableSingle<ResourceType, ResourceTypes.Single>
            implements ResourceTypes.Single {

        Single(ResourceTypes.Single wrapped, ObservableContext context) {
            super(wrapped, context);
        }

        @Override
        public ObservableResources.Read resources() {
            return wrap(ObservableResources.Read::new, wrapped.resources());
        }

        @Override
        public ObservableMetricTypes.ReadAssociate metricTypes() {
            return wrap(ObservableMetricTypes.ReadAssociate::new, wrapped.metricTypes());
        }
    }

    static final class Multiple extends ObservableBase.RelatableMultiple<ResourceType, ResourceTypes.Multiple>
            implements ResourceTypes.Multiple {

        Multiple(ResourceTypes.Multiple wrapped, ObservableContext context) {
            super(wrapped, context);
        }

        @Override
        public ObservableResources.Read resources() {
            return wrap(ObservableResources.Read::new, wrapped.resources());
        }

        @Override
        public ObservableMetricTypes.Read metricTypes() {
            return wrap(ObservableMetricTypes.Read::new, wrapped.metricTypes());
        }
    }
}
