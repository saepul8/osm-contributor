/**
 * Copyright (C) 2019 Takima
 * <p>
 * This file is part of OSM Contributor.
 * <p>
 * OSM Contributor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * OSM Contributor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with OSM Contributor.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.jawg.osmcontributor.model.events;

import java.util.List;

import io.jawg.osmcontributor.model.entities.relation_display.RelationDisplay;

public class BusLinesSuggestionForPoiLoadedEvent {
    private List<RelationDisplay> relationDisplays;

    public BusLinesSuggestionForPoiLoadedEvent(List<RelationDisplay> relationDisplays) {
        this.relationDisplays = relationDisplays;
    }

    public List<RelationDisplay> getRelationDisplays() {
        return relationDisplays;
    }
}
