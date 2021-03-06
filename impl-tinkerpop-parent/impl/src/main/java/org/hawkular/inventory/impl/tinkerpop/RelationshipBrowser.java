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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import org.hawkular.inventory.api.EntityNotFoundException;
import org.hawkular.inventory.api.Environments;
import org.hawkular.inventory.api.Feeds;
import org.hawkular.inventory.api.MetricTypes;
import org.hawkular.inventory.api.Metrics;
import org.hawkular.inventory.api.RelationNotFoundException;
import org.hawkular.inventory.api.Relationships;
import org.hawkular.inventory.api.ResourceTypes;
import org.hawkular.inventory.api.Resources;
import org.hawkular.inventory.api.Tenants;
import org.hawkular.inventory.api.filters.Filter;
import org.hawkular.inventory.api.filters.RelationFilter;
import org.hawkular.inventory.api.filters.With;
import org.hawkular.inventory.api.model.Entity;
import org.hawkular.inventory.api.model.Environment;
import org.hawkular.inventory.api.model.Feed;
import org.hawkular.inventory.api.model.Metric;
import org.hawkular.inventory.api.model.MetricType;
import org.hawkular.inventory.api.model.Relationship;
import org.hawkular.inventory.api.model.Resource;
import org.hawkular.inventory.api.model.ResourceType;
import org.hawkular.inventory.api.model.Tenant;
import org.hawkular.inventory.api.paging.Page;
import org.hawkular.inventory.api.paging.Pager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Jirka Kremser
 * @since 0.0.1
 */
final class RelationshipBrowser extends AbstractGraphService {

    private RelationshipBrowser(InventoryContext iContext, FilterApplicator.Tree path) {
        super(iContext, path);
    }

    public static <T extends Entity<B, U>, B extends Entity.Blueprint, U extends Entity.Update>
        Relationships.Single single(InventoryContext iContext, Class<T> sourceClass, Relationships.Direction direction,
                                    FilterApplicator.Tree path, RelationFilter[] filters) {

        final Filter goToEdge = new JumpInOutFilter(direction, false);
        RelationshipBrowser b = new RelationshipBrowser(iContext, AbstractGraphService.pathWith
                (path, goToEdge).andFilter(filters).get());
        return new Relationships.Single() {

            @Override
            public Relationship entity() throws EntityNotFoundException, RelationNotFoundException {
                HawkularPipeline<?, Edge> edges = b.source().cast(Edge.class);
                if (!edges.hasNext()) {
                    throw new RelationNotFoundException(sourceClass, FilterApplicator.filters(b.sourcePaths));
                }
                Edge edge = edges.next();

                Relationship relationship = new Relationship(getEid(edge), edge.getLabel(), convert(edge
                         .getVertex(Direction.OUT)), convert(edge.getVertex(Direction.IN)));
                Map<String, Object> properties = edge.getPropertyKeys().stream().collect(Collectors.toMap(Function
                        .<String>identity(), edge::getProperty));

                Arrays.asList(RelationshipService.MAPPED_PROPERTIES).forEach(properties::remove);

                return relationship.update().with(Relationship.Update.builder().withProperties(properties).build());
            }
        };
    }

    public static Relationships.Multiple multiple(InventoryContext iContext, Relationships.Direction direction,
            FilterApplicator.Tree path, RelationFilter[] filters) {

        final Filter goToEdge = new JumpInOutFilter(direction, false);
        final Filter goFromEdge = new JumpInOutFilter(direction, true);
        RelationshipBrowser b = new RelationshipBrowser(iContext, AbstractGraphService.pathWith
                (path, goToEdge).andFilter(filters).get());

        return new Relationships.Multiple() {
            @Override
            public Page<Relationship> entities(Pager pager) {
                HawkularPipeline<?, Edge> edges = b.source().counter("total").page(pager).cast(Edge.class);

                List<String> mappedProperties = Arrays.asList(RelationshipService.MAPPED_PROPERTIES);

                Stream<Relationship> relationshipStream = StreamSupport
                        .stream(edges.spliterator(), false)
                        .map(edge -> {
                            Relationship relationship = new Relationship(getEid(edge), edge.getLabel(),
                                    convert(edge.getVertex(Direction.OUT)), convert(edge.getVertex(Direction.IN)));
                            // copy the properties
                            Map<String, Object> properties = edge.getPropertyKeys().stream()
                                    .collect(Collectors.toMap(Function.<String>identity(), edge::getProperty));

                            mappedProperties.forEach(properties::remove);

                            return relationship.update().with(Relationship.Update.builder().withProperties(properties)
                                    .build());
                        });

                return new Page<>(relationshipStream.collect(Collectors.toList()), pager, edges.getCount("total"));
            }

            @Override
            public Tenants.Read tenants() {
                Filter.Accumulator acc = Filter.by(goFromEdge, With.type(Tenant.class));
                return new TenantsService(b.context, b.pathToHereWithSelect(acc));
            }

            @Override
            public Environments.Read environments() {
                Filter.Accumulator acc = Filter.by(goFromEdge, With.type(Environment.class));
                return new EnvironmentsService(b.context, b.pathToHereWithSelect(acc));
            }

            @Override
            public Feeds.Read feeds() {
                Filter.Accumulator acc = Filter.by(goFromEdge, With.type(Feed.class));
                return new FeedsService(b.context, b.pathToHereWithSelect(acc));
            }

            @Override
            public MetricTypes.Read metricTypes() {
                Filter.Accumulator acc = Filter.by(goFromEdge, With.type(MetricType.class));
                return new MetricTypesService(b.context, b.pathToHereWithSelect(acc));
            }

            @Override
            public Metrics.Read metrics() {
                Filter.Accumulator acc = Filter.by(goFromEdge, With.type(Metric.class));
                return new MetricsService(b.context, b.pathToHereWithSelect(acc));
            }

            @Override
            public Resources.Read resources() {
                Filter.Accumulator acc = Filter.by(goFromEdge, With.type(Resource.class));
                return new ResourcesService(b.context, b.pathToHereWithSelect(acc));
            }

            @Override
            public ResourceTypes.Read resourceTypes() {
                Filter.Accumulator acc = Filter.by(goFromEdge, With.type(ResourceType.class));
                return new ResourceTypesService(b.context, b.pathToHereWithSelect(acc));
            }
        };
    }

    // filter used internally by the impl for jumping from a vertex to an edge or back
    static class JumpInOutFilter extends Filter {
        private final Relationships.Direction direction;
        private final boolean fromEdge;

        JumpInOutFilter(Relationships.Direction direction, boolean fromEdge) {
            this.direction = direction;
            this.fromEdge = fromEdge;
        }

        public Relationships.Direction getDirection() {
            return direction;
        }

        public boolean isFromEdge() {
            return fromEdge;
        }

        @Override
        public String toString() {
            return "Jump[" + (fromEdge ? "from " : "to ") + direction.name() + " edges]";
        }
    }
}
