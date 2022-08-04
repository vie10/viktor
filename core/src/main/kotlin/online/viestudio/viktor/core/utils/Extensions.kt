package online.viestudio.viktor.core.utils

import java.awt.Desktop
import java.net.URI

fun browseUri(uri: String) = Desktop.getDesktop().browse(URI.create(uri))