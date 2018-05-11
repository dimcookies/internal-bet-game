package bet.service.mgmt;

import bet.api.dto.ManagementDto;

import java.io.Serializable;
import java.util.List;

/**
 * Interface implemented by all management services of the module
 * 
 * @author n.kotzalas
 *
 * @param <V>
 *            Type of the JPA entity managed by this service
 * @param <E>
 *            Type of the id of the JPA entity managed by this service
 * @param <T>
 *            Type of the DTO that represents the JPA entity managed by this
 *            service
 */
public interface ManagementService<V extends Serializable, E extends Serializable, T extends ManagementDto<V, E>> {

	/**
	 * Creates a new entity using the provided DTO as input
	 * 
	 * @param dto
	 *            The DTO that represents the new entity
	 * @return DTO that represents the created entity
	 */
	T create(T dto);

	/**
	 * Updates an already existing DTO with the provided input (the id of the
	 * DTO must exist in the database)
	 * 
	 * @param dto
	 *            The DTO that must updated
	 * @return The updated version of the DTO
	 */
	T update(T dto);

	/**
	 * Lists all entities managed by this service
	 * 
	 * @return All the entities managed by this service
	 */
	List<T> list();

	

}
