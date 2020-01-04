package cubes.objects

import processing.core.PVector

open class Shape constructor(
    var angle: PVector = PVector(),
    val position: PVector = PVector(),
    val scale: PVector = PVector()
)