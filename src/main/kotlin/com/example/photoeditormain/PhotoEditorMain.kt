package com.example.photoeditormain

import com.google.gson.Gson
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.io.IOException

class FaceWindow {
    private var root = AnchorPane()
    private val width = 1280.0
    private val height = 720.0
    private var scene = Scene(root, width, height)

    fun start(): Scene {
        root.children.add(createButtons())
        root.children.add(saveAndOpenNodesButtons(150.0, 25.0))

        val end = EndNode()
        end.layoutX = width - end.rootPane!!.prefWidth - 30
        end.layoutY = height / 16
        root.children.add(end)

        return scene
    }

    private fun createButtons(): VBox {
        val vBox = VBox(20.0)
        vBox.style = "-fx-padding: 20px 10px; -fx-background-color: #A60000;" +
                "-fx-background-radius: 15px; -fx-border-radius: 15px;"

        fun createButton(buttonTypes: ButtonTypes) {
            val button = Button(buttonTypes.toString())
            button.style = "-fx-padding: 5px 10px; -fx-margin: 10px;" +
                    " -fx-text-style: bold; -fx-background-color: #A60000;"
            button.onAction = EventHandler {
                val node = getNode(buttonTypes)
                node.layoutX += 100
                node.layoutY += 100
                root.children.add(node)
            }
            vBox.children.add(button)
        }

        val buttonTypes: Array<ButtonTypes> = arrayOf(
            ButtonTypes.INT, ButtonTypes.FLOAT, ButtonTypes.STRING,
            ButtonTypes.IMAGE, ButtonTypes.SEPIA, ButtonTypes.GREY,
            ButtonTypes.INVERT, ButtonTypes.BRIGHT, ButtonTypes.GAUSSIAN,
            ButtonTypes.ROTATE, ButtonTypes.SCALE_PIXEL,
            ButtonTypes.SCALE, ButtonTypes.MOVE_PIXEL, ButtonTypes.MOVE,
            ButtonTypes.ADD_TEXT_PIXEL, ButtonTypes.ADD_TEXT
        )

        for (button in buttonTypes) {
            createButton(button)
        }

        return vBox
    }

    private fun saveAndOpenNodesButtons(x: Double, y: Double): HBox {
        val hBox = HBox(10.0)
        hBox.style = "-fx-padding: 20px 10px;-fx-background-color: #5F9EA0;" +
                "-fx-background-radius: 15px; -fx-border-radius: 15px;"
        hBox.layoutX = x
        hBox.layoutY = y

        val buttonSave = Button(Titles.SAVE_NODES)
        buttonSave.style = "-fx-padding: 5px 10px; -fx-margin: 10px;" +
                " -fx-text-style: bold; -fx-background-color: #A60000;"

        buttonSave.onAction = EventHandler {
            val gson = Gson()
            val nodes = root.children.filterIsInstance<DraggableNode>()
            val listNodes = MutableList(nodes.size) { nodes[it].toData() }
            val links = root.children.filterIsInstance<NodeLink>()
            val listLinks = MutableList(links.size) { links[it].toData() }

            println(gson.toJson(Saved(listNodes, listLinks)))

            val fileChooser = FileChooser()
            fileChooser.title = Titles.SAVE_NODES
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter(Titles.NODE_FILES, Formats.NS)
            )

            val dir = fileChooser.showSaveDialog(scene.window)
            if (dir != null) {
                try {
                    val file = File(dir.toURI())
                    file.writeText(gson.toJson(Saved(listNodes, listLinks)))
                } catch (e: IOException) {
                    println(e)
                }
            }
        }
        hBox.children.add(buttonSave)

        val buttonOpen = Button(Titles.OPEN_NODES)
        buttonOpen.style = "-fx-padding: 5px 10px; -fx-margin: 10px;" +
                " -fx-text-style: bold; -fx-background-color: #778899;"

        buttonOpen.onAction = EventHandler {
            val fileChooser = FileChooser()
            fileChooser.title = Titles.OPEN_NODES
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter(Titles.NODE_FILES, Formats.NS)
            )

            val dir = fileChooser.showOpenDialog(scene.window)
            if (dir != null) {
                try {
                    val file = File(dir.toURI())
                    if (!file.exists()) return@EventHandler

                    val data = Gson().fromJson(file.readText(), Saved::class.java)
                    if (data.links == null || data.nodes == null) return@EventHandler

                    root.children.removeIf { it is DraggableNode || it is NodeLink }

                    data.nodes.forEach {
                        val node = it.type?.let { it1 -> getNode(it1) }
                        node?.fromData(it)
                        root.children.add(node)
                    }

                    data.links.forEach {

                        val inNode = root.lookup("#${it.inputNode}") as DraggableNode
                        val outNode = root.lookup("#${it.outputNode}") as DraggableNode
                        val inAnchor = root.lookup("#${it.inputAnchor}") as AnchorPane
                        val outAnchor = root.lookup("#${it.outputAnchor}") as AnchorPane

                        inAnchor.layoutX = it.inputAnchorSize.first
                        inAnchor.layoutY = it.inputAnchorSize.second

                        outAnchor.layoutX = it.outputAnchorSize.first
                        outAnchor.layoutY = it.outputAnchorSize.second

                        inNode.linkNodes(outNode, inNode, outAnchor, inAnchor, it.inputAnchor!!).id = it.id
                    }

                } catch (e: IOException) {
                    println(e)
                }
            }
        }
        hBox.children.add(buttonOpen)

        return hBox
    }


    private fun getNode(buttonTypes: ButtonTypes): DraggableNode {
        return when (buttonTypes) {
            ButtonTypes.INT -> IntNode()
            ButtonTypes.FLOAT -> FloatNode()
            ButtonTypes.STRING -> StringNode()
            ButtonTypes.IMAGE -> ImageNodeClass()
            ButtonTypes.SEPIA -> SepiaNode()
            ButtonTypes.GREY -> GreyNode()
            ButtonTypes.INVERT -> InvertNode()
            ButtonTypes.BRIGHT -> BrightnessNode()
            ButtonTypes.GAUSSIAN -> GaussianNode()
            ButtonTypes.SCALE_PIXEL -> ScalePixelNode()
            ButtonTypes.SCALE -> ScalePercentNode()
            ButtonTypes.MOVE_PIXEL -> MovePixelsNode()
            ButtonTypes.MOVE -> MovePercentNode()
            ButtonTypes.ROTATE-> RotateNode()
            ButtonTypes.ADD_TEXT_PIXEL -> AddTextPixelNode()
            ButtonTypes.ADD_TEXT -> AddTextPercentNode()
            ButtonTypes.FINISH_NODE -> EndNode()
        }
    }
}

data class Saved(val nodes: MutableList<NodeData>?,
                 val links: MutableList<LinkData>?)

class AwesomePhotoEditor : Application() {
    override fun start(primaryStage: Stage) {
        nu.pattern.OpenCV.loadLocally()
        primaryStage.scene = FaceWindow().start()
        primaryStage.title = "Awesome Photo Editor"
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(AwesomePhotoEditor::class.java)
        }
    }
}