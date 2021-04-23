/**
 * Copyright (c) Microsoft Corporation.
 * Licensed under the MIT License.
 */
package io.flutter.plugins.androidintent

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.microsoft.flutterdualscreen.ShadowSensorManagerWithAll
import com.microsoft.flutterdualscreen.DualScreenInfo
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.StandardMethodCodec
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow
import java.nio.ByteBuffer


private const val HINGE_ANGLE_CHANNEL_NAME = "com.microsoft.flutterdualscreen/hinge_angle"
private const val HINGE_INFO_CHANNEL_NAME = "com.microsoft.flutterdualscreen/hinge_info"

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, shadows = [ShadowSensorManagerWithAll::class])
class DualScreenInfoTest {
    private lateinit var context: Context
    private lateinit var sensorManager: ShadowSensorManagerWithAll
    private lateinit var pluginBinding: FlutterPlugin.FlutterPluginBinding
    private lateinit var binaryMessenger: BinaryMessenger
    private lateinit var dualScreenInfo: DualScreenInfo

    @Before
    fun setUp() {
        context = getApplicationContext()
        binaryMessenger = mock()
        pluginBinding = mock {
            on { applicationContext } doReturn context
            on { binaryMessenger } doReturn binaryMessenger
        }

        val realSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        sensorManager = Shadow.extract<ShadowSensorManagerWithAll>(realSensorManager)

        dualScreenInfo = DualScreenInfo()
    }

    @Test
    fun registersAndUnregistersChannels() {
        // When
        dualScreenInfo.onAttachedToEngine(pluginBinding)

        // Then
        verify(binaryMessenger, times(1))
                .setMessageHandler(eq(HINGE_ANGLE_CHANNEL_NAME), notNull())
        verify(binaryMessenger, times(1))
                .setMessageHandler(eq(HINGE_INFO_CHANNEL_NAME), notNull())

        // When
        dualScreenInfo.onDetachedFromEngine(pluginBinding)

        // Then
        verify(binaryMessenger, times(1))
                .setMessageHandler(eq(HINGE_ANGLE_CHANNEL_NAME), isNull())
        verify(binaryMessenger, times(1))
                .setMessageHandler(eq(HINGE_INFO_CHANNEL_NAME), isNull())
    }

    @Test
    @Config(sdk = [30])
    fun hasHingeAngleSensor_API30() {
        // Given
        val sensor = mock<Sensor> {
            on { type } doReturn Sensor.TYPE_HINGE_ANGLE
        }
        sensorManager.addSensor(sensor)
        dualScreenInfo.onAttachedToEngine(pluginBinding)
        val reply = mock<BinaryMessenger.BinaryReply>()

        // When
        val info = catchInfoHandler()
        info.onMessage(encode("hasHingeAngleSensor", null), reply)

        // Then
        val argumentCaptor = argumentCaptor<ByteBuffer>()
        verify(reply, times(1)).reply(argumentCaptor.capture())
        val response = argumentCaptor.allValues.map {
            it.flip()
            StandardMethodCodec.INSTANCE.decodeEnvelope(it)
        }
        assertEquals(response[0], true)
    }

    @Test
    fun hasHingeAngleSensor_sensorName() {
        // Given
        val sensor = mock<Sensor> {
            on { name } doReturn "Hinge Angle"
        }
        sensorManager.addSensor(sensor)
        dualScreenInfo.onAttachedToEngine(pluginBinding)
        val reply = mock<BinaryMessenger.BinaryReply>()

        // When
        val info = catchInfoHandler()
        info.onMessage(encode("hasHingeAngleSensor", null), reply)

        // Then
        val argumentCaptor = argumentCaptor<ByteBuffer>()
        verify(reply, times(1)).reply(argumentCaptor.capture())
        val response = argumentCaptor.allValues.map {
            it.flip()
            StandardMethodCodec.INSTANCE.decodeEnvelope(it)
        }
        assertEquals(response[0], true)
    }

    @Test
    @Config(sdk = [21])
    fun hasHingeAngleSensor_sensorTypeString() {
        // Given
        val sensor = mock<Sensor> {
            on { stringType } doReturn "android.sensor.hinge_angle"
        }
        sensorManager.addSensor(sensor)
        dualScreenInfo.onAttachedToEngine(pluginBinding)
        val reply = mock<BinaryMessenger.BinaryReply>()

        // When
        val info = catchInfoHandler()
        info.onMessage(encode("hasHingeAngleSensor", null), reply)

        // Then
        val argumentCaptor = argumentCaptor<ByteBuffer>()
        verify(reply, times(1)).reply(argumentCaptor.capture())
        val response = argumentCaptor.allValues.map {
            it.flip()
            StandardMethodCodec.INSTANCE.decodeEnvelope(it)
        }
        assertEquals(response[0], true)
    }

    @Test
    @Config(sdk = [21])
    fun hasHingeAngleSensor_noHingeSensor() {
        // Given
        val sensor = mock<Sensor> {
            on { name } doReturn "Gyro sensor"
            on { type } doReturn Sensor.TYPE_GYROSCOPE
            on { stringType } doReturn "android.sensor.gyro"
        }
        sensorManager.addSensor(sensor)
        dualScreenInfo.onAttachedToEngine(pluginBinding)
        val reply = mock<BinaryMessenger.BinaryReply>()

        // When
        val info = catchInfoHandler()
        info.onMessage(encode("hasHingeAngleSensor", null), reply)

        // Then
        val argumentCaptor = argumentCaptor<ByteBuffer>()
        verify(reply, times(1)).reply(argumentCaptor.capture())
        val response = argumentCaptor.allValues.map {
            it.flip()
            StandardMethodCodec.INSTANCE.decodeEnvelope(it)
        }
        assertEquals(response[0], false)
    }

    @Test
    fun hingeAngleStream_reportsEvents() {
        // Given
        val sensor = mock<Sensor> {
            on { name } doReturn "Hinge Angle"
        }
        sensorManager.addSensor(sensor)
        dualScreenInfo.onAttachedToEngine(pluginBinding)
        val reply = mock<BinaryMessenger.BinaryReply>()

        // When
        val angle = catchAngleHandler()
        angle.onMessage(encode("listen", null), reply)
        produceSensorEvent(sensor, 1.4F)
        produceSensorEvent(sensor, 2.4F)
        produceSensorEvent(sensor, 3.4F)

        // Then
        val argumentCaptor = argumentCaptor<ByteBuffer>()
        verify(binaryMessenger, times(4)).send(eq(HINGE_ANGLE_CHANNEL_NAME), argumentCaptor.capture())
        val streamedValues = argumentCaptor.allValues.map {
            it.flip()
            StandardMethodCodec.INSTANCE.decodeEnvelope(it)
        }
        assertEquals(streamedValues[1] as Double, 1.4, 0.000001)
        assertEquals(streamedValues[2] as Double, 2.4, 0.000001)
        assertEquals(streamedValues[3] as Double, 3.4, 0.000001)
    }

    @Test
    fun hingeAngleStream_startsWith180() {
        // Given
        val sensor = mock<Sensor> {
            on { name } doReturn "Hinge Angle"
        }
        sensorManager.addSensor(sensor)
        dualScreenInfo.onAttachedToEngine(pluginBinding)
        val reply = mock<BinaryMessenger.BinaryReply>()

        // When
        val angle = catchAngleHandler()
        angle.onMessage(encode("listen", null), reply)

        // Then
        val argumentCaptor = argumentCaptor<ByteBuffer>()
        verify(binaryMessenger, times(1)).send(eq(HINGE_ANGLE_CHANNEL_NAME), argumentCaptor.capture())
        val methodCalls = argumentCaptor.allValues.map {
            it.flip()
            StandardMethodCodec.INSTANCE.decodeEnvelope(it)
        }
        assertEquals(methodCalls[0] as Double, 180.0, 0.000001)
    }

    @Test
    fun hingeAngleStream_isEndedWhenNoHingeAngleSensor() {
        // Given
        dualScreenInfo.onAttachedToEngine(pluginBinding)
        val reply = mock<BinaryMessenger.BinaryReply>()

        // When
        val angle = catchAngleHandler()
        angle.onMessage(encode("listen", null), reply)

        // Then
        val argumentCaptor = argumentCaptor<ByteBuffer>()
        verify(binaryMessenger, times(1)).send(eq(HINGE_ANGLE_CHANNEL_NAME), argumentCaptor.capture())
        val streamedValues = argumentCaptor.allValues.map {
            it?.run {
                it.flip()
                StandardMethodCodec.INSTANCE.decodeEnvelope(it)
            }
        }
        assertNull(streamedValues[0])
    }

    private fun produceSensorEvent(s: Sensor, v: Float) {
        val sensorEvent = mock<SensorEvent>()

        val sensorField = SensorEvent::class.java.getField("sensor")
        sensorField.isAccessible = true
        sensorField.set(sensorEvent, s)

        val valuesField = SensorEvent::class.java.getField("values")
        valuesField.isAccessible = true
        valuesField.set(sensorEvent, floatArrayOf(v))

        sensorManager.sendSensorEventToListeners(sensorEvent)
    }

    private fun decode(buffer: ByteBuffer?): MethodCall? {
        return if (buffer == null) {
            null
        } else {
            StandardMethodCodec.INSTANCE.decodeMethodCall(buffer)
        }
    }

    private fun encode(method: String, argument: Any?): ByteBuffer {
        val buffer = StandardMethodCodec.INSTANCE.encodeMethodCall(MethodCall(method, argument))
        buffer.flip()
        return buffer
    }

    private fun catchAngleHandler(): BinaryMessenger.BinaryMessageHandler {
        val hingeAngleHandlerCaptor = argumentCaptor<BinaryMessenger.BinaryMessageHandler>()
        verify(binaryMessenger, times(1))
                .setMessageHandler(eq(HINGE_ANGLE_CHANNEL_NAME), hingeAngleHandlerCaptor.capture())
        return hingeAngleHandlerCaptor.firstValue
    }

    private fun catchInfoHandler(): BinaryMessenger.BinaryMessageHandler {
        val hingeInfoHandlerCaptor = argumentCaptor<BinaryMessenger.BinaryMessageHandler>()
        verify(binaryMessenger, times(1))
                .setMessageHandler(eq(HINGE_INFO_CHANNEL_NAME), hingeInfoHandlerCaptor.capture())

        return hingeInfoHandlerCaptor.firstValue
    }
}