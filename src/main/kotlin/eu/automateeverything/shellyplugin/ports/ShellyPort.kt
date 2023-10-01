/*
 * Copyright (c) 2019-2022 Tomasz Babiuk
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.automateeverything.shellyplugin.ports

import eu.automateeverything.data.hardware.PortValue
import eu.automateeverything.domain.events.EventBus
import eu.automateeverything.domain.hardware.Port
import eu.automateeverything.domain.hardware.PortCapabilities

abstract class ShellyPort<V : PortValue>(
    factoryId: String,
    adapterId: String,
    portId: String,
    eventBus: EventBus,
    valueClazz: Class<V>,
    capabilities: PortCapabilities,
    sleepInterval: Long,
    lastSeenTimestamp: Long
) : Port<V>(factoryId, adapterId, portId, eventBus, valueClazz, capabilities, sleepInterval) {

    init {
        updateLastSeenTimeStamp(lastSeenTimestamp)
    }

    abstract val readTopics: Array<String>

    abstract fun setValueFromMqttPayload(payload: String)
}

abstract class ShellyOutputPort<V : PortValue>(
    factoryId: String,
    adapterId: String,
    portId: String,
    eventBus: EventBus,
    valueClazz: Class<V>,
    capabilities: PortCapabilities,
    sleepInterval: Long,
    lastSeenTimestamp: Long
) :
    ShellyPort<V>(
        factoryId,
        adapterId,
        portId,
        eventBus,
        valueClazz,
        capabilities,
        sleepInterval,
        lastSeenTimestamp
    ) {

    abstract val writeTopic: String

    abstract fun getExecutePayload(): String?
}
