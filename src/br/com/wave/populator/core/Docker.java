package br.com.wave.populator.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.brasilti.repository.annotations.Transactional;
import br.com.brasilti.repository.core.Keeper;
import br.com.brasilti.repository.enums.RemoveEnum;
import br.com.brasilti.repository.exceptions.RepositoryException;
import br.com.brasilti.utils.reflection.ReflectionUtil;
import br.com.wave.populator.enums.FieldEnum;
import br.com.wave.populator.exceptions.PopulatorException;

public class Docker {

	private PatternManager manager;

	@Inject
	private Keeper keeper;

	private List<Object> instances;

	public Docker() {
		this.manager = PatternManager.getInstance();
		this.instances = new ArrayList<Object>();
	}

	@Transactional
	public void persistInstances() throws PopulatorException {
		for (Object instance : this.manager.getValues()) {
			try {
				Field field = ReflectionUtil.getField(FieldEnum.ID.getValue(), instance.getClass());
				Object value = ReflectionUtil.get(field, instance);
				if (value == null) {
					this.keeper.persist(instance);
				}
				this.instances.add(instance);
			} catch (RepositoryException e) {
				throw new PopulatorException(e.getMessage());
			}
		}
	}

	@Transactional
	public void removeInstances() throws PopulatorException {
		for (Object instance : this.instances) {
			try {
				this.keeper.remove(instance, RemoveEnum.PHYSICAL);
			} catch (RepositoryException e) {
				throw new PopulatorException(e.getMessage());
			}
		}

		this.instances = new ArrayList<Object>();
	}

}
