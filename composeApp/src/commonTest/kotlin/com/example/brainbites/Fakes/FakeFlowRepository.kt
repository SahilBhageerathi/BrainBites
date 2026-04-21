import com.example.brainbites.domain.models.FlowPuzzle
import com.example.brainbites.domain.models.Hint
import com.example.brainbites.domain.models.Wall
import com.example.brainbites.domain.repo.FlowRepository

class FakeFlowRepository : FlowRepository {
    val puzzles = listOf(
        FlowPuzzle(
            id = 1,
            gridSize = 3,
            hints = listOf(
                Hint(0, 0, 1),
                Hint(2, 2, 2)
            ),
            walls = listOf(
                Wall(0, 1, "bottom")
            )
        )
    )

    override suspend fun getAllPuzzles(): List<FlowPuzzle> {
        return puzzles
    }

    override fun getPuzzleCount(): Int = 1
}