/**
 * Copyright (c) Microsoft Corporation.
 * Licensed under the MIT License.
 */

package com.microsoft.flutterdualscreen

import android.hardware.SensorManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 * Responds to method calls about the hinge sensor. For now it can tell the caller if the device is
 * equipped with a hinge sensor.
 */
class HingeInfoMethodCallHandler(private val sensorManager: SensorManager) : MethodChannel.MethodCallHandler {
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "hasHingeAngleSensor" -> result.success(HingeSensorResolver(sensorManager).resolve() != null)
            else -> result.notImplemented()
        }
    }
}