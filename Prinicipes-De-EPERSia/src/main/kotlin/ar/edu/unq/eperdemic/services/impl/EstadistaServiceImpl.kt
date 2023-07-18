package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.UbicacionJPADAO

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.services.EstadistaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.NoResultException


@Service
@Transactional
class EstadistaServiceImpl : EstadistaService {

    @Autowired private lateinit var especieDAO : EspecieDAO
    @Autowired private lateinit var ubicacionDAO : UbicacionJPADAO

    override fun especieLider(): Especie? {
        return especieDAO.especieLider() ?: throw NoResultException("No se encontró ninguna especie líder")
    }

    override fun lideres(): List<Especie> {
        return especieDAO.especiesLideres()
    }

    override fun reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagios {

        lateinit var reporteDeContagios: ReporteDeContagios

        val sizeVectores = ubicacionDAO.cantidadDeVectoresEn(nombreDeLaUbicacion)
        val sizeInfectados = ubicacionDAO.cantidadDeInfectados(nombreDeLaUbicacion)
        val masMortal = especieDAO.especieLiderEn(nombreDeLaUbicacion)?.nombre ?: "No hay vectores"
        reporteDeContagios = ReporteDeContagios(sizeVectores,sizeInfectados,masMortal)

        return reporteDeContagios
    }
    override fun clear() {
        especieDAO.deleteAll()
        ubicacionDAO.deleteAll()
    }
}