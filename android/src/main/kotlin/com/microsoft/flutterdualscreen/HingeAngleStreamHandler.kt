/**
 * Copyright (c) Microsoft Corporation.
 * Licensed under the MIT License.
 */

package com.microsoft.flutterdualscreen

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel

/**
 * Stream handler that starts reporting hinge angle sensor values when listened to.
 *
 * Manages the connection to [SensorManager] by registering to listen to the [Sensor] when
 * [EventChannel] is being listened to and unregistering when the [EventChannel] cancels.
 *
 * The sensor being used is determined by [HingeSensorResolver].
 * The sensor data is processed by [SensorListenerToEventSink].
 *
 * If the device is not equipped with a Hinge Angle Sensor, the stream is closed and remains empty.
 */
class HingeAngleStreamHandler(private val sensorManager: SensorManager) : EventChannel.StreamHandler {
    private var sensorEventListener: SensorEventListener? = null
    private val sensor: Sensor? = initSensor()

    override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
        eventSink?.let {
            if (sensor == null) {
                eventSink.endOfStream()
            } else {
                sensorEventListener?.let {
                    sensorManager.unregisterListener(it)
                }
                sensorEventListener = SensorListenerToEventSink(eventSink)
                sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onCancel(arguments: Any?) {
        sensorManager.unregisterListener(sensorEventListener)
        sensorEventListener = null
    }

    private fun initSensor(): Sensor? {
        return HingeSensorResolver(sensorManager).resolve();
    }
}