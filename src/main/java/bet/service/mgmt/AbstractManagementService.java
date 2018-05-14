package bet.service.mgmt;

import bet.api.dto.ManagementDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public abstract class AbstractManagementService<T extends Serializable, V extends Serializable, E extends ManagementDto<T, V>>
		implements ManagementService<T, V, E> {

	/**
	 * Repository of the dto to which the service corresponds
	 */
	@Autowired
	protected CrudRepository<T, V> repository;

	@Override
	public E create(E dto) {

		T entity = repository.save(dto.toEntity());
		dto.fromEntity(entity);

		return dto;
	}

	@Override
	public E update(E dto) {

		T entity = repository.save(dto.toEntity());
		dto.fromEntity(entity);

		return dto;
	}

}
