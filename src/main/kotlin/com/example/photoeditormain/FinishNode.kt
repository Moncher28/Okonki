package com.example.photoeditormain

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.paint.Paint
import javafx.stage.FileChooser
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.io.IOException

class ImageNodeClass : ImageNode() {
    override val nodeType: NodeTypes = NodeTypes.IMAGE

    @FXML
    var openButton: Button? = null

    private var imageMat: Mat? = null
    private var path: String? = null

    override fun getValue(): Mat? {
        return imageMat
    }

    private fun getImage() {
        imageMat = Imgcodecs.imread(path)
        updateNode()
        imageView!!.isVisible = true
        outputLink?.kickAction()
    }

    override fun addInit() {
        openButton!!.onAction = EventHandler {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(
                FileChooser.ExtensionFilter(
                    Titles.IMAGE_FILES, Formats.PNG, Formats.JPG)
            )
            fileChooser.title = Titles.OPEN_IMAGE_FILE
            val file = fileChooser.showOpenDialog(scene.window)
            if (file != null) {
                path = file.absolutePath
                getImage()
            }
        }
    }

    override fun toData(): NodeData {
        val data = super.toData()
        data.data = path
        return data
    }

    override fun fromData(nodeData: NodeData) {
        super.fromData(nodeData)
        path = nodeData.data
        getImage()
    }

    init {
        init(UIFXML.IMAGE_NODE)
    }
}

class EndNode : ImageNode() {
    @FXML
    var saveButton: Button? = null

    override fun getValue(): Mat? {
        return nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return null
    }

    override fun addInit() {
        rootPane!!.onDragDetected = null

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE

        saveButton!!.onAction = EventHandler {
            val mat = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return@EventHandler

            val fileChooser = FileChooser()
            fileChooser.title = Titles.SAVE_IMAGE
            fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter(
                Titles.IMAGE_FILES, Formats.PNG, Formats.JPG)
            )
            val dir = fileChooser.showSaveDialog(scene.window)
            if (dir != null) {
                try {
                    Imgcodecs.imwrite(dir.absolutePath, mat)
                } catch (e: IOException) {
                    println(e)
                }
            }
        }
    }

    override fun updateNode() {
        isRightNodes()
        val v = getValue()
        if (v != null) {
            imageView!!.isVisible = true
            imageView!!.image = Config.matToImage(v)
            saveButton!!.textFill = Paint.valueOf(Colors.BLACK)
        } else {
            saveButton!!.textFill = Paint.valueOf(Colors.RED)
        }
    }

    init {
        init(UIFXML.FINISH_NODE)
    }
}