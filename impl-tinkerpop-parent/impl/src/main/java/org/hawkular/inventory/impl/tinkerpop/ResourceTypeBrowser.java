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

package org.hawkular.inventory.impl.tinkerpop;

import org.hawkular.inventory.api.EntityNotFoundException;
import org.hawkular.inventory.api.MetricTypes;
import org.hawkular.inventory.api.RelationNotFoundException;
import org.hawkular.inventory.api.Relationships;
import org.hawkular.inventory.api.ResourceTypes;
import org.hawkular.inventory.api.Resources;
import org.hawkular.inventory.api.filters.Filter;
import org.hawkular.inventory.api.filters.Related;
import org.hawkular.inventory.api.filters.With;
import org.hawkular.inventory.api.model.MetricType;
import org.hawkular.inventory.api.model.Resource;
import org.hawkular.inventory.api.model.ResourceType;
import org.hawkular.inventory.api.paging.Page;
import org.hawkular.inventory.api.paging.Pager;

import static org.hawkular.inventory.api.Relationships.WellKnown.defines;
import static org.hawkular.inventory.api.Relationships.WellKnown.owns;

/**
 * @author Lukas Krejci
 * @since 0.0.1
 */
final class ResourceTypeBrowser extends AbstractBrowser<ResourceType, ResourceType.Blueprint, ResourceType.Update> {
    private ResourceTypeBrowser(InventoryContext context, FilterApplicator.Tree path) {
        super(context, ResourceType.class, path);
    }

    public static ResourceTypes.Single single(InventoryContext context, FilterApplicator.Tree path) {
        ResourceTypeBrowser b = new ResourceTypeBrowser(context, path);

        return new ResourceTypes.Single() {

            @Override
            public ResourceType entity() throws EntityNotFoundException, RelationNotFoundException {
                return b.entity();
            }

            @Override
            public Relationships.ReadWrite relationships() {
                return b.relationships();
            }

            @Override
            public Relationships.ReadWrite relationships(Relationships.Direction direction) {
                return b.relationships(direction);
            }

            @Override
            public Resources.Read resources() {
                return b.resources();
            }

            @Override
            public MetricTypes.ReadAssociate metricTypes() {
                return b.metricTypes();
            }
        };
    }

    public static ResourceTypes.Multiple multiple(InventoryContext context, FilterApplicator.Tree path) {
        ResourceTypeBrowser b = new ResourceTypeBrowser(context, path);

        return new ResourceTypes.Multiple() {
            @Override
            public MetricTypes.Read metricTypes() {
                return b.metricTypes();
            }

            @Override
            public Resources.Read resources() {
                return b.resources();
            }

            @Override
            public Relationships.Read relationships() {
                return b.relationships();
            }

            @Override
            public Relationships.Read relationships(Relationships.Direction direction) {
                return b.relationships(direction);
            }

            @Override
            public Page<ResourceType> entities(Pager pager) {
                return b.entities(pager);
            }
        };
    }

    private Resources.Read resources() {
        return new ResourcesService(context, pathToHereWithSelect(Filter.by(Related.by(defines),
                With.type(Resource.class))));
    }

    private MetricTypes.ReadAssociate metricTypes() {
        return new MetricTypesService(context, pathToHereWithSelect(Filter.by(Related.by(owns),
                With.type(MetricType.class))));
    }
}
