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

import com.tinkerpop.blueprints.Vertex;
import org.hawkular.inventory.api.ResourceTypes;
import org.hawkular.inventory.api.filters.Filter;
import org.hawkular.inventory.api.filters.Related;
import org.hawkular.inventory.api.filters.With;
import org.hawkular.inventory.api.model.ResourceType;
import org.hawkular.inventory.api.model.Tenant;

import static org.hawkular.inventory.api.Relationships.WellKnown.contains;
import static org.hawkular.inventory.impl.tinkerpop.Constants.Type.tenant;

/**
 * @author Lukas Krejci
 * @since 0.0.1
 */
final class ResourceTypesService extends
        AbstractSourcedGraphService<ResourceTypes.Single, ResourceTypes.Multiple, ResourceType, ResourceType.Blueprint,
                ResourceType.Update> implements ResourceTypes.ReadWrite, ResourceTypes.Read {

    ResourceTypesService(InventoryContext context, PathContext ctx) {
        super(context, ResourceType.class, ctx);
    }

    @Override
    protected Filter[] initNewEntity(Vertex newEntity, ResourceType.Blueprint blueprint) {
        Vertex exampleTnt = null;
        for (Vertex t : source().hasType(tenant)) {
            addEdge(t, contains.name(), newEntity);
            exampleTnt = t;
        }

        newEntity.setProperty(Constants.Property.__version.name(), blueprint.getVersion());

        return Filter.by(With.type(Tenant.class), With.id(getEid(exampleTnt)), Related.by(contains),
                With.type(ResourceType.class), With.id(getEid(newEntity))).get();
    }

    @Override
    protected ResourceTypes.Single createSingleBrowser(FilterApplicator.Tree path) {
        return ResourceTypeBrowser.single(context, path);
    }

    @Override
    protected ResourceTypes.Multiple createMultiBrowser(FilterApplicator.Tree path) {
        return ResourceTypeBrowser.multiple(context, path);
    }

    @Override
    protected String getProposedId(ResourceType.Blueprint b) {
        return b.getId();
    }

    @Override
    protected void updateExplicitProperties(ResourceType.Update update, Vertex vertex) {
        vertex.setProperty(Constants.Property.__version.name(), update.getVersion());
    }
}
