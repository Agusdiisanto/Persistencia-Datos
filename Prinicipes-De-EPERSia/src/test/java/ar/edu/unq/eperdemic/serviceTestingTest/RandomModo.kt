package ar.edu.unq.eperdemic.serviceTestingTest

import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoDefaultContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RandomModo {
    private val service: RandomContagio = RandomContagio

    @Test
    fun test_serviceTestingModoTesting() {
        val testing = ModoTestContagio(100, 100, 100, 10, 0)
        service.setModo(testing)
        Assertions.assertEquals(100, service.randomContagioAnimal())
        Assertions.assertEquals(100, service.randomContagioPersona())
        Assertions.assertEquals(100, service.randomContagioInsecto())
        Assertions.assertEquals(10, service.random())
        Assertions.assertEquals(0, service.randomPosibilidad())
    }

    @Test
    fun test_serviceTestingModoDefault() {
        service.setModo(ModoDefaultContagio())
        Assertions.assertTrue((0..100).contains(service.randomContagioAnimal()))
        Assertions.assertTrue((0..100).contains(service.randomContagioPersona()))
        Assertions.assertTrue((0..100).contains(service.randomContagioInsecto()))
        Assertions.assertTrue((0..10).contains(service.random()))
        Assertions.assertTrue((0..100).contains(service.randomPosibilidad()))
    }
}

