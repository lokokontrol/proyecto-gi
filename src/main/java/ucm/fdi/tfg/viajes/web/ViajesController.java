package ucm.fdi.tfg.viajes.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ucm.fdi.tfg.pagos.business.entity.Pago;
import ucm.fdi.tfg.proyecto.business.boundary.ProyectosManager;
import ucm.fdi.tfg.proyecto.business.entity.Proyecto;
import ucm.fdi.tfg.users.business.boundary.UserManager;
import ucm.fdi.tfg.users.business.entity.Investigador;
import ucm.fdi.tfg.users.business.entity.User;
import ucm.fdi.tfg.viajes.business.boundary.ViajesManager;
import ucm.fdi.tfg.viajes.business.entity.Dieta;
import ucm.fdi.tfg.viajes.business.entity.GastoViaje;
import ucm.fdi.tfg.viajes.business.entity.Viaje;

@Controller
public class ViajesController {


	private UserManager users ;
	private ViajesManager viajes;
	private ProyectosManager proyectosManager;
	
	@Autowired
	public ViajesController (UserManager users, ViajesManager viajes,ProyectosManager proyectosManager){
		this.users=users;
		this.viajes=viajes;
		this.proyectosManager =  proyectosManager;
	}
	
	@RequestMapping(value = "/proyectos/{idProyecto}/viajes", method = RequestMethod.GET)
	public ModelAndView listarViajes(@PathVariable(value="idProyecto") Long idProyecto) {
				
		Map<String, Object> model = new HashMap<String, Object>();
		
		List<Viaje> viajesPorProyecto = viajes.viajesPorProyecto(idProyecto);
		
		model.put("viajesPorProyecto", viajesPorProyecto);
		model.put("idProyecto", idProyecto);
		
		
		model.put("usuario", SecurityContextHolder.getContext().getAuthentication().getName());

		ModelAndView view = new ModelAndView("viajes/listarViajes", model);
		
		return view;
	}
	
	@RequestMapping(value = "/proyectos/{idProyecto}/altaViaje", method = RequestMethod.GET)
	public ModelAndView viajeform(@PathVariable(value="idProyecto") Long idProyecto) {
	Map<String, Object> model = new HashMap<String, Object>();
		
		//Cogemos el proyecto  que vamos a pintar en el Pago
		Proyecto proyecto = proyectosManager.findProyecto(idProyecto);
		
		Investigador inv = proyecto.getInvestigadorPrincipal();
		
		User userInvestigadorPrincipal = users.findOneUser(inv.getId());
		
		Viaje viaje = new Viaje(proyecto);
		
		GastoViaje g = new GastoViaje();
		viaje.getGastos().add(g);		
		
		
		List<Dieta> dietas = viajes.dameDietas();
		
		
		//Usuarios que participan en un proyecto
		List<User> investigadoresAsignadosAproyecto = proyectosManager.investigadoresProyecto(idProyecto);
		
		model.put("modoTitulo", "Alta");
		model.put("modo", "altaViaje");	
		
		model.put("viaje", viaje);
		model.put("user", userInvestigadorPrincipal);
		
		model.put("dietas", dietas);
		model.put("importePrecioKm", viajes.getCostePorKm());
		
		model.put("investigadoresAsignadosAproyecto", investigadoresAsignadosAproyecto);
		
		model.put("usuario", SecurityContextHolder.getContext().getAuthentication().getName());

		ModelAndView view = new ModelAndView("viajes/viajeForm", model);
		
		return view;
	}
	
	@RequestMapping(value = "/proyectos/{idProyecto}/altaViaje", method = RequestMethod.POST)
	public String addViaje(@PathVariable(value="idProyecto") Long idProyecto ,@ModelAttribute Viaje viaje, BindingResult errors){
	
		viaje.setProyecto(proyectosManager.findProyecto(idProyecto));
		viajes.save(viaje);
		return "redirect:/inicio";
		
	}
	
	@RequestMapping(value = "/proyectos/{idProyecto}/edit/viajes/{idViaje}/", method = RequestMethod.GET)
	public ModelAndView editarViaje(@PathVariable(value="idProyecto") Long idProyecto, @PathVariable(value="idViaje") Long idViaje) {

		ModelAndView view = new ModelAndView("viajes/viajeForm");
		
		
		Proyecto proyecto = proyectosManager.findProyecto(idProyecto);
		Investigador inv = proyecto.getInvestigadorPrincipal();
		User userActivo = users.findOneUser(inv.getId());
		
		view.addObject("user", userActivo); 
		
		Viaje viaje = this.viajes.findOneViaje(idViaje);	
		
		
		List<Dieta> dietas = viajes.dameDietas();
		List<User> investigadoresAsignadosAproyecto = proyectosManager.investigadoresProyecto(idProyecto);
		
		view.addObject("investigadoresAsignadosAproyecto", investigadoresAsignadosAproyecto);
		
		view.addObject(viaje);
		
		view.addObject("dietas", dietas);
		view.addObject("importePrecioKm", viajes.getCostePorKm());
		
		view.addObject("modoTitulo", "Editar");
		view.addObject("modo", "edit/viajes");	
		view.addObject("idViaje", idViaje);	
		view.addObject("usuario", SecurityContextHolder.getContext().getAuthentication().getName());
		
		return view;
	}
				
	@RequestMapping(value = "/proyectos/{idProyecto}/edit/viajes/{idViaje}", method = RequestMethod.POST)
	public ModelAndView editarViajePost(@PathVariable(value="idProyecto") Long idProyecto ,@ModelAttribute @Valid Viaje viaje, BindingResult errors, @PathVariable(value="idViaje") Long idViaje){
				ModelAndView view = null;	
				
				Proyecto proyecto = proyectosManager.findProyecto(idProyecto);
				
				viaje.setProyecto(proyecto);
				
				if(errors.hasErrors()){
					
					view = new ModelAndView("viajes/viajeForm");
					
			
					Investigador inv = proyecto.getInvestigadorPrincipal();
						
					List<Dieta> dietas = viajes.dameDietas();
					List<User> investigadoresAsignadosAproyecto = proyectosManager.investigadoresProyecto(idProyecto);
					
					view.addObject("investigadoresAsignadosAproyecto", investigadoresAsignadosAproyecto);
					
					view.addObject(viaje);
					
					view.addObject("dietas", dietas);
					view.addObject("importePrecioKm", viajes.getCostePorKm());
					
					view.addObject("modoTitulo", "Editar");
					view.addObject("modo", "edit/viajes");	
					view.addObject("idViaje", idViaje);	
					view.addObject("usuario", SecurityContextHolder.getContext().getAuthentication().getName());
					
					User userActivo = users.findOneUser(inv.getId());
					view.addObject("user", userActivo);
					view.addObject("viaje", viaje);
					
				}else{
					viajes.editar(viaje,idViaje);	
					view = new ModelAndView("redirect:/proyectos/"+idProyecto+"/");
				}
				return view;
	}
		
	
	
	
	
}
