package com.microsoft.flutterdualscreen

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build.VERSION_CODES
import android.os.Handler
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimaps
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.RealObject

/**
 * Same as [org.robolectric.shadows.ShadowSensorManager] but behaves better when asked to retrieve
 * all sensors (Default robolectric implementation returns an empty list for [Sensor.TYPE_ALL])
 */
@Implements(value = SensorManager::class, looseSignatures = true)
class ShadowSensorManagerWithAll {
    var forceListenersToFail = false
    private val sensorMap = hashMapOf<Int, Sensor>()
    private val listeners = Multimaps.synchronizedMultimap(HashMultimap.create<SensorEventListener, Sensor>())

    @RealObject
    private val realObject: SensorManager? = null

    fun addSensor(sensor: Sensor) {
        sensorMap[sensor.type] = sensor
    }

    @Implementation
    fun getDefaultSensor(type: Int): Sensor? {
        return sensorMap[type]
    }

    @Implementation
    fun getSensorList(type: Int): List<Sensor> {
        return if (type == Sensor.TYPE_ALL) {
            sensorMap.values.toList()
        } else {
            val sensor = sensorMap[type]
            return if (sensor != null) {
                listOf(sensor)
            } else {
                listOf()
            }
        }
    }

    @Implementation
    fun registerListener(
            listener: SensorEventListener?, sensor: Sensor?, rate: Int, handler: Handler?): Boolean {
        return registerListener(listener, sensor, rate)
    }

    @Implementation
    fun registerListener(
            listener: SensorEventListener?, sensor: Sensor?, rate: Int, maxLatency: Int): Boolean {
        return registerListener(listener, sensor, rate)
    }

    @Implementation(minSdk = VERSION_CODES.KITKAT)
    fun registerListener(
            listener: SensorEventListener?, sensor: Sensor?, rate: Int, maxLatency: Int, handler: Handler?): Boolean {
        return registerListener(listener, sensor, rate)
    }

    @Implementation
    fun registerListener(listener: SensorEventListener?, sensor: Sensor?, rate: Int): Boolean {
        if (forceListenersToFail) {
            return false
        }
        listeners.put(listener, sensor)
        return true
    }

    @Implementation
    fun unregisterListener(listener: SensorEventListener?, sensor: Sensor?) {
        listeners.remove(listener, sensor)
    }

    @Implementation
    fun unregisterListener(listener: SensorEventListener?) {
        listeners.removeAll(listener)
    }

    fun getListeners(): List<SensorEventListener> {
        return listeners.keySet().toList()
    }

    fun sendSensorEventToListeners(event: SensorEvent?) {
        getListeners().onEach { it.onSensorChanged(event) }
    }
}