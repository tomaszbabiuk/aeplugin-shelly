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

import eu.automateeverything.domain.hardware.Luminosity
import eu.automateeverything.shellyplugin.LuminosityBriefDto
import java.math.BigDecimal

class ShellyLuminosityInputPort(
        id: String,
        shellyId: String,
        sleepInterval: Long,
        lastSeenTimestamp: Long
) : ShellyInputPort<Luminosity>(id, Luminosity::class.java, sleepInterval, lastSeenTimestamp) {

    private var value = Luminosity(BigDecimal.ZERO)
    override val readTopics = arrayOf("shellies/$shellyId/sensor/lux")

    override fun read(): Luminosity {
        return value
    }

    override fun setValueFromMqttPayload(payload: String) {
        val valueParsed = payload.toBigDecimal()
        value = Luminosity(valueParsed)
    }

    fun setValueFromLuminosityResponse(luminosityBrief: LuminosityBriefDto) {
        value = Luminosity(luminosityBrief.value)
    }
}