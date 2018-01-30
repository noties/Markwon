package ru.noties.markwon.spans.configuration.image

/**
 * Configuration for images
 *
 * Can set two parameters: image width style and horizontal gravity
 *
 * @property imageWidth Width of the image to be displayed (WRAP_CONTENT or MATCH_PARENT)
 * @property gravity Horizontal gravity (left, right or center)
 */
class ImageConfig @JvmOverloads constructor(
        val imageWidth: ImageWidth = ImageWidth.Wrap,
        val gravity: ImageGravity = ImageGravity.Left
)

enum class ImageGravity {
    Left,
    Center,
    Right
}

enum class ImageWidth {
    Wrap,
    MatchParent
}