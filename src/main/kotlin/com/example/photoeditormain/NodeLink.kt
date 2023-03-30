package com.example.photoeditormain

import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.CubicCurve
import java.io.IOException
import java.util.*

class NodeLink : AnchorPane() {
    @FXML
    var nodeLink: CubicCurve? = null

    private var inputLinkString: String = ""
    private var inputNode: DraggableNode? = null
    private var outputNode: DraggableNode? = null
    private var inputAnchor: AnchorPane? = null
    private var outputAnchor: AnchorPane? = null

    @FXML
    private fun initialize() {
        //насколько кривая, будет кривой
        nodeLink!!.controlX1Property().bind(
            Bindings.add(nodeLink!!.startXProperty(), 100))
        nodeLink!!.controlX2Property().bind(
            Bindings.add(nodeLink!!.endXProperty(), -100))
        nodeLink!!.controlY1Property().bind(
            Bindings.add(nodeLink!!.startYProperty(), 0))
        nodeLink!!.controlY2Property().bind(
            Bindings.add(nodeLink!!.endYProperty(), 0))

        //обновляют значения в кривой
        parentProperty().addListener { _, _, _ ->
            if (parent == null) {
                if (inputNode != null) {
                    inputNode!!.connectedLinks.remove(this)
                    if (outputNode != null && inputNode!!.nodes.containsKey(inputLinkString)) {
                        inputNode!!.nodes[inputLinkString] =
                            inputNode!!.nodes[inputLinkString]!!.copy(second = null)
                    }
                }
                if (outputNode != null) {
                    outputNode!!.connectedLinks.remove(this)
                    outputNode!!.outputLink = null
                }
            }
        }
    }

    fun setStart(point: Point2D) {
        nodeLink!!.startX = point.x
        nodeLink!!.startY = point.y
    }

    fun setEnd(point: Point2D) {
        nodeLink!!.endX = point.x
        nodeLink!!.endY = point.y
    }

        //изменение проперти линка, а проперти изменяет значение насколько кривая - кривая
    fun bindStartEnd(source1: DraggableNode, source2: DraggableNode, a1: AnchorPane, a2: AnchorPane) {
        nodeLink!!.startXProperty().bind(
            Bindings.add(source1.layoutXProperty(), a1.layoutX + a1.width / 2.0))
        nodeLink!!.startYProperty().bind(
            Bindings.add(source1.layoutYProperty(), a1.layoutY + a1.height / 2.0))
        nodeLink!!.endXProperty().bind(
            Bindings.add(source2.layoutXProperty(), a2.layoutX + a2.width / 2.0))
        nodeLink!!.endYProperty().bind(
            Bindings.add(source2.layoutYProperty(), a2.layoutY + a2.height / 2.0))

        inputLinkString = a2.id
        outputAnchor = a1
        inputAnchor = a2
        links(source1, source2)
    }

    private fun links(source1: DraggableNode, source2: DraggableNode) {
        outputNode = source1
        inputNode = source2
        source1.connectedLinks.add(this)
        source2.connectedLinks.add(this)

        if (updateNode(outputNode!!))
            kickAction()
    }

    private fun updateNode(node: DraggableNode): Boolean {
        if (node.nodes.all { it.value.second != null }) {
            node.updateNode()
            return true
        }
        return false
    }

    fun kickAction() {
        if (inputNode == null)
            return

        if (updateNode(inputNode!!) && inputNode!!.outputLink != null)
            inputNode!!.outputLink!!.kickAction()
    }

    fun toData(): LinkData {
        return LinkData(
            id,
            inputNode?.id,
            inputNode!!::class.simpleName,
            outputNode?.id,
            outputNode!!::class.simpleName,
            inputAnchor?.id,
            PairD(inputAnchor!!.layoutX + inputAnchor!!.width / 2,
                inputAnchor!!.layoutY + inputAnchor!!.height / 2),
            outputAnchor?.id,
            PairD(
                outputAnchor!!.layoutX + outputAnchor!!.width / 2,
                outputAnchor!!.layoutY + outputAnchor!!.height / 2
            ),
        )
    }

    init {
        val fxmlLoader = FXMLLoader(
            javaClass.getResource(UIFXML.LINK_NODE)
        )
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        try {
            fxmlLoader.load<Any>()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }
        id = UUID.randomUUID().toString()
    }
}

data class PairD<A, B>(val first: A, val second: B)

data class LinkData(
    val id: String?,
    val inputNode: String?,
    val inputNodeClass: String?,
    val outputNode: String?,
    val outputNodeClass: String?,
    val inputAnchor: String?,
    val inputAnchorSize: PairD<Double, Double>,
    val outputAnchor: String?,
    val outputAnchorSize: PairD<Double, Double>
)