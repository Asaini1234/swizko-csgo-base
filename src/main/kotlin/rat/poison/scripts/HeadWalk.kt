package rat.swizko.scripts

import org.jire.arrowhead.keyPressed
import rat.swizko.curSettings
import rat.swizko.game.angle
import rat.swizko.game.clientState
import rat.swizko.game.entity.EntityType
import rat.swizko.game.entity.absPosition
import rat.swizko.game.entity.onGround
import rat.swizko.game.forEntities
import rat.swizko.game.hooks.cursorEnable
import rat.swizko.game.hooks.updateCursorEnable
import rat.swizko.game.me
import rat.swizko.robot
import rat.swizko.scripts.aim.meDead
import rat.swizko.utils.Angle
import rat.swizko.utils.Vector
import rat.swizko.utils.every
import rat.swizko.utils.generalUtil.strToBool
import java.awt.event.KeyEvent
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private var onEnt = 0L

//Currently just a poc
////Only moves towards the center of the player
//////Keeps up with crouching just fine, but not normal running usually
////////Doesn't predict, isn't accurate

var mePos = Vector()
var onEntPos = Vector()

internal fun headWalk() = every(2, inGameCheck = true) {
    if (!curSettings["HEAD_WALK"].strToBool() || meDead) return@every

    if (!keyPressed(KeyEvent.VK_W) && !keyPressed(KeyEvent.VK_A) && !keyPressed(KeyEvent.VK_S) && !keyPressed(KeyEvent.VK_D)) {
        mePos = me.absPosition()
        if (onPlayerHead()) {
            updateCursorEnable()
            if (cursorEnable) return@every

            val pos = Vector(mePos.x - onEntPos.x, mePos.y - onEntPos.y, mePos.z - onEntPos.z)
            val yaw = clientState.angle().y
            val calcYaw = yaw/180*Math.PI

            //Is this even needed?
            val distX = (pos.x * cos(calcYaw) + pos.y * sin(calcYaw)).toInt()
            val distY = (pos.y * cos(calcYaw) - pos.x * sin(calcYaw)).toInt()

            //Gotta be a simpler way
            var wRelease = false
            var aRelease = false
            var sRelease = false
            var dRelease = false
            when {
                distX < 2 -> {
                    robot.keyPress(KeyEvent.VK_W)
                    wRelease = true
                }
                distX > 2 -> {
                    robot.keyPress(KeyEvent.VK_S)
                    sRelease = true
                }
            }

            when {
                distY < 2 -> {
                    robot.keyPress(KeyEvent.VK_A)
                    aRelease = true
                }
                distY > 2 -> {
                    robot.keyPress(KeyEvent.VK_D)
                    dRelease = true
                }
            }
            //Sleep once instead of up to twice
            Thread.sleep(1)
            if (wRelease) {
                robot.keyRelease(KeyEvent.VK_W)
            }
            if (aRelease) {
                robot.keyRelease(KeyEvent.VK_A)
            }
            if (sRelease) {
                robot.keyRelease(KeyEvent.VK_S)
            }
            if (dRelease) {
                robot.keyRelease(KeyEvent.VK_D)
            }
        }
    }
}

internal fun onPlayerHead() : Boolean {
    var entPos : Angle
    onEnt = 0L

    forEntities(EntityType.CCSPlayer) {
        val entity = it.entity
        if (entity == me || !entity.onGround()) return@forEntities

        entPos = entity.absPosition()

        val xDist = abs(mePos.x - entPos.x)
        val yDist = abs(mePos.y - entPos.y)
        val zDif = mePos.z - entPos.z

        if (xDist <= 30 && yDist <= 30 && zDif in 50.0..75.0) {
            onEnt = entity
            onEntPos = entPos
        }

        if (onEnt != 0L) {
            return@forEntities
        }
    }

    return when (onEnt) {
        0L -> false
        else -> true
    }
}