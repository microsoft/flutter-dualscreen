/// Copyright (c) Microsoft Corporation.
/// Licensed under the MIT License.

import 'dart:ui';

import 'package:dual_screen/dual_screen.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('MediaQueryData.hinge returns something', () async {
    final MediaQueryData mediaQuery = MediaQueryData(
        displayFeatures: <DisplayFeature>[
          const DisplayFeature(
            bounds: Rect.fromLTRB(390, 0, 410, 600),
            type: DisplayFeatureType.hinge,
            state: DisplayFeatureState.postureFlat,
          )
        ]
    );
    expect(mediaQuery.hinge, isNotNull);
  });

  test('MediaQueryData.hinge returns the correct display feature', () async {
    final hinge = const DisplayFeature(
      bounds: Rect.fromLTRB(390, 0, 410, 600),
      type: DisplayFeatureType.hinge,
      state: DisplayFeatureState.postureFlat,
    );
    final MediaQueryData mediaQuery = MediaQueryData(
        displayFeatures: <DisplayFeature>[
          const DisplayFeature(
            bounds: Rect.fromLTRB(30, 10, 41, 60),
            type: DisplayFeatureType.cutout,
            state: DisplayFeatureState.unknown,
          ),
          hinge,
          const DisplayFeature(
            bounds: Rect.fromLTRB(90, 20, 50, 60),
            type: DisplayFeatureType.fold,
            state: DisplayFeatureState.postureFlat,
          )
        ]
    );
    expect(mediaQuery.hinge, equals(hinge));
  });

  test('MediaQueryData.hinge returns null when there is no hinge', () async {
    final MediaQueryData mediaQuery = MediaQueryData(
        displayFeatures: <DisplayFeature>[
          const DisplayFeature(
            bounds: Rect.fromLTRB(30, 10, 41, 60),
            type: DisplayFeatureType.cutout,
            state: DisplayFeatureState.unknown,
          ),
          const DisplayFeature(
            bounds: Rect.fromLTRB(90, 20, 50, 60),
            type: DisplayFeatureType.fold,
            state: DisplayFeatureState.postureFlat,
          )
        ]
    );
    expect(mediaQuery.hinge, isNull);
  });
}
