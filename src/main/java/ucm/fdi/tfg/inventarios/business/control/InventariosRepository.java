package ucm.fdi.tfg.inventarios.business.control;

import org.springframework.data.jpa.repository.JpaRepository;

import ucm.fdi.tfg.inventarios.business.entity.Inventario;

public interface InventariosRepository extends JpaRepository<Inventario, Long>{

}