package hello

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Algorithm(
    val stream: WriteCommittedStream = WriteCommittedStream()
) {

    private val LOGGER: Logger = LoggerFactory.getLogger(Algorithm::class.java)

    private val sequence = listOf("F", "L", "L", "L", "L")

    private var current = 0

    fun decide(arena: Arena): String {

//        stream.send(arena)
//        (arena)

        val me = arena.state["https://34.117.10.100.sslip.io/"]
            ?: arena.state["34.117.10.100.sslip.io"]
            ?: arena.state.values.random()

        val front = when (me.direction) {
            "N" -> me.x to me.y - 1
            "S" -> me.x to me.y + 1
            "W" -> me.x - 1 to me.y
            "E" -> me.x + 1 to me.y
            else -> me.x to me.y + 1
        }

        val isInFront = arena.state.values.any { it.x to it.y == front }

        if (me.y == 0) {
            return if (me.direction == "S") "F" else "R"
        }
        if (me.x == 0) {
            return if (me.direction == "E") "F" else "R"
        }
        if (me.y == arena.dims[1] - 1) {
            return if (me.direction == "N") "F" else "R"
        }
        if (me.x == arena.dims[0] - 1) {
            return if (me.direction == "W") "F" else "R"
        }

        val fields = when (me.direction) {
            "N" -> setOf(me.x to me.y - 1, me.x to me.y - 2, me.x to me.y - 3)
            "S" -> setOf(me.x to me.y + 1, me.x to me.y + 2, me.x to me.y + 3)
            "W" -> setOf(me.x - 1 to me.y, me.x - 2 to me.y, me.x - 3 to me.y)
            "E" -> setOf(me.x + 1 to me.y, me.x + 2 to me.y, me.x + 3 to me.y)
            else -> setOf()
        }

        val attackers = setOf(
            me.x to me.y + 1,
            me.x to me.y + 2,
            me.x to me.y + 3,
            me.x to me.y - 1,
            me.x to me.y - 2,
            me.x to me.y - 3,
            me.x + 1 to me.y,
            me.x + 2 to me.y,
            me.x + 3 to me.y,
            me.x - 1 to me.y,
            me.x - 2 to me.y,
            me.x - 3 to me.y
        )

        val allAttackers: Set<PlayerState> = arena.state.values.filter { it.x to it.y in attackers }
            .filter {
                it.x > me.x && it.direction == "W" ||
                it.x < me.x && it.direction == "E" ||
                it.y > me.y && it.direction == "N" ||
                it.y < me.y && it.direction == "S"
            }
            .toSet()

        val victims = arena.state.values.filter { it.x to it.y in attackers } - allAttackers

        val attackersPower = allAttackers.size

        LOGGER.info("Im on ${me.x} ${me.y}, attacked by $attackersPower")

        if(attackersPower == 1 && allAttackers.first().direction.isOppositeTo(me.direction)) {
            return if(allAttackers.first().score < me.score) "T" else "L"
        }

        if (attackersPower > 0) {
            return if(isInFront) "L" else "F"
        }

        if (arena.state.values.find { it.x to it.y in fields } != null)
            return "T"

        if(victims.isNotEmpty()) {
            when(me.direction) {
                "N" -> if(victims.any { it.x > me.x }) return "R" else if(victims.any { it.x < me.x }) return "L"
                "S" -> if(victims.any { it.x > me.x }) return "L" else if(victims.any { it.x < me.x }) return "R"
                "W" -> if(victims.any { it.y > me.y }) return "R" else if(victims.any { it.y < me.y }) return "L"
                "E" -> if(victims.any { it.y > me.y }) return "L" else if(victims.any { it.y < me.y }) return "R"
            }
        }

        return sequence[current++ % sequence.size]
    }

}

private fun String.isOppositeTo(direction: String) = when(this) {
    "W" -> "E"
    "E" -> "W"
    "N" -> "S"
    "S" -> "N"
    else -> "X"
} == direction