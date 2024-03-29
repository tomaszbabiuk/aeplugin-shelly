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
import eu.automateeverything.domain.hardware.PortCapabilities
import eu.automateeverything.domain.hardware.Relay
import eu.automateeverything.shellyplugin.RelayResponseDto
import java.math.BigDecimal

class ShellyRelayPort(
    factoryId: String,
    adapterId: String,
    portId: String,
    eventBus: EventBus,
    sleepInterval: Long,
    lastSeenTimestamp: Long,
    shellyId: String,
    channel: Int
) :
    ShellyOutputPort<Relay>(
        factoryId,
        adapterId,
        portId,
        eventBus,
        Relay::class.java,
        PortCapabilities(canRead = true, canWrite = true),
        sleepInterval,
        lastSeenTimestamp
    ) {

    private var readValue = Relay(false)
    override val readTopics = arrayOf("shellies/$shellyId/relay/$channel")
    override val writeTopic = "shellies/$shellyId/relay/$channel/command"

    override fun readInternal(): Relay {
        if (requestedValue != null) {
            return requestedValue!!
        }
        return readValue
    }

    override fun setValueFromMqttPayload(payload: String) {
        readValue = Relay(if (payload == "on") BigDecimal.ONE else BigDecimal.ZERO)
    }

    override fun getExecutePayload(): String? {
        if (requestedValue == null) {
            return null
        }

        return if (requestedValue!!.value == BigDecimal.ONE) "on" else "off"
    }

    fun setValueFromRelayResponse(response: RelayResponseDto) {
        readValue = Relay(if (response.ison) BigDecimal.ONE else BigDecimal.ZERO)
    }
}
