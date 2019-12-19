package test

import processing.core.PApplet

// from https://happycoding.io/examples/java/processing-in-java/swing-control-window

class Test1 : PApplet() {

    private var red = 0f
    private var green = 0f
    private var blue = 0f

    override fun settings() {
        size(500, 500)
    }

    override fun draw() {
        background(red, green, blue)
    }

    fun setColor(red: Int, green: Int, blue: Int) {
        this.red = red.toFloat()
        this.green = green.toFloat()
        this.blue = blue.toFloat()
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

}