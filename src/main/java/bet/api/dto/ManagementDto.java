package bet.api.dto;

import java.io.Serializable;

public interface ManagementDto<T extends Serializable, V extends Serializable> extends Serializable {

	/**
	 * Creates a new entity from this object
	 * 
	 * @return The created entity
	 */
	T toEntity();

	/**
	 * Copies the values of the provided entity to this object
	 * 
	 * @param entity
	 *            The entity from which the values must be copied
	 */
	void fromEntity(T entity);

}
