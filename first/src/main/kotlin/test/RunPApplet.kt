package test

fun main(args: Array<String>) {
    val sketch = Test1()
    val swingGui = SwingGui(sketch)
    sketch.run()
    swingGui.show()
}