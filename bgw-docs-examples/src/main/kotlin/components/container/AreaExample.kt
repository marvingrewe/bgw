package components.container

import tools.aqua.bgw.components.container.Area
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.resize
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.visual.ColorVisual

fun main() {
    AreaExample()
}

class AreaExample : BoardGameApplication("Area example") {
    val gameScene = BoardGameScene(background = ColorVisual.LIGHT_GRAY)

    val numberOfComponentsLabel = Label(width = 400, posX = 50, posY = 50)
    val area = Area<TokenView>(100, 400, 50, 100, ColorVisual.DARK_GRAY)

    val greenToken = TokenView(visual = ColorVisual.GREEN)
    val redToken = TokenView(visual = ColorVisual.RED)

    init {
        area.onAdd = {
            this.resize(100,100)
        }
        area.onRemove = {
            this.rotation += 45
        }

        area.addComponentsListener {
            numberOfComponentsLabel.label = "Number of components in this area: ${area.numberOfComponents()}"
        }

        area.add(greenToken)
        area.add(redToken, 0)

        area.remove(redToken)

        gameScene.addComponents(area, numberOfComponentsLabel)
        showGameScene(gameScene)
        show()
    }
}