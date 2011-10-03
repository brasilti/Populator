package br.com.wave.populator.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import br.com.brasilti.utils.reflection.ReflectionUtil;
import br.com.wave.populator.enums.ErrorEnum;
import br.com.wave.populator.exceptions.PopulatorException;

public class Validator {

	public <T> void validate(T instance) throws PopulatorException {
		if (instance == null) {
			throw new PopulatorException(ErrorEnum.NULL);
		}

		Class<?> klass = instance.getClass();
		if (!ReflectionUtil.implementz(klass, Serializable.class)) {
			throw new PopulatorException(ErrorEnum.NOT_SERIALIZABLE, klass.getName());
		}

		List<Field> fields = ReflectionUtil.getPersistentFields(klass);
		if (fields.isEmpty()) {
			throw new PopulatorException(ErrorEnum.NOT_PERSISTENT_FIELDS, klass.getName());
		}
	}

}
