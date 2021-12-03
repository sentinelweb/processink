package cubes.motion

import cubes.models.Shape
import java.awt.Color

/**
 * Generic helpers for lists of [Shape]
 */

class ShapeList {

    companion object {

        fun coloriseListGradient(list: List<Shape>, start: Color, end: Color) {
            list.forEachIndexed { i, cube ->
                cube.fillColor = Color(
                    Motion.interpolate(
                        start.red.toFloat(),
                        end.red.toFloat(),
                        i.toFloat() / list.size
                    ).toInt(),
                    Motion.interpolate(
                        start.green.toFloat(),
                        end.green.toFloat(),
                        i.toFloat() / list.size
                    ).toInt(),
                    Motion.interpolate(
                        start.blue.toFloat(),
                        end.blue.toFloat(),
                        i.toFloat() / list.size
                    ).toInt(),
                    Motion.interpolate(
                        start.alpha.toFloat(),
                        end.alpha.toFloat(),
                        i.toFloat() / list.size
                    ).toInt()
                )
            }
        }

    }
}