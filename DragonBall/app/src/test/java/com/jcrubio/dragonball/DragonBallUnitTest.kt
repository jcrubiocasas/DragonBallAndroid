package com.jcrubio.dragonball

import com.jcrubio.dragonball.Home.Model.Heroe
import com.jcrubio.dragonball.Home.Model.HeroeDTO
import com.jcrubio.dragonball.Home.ViewModel.HomeViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule
import java.net.URL

class DragonBallUnitTest {

    @get:Rule
    var mainCoroutineRule = com.jcrubio.dragonball.Rule.CoreCoroutineRule()

    private val viewModel = HomeViewModel()

    @Test
    fun `simple arithmetic validation`() {
        assertEquals(4, 2 * 2)
        assertNotEquals(5, 2 + 2)
    }

    @Test
    fun `hero attributes validation`() {
        val hero = Heroe(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "1234",
            favorite = false,
            description = "Saiyan Warrior",
            name = "Goku"
        )

        assertEquals("Goku", hero.name)
        assertNotEquals("Vegeta", hero.name)
        assertTrue(URL(hero.photo) is URL)
        assertEquals("1234", hero.id)
        assertFalse(hero.favorite)
        assertEquals("Saiyan Warrior", hero.description)
        assertFalse(hero.isDead)
        assertEquals(hero.totalHitPoints, hero.currentHitPoints)
        assertEquals(0, hero.timesSlected)
    }

    @Test
    fun `heroDTO attributes validation`() {
        val heroDTO = HeroeDTO(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "5678",
            favorite = true,
            description = "Legendary Saiyan",
            name = "Broly"
        )

        assertEquals("Broly", heroDTO.name)
        assertNotEquals("Frieza", heroDTO.name)
        assertTrue(URL(heroDTO.photo) is URL)
        assertEquals("5678", heroDTO.id)
        assertTrue(heroDTO.favorite)
        assertEquals("Legendary Saiyan", heroDTO.description)
    }

    @Test
    fun `hero attack mechanics`() = runTest {
        viewModel.selectedHeroe = Heroe(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "7890",
            favorite = false,
            description = "Protector of Earth",
            name = "Gohan"
        )

        launch {
            viewModel.uiState.collect {
                when (it) {
                    is HomeViewModel.UiStateCA.OnHeroIsDead -> {
                        assertTrue(viewModel.selectedHeroe.currentHitPoints <= 0)
                        cancel()
                    }
                    is HomeViewModel.UiStateCA.OnHeroeHPChange -> {
                        assertTrue(viewModel.selectedHeroe.currentHitPoints > 0)
                        val receivedDamage = viewModel.selectedHeroe.totalHitPoints - viewModel.selectedHeroe.currentHitPoints
                        assertTrue(receivedDamage in 10..60)
                        cancel()
                    }
                    else -> cancel()
                }
            }
        }
        viewModel.fightOnClickMethod("attack")
    }

    @Test
    fun `hero selection mechanics`() = runTest {
        val hero = Heroe(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "1122",
            favorite = true,
            description = "Elite Saiyan",
            name = "Vegeta"
        )

        launch {
            viewModel.uiState.collect {
                when (it) {
                    is HomeViewModel.UiStateCA.OnHeroeSelectedToFight -> {
                        assertTrue(it.heroe.timesSlected > 0)
                        cancel()
                    }
                    else -> cancel()
                }
            }
        }
        viewModel.selectedHeroToFightClicked(hero)
    }

    @Test
    fun `hero healing mechanics`() = runTest {
        viewModel.selectedHeroe = Heroe(
            photo = "https://cdn.alfabetajuega.com/alfabetajuega/2020/12/goku1.jpg?width=300",
            id = "3344",
            favorite = false,
            description = "Saiyan Elite",
            name = "Trunks"
        )

        viewModel.selectedHeroe.currentHitPoints -= 30
        assertTrue(viewModel.selectedHeroe.currentHitPoints < viewModel.selectedHeroe.totalHitPoints)

        launch {
            viewModel.uiState.collect {
                when (it) {
                    is HomeViewModel.UiStateCA.OnHeroeHPChange -> {
                        assertTrue(viewModel.selectedHeroe.currentHitPoints >= 30)
                        cancel()
                    }
                    else -> cancel()
                }
            }
        }
        viewModel.fightOnClickMethod("heal")
    }
}