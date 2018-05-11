package bet.web.mgmt;

import bet.api.dto.ManagementDto;
import bet.service.mgmt.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.io.Serializable;
import java.util.Collection;

/**
 * Superclass of all the Management API controllers of the module
 * 
 * @author n.kotzalas
 *
 * @param <V>
 *            Type of the DTO to which the controller corresponds
 * @param <T>
 *            Type of the id of the entity to which the controller corresponds
 * @param <E>
 *            Type of the entity to which the controller corresponds
 */
public abstract class AbstractOodController<V extends ManagementDto<E, T>, T extends Serializable, E extends Serializable> {

	@Autowired
	protected ManagementService<E, T, V> service;


	@RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public V create(@RequestBody V model) {
		return service.create(model);
	}

	@RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public V update(@RequestBody V model) {
		return service.update(model);
	}

	@RequestMapping(path = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<V> list() {
		return service.list();
	}

}
