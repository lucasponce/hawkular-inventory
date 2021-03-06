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

import org.hawkular.inventory.api.filters.Filter;

/**
 * Represents a position in the inventory traversal together with the list of filters that limit the possible
 * elements represented by a filtering service.
 *
 * @author Lukas Krejci
 * @since 0.0.1
 */
final class PathContext {

    final FilterApplicator.Tree sourcePath;

    final Filter[][] candidatesFilters;

    PathContext(FilterApplicator.Tree sourcePath, Filter[][] candidatesFilters) {
        if (candidatesFilters == null) {
            this.candidatesFilters = new Filter[0][];
        } else {
            this.candidatesFilters = candidatesFilters;
        }
        this.sourcePath = sourcePath;
    }

    PathContext(FilterApplicator.Tree sourcePath, Filter... candidatesFilters) {
        if (candidatesFilters == null) {
            this.candidatesFilters = new Filter[0][];
        } else {
            Filter[][] fs = new Filter[1][];
            fs[0] = candidatesFilters;
            this.candidatesFilters = fs;
        }
        this.sourcePath = sourcePath;
    }
}
