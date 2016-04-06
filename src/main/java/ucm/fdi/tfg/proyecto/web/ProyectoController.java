package ucm.fdi.tfg.proyecto.web;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ucm.fdi.tfg.proyecto.business.boundary.NuevoProyectoDTO;
import ucm.fdi.tfg.proyecto.business.boundary.ProyectosManager;
import ucm.fdi.tfg.proyecto.business.entity.Proyecto;
import ucm.fdi.tfg.users.business.boundary.UserManager;


@Controller
public class ProyectoController {
	
	private ProyectosManager proyectos;
	private UserManager users;

	@Autowired
	public ProyectoController(ProyectosManager proyectos, UserManager users) {
		this.proyectos = proyectos;
		this.users = users;
	}


	 	
		@RequestMapping(value = "/proyectos", method = RequestMethod.GET)
		public ModelAndView listarProeyctos() {
			
			Map<String, Object> model = new HashMap<String, Object>();
			
			model.put("proyectos", proyectos.findAll());

			ModelAndView view = new ModelAndView("proyectos/listarProyectos", model);

			return view;
		}
		
		@RequestMapping(value = "/proyecto/{id}/", method = RequestMethod.GET)
		public ModelAndView menuProyecto(@PathVariable(value="id") Long id) {
			
			Map<String, Object> model = new HashMap<String, Object>();
			
			model.put("idProyecto",id);
			
			ModelAndView view = new ModelAndView("menus/menuProyecto", model);

			return view;
		}
	
	
	//cambiar a /proyectos/nuevo
	@RequestMapping(value = "/crearProyecto", method = RequestMethod.GET)
	public ModelAndView añadirProyecto() {

		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("investigadores", users.findAllUserInvestigadores());
		model.put("nuevoProyectoDTO",  new NuevoProyectoDTO());
		ModelAndView view = new ModelAndView("proyectos/proyectoForm", model);

		return view;
	}
	
	

	
	@RequestMapping(value = "/crearProyecto", method = RequestMethod.POST)
	public ModelAndView añadirProyectoPost(@ModelAttribute("nuevoProyectoDTO") @Valid NuevoProyectoDTO  nuevoProyectoDTO, BindingResult errors) {
		
		ModelAndView view = null;			
		
		if (errors.hasErrors()) {
			view = new ModelAndView("proyectos/proyectoForm");
			view.addObject("investigadores", users.findAllUserInvestigadores());
			view.addObject("nuevoProyectoDTO", nuevoProyectoDTO);						
		} else {
			proyectos.nuevoProyecto(nuevoProyectoDTO);
			view = new ModelAndView("redirect:/proyectos");
		}
		
		return view;		
	}
	
	
	@RequestMapping(value = "/editar", method = RequestMethod.GET)
	public ModelAndView EditarlistarProeyctos() {
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("proyectos", proyectos.findAll());

		ModelAndView view = new ModelAndView("proyectos/EditarlistarProyectos", model);

		return view;
	}
	
	@RequestMapping(value = "edit/proyecto/{id}/", method = RequestMethod.GET)
	public ModelAndView EditProyecto(@PathVariable(value="id") Long id) {
		
		ModelAndView view = null;
		view = new ModelAndView("proyectos/proyectoFormEditar");
		NuevoProyectoDTO proyectDTO = new NuevoProyectoDTO();
		Proyecto proyecto = proyectos.findProyecto(id);
		
		proyectDTO.setInvestigadorId(proyecto.getInvestigadorPrincipal().getId());
		proyectDTO.setNumContabilidad((proyecto.getNumContabilidad()));
		proyectDTO.setReferencia((proyecto.getReferencia()));
		proyectDTO.setTitulo((proyecto.getTitulo()));
		//XXX proyectDTO.setInvestigadoresID(pro);
		
		//Le metemos el id, en la vista no parecera, ya que ira en in hide
		proyectDTO.setIdProyecto(id);
		
		
		view.addObject("investigadores", users.findAllUserInvestigadores());
		view.addObject("nuevoProyectoDTO" ,proyectDTO);

		return view;
	}
	
	@RequestMapping(value = "/edit/proyecto/{id}/", method = RequestMethod.POST)
	public ModelAndView editarProyectoPost(@ModelAttribute("nuevoProyectoDTO") @Valid NuevoProyectoDTO  nuevoProyectoDTO, BindingResult errors) {
		
		ModelAndView view = null;			
		
		if (errors.hasErrors()) {
			view = new ModelAndView("proyectos/proyectoForm");
			view.addObject("investigadores", users.findAllUserInvestigadores());
			view.addObject("nuevoProyectoDTO", nuevoProyectoDTO);						
		} else {
			proyectos.editar(nuevoProyectoDTO);
			view = new ModelAndView("redirect:/proyectos");
		}
		
		return view;		
	}

}
