/**
 * Copyright (c) Microsoft Corporation.
 * Licensed under the MIT License.
 */

package com.microsoft.flutterdualscreen

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import java.util.*

private const val HINGE_ANGLE_SENSOR_NAME = "hinge angle"
private const val HINGE_ANGLE_SENSOR_TYPE = "hinge_angle"

/**
 * Decides what sensor to use as a hinge sensor.
 *
 * This uses [Sensor.TYPE_HINGE_ANGLE] on API 30 and above. On lower versions it tries to find the
 * hinge angle sensor using the hinge name or hinge type string.
 */
class HingeSensorResolver(private val sensorManager: SensorManager) {
    fun resolve(): Sensor? {
        return resolveForAPI30() ?: resolveUsingSensorName() ?: resolveUsingTypeString()
    }

    private fun resolveForAPI30(): Sensor? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)
        } else {
            null
        }
    }

    private fun resolveUsingSensorName(): Sensor? {
        return sensorManager.getSensorList(Sensor.TYPE_ALL).firstOrNull {
            it.name?.lowercase()?.contains(HINGE_ANGLE_SENSOR_NAME) ?: false
        }
    }

    private fun resolveUsingTypeString(): Sensor? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            sensorManager.getSensorList(Sensor.TYPE_ALL).firstOrNull {
                it.stringType?.contains(HINGE_ANGLE_SENSOR_TYPE) ?: false
            }
        } else {
            null
        }
    }
}