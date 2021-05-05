import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:dual_screen_example/main.dart';

void main() {
  const EventChannel hingeAngleChannel =
  EventChannel('com.microsoft.flutterdualscreen/hinge_angle');
  const MethodChannel hingeInfoChannel =
  MethodChannel('com.microsoft.flutterdualscreen/hinge_info');

  tearDown(() {
    hingeInfoChannel.setMockMethodCallHandler(null);
    ServicesBinding.instance!.defaultBinaryMessenger
        .setMockMessageHandler(hingeAngleChannel.name, null);
  });

  testWidgets('Hinge angle sensor exists', (WidgetTester tester) async {
    hingeInfoChannel.setMockMethodCallHandler((MethodCall methodCall) async {
      return true;
    });

    await tester.pumpWidget(MyApp());
    await tester.pumpAndSettle();

    expect(
      find.text('Hinge angle sensor exists: true'),
      findsOneWidget,
    );
  });

  testWidgets('Hinge angle sensor does not exist', (WidgetTester tester) async {
    hingeInfoChannel.setMockMethodCallHandler((MethodCall methodCall) async {
      return false;
    });

    await tester.pumpWidget(MyApp());
    await tester.pumpAndSettle();

    expect(
      find.text('Hinge angle sensor exists: false'),
      findsOneWidget,
    );
  });

  testWidgets('Hinge angle sensor is not supported by platform', (WidgetTester tester) async {
    hingeInfoChannel.setMockMethodCallHandler((MethodCall methodCall) async {
      throw MissingPluginException();
    });

    await tester.pumpWidget(MyApp());
    await tester.pumpAndSettle();

    expect(
      find.text('Hinge angle sensor exists: false'),
      findsOneWidget,
    );
  });

  testWidgets('Hinge angle reports the value 123.0', (WidgetTester tester) async {
    _mockSensorStream(hingeAngleChannel.name, [123.0]);

    await tester.pumpWidget(MyApp());
    await tester.pumpAndSettle();

    expect(
      find.text('Hinge angle is: 123.0'),
      findsOneWidget,
    );
  });
}

void _mockSensorStream(
    String channelName, List<double> multipleSensorValues) {
  const StandardMethodCodec standardMethod = StandardMethodCodec();

  ServicesBinding.instance!.defaultBinaryMessenger
      .setMockMessageHandler(channelName, (ByteData? message) async {
    final MethodCall methodCall = standardMethod.decodeMethodCall(message);
    if (methodCall.method == 'listen') {
      multipleSensorValues.forEach((element) {
        ServicesBinding.instance!.defaultBinaryMessenger.handlePlatformMessage(
          channelName,
          standardMethod.encodeSuccessEnvelope(element),
              (ByteData? reply) {},
        );
      });
      ServicesBinding.instance!.defaultBinaryMessenger.handlePlatformMessage(
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
