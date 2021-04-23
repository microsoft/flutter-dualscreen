/**
 * Copyright (c) Microsoft Corporation.
 * Licensed under the MIT License.
 */

package com.microsoft.flutterdualscreen

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import io.flutter.plugin.common.EventChannel

/**
 * Converts [SensorEventListener] events to [EventChannel.EventSink] events.
 */
class SensorListenerToEventSink(private val eventSink: EventChannel.EventSink) : SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // no-op
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            this.eventSink.success(it.values[0])
        }
    }

    init {
        // In some situations, like on an emulator, the sensor does not report any value unless the
        // user interacts with the hinge. For these situations, we will start with the default value
        // of 180 deg, which corresponds to the device screens laying flat.
        this.eventSink.success(180.0)
    }
}