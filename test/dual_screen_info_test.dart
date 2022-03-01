/// Copyright (c) Microsoft Corporation.
/// Licensed under the MIT License.

import 'package:dual_screen/dual_screen.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  const EventChannel hingeAngleChannel =
      EventChannel('com.microsoft.flutterdualscreen/hinge_angle');
  const MethodChannel hingeInfoChannel =
      MethodChannel('com.microsoft.flutterdualscreen/hinge_info');

  TestWidgetsFlutterBinding.ensureInitialized();

  tearDown(() {
    hingeInfoChannel.setMockMethodCallHandler(null);
    ServicesBinding.instance.defaultBinaryMessenger
        .setMockMessageHandler(hingeAngleChannel.name, null);
  });

  test('hasHingeSensor is true for devices with hinge angle sensor', () async {
    hingeInfoChannel.setMockMethodCallHandler((MethodCall methodCall) async {
      return true;
    });

    expect(await DualScreenInfo.hasHingeAngleSensor, true);
  });

  test('hasHingeSensor is false for devices without hinge angle sensor',
      () async {
    hingeInfoChannel.setMockMethodCallHandler((MethodCall methodCall) async {
      return false;
    });

    expect(await DualScreenInfo.hasHingeAngleSensor, false);
  });

  test('hasHingeSensor is false on unsupported platforms', () async {
    hingeInfoChannel.setMockMethodCallHandler(null);

    expect(await DualScreenInfo.hasHingeAngleSensor, false);
  });

  test('hingeAngleEvents streams values on supported platforms', () async {
    _mockSensorStream(hingeAngleChannel.name, [142.2]);

    expect(await DualScreenInfo.hingeAngleEvents.first, 142.2);
  });

  test('hingeAngleEvents stream is empty for devices without a hinge on supported platforms', () async {
    _mockSensorStream(hingeAngleChannel.name, []);

    expect(await DualScreenInfo.hingeAngleEvents.isEmpty, true);
  });

  test('hingeAngleEvents streams does not stream values on unsupported platforms',
      () async {
        ServicesBinding.instance.defaultBinaryMessenger
            .setMockMessageHandler(hingeAngleChannel.name, null);

    expect(await DualScreenInfo.hingeAngleEvents.isEmpty, true);
  });
}

void _mockSensorStream(
    String channelName, List<double> multipleSensorValues) {
  const StandardMethodCodec standardMethod = StandardMethodCodec();

  ServicesBinding.instance.defaultBinaryMessenger
      .setMockMessageHandler(channelName, (ByteData? message) async {
    final MethodCall methodCall = standardMethod.decodeMethodCall(message);
    if (methodCall.method == 'listen') {
      multipleSensorValues.forEach((element) {
        ServicesBinding.instance.defaultBinaryMessenger.handlePlatformMessage(
          channelName,
          standardMethod.encodeSuccessEnvelope(element),
          (ByteData? reply) {},
        );
      });
      ServicesBinding.instance.defaultBinaryMessenger.handlePlatformMessage(
        channelName,
        null,
        (ByteData? reply) {},
      );
      return standardMethod.encodeSuccessEnvelope(null);
    } else if (methodCall.method == 'cancel') {
      return standardMethod.encodeSuccessEnvelope(null);
    } else {
      fail('Expected listen or cancel');
    }
  });
}
