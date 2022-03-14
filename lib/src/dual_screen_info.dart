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
        _hingeAngleEvents = _repeatLatest(
          _hingeAngleEventChannel
              .receiveBroadcastStream()
              .map((event) => event as double),
        );
      }
      return _hingeAngleEvents!;
    } catch (e) {
      return Stream.empty();
    }
  }

  /// Returns true if the device has a hinge angle sensor.
  ///
  /// Returns false if the platform is not supported or if the device does not
  /// have a hinge or hinge angle sensor.
  static Future<bool> get hasHingeAngleSensor async {
    try {
      return await _hingeInfoMethodChannel
              .invokeMethod<bool>('hasHingeAngleSensor') ??
          false;
    } catch (e) {
      return false;
    }
  }

  static Stream<T> _repeatLatest<T>(Stream<T> original) {
    var done = false;
    T? latest;
    var currentListeners = <MultiStreamController<T>>{};
    original.listen((event) {
      latest = event;
      for (var listener in [...currentListeners]) listener.addSync(event);
    }, onError: (Object error, StackTrace stack) {
      for (var listener in [...currentListeners])
        listener.addErrorSync(error, stack);
    }, onDone: () {
      done = true;
      latest = null;
      for (var listener in currentListeners) listener.closeSync();
      currentListeners.clear();
    });
    return Stream.multi((controller) {
      if (done) {
        controller.close();
        return;
      }
      currentListeners.add(controller);
      var latestValue = latest;
      if (latestValue != null) controller.add(latestValue);
      controller.onCancel = () {
        if (!done) {
          currentListeners.remove(controller);
        }
      };
    });
  }
}
