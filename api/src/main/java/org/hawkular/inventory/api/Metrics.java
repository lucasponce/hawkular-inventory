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

import org.hawkular.inventory.api.model.Metric;

/**
 * This is a wrapper class to hold various interfaces defining available functionality on metrics.
 *
 * @author Lukas Krejci
 * @since 1.0
 */
public final class Metrics {

    private Metrics() {

    }

    /**
     * Interface for accessing a single metric in a writable manner.
     */
    public interface Single extends ResolvableToSingleWithRelationships<Metric> {}

    /**
     * Interface for traversing over a set of metrics.
     *
     * <p>Note that traversing over a set of entities enables only read-only access. If you need to use any of the
     * modification methods, you first need to resolve the traversal to a single entity (using the
     * {@link ReadInterface#get(String)} method).
     */
    public interface Multiple extends ResolvableToManyWithRelationships<Metric> {}

    /**
     * Provides read-only access to metrics.
     */
    public interface Read extends ReadInterface<Single, Multiple> {}

    /**
     * Provides read-write access to metrics.
     */
    public interface ReadWrite extends ReadWriteInterface<Metric.Update, Metric.Blueprint, Single, Multiple> {}

    /**
     * Provides read-only access to metrics with the additional ability to relate the metrics to the current
     * position in the inventory traversal.
     */
    public interface ReadAssociate extends ReadInterface<Single, Multiple>, AssociationInterface {}
}
