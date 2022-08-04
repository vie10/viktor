package online.viestudio.viktor.core.utils

import java.awt.Desktop
import java.net.URI

fun Desktop.browse(uri: String) = Desktop.getDesktop().browse(URI.create(uri))