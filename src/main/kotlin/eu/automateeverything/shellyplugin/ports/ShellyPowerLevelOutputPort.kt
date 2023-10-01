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

import com.google.gson.Gson
import eu.automateeverything.domain.events.EventBus
import eu.automateeverything.domain.hardware.PortCapabilities
import eu.automateeverything.domain.hardware.PowerLevel
import eu.automateeverything.shellyplugin.LightBriefDto
import eu.automateeverything.shellyplugin.LightSetDto
import java.math.BigDecimal

class ShellyPowerLevelOutputPort(
    factoryId: String,
    adapterId: String,
    portId: String,
    eventBus: EventBus,
    sleepInterval: Long,
    lastSeenTimestamp: Long,
    shellyId: String,
    channel: Int
) :
    ShellyOutputPort<PowerLevel>(
        factoryId,
        adapterId,
        portId,
        eventBus,
        PowerLevel::class.java,
        PortCapabilities(canRead = true, canWrite = true),
        sleepInterval,
        lastSeenTimestamp
    ) {

    private val gson = Gson()
    private var readValue = PowerLevel(BigDecimal.ZERO)
    override val readTopics = arrayOf("shellies/$shellyId/light/$channel/status")
    override val writeTopic = "shellies/$shellyId/light/$channel/set"

    override fun readInternal(): PowerLevel {
        return readValue
    }

    override fun setValueFromMqttPayload(payload: String) {
        val response: LightBriefDto = gson.fromJson(payload, LightBriefDto::class.java)
        setValueFromLightResponse(response)
    }

    fun setValueFromLightResponse(lightResponse: LightBriefDto) {
        val valueInPercent = calculateBrightness(lightResponse)
        readValue = PowerLevel(valueInPercent.toBigDecimal())
    }

    private fun calculateBrightness(lightResponse: LightBriefDto): Int {
        val isOn = lightResponse.ison
        return if (isOn) lightResponse.brightness else 0
    }

    override fun getExecutePayload(): String? {
        if (requestedValue == null) {
            return null
        }

        if (requestedValue?.value == null) {
            return null
        }

        val response: LightSetDto =
            if (requestedValue!!.value == BigDecimal.ZERO) {
                LightSetDto("off", 0)
            } else {
                LightSetDto("on", requestedValue!!.value.toInt())
            }

        reset()

        return gson.toJson(response)
    }
}
