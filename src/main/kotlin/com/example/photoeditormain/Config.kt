package com.example.photoeditormain

import javafx.scene.image.Image
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.ByteArrayInputStream

enum class NodeTypes {
    INT, FLOAT, STRING, IMAGE, NONE
}

enum class ButtonTypes {
    INT, FLOAT, STRING, IMAGE, SEPIA, GREY, INVERT,
    BRIGHT, GAUSSIAN, SCALE_PIXEL, SCALE, MOVE_PIXEL,
    MOVE, ROTATE, ADD_TEXT_PIXEL, ADD_TEXT, FINISH_NODE
}

class Colors {
    companion object {
        const val RED = "#FA8072"
        const val BLACK = "#ADFF2F"
        const val GREEN = "#008000"
    }
}

class Formats {
    companion object {
        const val PNG = "*.png"
        const val JPG = "*.jpg"
        const val NS = "*.ns"
    }
}

class Link {
    companion object {
        const val ONE = "firstLink"
        const val TWO = "secondLink"
        const val THREE = "thirdLink"
        const val FOUR = "fourthLink"
        const val FIVE = "fifthLink"
    }
}

class Titles {
    companion object {
        const val INT = "Int"
        const val INT_DEF = "0"
        const val INT_X = "int x"
        const val INT_Y = "int y"
        const val FLOAT = "Float"
        const val FLOAT_DEF = "0.0"
        const val FLOAT_X = "float x"
        const val FLOAT_Y = "float y"
        const val STRING = "String"
        const val STRING_DEF = ""
        const val SEPIA = "Sepia"
        const val IMAGE = "Image"
        const val INVERT = "Invert"
        const val GREY = "Grey"
        const val BRIGHT = "Bright"
        const val GAUSSIAN = "Gaussian"
        const val SCALE_PIXEL = "Scale Pixel"
        const val SCALE = "Scale"
        const val MOVE = "Move"
        const val ROTATE = "Rotate"
        const val ADD_TEXT = "Add Text"
        const val IMAGE_FILES = "Image Files"
        const val OPEN_IMAGE_FILE = "Open Image File"
        const val SAVE_IMAGE = "Save Image"
        const val NODE_FILES = "Node Files"
        const val OPEN_NODES = "Open Nodes"
        const val SAVE_NODES = "Save Nodes"
    }
}

class UIFXML {
    companion object {
        const val FINISH_NODE = "FinishNode.fxml"
        const val IMAGE_NODE = "ImageNode.fxml"
        const val VALUE_NODE = "ValueNode.fxml"
        const val LINK_NODE = "NodeLink.fxml"
        const val ONE_LINK = "OneLink.fxml"
        const val TWO_LINKS = "TwoLinks.fxml"
        const val THREE_LINKS = "ThreeLink.fxml"
        const val FIVE_LINKS = "FiveLinks.fxml"
    }
}

class Config {
    companion object {
        fun matToImage(mat: Mat): Image {
            val buffer = MatOfByte()
            Imgcodecs.imencode(".png", mat, buffer)

            return Image(ByteArrayInputStream(buffer.toArray()))
        }
    }
}