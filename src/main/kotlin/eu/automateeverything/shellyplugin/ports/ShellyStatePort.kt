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

import eu.automateeverything.domain.events.EventBus
import eu.automateeverything.domain.hardware.BinaryInput
import eu.automateeverything.domain.hardware.PortCapabilities
import eu.automateeverything.shellyplugin.StateBriefDto

class ShellyStatePort(
    factoryId: String,
    adapterId: String,
    portId: String,
    eventBus: EventBus,
    sleepInterval: Long,
    lastSeenTimestamp: Long,
    shellyId: String
) :
    ShellyPort<BinaryInput>(
        factoryId,
        adapterId,
        portId,
        eventBus,
        BinaryInput::class.java,
        PortCapabilities(canRead = true, canWrite = false),
        sleepInterval,
        lastSeenTimestamp
    ) {

    private var value = BinaryInput(false)
    override val readTopics = arrayOf("shellies/$shellyId/sensor/state")

    override fun readInternal(): BinaryInput {
        return value
    }

    override fun setValueFromMqttPayload(payload: String) {
        value = BinaryInput(payload == "open")
    }

    fun setValueFromStateResponse(stateBrief: StateBriefDto) {
        value = BinaryInput(stateBrief.state == "open")
    }
}
