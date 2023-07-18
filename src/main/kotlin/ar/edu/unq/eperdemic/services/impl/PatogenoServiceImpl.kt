package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.VectorDAO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.services.PatogenoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PatogenoServiceImpl : PatogenoService, EntentyUtils() {
    @Autowired private lateinit var patogenoDAO: PatogenoDAO
    @Autowired private lateinit var especieDAO: EspecieDAO
    @Autowired private lateinit var ubicacionDAO: UbicacionJPADAO
    @Autowired private lateinit var vectorDAO: VectorDAO

    override fun crearPatogeno(patogeno: Patogeno): Patogeno {
        return patogenoDAO.save(patogeno)
    }

    override fun recuperarPatogeno(id: Long): Patogeno {
        return this.findByIdOrThrow(patogenoDAO,id)
    }

    override fun recuperarATodosLosPatogenos(): List<Patogeno> {
        return patogenoDAO.findAll().toMutableList()
    }

    override fun agregarEspecie(patogenoId: Long, nombre: String, ubicacionId: Long): Especie {
        val patogeno = this.findByIdOrThrow(patogenoDAO,patogenoId)
        val ubicacion = this.findByIdOrThrow(ubicacionDAO,ubicacionId)
        val especie = Especie(patogeno, nombre, ubicacion.nombre)
        val vector = ubicacion.vectorAlAzar()
        especieDAO.save(especie)
        especie.infectarSiCorresponde(vector)
        vectorDAO.save(vector)
        return especie
    }

    override fun especiesDePatogeno(patogenoId: Long): List<Especie> {
        val patogeno : Patogeno = this.findByIdOrThrow(patogenoDAO,patogenoId)
        return patogeno.especies.toList()
    }

    override fun cantidadDeInfectados(especieId: Long): Int {
        return especieDAO.cantidadDeInfectados(especieId)
    }

    override fun esPandemia(especieId: Long): Boolean {
        val cantidadDeEspecia = ubicacionDAO.cantidadDeUbicacionesConEspecie(especieId)
        val cantidadDeUbicaciones = ubicacionDAO.cantidadDeUbicaciones()
        return cantidadDeEspecia > cantidadDeUbicaciones / 2
    }

    override fun clear() {
        ubicacionDAO.deleteAll()
        patogenoDAO.deleteAll()
        vectorDAO.deleteAll()
        especieDAO.deleteAll()
    }
}