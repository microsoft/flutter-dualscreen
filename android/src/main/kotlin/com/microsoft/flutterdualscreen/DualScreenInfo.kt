/**
 * Copyright (c) Microsoft Corporation.
 * Licensed under the MIT License.
 */

package com.microsoft.flutterdualscreen

import android.content.Context
import android.hardware.SensorManager
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

private const val HINGE_ANGLE_CHANNEL_NAME = "com.microsoft.flutterdualscreen/hinge_angle"
private const val HINGE_INFO_CHANNEL_NAME = "com.microsoft.flutterdualscreen/hinge_info"

/**
 * Manages dual_screen event and method call channels (creates, registers, unregisters).
 *
 * The two channels it manages:
 *  - Hinge angle: Event channel that emits a new double value for each hinge sensor angle change.
 *  - Hinge info: Method channel that dart can use to know if the device has a hinge sensor or not.
 */
class DualScreenInfo : FlutterPlugin {
    private lateinit var hingeAngleChannel: EventChannel
    private lateinit var hingeInfoChannel: MethodChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val context = flutterPluginBinding.applicationContext;
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager;

        hingeAngleChannel = EventChannel(flutterPluginBinding.binaryMessenger, HINGE_ANGLE_CHANNEL_NAME)
        val hingeAngleStreamHandler = HingeAngleStreamHandler(sensorManager)
        hingeAngleChannel.setStreamHandler(hingeAngleStreamHandler)

        hingeInfoChannel = MethodChannel(flutterPluginBinding.binaryMessenger, HINGE_INFO_CHANNEL_NAME)
        val hingeInfoMethodHandler = HingeInfoMethodCallHandler(sensorManager)
        hingeInfoChannel.setMethodCallHandler(hingeInfoMethodHandler)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        hingeAngleChannel.setStreamHandler(null)
        hingeInfoChannel.setMethodCallHandler(null)
    }
}

