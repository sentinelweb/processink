package speecher.ui.image

import javax.swing.ImageIcon

class Image constructor(name: String) : ImageIcon(name::class.java.getResource("/images/$name"))