package ucm.fdi.tfg.gestores.business.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Gestor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	public Gestor(){
		
	}
}
