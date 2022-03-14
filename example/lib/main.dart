/// Copyright (c) Microsoft Corporation.
/// Licensed under the MIT License.

import 'package:flutter/material.dart';
import 'package:dual_screen/dual_screen.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: TwoPane(
        startPane: Scaffold(
          appBar: AppBar(
            title: const Text('Hinge Angle Sensor'),
          ),
          body: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                FutureBuilder<bool>(
                  future: DualScreenInfo.hasHingeAngleSensor,
                  builder: (context, hasHingeAngleSensor) {
                    return Text(
                        'Hinge angle sensor exists: ${hasHingeAngleSensor.data}');
                  },
                ),
                StreamBuilder<double>(
                  stream: DualScreenInfo.hingeAngleEvents,
                  builder: (context, hingeAngle) {
                    return Text('Hinge angle is: ${hingeAngle.data?.toStringAsFixed(2)}');
                  },
                ),
              ],
            ),
          ),
        ),
        endPane: Material(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Center(
              child: Text('This pane is visible on dual-screen devices.'),
            ),
          ),
        ),
        panePriority: TwoPanePriority.start,
      ),
    );
  }
}
