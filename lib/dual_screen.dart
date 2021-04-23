/// Copyright (c) Microsoft Corporation.
/// Licensed under the MIT License.

import 'dart:async';

import 'package:flutter/services.dart';

class DualScreenInfo {
  static const EventChannel _hingeAngleEventChannel =
      EventChannel('com.microsoft.flutterdualscreen/hinge_angle');
  static const MethodChannel _hingeInfoMethodChannel =
      MethodChannel('com.microsoft.flutterdualscreen/hinge_info');
  static Stream<double>? _hingeAngleEvents;

  /// A broadcast stream of events from the device hinge angle sensor.
  ///
  /// If the device is not equipped with a hinge angle sensor, this stream is
  /// empty.
  static Stream<double> get hingeAngleEvents {
    try {
      if (_hingeAngleEvents == null) {
        _hingeAngleEvents = _hingeAngleEventChannel
            .receiveBroadcastStream()
            .map((event) => event as double);
      }
      return _hingeAngleEvents!;
    } catch (MissingPluginException) {
      return Stream.empty();
    }
  }

  /// Returns true if the device has a hinge angle sensor.
  static Future<bool> get hasHingeAngleSensor async {
    try {
      return await _hingeInfoMethodChannel
          .invokeMethod<bool>('hasHingeAngleSensor') ??
          false;
    } catch (MissingPluginException) {
      return false;
    }
  }
}
